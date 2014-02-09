package com.parse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;

class ParseRequestRetryer
{
  public static HttpClient testClient = null;
  private int attemptsMade = 0;
  private HttpClient client;
  private long delay;
  private int maxAttempts;
  private HttpUriRequest request;
  
  public ParseRequestRetryer(HttpUriRequest paramHttpUriRequest, long paramLong, int paramInt)
  {
    if (testClient != null)
    {
      this.client = testClient;
      paramLong = 1L;
    }
    for (;;)
    {
      this.request = paramHttpUriRequest;
      this.maxAttempts = paramInt;
      this.delay = (paramLong + (paramLong * Math.random()));
      return;
      this.client = new DefaultHttpClient();
      this.client.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
    }
  }
  
  private byte[] sendOneRequest(ProgressCallback paramProgressCallback)
    throws ParseException
  {
    HttpResponse localHttpResponse;
    try
    {
      localHttpResponse = this.client.execute(this.request);
      ByteArrayOutputStream localByteArrayOutputStream;
      if (this.request.getMethod().equals("GET"))
      {
        int i = -1;
        Header[] arrayOfHeader = localHttpResponse.getHeaders("Content-Length");
        if (arrayOfHeader.length > 0) {
          i = Integer.parseInt(arrayOfHeader[0].getValue());
        }
        int j = 0;
        InputStream localInputStream = localHttpResponse.getEntity().getContent();
        localByteArrayOutputStream = new ByteArrayOutputStream();
        byte[] arrayOfByte1 = new byte[32768];
        for (;;)
        {
          int k = localInputStream.read(arrayOfByte1, 0, arrayOfByte1.length);
          if (k == -1) {
            break;
          }
          localByteArrayOutputStream.write(arrayOfByte1, 0, k);
          j += k;
          if ((paramProgressCallback != null) && (i != -1)) {
            BackgroundTask.executeTask(new BackgroundTask(paramProgressCallback)
            {
              public Integer run()
                throws ParseException
              {
                return Integer.valueOf(this.val$progressToReport);
              }
            });
          }
        }
      }
      byte[] arrayOfByte2;
      if (localHttpResponse.getStatusLine().getStatusCode() / 100 == 2) {
        break label292;
      }
    }
    catch (ClientProtocolException localClientProtocolException)
    {
      this.client.getConnectionManager().shutdown();
      throw connectionFailed("bad protocol", localClientProtocolException);
      localByteArrayOutputStream.flush();
      arrayOfByte2 = localByteArrayOutputStream.toByteArray();
      return arrayOfByte2;
    }
    catch (IOException localIOException)
    {
      this.client.getConnectionManager().shutdown();
      throw connectionFailed("i/o failure", localIOException);
    }
    Object[] arrayOfObject = new Object[1];
    arrayOfObject[0] = localHttpResponse.getStatusLine().getReasonPhrase();
    throw new ParseException(100, String.format("Upload to S3 failed. %s", arrayOfObject));
    label292:
    return null;
  }
  
  ParseException connectionFailed(String paramString, Exception paramException)
  {
    return new ParseException(100, paramString + ": " + paramException.getClass().getName() + ": " + paramException.getMessage());
  }
  
  public byte[] go(ProgressCallback paramProgressCallback)
    throws ParseException
  {
    for (;;)
    {
      byte[] arrayOfByte1;
      try
      {
        byte[] arrayOfByte2 = sendOneRequest(paramProgressCallback);
        arrayOfByte1 = arrayOfByte2;
        return arrayOfByte1;
      }
      catch (ParseException localParseException)
      {
        this.attemptsMade = (1 + this.attemptsMade);
        if (this.attemptsMade >= this.maxAttempts) {
          break label104;
        }
      }
      Parse.logI("com.parse.ParseRequestRetryer", "Request failed. Waiting " + this.delay + " milliseconds before attempt #" + (1 + this.attemptsMade));
      try
      {
        Thread.sleep(this.delay);
        label84:
        this.delay = (2L * this.delay);
        go(paramProgressCallback);
        return null;
        label104:
        boolean bool = this.request.isAborted();
        arrayOfByte1 = null;
        if (bool) {
          continue;
        }
        Parse.logI("com.parse.ParseRequestRetryer", "Request failed. Giving up.");
        throw localParseException;
      }
      catch (InterruptedException localInterruptedException)
      {
        break label84;
      }
    }
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.ParseRequestRetryer
 * JD-Core Version:    0.7.0.1
 */