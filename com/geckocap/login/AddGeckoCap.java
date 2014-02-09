package com.geckocap.login;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class AddGeckoCap
  extends Activity
{
  static HashMap<String, ArrayList<Object>> geckoCaps = new HashMap();
  DialogInterface.OnClickListener addPatients = new DialogInterface.OnClickListener()
  {
    public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
    {
      Intent localIntent = new Intent(AddGeckoCap.this.getApplicationContext(), AddEditPatient.class);
      AddGeckoCap.this.startActivity(localIntent);
    }
  };
  private TableRow capsLabel;
  private Spinner choosePatient;
  private TableRow confirmTab;
  private LinearLayout geckoPage;
  private ImageButton maintain;
  View.OnClickListener maintainSelected = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      if (!AddGeckoCap.this.maintain.isSelected())
      {
        AddGeckoCap.this.maintain.setSelected(true);
        AddGeckoCap.this.rescue.setSelected(false);
      }
    }
  };
  private List<String> names = new ArrayList();
  private EditText numEditText;
  SharedPreferences patientNames;
  private EditText prescEditText;
  private ImageButton rescue;
  View.OnClickListener rescueSelected = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      if (!AddGeckoCap.this.rescue.isSelected())
      {
        AddGeckoCap.this.rescue.setSelected(true);
        AddGeckoCap.this.maintain.setSelected(false);
        AddGeckoCap.this.usageEditText.setText("as needed");
      }
    }
  };
  View.OnClickListener saveButtonListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      String str1 = AddGeckoCap.this.numEditText.getText().toString();
      String str2 = (String)AddGeckoCap.this.choosePatient.getSelectedItem();
      if ((str1.length() < 1) && (str2 != "Choose patient"))
      {
        AlertDialog.Builder localBuilder2 = new AlertDialog.Builder(AddGeckoCap.this);
        localBuilder2.setTitle("Oops!");
        localBuilder2.setMessage("Must supply GeckoCap serial no.");
        localBuilder2.setPositiveButton(2131034130, null);
        localBuilder2.show();
        return;
      }
      if (str2 == "Choose patient")
      {
        AlertDialog.Builder localBuilder1 = new AlertDialog.Builder(AddGeckoCap.this);
        localBuilder1.setTitle("Oops!");
        localBuilder1.setMessage("Must select patient name");
        localBuilder1.setPositiveButton(2131034130, null);
        localBuilder1.show();
        return;
      }
      AddGeckoCap.this.confirmTab.setVisibility(8);
      AddGeckoCap.this.capsLabel.setVisibility(0);
      AddGeckoCap.this.showCap(str1, str2);
      AddGeckoCap.this.saveCap(str1, str2);
      ((InputMethodManager)AddGeckoCap.this.getSystemService("input_method")).hideSoftInputFromWindow(AddGeckoCap.this.numEditText.getWindowToken(), 0);
    }
  };
  View.OnTouchListener setSpinner = new View.OnTouchListener()
  {
    public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
    {
      if (paramAnonymousMotionEvent.getAction() == 1)
      {
        if (AddGeckoCap.this.names.size() < 1)
        {
          AlertDialog.Builder localBuilder = new AlertDialog.Builder(AddGeckoCap.this);
          localBuilder.setTitle("Oops!");
          localBuilder.setMessage("No patients registered yet");
          localBuilder.setPositiveButton(2131034130, AddGeckoCap.this.addPatients);
          localBuilder.show();
        }
      }
      else {
        return false;
      }
      ArrayAdapter localArrayAdapter = new ArrayAdapter(AddGeckoCap.this.getApplicationContext(), 17367048, AddGeckoCap.this.names);
      localArrayAdapter.setDropDownViewResource(17367049);
      AddGeckoCap.this.choosePatient.setAdapter(localArrayAdapter);
      return false;
    }
  };
  private EditText usageEditText;
  private EditText verifyEditText;
  
  private void saveCap(String paramString1, String paramString2)
  {
    String str = this.prescEditText.getText().toString();
    SharedPreferences.Editor localEditor = getSharedPreferences(paramString2 + str, 0).edit();
    localEditor.putString("prescription", str).commit();
    if (this.rescue.isSelected()) {
      localEditor.putBoolean("rescue", true).commit();
    }
    for (;;)
    {
      localEditor.putString("serialNum", paramString1).commit();
      ((ArrayList)geckoCaps.get(paramString2)).add(str);
      return;
      localEditor.putBoolean("rescue", false).commit();
    }
  }
  
  private void showCap(String paramString1, String paramString2)
  {
    final View localView = ((LayoutInflater)getSystemService("layout_inflater")).inflate(2130903066, null);
    TextView localTextView1 = (TextView)localView.findViewById(2131296380);
    TextView localTextView2 = (TextView)localView.findViewById(2131296385);
    TextView localTextView3 = (TextView)localView.findViewById(2131296386);
    TextView localTextView4 = (TextView)localView.findViewById(2131296387);
    localTextView1.setText(paramString1);
    String str1 = this.prescEditText.getText().toString();
    localTextView2.setText(paramString2 + " is to use " + str1);
    String str2 = this.usageEditText.getText().toString();
    localTextView3.setText(str2 + " times per day");
    if (this.rescue.isSelected())
    {
      ((TextView)localView.findViewById(2131296379)).setText("Rescue");
      ((ImageView)localView.findViewById(2131296378)).setImageResource(2130837570);
      localTextView3.setText(str2);
    }
    localTextView4.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        localView.setVisibility(8);
        AddGeckoCap.this.getSharedPreferences(this.val$spName, 0).edit().clear().commit();
      }
    });
    this.geckoPage.addView(localView);
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130903040);
    this.geckoPage = ((LinearLayout)findViewById(2131296256));
    this.confirmTab = ((TableRow)findViewById(2131296257));
    this.patientNames = getSharedPreferences("Patients", 0);
    Iterator localIterator = this.patientNames.getAll().entrySet().iterator();
    for (;;)
    {
      if (!localIterator.hasNext())
      {
        this.choosePatient = ((Spinner)findViewById(2131296259));
        this.choosePatient.setOnTouchListener(this.setSpinner);
        this.maintain = ((ImageButton)findViewById(2131296260));
        this.maintain.setOnClickListener(this.maintainSelected);
        this.rescue = ((ImageButton)findViewById(2131296261));
        this.rescue.setOnClickListener(this.rescueSelected);
        this.maintain.setSelected(true);
        this.numEditText = ((EditText)findViewById(2131296262));
        this.verifyEditText = ((EditText)findViewById(2131296263));
        this.prescEditText = ((EditText)findViewById(2131296264));
        this.usageEditText = ((EditText)findViewById(2131296265));
        ((Button)findViewById(2131296266)).setOnClickListener(this.saveButtonListener);
        ((Button)findViewById(2131296267)).setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            ArrayAdapter localArrayAdapter = new ArrayAdapter(AddGeckoCap.this.getApplicationContext(), 17367048, Arrays.asList(new String[] { "Choose patient" }));
            localArrayAdapter.setDropDownViewResource(17367049);
            AddGeckoCap.this.choosePatient.setAdapter(localArrayAdapter);
            AddGeckoCap.this.maintain.setSelected(true);
            AddGeckoCap.this.rescue.setSelected(false);
            AddGeckoCap.this.numEditText.setText("");
            AddGeckoCap.this.verifyEditText.setText("");
            AddGeckoCap.this.prescEditText.setText("");
            AddGeckoCap.this.usageEditText.setText("");
          }
        });
        this.capsLabel = ((TableRow)findViewById(2131296269));
        ((TextView)findViewById(2131296268)).setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            Intent localIntent = new Intent(AddGeckoCap.this.getApplicationContext(), Confirm.class);
            AddGeckoCap.this.startActivity(localIntent);
          }
        });
        return;
      }
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      this.names.add((String)localEntry.getKey());
      geckoCaps.put((String)localEntry.getKey(), new ArrayList());
    }
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.geckocap.login.AddGeckoCap
 * JD-Core Version:    0.7.0.1
 */