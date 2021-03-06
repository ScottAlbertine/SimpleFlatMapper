package org.sfm.csv.impl;

import org.sfm.csv.ParsingContext;
import org.sfm.csv.mapper.BreakDetector;
import org.sfm.csv.mapper.CellSetter;
import org.sfm.csv.mapper.CsvMapperCellConsumer;
import org.sfm.reflect.Setter;

import static org.sfm.utils.Asserts.requireNonNull;

public class DelegateCellSetter<T, P> implements CellSetter<T> {

	private final DelegateMarkerSetter<T, P> marker;
	private final CsvMapperCellConsumer<P> handler;
    private final Setter<T, P> setter;
	private final int cellIndex;

	@SuppressWarnings("unchecked")
    public DelegateCellSetter(DelegateMarkerSetter<T, P> marker, int cellIndex, BreakDetector parentBreakDetector) {
		this.marker = requireNonNull("marker",  marker);
		this.handler = marker.getMapper().newCellConsumer(null, parentBreakDetector);
        this.setter = marker.getSetter();
		this.cellIndex = cellIndex;
	}

	public DelegateCellSetter(DelegateMarkerSetter<T, P> marker,
                              CsvMapperCellConsumer<P> handler,  int cellIndex) {
		this.marker = requireNonNull("marker", marker);
		this.handler = requireNonNull("handler",handler);
		this.cellIndex = cellIndex;
        this.setter = null;
	}

    @Override
	public void set(T target, char[] chars, int offset, int length, ParsingContext context)
			throws Exception {
		this.handler.newCell(chars, offset, length, cellIndex);
        final BreakDetector breakDetector = handler.getBreakDetector();
        if (setter != null && (breakDetector == null || (breakDetector.broken()&& breakDetector.isNotNull()))) {
            setter.set(target, this.handler.getCurrentInstance());
        }
	}
	
	public CsvMapperCellConsumer getCellConsumer() {
		return handler;
	}

    @Override
    public String toString() {
        return "DelegateCellSetter{" +
                "marker=" + marker +
                ", handler=" + handler +
                ", cellIndex=" + cellIndex +
                '}';
    }
}
