package com.geckocap.login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import org.json.JSONArray;

public class WeeklyData
  extends Activity
{
  private TextView curData;
  private float density;
  private HashMap<String, Integer> faces = new HashMap();
  private int fireHeight;
  private ImageView forwardNav;
  private int iconBarSize;
  private int offset = 0;
  private TableRow rescueRow;
  private int size;
  private GetInhalerUses task;
  private RelativeLayout timeMarks;
  private float timeWidth;
  private int topMargin;
  
  private void setData()
  {
    this.task = new GetInhalerUses();
    GetInhalerUses localGetInhalerUses = this.task;
    String[] arrayOfString = new String[1];
    arrayOfString[0] = setUrl();
    localGetInhalerUses.execute(arrayOfString);
  }
  
  private void setDates()
  {
    GregorianCalendar localGregorianCalendar = new GregorianCalendar();
    localGregorianCalendar.add(5, -this.offset);
    String str1 = "";
    Object localObject1 = "";
    String str2 = "";
    Object localObject2 = "";
    int i = 0;
    if (i >= 7)
    {
      if (this.offset == 0)
      {
        this.curData.setText("This week");
        this.forwardNav.setVisibility(4);
      }
    }
    else
    {
      TableRow localTableRow = (TableRow)findViewById(ResourceUtilities.rowId[i]);
      TextView localTextView1 = (TextView)localTableRow.findViewById(2131296421);
      TextView localTextView2 = (TextView)localTableRow.findViewById(2131296422);
      String str3 = Integer.toString(localGregorianCalendar.get(5));
      localTextView1.setText(ResourceUtilities.monthAbr[localGregorianCalendar.get(2)]);
      localTextView2.setText(str3);
      if (i == 0)
      {
        str2 = Integer.toString(localGregorianCalendar.get(2));
        localObject2 = str3;
      }
      for (;;)
      {
        localGregorianCalendar.add(5, -1);
        i++;
        break;
        if (i == 6)
        {
          str1 = Integer.toString(localGregorianCalendar.get(2));
          localObject1 = str3;
        }
      }
    }
    this.curData.setText(str1 + "/" + (String)localObject1 + " - " + str2 + "/" + (String)localObject2);
    this.forwardNav.setVisibility(0);
  }
  
  private void setIcons(boolean paramBoolean, int paramInt)
  {
    ImageView localImageView1 = new ImageView(this);
    RelativeLayout.LayoutParams localLayoutParams1 = new RelativeLayout.LayoutParams(this.iconBarSize, this.iconBarSize);
    localLayoutParams1.setMargins((int)(this.timeWidth * paramInt / 1440.0D - this.iconBarSize / 2), this.topMargin, 0, 0);
    if (paramBoolean)
    {
      localImageView1.setImageResource(2130837577);
      ImageView localImageView2 = new ImageView(this);
      localImageView2.setImageResource(2130837537);
      RelativeLayout.LayoutParams localLayoutParams2 = new RelativeLayout.LayoutParams(this.size, this.fireHeight);
      this.rescueRow.addView(localImageView2, localLayoutParams2);
    }
    for (;;)
    {
      this.timeMarks.addView(localImageView1, localLayoutParams1);
      return;
      localImageView1.setImageResource(2130837575);
    }
  }
  
  private String setUrl()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    GregorianCalendar localGregorianCalendar = new GregorianCalendar();
    localGregorianCalendar.add(5, -6 + -1 * this.offset);
    localStringBuffer.append("http://54.243.194.63/parents/data_endpoint_weekly/");
    localStringBuffer.append(Integer.toString(localGregorianCalendar.get(1)));
    StringBuilder localStringBuilder1 = new StringBuilder("-");
    Object[] arrayOfObject1 = new Object[1];
    arrayOfObject1[0] = Integer.valueOf(1 + localGregorianCalendar.get(2));
    localStringBuffer.append(String.format("%02d", arrayOfObject1));
    StringBuilder localStringBuilder2 = new StringBuilder("-");
    Object[] arrayOfObject2 = new Object[1];
    arrayOfObject2[0] = Integer.valueOf(localGregorianCalendar.get(5));
    localStringBuffer.append(String.format("%02d", arrayOfObject2));
    localGregorianCalendar.add(5, 6);
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
    setContentView(2130903075);
    TextView localTextView1 = (TextView)findViewById(2131296411);
    TextView localTextView2 = (TextView)findViewById(2131296410);
    TextView localTextView3 = (TextView)findViewById(2131296412);
    localTextView1.setBackgroundResource(2130837561);
    localTextView2.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        Intent localIntent = new Intent(WeeklyData.this.getApplicationContext(), PatientDashboard.class);
        WeeklyData.this.startActivity(localIntent);
        WeeklyData.this.finish();
      }
    });
    localTextView3.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        Intent localIntent = new Intent(WeeklyData.this.getApplicationContext(), DoctorDashboard.class);
        localIntent.setFlags(131072);
        WeeklyData.this.startActivity(localIntent);
        WeeklyData.this.finish();
      }
    });
    ((ImageView)findViewById(2131296316)).setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        WeeklyData.this.task.cancel(true);
        WeeklyData localWeeklyData = WeeklyData.this;
        localWeeklyData.offset = (7 + localWeeklyData.offset);
        WeeklyData.this.setData();
      }
    });
    this.forwardNav = ((ImageView)findViewById(2131296317));
    this.forwardNav.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        WeeklyData.this.task.cancel(true);
        WeeklyData localWeeklyData = WeeklyData.this;
        localWeeklyData.offset = (-7 + localWeeklyData.offset);
        WeeklyData.this.setData();
      }
    });
    ((TextView)findViewById(2131296319)).setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        Intent localIntent = new Intent(WeeklyData.this.getApplicationContext(), ParentDashboard.class);
        localIntent.setFlags(131072);
        WeeklyData.this.startActivity(localIntent);
        WeeklyData.this.finish();
      }
    });
    this.curData = ((TextView)findViewById(2131296318));
    SharedPreferences localSharedPreferences = getSharedPreferences("screen", 0);
    this.density = localSharedPreferences.getFloat("density", 1.0F);
    this.timeWidth = (localSharedPreferences.getFloat("width", 0.0F) - 97.0F * this.density);
    this.iconBarSize = ((int)(20.0F * this.density));
    this.topMargin = ((int)(37.0F * this.density));
    this.size = ((int)(30.0F * this.density));
    this.fireHeight = ((int)(37.5D * this.density));
    this.faces.put("pain", Integer.valueOf(2130837536));
    this.faces.put("worried", Integer.valueOf(2130837535));
    this.faces.put("surprised", Integer.valueOf(2130837535));
    this.faces.put("happy", Integer.valueOf(2130837534));
    setData();
  }
  
  class GetInhalerUses
    extends AsyncTask<String, String, Void>
  {
    JSONArray weekData;
    
    GetInhalerUses() {}
    
    protected Void doInBackground(String... paramVarArgs)
    {
      if (isCancelled()) {
        return null;
      }
      this.weekData = new JSONParser().getJSONFromUrl(paramVarArgs[0]);
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
      //   1: getfield 36	com/geckocap/login/WeeklyData$GetInhalerUses:weekData	Lorg/json/JSONArray;
      //   4: ifnull +11 -> 15
      //   7: iconst_0
      //   8: istore_2
      //   9: iload_2
      //   10: bipush 7
      //   12: if_icmplt +4 -> 16
      //   15: return
      //   16: aload_0
      //   17: getfield 36	com/geckocap/login/WeeklyData$GetInhalerUses:weekData	Lorg/json/JSONArray;
      //   20: iload_2
      //   21: invokevirtual 54	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
      //   24: astore 4
      //   26: aload_0
      //   27: getfield 13	com/geckocap/login/WeeklyData$GetInhalerUses:this$0	Lcom/geckocap/login/WeeklyData;
      //   30: getstatic 60	com/geckocap/login/ResourceUtilities:rowId	[I
      //   33: iload_2
      //   34: iaload
      //   35: invokevirtual 66	com/geckocap/login/WeeklyData:findViewById	(I)Landroid/view/View;
      //   38: checkcast 68	android/widget/TableRow
      //   41: astore 5
      //   43: aload 5
      //   45: ldc 69
      //   47: invokevirtual 70	android/widget/TableRow:findViewById	(I)Landroid/view/View;
      //   50: checkcast 72	android/widget/ImageView
      //   53: aload_0
      //   54: getfield 13	com/geckocap/login/WeeklyData$GetInhalerUses:this$0	Lcom/geckocap/login/WeeklyData;
      //   57: invokestatic 76	com/geckocap/login/WeeklyData:access$1	(Lcom/geckocap/login/WeeklyData;)Ljava/util/HashMap;
      //   60: aload 4
      //   62: ldc 78
      //   64: invokevirtual 84	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
      //   67: invokevirtual 90	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
      //   70: checkcast 92	java/lang/Integer
      //   73: invokevirtual 96	java/lang/Integer:intValue	()I
      //   76: invokevirtual 100	android/widget/ImageView:setImageResource	(I)V
      //   79: aload 5
      //   81: ldc 101
      //   83: invokevirtual 70	android/widget/TableRow:findViewById	(I)Landroid/view/View;
      //   86: checkcast 72	android/widget/ImageView
      //   89: astore 6
      //   91: aload 5
      //   93: ldc 102
      //   95: invokevirtual 70	android/widget/TableRow:findViewById	(I)Landroid/view/View;
      //   98: checkcast 72	android/widget/ImageView
      //   101: astore 7
      //   103: aload 4
      //   105: ldc 104
      //   107: invokevirtual 107	org/json/JSONObject:getJSONArray	(Ljava/lang/String;)Lorg/json/JSONArray;
      //   110: astore 8
      //   112: aload 8
      //   114: iconst_0
      //   115: invokevirtual 111	org/json/JSONArray:getBoolean	(I)Z
      //   118: ifeq +172 -> 290
      //   121: aload 6
      //   123: ldc 112
      //   125: invokevirtual 100	android/widget/ImageView:setImageResource	(I)V
      //   128: aload 8
      //   130: iconst_1
      //   131: invokevirtual 111	org/json/JSONArray:getBoolean	(I)Z
      //   134: ifeq +172 -> 306
      //   137: aload 7
      //   139: ldc 112
      //   141: invokevirtual 100	android/widget/ImageView:setImageResource	(I)V
      //   144: aload_0
      //   145: getfield 13	com/geckocap/login/WeeklyData$GetInhalerUses:this$0	Lcom/geckocap/login/WeeklyData;
      //   148: aload 5
      //   150: ldc 113
      //   152: invokevirtual 70	android/widget/TableRow:findViewById	(I)Landroid/view/View;
      //   155: checkcast 68	android/widget/TableRow
      //   158: invokestatic 117	com/geckocap/login/WeeklyData:access$2	(Lcom/geckocap/login/WeeklyData;Landroid/widget/TableRow;)V
      //   161: aload_0
      //   162: getfield 13	com/geckocap/login/WeeklyData$GetInhalerUses:this$0	Lcom/geckocap/login/WeeklyData;
      //   165: invokestatic 121	com/geckocap/login/WeeklyData:access$3	(Lcom/geckocap/login/WeeklyData;)Landroid/widget/TableRow;
      //   168: invokevirtual 124	android/widget/TableRow:removeAllViews	()V
      //   171: aload_0
      //   172: getfield 13	com/geckocap/login/WeeklyData$GetInhalerUses:this$0	Lcom/geckocap/login/WeeklyData;
      //   175: aload 5
      //   177: ldc 125
      //   179: invokevirtual 70	android/widget/TableRow:findViewById	(I)Landroid/view/View;
      //   182: checkcast 127	android/widget/RelativeLayout
      //   185: invokestatic 131	com/geckocap/login/WeeklyData:access$4	(Lcom/geckocap/login/WeeklyData;Landroid/widget/RelativeLayout;)V
      //   188: aload_0
      //   189: getfield 13	com/geckocap/login/WeeklyData$GetInhalerUses:this$0	Lcom/geckocap/login/WeeklyData;
      //   192: invokestatic 135	com/geckocap/login/WeeklyData:access$5	(Lcom/geckocap/login/WeeklyData;)Landroid/widget/RelativeLayout;
      //   195: invokevirtual 136	android/widget/RelativeLayout:removeAllViews	()V
      //   198: aload 4
      //   200: ldc 138
      //   202: invokevirtual 107	org/json/JSONObject:getJSONArray	(Ljava/lang/String;)Lorg/json/JSONArray;
      //   205: astore 9
      //   207: aload 9
      //   209: ifnull +20 -> 229
      //   212: iconst_0
      //   213: istore 12
      //   215: aload 9
      //   217: invokevirtual 141	org/json/JSONArray:length	()I
      //   220: istore 14
      //   222: iload 12
      //   224: iload 14
      //   226: if_icmplt +90 -> 316
      //   229: aload 5
      //   231: ldc 142
      //   233: invokevirtual 70	android/widget/TableRow:findViewById	(I)Landroid/view/View;
      //   236: checkcast 144	android/widget/ProgressBar
      //   239: astore 10
      //   241: iload_2
      //   242: ifne +152 -> 394
      //   245: aload_0
      //   246: getfield 13	com/geckocap/login/WeeklyData$GetInhalerUses:this$0	Lcom/geckocap/login/WeeklyData;
      //   249: invokestatic 148	com/geckocap/login/WeeklyData:access$7	(Lcom/geckocap/login/WeeklyData;)I
      //   252: ifne +134 -> 386
      //   255: new 150	java/util/GregorianCalendar
      //   258: dup
      //   259: invokespecial 151	java/util/GregorianCalendar:<init>	()V
      //   262: astore 11
      //   264: aload 10
      //   266: bipush 100
      //   268: aload 11
      //   270: bipush 11
      //   272: invokevirtual 156	java/util/Calendar:get	(I)I
      //   275: imul
      //   276: aload 11
      //   278: bipush 12
      //   280: invokevirtual 156	java/util/Calendar:get	(I)I
      //   283: iadd
      //   284: invokevirtual 159	android/widget/ProgressBar:setProgress	(I)V
      //   287: goto +107 -> 394
      //   290: aload 6
      //   292: ldc 160
      //   294: invokevirtual 100	android/widget/ImageView:setImageResource	(I)V
      //   297: goto -169 -> 128
      //   300: astore_3
      //   301: aload_3
      //   302: invokevirtual 163	org/json/JSONException:printStackTrace	()V
      //   305: return
      //   306: aload 7
      //   308: ldc 160
      //   310: invokevirtual 100	android/widget/ImageView:setImageResource	(I)V
      //   313: goto -169 -> 144
      //   316: aload 9
      //   318: iload 12
      //   320: invokevirtual 54	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
      //   323: astore 15
      //   325: aload 15
      //   327: ldc 165
      //   329: invokevirtual 84	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
      //   332: ldc 167
      //   334: invokevirtual 173	java/lang/String:equals	(Ljava/lang/Object;)Z
      //   337: ifeq +21 -> 358
      //   340: aload_0
      //   341: getfield 13	com/geckocap/login/WeeklyData$GetInhalerUses:this$0	Lcom/geckocap/login/WeeklyData;
      //   344: iconst_0
      //   345: aload 15
      //   347: ldc 175
      //   349: invokevirtual 179	org/json/JSONObject:getInt	(Ljava/lang/String;)I
      //   352: invokestatic 183	com/geckocap/login/WeeklyData:access$6	(Lcom/geckocap/login/WeeklyData;ZI)V
      //   355: goto +45 -> 400
      //   358: aload_0
      //   359: getfield 13	com/geckocap/login/WeeklyData$GetInhalerUses:this$0	Lcom/geckocap/login/WeeklyData;
      //   362: iconst_1
      //   363: aload 15
      //   365: ldc 175
      //   367: invokevirtual 179	org/json/JSONObject:getInt	(Ljava/lang/String;)I
      //   370: invokestatic 183	com/geckocap/login/WeeklyData:access$6	(Lcom/geckocap/login/WeeklyData;ZI)V
      //   373: goto +27 -> 400
      //   376: astore 13
      //   378: aload 13
      //   380: invokevirtual 163	org/json/JSONException:printStackTrace	()V
      //   383: goto -154 -> 229
      //   386: aload 10
      //   388: sipush 2400
      //   391: invokevirtual 159	android/widget/ProgressBar:setProgress	(I)V
      //   394: iinc 2 1
      //   397: goto -388 -> 9
      //   400: iinc 12 1
      //   403: goto -188 -> 215
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	406	0	this	GetInhalerUses
      //   0	406	1	paramVoid	Void
      //   8	387	2	i	int
      //   300	2	3	localJSONException1	org.json.JSONException
      //   24	175	4	localJSONObject1	org.json.JSONObject
      //   41	189	5	localTableRow	TableRow
      //   89	202	6	localImageView1	ImageView
      //   101	206	7	localImageView2	ImageView
      //   110	19	8	localJSONArray1	JSONArray
      //   205	112	9	localJSONArray2	JSONArray
      //   239	148	10	localProgressBar	android.widget.ProgressBar
      //   262	15	11	localGregorianCalendar	GregorianCalendar
      //   213	188	12	j	int
      //   376	3	13	localJSONException2	org.json.JSONException
      //   220	7	14	k	int
      //   323	41	15	localJSONObject2	org.json.JSONObject
      // Exception table:
      //   from	to	target	type
      //   16	128	300	org/json/JSONException
      //   128	144	300	org/json/JSONException
      //   144	207	300	org/json/JSONException
      //   229	241	300	org/json/JSONException
      //   245	287	300	org/json/JSONException
      //   290	297	300	org/json/JSONException
      //   306	313	300	org/json/JSONException
      //   378	383	300	org/json/JSONException
      //   386	394	300	org/json/JSONException
      //   215	222	376	org/json/JSONException
      //   316	355	376	org/json/JSONException
      //   358	373	376	org/json/JSONException
    }
    
    protected void onPreExecute()
    {
      super.onPreExecute();
      WeeklyData.this.setDates();
    }
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.geckocap.login.WeeklyData
 * JD-Core Version:    0.7.0.1
 */