package com.parse;

import com.parse.gdata.Preconditions;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ParsePush
{
  private static final String TAG = "com.parse.ParsePush";
  private Set<String> mChannelSet = null;
  private JSONObject mData;
  private Long mExpirationTime = null;
  private Long mExpirationTimeInterval = null;
  private Boolean mPushToAndroid = null;
  private Boolean mPushToIOS = null;
  private ParseQuery mQuery = null;
  
  public static void sendDataInBackground(JSONObject paramJSONObject, ParseQuery paramParseQuery)
  {
    sendDataInBackground(paramJSONObject, paramParseQuery, null);
  }
  
  public static void sendDataInBackground(JSONObject paramJSONObject, ParseQuery paramParseQuery, SendCallback paramSendCallback)
  {
    ParsePush localParsePush = new ParsePush();
    localParsePush.setQuery(paramParseQuery);
    localParsePush.setData(paramJSONObject);
    localParsePush.sendInBackground(paramSendCallback);
  }
  
  public static void sendMessageInBackground(String paramString, ParseQuery paramParseQuery)
  {
    sendMessageInBackground(paramString, paramParseQuery, null);
  }
  
  public static void sendMessageInBackground(String paramString, ParseQuery paramParseQuery, SendCallback paramSendCallback)
  {
    ParsePush localParsePush = new ParsePush();
    localParsePush.setQuery(paramParseQuery);
    localParsePush.setMessage(paramString);
    localParsePush.sendInBackground(paramSendCallback);
  }
  
  ParseCommand buildCommand()
  {
    ParseCommand localParseCommand = new ParseCommand("client_push");
    if (this.mData == null) {
      throw new IllegalArgumentException("Cannot send a push without calling either setMessage or setData");
    }
    localParseCommand.put("data", this.mData);
    label82:
    int i;
    if (this.mQuery != null)
    {
      localParseCommand.put("where", this.mQuery.getFindParams().optJSONObject("data"));
      if (this.mExpirationTime == null) {
        break label175;
      }
      localParseCommand.put("expiration_time", this.mExpirationTime.longValue());
      if (this.mQuery == null)
      {
        if ((this.mPushToAndroid != null) && (!this.mPushToAndroid.booleanValue())) {
          break label198;
        }
        i = 1;
        label108:
        if ((this.mPushToIOS == null) || (!this.mPushToIOS.booleanValue())) {
          break label203;
        }
      }
    }
    label175:
    label198:
    label203:
    for (int j = 1;; j = 0)
    {
      if ((j == 0) || (i == 0)) {
        break label208;
      }
      return localParseCommand;
      if (this.mChannelSet == null)
      {
        localParseCommand.put("channel", "");
        break;
      }
      localParseCommand.put("channels", new JSONArray(this.mChannelSet));
      break;
      if (this.mExpirationTimeInterval == null) {
        break label82;
      }
      localParseCommand.put("expiration_time_interval", this.mExpirationTimeInterval.longValue());
      break label82;
      i = 0;
      break label108;
    }
    label208:
    if (j != 0)
    {
      localParseCommand.put("type", "ios");
      return localParseCommand;
    }
    if (i != 0)
    {
      localParseCommand.put("type", "android");
      return localParseCommand;
    }
    throw new IllegalArgumentException("Cannot push if both pushToIOS and pushToAndroid are false");
  }
  
  public void clearExpiration()
  {
    this.mExpirationTime = null;
    this.mExpirationTimeInterval = null;
  }
  
  public void send()
    throws ParseException
  {
    buildCommand().perform();
  }
  
  public void sendInBackground()
  {
    sendInBackground(null);
  }
  
  public void sendInBackground(SendCallback paramSendCallback)
  {
    BackgroundTask.executeTask(new BackgroundTask(paramSendCallback)
    {
      public Void run()
        throws ParseException
      {
        ParsePush.this.send();
        return null;
      }
    });
  }
  
  public void setChannel(String paramString)
  {
    if (paramString != null) {}
    for (boolean bool = true;; bool = false)
    {
      Preconditions.checkArgument(bool, "channel cannot be null");
      this.mChannelSet = new HashSet();
      this.mChannelSet.add(paramString);
      this.mQuery = null;
      return;
    }
  }
  
  public void setChannels(Collection<String> paramCollection)
  {
    boolean bool1;
    if (paramCollection != null)
    {
      bool1 = true;
      Preconditions.checkArgument(bool1, "channels collection cannot be null");
      Iterator localIterator = paramCollection.iterator();
      label19:
      if (!localIterator.hasNext()) {
        break label64;
      }
      if ((String)localIterator.next() == null) {
        break label58;
      }
    }
    label58:
    for (boolean bool2 = true;; bool2 = false)
    {
      Preconditions.checkArgument(bool2, "channel cannot be null");
      break label19;
      bool1 = false;
      break;
    }
    label64:
    this.mChannelSet = new HashSet();
    this.mChannelSet.addAll(paramCollection);
    this.mQuery = null;
  }
  
  public void setData(JSONObject paramJSONObject)
  {
    this.mData = paramJSONObject;
  }
  
  public void setExpirationTime(long paramLong)
  {
    this.mExpirationTime = Long.valueOf(paramLong);
    this.mExpirationTimeInterval = null;
  }
  
  public void setExpirationTimeInterval(long paramLong)
  {
    this.mExpirationTime = null;
    this.mExpirationTimeInterval = Long.valueOf(paramLong);
  }
  
  public void setMessage(String paramString)
  {
    JSONObject localJSONObject = new JSONObject();
    try
    {
      localJSONObject.put("alert", paramString);
      setData(localJSONObject);
      return;
    }
    catch (JSONException localJSONException)
    {
      for (;;)
      {
        Parse.logE("com.parse.ParsePush", "JSONException in setMessage", localJSONException);
      }
    }
  }
  
  public void setPushToAndroid(boolean paramBoolean)
  {
    if (this.mQuery == null) {}
    for (boolean bool = true;; bool = false)
    {
      Preconditions.checkArgument(bool, "Cannot set push targets (i.e. setPushToAndroid or setPushToIOS) when pushing to a query");
      this.mPushToAndroid = Boolean.valueOf(paramBoolean);
      return;
    }
  }
  
  public void setPushToIOS(boolean paramBoolean)
  {
    if (this.mQuery == null) {}
    for (boolean bool = true;; bool = false)
    {
      Preconditions.checkArgument(bool, "Cannot set push targets (i.e. setPushToAndroid or setPushToIOS) when pushing to a query");
      this.mPushToIOS = Boolean.valueOf(paramBoolean);
      return;
    }
  }
  
  public void setQuery(ParseQuery paramParseQuery)
  {
    boolean bool1 = true;
    boolean bool2;
    if (paramParseQuery != null)
    {
      bool2 = bool1;
      Preconditions.checkArgument(bool2, "Cannot target a null query");
      if ((this.mPushToIOS != null) || (this.mPushToAndroid != null)) {
        break label64;
      }
    }
    for (;;)
    {
      Preconditions.checkArgument(bool1, "Cannot set push targets (i.e. setPushToAndroid or setPushToIOS) when pushing to a query");
      Preconditions.checkArgument(paramParseQuery.getClassName().equals("_Installation"), "Can only push to a query for Installations");
      this.mChannelSet = null;
      this.mQuery = paramParseQuery;
      return;
      bool2 = false;
      break;
      label64:
      bool1 = false;
    }
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.ParsePush
 * JD-Core Version:    0.7.0.1
 */