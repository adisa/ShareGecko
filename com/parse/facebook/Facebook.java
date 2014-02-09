package com.parse.facebook;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;
import android.webkit.CookieSyncManager;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

public class Facebook
{
  public static final String CANCEL_URI = "fbconnect://cancel";
  private static final int DEFAULT_AUTH_ACTIVITY_CODE = 32665;
  protected static String DIALOG_BASE_URL = "https://m.facebook.com/dialog/";
  public static final String EXPIRES = "expires_in";
  public static final String FB_APP_SIGNATURE = "30820268308201d102044a9c4610300d06092a864886f70d0101040500307a310b3009060355040613025553310b3009060355040813024341311230100603550407130950616c6f20416c746f31183016060355040a130f46616365626f6f6b204d6f62696c653111300f060355040b130846616365626f6f6b311d301b0603550403131446616365626f6f6b20436f72706f726174696f6e3020170d3039303833313231353231365a180f32303530303932353231353231365a307a310b3009060355040613025553310b3009060355040813024341311230100603550407130950616c6f20416c746f31183016060355040a130f46616365626f6f6b204d6f62696c653111300f060355040b130846616365626f6f6b311d301b0603550403131446616365626f6f6b20436f72706f726174696f6e30819f300d06092a864886f70d010101050003818d0030818902818100c207d51df8eb8c97d93ba0c8c1002c928fab00dc1b42fca5e66e99cc3023ed2d214d822bc59e8e35ddcf5f44c7ae8ade50d7e0c434f500e6c131f4a2834f987fc46406115de2018ebbb0d5a3c261bd97581ccfef76afc7135a6d59e8855ecd7eacc8f8737e794c60a761c536b72b11fac8e603f5da1a2d54aa103b8a13c0dbc10203010001300d06092a864886f70d0101040500038181005ee9be8bcbb250648d3b741290a82a1c9dc2e76a0af2f2228f1d9f9c4007529c446a70175c5a900d5141812866db46be6559e2141616483998211f4a673149fb2232a10d247663b26a9031e15f84bc1c74d141ff98a02d76f85b2c8ab2571b6469b232d8e768a7f7ca04f7abe4a775615916c07940656b58717457b42bd928a2";
  public static final int FORCE_DIALOG_AUTH = -1;
  protected static String GRAPH_BASE_URL = "https://graph.facebook.com/";
  private static final String LOGIN = "oauth";
  public static final String REDIRECT_URI = "fbconnect://success";
  protected static String RESTSERVER_URL = "https://api.facebook.com/restserver.php";
  public static final String SINGLE_SIGN_ON_DISABLED = "service_disabled";
  public static final String TOKEN = "access_token";
  private final long REFRESH_TOKEN_BARRIER = 86400000L;
  private long mAccessExpires = 0L;
  private String mAccessToken = null;
  private String mAppId;
  private Activity mAuthActivity;
  private int mAuthActivityCode;
  private DialogListener mAuthDialogListener;
  private String[] mAuthPermissions;
  private long mLastAccessUpdate = 0L;
  
