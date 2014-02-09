package com.parse;

import android.content.Context;
import com.parse.auth.TwitterAuthenticationProvider;
import com.parse.twitter.Twitter;
import java.util.Set;
import org.json.JSONException;

public final class ParseTwitterUtils
{
  private static boolean isInitialized;
  private static TwitterAuthenticationProvider provider;
  private static Twitter twitter;
  
  private static void checkInitialization()
  {
    if (!isInitialized) {
      throw new IllegalStateException("You must call ParseTwitterUtils.initialize() before using ParseTwitterUtils");
    }
  }
  
  private static TwitterAuthenticationProvider getAuthenticationProvider()
  {
    if (provider == null) {
      provider = new TwitterAuthenticationProvider(getTwitter());
    }
    return provider;
  }
  
  public static Twitter getTwitter()
  {
    if (twitter == null) {
      twitter = new Twitter("", "");
    }
    return twitter;
  }
  
  public static void initialize(String paramString1, String paramString2)
  {
    getTwitter().setConsumerKey(paramString1);
    getTwitter().setConsumerSecret(paramString2);
    ParseUser.registerAuthenticationProvider(getAuthenticationProvider());
    isInitialized = true;
  }
  
  public static boolean isLinked(ParseUser paramParseUser)
  {
    return paramParseUser.getLinkedServiceNames().contains(getAuthenticationProvider().getAuthType());
  }
  
  public static void link(ParseUser paramParseUser, Context paramContext)
  {
    link(paramParseUser, paramContext, null);
  }
  
  public static void link(ParseUser paramParseUser, Context paramContext, SaveCallback paramSaveCallback)
  {
    checkInitialization();
    getAuthenticationProvider().setContext(paramContext);
    paramParseUser.linkWith(getAuthenticationProvider().getAuthType(), paramSaveCallback);
  }
  
  public static void link(ParseUser paramParseUser, String paramString1, String paramString2, String paramString3, String paramString4)
  {
    link(paramParseUser, paramString1, paramString2, paramString3, paramString4, null);
  }
  
  public static void link(ParseUser paramParseUser, String paramString1, String paramString2, String paramString3, String paramString4, SaveCallback paramSaveCallback)
  {
    
    try
    {
      paramParseUser.linkWith(getAuthenticationProvider().getAuthType(), getAuthenticationProvider().getAuthData(paramString1, paramString2, paramString3, paramString4), paramSaveCallback);
      return;
    }
    catch (JSONException localJSONException)
    {
      while (paramSaveCallback == null) {}
      paramSaveCallback.internalDone(null, new ParseException(localJSONException));
    }
  }
  
  public static void logIn(Context paramContext, LogInCallback paramLogInCallback)
  {
    checkInitialization();
    getAuthenticationProvider().setContext(paramContext);
    ParseUser.logInWith(getAuthenticationProvider().getAuthType(), paramLogInCallback);
  }
  
  public static void logIn(String paramString1, String paramString2, String paramString3, String paramString4, LogInCallback paramLogInCallback)
  {
    
    try
    {
      ParseUser.logInWith(getAuthenticationProvider().getAuthType(), getAuthenticationProvider().getAuthData(paramString1, paramString2, paramString3, paramString4), paramLogInCallback);
      return;
    }
    catch (JSONException localJSONException)
    {
      while (paramLogInCallback == null) {}
      paramLogInCallback.internalDone(null, new ParseException(localJSONException));
    }
  }
  
  public static void unlink(ParseUser paramParseUser)
    throws ParseException
  {
    checkInitialization();
    paramParseUser.unlinkFrom(getAuthenticationProvider().getAuthType());
  }
  
  public static void unlinkInBackground(ParseUser paramParseUser)
  {
    checkInitialization();
    paramParseUser.unlinkFromInBackground(getAuthenticationProvider().getAuthType());
  }
  
  public static void unlinkInBackground(ParseUser paramParseUser, SaveCallback paramSaveCallback)
  {
    checkInitialization();
    paramParseUser.unlinkFromInBackground(getAuthenticationProvider().getAuthType(), paramSaveCallback);
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.ParseTwitterUtils
 * JD-Core Version:    0.7.0.1
 */