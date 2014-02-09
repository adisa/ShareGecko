package com.geckocap.login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class GeckoLoginActivity
  extends Activity
{
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130903051);
    TextView localTextView1 = (TextView)findViewById(2131296310);
    Button localButton = (Button)findViewById(2131296311);
    TextView localTextView2 = (TextView)findViewById(2131296312);
    localTextView1.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        Intent localIntent = new Intent(GeckoLoginActivity.this.getApplicationContext(), EmailPassword.class);
        GeckoLoginActivity.this.startActivity(localIntent);
      }
    });
    localButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        Intent localIntent = new Intent(GeckoLoginActivity.this.getApplicationContext(), ParentDashboard.class);
        Display localDisplay = GeckoLoginActivity.this.getWindowManager().getDefaultDisplay();
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        localDisplay.getMetrics(localDisplayMetrics);
        float f1 = GeckoLoginActivity.this.getResources().getDisplayMetrics().density;
        float f2 = localDisplayMetrics.widthPixels;
        SharedPreferences localSharedPreferences = GeckoLoginActivity.this.getSharedPreferences("screen", 0);
        localSharedPreferences.edit().putFloat("width", f2).commit();
        localSharedPreferences.edit().putFloat("density", f1).commit();
        GeckoLoginActivity.this.startActivity(localIntent);
      }
    });
    localTextView2.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        Intent localIntent = new Intent(GeckoLoginActivity.this.getApplicationContext(), RegisterActivity.class);
        GeckoLoginActivity.this.startActivity(localIntent);
      }
    });
    SharedPreferences localSharedPreferences = getSharedPreferences("data", 0);
    localSharedPreferences.edit().putInt("parent_star_pts", 45).commit();
    localSharedPreferences.edit().putInt("patient_star_pts", 46).commit();
    localSharedPreferences.edit().putString("demo_patient_firstname", "Owen").commit();
    localSharedPreferences.edit().putString("demo_patient_lastname", "Chiu").commit();
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.geckocap.login.GeckoLoginActivity
 * JD-Core Version:    0.7.0.1
 */