package com.geckocap.login;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class Patients
  extends ListActivity
{
  public static final String ROW_ID = "row_id";
  private CursorAdapter patientAdapter;
  private ListView patientsListView;
  AdapterView.OnItemClickListener viewPatientListener = new AdapterView.OnItemClickListener()
  {
    public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
    {
      Intent localIntent = new Intent(Patients.this, ViewPatient.class);
      localIntent.putExtra("row_id", paramAnonymousLong);
      Patients.this.startActivity(localIntent);
    }
  };
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.patientsListView = getListView();
    this.patientsListView.setOnItemClickListener(this.viewPatientListener);
    setListAdapter(new SimpleCursorAdapter(this, 2130903062, null, new String[] { "name" }, new int[] { 2131296377 }));
  }
  
  public boolean onCreateOptionsMenu(Menu paramMenu)
  {
    super.onCreateOptionsMenu(paramMenu);
    getMenuInflater().inflate(2131230720, paramMenu);
    return true;
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    startActivity(new Intent(this, AddEditPatient.class));
    return super.onOptionsItemSelected(paramMenuItem);
  }
  
  public void onResume()
  {
    super.onResume();
    new GetPatientsTask(null).execute(null);
  }
  
  public void onStop()
  {
    Cursor localCursor = this.patientAdapter.getCursor();
    if (localCursor != null) {
      localCursor.deactivate();
    }
    this.patientAdapter.changeCursor(null);
    super.onStop();
  }
  
  private class GetPatientsTask
    extends AsyncTask<Object, Object, Cursor>
  {
    DatabaseConnector databaseConnector = new DatabaseConnector(Patients.this);
    
    private GetPatientsTask() {}
    
    protected Cursor doInBackground(Object... paramVarArgs)
    {
      this.databaseConnector.open();
      return this.databaseConnector.getAllPatients();
    }
    
    protected void onPostExecute(Cursor paramCursor)
    {
      Patients.this.patientAdapter.changeCursor(paramCursor);
      this.databaseConnector.close();
    }
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.geckocap.login.Patients
 * JD-Core Version:    0.7.0.1
 */