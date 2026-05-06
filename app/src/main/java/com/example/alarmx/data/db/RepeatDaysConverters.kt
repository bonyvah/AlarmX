package com.example.alarmx.data.db

import java.time.DayOfWeek

/**
 * Bidirectional conversion between `Set<DayOfWeek>` and a 7-bit packed Int.
 */
object RepeatDaysConverters {

    fun toBitmask(days: Set<DayOfWeek>): Int {
        var mask = 0
        for (day in days) {
            mask = mask or (1 shl (day.value - 1))
        }
        return mask
    }

    fun fromBitmask(bitmask: Int): Set<DayOfWeek> {
        if (bitmask == 0) return emptySet()
        val days = LinkedHashSet<DayOfWeek>(7)
        for (i in 0 until 7) {
            if (bitmask and (1 shl i) != 0) {
                days += DayOfWeek.of(i + 1)
            }
        }
        return days
    }
}
