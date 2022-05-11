package com.motorhome.database;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

public class DataResult {
    // A map that retains its insertion order
    private final LinkedHashMap<String, List<Object>> data;

    public DataResult(ResultSet rs) {
        data = new LinkedHashMap<>();

        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            // get column names
            for (int i = 1; i <= rsmd.getColumnCount(); i++ ) {
                data.put(rsmd.getColumnName(i), new ArrayList<>());
            }
            // get data
            while (rs.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    data.get(rsmd.getColumnName(i)).add(rs.getObject(i));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public int getNumberOfColumns() {
        return data.size();
    }

    public int getNumberOfRows() {
        return data.get(getColumnName(0)).size();
    }

    public String getColumnName(int index) {
        int count = 0;
        Set<String> keySet = data.keySet();
        if (index > keySet.size()) {
            for (String column: keySet) {
                if (count == index) {
                    return column;
                } count++;
            }
        }
        return null;
    }

    public int getColumnIndex(String name) {
        int count = 0;
        Set<String> keySet = data.keySet();
        if (data.containsKey(name)) {
            for (String column: keySet) {
                if (name.equals(column)) {
                    return count;
                } count++;
            }
        }
        return -1;
    }

    public Object getData(int column, int row) {
        return data.get(getColumnName(column)).get(row);
    }

    public Object getData(String columnName, int row) {
        return data.get(columnName).get(row);
    }

    public List<Object> getColumn(int column) {
        return data.get(getColumnName(column));
    }

    public List<Object> getColumn(String columnName) {
        return data.get(columnName);
    }

    public Map<String, List<Object>> getMap() {
        return data;
    }
}
