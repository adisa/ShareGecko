package com.parse.twitter;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.webkit.CookieSyncManager;
import com.parse.internal.AsyncCallback;
import com.parse.oauth.OAuth1FlowDialog;
import com.parse.oauth.OAuth1FlowDialog.FlowResultHandler;
import com.parse.oauth.OAuth1FlowException;
import com.parse.signpost.OAuthConsumer;
import com.parse.signpost.OAuthProvider;
import com.parse.signpost.commonshttp.CommonsHttpOAuthConsumer;
import com.parse.signpost.commonshttp.CommonsHttpOAuthProvider;
import com.parse.signpost.http.HttpParameters;
import org.apache.http.client.methods.HttpUriRequest;

public class Twitter
{
  static final String ACCESS_TOKEN_URL = "https://api.twitter.com/oauth/access_token";
  static final String AUTHORIZE_URL = "https://api.twitter.com/oauth/authorize";
  private static final String CALLBACK_URL = "twitter-oauth://complete";
  private static final OAuthProvider PROVIDER = new CommonsHttpOAuthProvider("https://api.twitter.com/oauth/request_token", "https://api.twitter.com/oauth/access_token", "https://api.twitter.com/oauth/authorize");
  static final String REQUEST_TOKEN_URL = "https://api.twitter.com/oauth/request_token";
  private static final String SCREEN_NAME_PARAM = "screen_name";
  private static final String USER_ID_PARAM = "user_id";
  private static final String VERIFIER_PARAM = "oauth_verifier";
  private String authToken;
  private String authTokenSecret;
  private String consumerKey;
  private String consumerSecret;
  private String screenName;
  private String userId;
  
  public Twitter(String paramString1, String paramString2)
  {
    this.consumerKey = paramString1;
    this.consumerSecret = paramString2;
  }
  
