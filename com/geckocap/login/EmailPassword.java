package com.geckocap.login;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EmailPassword
  extends Activity
{
  private EditText acctEmailEditText;
  View.OnClickListener emailButtonClicked = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      String str = EmailPassword.this.acctEmailEditText.getText().toString();
      EmailPassword.this.acctEmailEditText.setText("");
      Toast.makeText(EmailPassword.this, "Password reset instructions sent to " + str, 1).show();
      EmailPassword.this.finish();
    }
  };
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130903049);
    this.acctEmailEditText = ((EditText)findViewById(2131296308));
    ((Button)findViewById(2131296309)).setOnClickListener(this.emailButtonClicked);
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.geckocap.login.EmailPassword
 * JD-Core Version:    0.7.0.1
 */