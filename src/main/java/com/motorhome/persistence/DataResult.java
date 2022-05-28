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
    private int iterCount = -1;

    /**
     * Constructor
     * @param rs ResultSet object, source of data for this object
     */
    public DataResult(ResultSet rs) {
        ArrayList<String> names = new ArrayList<>();
        Set<String> excludedNames = new HashSet<>();
        data = new LinkedHashMap<>();

        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            // get column names
            for (int i = 1; i <= rsmd.getColumnCount(); i++ ) {
                String name = rsmd.getColumnLabel(i);
                // check if name is already used for example id
                boolean contains = names.contains(name);
                if (contains || excludedNames.contains(name)) {
                    // add the name to excluded
                    excludedNames.add(name);
                    // add table.name example: user.id
                    names.add(rsmd.getTableName(i) + "." + name);
                    if (contains) {
                        // get index of already used name
                        int index = names.indexOf(name);
                        // change that name to other_table.name
                        names.set(index, rsmd.getTableName(index+1) + "." + name);
                    }
                } else {
                    names.add(name);
                }
            }
            // populate map with empty rows
            for (String name: names) {
                data.put(name, new ArrayList<>());
            }
            // if empty finish
            if (!rs.isBeforeFirst() ) { return; }
            // get data
            while (rs.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    data.get(names.get(i-1)).add(rs.getObject(i));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /**
     * check if DataResult object is empty
     * @return {@code true} if empty, else {@code false}
     */
    public boolean isEmpty() {
        boolean empty = true;
        for (var column: data.values()) {
            empty = column.isEmpty(); // if not empty true
            if (!empty) { break; }
        }
        return data.isEmpty() || empty;
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
        return getColumn(0).size();
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

    // Iter functions

    public boolean next() { return ++iterCount < getNumberOfRows(); }

    public Map<String, Object> getCurrentRow() {
        return getRow(iterCount ==-1?0:iterCount);
    }

    public void resetIter() {
        iterCount = -1;
    }

    public Object get(String nameOfColumn) {
        return getCurrentRow().get(nameOfColumn);
    }

    // tClass acts as a marker basically, without it if you provided null orElse it would return Object
    public <T> T get(Class<T> tClass, String nameOfCol,  T orElse) {
        return Optional.ofNullable((T) getCurrentRow().get(nameOfCol)).orElse(orElse);
    }

    public <T> T get(Class<T> tClass, String nameOfCol) {
//        final String defaultString = "";
        final String defaultString = null;

        Object orElse;
        if (tClass == Integer.class) {
            orElse = 0;
        } else if (tClass == String.class) {
            orElse = defaultString;
        } else if (tClass == Double.class) {
            orElse = 0.0;
        } else if (tClass == Boolean.class) {
            orElse = false;
        } else { orElse = null; } // this will throw error, but idc you shouldnt use this function with other classes anyway

        return Optional.ofNullable((T) getCurrentRow().get(nameOfCol)).orElse((T) orElse);
    }
}
