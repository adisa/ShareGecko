package com.parse.signpost.signature;

import com.parse.signpost.OAuth;
import com.parse.signpost.exception.OAuthMessageSignerException;
import com.parse.signpost.http.HttpParameters;
import com.parse.signpost.http.HttpRequest;

public class PlainTextMessageSigner
  extends OAuthMessageSigner
{
  public String getSignatureMethod()
  {
    return "PLAINTEXT";
  }
  
  public String sign(HttpRequest paramHttpRequest, HttpParameters paramHttpParameters)
    throws OAuthMessageSignerException
  {
    return OAuth.percentEncode(getConsumerSecret()) + '&' + OAuth.percentEncode(getTokenSecret());
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.signpost.signature.PlainTextMessageSigner
 * JD-Core Version:    0.7.0.1
 */