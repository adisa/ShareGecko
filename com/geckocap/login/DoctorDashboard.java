package com.geckocap.login;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DoctorDashboard
  extends Activity
{
  final Context context = this;
  View.OnClickListener reportPopup = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      final Dialog localDialog = new Dialog(DoctorDashboard.this.context);
      localDialog.requestWindowFeature(1);
      localDialog.setContentView(2130903071);
      View.OnClickListener local1 = new View.OnClickListener()
      {
        public void onClick(View paramAnonymous2View)
        {
          localDialog.dismiss();
        }
      };
      Button localButton1 = (Button)localDialog.findViewById(2131296402);
      Button localButton2 = (Button)localDialog.findViewById(2131296403);
      Button localButton3 = (Button)localDialog.findViewById(2131296404);
      Button localButton4 = (Button)localDialog.findViewById(2131296283);
      localButton1.setOnClickListener(local1);
      localButton2.setOnClickListener(local1);
      localButton3.setOnClickListener(local1);
      localButton4.setOnClickListener(local1);
      localDialog.show();
    }
  };
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130903048);
    TextView localTextView1 = (TextView)findViewById(2131296411);
    TextView localTextView2 = (TextView)findViewById(2131296410);
    ((TextView)findViewById(2131296412)).setBackgroundResource(2130837561);
    localTextView1.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        Intent localIntent = new Intent(DoctorDashboard.this.getApplicationContext(), ParentDashboard.class);
        localIntent.setFlags(131072);
        DoctorDashboard.this.startActivity(localIntent);
      }
    });
    localTextView2.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        Intent localIntent = new Intent(DoctorDashboard.this.getApplicationContext(), PatientDashboard.class);
        localIntent.setFlags(131072);
        DoctorDashboard.this.startActivity(localIntent);
      }
    });
    Button localButton1 = (Button)findViewById(2131296303);
    Button localButton2 = (Button)findViewById(2131296304);
    Button localButton3 = (Button)findViewById(2131296305);
    localButton1.setOnClickListener(this.reportPopup);
    localButton2.setOnClickListener(this.reportPopup);
    localButton3.setOnClickListener(this.reportPopup);
    SharedPreferences localSharedPreferences = getSharedPreferences("data", 0);
    ((TextView)findViewById(2131296306)).setText("Your last report for " + localSharedPreferences.getString("demo_patient_firstname", "") + " " + localSharedPreferences.getString("demo_patient_lasttname", "") + "was generated and emailed on September 1, 2012");
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.geckocap.login.DoctorDashboard
 * JD-Core Version:    0.7.0.1
 */