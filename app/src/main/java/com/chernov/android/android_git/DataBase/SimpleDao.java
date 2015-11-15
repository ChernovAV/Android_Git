package com.chernov.android.android_git.DataBase;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Android on 14.11.2015.
 */
public class SimpleDao extends BaseDaoImpl<Simple, Integer> {

    protected SimpleDao(ConnectionSource connectionSource,
                      Class<Simple> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<Simple> getAllSimple() throws SQLException{
        return this.queryForAll();
    }

    public List<Simple> getSimpleByName(String name) throws SQLException{
        QueryBuilder<Simple, Integer> queryBuilder = queryBuilder();
        queryBuilder.where().eq("username", name);
        PreparedQuery<Simple> preparedQuery = queryBuilder.prepare();
        List<Simple> goalList = query(preparedQuery);
        return goalList;
    }
}
