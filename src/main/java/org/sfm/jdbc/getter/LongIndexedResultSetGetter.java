package org.sfm.jdbc.getter;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.LongGetter;

public class LongIndexedResultSetGetter implements LongGetter<ResultSet>,
		Getter<ResultSet, Long> {

	private final int column;

	public LongIndexedResultSetGetter(int column) {
		this.column = column;
	}

	@Override
	public long getLong(ResultSet target) throws SQLException {
		return target.getLong(column);
	}

	@Override
	public Long get(ResultSet target) throws Exception {
		long l = getLong(target);
		if (target.wasNull()) {
			return null;
		} else {
			return Long.valueOf(l);
		}
	}
}