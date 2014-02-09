package com.geckocap.login;

import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;

public class JSONParser
{
  static InputStream input = null;
  static JSONArray jArr = null;
  static String json = "";
  
  /* Error */
  public JSONArray getJSONFromUrl(String paramString)
  {
    // Byte code:
    //   0: new 37	org/apache/http/impl/client/DefaultHttpClient
    //   3: dup
    //   4: invokespecial 38	org/apache/http/impl/client/DefaultHttpClient:<init>	()V
    //   7: new 40	org/apache/http/client/methods/HttpPost
    //   10: dup
    //   11: aload_1
    //   12: invokespecial 43	org/apache/http/client/methods/HttpPost:<init>	(Ljava/lang/String;)V
    //   15: invokevirtual 47	org/apache/http/impl/client/DefaultHttpClient:execute	(Lorg/apache/http/client/methods/HttpUriRequest;)Lorg/apache/http/HttpResponse;
    //   18: invokeinterface 53 1 0
    //   23: invokeinterface 59 1 0
    //   28: putstatic 14	com/geckocap/login/JSONParser:input	Ljava/io/InputStream;
    //   31: new 61	java/io/BufferedReader
    //   34: dup
    //   35: new 63	java/io/InputStreamReader
    //   38: dup
    //   39: getstatic 14	com/geckocap/login/JSONParser:input	Ljava/io/InputStream;
    //   42: ldc 65
    //   44: invokespecial 68	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;Ljava/lang/String;)V
    //   47: bipush 8
    //   49: invokespecial 71	java/io/BufferedReader:<init>	(Ljava/io/Reader;I)V
    //   52: astore_3
    //   53: new 73	java/lang/StringBuilder
    //   56: dup
    //   57: invokespecial 74	java/lang/StringBuilder:<init>	()V
    //   60: astore 4
    //   62: aload_3
    //   63: invokevirtual 78	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   66: astore 9
    //   68: aload 9
    //   70: ifnonnull +71 -> 141
    //   73: getstatic 14	com/geckocap/login/JSONParser:input	Ljava/io/InputStream;
    //   76: invokevirtual 83	java/io/InputStream:close	()V
    //   79: aload 4
    //   81: invokevirtual 86	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   84: putstatic 20	com/geckocap/login/JSONParser:json	Ljava/lang/String;
    //   87: ldc 88
    //   89: getstatic 20	com/geckocap/login/JSONParser:json	Ljava/lang/String;
    //   92: invokestatic 94	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   95: pop
    //   96: new 96	org/json/JSONArray
    //   99: dup
    //   100: getstatic 20	com/geckocap/login/JSONParser:json	Ljava/lang/String;
    //   103: invokespecial 97	org/json/JSONArray:<init>	(Ljava/lang/String;)V
    //   106: putstatic 16	com/geckocap/login/JSONParser:jArr	Lorg/json/JSONArray;
    //   109: getstatic 16	com/geckocap/login/JSONParser:jArr	Lorg/json/JSONArray;
    //   112: areturn
    //   113: astore 13
    //   115: aload 13
    //   117: invokevirtual 100	java/io/UnsupportedEncodingException:printStackTrace	()V
    //   120: goto -89 -> 31
    //   123: astore 12
    //   125: aload 12
    //   127: invokevirtual 101	org/apache/http/client/ClientProtocolException:printStackTrace	()V
    //   130: goto -99 -> 31
    //   133: astore_2
    //   134: aload_2
    //   135: invokevirtual 102	java/io/IOException:printStackTrace	()V
    //   138: goto -107 -> 31
    //   141: aload 4
    //   143: aload 9
    //   145: invokevirtual 106	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   148: pop
    //   149: goto -87 -> 62
    //   152: astore 5
    //   154: ldc 108
    //   156: new 73	java/lang/StringBuilder
    //   159: dup
    //   160: ldc 110
    //   162: invokespecial 111	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   165: aload 5
    //   167: invokevirtual 112	java/lang/Exception:toString	()Ljava/lang/String;
    //   170: invokevirtual 106	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   173: invokevirtual 86	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   176: invokestatic 94	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   179: pop
    //   180: goto -84 -> 96
    //   183: astore 7
    //   185: ldc 114
    //   187: new 73	java/lang/StringBuilder
    //   190: dup
    //   191: ldc 116
    //   193: invokespecial 111	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   196: aload 7
    //   198: invokevirtual 117	org/json/JSONException:toString	()Ljava/lang/String;
    //   201: invokevirtual 106	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   204: invokevirtual 86	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   207: invokestatic 94	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   210: pop
    //   211: goto -102 -> 109
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	214	0	this	JSONParser
    //   0	214	1	paramString	String
    //   133	2	2	localIOException	IOException
    //   52	11	3	localBufferedReader	BufferedReader
    //   60	82	4	localStringBuilder	StringBuilder
    //   152	14	5	localException	Exception
    //   183	14	7	localJSONException	org.json.JSONException
    //   66	78	9	str	String
    //   123	3	12	localClientProtocolException	ClientProtocolException
    //   113	3	13	localUnsupportedEncodingException	UnsupportedEncodingException
    // Exception table:
    //   from	to	target	type
    //   0	31	113	java/io/UnsupportedEncodingException
    //   0	31	123	org/apache/http/client/ClientProtocolException
    //   0	31	133	java/io/IOException
    //   31	62	152	java/lang/Exception
    //   62	68	152	java/lang/Exception
    //   73	96	152	java/lang/Exception
    //   141	149	152	java/lang/Exception
    //   96	109	183	org/json/JSONException
  }
  
  public String getStringFromUrl(String paramString)
  {
    try
    {
      input = new DefaultHttpClient().execute(new HttpPost(paramString)).getEntity().getContent();
      try
      {
        BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(input, "iso-8859-1"), 8);
        localStringBuilder = new StringBuilder();
        str = localBufferedReader.readLine();
        if (str != null) {
          break label128;
        }
        input.close();
        json = localStringBuilder.toString();
        Log.e("JSON", json);
      }
      catch (Exception localException)
      {
        for (;;)
        {
          StringBuilder localStringBuilder;
          String str;
          Log.e("Buffer Error", "Error converting result " + localException.toString());
        }
      }
      return json;
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      for (;;)
      {
        localUnsupportedEncodingException.printStackTrace();
      }
    }
    catch (ClientProtocolException localClientProtocolException)
    {
      for (;;)
      {
        localClientProtocolException.printStackTrace();
      }
    }
    catch (IOException localIOException)
    {
      for (;;)
      {
        localIOException.printStackTrace();
        continue;
        label128:
        localStringBuilder.append(str);
      }
    }
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.geckocap.login.JSONParser
 * JD-Core Version:    0.7.0.1
 */