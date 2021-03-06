package org.sfm.jdbc.impl.convert.time;

import org.sfm.utils.conv.Converter;

import java.sql.Time;
import java.time.LocalDate;
import java.time.OffsetTime;

public class JavaOffsetTimeToTimeConverter implements Converter<OffsetTime, Time> {
    @Override
    public Time convert(OffsetTime in) throws Exception {
        if (in == null) return null;
        return new Time(in.atDate(LocalDate.now()).toInstant().toEpochMilli());
    }
}
