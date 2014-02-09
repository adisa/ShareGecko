package com.geckocap.login;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseConnector
{
  private static final String DATABASE_NAME = "UserPatients";
  private SQLiteDatabase database;
  private DatabaseOpenHelper databaseOpenHelper;
  
  public DatabaseConnector(Context paramContext)
  {
    this.databaseOpenHelper = new DatabaseOpenHelper(paramContext, "UserPatients", null, 1);
  }
  
  public void close()
  {
    if (this.database != null) {
      this.database.close();
    }
  }
  
  public Cursor getAllPatients()
  {
    return this.database.query("patients", new String[] { "_id", "name" }, null, null, null, null, "name");
  }
  
  public Cursor getOnePatient(long paramLong)
  {
    return this.database.query("patients", null, "_id=" + paramLong, null, null, null, null);
  }
  
  public void insertPatient(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("name", paramString1);
    localContentValues.put("month", paramString2);
    localContentValues.put("day", paramString3);
    localContentValues.put("year", paramString4);
    open();
    this.database.insert("patients", null, localContentValues);
    close();
  }
  
  public void open()
    throws SQLException
  {
    this.database = this.databaseOpenHelper.getWritableDatabase();
  }
  
  public void updatePatient(long paramLong, String paramString1, String paramString2, String paramString3, String paramString4)
  {
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("name", paramString1);
    localContentValues.put("month", paramString2);
    localContentValues.put("day", paramString3);
    localContentValues.put("year", paramString4);
    open();
    this.database.update("patients", localContentValues, "_id=" + paramLong, null);
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
      paramSQLiteDatabase.execSQL("CREATE TABLE patients(_id integer primary key autoincrement,name TEXT, month TEXT, day TEXT, year TEXT);");
    }
    
    public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2) {}
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.geckocap.login.DatabaseConnector
 * JD-Core Version:    0.7.0.1
 */