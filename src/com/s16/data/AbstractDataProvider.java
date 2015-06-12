package com.s16.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.BaseColumns;
import android.text.TextUtils;

public abstract class AbstractDataProvider extends ContentProvider {

	protected static final String TAG = AbstractDataProvider.class.getSimpleName();
	
	public static final String METHOD_BACKUP = "backup";
	public static final String METHOD_RESTORE = "restore";
	public static final String METHOD_SAVE = "save";
	public static final String METHOD_GETMAXID = "getMaxId";
	public static final String RETURN_KEY = "result";
	
	private static final String DATABASE_NAME = "database";
	private static final int DATABASE_VERSION = 1;
	
	private SQLiteDatabase mDatabase;
	private String mDatabasePath;
	
	private class DatabaseHelper extends SQLiteOpenHelper {		

		DatabaseHelper(Context context) {
			super(context, mDatabaseName, null, mDatabaseVersion);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			if (mTables == null) return;
			for(int i = 0; i < mTables.length; i++) {
				mTables[i].create(db);
			}
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if (mTables == null) return;
			for(int i = 0; i < mTables.length; i++) {
				mTables[i].drop(db);
			}
			onCreate(db);
		}
	}
	
	private final String mDatabaseName;
	private final int mDatabaseVersion;
	private final DataTable[] mTables;
	private DatabaseHelper mDbHelper;
	
	public AbstractDataProvider()  {
		super();
		mDatabaseName = getDatabaseName();
		mDatabaseVersion = getDatabaseVersion();
		mTables = getAllTables();
	}
	
	protected String getDatabaseName() {
		return DATABASE_NAME;
	}
	
	protected int getDatabaseVersion() {
		return DATABASE_VERSION;
	}
	
	protected abstract Uri getContentUri();
	
	protected abstract DataTable[] getAllTables();
	
	protected abstract DataTable getTable(Uri uri);
	
	protected abstract boolean useDistinct(Uri uri);
	
	@Override
	public abstract String getType(Uri uri);

	protected DataTable getTable(String tableName) {
		if (mTables == null) return null;
		for(int i = 0; i < mTables.length; i++) {
			if (mTables[i].getTableName().equals(tableName)) {
				return mTables[i];
			}
		}
		return null;
	}
	
