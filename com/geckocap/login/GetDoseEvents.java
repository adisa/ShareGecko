package com.geckocap.login;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetDoseEvents
  extends Activity
{
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130903074);
    GregorianCalendar localGregorianCalendar = new GregorianCalendar();
    String str1 = Integer.toString(localGregorianCalendar.get(1));
    StringBuilder localStringBuilder1 = new StringBuilder("-");
    Object[] arrayOfObject1 = new Object[1];
    arrayOfObject1[0] = Integer.valueOf(1 + localGregorianCalendar.get(2));
    String str2 = String.format("%02d", arrayOfObject1);
    StringBuilder localStringBuilder2 = new StringBuilder("-");
    Object[] arrayOfObject2 = new Object[1];
    arrayOfObject2[0] = Integer.valueOf(localGregorianCalendar.get(5));
    String str3 = String.format("%02d", arrayOfObject2);
    String str4 = "http://54.243.194.63/parents/data_endpoint/" + str1 + str2 + str3 + "/dose_events";
    JSONArray localJSONArray = new JSONParser().getJSONFromUrl(str4);
    if (localJSONArray == null) {
      Toast.makeText(getApplicationContext(), "null :(", 0).show();
    }
    for (;;)
    {
      return;
      int i = 0;
      try
      {
        while (i < localJSONArray.length())
        {
          JSONObject localJSONObject = localJSONArray.getJSONObject(i);
          String str5 = localJSONObject.getString("timestamp");
          String str6 = localJSONObject.getString("type");
          Toast.makeText(getApplicationContext(), str6 + "," + str5, 0).show();
          i++;
        }
        return;
      }
      catch (JSONException localJSONException)
      {
        localJSONException.printStackTrace();
      }
    }
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.geckocap.login.GetDoseEvents
 * JD-Core Version:    0.7.0.1
 */