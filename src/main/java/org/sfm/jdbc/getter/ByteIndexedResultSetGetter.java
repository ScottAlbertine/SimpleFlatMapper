package org.sfm.jdbc.getter;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.ByteGetter;

public class ByteIndexedResultSetGetter implements ByteGetter<ResultSet>, Getter<ResultSet, Byte> {

	private final int column;
	
	public ByteIndexedResultSetGetter(int column) {
		this.column = column;
	}

	@Override
	public byte getByte(ResultSet target) throws SQLException {
		return target.getByte(column);
	}

	@Override
	public Byte get(ResultSet target) throws Exception {
		byte b = getByte(target);
		if (target.wasNull()) {
			return null;
		} else {
			return Byte.valueOf(b);
		}
	}
}