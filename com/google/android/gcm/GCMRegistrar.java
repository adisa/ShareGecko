package com.google.android.gcm;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.os.Build.VERSION;
import android.util.Log;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class GCMRegistrar
{
  private static final String BACKOFF_MS = "backoff_ms";
  private static final int DEFAULT_BACKOFF_MS = 3000;
  public static final long DEFAULT_ON_SERVER_LIFESPAN_MS = 604800000L;
  private static final String GSF_PACKAGE = "com.google.android.gsf";
  private static final String PREFERENCES = "com.google.android.gcm";
  private static final String PROPERTY_APP_VERSION = "appVersion";
  private static final String PROPERTY_ON_SERVER = "onServer";
  private static final String PROPERTY_ON_SERVER_EXPIRATION_TIME = "onServerExpirationTime";
  private static final String PROPERTY_ON_SERVER_LIFESPAN = "onServerLifeSpan";
  private static final String PROPERTY_REG_ID = "regId";
  private static final String TAG = "GCMRegistrar";
  private static GCMBroadcastReceiver sRetryReceiver;
  private static String sRetryReceiverClassName;
  
  private GCMRegistrar()
  {
    throw new UnsupportedOperationException();
  }
  
  public static void checkDevice(Context paramContext)
  {
    int i = Build.VERSION.SDK_INT;
    if (i < 8) {
      throw new UnsupportedOperationException("Device must be at least API Level 8 (instead of " + i + ")");
    }
    PackageManager localPackageManager = paramContext.getPackageManager();
    try
    {
      localPackageManager.getPackageInfo("com.google.android.gsf", 0);
      return;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      throw new UnsupportedOperationException("Device does not have package com.google.android.gsf");
    }
  }
  
  public static void checkManifest(Context paramContext)
  {
    PackageManager localPackageManager = paramContext.getPackageManager();
    String str1 = paramContext.getPackageName();
    String str2 = str1 + ".permission.C2D_MESSAGE";
    ActivityInfo[] arrayOfActivityInfo;
    try
    {
      localPackageManager.getPermissionInfo(str2, 4096);
      PackageInfo localPackageInfo;
      if (!Log.isLoggable("GCMRegistrar", 2)) {
        break label195;
      }
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException1)
    {
      try
      {
        localPackageInfo = localPackageManager.getPackageInfo(str1, 2);
        arrayOfActivityInfo = localPackageInfo.receivers;
        if ((arrayOfActivityInfo != null) && (arrayOfActivityInfo.length != 0)) {
          break label150;
        }
        throw new IllegalStateException("No receiver for package " + str1);
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException2)
      {
        throw new IllegalStateException("Could not get receivers for package " + str1);
      }
      localNameNotFoundException1 = localNameNotFoundException1;
      throw new IllegalStateException("Application does not define permission " + str2);
    }
    label150:
    Log.v("GCMRegistrar", "number of receivers for " + str1 + ": " + arrayOfActivityInfo.length);
    label195:
    HashSet localHashSet = new HashSet();
    int i = arrayOfActivityInfo.length;
    for (int j = 0; j < i; j++)
    {
      ActivityInfo localActivityInfo = arrayOfActivityInfo[j];
      if ("com.google.android.c2dm.permission.SEND".equals(localActivityInfo.permission)) {
        localHashSet.add(localActivityInfo.name);
      }
    }
    if (localHashSet.isEmpty()) {
      throw new IllegalStateException("No receiver allowed to receive com.google.android.c2dm.permission.SEND");
    }
    checkReceiver(paramContext, localHashSet, "com.google.android.c2dm.intent.REGISTRATION");
    checkReceiver(paramContext, localHashSet, "com.google.android.c2dm.intent.RECEIVE");
  }
  
  private static void checkReceiver(Context paramContext, Set<String> paramSet, String paramString)
  {
    PackageManager localPackageManager = paramContext.getPackageManager();
    String str1 = paramContext.getPackageName();
    Intent localIntent = new Intent(paramString);
    localIntent.setPackage(str1);
    List localList = localPackageManager.queryBroadcastReceivers(localIntent, 32);
    if (localList.isEmpty()) {
      throw new IllegalStateException("No receivers for action " + paramString);
    }
    if (Log.isLoggable("GCMRegistrar", 2)) {
      Log.v("GCMRegistrar", "Found " + localList.size() + " receivers for action " + paramString);
    }
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      String str2 = ((ResolveInfo)localIterator.next()).activityInfo.name;
      if (!paramSet.contains(str2)) {
        throw new IllegalStateException("Receiver " + str2 + " is not set with permission " + "com.google.android.c2dm.permission.SEND");
      }
    }
  }
  
  static String clearRegistrationId(Context paramContext)
  {
    return setRegistrationId(paramContext, "");
  }
  
  private static int getAppVersion(Context paramContext)
  {
    try
    {
      int i = paramContext.getPackageManager().getPackageInfo(paramContext.getPackageName(), 0).versionCode;
      return i;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      throw new RuntimeException("Coult not get package name: " + localNameNotFoundException);
    }
  }
  
  static int getBackoff(Context paramContext)
  {
    return getGCMPreferences(paramContext).getInt("backoff_ms", 3000);
  }
  
  static String getFlatSenderIds(String... paramVarArgs)
  {
    if ((paramVarArgs == null) || (paramVarArgs.length == 0)) {
      throw new IllegalArgumentException("No senderIds");
    }
    StringBuilder localStringBuilder = new StringBuilder(paramVarArgs[0]);
    for (int i = 1; i < paramVarArgs.length; i++) {
      localStringBuilder.append(',').append(paramVarArgs[i]);
    }
    return localStringBuilder.toString();
  }
  
  private static SharedPreferences getGCMPreferences(Context paramContext)
  {
    return paramContext.getSharedPreferences("com.google.android.gcm", 0);
  }
  
  public static long getRegisterOnServerLifespan(Context paramContext)
  {
    return getGCMPreferences(paramContext).getLong("onServerLifeSpan", 604800000L);
  }
  
  public static String getRegistrationId(Context paramContext)
  {
    SharedPreferences localSharedPreferences = getGCMPreferences(paramContext);
    String str = localSharedPreferences.getString("regId", "");
    int i = localSharedPreferences.getInt("appVersion", -2147483648);
    int j = getAppVersion(paramContext);
    if ((i != -2147483648) && (i != j))
    {
      Log.v("GCMRegistrar", "App version changed from " + i + " to " + j + "; resetting registration id");
      clearRegistrationId(paramContext);
      str = "";
    }
    return str;
  }
  
  static void internalRegister(Context paramContext, String... paramVarArgs)
  {
    String str = getFlatSenderIds(paramVarArgs);
    Log.v("GCMRegistrar", "Registering app " + paramContext.getPackageName() + " of senders " + str);
    Intent localIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
    localIntent.setPackage("com.google.android.gsf");
    localIntent.putExtra("app", PendingIntent.getBroadcast(paramContext, 0, new Intent(), 0));
    localIntent.putExtra("sender", str);
    paramContext.startService(localIntent);
  }
  
  static void internalUnregister(Context paramContext)
  {
    Log.v("GCMRegistrar", "Unregistering app " + paramContext.getPackageName());
    Intent localIntent = new Intent("com.google.android.c2dm.intent.UNREGISTER");
    localIntent.setPackage("com.google.android.gsf");
    localIntent.putExtra("app", PendingIntent.getBroadcast(paramContext, 0, new Intent(), 0));
    paramContext.startService(localIntent);
  }
  
  public static boolean isRegistered(Context paramContext)
  {
    return getRegistrationId(paramContext).length() > 0;
  }
  
  public static boolean isRegisteredOnServer(Context paramContext)
  {
    SharedPreferences localSharedPreferences = getGCMPreferences(paramContext);
    boolean bool = localSharedPreferences.getBoolean("onServer", false);
    Log.v("GCMRegistrar", "Is registered on server: " + bool);
    if (bool)
    {
      long l = localSharedPreferences.getLong("onServerExpirationTime", -1L);
      if (System.currentTimeMillis() > l)
      {
        Log.v("GCMRegistrar", "flag expired on: " + new Timestamp(l));
        bool = false;
      }
    }
    return bool;
  }
  
  public static void onDestroy(Context paramContext)
  {
    try
    {
      if (sRetryReceiver != null)
      {
        Log.v("GCMRegistrar", "Unregistering receiver");
        paramContext.unregisterReceiver(sRetryReceiver);
        sRetryReceiver = null;
      }
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public static void register(Context paramContext, String... paramVarArgs)
  {
    resetBackoff(paramContext);
    internalRegister(paramContext, paramVarArgs);
  }
  
  static void resetBackoff(Context paramContext)
  {
    Log.d("GCMRegistrar", "resetting backoff for " + paramContext.getPackageName());
    setBackoff(paramContext, 3000);
  }
  
  static void setBackoff(Context paramContext, int paramInt)
  {
    SharedPreferences.Editor localEditor = getGCMPreferences(paramContext).edit();
    localEditor.putInt("backoff_ms", paramInt);
    localEditor.commit();
  }
  
  public static void setRegisterOnServerLifespan(Context paramContext, long paramLong)
  {
    SharedPreferences.Editor localEditor = getGCMPreferences(paramContext).edit();
    localEditor.putLong("onServerLifeSpan", paramLong);
    localEditor.commit();
  }
  
  public static void setRegisteredOnServer(Context paramContext, boolean paramBoolean)
  {
    SharedPreferences.Editor localEditor = getGCMPreferences(paramContext).edit();
    localEditor.putBoolean("onServer", paramBoolean);
    long l = getRegisterOnServerLifespan(paramContext) + System.currentTimeMillis();
    Log.v("GCMRegistrar", "Setting registeredOnServer status as " + paramBoolean + " until " + new Timestamp(l));
    localEditor.putLong("onServerExpirationTime", l);
    localEditor.commit();
  }
  
  static String setRegistrationId(Context paramContext, String paramString)
  {
    SharedPreferences localSharedPreferences = getGCMPreferences(paramContext);
    String str = localSharedPreferences.getString("regId", "");
    int i = getAppVersion(paramContext);
    Log.v("GCMRegistrar", "Saving regId on app version " + i);
    SharedPreferences.Editor localEditor = localSharedPreferences.edit();
    localEditor.putString("regId", paramString);
    localEditor.putInt("appVersion", i);
    localEditor.commit();
    return str;
  }
  
  /* Error */
  static void setRetryBroadcastReceiver(Context paramContext)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 361	com/google/android/gcm/GCMRegistrar:sRetryReceiver	Lcom/google/android/gcm/GCMBroadcastReceiver;
    //   6: ifnonnull +97 -> 103
    //   9: getstatic 424	com/google/android/gcm/GCMRegistrar:sRetryReceiverClassName	Ljava/lang/String;
    //   12: ifnonnull +95 -> 107
    //   15: ldc 39
    //   17: ldc_w 426
    //   20: invokestatic 429	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   23: pop
    //   24: new 431	com/google/android/gcm/GCMBroadcastReceiver
    //   27: dup
    //   28: invokespecial 432	com/google/android/gcm/GCMBroadcastReceiver:<init>	()V
    //   31: putstatic 361	com/google/android/gcm/GCMRegistrar:sRetryReceiver	Lcom/google/android/gcm/GCMBroadcastReceiver;
    //   34: aload_0
    //   35: invokevirtual 97	android/content/Context:getPackageName	()Ljava/lang/String;
    //   38: astore 4
    //   40: new 434	android/content/IntentFilter
    //   43: dup
    //   44: ldc_w 436
    //   47: invokespecial 437	android/content/IntentFilter:<init>	(Ljava/lang/String;)V
    //   50: astore 5
    //   52: aload 5
    //   54: aload 4
    //   56: invokevirtual 440	android/content/IntentFilter:addCategory	(Ljava/lang/String;)V
    //   59: new 60	java/lang/StringBuilder
    //   62: dup
    //   63: invokespecial 61	java/lang/StringBuilder:<init>	()V
    //   66: aload 4
    //   68: invokevirtual 67	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   71: ldc 99
    //   73: invokevirtual 67	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   76: invokevirtual 76	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   79: astore 6
    //   81: ldc 39
    //   83: ldc_w 442
    //   86: invokestatic 132	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
    //   89: pop
    //   90: aload_0
    //   91: getstatic 361	com/google/android/gcm/GCMRegistrar:sRetryReceiver	Lcom/google/android/gcm/GCMBroadcastReceiver;
    //   94: aload 5
    //   96: aload 6
    //   98: aconst_null
    //   99: invokevirtual 446	android/content/Context:registerReceiver	(Landroid/content/BroadcastReceiver;Landroid/content/IntentFilter;Ljava/lang/String;Landroid/os/Handler;)Landroid/content/Intent;
    //   102: pop
    //   103: ldc 2
    //   105: monitorexit
    //   106: return
    //   107: getstatic 424	com/google/android/gcm/GCMRegistrar:sRetryReceiverClassName	Ljava/lang/String;
    //   110: invokestatic 452	java/lang/Class:forName	(Ljava/lang/String;)Ljava/lang/Class;
    //   113: invokevirtual 455	java/lang/Class:newInstance	()Ljava/lang/Object;
    //   116: checkcast 431	com/google/android/gcm/GCMBroadcastReceiver
    //   119: putstatic 361	com/google/android/gcm/GCMRegistrar:sRetryReceiver	Lcom/google/android/gcm/GCMBroadcastReceiver;
    //   122: goto -88 -> 34
    //   125: astore_2
    //   126: ldc 39
    //   128: new 60	java/lang/StringBuilder
    //   131: dup
    //   132: invokespecial 61	java/lang/StringBuilder:<init>	()V
    //   135: ldc_w 457
    //   138: invokevirtual 67	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   141: getstatic 424	com/google/android/gcm/GCMRegistrar:sRetryReceiverClassName	Ljava/lang/String;
    //   144: invokevirtual 67	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   147: ldc_w 459
    //   150: invokevirtual 67	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   153: ldc_w 431
    //   156: invokevirtual 462	java/lang/Class:getName	()Ljava/lang/String;
    //   159: invokevirtual 67	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   162: ldc_w 464
    //   165: invokevirtual 67	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   168: invokevirtual 76	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   171: invokestatic 429	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   174: pop
    //   175: new 431	com/google/android/gcm/GCMBroadcastReceiver
    //   178: dup
    //   179: invokespecial 432	com/google/android/gcm/GCMBroadcastReceiver:<init>	()V
    //   182: putstatic 361	com/google/android/gcm/GCMRegistrar:sRetryReceiver	Lcom/google/android/gcm/GCMBroadcastReceiver;
    //   185: goto -151 -> 34
    //   188: astore_1
    //   189: ldc 2
    //   191: monitorexit
    //   192: aload_1
    //   193: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	194	0	paramContext	Context
    //   188	5	1	localObject	Object
    //   125	1	2	localException	java.lang.Exception
    //   38	29	4	str1	String
    //   50	45	5	localIntentFilter	android.content.IntentFilter
    //   79	18	6	str2	String
    // Exception table:
    //   from	to	target	type
    //   107	122	125	java/lang/Exception
    //   3	34	188	finally
    //   34	103	188	finally
    //   107	122	188	finally
    //   126	185	188	finally
  }
  
  static void setRetryReceiverClassName(String paramString)
  {
    Log.v("GCMRegistrar", "Setting the name of retry receiver class to " + paramString);
    sRetryReceiverClassName = paramString;
  }
  
  public static void unregister(Context paramContext)
  {
    resetBackoff(paramContext);
    internalUnregister(paramContext);
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.google.android.gcm.GCMRegistrar
 * JD-Core Version:    0.7.0.1
 */