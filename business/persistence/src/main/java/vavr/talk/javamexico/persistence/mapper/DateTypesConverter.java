package vavr.talk.javamexico.persistence.mapper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class DateTypesConverter {

    /**
     * Simple converter type from {@link LocalDateTime} to {@link OffsetDateTime}.
     * It will use {@link ZoneOffset#UTC} as default {@link java.time.ZoneId}
     *
     * @param localDateTime {@link LocalDateTime}
     * @return {@link OffsetDateTime}
     */
    public static OffsetDateTime fromLocalDateTime(final LocalDateTime localDateTime) {
        return localDateTime.atOffset(ZoneOffset.UTC);
    }

    /**
     * Simple converter type from {@link OffsetDateTime} to {@link LocalDateTime}.
     * It will use {@link ZoneOffset#UTC} as default {@link java.time.ZoneId}
     *
     * @param offsetDateTime {@link LocalDateTime}
     * @return {@link OffsetDateTime}
     */
    public static LocalDateTime fromOffsetDateTime(final OffsetDateTime offsetDateTime) {
        return offsetDateTime.toLocalDateTime();
    }


}
