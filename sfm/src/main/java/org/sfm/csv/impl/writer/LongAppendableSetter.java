package org.sfm.csv.impl.writer;

import org.sfm.csv.CellWriter;
import org.sfm.reflect.primitive.LongSetter;

public class LongAppendableSetter implements LongSetter<Appendable> {

    private final CellWriter cellWriter;

    public LongAppendableSetter(CellWriter cellWriter) {
        this.cellWriter = cellWriter;
    }

    @Override
    public void setLong(Appendable target, long value) throws Exception {
        cellWriter.writeValue(Long.toString(value), target);
    }
}
