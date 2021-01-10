package io.idhub.jwt.serialisers;

import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;

import java.time.format.DateTimeFormatterBuilder;

public class InstantWithMilliSecondPrecisionSerializer extends InstantSerializer {

    public InstantWithMilliSecondPrecisionSerializer() {
        super(InstantSerializer.INSTANCE, false, new DateTimeFormatterBuilder().appendInstant(3).toFormatter());
    }

}
