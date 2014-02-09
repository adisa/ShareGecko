package com.parse;

import com.parse.codec.digest.DigestUtils;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

class ParseCommand
{
  private static int CONNECTION_TIMEOUT = 10000;
  private static int INITIAL_DELAY = 1000;
  private static int SOCKET_TIMEOUT = 10000;
  private int attemptsMade = 0;
  private boolean callCallbacksOnFailure = true;
  private boolean cancelled = false;
  private HttpClient client;
  private long delay = 0L;
  private InternalCallback internalCallback;
  private String localId;
  String op;
  JSONObject params;
  private HttpPost post;
  private boolean retryEnabled = false;
  
  ParseCommand(String paramString)
  {
    this.op = paramString;
    this.params = new JSONObject();
    BasicHttpParams localBasicHttpParams = new BasicHttpParams();
    HttpConnectionParams.setConnectionTimeout(localBasicHttpParams, CONNECTION_TIMEOUT);
    HttpConnectionParams.setSoTimeout(localBasicHttpParams, SOCKET_TIMEOUT);
    this.client = new DefaultHttpClient(localBasicHttpParams);
    maybeSetupHttpProxy();
  }
  
  ParseCommand(String paramString, HttpClient paramHttpClient)
  {
    this.op = paramString;
    this.params = new JSONObject();
    this.client = paramHttpClient;
    maybeSetupHttpProxy();
  }
  
  ParseCommand(JSONObject paramJSONObject)
    throws JSONException
  {
    this.op = paramJSONObject.getString("op");
    this.params = paramJSONObject.getJSONObject("params");
    this.localId = paramJSONObject.optString("localId", null);
    BasicHttpParams localBasicHttpParams = new BasicHttpParams();
    HttpConnectionParams.setConnectionTimeout(localBasicHttpParams, CONNECTION_TIMEOUT);
    HttpConnectionParams.setSoTimeout(localBasicHttpParams, SOCKET_TIMEOUT);
    this.client = new DefaultHttpClient(localBasicHttpParams);
    maybeSetupHttpProxy();
  }
  
  static void addToStringer(JSONStringer paramJSONStringer, Object paramObject)
    throws JSONException
  {
    if ((paramObject instanceof JSONObject))
    {
      paramJSONStringer.object();
      JSONObject localJSONObject = (JSONObject)paramObject;
      Iterator localIterator1 = localJSONObject.keys();
      ArrayList localArrayList = new ArrayList();
      while (localIterator1.hasNext()) {
        localArrayList.add(localIterator1.next());
      }
      Collections.sort(localArrayList);
      Iterator localIterator2 = localArrayList.iterator();
      while (localIterator2.hasNext())
      {
        String str = (String)localIterator2.next();
        paramJSONStringer.key(str);
        addToStringer(paramJSONStringer, localJSONObject.opt(str));
      }
      paramJSONStringer.endObject();
      return;
    }
    if ((paramObject instanceof JSONArray))
    {
      JSONArray localJSONArray = (JSONArray)paramObject;
      paramJSONStringer.array();
      for (int i = 0; i < localJSONArray.length(); i++) {
        addToStringer(paramJSONStringer, localJSONArray.get(i));
      }
      paramJSONStringer.endArray();
      return;
    }
    paramJSONStringer.value(paramObject);
  }
  
  private void calculateNextDelay()
  {
    this.delay = (2L * this.delay);
  }
  
  private static void getLocalPointersIn(Object paramObject, ArrayList<JSONObject> paramArrayList)
    throws JSONException
  {
    JSONObject localJSONObject;
    if ((paramObject instanceof JSONObject))
    {
      localJSONObject = (JSONObject)paramObject;
      if (("Pointer".equals(localJSONObject.opt("__type"))) && (localJSONObject.has("localId"))) {
        paramArrayList.add((JSONObject)paramObject);
      }
    }
    for (;;)
    {
      return;
      Iterator localIterator = localJSONObject.keys();
      while (localIterator.hasNext()) {
        getLocalPointersIn(localJSONObject.get((String)localIterator.next()), paramArrayList);
      }
      if ((paramObject instanceof JSONArray))
      {
        JSONArray localJSONArray = (JSONArray)paramObject;
        for (int i = 0; i < localJSONArray.length(); i++) {
          getLocalPointersIn(localJSONArray.get(i), paramArrayList);
        }
      }
    }
  }
  
