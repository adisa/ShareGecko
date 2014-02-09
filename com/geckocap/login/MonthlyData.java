package com.geckocap.login;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MonthlyData
  extends Activity
{
  private TextView curData;
  private int offset = 0;
  
  private String setUrl()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    GregorianCalendar localGregorianCalendar = new GregorianCalendar();
    localGregorianCalendar.add(5, -7);
    localStringBuffer.append("http://54.243.194.63/parents/data_endpoint_monthly/");
    localStringBuffer.append(Integer.toString(localGregorianCalendar.get(1)));
    StringBuilder localStringBuilder1 = new StringBuilder("-");
    Object[] arrayOfObject1 = new Object[1];
    arrayOfObject1[0] = Integer.valueOf(1 + localGregorianCalendar.get(2));
    localStringBuffer.append(String.format("%02d", arrayOfObject1));
    StringBuilder localStringBuilder2 = new StringBuilder("-");
    Object[] arrayOfObject2 = new Object[1];
    arrayOfObject2[0] = Integer.valueOf(localGregorianCalendar.get(5));
    localStringBuffer.append(String.format("%02d", arrayOfObject2));
    localGregorianCalendar.add(5, -27);
    localStringBuffer.append("_" + Integer.toString(localGregorianCalendar.get(1)));
    StringBuilder localStringBuilder3 = new StringBuilder("-");
    Object[] arrayOfObject3 = new Object[1];
    arrayOfObject3[0] = Integer.valueOf(1 + localGregorianCalendar.get(2));
    localStringBuffer.append(String.format("%02d", arrayOfObject3));
    StringBuilder localStringBuilder4 = new StringBuilder("-");
    Object[] arrayOfObject4 = new Object[1];
    arrayOfObject4[0] = Integer.valueOf(localGregorianCalendar.get(5));
    localStringBuffer.append(String.format("%02d", arrayOfObject4));
    localStringBuffer.append("/daily_data");
    return localStringBuffer.toString();
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130903054);
    this.curData = ((TextView)findViewById(2131296318));
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.geckocap.login.MonthlyData
 * JD-Core Version:    0.7.0.1
 */