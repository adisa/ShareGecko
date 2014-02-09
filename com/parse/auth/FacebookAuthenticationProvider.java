package com.parse.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import com.parse.facebook.DialogError;
import com.parse.facebook.Facebook;
import com.parse.facebook.Facebook.DialogListener;
import com.parse.facebook.Facebook.ServiceListener;
import com.parse.facebook.FacebookError;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.SimpleTimeZone;
import org.json.JSONException;
import org.json.JSONObject;

public class FacebookAuthenticationProvider
  implements ParseAuthenticationProvider
{
  private static final String AUTH_TYPE_NAME = "facebook";
  public static final int DEFAULT_AUTH_ACTIVITY_CODE = 32665;
  private int activityCode;
  private WeakReference<Activity> baseActivity;
  private ParseAuthenticationProvider.ParseAuthenticationCallback currentOperationCallback;
  private final Facebook facebook;
  private Collection<String> permissions;
  private final DateFormat preciseDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
  private boolean useSingleSignOn;
  private String userId;
  
  public FacebookAuthenticationProvider(Facebook paramFacebook)
  {
    this.preciseDateFormat.setTimeZone(new SimpleTimeZone(0, "GMT"));
    this.activityCode = 32665;
    this.facebook = paramFacebook;
  }
  
  private void handleCancel()
  {
    if (this.currentOperationCallback == null) {
      return;
    }
    try
    {
      this.currentOperationCallback.onCancel();
      return;
    }
    finally
    {
      this.currentOperationCallback = null;
    }
  }
  
  private void handleError(Throwable paramThrowable)
  {
    if (this.currentOperationCallback == null) {
      return;
    }
    try
    {
      this.currentOperationCallback.onError(paramThrowable);
      return;
    }
    finally
    {
      this.currentOperationCallback = null;
    }
  }
  
  /* Error */
  private void handleSuccess(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 85	com/parse/auth/FacebookAuthenticationProvider:currentOperationCallback	Lcom/parse/auth/ParseAuthenticationProvider$ParseAuthenticationCallback;
    //   4: ifnonnull +4 -> 8
    //   7: return
    //   8: aload_0
    //   9: aload_1
    //   10: putfield 76	com/parse/auth/FacebookAuthenticationProvider:userId	Ljava/lang/String;
    //   13: aload_0
    //   14: aload_1
    //   15: aload_0
    //   16: getfield 59	com/parse/auth/FacebookAuthenticationProvider:facebook	Lcom/parse/facebook/Facebook;
    //   19: invokevirtual 101	com/parse/facebook/Facebook:getAccessToken	()Ljava/lang/String;
    //   22: new 103	java/util/Date
    //   25: dup
    //   26: aload_0
    //   27: getfield 59	com/parse/auth/FacebookAuthenticationProvider:facebook	Lcom/parse/facebook/Facebook;
    //   30: invokevirtual 107	com/parse/facebook/Facebook:getAccessExpires	()J
    //   33: invokespecial 110	java/util/Date:<init>	(J)V
    //   36: invokevirtual 114	com/parse/auth/FacebookAuthenticationProvider:getAuthData	(Ljava/lang/String;Ljava/lang/String;Ljava/util/Date;)Lorg/json/JSONObject;
    //   39: astore_3
    //   40: aload_0
    //   41: getfield 85	com/parse/auth/FacebookAuthenticationProvider:currentOperationCallback	Lcom/parse/auth/ParseAuthenticationProvider$ParseAuthenticationCallback;
    //   44: aload_3
    //   45: invokeinterface 118 2 0
    //   50: aload_0
    //   51: aconst_null
    //   52: putfield 85	com/parse/auth/FacebookAuthenticationProvider:currentOperationCallback	Lcom/parse/auth/ParseAuthenticationProvider$ParseAuthenticationCallback;
    //   55: return
    //   56: astore_2
    //   57: aload_0
    //   58: aload_2
    //   59: invokespecial 67	com/parse/auth/FacebookAuthenticationProvider:handleError	(Ljava/lang/Throwable;)V
    //   62: return
    //   63: astore 4
    //   65: aload_0
    //   66: aconst_null
    //   67: putfield 85	com/parse/auth/FacebookAuthenticationProvider:currentOperationCallback	Lcom/parse/auth/ParseAuthenticationProvider$ParseAuthenticationCallback;
    //   70: aload 4
    //   72: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	73	0	this	FacebookAuthenticationProvider
    //   0	73	1	paramString	String
    //   56	3	2	localJSONException	JSONException
    //   39	6	3	localJSONObject	JSONObject
    //   63	8	4	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   13	40	56	org/json/JSONException
    //   40	50	63	finally
  }
  
  public void authenticate(ParseAuthenticationProvider.ParseAuthenticationCallback paramParseAuthenticationCallback)
  {
    Activity localActivity;
    for (;;)
    {
      try
      {
        if (this.currentOperationCallback != null) {
          cancel();
        }
        this.currentOperationCallback = paramParseAuthenticationCallback;
        if (this.baseActivity == null)
        {
          localActivity = null;
          if (localActivity != null) {
            break;
          }
          throw new IllegalStateException("Activity must be non-null for Facebook authentication to proceed.");
        }
      }
      finally {}
      localActivity = (Activity)this.baseActivity.get();
    }
    int i;
    Object localObject2;
    if (this.useSingleSignOn)
    {
      i = this.activityCode;
      Collection localCollection = this.permissions;
      localObject2 = null;
      if (localCollection != null) {
        break label116;
      }
    }
    for (;;)
    {
      this.facebook.authorize(localActivity, (String[])localObject2, i, new Facebook.DialogListener()
      {
        public void onCancel()
        {
          FacebookAuthenticationProvider.this.handleCancel();
        }
        
        public void onComplete(Bundle paramAnonymousBundle)
        {
          if (FacebookAuthenticationProvider.this.currentOperationCallback == null) {
            return;
          }
          new FacebookAuthenticationProvider.GetUserIdBackgroundTask(FacebookAuthenticationProvider.this, null).execute(new Void[0]);
        }
        
        public void onError(DialogError paramAnonymousDialogError)
        {
          FacebookAuthenticationProvider.this.handleError(paramAnonymousDialogError);
        }
        
        public void onFacebookError(FacebookError paramAnonymousFacebookError)
        {
          FacebookAuthenticationProvider.this.handleError(paramAnonymousFacebookError);
        }
      });
      return;
      i = -1;
      break;
      label116:
      String[] arrayOfString = (String[])this.permissions.toArray(new String[this.permissions.size()]);
      localObject2 = arrayOfString;
    }
  }
  
  public void authenticateComplete(int paramInt1, int paramInt2, Intent paramIntent)
  {
    this.facebook.authorizeCallback(paramInt1, paramInt2, paramIntent);
  }
  
  public void cancel()
  {
    try
    {
      handleCancel();
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public void deauthenticate()
  {
    this.facebook.setAccessExpires(0L);
    this.facebook.setAccessToken(null);
  }
  
  public void extendAccessToken(Context paramContext, ParseAuthenticationProvider.ParseAuthenticationCallback paramParseAuthenticationCallback)
  {
    try
    {
      if (this.currentOperationCallback != null) {
        cancel();
      }
      this.currentOperationCallback = paramParseAuthenticationCallback;
      if (!this.facebook.extendAccessToken(paramContext, new Facebook.ServiceListener()
      {
        public void onComplete(Bundle paramAnonymousBundle)
        {
          FacebookAuthenticationProvider.this.handleSuccess(FacebookAuthenticationProvider.this.userId);
        }
        
        public void onError(Error paramAnonymousError)
        {
          FacebookAuthenticationProvider.this.handleError(paramAnonymousError);
        }
        
        public void onFacebookError(FacebookError paramAnonymousFacebookError)
        {
          FacebookAuthenticationProvider.this.handleError(paramAnonymousFacebookError);
        }
      })) {
        handleCancel();
      }
      return;
    }
    finally {}
  }
  
  public int getActivityCode()
  {
    return this.activityCode;
  }
  
  public JSONObject getAuthData(String paramString1, String paramString2, Date paramDate)
    throws JSONException
  {
    JSONObject localJSONObject = new JSONObject();
    localJSONObject.put("id", paramString1);
    localJSONObject.put("access_token", paramString2);
    localJSONObject.put("expiration_date", this.preciseDateFormat.format(paramDate));
    return localJSONObject;
  }
  
  public String getAuthType()
  {
    return "facebook";
  }
  
  public Facebook getFacebook()
  {
    return this.facebook;
  }
  
  public boolean restoreAuthentication(JSONObject paramJSONObject)
  {
    if (paramJSONObject == null)
    {
      this.facebook.setAccessExpires(1L);
      this.facebook.setAccessToken(null);
      return true;
    }
    try
    {
      this.facebook.setAccessToken(paramJSONObject.getString("access_token"));
      this.facebook.setAccessExpires(this.preciseDateFormat.parse(paramJSONObject.getString("expiration_date")).getTime());
      return true;
    }
    catch (Exception localException) {}
    return false;
  }
  
  public void setActivity(Activity paramActivity)
  {
    try
    {
      this.baseActivity = new WeakReference(paramActivity);
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public void setActivityCode(int paramInt)
  {
    try
    {
      this.activityCode = paramInt;
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public void setPermissions(Collection<String> paramCollection)
  {
    try
    {
      this.permissions = paramCollection;
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public void setSingleSignOn(boolean paramBoolean)
  {
    try
    {
      this.useSingleSignOn = paramBoolean;
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  private class GetUserIdBackgroundTask
    extends AsyncTask<Void, Void, Object>
  {
    private GetUserIdBackgroundTask() {}
    
    protected Object doInBackground(Void... paramVarArgs)
    {
      Bundle localBundle = new Bundle();
      localBundle.putString("fields", "id");
      try
      {
        String str = new JSONObject(FacebookAuthenticationProvider.this.facebook.request("me", localBundle)).getString("id");
        return str;
      }
      catch (Exception localException)
      {
        return localException;
      }
    }
    
    protected void onPostExecute(Object paramObject)
    {
      super.onPostExecute(paramObject);
      if ((paramObject instanceof Exception))
      {
        FacebookAuthenticationProvider.this.handleError((Exception)paramObject);
        return;
      }
      FacebookAuthenticationProvider.this.handleSuccess((String)paramObject);
    }
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.auth.FacebookAuthenticationProvider
 * JD-Core Version:    0.7.0.1
 */