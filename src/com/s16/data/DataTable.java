package com.s16.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class DataTable {
	
	protected static final String VND_PREFIX = "vnd.datatable";
	protected static final String NAME_QUOTES = "`";
	protected static final String SCHEME = "content://";
	
	protected static final String[] DATA_TYPES = new String[] {
		"VARCHAR", "NVARCHAR", "TEXT", "INTEGER", "REAL", "FLOAT", 
		"BOOLEAN", "CLOB", "BLOB", "TIMESTAMP", "NUMERIC", 
		"VARYING CHARACTER", "NATIONAL VARYING CHARACTER", "NONE"
	};
	
	private final String mAuthority;
	private final String mTableName;
	private final ArrayList<DataColumn> mColumns = new ArrayList<DataColumn>();
	private SQLiteDatabase mDatabase;
	private Executor mExecutor;
	private int mId;
	
	public static DataTable newInstance(String authority, String tableName) {
		return new DataTable(authority, tableName); 
	}
	
	public static DataTable newInstance(String authority, int id, String tableName) {
		return new DataTable(authority, id, tableName); 
	}
	
	public DataTable(String authority, String tableName) {
		this(authority, tableName.hashCode(), tableName);
	}
	
	public DataTable(String authority, int id, String tableName) {
		mAuthority = authority;
		mTableName = tableName;
		mExecutor = new Executor();
		mId = id;
	}
	
	public int getId() {
		return mId;
	}

	public DataTable setId(int id) {
		mId = id;
		return this;
	}

	protected void assertTable() {
        if (mTableName == null) {
            throw new IllegalStateException("Table not specified");
        }
        
        if (mColumns == null || mColumns.size() == 0) {
            throw new IllegalStateException("Columns not specified");
        }
    }
	
	protected void assertDatabase() {
        if (mDatabase == null) {
            throw new IllegalStateException("Databse not specified");
        }
	}
	
	protected boolean checkHasPrimaryKey() {
		if (mColumns == null || mColumns.size() == 0) {
			return false;
		}
		
		int keyCount = 0;
		for(int i = 0; i < mColumns.size(); i++) {
			if (mColumns.get(i).isPrimaryKey) {
				keyCount++;
			}
		}
		
		if (keyCount > 1) {
			throw new IllegalStateException("Table does not allow more then one primary key.");
		}
		
		return (keyCount == 1);
	}
	
	@Override
    public String toString() {
		String value = mTableName;
		value += " (";
		for(int i = 0; i < mColumns.size(); i++) {
			if (i == 0) value += ", "; 
			value += mColumns.toString();
		}
		value += ")";
		return value;
	}
	
	@Override
    public boolean equals(Object o) {
		if (o instanceof DataTable) {
			DataTable other = (DataTable)o;
			return mTableName.equals(other.getTableName());
		}
		return super.equals(o);
	}
	
	/**
	 * Get the String of Uri. 
	 */
	public String getUriString() {
		return SCHEME + mAuthority + "/" + mTableName;
	}
	
	/**
	 * Get the Uri for this table. 
	 */
	public Uri getUri() {
		return Uri.parse(getUriString());
	}
	
	/**
	 * Get the Uri for this table. 
	 */
	public Uri getUri(String uriString) {
		if (TextUtils.isEmpty(uriString)) return null;
		return Uri.parse(uriString + "/" + mTableName);
	}
	
	/**
	 * Get the name of table. 
	 */
	public String getTableName() {
		return mTableName;
	}
	
	/**
	 * Add primary key column to table. 
	 */
	public DataTable addPrimaryKey(String columnName, String sqlDataType, boolean isAutoIncrement) {
		return addColumn(columnName, sqlDataType, true, false, isAutoIncrement, null);
	}
	
	/**
	 * Add column to table. 
	 */
	public DataTable addColumn(DataColumn column) {
		column.index = mColumns.size();
		mExecutor.putColumn(column);
		mColumns.add(column);
		return this;
	}
	
	/**
	 * Add column to table. 
	 */
	public DataTable addColumn(String columnName, String sqlDataType) {
		return addColumn(columnName, sqlDataType, false, true, false, null);
	}
	
	/**
	 * Add column to table. 
	 */
	public DataTable addColumn(String columnName, String sqlDataType, boolean isAllowNull) {
		return addColumn(columnName, sqlDataType, false, isAllowNull, false, null);
	}
	
	/**
	 * Add column to table. 
	 */
	public DataTable addColumn(String columnName, String sqlDataType, boolean isAllowNull, String defaultValue) {
		return addColumn(columnName, sqlDataType, false, isAllowNull, false, defaultValue);
	}
	
	/**
	 * Add column to table. 
	 */
	public DataTable addColumn(String columnName, String sqlDataType, boolean isPrimaryKey
			, boolean isAllowNull, boolean isAutoIncrement, String defaultValue) {
		
		if (isPrimaryKey && checkHasPrimaryKey()) {
			throw new IllegalStateException("Table does not allow more then one primary key.");
		}
		
		DataColumn column = new DataColumn(columnName, sqlDataType);
		column.isPrimaryKey = isPrimaryKey;
		column.isAllowNull = isAllowNull;
		column.isAutoIncrement = isAutoIncrement;
		column.defaultValue = defaultValue;
		return addColumn(column);
	}
	
	/**
	 * Get the column of given name from table. 
	 */
	public DataColumn getColumn(String columnName) {
		int colIndex = indexOf(columnName);
		if (colIndex > -1) {
			return mColumns.get(colIndex);
		}
		return null;
	}
	
	/**
	 * Get the primary key column from table. 
	 */
	public DataColumn getPrimaryKey() {
		assertTable();
		if (!checkHasPrimaryKey()) return null;
		
		DataColumn keyColumn = null;
		for(int i = 0; i < mColumns.size(); i++) {
			DataColumn column = mColumns.get(i); 
			if (column.isPrimaryKey) {
				keyColumn = column;
				break;
			}
		}
		
		return keyColumn;
	}
	
	/**
	 * Get the columns name array from table. 
	 */
	public String[] getColumnNames() {
		assertTable();
		String[] columns = new String[mColumns.size()];
		for(int i = 0; i < mColumns.size(); i++) {
			columns[i] = mColumns.get(i).name; 
		}
		return columns;
	}
	
	/**
	 * Get the count of columns from table. 
	 */
	public int getColumnCount() {
    	return mColumns.size();
    }
	
	/**
	 * Get the URI pattern is for more than one row. 
	 */
	public String getContentTypeDir() {
		String name = getTableName().toLowerCase();
		if (name.endsWith("y")) {
			return ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + VND_PREFIX + "." + name.substring(0, name.length() - 1) + "ies";
		} else if (name.endsWith("s")) {
			return ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + VND_PREFIX + "." + name;
		} else {
			return ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + VND_PREFIX + "." + name + "s";
		}
	}
	
	/**
	 * Get the URI pattern is for a single row.
	 */
	public String getContentTypeItem() {
		String name = getTableName().toLowerCase();
		if (name.endsWith("s")) {
			return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + VND_PREFIX + "." + name.substring(0, name.length() - 1);
		}
		return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + VND_PREFIX + "." + name;
	}
	
	/**
	 * Get the index of column from table. 
	 */
	public int indexOf(String columnName) {
		if (TextUtils.isEmpty(columnName)) return -1;
		for(int i = 0; i < mColumns.size(); i++) {
			if (mColumns.get(i).name.equals(columnName)) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Get the index of column from table. 
	 */
	public int indexOf(DataColumn column) {
		if (column == null) return -1;
		for(int i = 0; i < mColumns.size(); i++) {
			if (mColumns.get(i).name.equals(column.name)) {
				return i;
			}
		}
		return -1;
	}
	
	protected String createStatement(boolean withIsNotExist) {
		assertTable();
		String sql = "";
		sql += "CREATE TABLE ";
		if (withIsNotExist) {
			sql += "IF NOT EXISTS ";
		}
		sql += NAME_QUOTES + mTableName + NAME_QUOTES + " (";
		
		for(int i = 0; i < mColumns.size(); i++) {
			DataColumn column = mColumns.get(i);
			
			if (i > 0) sql += ",";
			sql += column.toString();
		}
		
		sql += ");";
		
		Log.w("createStatement", sql);
		return sql;
	}
	
	protected String dropStatement(boolean withIsExist) {
		assertTable();
		return "DROP TABLE " + (withIsExist ? "IF EXISTS " : "") + NAME_QUOTES + mTableName + NAME_QUOTES + ");";
	}
	
	public SQLiteQueryBuilder getQueryBuilder() {
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(mTableName);
		builder.setProjectionMap(mExecutor.getProjectionMap());
		return builder;
	}
	
	@SuppressWarnings("unchecked")
	protected <T> T typeConvertOrDefault(Object value, T defVal) {
		if (value == null) return defVal;
		if (defVal.getClass().equals(value.getClass())) {
			return (T)value;
		}
		return defVal;
	}
	
	public ContentValues makeContentValues(Object... values)
			throws IllegalArgumentException {
		if (values == null || values.length != getColumnCount()) {
			throw new IllegalArgumentException();
		}
		ContentValues contentValues = new ContentValues();
		
		for(int i=0; i<mColumns.size(); i++) {
			DataColumn column = mColumns.get(i);
			Object val = values[i];
			if (val == null) {
				contentValues.putNull(column.name);
			} else if (val instanceof Short) {
				contentValues.put(column.name, typeConvertOrDefault(val, Short.MIN_VALUE));
			} else if (val instanceof Integer) {
				contentValues.put(column.name, typeConvertOrDefault(val, Integer.MIN_VALUE));
			} else if (val instanceof Long) {
				contentValues.put(column.name, typeConvertOrDefault(val, Long.MIN_VALUE));
			} else if (val instanceof Float) {
				contentValues.put(column.name, typeConvertOrDefault(val, Float.MIN_VALUE));
			} else if (val instanceof Double) {
				contentValues.put(column.name, typeConvertOrDefault(val, Double.MIN_VALUE));
			} else if (val instanceof Boolean) {
				contentValues.put(column.name, typeConvertOrDefault(val, Boolean.FALSE));
			} else if (val instanceof String) {
				contentValues.put(column.name, typeConvertOrDefault(val, (String)""));
			} else if (val instanceof byte[]) {
				contentValues.put(column.name, typeConvertOrDefault(val, (byte[])null));
			}
		}
		return contentValues;
	}
	
    public Executor from(SQLiteDatabase db) {
    	assertTable();
    	mExecutor.reset();
    	mDatabase = db;
    	return mExecutor;
    }
    
    /**
     * Execute create table.
     */
    public void create(SQLiteDatabase db) {
    	create(db, true);
    }
    
    /**
     * Execute create table.
     */
    public void create(SQLiteDatabase db, boolean checkIsNotExist) {
    	assertTable();
    	db.execSQL(createStatement(checkIsNotExist));
    }
    
    /**
     * Execute drop table.
     */
    public void drop(SQLiteDatabase db) {
    	drop(db, true);
    }
    
    /**
     * Execute drop table.
     */
    public void drop(SQLiteDatabase db, boolean checkIsExist) {
    	assertTable();
    	db.execSQL(dropStatement(checkIsExist));
    }

	public class DataColumn {
		
		public int index;
		public String name;
		public String dataType;
		public boolean isPrimaryKey;
		public boolean isAllowNull;
		public boolean isAutoIncrement;
		public String defaultValue;
		public String extended;
		private String mActualType;
		
		public DataColumn(String columnName, String sqlDataType) {
			name = columnName;
			dataType = sqlDataType;
			isAllowNull = true;
			index = -1;
		}
		
		public String getActualDataType() {
			if (TextUtils.isEmpty(dataType)) return null;
			if (TextUtils.isEmpty(mActualType)) {
				String type = dataType; 
				if (type.contains("(")) {
					type = type.substring(0, type.indexOf('('));
				}
				mActualType = type.trim().toUpperCase();
			}
			return mActualType;
		}
		
		@Override
	    public String toString() {
			return NAME_QUOTES + name + NAME_QUOTES
					+ " " + dataType
					+ (isPrimaryKey ? " PRIMARY KEY" : "")
					+ (isAutoIncrement ? " AUTOINCREMENT" : "")
					+ (!isAllowNull ? " NOT NULL" : "")
					+ (defaultValue != null ? " DEFAULT " + defaultValue : "")
					+ (extended != null ? " " + extended : "");
		}
	}
	
	public class Executor {
		
		private Map<String, String> mProjectionMap = new HashMap<String, String>();
	    private StringBuilder mSelection = new StringBuilder();
	    private List<String> mSelectionArgs = new ArrayList<String>();
	    
	    public Executor() {
	    	
	    }
	    
	    public Executor where(String id) {
	    	assertTable();
	    	mSelection.append("(").append(getPrimaryKey().name + "=?").append(")");
	    	mSelectionArgs.add(id);
	    	return this;
	    }
	    
	    protected void mapColumns(String[] columns) {
	        for (int i = 0; i < columns.length; i++) {
	            final String target = mProjectionMap.get(columns[i]);
	            if (target != null) {
	                columns[i] = target;
	            }
	        }
	    }
		
		/**
	     * Reset any internal state, allowing this builder to be recycled.
	     */
	    protected void reset() {
	        mSelection.setLength(0);
	        mSelectionArgs.clear();
	    }
	    
	    protected void putColumn(DataColumn column) {
	    	mProjectionMap.put(column.name, mTableName + "." + column.name);
	    }
	    
	    protected Map<String, String> getProjectionMap() {
	    	return mProjectionMap;
	    }
	    
	    /**
	     * Return selection string for current internal state.
	     *
	     * @see #getSelectionArgs()
	     */
	    protected String getSelection() {
	        return mSelection.toString();
	    }

	    /**
	     * Return selection arguments for current internal state.
	     *
	     * @see #getSelection()
	     */
	    protected String[] getSelectionArgs() {
	        return mSelectionArgs.toArray(new String[mSelectionArgs.size()]);
	    }
	    
	    /**
		 * Map a column. 
		 */
	    public Executor map(String fromColumn, String toClause) {
	        mProjectionMap.put(fromColumn, toClause + " AS " + fromColumn);
	        return this;
	    }
	    
	    /**
	     * Append the given selection clause to the internal state. Each clause is
	     * surrounded with parenthesis and combined using {@code AND}.
	     */
	    public Executor where(String selection, String... selectionArgs) {
	    	assertTable();
	        if (TextUtils.isEmpty(selection)) {
	            if (selectionArgs != null && selectionArgs.length > 0) {
	                throw new IllegalArgumentException("Valid selection required when including arguments=");
	            }

	            // Shortcut when clause is empty
	            return this;
	        }

	        if (mSelection.length() > 0) {
	            mSelection.append(" AND ");
	        }

	        mSelection.append("(").append(selection).append(")");
	        if (selectionArgs != null) {
	        	for(String arg : selectionArgs) {
	        		mSelectionArgs.add(arg);
	        	}
	        }

	        return this;
	    }
	    
	    /**
	     * Execute query using the current internal state as {@code WHERE} clause.
	     */
	    public Cursor query(boolean distinct, String orderBy, String limit) {
	        return query(distinct, getColumnNames(), null, null, orderBy, limit);
	    }
	    
	    /**
	     * Execute query using the current internal state as {@code WHERE} clause.
	     */
	    public Cursor query(String orderBy, String limit) {
	        return query(getColumnNames(), null, null, orderBy, limit);
	    }
	    
	    /**
	     * Execute query using the current internal state as {@code WHERE} clause.
	     */
	    public Cursor query(String[] columns, String orderBy, String limit) {
	        return query(columns, null, null, orderBy, limit);
	    }
	    
	    /**
	     * Execute query using the current internal state as {@code WHERE} clause.
	     */
	    public Cursor query(boolean distinct, String[] columns, String orderBy, String limit) {
	        return query(distinct, columns, null, null, orderBy, limit);
	    }

	    /**
	     * Execute query using the current internal state as {@code WHERE} clause.
	     */
	    public Cursor query(String[] columns, String groupBy,
                String having, String orderBy, String limit) {
	    	return query(false, columns, groupBy, having, orderBy, limit);
	    }
	    
	    /**
	     * Execute query using the current internal state as {@code WHERE} clause.
	     */
	    public Cursor query(boolean distinct, String[] columns, String groupBy,
	                        String having, String orderBy, String limit) {
	        assertTable();
	        assertDatabase();
	        if (columns != null) mapColumns(columns);
	        Cursor cursor = mDatabase.query(distinct, mTableName, columns, getSelection(), getSelectionArgs(), groupBy, having,
	                orderBy, limit);
	        return cursor;
	    }
	    
	    /**
	     * Execute update using the current internal state as {@code WHERE} clause.
	     */
	    public long save(ContentValues values) {
	        assertTable();
	        assertDatabase();
	        long result = mDatabase.update(mTableName, values, getSelection(), getSelectionArgs());
	        if (result < 1) {
	        	result = mDatabase.insert(mTableName, null, values);
	        }
	        return result; 
	    }
	    
	    /**
	     * Execute update using the current internal state as {@code WHERE} clause.
	     */
	    public long insert(ContentValues values) {
	        assertTable();
	        assertDatabase();
	        return mDatabase.insert(mTableName, null, values);
	    }
	    
	    /**
	     * Execute update using the current internal state as {@code WHERE} clause.
	     */
	    public long insert(String nullColumnHack, ContentValues values) {
	        assertTable();
	        assertDatabase();
	        return mDatabase.insert(mTableName, nullColumnHack, values);
	    }

	    /**
	     * Execute update using the current internal state as {@code WHERE} clause.
	     */
	    public int update(ContentValues values) {
	        assertTable();
	        assertDatabase();
	        return mDatabase.update(mTableName, values, getSelection(), getSelectionArgs());
	    }

	    /**
	     * Execute delete using the current internal state as {@code WHERE} clause.
	     */
	    public int delete() {
	        assertTable();
	        assertDatabase();
	        return mDatabase.delete(mTableName, getSelection(), getSelectionArgs());
	    }
	}
}