  private void maybeSetupHttpProxy()
  {
    String str1 = System.getProperty("http.proxyHost");
    String str2 = System.getProperty("http.proxyPort");
    if ((str1 == null) || (str1.length() == 0) || (str2 == null) || (str2.length() == 0)) {
      return;
    }
    HttpHost localHttpHost = new HttpHost(str1, Integer.parseInt(str2), "http");
    this.client.getParams().setParameter("http.route.default-proxy", localHttpHost);
  }
  
  /* Error */
  private JSONObject sendRequest(HttpClient paramHttpClient, HttpPost paramHttpPost)
    throws ParseException
  {
    // Byte code:
    //   0: new 238	java/io/BufferedReader
    //   3: dup
    //   4: new 240	java/io/InputStreamReader
    //   7: dup
    //   8: aload_1
    //   9: aload_2
    //   10: invokeinterface 244 2 0
    //   15: invokeinterface 250 1 0
    //   20: invokeinterface 256 1 0
    //   25: ldc_w 258
    //   28: invokespecial 261	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;Ljava/lang/String;)V
    //   31: sipush 8192
    //   34: invokespecial 264	java/io/BufferedReader:<init>	(Ljava/io/Reader;I)V
    //   37: invokevirtual 268	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   40: astore 5
    //   42: new 52	org/json/JSONObject
    //   45: dup
    //   46: new 270	org/json/JSONTokener
    //   49: dup
    //   50: aload 5
    //   52: invokespecial 272	org/json/JSONTokener:<init>	(Ljava/lang/String;)V
    //   55: invokespecial 275	org/json/JSONObject:<init>	(Lorg/json/JSONTokener;)V
    //   58: astore 6
    //   60: aload 6
    //   62: areturn
    //   63: astore 4
    //   65: aload_0
    //   66: ldc_w 277
    //   69: aload 4
    //   71: invokevirtual 281	com/parse/ParseCommand:connectionFailed	(Ljava/lang/String;Ljava/lang/Exception;)Lcom/parse/ParseException;
    //   74: athrow
    //   75: astore_3
    //   76: aload_0
    //   77: ldc_w 283
    //   80: aload_3
    //   81: invokevirtual 281	com/parse/ParseCommand:connectionFailed	(Ljava/lang/String;Ljava/lang/Exception;)Lcom/parse/ParseException;
    //   84: athrow
    //   85: astore 7
    //   87: aload_0
    //   88: ldc_w 285
    //   91: aload 7
    //   93: invokevirtual 281	com/parse/ParseCommand:connectionFailed	(Ljava/lang/String;Ljava/lang/Exception;)Lcom/parse/ParseException;
    //   96: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	97	0	this	ParseCommand
    //   0	97	1	paramHttpClient	HttpClient
    //   0	97	2	paramHttpPost	HttpPost
    //   75	6	3	localIOException	java.io.IOException
    //   63	7	4	localClientProtocolException	org.apache.http.client.ClientProtocolException
    //   40	11	5	str	String
    //   58	3	6	localJSONObject	JSONObject
    //   85	7	7	localJSONException	JSONException
    // Exception table:
    //   from	to	target	type
    //   0	42	63	org/apache/http/client/ClientProtocolException
    //   0	42	75	java/io/IOException
    //   42	60	85	org/json/JSONException
  }
  
  private JSONObject sendRequestWithRetries()
    throws ParseException
  {
    try
    {
      JSONObject localJSONObject = sendRequest(this.client, this.post);
      return localJSONObject;
    }
    catch (ParseException localParseException)
    {
      if (this.cancelled) {
        throw new ParseException(1, "Request cancelled");
      }
      this.attemptsMade = (1 + this.attemptsMade);
      if (!this.retryEnabled) {
        break label120;
      }
    }
    if (this.attemptsMade < 5) {
      Parse.logI("com.parse.ParseCommand", "Fetch failed. Waiting " + this.delay + " milliseconds before attempt #" + (1 + this.attemptsMade));
    }
    try
    {
      Thread.sleep(this.delay);
      label111:
      calculateNextDelay();
      return sendRequestWithRetries();
      label120:
      throw localParseException;
    }
    catch (InterruptedException localInterruptedException)
    {
      break label111;
    }
  }
  