  public Facebook(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("You must specify your application ID when instantiating a Facebook object. See README for details.");
    }
    this.mAppId = paramString;
  }
  
  private void startDialogAuth(Activity paramActivity, String[] paramArrayOfString)
  {
    Bundle localBundle = new Bundle();
    if (paramArrayOfString.length > 0) {
      localBundle.putString("scope", TextUtils.join(",", paramArrayOfString));
    }
    CookieSyncManager.createInstance(paramActivity);
    dialog(paramActivity, "oauth", localBundle, new DialogListener()
    {
      public void onCancel()
      {
        Util.logd("Facebook-authorize", "Login canceled");
        Facebook.this.mAuthDialogListener.onCancel();
      }
      
      public void onComplete(Bundle paramAnonymousBundle)
      {
        CookieSyncManager.getInstance().sync();
        Facebook.this.setAccessToken(paramAnonymousBundle.getString("access_token"));
        Facebook.this.setAccessExpiresIn(paramAnonymousBundle.getString("expires_in"));
        if (Facebook.this.isSessionValid())
        {
          Util.logd("Facebook-authorize", "Login Success! access_token=" + Facebook.this.getAccessToken() + " expires=" + Facebook.this.getAccessExpires());
          Facebook.this.mAuthDialogListener.onComplete(paramAnonymousBundle);
          return;
        }
        Facebook.this.mAuthDialogListener.onFacebookError(new FacebookError("Failed to receive access token."));
      }
      
      public void onError(DialogError paramAnonymousDialogError)
      {
        Util.logd("Facebook-authorize", "Login failed: " + paramAnonymousDialogError);
        Facebook.this.mAuthDialogListener.onError(paramAnonymousDialogError);
      }
      
      public void onFacebookError(FacebookError paramAnonymousFacebookError)
      {
        Util.logd("Facebook-authorize", "Login failed: " + paramAnonymousFacebookError);
        Facebook.this.mAuthDialogListener.onFacebookError(paramAnonymousFacebookError);
      }
    });
  }
  
  private boolean startSingleSignOn(Activity paramActivity, String paramString, String[] paramArrayOfString, int paramInt)
  {
    boolean bool = true;
    Intent localIntent = new Intent();
    localIntent.setClassName("com.facebook.katana", "com.facebook.katana.ProxyAuth");
    localIntent.putExtra("client_id", paramString);
    if (paramArrayOfString.length > 0) {
      localIntent.putExtra("scope", TextUtils.join(",", paramArrayOfString));
    }
    if (!validateActivityIntent(paramActivity, localIntent)) {
      return false;
    }
    this.mAuthActivity = paramActivity;
    this.mAuthPermissions = paramArrayOfString;
    this.mAuthActivityCode = paramInt;
    try
    {
      paramActivity.startActivityForResult(localIntent, paramInt);
      return bool;
    }
    catch (ActivityNotFoundException localActivityNotFoundException)
    {
      for (;;)
      {
        bool = false;
      }
    }
  }
  
  private boolean validateActivityIntent(Context paramContext, Intent paramIntent)
  {
    ResolveInfo localResolveInfo = paramContext.getPackageManager().resolveActivity(paramIntent, 0);
    if (localResolveInfo == null) {
      return false;
    }
    return validateAppSignatureForPackage(paramContext, localResolveInfo.activityInfo.packageName);
  }
  
  private boolean validateAppSignatureForPackage(Context paramContext, String paramString)
  {
    for (;;)
    {
      int j;
      try
      {
        PackageInfo localPackageInfo = paramContext.getPackageManager().getPackageInfo(paramString, 64);
        Signature[] arrayOfSignature = localPackageInfo.signatures;
        int i = arrayOfSignature.length;
        j = 0;
        boolean bool = false;
        if (j < i)
        {
          if (arrayOfSignature[j].toCharsString().equals("30820268308201d102044a9c4610300d06092a864886f70d0101040500307a310b3009060355040613025553310b3009060355040813024341311230100603550407130950616c6f20416c746f31183016060355040a130f46616365626f6f6b204d6f62696c653111300f060355040b130846616365626f6f6b311d301b0603550403131446616365626f6f6b20436f72706f726174696f6e3020170d3039303833313231353231365a180f32303530303932353231353231365a307a310b3009060355040613025553310b3009060355040813024341311230100603550407130950616c6f20416c746f31183016060355040a130f46616365626f6f6b204d6f62696c653111300f060355040b130846616365626f6f6b311d301b0603550403131446616365626f6f6b20436f72706f726174696f6e30819f300d06092a864886f70d010101050003818d0030818902818100c207d51df8eb8c97d93ba0c8c1002c928fab00dc1b42fca5e66e99cc3023ed2d214d822bc59e8e35ddcf5f44c7ae8ade50d7e0c434f500e6c131f4a2834f987fc46406115de2018ebbb0d5a3c261bd97581ccfef76afc7135a6d59e8855ecd7eacc8f8737e794c60a761c536b72b11fac8e603f5da1a2d54aa103b8a13c0dbc10203010001300d06092a864886f70d0101040500038181005ee9be8bcbb250648d3b741290a82a1c9dc2e76a0af2f2228f1d9f9c4007529c446a70175c5a900d5141812866db46be6559e2141616483998211f4a673149fb2232a10d247663b26a9031e15f84bc1c74d141ff98a02d76f85b2c8ab2571b6469b232d8e768a7f7ca04f7abe4a775615916c07940656b58717457b42bd928a2")) {
            bool = true;
          }
        }
        else {
          return bool;
        }
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        return false;
      }
      j++;
    }
  }
  
  private boolean validateServiceIntent(Context paramContext, Intent paramIntent)
  {
    ResolveInfo localResolveInfo = paramContext.getPackageManager().resolveService(paramIntent, 0);
    if (localResolveInfo == null) {
      return false;
    }
    return validateAppSignatureForPackage(paramContext, localResolveInfo.serviceInfo.packageName);
  }
  
  public void authorize(Activity paramActivity, DialogListener paramDialogListener)
  {
    authorize(paramActivity, new String[0], 32665, paramDialogListener);
  }
  
  public void authorize(Activity paramActivity, String[] paramArrayOfString, int paramInt, DialogListener paramDialogListener)
  {
    this.mAuthDialogListener = paramDialogListener;
    boolean bool = false;
    if (paramInt >= 0) {
      bool = startSingleSignOn(paramActivity, this.mAppId, paramArrayOfString, paramInt);
    }
    if (!bool) {
      startDialogAuth(paramActivity, paramArrayOfString);
    }
  }
  
  public void authorize(Activity paramActivity, String[] paramArrayOfString, DialogListener paramDialogListener)
  {
    authorize(paramActivity, paramArrayOfString, 32665, paramDialogListener);
  }
  
  public void authorizeCallback(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if (paramInt1 == this.mAuthActivityCode)
    {
      if (paramInt2 != -1) {
        break label307;
      }
      str1 = paramIntent.getStringExtra("error");
      if (str1 == null) {
        str1 = paramIntent.getStringExtra("error_type");
      }
      if (str1 == null) {
        break label205;
      }
      if ((!str1.equals("service_disabled")) && (!str1.equals("AndroidAuthKillSwitchException"))) {
        break label79;
      }
      Util.logd("Facebook-authorize", "Hosted auth currently disabled. Retrying dialog auth...");
      startDialogAuth(this.mAuthActivity, this.mAuthPermissions);
    }
    label79:
    while (paramInt2 != 0)
    {
      String str1;
      return;
      if ((str1.equals("access_denied")) || (str1.equals("OAuthAccessDeniedException")))
      {
        Util.logd("Facebook-authorize", "Login canceled by user.");
        this.mAuthDialogListener.onCancel();
        return;
      }
      String str2 = paramIntent.getStringExtra("error_description");
      if (str2 != null) {
        str1 = str1 + ":" + str2;
      }
      Util.logd("Facebook-authorize", "Login failed: " + str1);
      this.mAuthDialogListener.onFacebookError(new FacebookError(str1));
      return;
      setAccessToken(paramIntent.getStringExtra("access_token"));
      setAccessExpiresIn(paramIntent.getStringExtra("expires_in"));
      if (isSessionValid())
      {
        Util.logd("Facebook-authorize", "Login Success! access_token=" + getAccessToken() + " expires=" + getAccessExpires());
        this.mAuthDialogListener.onComplete(paramIntent.getExtras());
        return;
      }
      this.mAuthDialogListener.onFacebookError(new FacebookError("Failed to receive access token."));
      return;
    }
    label205:
    if (paramIntent != null)
    {
      Util.logd("Facebook-authorize", "Login failed: " + paramIntent.getStringExtra("error"));
      this.mAuthDialogListener.onError(new DialogError(paramIntent.getStringExtra("error"), paramIntent.getIntExtra("error_code", -1), paramIntent.getStringExtra("failing_url")));
      return;
    }
    label307:
    Util.logd("Facebook-authorize", "Login canceled by user.");
    this.mAuthDialogListener.onCancel();
  }
  
  public void dialog(Context paramContext, String paramString, Bundle paramBundle, DialogListener paramDialogListener)
  {
    if (paramDialogListener == null) {
      paramDialogListener = new DialogListener()
      {
        public void onCancel() {}
        
        public void onComplete(Bundle paramAnonymousBundle) {}
        
        public void onError(DialogError paramAnonymousDialogError) {}
        
        public void onFacebookError(FacebookError paramAnonymousFacebookError) {}
      };
    }
    String str1 = DIALOG_BASE_URL + paramString;
    paramBundle.putString("display", "touch");
    paramBundle.putString("redirect_uri", "fbconnect://success");
    if (paramString.equals("oauth"))
    {
      paramBundle.putString("type", "user_agent");
      paramBundle.putString("client_id", this.mAppId);
    }
    String str2;
    for (;;)
    {
      if (isSessionValid()) {
        paramBundle.putString("access_token", getAccessToken());
      }
      str2 = str1 + "?" + Util.encodeUrl(paramBundle);
      if (paramContext.checkCallingOrSelfPermission("android.permission.INTERNET") == 0) {
        break;
      }
      Util.showAlert(paramContext, "Error", "Application requires permission to access the Internet");
      return;
      paramBundle.putString("app_id", this.mAppId);
    }
    new FbDialog(paramContext, str2, paramDialogListener).show();
  }
  
  public void dialog(Context paramContext, String paramString, DialogListener paramDialogListener)
  {
    dialog(paramContext, paramString, new Bundle(), paramDialogListener);
  }
  
  public boolean extendAccessToken(Context paramContext, ServiceListener paramServiceListener)
  {
    Intent localIntent = new Intent();
    localIntent.setClassName("com.facebook.katana", "com.facebook.katana.platform.TokenRefreshService");
    if (!validateServiceIntent(paramContext, localIntent)) {
      return false;
    }
    return paramContext.bindService(localIntent, new TokenRefreshServiceConnection(paramContext, paramServiceListener), 1);
  }
  
  public boolean extendAccessTokenIfNeeded(Context paramContext, ServiceListener paramServiceListener)
  {
    if (shouldExtendAccessToken()) {
      return extendAccessToken(paramContext, paramServiceListener);
    }
    return true;
  }
  
  public long getAccessExpires()
  {
    return this.mAccessExpires;
  }
  
  public String getAccessToken()
  {
    return this.mAccessToken;
  }
  
  public String getAppId()
  {
    return this.mAppId;
  }
  
  public boolean isSessionValid()
  {
    return (getAccessToken() != null) && ((getAccessExpires() == 0L) || (System.currentTimeMillis() < getAccessExpires()));
  }
  
  public String logout(Context paramContext)
    throws MalformedURLException, IOException
  {
    Util.clearCookies(paramContext);
    Bundle localBundle = new Bundle();
    localBundle.putString("method", "auth.expireSession");
    String str = request(localBundle);
    setAccessToken(null);
    setAccessExpires(0L);
    return str;
  }
  
  public String request(Bundle paramBundle)
    throws MalformedURLException, IOException
  {
    if (!paramBundle.containsKey("method")) {
      throw new IllegalArgumentException("API method must be specified. (parameters must contain key \"method\" and value). See http://developers.facebook.com/docs/reference/rest/");
    }
    return request(null, paramBundle, "GET");
  }
  
  public String request(String paramString)
    throws MalformedURLException, IOException
  {
    return request(paramString, new Bundle(), "GET");
  }
  
  public String request(String paramString, Bundle paramBundle)
    throws MalformedURLException, IOException
  {
    return request(paramString, paramBundle, "GET");
  }
  
  public String request(String paramString1, Bundle paramBundle, String paramString2)
    throws FileNotFoundException, MalformedURLException, IOException
  {
    paramBundle.putString("format", "json");
    if (isSessionValid()) {
      paramBundle.putString("access_token", getAccessToken());
    }
    if (paramString1 != null) {}
    for (String str = GRAPH_BASE_URL + paramString1;; str = RESTSERVER_URL) {
      return Util.openUrl(str, paramString2, paramBundle);
    }
  }
  
  public void setAccessExpires(long paramLong)
  {
    this.mAccessExpires = paramLong;
  }
  
  public void setAccessExpiresIn(String paramString)
  {
    if (paramString != null) {
      if (!paramString.equals("0")) {
        break label22;
      }
    }
    label22:
    for (long l = 0L;; l = System.currentTimeMillis() + 1000L * Long.parseLong(paramString))
    {
      setAccessExpires(l);
      return;
    }
  }
  
  public void setAccessToken(String paramString)
  {
    this.mAccessToken = paramString;
    this.mLastAccessUpdate = System.currentTimeMillis();
  }
  
  public void setAppId(String paramString)
  {
    this.mAppId = paramString;
  }
  
  public boolean shouldExtendAccessToken()
  {
    return (isSessionValid()) && (System.currentTimeMillis() - this.mLastAccessUpdate >= 86400000L);
  }
  
  public static abstract interface DialogListener
  {
    public abstract void onCancel();
    
    public abstract void onComplete(Bundle paramBundle);
    
    public abstract void onError(DialogError paramDialogError);
    
    public abstract void onFacebookError(FacebookError paramFacebookError);
  }
  
  public static abstract interface ServiceListener
  {
    public abstract void onComplete(Bundle paramBundle);
    
    public abstract void onError(Error paramError);
    
    public abstract void onFacebookError(FacebookError paramFacebookError);
  }
  
  private class TokenRefreshServiceConnection
    implements ServiceConnection
  {
    final Context applicationsContext;
    final Messenger messageReceiver = new Messenger(new Handler()
    {
      public void handleMessage(Message paramAnonymousMessage)
      {
        String str1 = paramAnonymousMessage.getData().getString("access_token");
        long l = 1000L * paramAnonymousMessage.getData().getLong("expires_in");
        Bundle localBundle = (Bundle)paramAnonymousMessage.getData().clone();
        localBundle.putLong("expires_in", l);
        if (str1 != null)
        {
          Facebook.this.setAccessToken(str1);
          Facebook.this.setAccessExpires(l);
          if (Facebook.TokenRefreshServiceConnection.this.serviceListener != null) {
            Facebook.TokenRefreshServiceConnection.this.serviceListener.onComplete(localBundle);
          }
        }
        String str2;
        for (;;)
        {
          Facebook.TokenRefreshServiceConnection.this.applicationsContext.unbindService(Facebook.TokenRefreshServiceConnection.this);
          return;
          if (Facebook.TokenRefreshServiceConnection.this.serviceListener != null)
          {
            str2 = paramAnonymousMessage.getData().getString("error");
            if (!paramAnonymousMessage.getData().containsKey("error_code")) {
              break;
            }
            int i = paramAnonymousMessage.getData().getInt("error_code");
            Facebook.TokenRefreshServiceConnection.this.serviceListener.onFacebookError(new FacebookError(str2, null, i));
          }
        }
        Facebook.ServiceListener localServiceListener = Facebook.TokenRefreshServiceConnection.this.serviceListener;
        if (str2 != null) {}
        for (;;)
        {
          localServiceListener.onError(new Error(str2));
          break;
          str2 = "Unknown service error";
        }
      }
    });
    Messenger messageSender = null;
    final Facebook.ServiceListener serviceListener;
    
    public TokenRefreshServiceConnection(Context paramContext, Facebook.ServiceListener paramServiceListener)
    {
      this.applicationsContext = paramContext;
      this.serviceListener = paramServiceListener;
    }
    
    private void refreshToken()
    {
      Bundle localBundle = new Bundle();
      localBundle.putString("access_token", Facebook.this.mAccessToken);
      Message localMessage = Message.obtain();
      localMessage.setData(localBundle);
      localMessage.replyTo = this.messageReceiver;
      try
      {
        this.messageSender.send(localMessage);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        this.serviceListener.onError(new Error("Service connection error"));
      }
    }
    
    public void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
    {
      this.messageSender = new Messenger(paramIBinder);
      refreshToken();
    }
    
    public void onServiceDisconnected(ComponentName paramComponentName)
    {
      this.serviceListener.onError(new Error("Service disconnected"));
      this.applicationsContext.unbindService(this);
    }
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.facebook.Facebook
 * JD-Core Version:    0.7.0.1
 */