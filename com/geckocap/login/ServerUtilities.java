package com.geckocap.login;

import android.content.Context;
import android.util.Log;
import com.google.android.gcm.GCMRegistrar;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

public class ServerUtilities
{
  private static final int BACKOFF_MILLI_SECONDS = 2000;
  private static final int MAX_ATTEMPTS = 5;
  private static final Random random = new Random();
  
  private static void post(String paramString, Map<String, String> paramMap)
    throws IOException
  {
    HttpURLConnection localHttpURLConnection;
    for (;;)
    {
      StringBuilder localStringBuilder;
      Iterator localIterator;
      try
      {
        URL localURL = new URL(paramString);
        localStringBuilder = new StringBuilder();
        localIterator = paramMap.entrySet().iterator();
        if (!localIterator.hasNext())
        {
          String str = localStringBuilder.toString();
          Log.v("GeckoCap", "Posting '" + str + "' to " + localURL);
          byte[] arrayOfByte = str.getBytes();
          localHttpURLConnection = null;
          try
          {
            localHttpURLConnection = (HttpURLConnection)localURL.openConnection();
            localHttpURLConnection.setDoOutput(true);
            localHttpURLConnection.setUseCaches(false);
            localHttpURLConnection.setFixedLengthStreamingMode(arrayOfByte.length);
            localHttpURLConnection.setRequestMethod("POST");
            localHttpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            OutputStream localOutputStream = localHttpURLConnection.getOutputStream();
            localOutputStream.write(arrayOfByte);
            localOutputStream.close();
            int i = localHttpURLConnection.getResponseCode();
            if (i == 200) {
              break;
            }
            throw new IOException("Post failed with error code " + i);
          }
          finally
          {
            if (localHttpURLConnection != null) {
              localHttpURLConnection.disconnect();
            }
          }
        }
        localEntry = (Map.Entry)localIterator.next();
      }
      catch (MalformedURLException localMalformedURLException)
      {
        throw new IllegalArgumentException("invalid url: " + paramString);
      }
      Map.Entry localEntry;
      localStringBuilder.append((String)localEntry.getKey()).append('=').append((String)localEntry.getValue());
      if (localIterator.hasNext()) {
        localStringBuilder.append('&');
      }
    }
    if (localHttpURLConnection != null) {
      localHttpURLConnection.disconnect();
    }
  }
  
  static boolean register(Context paramContext, String paramString)
  {
    Log.i("GeckoCap", "registering device (regId = " + paramString + ")");
    HashMap localHashMap = new HashMap();
    localHashMap.put("regId", paramString);
    long l = 2000 + random.nextInt(1000);
    int i = 1;
    for (;;)
    {
      if (i > 5) {}
      do
      {
        return false;
        Log.d("GeckoCap", "Attempt #" + i + " to register");
        try
        {
          CommonUtilities.displayMessage(paramContext, "Attempt " + i + " out of " + 5 + " to register");
          post("http://192.168.1.14:8080/gcm-demo/register", localHashMap);
          GCMRegistrar.setRegisteredOnServer(paramContext, true);
          CommonUtilities.displayMessage(paramContext, "Registered device!");
          return true;
        }
        catch (IOException localIOException)
        {
          Log.e("GeckoCap", "Failed to register on attempt " + i, localIOException);
        }
      } while (i == 5);
      try
      {
        Log.d("GeckoCap", "Sleeping for " + l + " ms before retry");
        Thread.sleep(l);
        l *= 2L;
        i++;
      }
      catch (InterruptedException localInterruptedException)
      {
        Log.d("GeckoCap", "Thread interrupted");
        Thread.currentThread().interrupt();
      }
    }
    return false;
  }
  
  static void unregister(Context paramContext, String paramString)
  {
    Log.i("GeckoCap", "unregistering device (regId = " + paramString + ")");
    HashMap localHashMap = new HashMap();
    localHashMap.put("regId", paramString);
    try
    {
      post("http://192.168.1.14:8080/gcm-demo/unregister", localHashMap);
      GCMRegistrar.setRegisteredOnServer(paramContext, false);
      CommonUtilities.displayMessage(paramContext, "Device unregistered from server");
      return;
    }
    catch (IOException localIOException)
    {
      Log.e("GeckoCap", "device not unregistered from server: " + localIOException.getMessage());
    }
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.geckocap.login.ServerUtilities
 * JD-Core Version:    0.7.0.1
 */