package com.parse.signpost.signature;

import com.parse.signpost.OAuth;
import com.parse.signpost.http.HttpParameters;
import com.parse.signpost.http.HttpRequest;

public class QueryStringSigningStrategy
  implements SigningStrategy
{
  private static final long serialVersionUID = 1L;
  
  public String writeSignature(String paramString, HttpRequest paramHttpRequest, HttpParameters paramHttpParameters)
  {
    StringBuilder localStringBuilder = new StringBuilder(OAuth.addQueryParameters(paramHttpRequest.getRequestUrl(), new String[] { "oauth_signature", paramString }));
    if (paramHttpParameters.containsKey("oauth_token"))
    {
      localStringBuilder.append("&");
      localStringBuilder.append(paramHttpParameters.getAsQueryString("oauth_token"));
    }
    if (paramHttpParameters.containsKey("oauth_callback"))
    {
      localStringBuilder.append("&");
      localStringBuilder.append(paramHttpParameters.getAsQueryString("oauth_callback"));
    }
    if (paramHttpParameters.containsKey("oauth_verifier"))
    {
      localStringBuilder.append("&");
      localStringBuilder.append(paramHttpParameters.getAsQueryString("oauth_verifier"));
    }
    localStringBuilder.append("&");
    localStringBuilder.append(paramHttpParameters.getAsQueryString("oauth_consumer_key"));
    localStringBuilder.append("&");
    localStringBuilder.append(paramHttpParameters.getAsQueryString("oauth_version"));
    localStringBuilder.append("&");
    localStringBuilder.append(paramHttpParameters.getAsQueryString("oauth_signature_method"));
    localStringBuilder.append("&");
    localStringBuilder.append(paramHttpParameters.getAsQueryString("oauth_timestamp"));
    localStringBuilder.append("&");
    localStringBuilder.append(paramHttpParameters.getAsQueryString("oauth_nonce"));
    String str = localStringBuilder.toString();
    paramHttpRequest.setRequestUrl(str);
    return str;
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.signpost.signature.QueryStringSigningStrategy
 * JD-Core Version:    0.7.0.1
 */