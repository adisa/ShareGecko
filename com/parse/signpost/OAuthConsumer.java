package com.parse.signpost;

import com.parse.signpost.exception.OAuthCommunicationException;
import com.parse.signpost.exception.OAuthExpectationFailedException;
import com.parse.signpost.exception.OAuthMessageSignerException;
import com.parse.signpost.http.HttpParameters;
import com.parse.signpost.http.HttpRequest;
import com.parse.signpost.signature.OAuthMessageSigner;
import com.parse.signpost.signature.SigningStrategy;
import java.io.Serializable;

public abstract interface OAuthConsumer
  extends Serializable
{
  public abstract String getConsumerKey();
  
  public abstract String getConsumerSecret();
  
  public abstract HttpParameters getRequestParameters();
  
  public abstract String getToken();
  
  public abstract String getTokenSecret();
  
  public abstract void setAdditionalParameters(HttpParameters paramHttpParameters);
  
  public abstract void setMessageSigner(OAuthMessageSigner paramOAuthMessageSigner);
  
  public abstract void setSendEmptyTokens(boolean paramBoolean);
  
  public abstract void setSigningStrategy(SigningStrategy paramSigningStrategy);
  
  public abstract void setTokenWithSecret(String paramString1, String paramString2);
  
  public abstract HttpRequest sign(HttpRequest paramHttpRequest)
    throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException;
  
  public abstract HttpRequest sign(Object paramObject)
    throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException;
  
  public abstract String sign(String paramString)
    throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException;
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.signpost.OAuthConsumer
 * JD-Core Version:    0.7.0.1
 */