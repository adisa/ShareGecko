package com.geckocap.login;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Arrays;
import java.util.List;

public class AddEditPatient
  extends Activity
{
  private TableRow confirmTab;
  private String day;
  private Spinner daySpinner;
  private Spinner monthSpinner;
  private String month_num;
  private EditText nameEditText;
  private int numPatients = 0;
  SharedPreferences patientNames;
  private LinearLayout patientPage;
  private TableRow patientsLabel;
  View.OnClickListener savePatientButtonClicked = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      String str = AddEditPatient.this.nameEditText.getText().toString();
      if (str.length() != 0)
      {
        AddEditPatient.this.savePatient(str);
        AddEditPatient.this.confirmTab.setVisibility(8);
        AddEditPatient.this.patientsLabel.setVisibility(0);
        AddEditPatient.this.showPatient(str);
        Toast.makeText(AddEditPatient.this, "Patient saved", 0).show();
        return;
      }
      AlertDialog.Builder localBuilder = new AlertDialog.Builder(AddEditPatient.this);
      localBuilder.setTitle(2131034128);
      localBuilder.setMessage(2131034129);
      localBuilder.setPositiveButton(2131034130, null);
      localBuilder.show();
    }
  };
  View.OnTouchListener showSpinner = new View.OnTouchListener()
  {
    public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
    {
      List localList;
      if (paramAnonymousMotionEvent.getAction() == 1)
      {
        int i = paramAnonymousView.getId();
        localList = null;
        switch (i)
        {
        }
      }
      for (;;)
      {
        ArrayAdapter localArrayAdapter = new ArrayAdapter(AddEditPatient.this.getApplicationContext(), 17367048, localList);
        localArrayAdapter.setDropDownViewResource(17367049);
        ((Spinner)paramAnonymousView).setAdapter(localArrayAdapter);
        return false;
        localList = Arrays.asList(new String[] { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" });
        paramAnonymousView = AddEditPatient.this.monthSpinner;
        continue;
        localList = Arrays.asList(new String[] { "1", "2", "3", "4" });
        paramAnonymousView = AddEditPatient.this.daySpinner;
        continue;
        localList = Arrays.asList(new String[] { "2007", "2006", "2005", "2004" });
        paramAnonymousView = AddEditPatient.this.yearSpinner;
      }
    }
  };
  private String year;
  private Spinner yearSpinner;
  
  private void savePatient(String paramString)
  {
    this.month_num = Integer.toString(1 + this.monthSpinner.getSelectedItemPosition());
    this.day = ((String)this.daySpinner.getSelectedItem());
    this.year = ((String)this.yearSpinner.getSelectedItem());
    this.patientNames.edit().putString(paramString, paramString).commit();
    this.numPatients = (1 + this.numPatients);
    SharedPreferences localSharedPreferences = getSharedPreferences(paramString + "Dob", 0);
    localSharedPreferences.edit().putString("month", this.month_num).commit();
    localSharedPreferences.edit().putString("day", this.day).commit();
    localSharedPreferences.edit().putString("year", this.year).commit();
  }
  
  private void setDefaultSpinners()
  {
    ArrayAdapter localArrayAdapter1 = new ArrayAdapter(getApplicationContext(), 17367048, Arrays.asList(new String[] { "Year of Birth" }));
    localArrayAdapter1.setDropDownViewResource(17367049);
    ArrayAdapter localArrayAdapter2 = new ArrayAdapter(getApplicationContext(), 17367048, Arrays.asList(new String[] { "Month" }));
    localArrayAdapter2.setDropDownViewResource(17367049);
    ArrayAdapter localArrayAdapter3 = new ArrayAdapter(getApplicationContext(), 17367048, Arrays.asList(new String[] { "Day" }));
    localArrayAdapter3.setDropDownViewResource(17367049);
    this.monthSpinner.setAdapter(localArrayAdapter2);
    this.daySpinner.setAdapter(localArrayAdapter3);
    this.yearSpinner.setAdapter(localArrayAdapter1);
  }
  
  private void showPatient(final String paramString)
  {
    final View localView = ((LayoutInflater)getSystemService("layout_inflater")).inflate(2130903068, null);
    TextView localTextView1 = (TextView)localView.findViewById(2131296390);
    TextView localTextView2 = (TextView)localView.findViewById(2131296391);
    TextView localTextView3 = (TextView)localView.findViewById(2131296392);
    localTextView1.setText(paramString);
    localTextView2.setText("DOB: " + this.month_num + "/" + this.day + "/" + this.year);
    localTextView3.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        localView.setVisibility(8);
        AddEditPatient.this.patientNames.edit().remove(paramString).commit();
        AddEditPatient localAddEditPatient = AddEditPatient.this;
        localAddEditPatient.numPatients = (-1 + localAddEditPatient.numPatients);
      }
    });
    this.patientPage.addView(localView);
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130903041);
    this.patientPage = ((LinearLayout)findViewById(2131296270));
    this.confirmTab = ((TableRow)findViewById(2131296271));
    this.nameEditText = ((EditText)findViewById(2131296273));
    this.monthSpinner = ((Spinner)findViewById(2131296275));
    this.daySpinner = ((Spinner)findViewById(2131296276));
    this.yearSpinner = ((Spinner)findViewById(2131296274));
    this.patientsLabel = ((TableRow)findViewById(2131296280));
    this.monthSpinner.setOnTouchListener(this.showSpinner);
    this.daySpinner.setOnTouchListener(this.showSpinner);
    this.yearSpinner.setOnTouchListener(this.showSpinner);
    ((Button)findViewById(2131296277)).setOnClickListener(this.savePatientButtonClicked);
    ((Button)findViewById(2131296278)).setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        AddEditPatient.this.nameEditText.setText("");
        AddEditPatient.this.setDefaultSpinners();
      }
    });
    ((TextView)findViewById(2131296279)).setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        Intent localIntent = new Intent(AddEditPatient.this.getApplicationContext(), AddGeckoCap.class);
        localIntent.putExtra("numPatients", AddEditPatient.this.numPatients);
        AddEditPatient.this.startActivity(localIntent);
      }
    });
    this.patientNames = getSharedPreferences("Patients", 0);
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.geckocap.login.AddEditPatient
 * JD-Core Version:    0.7.0.1
 */