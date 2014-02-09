package com.geckocap.login;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

public class GCMIntentService
  extends GCMBaseIntentService
{
  private static final String TAG = "GCMIntentService";
  
  public GCMIntentService()
  {
    super(new String[] { "1028761472500" });
  }
  
  private static void generateNotification(Context paramContext, String paramString)
  {
    long l = System.currentTimeMillis();
    NotificationManager localNotificationManager = (NotificationManager)paramContext.getSystemService("notification");
    Notification localNotification = new Notification(2130837539, paramString, l);
    localNotification.setLatestEventInfo(paramContext, paramContext.getString(2131034113), paramString, PendingIntent.getActivity(paramContext, 0, new Intent("android.intent.action.VIEW", Uri.parse("http://www.geckocap.com")), 0));
    localNotification.flags = (0x10 | localNotification.flags);
    localNotificationManager.notify(0, localNotification);
  }
  
  protected void onError(Context paramContext, String paramString)
  {
    Log.i("GCMIntentService", "Received error: " + paramString);
  }
  
  protected void onMessage(Context paramContext, Intent paramIntent)
  {
    Log.i("GCMIntentService", "Received message");
    generateNotification(paramContext, "GeckoCap notification");
    CommonUtilities.displayMessage(paramContext, "You've got a message!");
  }
  
  protected boolean onRecoverableError(Context paramContext, String paramString)
  {
    Log.i("GCMIntentService", "Received recoverable error: " + paramString);
    return super.onRecoverableError(paramContext, paramString);
  }
  
  protected void onRegistered(Context paramContext, String paramString)
  {
    Log.i("GCMIntentService", "Device registered: regId=  " + paramString);
    ServerUtilities.register(paramContext, paramString);
  }
  
  protected void onUnregistered(Context paramContext, String paramString)
  {
    Log.i("GCMIntentService", "Device unregistered");
    if (GCMRegistrar.isRegisteredOnServer(paramContext))
    {
      ServerUtilities.unregister(paramContext, paramString);
      return;
    }
    Log.i("GCMIntentService", "Ignoring unregister callback");
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.geckocap.login.GCMIntentService
 * JD-Core Version:    0.7.0.1
 */