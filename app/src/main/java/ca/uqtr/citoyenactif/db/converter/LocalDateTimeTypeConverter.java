package ca.uqtr.citoyenactif.db.converter;

import androidx.room.TypeConverter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class LocalDateTimeTypeConverter {

    @TypeConverter
    public static LocalDateTime toLocalDateTime(Long value) {
        return value == null ? LocalDateTime.now() :
                LocalDateTime.ofEpochSecond(value,0, ZoneOffset.UTC);
    }

    @TypeConverter
    public static Long toLong(LocalDateTime value) {
        return value == null ? 0 : value.toEpochSecond(ZoneOffset.UTC);
    }
}
