package org.sfm.datastax;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.SettableByIndexData;
import org.sfm.map.Mapper;
import org.sfm.utils.ErrorHelper;


public class BoundStatementMapper<T> {
    private final Mapper<T, SettableByIndexData> mapper;

    public BoundStatementMapper(Mapper<T, SettableByIndexData> mapper) {
        this.mapper = mapper;
    }

    public BoundStatement mapTo(T object, BoundStatement boundStatement) {
        try {
            mapper.mapTo(object, boundStatement, null);
        } catch (Exception e) {
            ErrorHelper.rethrow(e);
        }
        return boundStatement;
    }
}