  public static void setInitialDelay(double paramDouble)
  {
    INITIAL_DELAY = (int)(1000.0D * paramDouble);
  }
  
  static String toDeterministicString(Object paramObject)
    throws JSONException
  {
    JSONStringer localJSONStringer = new JSONStringer();
    addToStringer(localJSONStringer, paramObject);
    return localJSONStringer.toString();
  }
  
  public void cancel()
  {
    if (this.post != null) {
      this.post.abort();
    }
    this.cancelled = true;
  }
  
  ParseException connectionFailed(String paramString, Exception paramException)
  {
    return new ParseException(100, paramString + ": " + paramException.getClass().getName() + ": " + paramException.getMessage());
  }
  
  public void enableRetrying()
  {
    this.retryEnabled = true;
    this.delay = (INITIAL_DELAY + (INITIAL_DELAY * Math.random()));
  }
  
  String getCacheKey()
  {
    try
    {
      String str1 = toDeterministicString(this.params);
      String str2 = str1;
      ParseUser localParseUser = ParseUser.getCurrentUser();
      if (localParseUser != null)
      {
        String str3 = localParseUser.getSessionToken();
        if (str3 != null) {
          str2 = str2 + str3;
        }
      }
      return "ParseCommand." + this.op + "." + "2" + "." + DigestUtils.md5Hex(str2);
    }
    catch (JSONException localJSONException)
    {
      throw new RuntimeException(localJSONException.getMessage());
    }
  }
  
  String getLocalId()
  {
    return this.localId;
  }
  
  public void maybeChangeServerOperation()
    throws JSONException
  {
    if (this.localId != null)
    {
      String str = LocalIdManager.getDefaultInstance().getObjectId(this.localId);
      if (str != null)
      {
        this.localId = null;
        JSONObject localJSONObject = this.params.optJSONObject("data");
        if (localJSONObject != null) {
          localJSONObject.put("objectId", str);
        }
        if (this.op.equals("create")) {
          this.op = "update";
        }
      }
    }
  }
  
  Object perform()
    throws ParseException
  {
    return perform(false);
  }
  
  Object perform(boolean paramBoolean)
    throws ParseException
  {
    Parse.checkInit();
    resolveLocalIds();
    if ((this.post == null) || (!this.post.getURI().getHost().equals(ParseObject.server))) {
      preparePost();
    }
    Object localObject1 = null;
    try
    {
      JSONObject localJSONObject = sendRequestWithRetries();
      try
      {
        boolean bool = localJSONObject.has("error");
        localObject1 = null;
        if (bool) {
          throw new ParseException(localJSONObject.getInt("code"), localJSONObject.getString("error"));
        }
      }
      catch (JSONException localJSONException)
      {
        throw connectionFailed("corrupted json", localJSONException);
      }
      localObject3 = localJSONObject.get("result");
    }
    finally
    {
      if ((this.internalCallback != null) && ((localObject1 != null) || (this.callCallbacksOnFailure))) {
        this.internalCallback.perform(this, localObject1);
      }
    }
    Object localObject3;
    localObject1 = localObject3;
    if (paramBoolean) {
      Parse.saveToKeyValueCache(getCacheKey(), localObject1.toString());
    }
    if ((this.internalCallback != null) && ((localObject1 != null) || (this.callCallbacksOnFailure))) {
      this.internalCallback.perform(this, localObject1);
    }
    return localObject1;
  }
  
