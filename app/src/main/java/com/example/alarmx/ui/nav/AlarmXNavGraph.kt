package com.example.alarmx.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.alarmx.ui.alarm.AlarmEvent
import com.example.alarmx.ui.alarm.AlarmViewModel
import com.example.alarmx.ui.alarm.dismiss.DismissScreen
import com.example.alarmx.ui.alarm.editor.AlarmEditorScreen
import com.example.alarmx.ui.alarm.list.AlarmListScreen
import com.example.alarmx.ui.alarm.preferences.PreferencesScreen

object AlarmXRoutes {
    const val LIST = "list"
    const val EDITOR_CREATE = "editor"
    const val EDITOR_EDIT = "editor/{alarmId}"
    const val DISMISS = "dismiss/{alarmId}"
    const val PREFERENCES = "preferences"
    const val ALARM_ID_ARG = "alarmId"

    fun editor(alarmId: Long) = "editor/$alarmId"
    fun dismiss(alarmId: Long) = "dismiss/$alarmId"
}

@Composable
fun AlarmXNavGraph(
    modifier: Modifier = Modifier,
    initialAlarmId: Long? = null,
    navController: NavHostController = rememberNavController(),
) {
    val alarmViewModel: AlarmViewModel = hiltViewModel()

    LaunchedEffect(initialAlarmId) {
        if (initialAlarmId != null) {
            navController.navigate(AlarmXRoutes.dismiss(initialAlarmId))
        }
    }

    LaunchedEffect(navController) {
        alarmViewModel.events.collect { event ->
            when (event) {
                is AlarmEvent.NavigateToDismiss -> {
                    val alreadyOnDismiss =
                        navController.currentDestination?.route == AlarmXRoutes.DISMISS
                    if (!alreadyOnDismiss) {
                        navController.navigate(AlarmXRoutes.dismiss(event.alarmId))
                    }
                }
                AlarmEvent.NavigateBackToList -> {
                    navController.popBackStack(AlarmXRoutes.LIST, inclusive = false)
                }
                else -> Unit
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = AlarmXRoutes.LIST,
        modifier = modifier,
    ) {
        composable(AlarmXRoutes.LIST) {
            AlarmListScreen(
                viewModel = alarmViewModel,
                onCreate = { navController.navigate(AlarmXRoutes.EDITOR_CREATE) },
                onEdit = { id -> navController.navigate(AlarmXRoutes.editor(id)) },
                onOpenPreferences = { navController.navigate(AlarmXRoutes.PREFERENCES) },
            )
        }
        composable(AlarmXRoutes.PREFERENCES) {
            PreferencesScreen(
                viewModel = alarmViewModel,
                onClose = { navController.popBackStack() },
            )
        }
        composable(AlarmXRoutes.EDITOR_CREATE) {
            AlarmEditorScreen(
                viewModel = alarmViewModel,
                alarmId = null,
                onClose = { navController.popBackStack() },
            )
        }
        composable(
            route = AlarmXRoutes.EDITOR_EDIT,
            arguments = listOf(
                navArgument(AlarmXRoutes.ALARM_ID_ARG) { type = NavType.LongType },
            ),
        ) { entry ->
            val alarmId = entry.arguments?.getLong(AlarmXRoutes.ALARM_ID_ARG)
            AlarmEditorScreen(
                viewModel = alarmViewModel,
                alarmId = alarmId,
                onClose = { navController.popBackStack() },
            )
        }
        composable(
            route = AlarmXRoutes.DISMISS,
            arguments = listOf(
                navArgument(AlarmXRoutes.ALARM_ID_ARG) { type = NavType.LongType },
            ),
        ) { entry ->
            val alarmId = entry.arguments?.getLong(AlarmXRoutes.ALARM_ID_ARG) ?: 0L
            LaunchedEffect(alarmId) {
                alarmViewModel.onAlarmTriggered(alarmId)
            }
            DismissScreen(viewModel = alarmViewModel)
        }
    }
}
