package com.parse.signpost;

import com.parse.signpost.exception.OAuthCommunicationException;
import com.parse.signpost.exception.OAuthExpectationFailedException;
import com.parse.signpost.exception.OAuthMessageSignerException;
import com.parse.signpost.exception.OAuthNotAuthorizedException;
import com.parse.signpost.http.HttpParameters;
import com.parse.signpost.http.HttpRequest;
import com.parse.signpost.http.HttpResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractOAuthProvider
  implements OAuthProvider
{
  private static final long serialVersionUID = 1L;
  private String accessTokenEndpointUrl;
  private String authorizationWebsiteUrl;
  private Map<String, String> defaultHeaders;
  private boolean isOAuth10a;
  private transient OAuthProviderListener listener;
  private String requestTokenEndpointUrl;
  private HttpParameters responseParameters;
  
  public AbstractOAuthProvider(String paramString1, String paramString2, String paramString3)
  {
    this.requestTokenEndpointUrl = paramString1;
    this.accessTokenEndpointUrl = paramString2;
    this.authorizationWebsiteUrl = paramString3;
    this.responseParameters = new HttpParameters();
    this.defaultHeaders = new HashMap();
  }
  
  protected void closeConnection(HttpRequest paramHttpRequest, HttpResponse paramHttpResponse)
    throws Exception
  {}
  
  protected abstract HttpRequest createRequest(String paramString)
    throws Exception;
  
  public String getAccessTokenEndpointUrl()
  {
    return this.accessTokenEndpointUrl;
  }
  
  public String getAuthorizationWebsiteUrl()
  {
    return this.authorizationWebsiteUrl;
  }
  
  public Map<String, String> getRequestHeaders()
  {
    return this.defaultHeaders;
  }
  
  public String getRequestTokenEndpointUrl()
  {
    return this.requestTokenEndpointUrl;
  }
  
  protected String getResponseParameter(String paramString)
  {
    return this.responseParameters.getFirst(paramString);
  }
  
  public HttpParameters getResponseParameters()
  {
    return this.responseParameters;
  }
  
  protected void handleUnexpectedResponse(int paramInt, HttpResponse paramHttpResponse)
    throws Exception
  {
    if (paramHttpResponse == null) {
      return;
    }
    BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(paramHttpResponse.getContent()), 8192);
    StringBuilder localStringBuilder = new StringBuilder();
    for (String str = localBufferedReader.readLine(); str != null; str = localBufferedReader.readLine()) {
      localStringBuilder.append(str);
    }
    switch (paramInt)
    {
    default: 
      throw new OAuthCommunicationException("Service provider responded in error: " + paramInt + " (" + paramHttpResponse.getReasonPhrase() + ")", localStringBuilder.toString());
    }
    throw new OAuthNotAuthorizedException(localStringBuilder.toString());
  }
  
  public boolean isOAuth10a()
  {
    return this.isOAuth10a;
  }
  
  public void removeListener(OAuthProviderListener paramOAuthProviderListener)
  {
    this.listener = null;
  }
  
  public void retrieveAccessToken(OAuthConsumer paramOAuthConsumer, String paramString)
    throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException
  {
    if ((paramOAuthConsumer.getToken() == null) || (paramOAuthConsumer.getTokenSecret() == null)) {
      throw new OAuthExpectationFailedException("Authorized request token or token secret not set. Did you retrieve an authorized request token before?");
    }
    if ((this.isOAuth10a) && (paramString != null))
    {
      retrieveToken(paramOAuthConsumer, this.accessTokenEndpointUrl, new String[] { "oauth_verifier", paramString });
      return;
    }
    retrieveToken(paramOAuthConsumer, this.accessTokenEndpointUrl, new String[0]);
  }
  
  public String retrieveRequestToken(OAuthConsumer paramOAuthConsumer, String paramString)
    throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException
  {
    paramOAuthConsumer.setTokenWithSecret(null, null);
    retrieveToken(paramOAuthConsumer, this.requestTokenEndpointUrl, new String[] { "oauth_callback", paramString });
    String str1 = this.responseParameters.getFirst("oauth_callback_confirmed");
    this.responseParameters.remove("oauth_callback_confirmed");
    this.isOAuth10a = Boolean.TRUE.toString().equals(str1);
    if (this.isOAuth10a)
    {
      String str3 = this.authorizationWebsiteUrl;
      String[] arrayOfString2 = new String[2];
      arrayOfString2[0] = "oauth_token";
      arrayOfString2[1] = paramOAuthConsumer.getToken();
      return OAuth.addQueryParameters(str3, arrayOfString2);
    }
    String str2 = this.authorizationWebsiteUrl;
    String[] arrayOfString1 = new String[4];
    arrayOfString1[0] = "oauth_token";
    arrayOfString1[1] = paramOAuthConsumer.getToken();
    arrayOfString1[2] = "oauth_callback";
    arrayOfString1[3] = paramString;
    return OAuth.addQueryParameters(str2, arrayOfString1);
  }
  
  /* Error */
  protected void retrieveToken(OAuthConsumer paramOAuthConsumer, String paramString, String... paramVarArgs)
    throws OAuthMessageSignerException, OAuthCommunicationException, OAuthNotAuthorizedException, OAuthExpectationFailedException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 183	com/parse/signpost/AbstractOAuthProvider:getRequestHeaders	()Ljava/util/Map;
    //   4: astore 4
    //   6: aload_1
    //   7: invokeinterface 186 1 0
    //   12: ifnull +12 -> 24
    //   15: aload_1
    //   16: invokeinterface 189 1 0
    //   21: ifnonnull +13 -> 34
    //   24: new 130	com/parse/signpost/exception/OAuthExpectationFailedException
    //   27: dup
    //   28: ldc 191
    //   30: invokespecial 141	com/parse/signpost/exception/OAuthExpectationFailedException:<init>	(Ljava/lang/String;)V
    //   33: athrow
    //   34: aconst_null
    //   35: astore 5
    //   37: aconst_null
    //   38: astore 6
    //   40: aload_0
    //   41: aload_2
    //   42: invokevirtual 193	com/parse/signpost/AbstractOAuthProvider:createRequest	(Ljava/lang/String;)Lcom/parse/signpost/http/HttpRequest;
    //   45: astore 5
    //   47: aload 4
    //   49: invokeinterface 199 1 0
    //   54: invokeinterface 205 1 0
    //   59: astore 12
    //   61: aload 12
    //   63: invokeinterface 210 1 0
    //   68: istore 13
    //   70: aconst_null
    //   71: astore 6
    //   73: iload 13
    //   75: ifeq +57 -> 132
    //   78: aload 12
    //   80: invokeinterface 214 1 0
    //   85: checkcast 143	java/lang/String
    //   88: astore 14
    //   90: aload 5
    //   92: aload 14
    //   94: aload 4
    //   96: aload 14
    //   98: invokeinterface 218 2 0
    //   103: checkcast 143	java/lang/String
    //   106: invokeinterface 223 3 0
    //   111: goto -50 -> 61
    //   114: astore 11
    //   116: aload 11
    //   118: athrow
    //   119: astore 8
    //   121: aload_0
    //   122: aload 5
    //   124: aload 6
    //   126: invokevirtual 225	com/parse/signpost/AbstractOAuthProvider:closeConnection	(Lcom/parse/signpost/http/HttpRequest;Lcom/parse/signpost/http/HttpResponse;)V
    //   129: aload 8
    //   131: athrow
    //   132: aload_3
    //   133: ifnull +27 -> 160
    //   136: new 36	com/parse/signpost/http/HttpParameters
    //   139: dup
    //   140: invokespecial 37	com/parse/signpost/http/HttpParameters:<init>	()V
    //   143: astore 29
    //   145: aload 29
    //   147: aload_3
    //   148: iconst_1
    //   149: invokevirtual 229	com/parse/signpost/http/HttpParameters:putAll	([Ljava/lang/String;Z)V
    //   152: aload_1
    //   153: aload 29
    //   155: invokeinterface 233 2 0
    //   160: aload_0
    //   161: getfield 124	com/parse/signpost/AbstractOAuthProvider:listener	Lcom/parse/signpost/OAuthProviderListener;
    //   164: astore 15
    //   166: aconst_null
    //   167: astore 6
    //   169: aload 15
    //   171: ifnull +14 -> 185
    //   174: aload_0
    //   175: getfield 124	com/parse/signpost/AbstractOAuthProvider:listener	Lcom/parse/signpost/OAuthProviderListener;
    //   178: aload 5
    //   180: invokeinterface 239 2 0
    //   185: aload_1
    //   186: aload 5
    //   188: invokeinterface 243 2 0
    //   193: pop
    //   194: aload_0
    //   195: getfield 124	com/parse/signpost/AbstractOAuthProvider:listener	Lcom/parse/signpost/OAuthProviderListener;
    //   198: astore 17
    //   200: aconst_null
    //   201: astore 6
    //   203: aload 17
    //   205: ifnull +14 -> 219
    //   208: aload_0
    //   209: getfield 124	com/parse/signpost/AbstractOAuthProvider:listener	Lcom/parse/signpost/OAuthProviderListener;
    //   212: aload 5
    //   214: invokeinterface 246 2 0
    //   219: aload_0
    //   220: aload 5
    //   222: invokevirtual 250	com/parse/signpost/AbstractOAuthProvider:sendRequest	(Lcom/parse/signpost/http/HttpRequest;)Lcom/parse/signpost/http/HttpResponse;
    //   225: astore 6
    //   227: aload 6
    //   229: invokeinterface 254 1 0
    //   234: istore 18
    //   236: aload_0
    //   237: getfield 124	com/parse/signpost/AbstractOAuthProvider:listener	Lcom/parse/signpost/OAuthProviderListener;
    //   240: astore 19
    //   242: iconst_0
    //   243: istore 20
    //   245: aload 19
    //   247: ifnull +22 -> 269
    //   250: aload_0
    //   251: getfield 124	com/parse/signpost/AbstractOAuthProvider:listener	Lcom/parse/signpost/OAuthProviderListener;
    //   254: aload 5
    //   256: aload 6
    //   258: invokeinterface 258 3 0
    //   263: istore 21
    //   265: iload 21
    //   267: istore 20
    //   269: iload 20
    //   271: ifeq +24 -> 295
    //   274: aload_0
    //   275: aload 5
    //   277: aload 6
    //   279: invokevirtual 225	com/parse/signpost/AbstractOAuthProvider:closeConnection	(Lcom/parse/signpost/http/HttpRequest;Lcom/parse/signpost/http/HttpResponse;)V
    //   282: return
    //   283: astore 28
    //   285: new 94	com/parse/signpost/exception/OAuthCommunicationException
    //   288: dup
    //   289: aload 28
    //   291: invokespecial 261	com/parse/signpost/exception/OAuthCommunicationException:<init>	(Ljava/lang/Exception;)V
    //   294: athrow
    //   295: iload 18
    //   297: sipush 300
    //   300: if_icmplt +11 -> 311
    //   303: aload_0
    //   304: iload 18
    //   306: aload 6
    //   308: invokevirtual 263	com/parse/signpost/AbstractOAuthProvider:handleUnexpectedResponse	(ILcom/parse/signpost/http/HttpResponse;)V
    //   311: aload 6
    //   313: invokeinterface 76 1 0
    //   318: invokestatic 267	com/parse/signpost/OAuth:decodeForm	(Ljava/io/InputStream;)Lcom/parse/signpost/http/HttpParameters;
    //   321: astore 22
    //   323: aload 22
    //   325: ldc 175
    //   327: invokevirtual 62	com/parse/signpost/http/HttpParameters:getFirst	(Ljava/lang/Object;)Ljava/lang/String;
    //   330: astore 23
    //   332: aload 22
    //   334: ldc_w 269
    //   337: invokevirtual 62	com/parse/signpost/http/HttpParameters:getFirst	(Ljava/lang/Object;)Ljava/lang/String;
    //   340: astore 24
    //   342: aload 22
    //   344: ldc 175
    //   346: invokevirtual 162	com/parse/signpost/http/HttpParameters:remove	(Ljava/lang/Object;)Ljava/util/SortedSet;
    //   349: pop
    //   350: aload 22
    //   352: ldc_w 269
    //   355: invokevirtual 162	com/parse/signpost/http/HttpParameters:remove	(Ljava/lang/Object;)Ljava/util/SortedSet;
    //   358: pop
    //   359: aload_0
    //   360: aload 22
    //   362: invokevirtual 272	com/parse/signpost/AbstractOAuthProvider:setResponseParameters	(Lcom/parse/signpost/http/HttpParameters;)V
    //   365: aload 23
    //   367: ifnull +8 -> 375
    //   370: aload 24
    //   372: ifnonnull +19 -> 391
    //   375: new 130	com/parse/signpost/exception/OAuthExpectationFailedException
    //   378: dup
    //   379: ldc_w 274
    //   382: invokespecial 141	com/parse/signpost/exception/OAuthExpectationFailedException:<init>	(Ljava/lang/String;)V
    //   385: athrow
    //   386: astore 10
    //   388: aload 10
    //   390: athrow
    //   391: aload_1
    //   392: aload 23
    //   394: aload 24
    //   396: invokeinterface 154 3 0
    //   401: aload_0
    //   402: aload 5
    //   404: aload 6
    //   406: invokevirtual 225	com/parse/signpost/AbstractOAuthProvider:closeConnection	(Lcom/parse/signpost/http/HttpRequest;Lcom/parse/signpost/http/HttpResponse;)V
    //   409: return
    //   410: astore 27
    //   412: new 94	com/parse/signpost/exception/OAuthCommunicationException
    //   415: dup
    //   416: aload 27
    //   418: invokespecial 261	com/parse/signpost/exception/OAuthCommunicationException:<init>	(Ljava/lang/Exception;)V
    //   421: athrow
    //   422: astore 7
    //   424: new 94	com/parse/signpost/exception/OAuthCommunicationException
    //   427: dup
    //   428: aload 7
    //   430: invokespecial 261	com/parse/signpost/exception/OAuthCommunicationException:<init>	(Ljava/lang/Exception;)V
    //   433: athrow
    //   434: astore 9
    //   436: new 94	com/parse/signpost/exception/OAuthCommunicationException
    //   439: dup
    //   440: aload 9
    //   442: invokespecial 261	com/parse/signpost/exception/OAuthCommunicationException:<init>	(Ljava/lang/Exception;)V
    //   445: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	446	0	this	AbstractOAuthProvider
    //   0	446	1	paramOAuthConsumer	OAuthConsumer
    //   0	446	2	paramString	String
    //   0	446	3	paramVarArgs	String[]
    //   4	91	4	localMap	Map
    //   35	368	5	localHttpRequest	HttpRequest
    //   38	367	6	localHttpResponse	HttpResponse
    //   422	7	7	localException1	Exception
    //   119	11	8	localObject	Object
    //   434	7	9	localException2	Exception
    //   386	3	10	localOAuthExpectationFailedException	OAuthExpectationFailedException
    //   114	3	11	localOAuthNotAuthorizedException	OAuthNotAuthorizedException
    //   59	20	12	localIterator	java.util.Iterator
    //   68	6	13	bool1	boolean
    //   88	9	14	str1	String
    //   164	6	15	localOAuthProviderListener1	OAuthProviderListener
    //   198	6	17	localOAuthProviderListener2	OAuthProviderListener
    //   234	71	18	i	int
    //   240	6	19	localOAuthProviderListener3	OAuthProviderListener
    //   243	27	20	j	int
    //   263	3	21	bool2	boolean
    //   321	40	22	localHttpParameters1	HttpParameters
    //   330	63	23	str2	String
    //   340	55	24	str3	String
    //   410	7	27	localException3	Exception
    //   283	7	28	localException4	Exception
    //   143	11	29	localHttpParameters2	HttpParameters
    // Exception table:
    //   from	to	target	type
    //   40	61	114	com/parse/signpost/exception/OAuthNotAuthorizedException
    //   61	70	114	com/parse/signpost/exception/OAuthNotAuthorizedException
    //   78	111	114	com/parse/signpost/exception/OAuthNotAuthorizedException
    //   136	160	114	com/parse/signpost/exception/OAuthNotAuthorizedException
    //   160	166	114	com/parse/signpost/exception/OAuthNotAuthorizedException
    //   174	185	114	com/parse/signpost/exception/OAuthNotAuthorizedException
    //   185	200	114	com/parse/signpost/exception/OAuthNotAuthorizedException
    //   208	219	114	com/parse/signpost/exception/OAuthNotAuthorizedException
    //   219	242	114	com/parse/signpost/exception/OAuthNotAuthorizedException
    //   250	265	114	com/parse/signpost/exception/OAuthNotAuthorizedException
    //   303	311	114	com/parse/signpost/exception/OAuthNotAuthorizedException
    //   311	365	114	com/parse/signpost/exception/OAuthNotAuthorizedException
    //   375	386	114	com/parse/signpost/exception/OAuthNotAuthorizedException
    //   391	401	114	com/parse/signpost/exception/OAuthNotAuthorizedException
    //   40	61	119	finally
    //   61	70	119	finally
    //   78	111	119	finally
    //   116	119	119	finally
    //   136	160	119	finally
    //   160	166	119	finally
    //   174	185	119	finally
    //   185	200	119	finally
    //   208	219	119	finally
    //   219	242	119	finally
    //   250	265	119	finally
    //   303	311	119	finally
    //   311	365	119	finally
    //   375	386	119	finally
    //   388	391	119	finally
    //   391	401	119	finally
    //   424	434	119	finally
    //   274	282	283	java/lang/Exception
    //   40	61	386	com/parse/signpost/exception/OAuthExpectationFailedException
    //   61	70	386	com/parse/signpost/exception/OAuthExpectationFailedException
    //   78	111	386	com/parse/signpost/exception/OAuthExpectationFailedException
    //   136	160	386	com/parse/signpost/exception/OAuthExpectationFailedException
    //   160	166	386	com/parse/signpost/exception/OAuthExpectationFailedException
    //   174	185	386	com/parse/signpost/exception/OAuthExpectationFailedException
    //   185	200	386	com/parse/signpost/exception/OAuthExpectationFailedException
    //   208	219	386	com/parse/signpost/exception/OAuthExpectationFailedException
    //   219	242	386	com/parse/signpost/exception/OAuthExpectationFailedException
    //   250	265	386	com/parse/signpost/exception/OAuthExpectationFailedException
    //   303	311	386	com/parse/signpost/exception/OAuthExpectationFailedException
    //   311	365	386	com/parse/signpost/exception/OAuthExpectationFailedException
    //   375	386	386	com/parse/signpost/exception/OAuthExpectationFailedException
    //   391	401	386	com/parse/signpost/exception/OAuthExpectationFailedException
    //   401	409	410	java/lang/Exception
    //   40	61	422	java/lang/Exception
    //   61	70	422	java/lang/Exception
    //   78	111	422	java/lang/Exception
    //   136	160	422	java/lang/Exception
    //   160	166	422	java/lang/Exception
    //   174	185	422	java/lang/Exception
    //   185	200	422	java/lang/Exception
    //   208	219	422	java/lang/Exception
    //   219	242	422	java/lang/Exception
    //   250	265	422	java/lang/Exception
    //   303	311	422	java/lang/Exception
    //   311	365	422	java/lang/Exception
    //   375	386	422	java/lang/Exception
    //   391	401	422	java/lang/Exception
    //   121	129	434	java/lang/Exception
  }
  
  protected abstract HttpResponse sendRequest(HttpRequest paramHttpRequest)
    throws Exception;
  
  public void setListener(OAuthProviderListener paramOAuthProviderListener)
  {
    this.listener = paramOAuthProviderListener;
  }
  
  public void setOAuth10a(boolean paramBoolean)
  {
    this.isOAuth10a = paramBoolean;
  }
  
  public void setRequestHeader(String paramString1, String paramString2)
  {
    this.defaultHeaders.put(paramString1, paramString2);
  }
  
  public void setResponseParameters(HttpParameters paramHttpParameters)
  {
    this.responseParameters = paramHttpParameters;
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.signpost.AbstractOAuthProvider
 * JD-Core Version:    0.7.0.1
 */