  public void authorize(final Context paramContext, final AsyncCallback paramAsyncCallback)
  {
    if ((getConsumerKey() == null) || (getConsumerKey().length() == 0) || (getConsumerSecret() == null) || (getConsumerSecret().length() == 0)) {
      throw new IllegalStateException("Twitter must be initialized with a consumer key and secret before authorization.");
    }
    final CommonsHttpOAuthConsumer localCommonsHttpOAuthConsumer = new CommonsHttpOAuthConsumer(getConsumerKey(), getConsumerSecret());
    final ProgressDialog localProgressDialog = new ProgressDialog(paramContext);
    localProgressDialog.setMessage("Loading...");
    new AsyncTask()
    {
      private Throwable error;
      
      protected String doInBackground(Void... paramAnonymousVarArgs)
      {
        try
        {
          String str = Twitter.PROVIDER.retrieveRequestToken(localCommonsHttpOAuthConsumer, "twitter-oauth://complete");
          return str;
        }
        catch (Throwable localThrowable)
        {
          this.error = localThrowable;
        }
        return null;
      }
      
      protected void onPostExecute(String paramAnonymousString)
      {
        super.onPostExecute(paramAnonymousString);
        try
        {
          if (this.error != null)
          {
            paramAsyncCallback.onFailure(this.error);
            return;
          }
          CookieSyncManager.createInstance(paramContext);
          new OAuth1FlowDialog(paramContext, paramAnonymousString, "twitter-oauth://complete", "api.twitter", new OAuth1FlowDialog.FlowResultHandler()
          {
            public void onCancel()
            {
              Twitter.1.this.val$callback.onCancel();
            }
            
            public void onComplete(String paramAnonymous2String)
            {
              CookieSyncManager.getInstance().sync();
              final String str = Uri.parse(paramAnonymous2String).getQueryParameter("oauth_verifier");
              if (str == null)
              {
                Twitter.1.this.val$callback.onCancel();
                return;
              }
              new AsyncTask()
              {
                private Throwable error;
                
                protected HttpParameters doInBackground(Void... paramAnonymous3VarArgs)
                {
                  try
                  {
                    Twitter.PROVIDER.retrieveAccessToken(Twitter.1.this.val$consumer, str);
                    return Twitter.PROVIDER.getResponseParameters();
                  }
                  catch (Throwable localThrowable)
                  {
                    for (;;)
                    {
                      this.error = localThrowable;
                    }
                  }
                }
                
                /* Error */
                protected void onPostExecute(HttpParameters paramAnonymous3HttpParameters)
                {
                  // Byte code:
                  //   0: aload_0
                  //   1: aload_1
                  //   2: invokespecial 67	android/os/AsyncTask:onPostExecute	(Ljava/lang/Object;)V
                  //   5: aload_0
                  //   6: getfield 57	com/parse/twitter/Twitter$1$1$1:error	Ljava/lang/Throwable;
                  //   9: ifnull +36 -> 45
                  //   12: aload_0
                  //   13: getfield 20	com/parse/twitter/Twitter$1$1$1:this$2	Lcom/parse/twitter/Twitter$1$1;
                  //   16: getfield 39	com/parse/twitter/Twitter$1$1:this$1	Lcom/parse/twitter/Twitter$1;
                  //   19: getfield 71	com/parse/twitter/Twitter$1:val$callback	Lcom/parse/internal/AsyncCallback;
                  //   22: aload_0
                  //   23: getfield 57	com/parse/twitter/Twitter$1$1$1:error	Ljava/lang/Throwable;
                  //   26: invokeinterface 77 2 0
                  //   31: aload_0
                  //   32: getfield 20	com/parse/twitter/Twitter$1$1$1:this$2	Lcom/parse/twitter/Twitter$1$1;
                  //   35: getfield 39	com/parse/twitter/Twitter$1$1:this$1	Lcom/parse/twitter/Twitter$1;
                  //   38: getfield 81	com/parse/twitter/Twitter$1:val$progress	Landroid/app/ProgressDialog;
                  //   41: invokevirtual 86	android/app/ProgressDialog:dismiss	()V
                  //   44: return
                  //   45: aload_0
                  //   46: getfield 20	com/parse/twitter/Twitter$1$1$1:this$2	Lcom/parse/twitter/Twitter$1$1;
                  //   49: getfield 39	com/parse/twitter/Twitter$1$1:this$1	Lcom/parse/twitter/Twitter$1;
                  //   52: getfield 90	com/parse/twitter/Twitter$1:this$0	Lcom/parse/twitter/Twitter;
                  //   55: aload_0
                  //   56: getfield 20	com/parse/twitter/Twitter$1$1$1:this$2	Lcom/parse/twitter/Twitter$1$1;
                  //   59: getfield 39	com/parse/twitter/Twitter$1$1:this$1	Lcom/parse/twitter/Twitter$1;
                  //   62: getfield 45	com/parse/twitter/Twitter$1:val$consumer	Lcom/parse/signpost/OAuthConsumer;
                  //   65: invokeinterface 96 1 0
                  //   70: invokevirtual 99	com/parse/twitter/Twitter:setAuthToken	(Ljava/lang/String;)V
                  //   73: aload_0
                  //   74: getfield 20	com/parse/twitter/Twitter$1$1$1:this$2	Lcom/parse/twitter/Twitter$1$1;
                  //   77: getfield 39	com/parse/twitter/Twitter$1$1:this$1	Lcom/parse/twitter/Twitter$1;
                  //   80: getfield 90	com/parse/twitter/Twitter$1:this$0	Lcom/parse/twitter/Twitter;
                  //   83: aload_0
                  //   84: getfield 20	com/parse/twitter/Twitter$1$1$1:this$2	Lcom/parse/twitter/Twitter$1$1;
                  //   87: getfield 39	com/parse/twitter/Twitter$1$1:this$1	Lcom/parse/twitter/Twitter$1;
                  //   90: getfield 45	com/parse/twitter/Twitter$1:val$consumer	Lcom/parse/signpost/OAuthConsumer;
                  //   93: invokeinterface 102 1 0
                  //   98: invokevirtual 105	com/parse/twitter/Twitter:setAuthTokenSecret	(Ljava/lang/String;)V
                  //   101: aload_0
                  //   102: getfield 20	com/parse/twitter/Twitter$1$1$1:this$2	Lcom/parse/twitter/Twitter$1$1;
                  //   105: getfield 39	com/parse/twitter/Twitter$1$1:this$1	Lcom/parse/twitter/Twitter$1;
                  //   108: getfield 90	com/parse/twitter/Twitter$1:this$0	Lcom/parse/twitter/Twitter;
                  //   111: aload_1
                  //   112: ldc 107
                  //   114: invokevirtual 113	com/parse/signpost/http/HttpParameters:getFirst	(Ljava/lang/Object;)Ljava/lang/String;
                  //   117: invokevirtual 116	com/parse/twitter/Twitter:setScreenName	(Ljava/lang/String;)V
                  //   120: aload_0
                  //   121: getfield 20	com/parse/twitter/Twitter$1$1$1:this$2	Lcom/parse/twitter/Twitter$1$1;
                  //   124: getfield 39	com/parse/twitter/Twitter$1$1:this$1	Lcom/parse/twitter/Twitter$1;
                  //   127: getfield 90	com/parse/twitter/Twitter$1:this$0	Lcom/parse/twitter/Twitter;
                  //   130: aload_1
                  //   131: ldc 118
                  //   133: invokevirtual 113	com/parse/signpost/http/HttpParameters:getFirst	(Ljava/lang/Object;)Ljava/lang/String;
                  //   136: invokevirtual 121	com/parse/twitter/Twitter:setUserId	(Ljava/lang/String;)V
                  //   139: aload_0
                  //   140: getfield 20	com/parse/twitter/Twitter$1$1$1:this$2	Lcom/parse/twitter/Twitter$1$1;
                  //   143: getfield 39	com/parse/twitter/Twitter$1$1:this$1	Lcom/parse/twitter/Twitter$1;
                  //   146: getfield 71	com/parse/twitter/Twitter$1:val$callback	Lcom/parse/internal/AsyncCallback;
                  //   149: aload_0
                  //   150: getfield 20	com/parse/twitter/Twitter$1$1$1:this$2	Lcom/parse/twitter/Twitter$1$1;
                  //   153: getfield 39	com/parse/twitter/Twitter$1$1:this$1	Lcom/parse/twitter/Twitter$1;
                  //   156: getfield 90	com/parse/twitter/Twitter$1:this$0	Lcom/parse/twitter/Twitter;
                  //   159: invokeinterface 124 2 0
                  //   164: aload_0
                  //   165: getfield 20	com/parse/twitter/Twitter$1$1$1:this$2	Lcom/parse/twitter/Twitter$1$1;
                  //   168: getfield 39	com/parse/twitter/Twitter$1$1:this$1	Lcom/parse/twitter/Twitter$1;
                  //   171: getfield 81	com/parse/twitter/Twitter$1:val$progress	Landroid/app/ProgressDialog;
                  //   174: invokevirtual 86	android/app/ProgressDialog:dismiss	()V
                  //   177: return
                  //   178: astore_3
                  //   179: aload_0
                  //   180: getfield 20	com/parse/twitter/Twitter$1$1$1:this$2	Lcom/parse/twitter/Twitter$1$1;
                  //   183: getfield 39	com/parse/twitter/Twitter$1$1:this$1	Lcom/parse/twitter/Twitter$1;
                  //   186: getfield 71	com/parse/twitter/Twitter$1:val$callback	Lcom/parse/internal/AsyncCallback;
                  //   189: aload_3
                  //   190: invokeinterface 77 2 0
                  //   195: aload_0
                  //   196: getfield 20	com/parse/twitter/Twitter$1$1$1:this$2	Lcom/parse/twitter/Twitter$1$1;
                  //   199: getfield 39	com/parse/twitter/Twitter$1$1:this$1	Lcom/parse/twitter/Twitter$1;
                  //   202: getfield 81	com/parse/twitter/Twitter$1:val$progress	Landroid/app/ProgressDialog;
                  //   205: invokevirtual 86	android/app/ProgressDialog:dismiss	()V
                  //   208: return
                  //   209: astore_2
                  //   210: aload_0
                  //   211: getfield 20	com/parse/twitter/Twitter$1$1$1:this$2	Lcom/parse/twitter/Twitter$1$1;
                  //   214: getfield 39	com/parse/twitter/Twitter$1$1:this$1	Lcom/parse/twitter/Twitter$1;
                  //   217: getfield 81	com/parse/twitter/Twitter$1:val$progress	Landroid/app/ProgressDialog;
                  //   220: invokevirtual 86	android/app/ProgressDialog:dismiss	()V
                  //   223: aload_2
                  //   224: athrow
                  // Local variable table:
                  //   start	length	slot	name	signature
                  //   0	225	0	this	1
                  //   0	225	1	paramAnonymous3HttpParameters	HttpParameters
                  //   209	15	2	localObject	Object
                  //   178	12	3	localThrowable	Throwable
                  // Exception table:
                  //   from	to	target	type
                  //   45	139	178	java/lang/Throwable
                  //   5	31	209	finally
                  //   45	139	209	finally
                  //   139	164	209	finally
                  //   179	195	209	finally
                }
                
                protected void onPreExecute()
                {
                  super.onPreExecute();
                  Twitter.1.this.val$progress.show();
                }
              }.execute(new Void[0]);
            }
            
            public void onError(int paramAnonymous2Int, String paramAnonymous2String1, String paramAnonymous2String2)
            {
              Twitter.1.this.val$callback.onFailure(new OAuth1FlowException(paramAnonymous2Int, paramAnonymous2String1, paramAnonymous2String2));
            }
          }).show();
          return;
        }
        finally
        {
          localProgressDialog.dismiss();
        }
      }
      
      protected void onPreExecute()
      {
        super.onPreExecute();
        localProgressDialog.show();
      }
    }.execute(new Void[0]);
  }
  
