package com.parse.signpost;

import com.parse.signpost.exception.OAuthCommunicationException;
import com.parse.signpost.exception.OAuthExpectationFailedException;
import com.parse.signpost.exception.OAuthMessageSignerException;
import com.parse.signpost.exception.OAuthNotAuthorizedException;
import com.parse.signpost.http.HttpParameters;
import java.io.Serializable;
import java.util.Map;

public abstract interface OAuthProvider
  extends Serializable
{
  public abstract String getAccessTokenEndpointUrl();
  
  public abstract String getAuthorizationWebsiteUrl();
  
  @Deprecated
  public abstract Map<String, String> getRequestHeaders();
  
  public abstract String getRequestTokenEndpointUrl();
  
  public abstract HttpParameters getResponseParameters();
  
  public abstract boolean isOAuth10a();
  
  public abstract void removeListener(OAuthProviderListener paramOAuthProviderListener);
  
  public abstract void retrieveAccessToken(OAuthConsumer paramOAuthConsumer, String paramString)
    throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException;
  
  public abstract String retrieveRequestToken(OAuthConsumer paramOAuthConsumer, String paramString)
    throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException;
  
  public abstract void setListener(OAuthProviderListener paramOAuthProviderListener);
  
  public abstract void setOAuth10a(boolean paramBoolean);
  
  @Deprecated
  public abstract void setRequestHeader(String paramString1, String paramString2);
  
  public abstract void setResponseParameters(HttpParameters paramHttpParameters);
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.signpost.OAuthProvider
 * JD-Core Version:    0.7.0.1
 */