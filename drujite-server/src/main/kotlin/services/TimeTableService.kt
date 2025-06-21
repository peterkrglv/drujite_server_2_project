package services

import db.repos.EventRepository
import db.repos.TimeTableRepository
import models.EventModel
import models.TimeTableModel

class TimeTableService(
    private val timeTableRepository: TimeTableRepository,
    private val eventRepository: EventRepository
) {
    suspend fun getTimeTable(timeTableId: Int) = timeTableRepository.getBySessionAndDate(timeTableId)

    suspend fun addTimeTable(timeTable: TimeTableModel) = timeTableRepository.add(timeTable)

    suspend fun deleteTimeTable(timeTableId: Int) = timeTableRepository.delete(timeTableId)

    suspend fun getSessionsTimetables(sessionId: Int) = timeTableRepository.getSessionsTimetables(sessionId)

    suspend fun addEvent(event: EventModel): Int? {
        return eventRepository.add(event)
    }

    suspend fun deleteEvent(eventId: Int): Boolean {
        return eventRepository.delete(eventId)
    }

    suspend fun getEvent(eventId: Int): EventModel? {
        return eventRepository.get(eventId)
    }

    suspend fun getEventsBySessionAndDate(sessionId: Int, date: String): List<EventModel> {
        val timeTable = timeTableRepository.getBySessionAndDate(sessionId, date) ?: return emptyList()
        val eventIds = eventRepository.getTimetableEventIds(timeTable.id)
        return eventIds.mapNotNull { eventRepository.get(it) }
            .sortedBy { it.time }
    }

    suspend fun getEventsByTimetableId(timetableId: Int): List<EventModel> {
        val events = eventRepository.getTimetableEventIds(timetableId)
        return events.mapNotNull { eventRepository.get(it) }
            .sortedBy {
                val parts = it.time.split(":")
                val hours = parts[0].toInt()
                val minutes = parts[1].toInt()
                hours * 60 + minutes
            }
    }
}