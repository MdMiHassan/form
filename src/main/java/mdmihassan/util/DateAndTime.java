package mdmihassan.util;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;

public class DateAndTime {

    public static Instant toInstant(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toInstant();
    }

    public static Instant toInstant(Date date) {
        return date == null ? null : date.toInstant();
    }

    public static Date toDate(Instant instant) {
        return instant == null ? null : Date.from(instant);
    }

    public static Timestamp toTimestamp(Instant instant) {
        return instant == null ? null : Timestamp.from(instant);
    }

}
