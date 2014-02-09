package com.parse;

import java.util.ArrayList;
import java.util.Iterator;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONObject;

public class ParseFile
{
  private ArrayList<GetDataCallback> callbacks = new ArrayList();
  private ParseCommand currentCommand = null;
  private BackgroundTask<?> currentTask = null;
  private byte[] data;
  private HttpPost fileUploadPost = null;
  private String name = null;
  private ParseFileState previousState;
  private ParseFileState state;
  private String url = null;
  
  ParseFile(String paramString1, String paramString2)
  {
    this.name = paramString1;
    this.url = paramString2;
    setState(ParseFileState.AWAITING_FETCH);
  }
  
  public ParseFile(String paramString, byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte.length > Parse.maxParseFileSize)
    {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = Integer.valueOf(Parse.maxParseFileSize);
      throw new IllegalArgumentException(String.format("ParseFile must be less than %i bytes", arrayOfObject));
    }
    this.name = paramString;
    this.data = paramArrayOfByte;
    setState(ParseFileState.DIRTY);
  }
  
  public ParseFile(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte.length > Parse.maxParseFileSize)
    {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = Integer.valueOf(Parse.maxParseFileSize);
      throw new IllegalArgumentException(String.format("ParseFile must be less than %i bytes", arrayOfObject));
    }
    setState(ParseFileState.DIRTY);
    this.data = paramArrayOfByte;
  }
  
  private ParseCommand constructFileUploadCommand()
  {
    this.currentCommand = new ParseCommand("upload_file");
    this.currentCommand.enableRetrying();
    if (this.name != null) {
      this.currentCommand.put("name", this.name);
    }
    return this.currentCommand;
  }
  
  private void handleFileUploadResult(JSONObject paramJSONObject, ProgressCallback paramProgressCallback)
    throws ParseException
  {
    if (this.state != ParseFileState.SAVING) {
      return;
    }
    if (this.fileUploadPost == null) {
      prepareFileUploadPost(paramJSONObject, paramProgressCallback);
    }
    new ParseRequestRetryer(this.fileUploadPost, 1000L, 5).go(null);
    setState(ParseFileState.DATA_AVAILABLE);
  }
  
  /* Error */
  private void prepareFileUploadPost(JSONObject paramJSONObject, ProgressCallback paramProgressCallback)
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: ldc 104
    //   4: invokevirtual 141	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   7: putfield 29	com/parse/ParseFile:name	Ljava/lang/String;
    //   10: aload_0
    //   11: aload_1
    //   12: ldc 142
    //   14: invokevirtual 141	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   17: putfield 31	com/parse/ParseFile:url	Ljava/lang/String;
    //   20: aload_1
    //   21: ldc 144
    //   23: invokevirtual 148	org/json/JSONObject:getJSONObject	(Ljava/lang/String;)Lorg/json/JSONObject;
    //   26: astore 4
    //   28: new 150	com/parse/CountingMultipartEntity
    //   31: dup
    //   32: getstatic 156	com/parse/entity/mime/HttpMultipartMode:BROWSER_COMPATIBLE	Lcom/parse/entity/mime/HttpMultipartMode;
    //   35: aload_2
    //   36: invokespecial 159	com/parse/CountingMultipartEntity:<init>	(Lcom/parse/entity/mime/HttpMultipartMode;Lcom/parse/ProgressCallback;)V
    //   39: astore 5
    //   41: aload_0
    //   42: getfield 29	com/parse/ParseFile:name	Ljava/lang/String;
    //   45: ldc 161
    //   47: invokevirtual 165	java/lang/String:lastIndexOf	(Ljava/lang/String;)I
    //   50: istore 6
    //   52: aconst_null
    //   53: astore 7
    //   55: iload 6
    //   57: iconst_m1
    //   58: if_icmpeq +33 -> 91
    //   61: aload_0
    //   62: getfield 29	com/parse/ParseFile:name	Ljava/lang/String;
    //   65: iconst_1
    //   66: aload_0
    //   67: getfield 29	com/parse/ParseFile:name	Ljava/lang/String;
    //   70: ldc 161
    //   72: invokevirtual 165	java/lang/String:lastIndexOf	(Ljava/lang/String;)I
    //   75: iadd
    //   76: invokevirtual 169	java/lang/String:substring	(I)Ljava/lang/String;
    //   79: astore 14
    //   81: invokestatic 175	android/webkit/MimeTypeMap:getSingleton	()Landroid/webkit/MimeTypeMap;
    //   84: aload 14
    //   86: invokevirtual 178	android/webkit/MimeTypeMap:getMimeTypeFromExtension	(Ljava/lang/String;)Ljava/lang/String;
    //   89: astore 7
    //   91: aload 7
    //   93: ifnonnull +7 -> 100
    //   96: ldc 180
    //   98: astore 7
    //   100: aload 5
    //   102: ldc 182
    //   104: new 184	com/parse/entity/mime/content/StringBody
    //   107: dup
    //   108: aload 7
    //   110: invokespecial 185	com/parse/entity/mime/content/StringBody:<init>	(Ljava/lang/String;)V
    //   113: invokevirtual 189	com/parse/CountingMultipartEntity:addPart	(Ljava/lang/String;Lcom/parse/entity/mime/content/ContentBody;)V
    //   116: aload 4
    //   118: invokevirtual 193	org/json/JSONObject:keys	()Ljava/util/Iterator;
    //   121: astore 9
    //   123: aload 9
    //   125: invokeinterface 199 1 0
    //   130: ifeq +97 -> 227
    //   133: aload 9
    //   135: invokeinterface 203 1 0
    //   140: checkcast 70	java/lang/String
    //   143: astore 11
    //   145: aload 5
    //   147: aload 11
    //   149: new 184	com/parse/entity/mime/content/StringBody
    //   152: dup
    //   153: aload 4
    //   155: aload 11
    //   157: invokevirtual 141	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   160: invokespecial 185	com/parse/entity/mime/content/StringBody:<init>	(Ljava/lang/String;)V
    //   163: invokevirtual 189	com/parse/CountingMultipartEntity:addPart	(Ljava/lang/String;Lcom/parse/entity/mime/content/ContentBody;)V
    //   166: goto -43 -> 123
    //   169: astore 13
    //   171: new 205	java/lang/RuntimeException
    //   174: dup
    //   175: aload 13
    //   177: invokevirtual 209	java/io/UnsupportedEncodingException:getMessage	()Ljava/lang/String;
    //   180: invokespecial 210	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   183: athrow
    //   184: astore_3
    //   185: new 205	java/lang/RuntimeException
    //   188: dup
    //   189: aload_3
    //   190: invokevirtual 211	org/json/JSONException:getMessage	()Ljava/lang/String;
    //   193: invokespecial 210	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   196: athrow
    //   197: astore 8
    //   199: new 205	java/lang/RuntimeException
    //   202: dup
    //   203: aload 8
    //   205: invokevirtual 209	java/io/UnsupportedEncodingException:getMessage	()Ljava/lang/String;
    //   208: invokespecial 210	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   211: athrow
    //   212: astore 12
    //   214: new 205	java/lang/RuntimeException
    //   217: dup
    //   218: aload 12
    //   220: invokevirtual 211	org/json/JSONException:getMessage	()Ljava/lang/String;
    //   223: invokespecial 210	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   226: athrow
    //   227: aload 5
    //   229: ldc 213
    //   231: new 215	com/parse/entity/mime/content/ByteArrayBody
    //   234: dup
    //   235: aload_0
    //   236: getfield 79	com/parse/ParseFile:data	[B
    //   239: aload 7
    //   241: ldc 213
    //   243: invokespecial 218	com/parse/entity/mime/content/ByteArrayBody:<init>	([BLjava/lang/String;Ljava/lang/String;)V
    //   246: invokevirtual 189	com/parse/CountingMultipartEntity:addPart	(Ljava/lang/String;Lcom/parse/entity/mime/content/ContentBody;)V
    //   249: aload_0
    //   250: new 220	org/apache/http/client/methods/HttpPost
    //   253: dup
    //   254: aload_1
    //   255: ldc 222
    //   257: invokevirtual 141	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   260: invokespecial 223	org/apache/http/client/methods/HttpPost:<init>	(Ljava/lang/String;)V
    //   263: putfield 37	com/parse/ParseFile:fileUploadPost	Lorg/apache/http/client/methods/HttpPost;
    //   266: aload_0
    //   267: getfield 37	com/parse/ParseFile:fileUploadPost	Lorg/apache/http/client/methods/HttpPost;
    //   270: aload 5
    //   272: invokevirtual 227	org/apache/http/client/methods/HttpPost:setEntity	(Lorg/apache/http/HttpEntity;)V
    //   275: return
    //   276: astore 10
    //   278: new 205	java/lang/RuntimeException
    //   281: dup
    //   282: aload 10
    //   284: invokevirtual 211	org/json/JSONException:getMessage	()Ljava/lang/String;
    //   287: invokespecial 210	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   290: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	291	0	this	ParseFile
    //   0	291	1	paramJSONObject	JSONObject
    //   0	291	2	paramProgressCallback	ProgressCallback
    //   184	6	3	localJSONException1	org.json.JSONException
    //   26	128	4	localJSONObject	JSONObject
    //   39	232	5	localCountingMultipartEntity	CountingMultipartEntity
    //   50	9	6	i	int
    //   53	187	7	str1	String
    //   197	7	8	localUnsupportedEncodingException1	java.io.UnsupportedEncodingException
    //   121	13	9	localIterator	Iterator
    //   276	7	10	localJSONException2	org.json.JSONException
    //   143	13	11	str2	String
    //   212	7	12	localJSONException3	org.json.JSONException
    //   169	7	13	localUnsupportedEncodingException2	java.io.UnsupportedEncodingException
    //   79	6	14	str3	String
    // Exception table:
    //   from	to	target	type
    //   145	166	169	java/io/UnsupportedEncodingException
    //   0	28	184	org/json/JSONException
    //   100	116	197	java/io/UnsupportedEncodingException
    //   145	166	212	org/json/JSONException
    //   249	266	276	org/json/JSONException
  }
  
  private void revertState()
  {
    setState(this.previousState);
  }
  
  private void save(boolean paramBoolean, ProgressCallback paramProgressCallback)
    throws ParseException
  {
    if (paramBoolean)
    {
      assertNotRunning();
      if (this.state != ParseFileState.DIRTY) {
        return;
      }
      setState(ParseFileState.SAVING);
    }
    try
    {
      handleFileUploadResult((JSONObject)constructFileUploadCommand().perform(), paramProgressCallback);
      return;
    }
    catch (ParseException localParseException)
    {
      revertState();
      throw localParseException;
    }
  }
  
  private void setState(ParseFileState paramParseFileState)
  {
    try
    {
      if ((paramParseFileState != ParseFileState.SAVING) && (paramParseFileState != ParseFileState.FETCHING))
      {
        this.currentTask = null;
        this.currentCommand = null;
      }
      if (this.state != paramParseFileState)
      {
        this.previousState = this.state;
        this.state = paramParseFileState;
      }
      return;
    }
    finally {}
  }
  
  /* Error */
  protected void assertNotRunning()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 111	com/parse/ParseFile:state	Lcom/parse/ParseFile$ParseFileState;
    //   6: getstatic 114	com/parse/ParseFile$ParseFileState:SAVING	Lcom/parse/ParseFile$ParseFileState;
    //   9: if_acmpeq +13 -> 22
    //   12: aload_0
    //   13: getfield 111	com/parse/ParseFile:state	Lcom/parse/ParseFile$ParseFileState;
    //   16: getstatic 245	com/parse/ParseFile$ParseFileState:FETCHING	Lcom/parse/ParseFile$ParseFileState;
    //   19: if_acmpne +18 -> 37
    //   22: new 205	java/lang/RuntimeException
    //   25: dup
    //   26: ldc 247
    //   28: invokespecial 210	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   31: athrow
    //   32: astore_1
    //   33: aload_0
    //   34: monitorexit
    //   35: aload_1
    //   36: athrow
    //   37: aload_0
    //   38: monitorexit
    //   39: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	40	0	this	ParseFile
    //   32	4	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	22	32	finally
    //   22	32	32	finally
    //   33	35	32	finally
    //   37	39	32	finally
  }
  
  public void cancel()
  {
    if (this.fileUploadPost != null) {
      this.fileUploadPost.abort();
    }
    if (this.currentCommand != null) {
      this.currentCommand.cancel();
    }
    if (this.currentTask != null) {
      this.currentTask.cancel(true);
    }
    this.callbacks.clear();
    revertState();
  }
  
  public byte[] getData()
    throws ParseException
  {
    return getData(true, null);
  }
  
  protected byte[] getData(boolean paramBoolean, ProgressCallback paramProgressCallback)
    throws ParseException
  {
    if (isDataAvailable()) {
      return this.data;
    }
    if (paramBoolean)
    {
      assertNotRunning();
      setState(ParseFileState.FETCHING);
    }
    try
    {
      this.data = new ParseRequestRetryer(new HttpGet(this.url), 1000L, 5).go(paramProgressCallback);
      setState(ParseFileState.DATA_AVAILABLE);
      return this.data;
    }
    catch (IllegalStateException localIllegalStateException)
    {
      revertState();
      throw new ParseException(100, localIllegalStateException.getMessage());
    }
    catch (ParseException localParseException)
    {
      revertState();
      throw localParseException;
    }
  }
  
  public void getDataInBackground(GetDataCallback paramGetDataCallback)
  {
    getDataInBackground(paramGetDataCallback, null);
  }
  
  public void getDataInBackground(GetDataCallback paramGetDataCallback, final ProgressCallback paramProgressCallback)
  {
    for (;;)
    {
      try
      {
        if (isDataAvailable())
        {
          paramGetDataCallback.done(this.data, null);
          return;
        }
        if (this.state == ParseFileState.FETCHING)
        {
          this.callbacks.add(paramGetDataCallback);
          continue;
        }
        assertNotRunning();
      }
      finally {}
      setState(ParseFileState.FETCHING);
      this.callbacks.add(paramGetDataCallback);
      BackgroundTask.executeTask(new BackgroundTask(new GetDataCallback()
      {
        public void done(byte[] paramAnonymousArrayOfByte, ParseException paramAnonymousParseException)
        {
          Iterator localIterator = ParseFile.this.callbacks.iterator();
          while (localIterator.hasNext()) {
            ((GetDataCallback)localIterator.next()).done(paramAnonymousArrayOfByte, paramAnonymousParseException);
          }
          ParseFile.this.callbacks.clear();
        }
      })
      {
        public byte[] run()
          throws ParseException
        {
          return ParseFile.this.getData(false, paramProgressCallback);
        }
      });
    }
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public String getUrl()
  {
    return this.url;
  }
  
  public boolean isDataAvailable()
  {
    return (this.state != ParseFileState.AWAITING_FETCH) && (this.state != ParseFileState.FETCHING);
  }
  
  public boolean isDirty()
  {
    return this.state == ParseFileState.DIRTY;
  }
  
  public void save()
    throws ParseException
  {
    save(true, null);
  }
  
  public void saveInBackground()
  {
    saveInBackground(null);
  }
  
  public void saveInBackground(SaveCallback paramSaveCallback)
  {
    saveInBackground(paramSaveCallback, null);
  }
  
  /* Error */
  public void saveInBackground(SaveCallback paramSaveCallback, final ProgressCallback paramProgressCallback)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: invokevirtual 233	com/parse/ParseFile:assertNotRunning	()V
    //   6: aload_0
    //   7: getfield 111	com/parse/ParseFile:state	Lcom/parse/ParseFile$ParseFileState;
    //   10: getstatic 82	com/parse/ParseFile$ParseFileState:DIRTY	Lcom/parse/ParseFile$ParseFileState;
    //   13: if_acmpeq +11 -> 24
    //   16: aload_1
    //   17: aconst_null
    //   18: invokevirtual 322	com/parse/SaveCallback:done	(Lcom/parse/ParseException;)V
    //   21: aload_0
    //   22: monitorexit
    //   23: return
    //   24: aload_0
    //   25: getstatic 114	com/parse/ParseFile$ParseFileState:SAVING	Lcom/parse/ParseFile$ParseFileState;
    //   28: invokespecial 51	com/parse/ParseFile:setState	(Lcom/parse/ParseFile$ParseFileState;)V
    //   31: new 324	com/parse/ParseFile$1
    //   34: dup
    //   35: aload_0
    //   36: aload_1
    //   37: aload_2
    //   38: invokespecial 325	com/parse/ParseFile$1:<init>	(Lcom/parse/ParseFile;Lcom/parse/ParseCallback;Lcom/parse/ProgressCallback;)V
    //   41: astore 4
    //   43: aload_0
    //   44: aload 4
    //   46: putfield 33	com/parse/ParseFile:currentTask	Lcom/parse/BackgroundTask;
    //   49: aload 4
    //   51: invokestatic 307	com/parse/BackgroundTask:executeTask	(Lcom/parse/BackgroundTask;)I
    //   54: pop
    //   55: goto -34 -> 21
    //   58: astore_3
    //   59: aload_0
    //   60: monitorexit
    //   61: aload_3
    //   62: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	63	0	this	ParseFile
    //   0	63	1	paramSaveCallback	SaveCallback
    //   0	63	2	paramProgressCallback	ProgressCallback
    //   58	4	3	localObject	Object
    //   41	9	4	local1	1
    // Exception table:
    //   from	to	target	type
    //   2	21	58	finally
    //   24	55	58	finally
  }
  
  private static enum ParseFileState
  {
    static
    {
      AWAITING_FETCH = new ParseFileState("AWAITING_FETCH", 2);
      FETCHING = new ParseFileState("FETCHING", 3);
      DATA_AVAILABLE = new ParseFileState("DATA_AVAILABLE", 4);
      ParseFileState[] arrayOfParseFileState = new ParseFileState[5];
      arrayOfParseFileState[0] = DIRTY;
      arrayOfParseFileState[1] = SAVING;
      arrayOfParseFileState[2] = AWAITING_FETCH;
      arrayOfParseFileState[3] = FETCHING;
      arrayOfParseFileState[4] = DATA_AVAILABLE;
      $VALUES = arrayOfParseFileState;
    }
    
    private ParseFileState() {}
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.ParseFile
 * JD-Core Version:    0.7.0.1
 */