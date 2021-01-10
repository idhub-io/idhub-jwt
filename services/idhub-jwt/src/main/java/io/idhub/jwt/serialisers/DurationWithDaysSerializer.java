package io.idhub.jwt.serialisers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;

public class DurationWithDaysSerializer extends JsonSerializer<Duration> {

    private static final int DAY_IN_MILLIS = 24 * 60 * 60 * 1000;

    @Override
    public void serialize(Duration value, JsonGenerator jgen, SerializerProvider serializers) throws IOException {
        jgen.writeString(formatDurationToIncludeDays(value));
    }

    public static String formatDurationToIncludeDays(Duration duration) {
        if (duration.toDays() > 0) {
            long days = duration.toDays();
            long timeMillis = new BigDecimal(duration.toMillis()).subtract(new BigDecimal(duration.toDays()).multiply(new BigDecimal(DAY_IN_MILLIS))).longValue();
            Duration time = Duration.ofMillis(timeMillis);
            String durationPart = time.toString().substring(time.toString().indexOf("P") + 1);
            return "P" + days + "D" + durationPart;
        } else {
            return duration.toString();
        }
    }

}
