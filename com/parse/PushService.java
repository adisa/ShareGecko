package com.parse;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.IBinder;
import com.parse.os.ParseAsyncTask;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public final class PushService
  extends Service
{
  private static final String TAG = "com.parse.PushService";
  static int consecutiveFailures;
  private static int defaultPushPort = 8253;
  private static int delaySeconds;
  private static String pushServer = "push.parse.com";
  static Semaphore sleepSemaphore;
  static Socket socket;
  private Timer keepAliveTimer = null;
  private int pushPort;
  private ServiceState state = ServiceState.STOPPED;
  private ParseAsyncTask<Void, Void, String> task;
  
  public static Set<String> getSubscriptions(Context paramContext)
  {
    try
    {
      Set localSet = ParsePushRouter.getSubscriptions(paramContext);
      return localSet;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  private void increaseDelay()
  {
    consecutiveFailures = 1 + consecutiveFailures;
    delaySeconds = (int)(delaySeconds * (1.5D + Math.random() / 2.0D));
    delaySeconds = Math.max(15, delaySeconds);
    delaySeconds = Math.min(delaySeconds, 300);
  }
  
  private void readInBackground(final BufferedReader paramBufferedReader)
  {
    if (this.state == ServiceState.DESTRUCTING) {
      return;
    }
    this.task = new ParseAsyncTask()
    {
      private BufferedReader reader;
      
      /* Error */
      protected String doInBackground(Void... paramAnonymousVarArgs)
      {
        // Byte code:
        //   0: aload_0
        //   1: getfield 22	com/parse/PushService$2:val$initialReader	Ljava/io/BufferedReader;
        //   4: ifnull +26 -> 30
        //   7: aload_0
        //   8: aload_0
        //   9: getfield 22	com/parse/PushService$2:val$initialReader	Ljava/io/BufferedReader;
        //   12: putfield 44	com/parse/PushService$2:reader	Ljava/io/BufferedReader;
        //   15: aload_0
        //   16: getfield 20	com/parse/PushService$2:this$0	Lcom/parse/PushService;
        //   19: invokestatic 48	com/parse/PushService:access$200	(Lcom/parse/PushService;)Lcom/parse/PushService$ServiceState;
        //   22: getstatic 54	com/parse/PushService$ServiceState:DESTRUCTING	Lcom/parse/PushService$ServiceState;
        //   25: if_acmpne +359 -> 384
        //   28: aconst_null
        //   29: areturn
        //   30: invokestatic 58	com/parse/PushService:access$100	()I
        //   33: ifle +44 -> 77
        //   36: ldc 60
        //   38: new 62	java/lang/StringBuilder
        //   41: dup
        //   42: invokespecial 63	java/lang/StringBuilder:<init>	()V
        //   45: ldc 65
        //   47: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   50: invokestatic 58	com/parse/PushService:access$100	()I
        //   53: invokevirtual 72	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
        //   56: ldc 74
        //   58: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   61: invokevirtual 78	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   64: invokestatic 84	com/parse/Parse:logI	(Ljava/lang/String;Ljava/lang/String;)V
        //   67: sipush 1000
        //   70: invokestatic 58	com/parse/PushService:access$100	()I
        //   73: imul
        //   74: invokestatic 88	com/parse/PushService:sleep	(I)V
        //   77: aload_0
        //   78: getfield 20	com/parse/PushService$2:this$0	Lcom/parse/PushService;
        //   81: invokestatic 48	com/parse/PushService:access$200	(Lcom/parse/PushService;)Lcom/parse/PushService$ServiceState;
        //   84: getstatic 54	com/parse/PushService$ServiceState:DESTRUCTING	Lcom/parse/PushService$ServiceState;
        //   87: if_acmpeq -59 -> 28
        //   90: aload_0
        //   91: getfield 20	com/parse/PushService$2:this$0	Lcom/parse/PushService;
        //   94: invokestatic 92	com/parse/PushService:access$300	(Lcom/parse/PushService;)V
        //   97: getstatic 96	com/parse/PushService:socket	Ljava/net/Socket;
        //   100: ifnull +9 -> 109
        //   103: getstatic 96	com/parse/PushService:socket	Ljava/net/Socket;
        //   106: invokevirtual 101	java/net/Socket:close	()V
        //   109: ldc 60
        //   111: new 62	java/lang/StringBuilder
        //   114: dup
        //   115: invokespecial 63	java/lang/StringBuilder:<init>	()V
        //   118: ldc 103
        //   120: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   123: invokestatic 106	com/parse/PushService:access$400	()Ljava/lang/String;
        //   126: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   129: ldc 108
        //   131: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   134: aload_0
        //   135: getfield 20	com/parse/PushService$2:this$0	Lcom/parse/PushService;
        //   138: invokestatic 112	com/parse/PushService:access$500	(Lcom/parse/PushService;)I
        //   141: invokevirtual 72	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
        //   144: invokevirtual 78	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   147: invokestatic 115	com/parse/Parse:logD	(Ljava/lang/String;Ljava/lang/String;)V
        //   150: new 117	java/net/InetSocketAddress
        //   153: dup
        //   154: invokestatic 106	com/parse/PushService:access$400	()Ljava/lang/String;
        //   157: aload_0
        //   158: getfield 20	com/parse/PushService$2:this$0	Lcom/parse/PushService;
        //   161: invokestatic 112	com/parse/PushService:access$500	(Lcom/parse/PushService;)I
        //   164: invokespecial 120	java/net/InetSocketAddress:<init>	(Ljava/lang/String;I)V
        //   167: astore 4
        //   169: new 98	java/net/Socket
        //   172: dup
        //   173: invokespecial 121	java/net/Socket:<init>	()V
        //   176: astore 5
        //   178: aload 5
        //   180: iconst_1
        //   181: invokevirtual 125	java/net/Socket:setKeepAlive	(Z)V
        //   184: getstatic 129	com/parse/PushService:sleepSemaphore	Ljava/util/concurrent/Semaphore;
        //   187: ifnonnull +75 -> 262
        //   190: aload 5
        //   192: aload 4
        //   194: sipush 5000
        //   197: invokevirtual 133	java/net/Socket:connect	(Ljava/net/SocketAddress;I)V
        //   200: aload_0
        //   201: getfield 20	com/parse/PushService$2:this$0	Lcom/parse/PushService;
        //   204: astore 6
        //   206: aload 6
        //   208: monitorenter
        //   209: aload_0
        //   210: getfield 20	com/parse/PushService$2:this$0	Lcom/parse/PushService;
        //   213: invokestatic 48	com/parse/PushService:access$200	(Lcom/parse/PushService;)Lcom/parse/PushService$ServiceState;
        //   216: getstatic 54	com/parse/PushService$ServiceState:DESTRUCTING	Lcom/parse/PushService$ServiceState;
        //   219: if_acmpne +77 -> 296
        //   222: aload 6
        //   224: monitorexit
        //   225: aconst_null
        //   226: areturn
        //   227: astore 7
        //   229: aload 6
        //   231: monitorexit
        //   232: aload 7
        //   234: athrow
        //   235: astore 13
        //   237: ldc 60
        //   239: ldc 135
        //   241: aload 13
        //   243: invokestatic 139	com/parse/Parse:logE	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
        //   246: aconst_null
        //   247: areturn
        //   248: astore 12
        //   250: ldc 60
        //   252: ldc 141
        //   254: aload 12
        //   256: invokestatic 139	com/parse/Parse:logE	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
        //   259: goto -150 -> 109
        //   262: aload 5
        //   264: aload 4
        //   266: bipush 50
        //   268: invokevirtual 133	java/net/Socket:connect	(Ljava/net/SocketAddress;I)V
        //   271: goto -71 -> 200
        //   274: astore_3
        //   275: ldc 60
        //   277: ldc 143
        //   279: aload_3
        //   280: invokestatic 139	com/parse/Parse:logE	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
        //   283: aconst_null
        //   284: areturn
        //   285: astore_2
        //   286: ldc 60
        //   288: ldc 145
        //   290: aload_2
        //   291: invokestatic 139	com/parse/Parse:logE	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
        //   294: aconst_null
        //   295: areturn
        //   296: aload 5
        //   298: putstatic 96	com/parse/PushService:socket	Ljava/net/Socket;
        //   301: aload 6
        //   303: monitorexit
        //   304: aload_0
        //   305: new 147	java/io/BufferedReader
        //   308: dup
        //   309: new 149	java/io/InputStreamReader
        //   312: dup
        //   313: getstatic 96	com/parse/PushService:socket	Ljava/net/Socket;
        //   316: invokevirtual 153	java/net/Socket:getInputStream	()Ljava/io/InputStream;
        //   319: ldc 155
        //   321: invokespecial 158	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;Ljava/lang/String;)V
        //   324: sipush 8192
        //   327: invokespecial 161	java/io/BufferedReader:<init>	(Ljava/io/Reader;I)V
        //   330: putfield 44	com/parse/PushService$2:reader	Ljava/io/BufferedReader;
        //   333: aload_0
        //   334: getfield 20	com/parse/PushService$2:this$0	Lcom/parse/PushService;
        //   337: invokestatic 48	com/parse/PushService:access$200	(Lcom/parse/PushService;)Lcom/parse/PushService$ServiceState;
        //   340: getstatic 54	com/parse/PushService$ServiceState:DESTRUCTING	Lcom/parse/PushService$ServiceState;
        //   343: if_acmpeq -315 -> 28
        //   346: aload_0
        //   347: getfield 24	com/parse/PushService$2:val$finalService	Lcom/parse/PushService;
        //   350: invokestatic 165	com/parse/PushService:access$600	(Landroid/content/Context;)Z
        //   353: ifne -338 -> 15
        //   356: aconst_null
        //   357: areturn
        //   358: astore 9
        //   360: ldc 60
        //   362: ldc 167
        //   364: aload 9
        //   366: invokestatic 139	com/parse/Parse:logE	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
        //   369: aconst_null
        //   370: areturn
        //   371: astore 8
        //   373: ldc 60
        //   375: ldc 169
        //   377: aload 8
        //   379: invokestatic 139	com/parse/Parse:logE	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
        //   382: aconst_null
        //   383: areturn
        //   384: invokestatic 172	com/parse/PushService:resetDelay	()V
        //   387: ldc 60
        //   389: ldc 174
        //   391: invokestatic 115	com/parse/Parse:logD	(Ljava/lang/String;Ljava/lang/String;)V
        //   394: aload_0
        //   395: getfield 44	com/parse/PushService$2:reader	Ljava/io/BufferedReader;
        //   398: invokevirtual 177	java/io/BufferedReader:readLine	()Ljava/lang/String;
        //   401: astore 11
        //   403: aload 11
        //   405: areturn
        //   406: astore 10
        //   408: ldc 60
        //   410: ldc 179
        //   412: invokestatic 182	com/parse/Parse:logV	(Ljava/lang/String;Ljava/lang/String;)V
        //   415: aconst_null
        //   416: areturn
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	417	0	this	2
        //   0	417	1	paramAnonymousVarArgs	Void[]
        //   285	6	2	localIOException1	IOException
        //   274	6	3	localUnknownHostException	java.net.UnknownHostException
        //   167	98	4	localInetSocketAddress	java.net.InetSocketAddress
        //   176	121	5	localSocket	Socket
        //   227	6	7	localObject	Object
        //   371	7	8	localIOException2	IOException
        //   358	7	9	localUnsupportedEncodingException	UnsupportedEncodingException
        //   406	1	10	localIOException3	IOException
        //   401	3	11	str	String
        //   248	7	12	localIOException4	IOException
        //   235	7	13	localInterruptedException	InterruptedException
        // Exception table:
        //   from	to	target	type
        //   209	225	227	finally
        //   229	232	227	finally
        //   296	304	227	finally
        //   36	77	235	java/lang/InterruptedException
        //   103	109	248	java/io/IOException
        //   109	200	274	java/net/UnknownHostException
        //   262	271	274	java/net/UnknownHostException
        //   109	200	285	java/io/IOException
        //   262	271	285	java/io/IOException
        //   304	333	358	java/io/UnsupportedEncodingException
        //   304	333	371	java/io/IOException
        //   387	403	406	java/io/IOException
      }
      
      protected void onPostExecute(String paramAnonymousString)
      {
        if (PushService.this.state == PushService.ServiceState.DESTRUCTING)
        {
          PushService.access$202(PushService.this, PushService.ServiceState.STOPPED);
          return;
        }
        if (paramAnonymousString == null)
        {
          PushService.this.readInBackground(null);
          return;
        }
        JSONTokener localJSONTokener = new JSONTokener(paramAnonymousString);
        try
        {
          JSONObject localJSONObject = new JSONObject(localJSONTokener);
          ParsePushRouter.routePush(jdField_this, localJSONObject);
          PushService.this.readInBackground(this.reader);
          return;
        }
        catch (JSONException localJSONException)
        {
          Parse.logE("com.parse.PushService", "bad json: " + paramAnonymousString, localJSONException);
          PushService.this.readInBackground(this.reader);
        }
      }
    };
    this.task.execute(new Void[0]);
  }
  
  static void resetDelay()
  {
    delaySeconds = 0;
    consecutiveFailures = 0;
  }
  
  private void sendKeepAlive()
  {
    sendMessage("{}");
  }
  
  static boolean sendMessage(String paramString)
  {
    if (socket == null) {
      return false;
    }
    try
    {
      BufferedWriter localBufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), 8192);
      Parse.logD("com.parse.PushService", "subscribing with " + paramString + " @ " + socket.getPort());
      localBufferedWriter.write(paramString + "\n");
      localBufferedWriter.flush();
      return true;
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      Parse.logE("com.parse.PushService", "unsupported encoding", localUnsupportedEncodingException);
      return false;
    }
    catch (IOException localIOException)
    {
      Parse.logE("com.parse.PushService", "could not construct writer", localIOException);
    }
    return false;
  }
  
  private static boolean sendSubscriptionInformation(Context paramContext)
  {
    try
    {
      boolean bool = sendMessage(ParsePushRouter.getPushRequestJSON(paramContext).toString());
      return bool;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public static void setDefaultPushCallback(Context paramContext, Class<? extends Activity> paramClass)
  {
    String str = paramContext.getPackageName();
    PackageManager localPackageManager = paramContext.getPackageManager();
    try
    {
      ApplicationInfo localApplicationInfo = localPackageManager.getApplicationInfo(str, 0);
      setDefaultPushCallback(paramContext, paramClass, localApplicationInfo.icon);
      return;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      Parse.logE("com.parse.PushService", "missing package " + str, localNameNotFoundException);
    }
  }
  
  public static void setDefaultPushCallback(Context paramContext, Class<? extends Activity> paramClass, int paramInt)
  {
    ParsePushRouter.setDefaultRoute(paramContext, paramClass, paramInt);
    if (paramClass != null) {
      startService(paramContext);
    }
    while (ParsePushRouter.hasRoutes(paramContext)) {
      return;
    }
    Parse.logD("com.parse.PushService", "Shutting down push service. No remaining channels");
    paramContext.stopService(new Intent(paramContext, PushService.class));
  }
  
  static void sleep(int paramInt)
    throws InterruptedException
  {
    Parse.logV("com.parse.PushService", "Sleeping " + paramInt + " ms");
    Semaphore localSemaphore = sleepSemaphore;
    if (localSemaphore == null)
    {
      Thread.sleep(paramInt);
      return;
    }
    while (paramInt > 100)
    {
      localSemaphore.acquire(100);
      paramInt -= 100;
    }
    localSemaphore.acquire(paramInt);
  }
  
  private static void startService(Context paramContext)
  {
    try
    {
      Parse.logD("com.parse.PushService", "ensuring push service is started");
      if (paramContext.startService(new Intent(paramContext, PushService.class)) == null) {
        Parse.logE("com.parse.PushService", "Could not start the push service. Make sure that the XML tag <service android:name=\"com.parse.PushService\" /> is in your AndroidManifest.xml as a child of the <application> element.");
      }
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public static void startServiceIfRequired(Context paramContext)
  {
    if (!ParsePushRouter.hasRoutes(paramContext))
    {
      Parse.logW("com.parse.PushService", "No known push routes; will not start push service");
      return;
    }
    startService(paramContext);
  }
  
  public static void subscribe(Context paramContext, String paramString, Class<? extends Activity> paramClass)
  {
    String str = paramContext.getPackageName();
    PackageManager localPackageManager = paramContext.getPackageManager();
    try
    {
      ApplicationInfo localApplicationInfo = localPackageManager.getApplicationInfo(str, 0);
      subscribe(paramContext, paramString, paramClass, localApplicationInfo.icon);
      return;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      Parse.logE("com.parse.PushService", "missing package " + str, localNameNotFoundException);
    }
  }
  
  public static void subscribe(Context paramContext, String paramString, Class<? extends Activity> paramClass, int paramInt)
  {
    try
    {
      startService(paramContext);
      ParsePushRouter.addChannelRoute(paramContext, paramString, paramClass, paramInt);
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public static void unsubscribe(Context paramContext, String paramString)
  {
    try
    {
      ParsePushRouter.removeChannelRoute(paramContext, paramString);
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  static void usePort(int paramInt)
  {
    defaultPushPort = paramInt;
  }
  
  static void useServer(String paramString)
  {
    pushServer = paramString;
  }
  
  public IBinder onBind(Intent paramIntent)
  {
    throw new IllegalArgumentException("You cannot bind directly to the PushService. Use PushService.subscribe instead.");
  }
  
  public void onCreate()
  {
    super.onCreate();
    if (Parse.applicationContext == null)
    {
      Parse.logE("com.parse.PushService", "The Parse push service cannot start because Parse.initialize has not yet been called. If you call Parse.initialize from an Activity's onCreate, that call should instead be in the Application.onCreate. Be sure your Application class is registered in your AndroidManifest.xml with the android:name property of your <application> tag.");
      this.state = ServiceState.ABORTING;
      stopSelf();
      return;
    }
    this.state = ServiceState.RUNNING;
    Parse.logD("com.parse.PushService", "creating push service");
    this.pushPort = defaultPushPort;
    this.keepAliveTimer = new Timer("com.parse.PushService.keepAliveTimer", true);
    this.keepAliveTimer.schedule(new TimerTask()
    {
      public void run()
      {
        PushService.this.sendKeepAlive();
      }
    }, 1200000L, 1200000L);
    resetDelay();
    readInBackground(null);
  }
  
  /* Error */
  public void onDestroy()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 357	android/app/Service:onDestroy	()V
    //   4: ldc 8
    //   6: ldc_w 359
    //   9: invokestatic 179	com/parse/Parse:logD	(Ljava/lang/String;Ljava/lang/String;)V
    //   12: aload_0
    //   13: getfield 43	com/parse/PushService:state	Lcom/parse/PushService$ServiceState;
    //   16: getstatic 327	com/parse/PushService$ServiceState:ABORTING	Lcom/parse/PushService$ServiceState;
    //   19: if_acmpne +11 -> 30
    //   22: aload_0
    //   23: getstatic 41	com/parse/PushService$ServiceState:STOPPED	Lcom/parse/PushService$ServiceState;
    //   26: putfield 43	com/parse/PushService:state	Lcom/parse/PushService$ServiceState;
    //   29: return
    //   30: aload_0
    //   31: getfield 114	com/parse/PushService:task	Lcom/parse/os/ParseAsyncTask;
    //   34: iconst_1
    //   35: invokevirtual 363	com/parse/os/ParseAsyncTask:cancel	(Z)Z
    //   38: pop
    //   39: aload_0
    //   40: getfield 45	com/parse/PushService:keepAliveTimer	Ljava/util/Timer;
    //   43: invokevirtual 365	java/util/Timer:cancel	()V
    //   46: aload_0
    //   47: monitorenter
    //   48: aload_0
    //   49: getstatic 107	com/parse/PushService$ServiceState:DESTRUCTING	Lcom/parse/PushService$ServiceState;
    //   52: putfield 43	com/parse/PushService:state	Lcom/parse/PushService$ServiceState;
    //   55: getstatic 135	com/parse/PushService:socket	Ljava/net/Socket;
    //   58: astore_3
    //   59: aconst_null
    //   60: putstatic 135	com/parse/PushService:socket	Ljava/net/Socket;
    //   63: aload_0
    //   64: monitorexit
    //   65: aload_3
    //   66: ifnull -37 -> 29
    //   69: aload_3
    //   70: invokevirtual 368	java/net/Socket:close	()V
    //   73: return
    //   74: astore 4
    //   76: return
    //   77: astore_2
    //   78: aload_0
    //   79: monitorexit
    //   80: aload_2
    //   81: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	82	0	this	PushService
    //   77	4	2	localObject	Object
    //   58	12	3	localSocket	Socket
    //   74	1	4	localIOException	IOException
    // Exception table:
    //   from	to	target	type
    //   69	73	74	java/io/IOException
    //   48	65	77	finally
    //   78	80	77	finally
  }
  
  static enum ServiceState
  {
    static
    {
      ABORTING = new ServiceState("ABORTING", 1);
      DESTRUCTING = new ServiceState("DESTRUCTING", 2);
      RUNNING = new ServiceState("RUNNING", 3);
      ServiceState[] arrayOfServiceState = new ServiceState[4];
      arrayOfServiceState[0] = STOPPED;
      arrayOfServiceState[1] = ABORTING;
      arrayOfServiceState[2] = DESTRUCTING;
      arrayOfServiceState[3] = RUNNING;
      $VALUES = arrayOfServiceState;
    }
    
    private ServiceState() {}
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.PushService
 * JD-Core Version:    0.7.0.1
 */