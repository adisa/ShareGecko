package com.geckocap.login;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import com.google.android.gcm.GCMRegistrar;

public class NotificationSettings
  extends Activity
{
  TextView mDisplay;
  private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      String str = paramAnonymousIntent.getExtras().getString("message");
      NotificationSettings.this.mDisplay.append(str + "\n");
    }
  };
  AsyncTask<Void, Void, Void> mRegisterTask;
  
  private void checkNotNull(Object paramObject, String paramString)
  {
    if (paramObject == null) {
      throw new NullPointerException(paramString + " is null");
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    checkNotNull("http://192.168.1.14:8080/gcm-demo", "SERVER_URL");
    checkNotNull("1028761472500", "SENDER_ID");
    GCMRegistrar.checkDevice(this);
    GCMRegistrar.checkManifest(this);
    setContentView(2130903058);
    this.mDisplay = ((TextView)findViewById(2131296342));
    registerReceiver(this.mHandleMessageReceiver, new IntentFilter("com.geckocap.login.DISPLAY_MESSAGE"));
    final String str = GCMRegistrar.getRegistrationId(this);
    if (str.equals("")) {
      GCMRegistrar.register(this, new String[] { "1028761472500" });
    }
    while (GCMRegistrar.isRegisteredOnServer(this)) {
      return;
    }
    this.mRegisterTask = new AsyncTask()
    {
      protected Void doInBackground(Void... paramAnonymousVarArgs)
      {
        if (!ServerUtilities.register(jdField_this, str)) {
          GCMRegistrar.unregister(jdField_this);
        }
        return null;
      }
      
      protected void onPostExecute(Void paramAnonymousVoid)
      {
        NotificationSettings.this.mRegisterTask = null;
      }
    };
    this.mRegisterTask.execute(new Void[] { null, null, null });
  }
  
  protected void onDestroy()
  {
    if (this.mRegisterTask != null) {
      this.mRegisterTask.cancel(true);
    }
    unregisterReceiver(this.mHandleMessageReceiver);
    GCMRegistrar.unregister(this);
    GCMRegistrar.onDestroy(this);
    super.onDestroy();
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.geckocap.login.NotificationSettings
 * JD-Core Version:    0.7.0.1
 */