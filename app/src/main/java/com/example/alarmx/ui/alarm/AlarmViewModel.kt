package com.example.alarmx.ui.alarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alarmx.domain.challenge.ChallengeProvider
import com.example.alarmx.domain.model.Alarm
import com.example.alarmx.domain.model.UserPreferences
import com.example.alarmx.domain.repository.AlarmRepository
import com.example.alarmx.domain.repository.PreferencesRepository
import com.example.alarmx.domain.ringing.RingingController
import com.example.alarmx.domain.usecase.CreateAlarmUseCase
import com.example.alarmx.domain.usecase.SnoozeUseCase
import com.example.alarmx.domain.usecase.SubmitAnswerResult
import com.example.alarmx.domain.usecase.SubmitAnswerUseCase
import com.example.alarmx.domain.usecase.TriggerAlarmUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.DayOfWeek
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val preferencesRepository: PreferencesRepository,
    private val challengeProvider: ChallengeProvider,
    private val createAlarmUseCase: CreateAlarmUseCase,
    private val triggerAlarmUseCase: TriggerAlarmUseCase,
    private val submitAnswerUseCase: SubmitAnswerUseCase,
    private val snoozeUseCase: SnoozeUseCase,
    private val ringingController: RingingController,
) : ViewModel() {

    private val now: () -> Long = { System.currentTimeMillis() }

    private val _state = MutableStateFlow(AlarmUiState())
    val state: StateFlow<AlarmUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<AlarmEvent>(extraBufferCapacity = 8)
    val events: SharedFlow<AlarmEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            alarmRepository.observeAlarms().collect { alarms ->
                _state.update { it.copy(alarms = alarms) }
            }
        }
        viewModelScope.launch {
            preferencesRepository.preferences.collect { prefs ->
                _state.update { it.copy(preferences = prefs) }
            }
        }
    }

    fun createAlarm(
        id: Long,
        triggerAtEpochMillis: Long,
        label: String = "",
        repeatDays: Set<DayOfWeek> = emptySet(),
    ) {
        launchSafely {
            createAlarmUseCase(
                id = id,
                triggerAtEpochMillis = triggerAtEpochMillis,
                label = label,
                repeatDays = repeatDays,
            )
        }
    }

    fun toggleAlarm(alarmId: Long, enabled: Boolean) {
        launchSafely { alarmRepository.setEnabled(alarmId, enabled) }
    }

    fun deleteAlarm(alarmId: Long) {
        launchSafely { alarmRepository.delete(alarmId) }
    }

    suspend fun loadAlarm(alarmId: Long): Alarm? = alarmRepository.findById(alarmId)

    fun saveAlarm(alarm: Alarm) {
        launchSafely { alarmRepository.save(alarm) }
    }

    fun updatePreferences(transform: (UserPreferences) -> UserPreferences) {
        launchSafely { preferencesRepository.update(transform) }
    }

    fun onAlarmTriggered(alarmId: Long) {
        launchSafely {
            val trigger = triggerAlarmUseCase(alarmId)
            if (trigger == null) {
                _events.emit(AlarmEvent.NavigateBackToList)
                return@launchSafely
            }
            _state.update {
                it.copy(
                    activeAlarmId = trigger.alarmId,
                    activeChallenge = trigger.challenge,
                    fullScreenVisible = true,
                    incorrectAttempts = 0,
                )
            }
            _events.emit(AlarmEvent.NavigateToDismiss(trigger.alarmId))
        }
    }

    fun submitAnswer(answer: Int) {
        val s = _state.value
        launchSafely {
            when (submitAnswerUseCase(s.activeAlarmId, s.activeChallenge, answer)) {
                SubmitAnswerResult.Correct -> {
                    ringingController.stop()
                    _state.update {
                        it.copy(
                            activeAlarmId = null,
                            activeChallenge = null,
                            fullScreenVisible = false,
                            incorrectAttempts = 0,
                        )
                    }
                    _events.emit(AlarmEvent.CorrectAnswerFeedback)
                    _events.emit(AlarmEvent.NavigateBackToList)
                }
                SubmitAnswerResult.Incorrect -> {
                    val difficulty = s.preferences.difficulty
                    _state.update {
                        it.copy(
                            activeChallenge = challengeProvider.generate(difficulty),
                            incorrectAttempts = it.incorrectAttempts + 1,
                        )
                    }
                    _events.emit(AlarmEvent.WrongAnswerFeedback)
                }
                SubmitAnswerResult.NoActiveChallenge -> {
                    _state.update {
                        it.copy(
                            activeAlarmId = null,
                            activeChallenge = null,
                            fullScreenVisible = false,
                            incorrectAttempts = 0,
                        )
                    }
                    _events.emit(AlarmEvent.NavigateBackToList)
                }
            }
        }
    }

    fun snoozeActiveAlarm() {
        val activeId = _state.value.activeAlarmId ?: return
        launchSafely {
            val snoozed = snoozeUseCase(activeId, now())
            if (snoozed == null) {
                _events.emit(AlarmEvent.ShowError("Snooze is disabled or alarm is gone"))
                return@launchSafely
            }
            ringingController.stop()
            _state.update {
                it.copy(
                    activeAlarmId = null,
                    activeChallenge = null,
                    fullScreenVisible = false,
                    incorrectAttempts = 0,
                )
            }
            _events.emit(AlarmEvent.Snoozed(snoozed.triggerAtEpochMillis))
            _events.emit(AlarmEvent.NavigateBackToList)
        }
    }

    fun dismissError() {
        _state.update { it.copy(error = null) }
    }

    private fun launchSafely(block: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(loading = true, error = null) }
                block()
            } catch (cancelled: kotlinx.coroutines.CancellationException) {
                throw cancelled
            } catch (t: Throwable) {
                val message = t.message ?: "Unknown error"
                _state.update { it.copy(error = message) }
                _events.emit(AlarmEvent.ShowError(message))
            } finally {
                _state.update { it.copy(loading = false) }
            }
        }
    }
}
