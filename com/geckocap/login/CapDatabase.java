package com.geckocap.login;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CapDatabase
{
  private static final String DATABASE_NAME = "GeckoCaps";
  private SQLiteDatabase database;
  private DatabaseOpenHelper databaseOpenHelper;
  
  public CapDatabase(Context paramContext)
  {
    this.databaseOpenHelper = new DatabaseOpenHelper(paramContext, "GeckoCaps", null, 1);
  }
  
  public void close()
  {
    if (this.database != null) {
      this.database.close();
    }
  }
  
  public Cursor getAllCaps()
  {
    return this.database.query("geckocaps", new String[] { "_id", "serial_num" }, null, null, null, null, "serial_num");
  }
  
  public Cursor getOneCap(long paramLong)
  {
    return this.database.query("geckocaps", null, "_id=" + paramLong, null, null, null, null);
  }
  
  public void insertCap(String paramString1, String paramString2, String paramString3)
  {
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("serial_num", paramString1);
    localContentValues.put("prescription", paramString2);
    localContentValues.put("usage", paramString3);
    open();
    this.database.insert("geckocaps", null, localContentValues);
    close();
  }
  
  public void open()
    throws SQLException
  {
    this.database = this.databaseOpenHelper.getWritableDatabase();
  }
  
  public void updatecap(long paramLong, String paramString1, String paramString2, String paramString3)
  {
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("serial_num", paramString1);
    localContentValues.put("prescription", paramString2);
    localContentValues.put("usage", paramString3);
    open();
    this.database.update("geckocaps", localContentValues, "_id=" + paramLong, null);
    close();
  }
  
  private class DatabaseOpenHelper
    extends SQLiteOpenHelper
  {
    public DatabaseOpenHelper(Context paramContext, String paramString, SQLiteDatabase.CursorFactory paramCursorFactory, int paramInt)
    {
      super(paramString, paramCursorFactory, paramInt);
    }
    
    public void onCreate(SQLiteDatabase paramSQLiteDatabase)
    {
      paramSQLiteDatabase.execSQL("CREATE TABLE geckocaps(_id integer primary key autoincrement,serial_num TEXT, prescription TEXT, usage TEXT);");
    }
    
    public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2) {}
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.geckocap.login.CapDatabase
 * JD-Core Version:    0.7.0.1
 */