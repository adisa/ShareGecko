package com.parse.auth;

import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;

public class AnonymousAuthenticationProvider
  implements ParseAuthenticationProvider
{
  public void authenticate(ParseAuthenticationProvider.ParseAuthenticationCallback paramParseAuthenticationCallback)
  {
    try
    {
      paramParseAuthenticationCallback.onSuccess(getAuthData());
      return;
    }
    catch (JSONException localJSONException)
    {
      throw new RuntimeException(localJSONException);
    }
  }
  
  public void cancel() {}
  
  public void deauthenticate() {}
  
  public JSONObject getAuthData()
    throws JSONException
  {
    JSONObject localJSONObject = new JSONObject();
    localJSONObject.put("id", UUID.randomUUID());
    return localJSONObject;
  }
  
  public String getAuthType()
  {
    return "anonymous";
  }
  
  public boolean restoreAuthentication(JSONObject paramJSONObject)
  {
    return true;
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.auth.AnonymousAuthenticationProvider
 * JD-Core Version:    0.7.0.1
 */