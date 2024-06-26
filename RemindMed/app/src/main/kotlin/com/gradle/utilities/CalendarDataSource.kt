package com.gradle.utilities

import com.gradle.models.CalendarModel
import com.gradle.models.DateModel
import java.util.Calendar
import java.util.Date

class CalendarDataSource {
    val today: Date
        get() {
            return Date()
        }

    fun getData(startDate: Date = today, lastSelectedDate: Date): CalendarModel {
        val calendar = Calendar.getInstance()
        calendar.time = startDate

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val firstDayInWeek = calendar.time

        calendar.add(Calendar.DAY_OF_YEAR, 7)
        val endDayInWeek = calendar.time

        val visibleDates = getDatesBetween(firstDayInWeek, endDayInWeek)
        return toCalendarModel(visibleDates, lastSelectedDate)
    }

    private fun getDatesBetween(startDate: Date, endDate: Date): List<Date> {
        val dateList = mutableListOf<Date>()
        val calendar = Calendar.getInstance()
        calendar.time = startDate

        while (calendar.time <= endDate) {
            dateList.add(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        return dateList
    }

    private fun toCalendarModel(
        dateList: List<Date>,
        lastSelectedDate: Date
    ): CalendarModel {
        return CalendarModel(
            selectedDate = toItemModel(lastSelectedDate, true),
            visibleDates = dateList.map {
                toItemModel(it, it == lastSelectedDate)
            }
        )
    }

    private fun toItemModel(date: Date, isSelectedDate: Boolean): DateModel {
        return DateModel(
            isSelected = isSelectedDate,
            isToday = isToday(date),
            date = date
        )
    }

    fun isToday(date: Date): Boolean {
        val todayDate = today
        return date.toFormattedDateString() == todayDate.toFormattedDateString()
    }
}
