package com.geckocap.login;

import android.content.Context;
import android.content.Intent;

public class CommonUtilities
{
  static final String DISPLAY_MESSAGE_ACTION = "com.geckocap.login.DISPLAY_MESSAGE";
  static final String EXTRA_MESSAGE = "message";
  static final String SENDER_ID = "1028761472500";
  static final String SERVER_URL = "http://192.168.1.14:8080/gcm-demo";
  static final String TAG = "GeckoCap";
  
  static void displayMessage(Context paramContext, String paramString)
  {
    Intent localIntent = new Intent("com.geckocap.login.DISPLAY_MESSAGE");
    localIntent.putExtra("message", paramString);
    paramContext.sendBroadcast(localIntent);
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.geckocap.login.CommonUtilities
 * JD-Core Version:    0.7.0.1
 */