  public String getAuthToken()
  {
    return this.authToken;
  }
  
  public String getAuthTokenSecret()
  {
    return this.authTokenSecret;
  }
  
  public String getConsumerKey()
  {
    return this.consumerKey;
  }
  
  public String getConsumerSecret()
  {
    return this.consumerSecret;
  }
  
  public String getScreenName()
  {
    return this.screenName;
  }
  
  public String getUserId()
  {
    return this.userId;
  }
  
  public void setAuthToken(String paramString)
  {
    this.authToken = paramString;
  }
  
  public void setAuthTokenSecret(String paramString)
  {
    this.authTokenSecret = paramString;
  }
  
  public void setConsumerKey(String paramString)
  {
    this.consumerKey = paramString;
  }
  
  public void setConsumerSecret(String paramString)
  {
    this.consumerSecret = paramString;
  }
  
  public void setScreenName(String paramString)
  {
    this.screenName = paramString;
  }
  
  public void setUserId(String paramString)
  {
    this.userId = paramString;
  }
  
  public void signRequest(HttpUriRequest paramHttpUriRequest)
  {
    CommonsHttpOAuthConsumer localCommonsHttpOAuthConsumer = new CommonsHttpOAuthConsumer(getConsumerKey(), getConsumerSecret());
    localCommonsHttpOAuthConsumer.setTokenWithSecret(getAuthToken(), getAuthTokenSecret());
    try
    {
      localCommonsHttpOAuthConsumer.sign(paramHttpUriRequest);
      return;
    }
    catch (Exception localException)
    {
      throw new RuntimeException(localException);
    }
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.twitter.Twitter
 * JD-Core Version:    0.7.0.1
 */