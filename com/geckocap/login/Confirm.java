package com.geckocap.login;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class Confirm
  extends Activity
{
  private LinearLayout confirmPage;
  LayoutInflater inflater;
  
  private void showCap(String paramString1, String paramString2)
  {
    SharedPreferences localSharedPreferences = getSharedPreferences(paramString1 + paramString2, 0);
    View localView = this.inflater.inflate(2130903063, null);
    ((TextView)localView.findViewById(2131296380)).setText(localSharedPreferences.getString("serialNum", ""));
    TextView localTextView1 = (TextView)localView.findViewById(2131296382);
    if (localSharedPreferences.getBoolean("rescue", false))
    {
      ImageView localImageView1 = (ImageView)localView.findViewById(2131296378);
      TextView localTextView2 = (TextView)localView.findViewById(2131296379);
      ImageView localImageView2 = (ImageView)localView.findViewById(2131296381);
      localImageView1.setImageResource(2130837570);
      localTextView2.setText("Rescue");
      localImageView2.setImageResource(2130837569);
      localTextView1.setText("You should now place the red GeckoCap on " + paramString1 + "'s Rescue inhaler, " + paramString2);
    }
    for (;;)
    {
      this.confirmPage.addView(localView);
      return;
      localTextView1.setText("You should now place the green GeckoCap on " + paramString1 + "'s Maintenance inhaler, " + paramString2);
    }
  }
  
  private void showPatient(String paramString)
  {
    View localView = this.inflater.inflate(2130903068, null);
    TextView localTextView1 = (TextView)localView.findViewById(2131296390);
    TextView localTextView2 = (TextView)localView.findViewById(2131296391);
    TextView localTextView3 = (TextView)localView.findViewById(2131296392);
    SharedPreferences localSharedPreferences = getSharedPreferences(paramString + "Dob", 0);
    String str1 = localSharedPreferences.getString("month", "");
    String str2 = localSharedPreferences.getString("day", "");
    String str3 = localSharedPreferences.getString("year", "");
    localTextView1.setText(paramString);
    localTextView2.setText("DOB: " + str1 + "/" + str2 + "/" + str3);
    localTextView3.setVisibility(8);
    this.confirmPage.addView(localView);
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130903045);
    this.confirmPage = ((LinearLayout)findViewById(2131296296));
    this.inflater = ((LayoutInflater)getSystemService("layout_inflater"));
    Iterator localIterator1 = AddGeckoCap.geckoCaps.entrySet().iterator();
    for (;;)
    {
      if (!localIterator1.hasNext())
      {
        View localView = this.inflater.inflate(2130903046, null);
        this.confirmPage.addView(localView);
        return;
      }
      String str = (String)((Map.Entry)localIterator1.next()).getKey();
      showPatient(str);
      Iterator localIterator2 = ((ArrayList)AddGeckoCap.geckoCaps.get(str)).iterator();
      while (localIterator2.hasNext()) {
        showCap(str, (String)localIterator2.next());
      }
    }
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.geckocap.login.Confirm
 * JD-Core Version:    0.7.0.1
 */