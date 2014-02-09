package com.geckocap.login;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

public class ParentDashboard
  extends Activity
{
  View.OnClickListener awardPopup = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      final Dialog localDialog = new Dialog(ParentDashboard.this.context);
      localDialog.requestWindowFeature(1);
      localDialog.setContentView(2130903042);
      String str1 = ParentDashboard.this.data.getString("demo_patient_firstname", "");
      ((TextView)localDialog.findViewById(2131296282)).setText("Award " + str1);
      TextView localTextView = (TextView)localDialog.findViewById(2131296284);
      String str2 = Integer.toString(ParentDashboard.this.data.getInt("parent_star_pts", 0));
      localTextView.setText("You have " + str2 + " stars. You can give " + str1 + " some, or choose a badge or gift");
      View.OnClickListener local1 = new View.OnClickListener()
      {
        public void onClick(View paramAnonymous2View)
        {
          localDialog.dismiss();
        }
      };
      Button localButton1 = (Button)localDialog.findViewById(2131296283);
      Button localButton2 = (Button)localDialog.findViewById(2131296286);
      Button localButton3 = (Button)localDialog.findViewById(2131296287);
      localButton1.setOnClickListener(local1);
      localButton2.setOnClickListener(local1);
      localButton3.setOnClickListener(local1);
      Button localButton4 = (Button)localDialog.findViewById(2131296285);
      final RatingBar localRatingBar = (RatingBar)localDialog.findViewById(2131296290);
      final TableRow localTableRow = (TableRow)localDialog.findViewById(2131296288);
      localRatingBar.setStepSize(1.0F);
      localButton4.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymous2View)
        {
          localTableRow.setVisibility(0);
        }
      });
      ((Button)localDialog.findViewById(2131296291)).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymous2View)
        {
          int i = (int)localRatingBar.getRating();
          localDialog.dismiss();
          ParentDashboard.this.givePoints(i);
        }
      });
      localDialog.show();
    }
  };
  private ImageView backNav;
  View.OnLongClickListener changeIcon = new View.OnLongClickListener()
  {
    public boolean onLongClick(View paramAnonymousView)
    {
      final Dialog localDialog = new Dialog(ParentDashboard.this.context);
      localDialog.requestWindowFeature(1);
      localDialog.setContentView(2130903044);
      View.OnClickListener local1 = new View.OnClickListener()
      {
        public void onClick(View paramAnonymous2View)
        {
          localDialog.dismiss();
        }
      };
      ((Button)localDialog.findViewById(2131296283)).setOnClickListener(local1);
      localDialog.show();
      return false;
    }
  };
  private ImageView child;
  final Context context = this;
  private TextView curDay;
  private LinearLayout dash1;
  SharedPreferences data;
  private float density;
  View.OnClickListener editSettings = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      Intent localIntent = new Intent(ParentDashboard.this.getApplicationContext(), NotificationSettings.class);
      ParentDashboard.this.startActivity(localIntent);
    }
  };
  private ImageView forwardNav;
  private ImageView heartsView;
  AsyncTask<Void, Void, Void> mRegisterTask;
  private int offset = 0;
  private TextView parentStars;
  private TextView patientStars;
  View.OnClickListener refillPopup = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      final Dialog localDialog = new Dialog(ParentDashboard.this.context);
      localDialog.requestWindowFeature(1);
      localDialog.setContentView(2130903064);
      View.OnClickListener local1 = new View.OnClickListener()
      {
        public void onClick(View paramAnonymous2View)
        {
          localDialog.dismiss();
        }
      };
      ((Button)localDialog.findViewById(2131296283)).setOnClickListener(local1);
      ((ImageView)localDialog.findViewById(2131296383)).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymous2View)
        {
          Intent localIntent = new Intent("android.intent.action.VIEW", Uri.parse("http://cvs.com"));
          ParentDashboard.this.startActivity(localIntent);
        }
      });
      localDialog.show();
    }
  };
  private ImageView refresh;
  View.OnClickListener refreshDash = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      long l = System.currentTimeMillis();
      NotificationManager localNotificationManager = (NotificationManager)ParentDashboard.this.context.getSystemService("notification");
      Notification localNotification = new Notification(2130837539, "GeckoCap notification", l);
      String str = ParentDashboard.this.context.getString(2131034113);
      Intent localIntent = new Intent("android.intent.action.VIEW", Uri.parse("http://54.243.194.63/parents"));
      PendingIntent localPendingIntent = PendingIntent.getActivity(ParentDashboard.this.context, 0, localIntent, 0);
      localNotification.setLatestEventInfo(ParentDashboard.this.context, str, "GeckoCap notification", localPendingIntent);
      localNotification.flags = (0x10 | localNotification.flags);
      localNotification.defaults = (0x1 | localNotification.defaults);
      localNotification.defaults = (0x2 | localNotification.defaults);
      localNotificationManager.notify(0, localNotification);
      ParentDashboard.this.getInhalerUses();
    }
  };
  View.OnClickListener rescuePopup = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      final LinearLayout localLinearLayout = (LinearLayout)paramAnonymousView.getParent();
      final Dialog localDialog = new Dialog(ParentDashboard.this.context);
      localDialog.requestWindowFeature(1);
      localDialog.setContentView(2130903072);
      ((Button)localDialog.findViewById(2131296283)).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymous2View)
        {
          localDialog.dismiss();
        }
      });
      View.OnClickListener local2 = new View.OnClickListener()
      {
        public void onClick(View paramAnonymous2View)
        {
          Button localButton = (Button)paramAnonymous2View;
          ParentDashboard.this.addRescueReason(localButton.getText().toString());
          localDialog.dismiss();
          ParentDashboard.this.dash1.removeView(localLinearLayout);
        }
      };
      Button localButton1 = (Button)localDialog.findViewById(2131296406);
      Button localButton2 = (Button)localDialog.findViewById(2131296407);
      Button localButton3 = (Button)localDialog.findViewById(2131296408);
      Button localButton4 = (Button)localDialog.findViewById(2131296409);
      localButton1.setOnClickListener(local2);
      localButton2.setOnClickListener(local2);
      localButton3.setOnClickListener(local2);
      localButton4.setOnClickListener(local2);
      localDialog.show();
    }
  };
  private int size;
  private GetInhalerUses task;
  private ProgressBar time;
  private RelativeLayout timeMarks;
  private float timeWidth;
  private int topMargin;
  
  private void addRescueReason(String paramString)
  {
    try
    {
      JSONObject localJSONObject = new JSONObject();
      localJSONObject.put("message", paramString);
      localJSONObject.put("user", "billy");
      DefaultHttpClient localDefaultHttpClient = new DefaultHttpClient();
      HttpPost localHttpPost = new HttpPost("http://192.168.1.67:8000/notifications/rescue_reason/");
      localHttpPost.setEntity(new StringEntity(localJSONObject.toString()));
      localHttpPost.setHeader("Accept", "application/json");
      localHttpPost.setHeader("Content-type", "application/json");
      localDefaultHttpClient.execute(localHttpPost);
      Toast.makeText(this, localJSONObject.toString(), 1).show();
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.e(localThrowable.toString(), "error thrown");
      Toast.makeText(this, localThrowable.toString(), 1).show();
    }
  }
  
  private void getInhalerUses()
  {
    this.task = new GetInhalerUses();
    GetInhalerUses localGetInhalerUses = this.task;
    String[] arrayOfString = new String[2];
    arrayOfString[0] = setUrl("/dose_events");
    arrayOfString[1] = setUrl("/hearts_pattern");
    localGetInhalerUses.execute(arrayOfString);
  }
  
  private void givePoints(int paramInt)
  {
    int i = this.data.getInt("parent_star_pts", 0) - paramInt;
    int j = paramInt + this.data.getInt("patient_star_pts", 0);
    this.data.edit().putInt("parent_star_pts", i).commit();
    this.data.edit().putInt("patient_star_pts", j).commit();
    this.parentStars.setText(Integer.toString(i));
    this.patientStars.setText(Integer.toString(j));
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
    localGregorianCalendar.add(5, this.offset);
    if (this.offset == 0)
    {
      this.forwardNav.setVisibility(4);
      this.time.setProgress(100 * localGregorianCalendar.get(11) + localGregorianCalendar.get(12));
      this.curDay.setText("Today's status");
      this.refresh.setVisibility(0);
      return;
    }
    this.forwardNav.setVisibility(0);
    this.time.setProgress(2400);
    TextView localTextView = this.curDay;
    StringBuilder localStringBuilder1 = new StringBuilder(String.valueOf(ResourceUtilities.daysOfWeek[localGregorianCalendar.get(7)])).append(",");
    Object[] arrayOfObject1 = new Object[1];
    arrayOfObject1[0] = Integer.valueOf(1 + localGregorianCalendar.get(2));
    StringBuilder localStringBuilder2 = localStringBuilder1.append(String.format("%02d", arrayOfObject1)).append("/");
    Object[] arrayOfObject2 = new Object[1];
    arrayOfObject2[0] = Integer.valueOf(localGregorianCalendar.get(5));
    localTextView.setText(String.format("%02d", arrayOfObject2));
    this.refresh.setVisibility(4);
  }
  
  private String setUrl(String paramString)
  {
    GregorianCalendar localGregorianCalendar = new GregorianCalendar();
    localGregorianCalendar.add(5, this.offset);
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
    setContentView(2130903059);
    this.dash1 = ((LinearLayout)findViewById(2131296343));
    TextView localTextView1 = (TextView)findViewById(2131296411);
    TextView localTextView2 = (TextView)findViewById(2131296410);
    TextView localTextView3 = (TextView)findViewById(2131296412);
    localTextView1.setBackgroundResource(2130837561);
    localTextView2.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        Intent localIntent = new Intent(ParentDashboard.this.getApplicationContext(), PatientDashboard.class);
        ParentDashboard.this.startActivity(localIntent);
        ParentDashboard.this.finish();
      }
    });
    localTextView3.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        Intent localIntent = new Intent(ParentDashboard.this.getApplicationContext(), DoctorDashboard.class);
        localIntent.setFlags(131072);
        ParentDashboard.this.startActivity(localIntent);
        ParentDashboard.this.finish();
      }
    });
    this.backNav = ((ImageView)findViewById(2131296316));
    this.backNav.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        ParentDashboard.this.task.cancel(true);
        ParentDashboard localParentDashboard = ParentDashboard.this;
        localParentDashboard.offset = (-1 + localParentDashboard.offset);
        ParentDashboard.this.getInhalerUses();
      }
    });
    this.forwardNav = ((ImageView)findViewById(2131296317));
    this.forwardNav.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        ParentDashboard.this.task.cancel(true);
        ParentDashboard localParentDashboard = ParentDashboard.this;
        localParentDashboard.offset = (1 + localParentDashboard.offset);
        ParentDashboard.this.getInhalerUses();
      }
    });
    this.curDay = ((TextView)findViewById(2131296318));
    ((TextView)findViewById(2131296344)).setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        Intent localIntent = new Intent(ParentDashboard.this.getApplicationContext(), WeeklyData.class);
        ParentDashboard.this.startActivity(localIntent);
      }
    });
    ((TextView)findViewById(2131296345)).setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        Intent localIntent = new Intent(ParentDashboard.this.getApplicationContext(), MonthlyData.class);
        ParentDashboard.this.startActivity(localIntent);
      }
    });
    this.child = ((ImageView)findViewById(2131296351));
    this.child.setOnLongClickListener(this.changeIcon);
    this.heartsView = ((ImageView)findViewById(2131296352));
    ((Button)findViewById(2131296350)).setOnClickListener(this.awardPopup);
    this.data = getSharedPreferences("data", 0);
    this.parentStars = ((TextView)findViewById(2131296348));
    this.parentStars.setText(Integer.toString(this.data.getInt("parent_star_pts", 0)));
    this.patientStars = ((TextView)findViewById(2131296349));
    this.patientStars.setText(Integer.toString(this.data.getInt("patient_star_pts", 0)));
    this.timeMarks = ((RelativeLayout)findViewById(2131296355));
    this.time = ((ProgressBar)findViewById(2131296353));
    this.density = getSharedPreferences("screen", 0).getFloat("density", 1.0F);
    this.timeWidth = (250.0F * this.density);
    this.size = ((int)(20.0F * this.density));
    this.topMargin = ((int)(13.0F * this.density));
    this.refresh = ((ImageView)findViewById(2131296354));
    this.refresh.setOnClickListener(this.refreshDash);
    ((Button)findViewById(2131296359)).setOnClickListener(this.rescuePopup);
    ((Button)findViewById(2131296362)).setOnClickListener(this.refillPopup);
    ((ImageView)findViewById(2131296358)).setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        ParentDashboard.this.dash1.removeView(ParentDashboard.this.findViewById(2131296357));
      }
    });
    ((ImageView)findViewById(2131296361)).setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        ParentDashboard.this.dash1.removeView(ParentDashboard.this.findViewById(2131296360));
      }
    });
    ((TextView)findViewById(2131296356)).setOnClickListener(this.editSettings);
    ((TextView)findViewById(2131296319)).setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        Intent localIntent = new Intent(ParentDashboard.this.getApplicationContext(), GetDoseEvents.class);
        ParentDashboard.this.startActivity(localIntent);
      }
    });
  }
  
  public void onResume()
  {
    super.onResume();
    getInhalerUses();
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
      //   1: getfield 37	com/geckocap/login/ParentDashboard$GetInhalerUses:events	Lorg/json/JSONArray;
      //   4: ifnull +20 -> 24
      //   7: iconst_0
      //   8: istore_3
      //   9: aload_0
      //   10: getfield 37	com/geckocap/login/ParentDashboard$GetInhalerUses:events	Lorg/json/JSONArray;
      //   13: invokevirtual 57	org/json/JSONArray:length	()I
      //   16: istore 5
      //   18: iload_3
      //   19: iload 5
      //   21: if_icmplt +45 -> 66
      //   24: aload_0
      //   25: getfield 39	com/geckocap/login/ParentDashboard$GetInhalerUses:hearts	Lorg/json/JSONArray;
      //   28: ifnull +37 -> 65
      //   31: aload_0
      //   32: getfield 39	com/geckocap/login/ParentDashboard$GetInhalerUses:hearts	Lorg/json/JSONArray;
      //   35: iconst_0
      //   36: invokevirtual 61	org/json/JSONArray:getBoolean	(I)Z
      //   39: ifeq +117 -> 156
      //   42: aload_0
      //   43: getfield 39	com/geckocap/login/ParentDashboard$GetInhalerUses:hearts	Lorg/json/JSONArray;
      //   46: iconst_1
      //   47: invokevirtual 61	org/json/JSONArray:getBoolean	(I)Z
      //   50: ifeq +87 -> 137
      //   53: aload_0
      //   54: getfield 14	com/geckocap/login/ParentDashboard$GetInhalerUses:this$0	Lcom/geckocap/login/ParentDashboard;
      //   57: invokestatic 67	com/geckocap/login/ParentDashboard:access$7	(Lcom/geckocap/login/ParentDashboard;)Landroid/widget/ImageView;
      //   60: ldc 68
      //   62: invokevirtual 74	android/widget/ImageView:setImageResource	(I)V
      //   65: return
      //   66: aload_0
      //   67: getfield 37	com/geckocap/login/ParentDashboard$GetInhalerUses:events	Lorg/json/JSONArray;
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
      //   92: getfield 14	com/geckocap/login/ParentDashboard$GetInhalerUses:this$0	Lcom/geckocap/login/ParentDashboard;
      //   95: iconst_0
      //   96: aload 6
      //   98: ldc 96
      //   100: invokevirtual 100	org/json/JSONObject:getInt	(Ljava/lang/String;)I
      //   103: invokestatic 104	com/geckocap/login/ParentDashboard:access$6	(Lcom/geckocap/login/ParentDashboard;ZI)V
      //   106: goto +63 -> 169
      //   109: aload_0
      //   110: getfield 14	com/geckocap/login/ParentDashboard$GetInhalerUses:this$0	Lcom/geckocap/login/ParentDashboard;
      //   113: iconst_1
      //   114: aload 6
      //   116: ldc 96
      //   118: invokevirtual 100	org/json/JSONObject:getInt	(Ljava/lang/String;)I
      //   121: invokestatic 104	com/geckocap/login/ParentDashboard:access$6	(Lcom/geckocap/login/ParentDashboard;ZI)V
      //   124: goto +45 -> 169
      //   127: astore 4
      //   129: aload 4
      //   131: invokevirtual 107	org/json/JSONException:printStackTrace	()V
      //   134: goto -110 -> 24
      //   137: aload_0
      //   138: getfield 14	com/geckocap/login/ParentDashboard$GetInhalerUses:this$0	Lcom/geckocap/login/ParentDashboard;
      //   141: invokestatic 67	com/geckocap/login/ParentDashboard:access$7	(Lcom/geckocap/login/ParentDashboard;)Landroid/widget/ImageView;
      //   144: ldc 108
      //   146: invokevirtual 74	android/widget/ImageView:setImageResource	(I)V
      //   149: return
      //   150: astore_2
      //   151: aload_2
      //   152: invokevirtual 107	org/json/JSONException:printStackTrace	()V
      //   155: return
      //   156: aload_0
      //   157: getfield 14	com/geckocap/login/ParentDashboard$GetInhalerUses:this$0	Lcom/geckocap/login/ParentDashboard;
      //   160: invokestatic 67	com/geckocap/login/ParentDashboard:access$7	(Lcom/geckocap/login/ParentDashboard;)Landroid/widget/ImageView;
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
      //   74	41	6	localJSONObject	JSONObject
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
      ParentDashboard.this.timeMarks.removeAllViews();
      ParentDashboard.this.setTime();
    }
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.geckocap.login.ParentDashboard
 * JD-Core Version:    0.7.0.1
 */