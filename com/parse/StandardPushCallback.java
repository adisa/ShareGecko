package com.parse;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import org.json.JSONObject;

public class StandardPushCallback
  extends PushCallback
{
  static boolean disableNotifications = false;
  static int totalNotifications = 0;
  
  private Bundle makeBundle()
  {
    Bundle localBundle = new Bundle();
    localBundle.putString("com.parse.Data", this.pushData.toString());
    localBundle.putString("com.parse.Channel", this.channel);
    return localBundle;
  }
  
  public void run()
  {
    totalNotifications = 1 + totalNotifications;
    if (disableNotifications) {}
    do
    {
      return;
      if (this.pushData == null) {
        this.pushData = new JSONObject();
      }
      if (!this.pushData.has("action")) {
        break;
      }
      Intent localIntent1 = new Intent();
      localIntent1.putExtras(makeBundle());
      localIntent1.setAction(this.pushData.optString("action"));
      this.service.getBaseContext().sendBroadcast(localIntent1);
    } while ((!this.pushData.has("alert")) && (!this.pushData.has("title")));
    String str1 = this.pushData.optString("alert", "Notification received.");
    String str2 = this.pushData.optString("title", this.localData.optString("appName"));
    long l = System.currentTimeMillis();
    ComponentName localComponentName = new ComponentName(this.localData.optString("activityPackage"), this.localData.optString("activityClass"));
    Intent localIntent2 = new Intent();
    localIntent2.setComponent(localComponentName);
    localIntent2.setFlags(268435456);
    localIntent2.putExtras(makeBundle());
    PendingIntent localPendingIntent = PendingIntent.getActivity(this.service, (int)l, localIntent2, 0);
    Notification localNotification = new Notification(this.localData.optInt("icon"), str1, l);
    localNotification.flags = (0x10 | localNotification.flags);
    localNotification.defaults = (0xFFFFFFFF | localNotification.defaults);
    localNotification.setLatestEventInfo(this.service, str2, str1, localPendingIntent);
    NotificationManager localNotificationManager = (NotificationManager)this.service.getSystemService("notification");
    int i = (int)l;
    try
    {
      localNotificationManager.notify(i, localNotification);
      return;
    }
    catch (SecurityException localSecurityException)
    {
      localNotification.defaults = 5;
      localNotificationManager.notify(i, localNotification);
    }
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.StandardPushCallback
 * JD-Core Version:    0.7.0.1
 */