	protected SQLiteDatabase getDatabase() {
		if (mDatabase == null) {
			if(mDbHelper == null) return null;
			try {
				mDatabase = mDbHelper.getWritableDatabase();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		
		return mDatabase;
	}
	
	protected File getDatabasePath() {
		if (!TextUtils.isEmpty(mDatabasePath)) {
			return new File(mDatabasePath);
		}
		return null;
	}
	
	public boolean isOpen() {
		SQLiteDatabase database = getDatabase();
		return (database != null) && (database.isOpen());
	}
	
	public void close() {
		if (mDbHelper != null) mDbHelper.close();
	}
	
	@Override
	public boolean onCreate() {
		if(mDbHelper == null) {
			mDbHelper = new DatabaseHelper(getContext());
		}
		
		SQLiteDatabase database = getDatabase();
		if (database != null) {
			mDatabasePath = database.getPath();
		}
		
		return (database != null);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor result = null;
		final SQLiteDatabase database = getDatabase();
		if (database == null) return result;
		
		boolean distinct = useDistinct(uri);
		DataTable table = getTable(uri);
		if (table != null) {
			if (projection == null) {
				result = table.from(database).where(selection, selectionArgs).query(distinct, sortOrder, null);
			} else {
				result = table.from(database).where(selection, selectionArgs).query(distinct, projection, sortOrder, null);	
			}
			if (result != null) {
				result.moveToFirst();
				result.setNotificationUri(getContext().getContentResolver(), uri);
			}
		}
		return result;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		final SQLiteDatabase database = getDatabase();
		if (database == null) return null;
		
		long rowID = 0;
		DataTable table = getTable(uri);
		if (table != null) {
			rowID = table.from(database).insert(values);
		}
		if (rowID > 0) {
			Uri _uri = ContentUris.withAppendedId(getContentUri(), rowID);
	        getContext().getContentResolver().notifyChange(_uri, null);
	        return _uri;
		}
		
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		final SQLiteDatabase database = getDatabase();
		if (database == null) return 0;
		
		int count = 0;
		DataTable table = getTable(uri);
		if (table != null) {
			count += table.from(database).where(selection, selectionArgs).delete();
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		final SQLiteDatabase database = getDatabase();
		if (database == null) return 0;
		
		int count = 0;
		DataTable table = getTable(uri);
		if (table != null) {
			count = table.from(database).where(selection, selectionArgs).update(values);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return (int)count;
	}
	
	public int save(String tableName, ContentValues values, String selection,
			String[] selectionArgs) {
		final SQLiteDatabase database = getDatabase();
		if (database == null) return 0;
		
		int count = 0;
		long rowID = 0;
		
		DataTable table = getTable(tableName);
		if (table != null) {
			count = table.from(database).where(selection, selectionArgs).update(values);
			if (count == 0) {
				
				if (values.containsKey(BaseColumns._ID)) {
					long id = values.getAsLong(BaseColumns._ID);
					if (id < 0) {
						id = getMaxId(tableName, BaseColumns._ID);
						values.put(BaseColumns._ID, id + 1);
					}
				}
				
				rowID = table.from(database).insert(values);
			} else {
				getContext().getContentResolver().notifyChange(table.getUri(), null);
			}
		}
		
		if (rowID > 0) {
			Uri _uri = ContentUris.withAppendedId(getContentUri(), rowID);
	        getContext().getContentResolver().notifyChange(_uri, null);
	        return 1;
		}
		
		return count;
	}
	
	public long getMaxId(String tableName, String idColumn) {
		final SQLiteDatabase database = getDatabase();
		if (database == null) return 0L;
		
		long id = 0L;
		Cursor idCursor = database.rawQuery("SELECT MAX(" + idColumn + ") FROM " + tableName + " LIMIT 1;" , null);
		if (idCursor != null) {
			if (idCursor.moveToFirst()) {
				id = idCursor.getInt(0);
			}
			idCursor.close();
		}
		return id;
	}
	
	@Override
    public int bulkInsert(Uri uri, ContentValues[] insertValuesArray) {
		final SQLiteDatabase database = getDatabase();
		if (database == null) return -1;
		if (insertValuesArray == null) return -1;
		
		DataTable table = getTable(uri);
		if (table != null) {
			int count = insertValuesArray.length;
			database.beginTransaction();
			
			for (int i=0; i<count; i++) {
				int resultCount = 0;	
				ContentValues values = insertValuesArray[i];
				if (values.containsKey(BaseColumns._ID)) {
					long id = values.getAsLong(BaseColumns._ID);
					if (id > 0) {
						String selection = BaseColumns._ID + " IS ?";
						resultCount = table.from(database).where(selection, new String[] { String.valueOf(id) }).update(values);
					}
				}
				
				if (resultCount == 0) {
					table.from(database).insert(values);
				}
			}
			
			database.setTransactionSuccessful();
			database.endTransaction();
			
			getContext().getContentResolver().notifyChange(uri, null);
			return count;
		}
		return -1;
	}
	
	@Override
	public Bundle call(String method, String arg, Bundle extras) {
		Bundle resultBundle = new Bundle();
		
		if (METHOD_BACKUP.equals(method)) {
			File srcDb = getDatabasePath();
			File destDb = new File(arg);
			boolean result = copyFile(srcDb, destDb);
			resultBundle.putBoolean(RETURN_KEY, result);
			
		} else if (METHOD_RESTORE.equals(method)) {
			File srcDb = new File(arg);
			File destDb = getDatabasePath();
			boolean result = copyFile(srcDb, destDb);
			resultBundle.putBoolean(RETURN_KEY, result);
			
		} else if (METHOD_SAVE.equals(method)) {
			ContentValues values = new ContentValues();
			String[] columns = extras.getStringArray("columns");
			for(String col : columns) {
				values.put(col, extras.getString(col));
			}
			String selection = extras.getString("selection", null);
			String[] selectionArgs = extras.getStringArray("selectionArgs");
			
			int result = save(arg, values, selection, selectionArgs);
			resultBundle.putInt(RETURN_KEY, result);
			
		} else if (METHOD_GETMAXID.equals(method)) {
			long id = getMaxId(arg, BaseColumns._ID);
			resultBundle.putLong(RETURN_KEY, id);
		}
		
		return resultBundle;
	}
	
	@SuppressWarnings("resource")
	protected boolean copyFile(File srcFile, File destFile) {
		try {
            File sd = Environment.getExternalStorageDirectory();
            if (sd.canWrite() && sd.canRead()) {
	            FileChannel src = new FileInputStream(srcFile).getChannel();
	            FileChannel dst = new FileOutputStream(destFile).getChannel();
	            dst.transferFrom(src, 0, src.size());
	            src.close();
	            dst.close();
	        }
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	return false;
	    }
		return true;
	}
}