  /* Error */
  void preparePost()
    throws ParseException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 55	com/parse/ParseCommand:params	Lorg/json/JSONObject;
    //   4: invokevirtual 110	org/json/JSONObject:keys	()Ljava/util/Iterator;
    //   7: astore_1
    //   8: new 52	org/json/JSONObject
    //   11: dup
    //   12: invokespecial 53	org/json/JSONObject:<init>	()V
    //   15: astore_2
    //   16: aload_1
    //   17: invokeinterface 119 1 0
    //   22: ifeq +46 -> 68
    //   25: aload_1
    //   26: invokeinterface 123 1 0
    //   31: checkcast 138	java/lang/String
    //   34: astore 18
    //   36: aload_2
    //   37: aload 18
    //   39: aload_0
    //   40: getfield 55	com/parse/ParseCommand:params	Lorg/json/JSONObject;
    //   43: aload 18
    //   45: invokevirtual 189	org/json/JSONObject:get	(Ljava/lang/String;)Ljava/lang/Object;
    //   48: invokevirtual 422	org/json/JSONObject:put	(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;
    //   51: pop
    //   52: goto -36 -> 16
    //   55: astore_3
    //   56: new 398	java/lang/RuntimeException
    //   59: dup
    //   60: aload_3
    //   61: invokevirtual 399	org/json/JSONException:getMessage	()Ljava/lang/String;
    //   64: invokespecial 400	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   67: athrow
    //   68: aload_2
    //   69: ldc_w 487
    //   72: ldc_w 489
    //   75: invokevirtual 422	org/json/JSONObject:put	(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;
    //   78: pop
    //   79: aload_2
    //   80: ldc_w 491
    //   83: iconst_1
    //   84: invokevirtual 494	org/json/JSONObject:put	(Ljava/lang/String;Z)Lorg/json/JSONObject;
    //   87: pop
    //   88: aload_2
    //   89: ldc_w 496
    //   92: invokestatic 502	com/parse/ParseInstallation:getCurrentInstallation	()Lcom/parse/ParseInstallation;
    //   95: invokevirtual 505	com/parse/ParseInstallation:getInstallationId	()Ljava/lang/String;
    //   98: invokevirtual 422	org/json/JSONObject:put	(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;
    //   101: pop
    //   102: aload_2
    //   103: ldc_w 507
    //   106: invokestatic 513	java/util/UUID:randomUUID	()Ljava/util/UUID;
    //   109: invokevirtual 514	java/util/UUID:toString	()Ljava/lang/String;
    //   112: invokevirtual 422	org/json/JSONObject:put	(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;
    //   115: pop
    //   116: invokestatic 382	com/parse/ParseUser:getCurrentUser	()Lcom/parse/ParseUser;
    //   119: astore 8
    //   121: aload 8
    //   123: ifnull +24 -> 147
    //   126: aload 8
    //   128: invokevirtual 385	com/parse/ParseUser:getSessionToken	()Ljava/lang/String;
    //   131: ifnull +16 -> 147
    //   134: aload_2
    //   135: ldc_w 516
    //   138: aload 8
    //   140: invokevirtual 385	com/parse/ParseUser:getSessionToken	()Ljava/lang/String;
    //   143: invokevirtual 422	org/json/JSONObject:put	(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;
    //   146: pop
    //   147: iconst_3
    //   148: anewarray 4	java/lang/Object
    //   151: astore 9
    //   153: aload 9
    //   155: iconst_0
    //   156: getstatic 450	com/parse/ParseObject:server	Ljava/lang/String;
    //   159: aastore
    //   160: aload 9
    //   162: iconst_1
    //   163: ldc_w 391
    //   166: aastore
    //   167: aload 9
    //   169: iconst_2
    //   170: aload_0
    //   171: getfield 50	com/parse/ParseCommand:op	Ljava/lang/String;
    //   174: aastore
    //   175: aload_0
    //   176: new 347	org/apache/http/client/methods/HttpPost
    //   179: dup
    //   180: ldc_w 518
    //   183: aload 9
    //   185: invokestatic 522	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   188: invokespecial 523	org/apache/http/client/methods/HttpPost:<init>	(Ljava/lang/String;)V
    //   191: putfield 291	com/parse/ParseCommand:post	Lorg/apache/http/client/methods/HttpPost;
    //   194: new 525	org/apache/http/entity/StringEntity
    //   197: dup
    //   198: aload_2
    //   199: invokevirtual 526	org/json/JSONObject:toString	()Ljava/lang/String;
    //   202: ldc_w 528
    //   205: invokespecial 530	org/apache/http/entity/StringEntity:<init>	(Ljava/lang/String;Ljava/lang/String;)V
    //   208: astore 10
    //   210: aload 10
    //   212: ldc_w 532
    //   215: invokevirtual 535	org/apache/http/entity/StringEntity:setContentType	(Ljava/lang/String;)V
    //   218: aload_0
    //   219: getfield 291	com/parse/ParseCommand:post	Lorg/apache/http/client/methods/HttpPost;
    //   222: aload 10
    //   224: invokevirtual 539	org/apache/http/client/methods/HttpPost:setEntity	(Lorg/apache/http/HttpEntity;)V
    //   227: new 541	com/parse/signpost/commonshttp/CommonsHttpOAuthConsumer
    //   230: dup
    //   231: getstatic 544	com/parse/Parse:applicationId	Ljava/lang/String;
    //   234: getstatic 547	com/parse/Parse:clientKey	Ljava/lang/String;
    //   237: invokespecial 548	com/parse/signpost/commonshttp/CommonsHttpOAuthConsumer:<init>	(Ljava/lang/String;Ljava/lang/String;)V
    //   240: astore 12
    //   242: aload 12
    //   244: aconst_null
    //   245: ldc_w 550
    //   248: invokeinterface 555 3 0
    //   253: aload 12
    //   255: aload_0
    //   256: getfield 291	com/parse/ParseCommand:post	Lorg/apache/http/client/methods/HttpPost;
    //   259: invokeinterface 559 2 0
    //   264: pop
    //   265: return
    //   266: astore 11
    //   268: new 398	java/lang/RuntimeException
    //   271: dup
    //   272: aload 11
    //   274: invokevirtual 560	java/io/UnsupportedEncodingException:getMessage	()Ljava/lang/String;
    //   277: invokespecial 400	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   280: athrow
    //   281: astore 15
    //   283: new 232	com/parse/ParseException
    //   286: dup
    //   287: bipush 109
    //   289: aload 15
    //   291: invokevirtual 561	com/parse/signpost/exception/OAuthMessageSignerException:getMessage	()Ljava/lang/String;
    //   294: invokespecial 298	com/parse/ParseException:<init>	(ILjava/lang/String;)V
    //   297: athrow
    //   298: astore 14
    //   300: new 232	com/parse/ParseException
    //   303: dup
    //   304: bipush 109
    //   306: aload 14
    //   308: invokevirtual 562	com/parse/signpost/exception/OAuthExpectationFailedException:getMessage	()Ljava/lang/String;
    //   311: invokespecial 298	com/parse/ParseException:<init>	(ILjava/lang/String;)V
    //   314: athrow
    //   315: astore 13
    //   317: new 232	com/parse/ParseException
    //   320: dup
    //   321: bipush 109
    //   323: aload 13
    //   325: invokevirtual 563	com/parse/signpost/exception/OAuthCommunicationException:getMessage	()Ljava/lang/String;
    //   328: invokespecial 298	com/parse/ParseException:<init>	(ILjava/lang/String;)V
    //   331: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	332	0	this	ParseCommand
    //   7	19	1	localIterator	Iterator
    //   15	184	2	localJSONObject	JSONObject
    //   55	6	3	localJSONException	JSONException
    //   119	20	8	localParseUser	ParseUser
    //   151	33	9	arrayOfObject	Object[]
    //   208	15	10	localStringEntity	org.apache.http.entity.StringEntity
    //   266	7	11	localUnsupportedEncodingException	java.io.UnsupportedEncodingException
    //   240	14	12	localCommonsHttpOAuthConsumer	com.parse.signpost.commonshttp.CommonsHttpOAuthConsumer
    //   315	9	13	localOAuthCommunicationException	com.parse.signpost.exception.OAuthCommunicationException
    //   298	9	14	localOAuthExpectationFailedException	com.parse.signpost.exception.OAuthExpectationFailedException
    //   281	9	15	localOAuthMessageSignerException	com.parse.signpost.exception.OAuthMessageSignerException
    //   34	10	18	str	String
    // Exception table:
    //   from	to	target	type
    //   16	52	55	org/json/JSONException
    //   68	121	55	org/json/JSONException
    //   126	147	55	org/json/JSONException
    //   194	227	266	java/io/UnsupportedEncodingException
    //   253	265	281	com/parse/signpost/exception/OAuthMessageSignerException
    //   253	265	298	com/parse/signpost/exception/OAuthExpectationFailedException
    //   253	265	315	com/parse/signpost/exception/OAuthCommunicationException
  }
  
  void put(String paramString, int paramInt)
  {
    try
    {
      this.params.put(paramString, paramInt);
      return;
    }
    catch (JSONException localJSONException)
    {
      throw new RuntimeException(localJSONException.getMessage());
    }
  }
  
  void put(String paramString, long paramLong)
  {
    try
    {
      this.params.put(paramString, paramLong);
      return;
    }
    catch (JSONException localJSONException)
    {
      throw new RuntimeException(localJSONException.getMessage());
    }
  }
  
  void put(String paramString1, String paramString2)
  {
    try
    {
      this.params.put(paramString1, paramString2);
      return;
    }
    catch (JSONException localJSONException)
    {
      throw new RuntimeException(localJSONException.getMessage());
    }
  }
  
  void put(String paramString, JSONArray paramJSONArray)
  {
    try
    {
      this.params.put(paramString, paramJSONArray);
      return;
    }
    catch (JSONException localJSONException)
    {
      throw new RuntimeException(localJSONException.getMessage());
    }
  }
  
  void put(String paramString, JSONObject paramJSONObject)
  {
    try
    {
      this.params.put(paramString, paramJSONObject);
      return;
    }
    catch (JSONException localJSONException)
    {
      throw new RuntimeException(localJSONException.getMessage());
    }
  }
  
  public void releaseLocalIds()
  {
    if (this.localId != null) {
      LocalIdManager.getDefaultInstance().releaseLocalIdOnDisk(this.localId);
    }
    try
    {
      Object localObject = this.params.get("data");
      ArrayList localArrayList = new ArrayList();
      getLocalPointersIn(localObject, localArrayList);
      Iterator localIterator = localArrayList.iterator();
      while (localIterator.hasNext())
      {
        String str = (String)((JSONObject)localIterator.next()).get("localId");
        LocalIdManager.getDefaultInstance().releaseLocalIdOnDisk(str);
      }
      return;
    }
    catch (JSONException localJSONException) {}
  }
  
  public void resolveLocalIds()
  {
    try
    {
      Object localObject = this.params.get("data");
      ArrayList localArrayList = new ArrayList();
      getLocalPointersIn(localObject, localArrayList);
      Iterator localIterator = localArrayList.iterator();
      while (localIterator.hasNext())
      {
        JSONObject localJSONObject = (JSONObject)localIterator.next();
        String str1 = (String)localJSONObject.get("localId");
        String str2 = LocalIdManager.getDefaultInstance().getObjectId(str1);
        if (str2 == null) {
          throw new IllegalStateException("Tried to serialize a command referencing a new, unsaved object.");
        }
        localJSONObject.put("objectId", str2);
        localJSONObject.remove("localId");
      }
      maybeChangeServerOperation();
      return;
    }
    catch (JSONException localJSONException) {}
  }
  
  public void retainLocalIds()
  {
    if (this.localId != null) {
      LocalIdManager.getDefaultInstance().retainLocalIdOnDisk(this.localId);
    }
    try
    {
      Object localObject = this.params.get("data");
      ArrayList localArrayList = new ArrayList();
      getLocalPointersIn(localObject, localArrayList);
      Iterator localIterator = localArrayList.iterator();
      while (localIterator.hasNext())
      {
        String str = (String)((JSONObject)localIterator.next()).get("localId");
        LocalIdManager.getDefaultInstance().retainLocalIdOnDisk(str);
      }
      return;
    }
    catch (JSONException localJSONException) {}
  }
  
  void setCallCallbacksOnFailure(boolean paramBoolean)
  {
    this.callCallbacksOnFailure = paramBoolean;
  }
  
  void setInternalCallback(InternalCallback paramInternalCallback)
  {
    this.internalCallback = paramInternalCallback;
  }
  
  void setLocalId(String paramString)
  {
    this.localId = paramString;
  }
  
  void setOp(String paramString)
  {
    this.op = paramString;
  }
  
  JSONObject toJSONObject()
  {
    try
    {
      JSONObject localJSONObject = new JSONObject();
      localJSONObject.put("op", this.op);
      localJSONObject.put("params", this.params);
      if (this.localId != null) {
        localJSONObject.put("localId", this.localId);
      }
      return localJSONObject;
    }
    catch (JSONException localJSONException)
    {
      throw new RuntimeException(localJSONException.getMessage());
    }
  }
  
  static abstract interface InternalCallback
  {
    public abstract void perform(ParseCommand paramParseCommand, Object paramObject);
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.ParseCommand
 * JD-Core Version:    0.7.0.1
 */