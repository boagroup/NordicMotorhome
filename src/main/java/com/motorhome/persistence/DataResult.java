package com.motorhome.persistence;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * Data Result Class
 * responsible for storing contents of ResultSet in clear manner
 * Author(s): Bartosz Birylo
 */
public class DataResult {
    // A map that retains its insertion order
    private final LinkedHashMap<String, List<Object>> data;
    private int iterCount = 0;

    /**
     * Constructor
     * @param rs ResultSet object, source of data for this object
     */
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

    /**
     * check if DataResult object is empty
     * @return {@code true} if empty, else {@code false}
     */
    public boolean isEmpty() {
        return data.isEmpty();
    }

    /**
     * get Number of columns in the DataResult
     * @return {@code int} value of number of columns
     */
    public int getNumberOfColumns() {
        return data.size();
    }

    /**
     * get Number of rows in a column the DataResult
     * @return {@code int} value of number of rows in a column
     */
    public int getNumberOfRows() {
        return data.get(getColumnName(0)).size();
    }

    /**
     * get name of the column at that index
     * @param index {@code int} - index of column
     * @return {@code String} - name of column
     */
    public String getColumnName(int index) {
        Set<String> keySet = data.keySet();
        if (index < keySet.size()) {
            int count = 0;
            for (String column: keySet) {
                if (count == index) {
                    return column;
                } count++;
            }
        }
        return null;
    }

    /**
     * get index of the column with that name
     * @param name {@code String} name of the column
     * @return {@code int} column index if found, else {@code -1}
     */
    public int getColumnIndex(String name) {
        Set<String> keySet = data.keySet();
        if (data.containsKey(name)) {
            int count = 0;
            for (String column: keySet) {
                if (name.equals(column)) {
                    return count;
                } count++;
            }
        }
        return -1;
    }

    /**
     * get data at given index
     * @param column {@code int} - index of column
     * @param row {@code int} - index of row
     * @return {@code Object} - data at this index
     */
    public Object getData(int column, int row) {
        List<Object> columnL = data.get(getColumnName(column));
        return columnL != null? columnL.get(row): null;
    }

    /**
     * get data at given name of column and index
     * @param columnName {@code String} - name of column
     * @param row {@code int} - index of row
     * @return {@code Object} - data at this name and index
     */
    public Object getData(String columnName, int row) {
        List<Object> column = data.get(columnName);
        return column != null? column.get(row): null;
    }

    /**
     * get reference to column at given index
     * @param column {@code int} - index of column
     * @return {@code List<Object>} - column at this index
     */
    public List<Object> getColumn(int column) {
        return data.get(getColumnName(column));
    }

    /**
     * get reference to column with given name
     * @param columnName {@code String} - name of column
     * @return {@code List<Object>} - column with this name
     */
    public List<Object> getColumn(String columnName) {
        return data.get(columnName);
    }

    /**
     *  get reference to the Map of columns and rows
      * @return {@code Map<String, List<Object>>} - Data inside DataResult
     */
    public Map<String, List<Object>> getMap() {
        return data;
    }

    public Map<String, Object> getRow(int index) {
        Map<String, Object> row = new LinkedHashMap<>();
        if (index > data.size()) {return null;}
        for (var key: data.keySet()) {
            row.put(key, getData(key, index));
        }
        return row;
    }

    public boolean next() {
        return iterCount++ < data.size();
    }

    public Map<String, Object> getCurrentRow() {
        return getRow(iterCount);
    }

    public void resetIter() {
        iterCount = 0;
    }
}
