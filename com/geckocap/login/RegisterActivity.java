package com.geckocap.login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class RegisterActivity
  extends Activity
{
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130903069);
    ((Button)findViewById(2131296398)).setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        Intent localIntent = new Intent(RegisterActivity.this.getApplicationContext(), AddEditPatient.class);
        RegisterActivity.this.startActivity(localIntent);
      }
    });
    ((TextView)findViewById(2131296399)).setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        Intent localIntent = new Intent(RegisterActivity.this.getApplicationContext(), GeckoLoginActivity.class);
        RegisterActivity.this.startActivity(localIntent);
      }
    });
    getSharedPreferences("Patients", 0).edit().clear().commit();
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.geckocap.login.RegisterActivity
 * JD-Core Version:    0.7.0.1
 */