package com.geckocap.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Start
  extends Activity
{
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130903053);
    Button localButton1 = (Button)findViewById(2131296313);
    Button localButton2 = (Button)findViewById(2131296314);
    localButton1.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        Intent localIntent = new Intent(Start.this.getApplicationContext(), GeckoLoginActivity.class);
        Start.this.startActivity(localIntent);
      }
    });
    localButton2.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        Intent localIntent = new Intent(Start.this.getApplicationContext(), RegisterActivity.class);
        Start.this.startActivity(localIntent);
      }
    });
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.geckocap.login.Start
 * JD-Core Version:    0.7.0.1
 */