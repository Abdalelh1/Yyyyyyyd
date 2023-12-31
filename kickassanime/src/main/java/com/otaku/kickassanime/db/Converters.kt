package com.otaku.kickassanime.db

import androidx.room.TypeConverter
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter

object Converters {
    private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    private val timeFormatter = DateTimeFormatter.ISO_TIME

    @TypeConverter
    @JvmStatic
    @Suppress("UNUSED")
    fun toOffsetDateTime(value: String?): LocalDateTime? {
        return value?.let {
            return dateTimeFormatter.parse(value, LocalDateTime::from)
        }
    }

    @TypeConverter
    @JvmStatic
    @Suppress("UNUSED")
    fun fromOffsetDateTime(date: LocalDateTime?): String? {
        return date?.format(dateTimeFormatter)
    }

    @TypeConverter
    @JvmStatic
    @Suppress("UNUSED")
    fun toOffsetTime(value: String?): LocalTime? {
        return value?.let {
            return timeFormatter.parse(value, LocalTime::from)
        }
    }

    @TypeConverter
    @JvmStatic
    @Suppress("UNUSED")
    fun fromOffsetTime(date: LocalTime?): String? {
        return date?.format(timeFormatter)
    }

}
