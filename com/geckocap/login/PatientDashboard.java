package com.geckocap.login;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.json.JSONArray;

public class PatientDashboard
  extends Activity
{
  final int BADGES = 6;
  View.OnClickListener badgePopup = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      final Dialog localDialog = new Dialog(PatientDashboard.this.context);
      localDialog.requestWindowFeature(1);
      localDialog.setContentView(2130903043);
      ImageView localImageView = (ImageView)localDialog.findViewById(2131296293);
      ((TextView)localDialog.findViewById(2131296294));
      ((TextView)localDialog.findViewById(2131296295));
      localImageView.setImageDrawable(((ImageView)paramAnonymousView).getDrawable());
      ((Button)localDialog.findViewById(2131296283)).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymous2View)
        {
          localDialog.dismiss();
        }
      });
      localDialog.show();
    }
  };
  final Context context = this;
  private float density;
  private ImageView heartsView;
  private int progressStatus;
  private int size;
  private GetInhalerUses task;
  private ProgressBar time;
  private RelativeLayout timeMarks;
  private float timeWidth;
  private int topMargin;
  
  private void getInhalerUses()
  {
    this.task = new GetInhalerUses();
    GetInhalerUses localGetInhalerUses = this.task;
    String[] arrayOfString = new String[2];
    arrayOfString[0] = setUrl("/dose_events");
    arrayOfString[1] = setUrl("/hearts_pattern");
    localGetInhalerUses.execute(arrayOfString);
  }
  
  private void setProgressBar(boolean paramBoolean, int paramInt)
  {
    ImageView localImageView = new ImageView(this);
    RelativeLayout.LayoutParams localLayoutParams = new RelativeLayout.LayoutParams(this.size, this.size);
    localLayoutParams.setMargins((int)((float)(this.timeWidth * paramInt / 1440.0D) + 3 * this.size / 2), this.topMargin, 0, 0);
    if (paramBoolean)
    {
      localImageView.setImageResource(2130837577);
      this.timeMarks.addView(localImageView, localLayoutParams);
      return;
    }
    localImageView.setImageResource(2130837575);
    this.timeMarks.addView(localImageView, localLayoutParams);
  }
  
  private void setTime()
  {
    GregorianCalendar localGregorianCalendar = new GregorianCalendar();
    this.progressStatus = (100 * localGregorianCalendar.get(11) + localGregorianCalendar.get(12));
    this.time.setProgress(this.progressStatus);
  }
  
  private String setUrl(String paramString)
  {
    GregorianCalendar localGregorianCalendar = new GregorianCalendar();
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("http://54.243.194.63/parents/data_endpoint/");
    localStringBuffer.append(Integer.toString(localGregorianCalendar.get(1)));
    StringBuilder localStringBuilder1 = new StringBuilder("-");
    Object[] arrayOfObject1 = new Object[1];
    arrayOfObject1[0] = Integer.valueOf(1 + localGregorianCalendar.get(2));
    localStringBuffer.append(String.format("%02d", arrayOfObject1));
    StringBuilder localStringBuilder2 = new StringBuilder("-");
    Object[] arrayOfObject2 = new Object[1];
    arrayOfObject2[0] = Integer.valueOf(localGregorianCalendar.get(5));
    localStringBuffer.append(String.format("%02d", arrayOfObject2));
    localStringBuffer.append(paramString);
    return localStringBuffer.toString();
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130903060);
    TextView localTextView1 = (TextView)findViewById(2131296411);
    TextView localTextView2 = (TextView)findViewById(2131296410);
    TextView localTextView3 = (TextView)findViewById(2131296412);
    localTextView2.setBackgroundResource(2130837561);
    localTextView1.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        Intent localIntent = new Intent(PatientDashboard.this.getApplicationContext(), ParentDashboard.class);
        localIntent.setFlags(131072);
        PatientDashboard.this.startActivity(localIntent);
        PatientDashboard.this.finish();
      }
    });
    localTextView3.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        Intent localIntent = new Intent(PatientDashboard.this.getApplicationContext(), DoctorDashboard.class);
        localIntent.setFlags(131072);
        PatientDashboard.this.startActivity(localIntent);
        PatientDashboard.this.finish();
      }
    });
    this.heartsView = ((ImageView)findViewById(2131296352));
    SharedPreferences localSharedPreferences = getSharedPreferences("data", 0);
    ((TextView)findViewById(2131296363)).setText(Integer.toString(localSharedPreferences.getInt("patient_star_pts", 0)));
    this.timeMarks = ((RelativeLayout)findViewById(2131296355));
    this.time = ((ProgressBar)findViewById(2131296353));
    this.density = getSharedPreferences("screen", 0).getFloat("density", 1.0F);
    this.timeWidth = (250.0F * this.density);
    this.size = ((int)(20.0F * this.density));
    this.topMargin = ((int)(13.0F * this.density));
    getInhalerUses();
    ((ImageView)findViewById(2131296354)).setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        PatientDashboard.this.getInhalerUses();
      }
    });
    ImageView[] arrayOfImageView = new ImageView[6];
    for (int i = 0;; i++)
    {
      if (i >= 6) {
        return;
      }
      arrayOfImageView[i] = ((ImageView)findViewById(ResourceUtilities.badges[i]));
      arrayOfImageView[i].setOnClickListener(this.badgePopup);
    }
  }
  
  class GetInhalerUses
    extends AsyncTask<String, String, Void>
  {
    JSONArray events;
    JSONArray hearts;
    
    GetInhalerUses() {}
    
    protected Void doInBackground(String... paramVarArgs)
    {
      if (isCancelled()) {}
      JSONParser localJSONParser;
      do
      {
        return null;
        localJSONParser = new JSONParser();
        this.events = localJSONParser.getJSONFromUrl(paramVarArgs[0]);
      } while (isCancelled());
      this.hearts = localJSONParser.getJSONFromUrl(paramVarArgs[1]);
      return null;
    }
    
    protected void onCancelled()
    {
      super.onCancelled();
    }
    
    /* Error */
    protected void onPostExecute(Void paramVoid)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 37	com/geckocap/login/PatientDashboard$GetInhalerUses:events	Lorg/json/JSONArray;
      //   4: ifnull +20 -> 24
      //   7: iconst_0
      //   8: istore_3
      //   9: aload_0
      //   10: getfield 37	com/geckocap/login/PatientDashboard$GetInhalerUses:events	Lorg/json/JSONArray;
      //   13: invokevirtual 57	org/json/JSONArray:length	()I
      //   16: istore 5
      //   18: iload_3
      //   19: iload 5
      //   21: if_icmplt +45 -> 66
      //   24: aload_0
      //   25: getfield 39	com/geckocap/login/PatientDashboard$GetInhalerUses:hearts	Lorg/json/JSONArray;
      //   28: ifnull +37 -> 65
      //   31: aload_0
      //   32: getfield 39	com/geckocap/login/PatientDashboard$GetInhalerUses:hearts	Lorg/json/JSONArray;
      //   35: iconst_0
      //   36: invokevirtual 61	org/json/JSONArray:getBoolean	(I)Z
      //   39: ifeq +117 -> 156
      //   42: aload_0
      //   43: getfield 39	com/geckocap/login/PatientDashboard$GetInhalerUses:hearts	Lorg/json/JSONArray;
      //   46: iconst_1
      //   47: invokevirtual 61	org/json/JSONArray:getBoolean	(I)Z
      //   50: ifeq +87 -> 137
      //   53: aload_0
      //   54: getfield 14	com/geckocap/login/PatientDashboard$GetInhalerUses:this$0	Lcom/geckocap/login/PatientDashboard;
      //   57: invokestatic 67	com/geckocap/login/PatientDashboard:access$3	(Lcom/geckocap/login/PatientDashboard;)Landroid/widget/ImageView;
      //   60: ldc 68
      //   62: invokevirtual 74	android/widget/ImageView:setImageResource	(I)V
      //   65: return
      //   66: aload_0
      //   67: getfield 37	com/geckocap/login/PatientDashboard$GetInhalerUses:events	Lorg/json/JSONArray;
      //   70: iload_3
      //   71: invokevirtual 78	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
      //   74: astore 6
      //   76: aload 6
      //   78: ldc 80
      //   80: invokevirtual 86	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
      //   83: ldc 88
      //   85: invokevirtual 94	java/lang/String:equals	(Ljava/lang/Object;)Z
      //   88: ifeq +21 -> 109
      //   91: aload_0
      //   92: getfield 14	com/geckocap/login/PatientDashboard$GetInhalerUses:this$0	Lcom/geckocap/login/PatientDashboard;
      //   95: iconst_0
      //   96: aload 6
      //   98: ldc 96
      //   100: invokevirtual 100	org/json/JSONObject:getInt	(Ljava/lang/String;)I
      //   103: invokestatic 104	com/geckocap/login/PatientDashboard:access$2	(Lcom/geckocap/login/PatientDashboard;ZI)V
      //   106: goto +63 -> 169
      //   109: aload_0
      //   110: getfield 14	com/geckocap/login/PatientDashboard$GetInhalerUses:this$0	Lcom/geckocap/login/PatientDashboard;
      //   113: iconst_1
      //   114: aload 6
      //   116: ldc 96
      //   118: invokevirtual 100	org/json/JSONObject:getInt	(Ljava/lang/String;)I
      //   121: invokestatic 104	com/geckocap/login/PatientDashboard:access$2	(Lcom/geckocap/login/PatientDashboard;ZI)V
      //   124: goto +45 -> 169
      //   127: astore 4
      //   129: aload 4
      //   131: invokevirtual 107	org/json/JSONException:printStackTrace	()V
      //   134: goto -110 -> 24
      //   137: aload_0
      //   138: getfield 14	com/geckocap/login/PatientDashboard$GetInhalerUses:this$0	Lcom/geckocap/login/PatientDashboard;
      //   141: invokestatic 67	com/geckocap/login/PatientDashboard:access$3	(Lcom/geckocap/login/PatientDashboard;)Landroid/widget/ImageView;
      //   144: ldc 108
      //   146: invokevirtual 74	android/widget/ImageView:setImageResource	(I)V
      //   149: return
      //   150: astore_2
      //   151: aload_2
      //   152: invokevirtual 107	org/json/JSONException:printStackTrace	()V
      //   155: return
      //   156: aload_0
      //   157: getfield 14	com/geckocap/login/PatientDashboard$GetInhalerUses:this$0	Lcom/geckocap/login/PatientDashboard;
      //   160: invokestatic 67	com/geckocap/login/PatientDashboard:access$3	(Lcom/geckocap/login/PatientDashboard;)Landroid/widget/ImageView;
      //   163: ldc 109
      //   165: invokevirtual 74	android/widget/ImageView:setImageResource	(I)V
      //   168: return
      //   169: iinc 3 1
      //   172: goto -163 -> 9
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	175	0	this	GetInhalerUses
      //   0	175	1	paramVoid	Void
      //   150	2	2	localJSONException1	org.json.JSONException
      //   8	162	3	i	int
      //   127	3	4	localJSONException2	org.json.JSONException
      //   16	6	5	j	int
      //   74	41	6	localJSONObject	org.json.JSONObject
      // Exception table:
      //   from	to	target	type
      //   9	18	127	org/json/JSONException
      //   66	106	127	org/json/JSONException
      //   109	124	127	org/json/JSONException
      //   31	65	150	org/json/JSONException
      //   137	149	150	org/json/JSONException
      //   156	168	150	org/json/JSONException
    }
    
    protected void onPreExecute()
    {
      PatientDashboard.this.timeMarks.removeAllViews();
      PatientDashboard.this.setTime();
    }
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.geckocap.login.PatientDashboard
 * JD-Core Version:    0.7.0.1
 */