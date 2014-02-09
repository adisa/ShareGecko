package com.parse;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class ParsePushRouter
{
  private static final Pattern CHANNEL_PATTERN;
  private static final String LEGACY_ROUTE_LOCATION = "persistentCallbacks";
  private static final String STATE_LOCATION = "pushState";
  private static final String TAG = "com.parse.ParsePushRouter";
  static Map<String, CallbackFactory> channelRoutes;
  static Set<String> channels;
  static CallbackFactory defaultRoute;
  private static boolean hasLoadedStateFromDisk;
  static JSONObject history;
  static String ignoreAfter;
  static String lastTime;
  static int maxHistory;
  
  static
  {
    if (!ParsePushRouter.class.desiredAssertionStatus()) {}
    for (boolean bool = true;; bool = false)
    {
      $assertionsDisabled = bool;
      CHANNEL_PATTERN = Pattern.compile("^$|^[a-zA-Z][A-Za-z0-9_-]*$");
      channelRoutes = null;
      defaultRoute = null;
      channels = new HashSet();
      lastTime = null;
      ignoreAfter = null;
      history = null;
      maxHistory = 10;
      hasLoadedStateFromDisk = false;
      return;
    }
  }
  
  static boolean addChannelRoute(Context paramContext, String paramString, Class<? extends Activity> paramClass, int paramInt)
  {
    if (paramString == null) {
      throw new NullPointerException("invalid channel: you cannot subscribe to null");
    }
    if (!CHANNEL_PATTERN.matcher(paramString).matches()) {
      throw new IllegalArgumentException("invalid channel name: " + paramString);
    }
    JSONObject localJSONObject = dataForActivity(paramContext, paramClass, paramInt);
    if (localJSONObject == null) {
      return false;
    }
    return addChannelRoute(paramContext, paramString, localJSONObject, StandardPushCallback.class);
  }
  
  static boolean addChannelRoute(Context paramContext, String paramString, JSONObject paramJSONObject, Class<? extends PushCallback> paramClass)
  {
    for (;;)
    {
      try
      {
        ensureStateIsLoaded(paramContext);
        try
        {
          JSONObject localJSONObject = new JSONObject(paramJSONObject.toString());
          if (channelRoutes.put(paramString, new CallbackFactory(paramClass, localJSONObject)) != null) {
            continue;
          }
          bool = true;
          if (bool)
          {
            ParseInstallation localParseInstallation = new ParseInstallation();
            localParseInstallation.addUnique("channels", paramString);
            saveEventually(paramContext, localParseInstallation);
          }
        }
        catch (JSONException localJSONException)
        {
          Parse.logE("com.parse.ParsePushRouter", "Impossible exception when deserializing a serialized JSON string: " + localJSONException.getMessage());
          boolean bool = false;
          continue;
        }
        return bool;
      }
      finally {}
      bool = false;
    }
  }
  
  static void addSingletonRoute(Context paramContext, String paramString, PushCallback paramPushCallback)
  {
    ensureStateIsLoaded(paramContext);
    if (paramString != null)
    {
      channelRoutes.put(paramString, new SingletonFactory(paramPushCallback));
      return;
    }
    defaultRoute = new SingletonFactory(paramPushCallback);
  }
  
  static void clearStateFromDisk(Context paramContext)
  {
    clearStateFromMemory();
    ParseObject.deleteDiskObject(paramContext, "persistentCallbacks");
    ParseObject.deleteDiskObject(paramContext, "pushState");
  }
  
  static void clearStateFromMemory()
  {
    hasLoadedStateFromDisk = false;
    channelRoutes = null;
    defaultRoute = null;
    lastTime = null;
    channels = new HashSet();
    history = null;
  }
  
  static JSONObject dataForActivity(Context paramContext, Class<? extends Activity> paramClass, int paramInt)
  {
    getApplicationId(paramContext);
    String str1 = paramContext.getPackageName();
    PackageManager localPackageManager = paramContext.getPackageManager();
    for (;;)
    {
      try
      {
        ApplicationInfo localApplicationInfo = localPackageManager.getApplicationInfo(str1, 0);
        CharSequence localCharSequence = localApplicationInfo.loadLabel(localPackageManager);
        String str3;
        String str4;
        JSONObject localJSONObject;
        if (localCharSequence != null)
        {
          str2 = localCharSequence.toString();
          ComponentName localComponentName = new ComponentName(paramContext, paramClass);
          str3 = localComponentName.getClassName();
          str4 = localComponentName.getPackageName();
          localJSONObject = new JSONObject();
        }
        String str2 = null;
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        try
        {
          localJSONObject.put("icon", paramInt);
          localJSONObject.put("appName", str2);
          localJSONObject.put("activityClass", str3);
          localJSONObject.put("activityPackage", str4);
          return localJSONObject;
        }
        catch (JSONException localJSONException)
        {
          throw new RuntimeException(localJSONException.getMessage());
        }
        localNameNotFoundException = localNameNotFoundException;
        Parse.logE("com.parse.ParsePushRouter", "missing package " + str1, localNameNotFoundException);
        return null;
      }
    }
  }
  
  /* Error */
  static void ensureStateIsLoaded(Context paramContext)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 75	com/parse/ParsePushRouter:hasLoadedStateFromDisk	Z
    //   6: istore_2
    //   7: iload_2
    //   8: ifeq +7 -> 15
    //   11: ldc 2
    //   13: monitorexit
    //   14: return
    //   15: iconst_1
    //   16: putstatic 75	com/parse/ParsePushRouter:hasLoadedStateFromDisk	Z
    //   19: aconst_null
    //   20: putstatic 58	com/parse/ParsePushRouter:defaultRoute	Lcom/parse/ParsePushRouter$CallbackFactory;
    //   23: new 255	java/util/HashMap
    //   26: dup
    //   27: invokespecial 256	java/util/HashMap:<init>	()V
    //   30: putstatic 56	com/parse/ParsePushRouter:channelRoutes	Ljava/util/Map;
    //   33: new 136	org/json/JSONObject
    //   36: dup
    //   37: invokespecial 227	org/json/JSONObject:<init>	()V
    //   40: putstatic 71	com/parse/ParsePushRouter:history	Lorg/json/JSONObject;
    //   43: aload_0
    //   44: ldc 13
    //   46: invokestatic 260	com/parse/ParseObject:getDiskObject	(Landroid/content/Context;Ljava/lang/String;)Lorg/json/JSONObject;
    //   49: astore_3
    //   50: aload_3
    //   51: ifnull +107 -> 158
    //   54: ldc 19
    //   56: new 108	java/lang/StringBuilder
    //   59: dup
    //   60: invokespecial 109	java/lang/StringBuilder:<init>	()V
    //   63: ldc_w 262
    //   66: invokevirtual 115	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   69: aload_3
    //   70: invokevirtual 137	org/json/JSONObject:toString	()Ljava/lang/String;
    //   73: invokevirtual 115	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   76: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   79: invokestatic 265	com/parse/Parse:logD	(Ljava/lang/String;Ljava/lang/String;)V
    //   82: aload_3
    //   83: invokestatic 269	com/parse/ParsePushRouter:parseChannelRoutes	(Lorg/json/JSONObject;)V
    //   86: getstatic 65	com/parse/ParsePushRouter:channels	Ljava/util/Set;
    //   89: astore 4
    //   91: aload 4
    //   93: monitorenter
    //   94: getstatic 65	com/parse/ParsePushRouter:channels	Ljava/util/Set;
    //   97: invokeinterface 274 1 0
    //   102: getstatic 65	com/parse/ParsePushRouter:channels	Ljava/util/Set;
    //   105: getstatic 56	com/parse/ParsePushRouter:channelRoutes	Ljava/util/Map;
    //   108: invokeinterface 278 1 0
    //   113: invokeinterface 282 2 0
    //   118: pop
    //   119: aload 4
    //   121: monitorexit
    //   122: new 151	com/parse/ParseInstallation
    //   125: dup
    //   126: invokespecial 152	com/parse/ParseInstallation:<init>	()V
    //   129: astore 7
    //   131: aload 7
    //   133: ldc 153
    //   135: getstatic 56	com/parse/ParsePushRouter:channelRoutes	Ljava/util/Map;
    //   138: invokeinterface 278 1 0
    //   143: invokevirtual 286	com/parse/ParseInstallation:addAllUnique	(Ljava/lang/String;Ljava/util/Collection;)V
    //   146: aload_0
    //   147: aload 7
    //   149: invokestatic 161	com/parse/ParsePushRouter:saveEventually	(Landroid/content/Context;Lcom/parse/ParseInstallation;)V
    //   152: aload_0
    //   153: ldc 13
    //   155: invokestatic 189	com/parse/ParseObject:deleteDiskObject	(Landroid/content/Context;Ljava/lang/String;)V
    //   158: aload_0
    //   159: ldc 16
    //   161: invokestatic 260	com/parse/ParseObject:getDiskObject	(Landroid/content/Context;Ljava/lang/String;)Lorg/json/JSONObject;
    //   164: astore 8
    //   166: aload 8
    //   168: ifnull -157 -> 11
    //   171: aload 8
    //   173: ldc_w 288
    //   176: invokevirtual 292	org/json/JSONObject:optJSONObject	(Ljava/lang/String;)Lorg/json/JSONObject;
    //   179: invokestatic 269	com/parse/ParsePushRouter:parseChannelRoutes	(Lorg/json/JSONObject;)V
    //   182: aload 8
    //   184: ldc_w 293
    //   187: invokevirtual 292	org/json/JSONObject:optJSONObject	(Ljava/lang/String;)Lorg/json/JSONObject;
    //   190: astore 9
    //   192: aconst_null
    //   193: astore 10
    //   195: aload 9
    //   197: ifnull +14 -> 211
    //   200: new 140	com/parse/ParsePushRouter$CallbackFactory
    //   203: dup
    //   204: aload 9
    //   206: invokespecial 295	com/parse/ParsePushRouter$CallbackFactory:<init>	(Lorg/json/JSONObject;)V
    //   209: astore 10
    //   211: aload 10
    //   213: putstatic 58	com/parse/ParsePushRouter:defaultRoute	Lcom/parse/ParsePushRouter$CallbackFactory;
    //   216: aload 8
    //   218: ldc_w 296
    //   221: aconst_null
    //   222: invokevirtual 300	org/json/JSONObject:optString	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   225: putstatic 67	com/parse/ParsePushRouter:lastTime	Ljava/lang/String;
    //   228: aload 8
    //   230: ldc_w 301
    //   233: aconst_null
    //   234: invokevirtual 300	org/json/JSONObject:optString	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   237: putstatic 69	com/parse/ParsePushRouter:ignoreAfter	Ljava/lang/String;
    //   240: aload 8
    //   242: ldc_w 303
    //   245: invokevirtual 307	org/json/JSONObject:optJSONArray	(Ljava/lang/String;)Lorg/json/JSONArray;
    //   248: astore 12
    //   250: aload 12
    //   252: ifnull +152 -> 404
    //   255: new 309	java/util/ArrayList
    //   258: dup
    //   259: invokespecial 310	java/util/ArrayList:<init>	()V
    //   262: astore 13
    //   264: iconst_0
    //   265: istore 14
    //   267: iload 14
    //   269: aload 12
    //   271: invokevirtual 316	org/json/JSONArray:length	()I
    //   274: if_icmpge +106 -> 380
    //   277: aload 13
    //   279: aload 12
    //   281: iload 14
    //   283: invokevirtual 319	org/json/JSONArray:optString	(I)Ljava/lang/String;
    //   286: invokeinterface 325 2 0
    //   291: pop
    //   292: iinc 14 1
    //   295: goto -28 -> 267
    //   298: astore 5
    //   300: aload 4
    //   302: monitorexit
    //   303: aload 5
    //   305: athrow
    //   306: astore_1
    //   307: ldc 2
    //   309: monitorexit
    //   310: aload_1
    //   311: athrow
    //   312: astore 32
    //   314: ldc 19
    //   316: new 108	java/lang/StringBuilder
    //   319: dup
    //   320: invokespecial 109	java/lang/StringBuilder:<init>	()V
    //   323: ldc_w 327
    //   326: invokevirtual 115	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   329: aload 32
    //   331: invokevirtual 328	java/lang/ClassNotFoundException:getMessage	()Ljava/lang/String;
    //   334: invokevirtual 115	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   337: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   340: invokestatic 172	com/parse/Parse:logE	(Ljava/lang/String;Ljava/lang/String;)V
    //   343: goto -127 -> 216
    //   346: astore 11
    //   348: ldc 19
    //   350: new 108	java/lang/StringBuilder
    //   353: dup
    //   354: invokespecial 109	java/lang/StringBuilder:<init>	()V
    //   357: ldc_w 330
    //   360: invokevirtual 115	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   363: aload 11
    //   365: invokevirtual 331	java/lang/ClassCastException:getMessage	()Ljava/lang/String;
    //   368: invokevirtual 115	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   371: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   374: invokestatic 172	com/parse/Parse:logE	(Ljava/lang/String;Ljava/lang/String;)V
    //   377: goto -161 -> 216
    //   380: new 151	com/parse/ParseInstallation
    //   383: dup
    //   384: invokespecial 152	com/parse/ParseInstallation:<init>	()V
    //   387: astore 15
    //   389: aload 15
    //   391: ldc 153
    //   393: aload 13
    //   395: invokevirtual 157	com/parse/ParseInstallation:addUnique	(Ljava/lang/String;Ljava/lang/Object;)V
    //   398: aload_0
    //   399: aload 15
    //   401: invokestatic 161	com/parse/ParsePushRouter:saveEventually	(Landroid/content/Context;Lcom/parse/ParseInstallation;)V
    //   404: aload 8
    //   406: ldc_w 333
    //   409: invokevirtual 307	org/json/JSONObject:optJSONArray	(Ljava/lang/String;)Lorg/json/JSONArray;
    //   412: astore 16
    //   414: aload 16
    //   416: ifnull +70 -> 486
    //   419: new 309	java/util/ArrayList
    //   422: dup
    //   423: invokespecial 310	java/util/ArrayList:<init>	()V
    //   426: astore 17
    //   428: iconst_0
    //   429: istore 18
    //   431: iload 18
    //   433: aload 16
    //   435: invokevirtual 316	org/json/JSONArray:length	()I
    //   438: if_icmpge +24 -> 462
    //   441: aload 17
    //   443: aload 16
    //   445: iload 18
    //   447: invokevirtual 319	org/json/JSONArray:optString	(I)Ljava/lang/String;
    //   450: invokeinterface 325 2 0
    //   455: pop
    //   456: iinc 18 1
    //   459: goto -28 -> 431
    //   462: new 151	com/parse/ParseInstallation
    //   465: dup
    //   466: invokespecial 152	com/parse/ParseInstallation:<init>	()V
    //   469: astore 19
    //   471: aload 19
    //   473: ldc 153
    //   475: aload 17
    //   477: invokevirtual 336	com/parse/ParseInstallation:removeAll	(Ljava/lang/String;Ljava/util/Collection;)V
    //   480: aload_0
    //   481: aload 19
    //   483: invokestatic 161	com/parse/ParsePushRouter:saveEventually	(Landroid/content/Context;Lcom/parse/ParseInstallation;)V
    //   486: aload 8
    //   488: ldc_w 338
    //   491: invokevirtual 342	org/json/JSONObject:has	(Ljava/lang/String;)Z
    //   494: ifeq +102 -> 596
    //   497: new 151	com/parse/ParseInstallation
    //   500: dup
    //   501: invokespecial 152	com/parse/ParseInstallation:<init>	()V
    //   504: astore 20
    //   506: aload 20
    //   508: aload 8
    //   510: ldc_w 338
    //   513: invokevirtual 292	org/json/JSONObject:optJSONObject	(Ljava/lang/String;)Lorg/json/JSONObject;
    //   516: invokevirtual 345	com/parse/ParseInstallation:mergeAfterFetch	(Lorg/json/JSONObject;)V
    //   519: aload 20
    //   521: ldc 153
    //   523: invokevirtual 349	com/parse/ParseInstallation:getList	(Ljava/lang/String;)Ljava/util/List;
    //   526: astore 21
    //   528: aload 21
    //   530: ifnull +33 -> 563
    //   533: getstatic 65	com/parse/ParsePushRouter:channels	Ljava/util/Set;
    //   536: astore 22
    //   538: aload 22
    //   540: monitorenter
    //   541: getstatic 65	com/parse/ParsePushRouter:channels	Ljava/util/Set;
    //   544: invokeinterface 274 1 0
    //   549: getstatic 65	com/parse/ParsePushRouter:channels	Ljava/util/Set;
    //   552: aload 21
    //   554: invokeinterface 282 2 0
    //   559: pop
    //   560: aload 22
    //   562: monitorexit
    //   563: aload 8
    //   565: ldc_w 350
    //   568: invokevirtual 342	org/json/JSONObject:has	(Ljava/lang/String;)Z
    //   571: ifeq -560 -> 11
    //   574: aload 8
    //   576: ldc_w 350
    //   579: invokevirtual 292	org/json/JSONObject:optJSONObject	(Ljava/lang/String;)Lorg/json/JSONObject;
    //   582: putstatic 71	com/parse/ParsePushRouter:history	Lorg/json/JSONObject;
    //   585: goto -574 -> 11
    //   588: astore 23
    //   590: aload 22
    //   592: monitorexit
    //   593: aload 23
    //   595: athrow
    //   596: aload 8
    //   598: ldc 153
    //   600: invokevirtual 342	org/json/JSONObject:has	(Ljava/lang/String;)Z
    //   603: ifeq -40 -> 563
    //   606: aload 8
    //   608: ldc 153
    //   610: invokevirtual 307	org/json/JSONObject:optJSONArray	(Ljava/lang/String;)Lorg/json/JSONArray;
    //   613: astore 25
    //   615: getstatic 65	com/parse/ParsePushRouter:channels	Ljava/util/Set;
    //   618: astore 26
    //   620: aload 26
    //   622: monitorenter
    //   623: iconst_0
    //   624: istore 27
    //   626: iload 27
    //   628: aload 25
    //   630: invokevirtual 316	org/json/JSONArray:length	()I
    //   633: if_icmpge +25 -> 658
    //   636: getstatic 65	com/parse/ParsePushRouter:channels	Ljava/util/Set;
    //   639: aload 25
    //   641: iload 27
    //   643: invokevirtual 319	org/json/JSONArray:optString	(I)Ljava/lang/String;
    //   646: invokeinterface 351 2 0
    //   651: pop
    //   652: iinc 27 1
    //   655: goto -29 -> 626
    //   658: aload 26
    //   660: monitorexit
    //   661: goto -98 -> 563
    //   664: astore 28
    //   666: aload 26
    //   668: monitorexit
    //   669: aload 28
    //   671: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	672	0	paramContext	Context
    //   306	5	1	localObject1	Object
    //   6	2	2	bool	boolean
    //   49	34	3	localJSONObject1	JSONObject
    //   298	6	5	localObject2	Object
    //   129	19	7	localParseInstallation1	ParseInstallation
    //   164	443	8	localJSONObject2	JSONObject
    //   190	15	9	localJSONObject3	JSONObject
    //   193	19	10	localCallbackFactory	CallbackFactory
    //   346	18	11	localClassCastException	ClassCastException
    //   248	32	12	localJSONArray1	JSONArray
    //   262	132	13	localArrayList1	java.util.ArrayList
    //   265	28	14	i	int
    //   387	13	15	localParseInstallation2	ParseInstallation
    //   412	32	16	localJSONArray2	JSONArray
    //   426	50	17	localArrayList2	java.util.ArrayList
    //   429	28	18	j	int
    //   469	13	19	localParseInstallation3	ParseInstallation
    //   504	16	20	localParseInstallation4	ParseInstallation
    //   526	27	21	localList	List
    //   588	6	23	localObject3	Object
    //   613	27	25	localJSONArray3	JSONArray
    //   618	49	26	localSet3	Set
    //   624	29	27	k	int
    //   664	6	28	localObject4	Object
    //   312	18	32	localClassNotFoundException	ClassNotFoundException
    // Exception table:
    //   from	to	target	type
    //   94	122	298	finally
    //   300	303	298	finally
    //   3	7	306	finally
    //   15	50	306	finally
    //   54	94	306	finally
    //   122	158	306	finally
    //   158	166	306	finally
    //   171	192	306	finally
    //   200	211	306	finally
    //   211	216	306	finally
    //   216	250	306	finally
    //   255	264	306	finally
    //   267	292	306	finally
    //   303	306	306	finally
    //   314	343	306	finally
    //   348	377	306	finally
    //   380	404	306	finally
    //   404	414	306	finally
    //   419	428	306	finally
    //   431	456	306	finally
    //   462	486	306	finally
    //   486	528	306	finally
    //   533	541	306	finally
    //   563	585	306	finally
    //   593	596	306	finally
    //   596	623	306	finally
    //   669	672	306	finally
    //   200	211	312	java/lang/ClassNotFoundException
    //   211	216	312	java/lang/ClassNotFoundException
    //   200	211	346	java/lang/ClassCastException
    //   211	216	346	java/lang/ClassCastException
    //   541	563	588	finally
    //   590	593	588	finally
    //   626	652	664	finally
    //   658	661	664	finally
    //   666	669	664	finally
  }
  
  /* Error */
  static String getApplicationId(Context paramContext)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: aload_0
    //   4: ldc_w 353
    //   7: invokestatic 260	com/parse/ParseObject:getDiskObject	(Landroid/content/Context;Ljava/lang/String;)Lorg/json/JSONObject;
    //   10: astore_2
    //   11: aload_2
    //   12: ifnonnull +11 -> 23
    //   15: new 136	org/json/JSONObject
    //   18: dup
    //   19: invokespecial 227	org/json/JSONObject:<init>	()V
    //   22: astore_2
    //   23: aload_2
    //   24: ldc_w 355
    //   27: invokevirtual 358	org/json/JSONObject:optString	(Ljava/lang/String;)Ljava/lang/String;
    //   30: astore_3
    //   31: aload_3
    //   32: ldc_w 360
    //   35: if_acmpeq +8 -> 43
    //   38: ldc 2
    //   40: monitorexit
    //   41: aload_3
    //   42: areturn
    //   43: invokestatic 362	com/parse/ParseObject:getApplicationId	()Ljava/lang/String;
    //   46: astore 4
    //   48: aload_2
    //   49: ldc_w 355
    //   52: aload 4
    //   54: invokevirtual 237	org/json/JSONObject:put	(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;
    //   57: pop
    //   58: aload_0
    //   59: ldc_w 353
    //   62: aload_2
    //   63: invokestatic 366	com/parse/ParseObject:saveDiskObject	(Landroid/content/Context;Ljava/lang/String;Lorg/json/JSONObject;)V
    //   66: aload 4
    //   68: astore_3
    //   69: goto -31 -> 38
    //   72: astore 5
    //   74: ldc 19
    //   76: ldc_w 368
    //   79: aload 5
    //   81: invokestatic 246	com/parse/Parse:logE	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   84: goto -26 -> 58
    //   87: astore_1
    //   88: ldc 2
    //   90: monitorexit
    //   91: aload_1
    //   92: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	93	0	paramContext	Context
    //   87	5	1	localObject1	Object
    //   10	53	2	localJSONObject	JSONObject
    //   30	39	3	localObject2	Object
    //   46	21	4	str	String
    //   72	8	5	localJSONException	JSONException
    // Exception table:
    //   from	to	target	type
    //   48	58	72	org/json/JSONException
    //   3	11	87	finally
    //   15	23	87	finally
    //   23	31	87	finally
    //   43	48	87	finally
    //   48	58	87	finally
    //   58	66	87	finally
    //   74	84	87	finally
  }
  
  static JSONObject getPushRequestJSON(Context paramContext)
  {
    for (;;)
    {
      JSONObject localJSONObject;
      JSONArray localJSONArray;
      try
      {
        ensureStateIsLoaded(paramContext);
        localJSONObject = new JSONObject();
        try
        {
          localJSONObject.put("installation_id", ParseInstallation.getCurrentInstallation().getInstallationId());
          localJSONObject.put("oauth_key", getApplicationId(paramContext));
          localJSONObject.put("v", "a1.1.9");
          if (lastTime != null) {
            continue;
          }
          localJSONObject.put("last", JSONObject.NULL);
          if (history.length() == 0) {
            break label168;
          }
          localJSONArray = new JSONArray();
          Iterator localIterator = history.keys();
          if (!localIterator.hasNext()) {
            break label158;
          }
          localJSONArray.put(localIterator.next());
          continue;
        }
        catch (JSONException localJSONException)
        {
          Parse.logE("com.parse.ParsePushRouter", "unexpected JSONException", localJSONException);
          localJSONObject = null;
        }
        return localJSONObject;
      }
      finally {}
      localJSONObject.put("last", lastTime);
      continue;
      label158:
      localJSONObject.put("last_seen", localJSONArray);
      label168:
      localJSONObject.putOpt("ignore_after", ignoreAfter);
    }
  }
  
  static Set<String> getSubscriptions(Context paramContext)
  {
    try
    {
      ensureStateIsLoaded(paramContext);
      Set localSet = Collections.unmodifiableSet(channels);
      return localSet;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  /* Error */
  private static void handlePushData(PushService paramPushService, String paramString, JSONObject paramJSONObject)
  {
    // Byte code:
    //   0: ldc_w 434
    //   3: monitorenter
    //   4: getstatic 56	com/parse/ParsePushRouter:channelRoutes	Ljava/util/Map;
    //   7: aload_1
    //   8: invokeinterface 438 2 0
    //   13: checkcast 140	com/parse/ParsePushRouter$CallbackFactory
    //   16: astore 4
    //   18: aload 4
    //   20: ifnonnull +53 -> 73
    //   23: getstatic 58	com/parse/ParsePushRouter:defaultRoute	Lcom/parse/ParsePushRouter$CallbackFactory;
    //   26: ifnonnull +42 -> 68
    //   29: ldc 19
    //   31: new 108	java/lang/StringBuilder
    //   34: dup
    //   35: invokespecial 109	java/lang/StringBuilder:<init>	()V
    //   38: ldc_w 440
    //   41: invokevirtual 115	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   44: aload_2
    //   45: invokevirtual 137	org/json/JSONObject:toString	()Ljava/lang/String;
    //   48: invokevirtual 115	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   51: ldc_w 442
    //   54: invokevirtual 115	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   57: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   60: invokestatic 445	com/parse/Parse:logW	(Ljava/lang/String;Ljava/lang/String;)V
    //   63: ldc_w 434
    //   66: monitorexit
    //   67: return
    //   68: getstatic 58	com/parse/ParsePushRouter:defaultRoute	Lcom/parse/ParsePushRouter$CallbackFactory;
    //   71: astore 4
    //   73: aload 4
    //   75: invokevirtual 449	com/parse/ParsePushRouter$CallbackFactory:newCallback	()Lcom/parse/PushCallback;
    //   78: astore 7
    //   80: ldc_w 434
    //   83: monitorexit
    //   84: ldc 19
    //   86: new 108	java/lang/StringBuilder
    //   89: dup
    //   90: invokespecial 109	java/lang/StringBuilder:<init>	()V
    //   93: ldc_w 451
    //   96: invokevirtual 115	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   99: aload_1
    //   100: invokevirtual 115	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   103: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   106: invokestatic 265	com/parse/Parse:logD	(Ljava/lang/String;Ljava/lang/String;)V
    //   109: aload 7
    //   111: aload_0
    //   112: invokevirtual 457	com/parse/PushCallback:setService	(Landroid/app/Service;)V
    //   115: aload 7
    //   117: aload_2
    //   118: invokevirtual 460	com/parse/PushCallback:setPushData	(Lorg/json/JSONObject;)V
    //   121: aload 7
    //   123: aload_1
    //   124: invokevirtual 463	com/parse/PushCallback:setChannel	(Ljava/lang/String;)V
    //   127: aload 7
    //   129: invokevirtual 466	com/parse/PushCallback:run	()V
    //   132: return
    //   133: astore 6
    //   135: ldc 19
    //   137: new 108	java/lang/StringBuilder
    //   140: dup
    //   141: invokespecial 109	java/lang/StringBuilder:<init>	()V
    //   144: ldc_w 468
    //   147: invokevirtual 115	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   150: aload 4
    //   152: getfield 472	com/parse/ParsePushRouter$CallbackFactory:klass	Ljava/lang/Class;
    //   155: invokevirtual 475	java/lang/Class:getCanonicalName	()Ljava/lang/String;
    //   158: invokevirtual 115	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   161: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   164: aload 6
    //   166: invokestatic 246	com/parse/Parse:logE	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   169: ldc_w 434
    //   172: monitorexit
    //   173: return
    //   174: astore_3
    //   175: ldc_w 434
    //   178: monitorexit
    //   179: aload_3
    //   180: athrow
    //   181: astore 5
    //   183: ldc 19
    //   185: new 108	java/lang/StringBuilder
    //   188: dup
    //   189: invokespecial 109	java/lang/StringBuilder:<init>	()V
    //   192: ldc_w 477
    //   195: invokevirtual 115	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   198: aload 4
    //   200: getfield 472	com/parse/ParsePushRouter$CallbackFactory:klass	Ljava/lang/Class;
    //   203: invokevirtual 475	java/lang/Class:getCanonicalName	()Ljava/lang/String;
    //   206: invokevirtual 115	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   209: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   212: aload 5
    //   214: invokestatic 246	com/parse/Parse:logE	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   217: ldc_w 434
    //   220: monitorexit
    //   221: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	222	0	paramPushService	PushService
    //   0	222	1	paramString	String
    //   0	222	2	paramJSONObject	JSONObject
    //   174	6	3	localObject	Object
    //   16	183	4	localCallbackFactory	CallbackFactory
    //   181	32	5	localInstantiationException	InstantiationException
    //   133	32	6	localIllegalAccessException	IllegalAccessException
    //   78	50	7	localPushCallback	PushCallback
    // Exception table:
    //   from	to	target	type
    //   73	80	133	java/lang/IllegalAccessException
    //   4	18	174	finally
    //   23	67	174	finally
    //   68	73	174	finally
    //   73	80	174	finally
    //   80	84	174	finally
    //   135	173	174	finally
    //   175	179	174	finally
    //   183	221	174	finally
    //   73	80	181	java/lang/InstantiationException
  }
  
  /* Error */
  public static boolean hasRoutes(Context paramContext)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: aload_0
    //   4: invokestatic 134	com/parse/ParsePushRouter:ensureStateIsLoaded	(Landroid/content/Context;)V
    //   7: getstatic 58	com/parse/ParsePushRouter:defaultRoute	Lcom/parse/ParsePushRouter$CallbackFactory;
    //   10: ifnonnull +34 -> 44
    //   13: getstatic 56	com/parse/ParsePushRouter:channelRoutes	Ljava/util/Map;
    //   16: ifnull +14 -> 30
    //   19: getstatic 56	com/parse/ParsePushRouter:channelRoutes	Ljava/util/Map;
    //   22: invokeinterface 482 1 0
    //   27: ifeq +17 -> 44
    //   30: aload_0
    //   31: invokestatic 484	com/parse/ParsePushRouter:getSubscriptions	(Landroid/content/Context;)Ljava/util/Set;
    //   34: invokeinterface 485 1 0
    //   39: istore_3
    //   40: iload_3
    //   41: ifne +10 -> 51
    //   44: iconst_1
    //   45: istore_2
    //   46: ldc 2
    //   48: monitorexit
    //   49: iload_2
    //   50: ireturn
    //   51: iconst_0
    //   52: istore_2
    //   53: goto -7 -> 46
    //   56: astore_1
    //   57: ldc 2
    //   59: monitorexit
    //   60: aload_1
    //   61: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	62	0	paramContext	Context
    //   56	5	1	localObject	Object
    //   45	8	2	bool1	boolean
    //   39	2	3	bool2	boolean
    // Exception table:
    //   from	to	target	type
    //   3	30	56	finally
    //   30	40	56	finally
  }
  
  /* Error */
  static void insertIntoHistory(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 71	com/parse/ParsePushRouter:history	Lorg/json/JSONObject;
    //   6: aload_0
    //   7: aload_1
    //   8: invokevirtual 418	org/json/JSONObject:putOpt	(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;
    //   11: pop
    //   12: getstatic 44	com/parse/ParsePushRouter:$assertionsDisabled	Z
    //   15: istore 4
    //   17: aconst_null
    //   18: astore 5
    //   20: iload 4
    //   22: ifne +43 -> 65
    //   25: getstatic 73	com/parse/ParsePushRouter:maxHistory	I
    //   28: istore 6
    //   30: aconst_null
    //   31: astore 5
    //   33: iload 6
    //   35: ifgt +30 -> 65
    //   38: new 488	java/lang/AssertionError
    //   41: dup
    //   42: invokespecial 489	java/lang/AssertionError:<init>	()V
    //   45: athrow
    //   46: astore_3
    //   47: ldc 2
    //   49: monitorexit
    //   50: aload_3
    //   51: athrow
    //   52: getstatic 71	com/parse/ParsePushRouter:history	Lorg/json/JSONObject;
    //   55: aload 8
    //   57: invokevirtual 493	org/json/JSONObject:remove	(Ljava/lang/String;)Ljava/lang/Object;
    //   60: pop
    //   61: aload 9
    //   63: astore 5
    //   65: getstatic 71	com/parse/ParsePushRouter:history	Lorg/json/JSONObject;
    //   68: invokevirtual 392	org/json/JSONObject:length	()I
    //   71: getstatic 73	com/parse/ParsePushRouter:maxHistory	I
    //   74: if_icmple +86 -> 160
    //   77: getstatic 71	com/parse/ParsePushRouter:history	Lorg/json/JSONObject;
    //   80: invokevirtual 397	org/json/JSONObject:keys	()Ljava/util/Iterator;
    //   83: astore 7
    //   85: aload 7
    //   87: invokeinterface 406 1 0
    //   92: checkcast 495	java/lang/String
    //   95: astore 8
    //   97: getstatic 71	com/parse/ParsePushRouter:history	Lorg/json/JSONObject;
    //   100: aload 8
    //   102: invokevirtual 358	org/json/JSONObject:optString	(Ljava/lang/String;)Ljava/lang/String;
    //   105: astore 9
    //   107: aload 7
    //   109: invokeinterface 402 1 0
    //   114: ifeq -62 -> 52
    //   117: aload 7
    //   119: invokeinterface 406 1 0
    //   124: checkcast 495	java/lang/String
    //   127: astore 10
    //   129: getstatic 71	com/parse/ParsePushRouter:history	Lorg/json/JSONObject;
    //   132: aload 10
    //   134: invokevirtual 358	org/json/JSONObject:optString	(Ljava/lang/String;)Ljava/lang/String;
    //   137: astore 11
    //   139: aload 11
    //   141: aload 9
    //   143: invokevirtual 499	java/lang/String:compareTo	(Ljava/lang/String;)I
    //   146: ifge -39 -> 107
    //   149: aload 10
    //   151: astore 8
    //   153: aload 11
    //   155: astore 9
    //   157: goto -50 -> 107
    //   160: aload 5
    //   162: ifnull +8 -> 170
    //   165: aload 5
    //   167: putstatic 69	com/parse/ParsePushRouter:ignoreAfter	Ljava/lang/String;
    //   170: ldc 2
    //   172: monitorexit
    //   173: return
    //   174: astore_2
    //   175: goto -163 -> 12
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	178	0	paramString1	String
    //   0	178	1	paramString2	String
    //   174	1	2	localJSONException	JSONException
    //   46	5	3	localObject1	Object
    //   15	6	4	bool	boolean
    //   18	148	5	localObject2	Object
    //   28	6	6	i	int
    //   83	35	7	localIterator	Iterator
    //   55	97	8	localObject3	Object
    //   61	95	9	localObject4	Object
    //   127	23	10	str1	String
    //   137	17	11	str2	String
    // Exception table:
    //   from	to	target	type
    //   3	12	46	finally
    //   12	17	46	finally
    //   25	30	46	finally
    //   38	46	46	finally
    //   52	61	46	finally
    //   65	107	46	finally
    //   107	149	46	finally
    //   165	170	46	finally
    //   3	12	174	org/json/JSONException
  }
  
  /* Error */
  private static boolean isSubscribedToChannel(Context paramContext, String paramString)
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore_2
    //   2: ldc 2
    //   4: monitorenter
    //   5: getstatic 65	com/parse/ParsePushRouter:channels	Ljava/util/Set;
    //   8: aload_1
    //   9: invokeinterface 502 2 0
    //   14: istore 4
    //   16: iload 4
    //   18: ifeq +8 -> 26
    //   21: ldc 2
    //   23: monitorexit
    //   24: iload_2
    //   25: ireturn
    //   26: getstatic 56	com/parse/ParsePushRouter:channelRoutes	Ljava/util/Map;
    //   29: aload_1
    //   30: invokeinterface 438 2 0
    //   35: checkcast 140	com/parse/ParsePushRouter$CallbackFactory
    //   38: astore 5
    //   40: aload 5
    //   42: ifnull +15 -> 57
    //   45: aload 5
    //   47: invokevirtual 505	com/parse/ParsePushRouter$CallbackFactory:requiresSubscription	()Z
    //   50: istore 6
    //   52: iload 6
    //   54: ifeq -33 -> 21
    //   57: iconst_0
    //   58: istore_2
    //   59: goto -38 -> 21
    //   62: astore_3
    //   63: ldc 2
    //   65: monitorexit
    //   66: aload_3
    //   67: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	68	0	paramContext	Context
    //   0	68	1	paramString	String
    //   1	58	2	bool1	boolean
    //   62	5	3	localObject	Object
    //   14	3	4	bool2	boolean
    //   38	8	5	localCallbackFactory	CallbackFactory
    //   50	3	6	bool3	boolean
    // Exception table:
    //   from	to	target	type
    //   5	16	62	finally
    //   26	40	62	finally
    //   45	52	62	finally
  }
  
  private static void parseChannelRoutes(JSONObject paramJSONObject)
  {
    if (paramJSONObject == null) {}
    for (;;)
    {
      return;
      channelRoutes.clear();
      Iterator localIterator = paramJSONObject.keys();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        JSONObject localJSONObject = null;
        try
        {
          localJSONObject = paramJSONObject.getJSONObject(str);
          channelRoutes.put(str, new CallbackFactory(localJSONObject));
        }
        catch (JSONException localJSONException)
        {
          Parse.logE("com.parse.ParsePushRouter", "Failed to parse push route " + localJSONObject);
        }
        catch (ClassNotFoundException localClassNotFoundException)
        {
          Parse.logE("com.parse.ParsePushRouter", "Route references missing class: " + localClassNotFoundException.getMessage());
        }
        catch (ClassCastException localClassCastException)
        {
          Parse.logE("com.parse.ParsePushRouter", "Route references class which is not a PushCallback: " + localClassCastException.getMessage());
        }
      }
    }
  }
  
  /* Error */
  static boolean removeChannelRoute(Context paramContext, String paramString)
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore_2
    //   2: ldc 2
    //   4: monitorenter
    //   5: aload_0
    //   6: invokestatic 134	com/parse/ParsePushRouter:ensureStateIsLoaded	(Landroid/content/Context;)V
    //   9: getstatic 56	com/parse/ParsePushRouter:channelRoutes	Ljava/util/Map;
    //   12: aload_1
    //   13: invokeinterface 521 2 0
    //   18: ifnull +41 -> 59
    //   21: new 151	com/parse/ParseInstallation
    //   24: dup
    //   25: invokespecial 152	com/parse/ParseInstallation:<init>	()V
    //   28: astore 4
    //   30: aload 4
    //   32: ldc 153
    //   34: iconst_1
    //   35: anewarray 495	java/lang/String
    //   38: dup
    //   39: iconst_0
    //   40: aload_1
    //   41: aastore
    //   42: invokestatic 527	java/util/Arrays:asList	([Ljava/lang/Object;)Ljava/util/List;
    //   45: invokevirtual 336	com/parse/ParseInstallation:removeAll	(Ljava/lang/String;Ljava/util/Collection;)V
    //   48: aload_0
    //   49: aload 4
    //   51: invokestatic 161	com/parse/ParsePushRouter:saveEventually	(Landroid/content/Context;Lcom/parse/ParseInstallation;)V
    //   54: ldc 2
    //   56: monitorexit
    //   57: iload_2
    //   58: ireturn
    //   59: iconst_0
    //   60: istore_2
    //   61: goto -7 -> 54
    //   64: astore_3
    //   65: ldc 2
    //   67: monitorexit
    //   68: aload_3
    //   69: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	70	0	paramContext	Context
    //   0	70	1	paramString	String
    //   1	60	2	bool	boolean
    //   64	5	3	localObject	Object
    //   28	22	4	localParseInstallation	ParseInstallation
    // Exception table:
    //   from	to	target	type
    //   5	54	64	finally
  }
  
  /* Error */
  static void routePush(final PushService paramPushService, final JSONObject paramJSONObject)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_2
    //   2: aload_1
    //   3: ldc_w 531
    //   6: aconst_null
    //   7: invokevirtual 300	org/json/JSONObject:optString	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   10: astore_3
    //   11: aload_1
    //   12: ldc_w 533
    //   15: invokevirtual 342	org/json/JSONObject:has	(Ljava/lang/String;)Z
    //   18: istore 5
    //   20: iconst_0
    //   21: istore_2
    //   22: iload 5
    //   24: ifeq +102 -> 126
    //   27: getstatic 67	com/parse/ParsePushRouter:lastTime	Ljava/lang/String;
    //   30: ifnull +25 -> 55
    //   33: aload_1
    //   34: ldc_w 533
    //   37: invokevirtual 358	org/json/JSONObject:optString	(Ljava/lang/String;)Ljava/lang/String;
    //   40: getstatic 67	com/parse/ParsePushRouter:lastTime	Ljava/lang/String;
    //   43: invokevirtual 499	java/lang/String:compareTo	(Ljava/lang/String;)I
    //   46: istore 8
    //   48: iconst_0
    //   49: istore_2
    //   50: iload 8
    //   52: ifle +15 -> 67
    //   55: aload_1
    //   56: ldc_w 533
    //   59: invokevirtual 358	org/json/JSONObject:optString	(Ljava/lang/String;)Ljava/lang/String;
    //   62: putstatic 67	com/parse/ParsePushRouter:lastTime	Ljava/lang/String;
    //   65: iconst_1
    //   66: istore_2
    //   67: getstatic 69	com/parse/ParsePushRouter:ignoreAfter	Ljava/lang/String;
    //   70: ifnull +56 -> 126
    //   73: aload_1
    //   74: ldc_w 533
    //   77: invokevirtual 358	org/json/JSONObject:optString	(Ljava/lang/String;)Ljava/lang/String;
    //   80: getstatic 69	com/parse/ParsePushRouter:ignoreAfter	Ljava/lang/String;
    //   83: invokevirtual 499	java/lang/String:compareTo	(Ljava/lang/String;)I
    //   86: ifgt +40 -> 126
    //   89: ldc 19
    //   91: new 108	java/lang/StringBuilder
    //   94: dup
    //   95: invokespecial 109	java/lang/StringBuilder:<init>	()V
    //   98: ldc_w 535
    //   101: invokevirtual 115	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   104: aload_1
    //   105: invokevirtual 137	org/json/JSONObject:toString	()Ljava/lang/String;
    //   108: invokevirtual 115	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   111: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   114: invokestatic 265	com/parse/Parse:logD	(Ljava/lang/String;Ljava/lang/String;)V
    //   117: iload_2
    //   118: ifeq +7 -> 125
    //   121: aload_0
    //   122: invokestatic 81	com/parse/ParsePushRouter:saveStateToDisk	(Landroid/content/Context;)V
    //   125: return
    //   126: aload_3
    //   127: ifnull +46 -> 173
    //   130: aload_0
    //   131: aload_3
    //   132: invokestatic 86	com/parse/ParsePushRouter:isSubscribedToChannel	(Landroid/content/Context;Ljava/lang/String;)Z
    //   135: ifne +38 -> 173
    //   138: new 537	com/parse/ParsePushRouter$3
    //   141: dup
    //   142: new 539	com/parse/ParsePushRouter$2
    //   145: dup
    //   146: aload_0
    //   147: aload_1
    //   148: invokespecial 541	com/parse/ParsePushRouter$2:<init>	(Lcom/parse/PushService;Lorg/json/JSONObject;)V
    //   151: aload_3
    //   152: aload_0
    //   153: invokespecial 544	com/parse/ParsePushRouter$3:<init>	(Lcom/parse/ParseCallback;Ljava/lang/String;Lcom/parse/PushService;)V
    //   156: iconst_0
    //   157: anewarray 546	java/lang/Void
    //   160: invokevirtual 550	com/parse/ParsePushRouter$3:execute	([Ljava/lang/Object;)Landroid/os/AsyncTask;
    //   163: pop
    //   164: iload_2
    //   165: ifeq -40 -> 125
    //   168: aload_0
    //   169: invokestatic 81	com/parse/ParsePushRouter:saveStateToDisk	(Landroid/content/Context;)V
    //   172: return
    //   173: aload_1
    //   174: ldc_w 552
    //   177: invokevirtual 342	org/json/JSONObject:has	(Ljava/lang/String;)Z
    //   180: ifeq +95 -> 275
    //   183: aload_1
    //   184: ldc_w 552
    //   187: invokevirtual 358	org/json/JSONObject:optString	(Ljava/lang/String;)Ljava/lang/String;
    //   190: astore 6
    //   192: getstatic 71	com/parse/ParsePushRouter:history	Lorg/json/JSONObject;
    //   195: aload 6
    //   197: invokevirtual 342	org/json/JSONObject:has	(Ljava/lang/String;)Z
    //   200: ifeq +40 -> 240
    //   203: ldc 19
    //   205: new 108	java/lang/StringBuilder
    //   208: dup
    //   209: invokespecial 109	java/lang/StringBuilder:<init>	()V
    //   212: ldc_w 554
    //   215: invokevirtual 115	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   218: aload_1
    //   219: invokevirtual 137	org/json/JSONObject:toString	()Ljava/lang/String;
    //   222: invokevirtual 115	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   225: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   228: invokestatic 265	com/parse/Parse:logD	(Ljava/lang/String;Ljava/lang/String;)V
    //   231: iload_2
    //   232: ifeq -107 -> 125
    //   235: aload_0
    //   236: invokestatic 81	com/parse/ParsePushRouter:saveStateToDisk	(Landroid/content/Context;)V
    //   239: return
    //   240: aload 6
    //   242: aload_1
    //   243: ldc_w 533
    //   246: invokevirtual 358	org/json/JSONObject:optString	(Ljava/lang/String;)Ljava/lang/String;
    //   249: invokestatic 556	com/parse/ParsePushRouter:insertIntoHistory	(Ljava/lang/String;Ljava/lang/String;)V
    //   252: iconst_1
    //   253: istore_2
    //   254: iload_2
    //   255: ifeq +7 -> 262
    //   258: aload_0
    //   259: invokestatic 81	com/parse/ParsePushRouter:saveStateToDisk	(Landroid/content/Context;)V
    //   262: aload_0
    //   263: aload_3
    //   264: aload_1
    //   265: ldc_w 558
    //   268: invokevirtual 292	org/json/JSONObject:optJSONObject	(Ljava/lang/String;)Lorg/json/JSONObject;
    //   271: invokestatic 560	com/parse/ParsePushRouter:handlePushData	(Lcom/parse/PushService;Ljava/lang/String;Lorg/json/JSONObject;)V
    //   274: return
    //   275: aload_1
    //   276: ldc_w 533
    //   279: invokevirtual 342	org/json/JSONObject:has	(Ljava/lang/String;)Z
    //   282: ifeq -28 -> 254
    //   285: getstatic 67	com/parse/ParsePushRouter:lastTime	Ljava/lang/String;
    //   288: ifnull +19 -> 307
    //   291: aload_1
    //   292: ldc_w 533
    //   295: invokevirtual 358	org/json/JSONObject:optString	(Ljava/lang/String;)Ljava/lang/String;
    //   298: getstatic 67	com/parse/ParsePushRouter:lastTime	Ljava/lang/String;
    //   301: invokevirtual 499	java/lang/String:compareTo	(Ljava/lang/String;)I
    //   304: ifle -50 -> 254
    //   307: aload_1
    //   308: ldc_w 533
    //   311: invokevirtual 358	org/json/JSONObject:optString	(Ljava/lang/String;)Ljava/lang/String;
    //   314: putstatic 67	com/parse/ParsePushRouter:lastTime	Ljava/lang/String;
    //   317: iconst_1
    //   318: istore_2
    //   319: goto -65 -> 254
    //   322: astore 4
    //   324: iload_2
    //   325: ifeq +7 -> 332
    //   328: aload_0
    //   329: invokestatic 81	com/parse/ParsePushRouter:saveStateToDisk	(Landroid/content/Context;)V
    //   332: aload 4
    //   334: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	335	0	paramPushService	PushService
    //   0	335	1	paramJSONObject	JSONObject
    //   1	324	2	i	int
    //   10	254	3	str1	String
    //   322	11	4	localObject	Object
    //   18	5	5	bool	boolean
    //   190	51	6	str2	String
    //   46	5	8	j	int
    // Exception table:
    //   from	to	target	type
    //   11	20	322	finally
    //   27	48	322	finally
    //   55	65	322	finally
    //   67	117	322	finally
    //   130	164	322	finally
    //   173	231	322	finally
    //   240	252	322	finally
    //   275	307	322	finally
    //   307	317	322	finally
  }
  
  private static void saveEventually(Context paramContext, final ParseInstallation paramParseInstallation)
  {
    paramParseInstallation.saveEventually(new SaveCallback()
    {
      public void done(ParseException paramAnonymousParseException)
      {
        if (paramAnonymousParseException != null)
        {
          Parse.logE("com.parse.ParsePushRouter", "Failed to save installation eventually", paramAnonymousParseException);
          return;
        }
        ParsePushRouter.ensureStateIsLoaded(this.val$context);
        synchronized (ParsePushRouter.channels)
        {
          ParsePushRouter.channels.clear();
          List localList = paramParseInstallation.getList("channels");
          ParsePushRouter.channels.addAll(localList);
          if (!ParsePushRouter.hasRoutes(this.val$context))
          {
            Parse.logD("com.parse.ParsePushRouter", "Shutting down push service. No remaining channels");
            this.val$context.stopService(new Intent(this.val$context, PushService.class));
          }
          new BackgroundTask(null)
          {
            public Void run()
              throws ParseException
            {
              ParsePushRouter.saveStateToDisk(ParsePushRouter.1.this.val$context);
              return null;
            }
          }.execute(new Void[0]);
          return;
        }
      }
    });
  }
  
  /* Error */
  private static void saveStateToDisk(Context paramContext)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: new 136	org/json/JSONObject
    //   6: dup
    //   7: invokespecial 227	org/json/JSONObject:<init>	()V
    //   10: astore_1
    //   11: aload_1
    //   12: ldc_w 569
    //   15: iconst_3
    //   16: invokevirtual 232	org/json/JSONObject:put	(Ljava/lang/String;I)Lorg/json/JSONObject;
    //   19: pop
    //   20: getstatic 58	com/parse/ParsePushRouter:defaultRoute	Lcom/parse/ParsePushRouter$CallbackFactory;
    //   23: ifnull +17 -> 40
    //   26: aload_1
    //   27: ldc_w 293
    //   30: getstatic 58	com/parse/ParsePushRouter:defaultRoute	Lcom/parse/ParsePushRouter$CallbackFactory;
    //   33: invokevirtual 573	com/parse/ParsePushRouter$CallbackFactory:toJSON	()Lorg/json/JSONObject;
    //   36: invokevirtual 418	org/json/JSONObject:putOpt	(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;
    //   39: pop
    //   40: new 136	org/json/JSONObject
    //   43: dup
    //   44: invokespecial 227	org/json/JSONObject:<init>	()V
    //   47: astore 5
    //   49: getstatic 56	com/parse/ParsePushRouter:channelRoutes	Ljava/util/Map;
    //   52: invokeinterface 278 1 0
    //   57: invokeinterface 576 1 0
    //   62: astore 6
    //   64: aload 6
    //   66: invokeinterface 402 1 0
    //   71: ifeq +75 -> 146
    //   74: aload 6
    //   76: invokeinterface 406 1 0
    //   81: checkcast 495	java/lang/String
    //   84: astore 12
    //   86: aload 5
    //   88: aload 12
    //   90: getstatic 56	com/parse/ParsePushRouter:channelRoutes	Ljava/util/Map;
    //   93: aload 12
    //   95: invokeinterface 438 2 0
    //   100: checkcast 140	com/parse/ParsePushRouter$CallbackFactory
    //   103: invokevirtual 573	com/parse/ParsePushRouter$CallbackFactory:toJSON	()Lorg/json/JSONObject;
    //   106: invokevirtual 418	org/json/JSONObject:putOpt	(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;
    //   109: pop
    //   110: goto -46 -> 64
    //   113: astore_3
    //   114: ldc 19
    //   116: new 108	java/lang/StringBuilder
    //   119: dup
    //   120: invokespecial 109	java/lang/StringBuilder:<init>	()V
    //   123: ldc_w 578
    //   126: invokevirtual 115	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   129: aload_3
    //   130: invokevirtual 166	org/json/JSONException:getMessage	()Ljava/lang/String;
    //   133: invokevirtual 115	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   136: invokevirtual 119	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   139: invokestatic 172	com/parse/Parse:logE	(Ljava/lang/String;Ljava/lang/String;)V
    //   142: ldc 2
    //   144: monitorexit
    //   145: return
    //   146: aload_1
    //   147: ldc_w 288
    //   150: aload 5
    //   152: invokevirtual 418	org/json/JSONObject:putOpt	(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;
    //   155: pop
    //   156: aload_1
    //   157: ldc 153
    //   159: new 312	org/json/JSONArray
    //   162: dup
    //   163: getstatic 65	com/parse/ParsePushRouter:channels	Ljava/util/Set;
    //   166: invokespecial 581	org/json/JSONArray:<init>	(Ljava/util/Collection;)V
    //   169: invokevirtual 237	org/json/JSONObject:put	(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;
    //   172: pop
    //   173: aload_1
    //   174: ldc_w 296
    //   177: getstatic 67	com/parse/ParsePushRouter:lastTime	Ljava/lang/String;
    //   180: invokevirtual 418	org/json/JSONObject:putOpt	(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;
    //   183: pop
    //   184: aload_1
    //   185: ldc_w 350
    //   188: getstatic 71	com/parse/ParsePushRouter:history	Lorg/json/JSONObject;
    //   191: invokevirtual 418	org/json/JSONObject:putOpt	(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;
    //   194: pop
    //   195: aload_1
    //   196: ldc_w 301
    //   199: getstatic 69	com/parse/ParsePushRouter:ignoreAfter	Ljava/lang/String;
    //   202: invokevirtual 418	org/json/JSONObject:putOpt	(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;
    //   205: pop
    //   206: aload_0
    //   207: ldc 16
    //   209: aload_1
    //   210: invokestatic 366	com/parse/ParseObject:saveDiskObject	(Landroid/content/Context;Ljava/lang/String;Lorg/json/JSONObject;)V
    //   213: goto -71 -> 142
    //   216: astore_2
    //   217: ldc 2
    //   219: monitorexit
    //   220: aload_2
    //   221: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	222	0	paramContext	Context
    //   10	200	1	localJSONObject1	JSONObject
    //   216	5	2	localObject	Object
    //   113	17	3	localJSONException	JSONException
    //   47	104	5	localJSONObject2	JSONObject
    //   62	13	6	localIterator	Iterator
    //   84	10	12	str	String
    // Exception table:
    //   from	to	target	type
    //   3	40	113	org/json/JSONException
    //   40	64	113	org/json/JSONException
    //   64	110	113	org/json/JSONException
    //   146	213	113	org/json/JSONException
    //   3	40	216	finally
    //   40	64	216	finally
    //   64	110	216	finally
    //   114	142	216	finally
    //   146	213	216	finally
  }
  
  static void setDefaultRoute(Context paramContext, Class<? extends Activity> paramClass, int paramInt)
  {
    ensureStateIsLoaded(paramContext);
    if (paramClass == null) {}
    JSONObject localJSONObject;
    for (defaultRoute = null;; defaultRoute = new CallbackFactory(StandardPushCallback.class, localJSONObject))
    {
      saveStateToDisk(paramContext);
      return;
      localJSONObject = dataForActivity(paramContext, paramClass, paramInt);
      if (localJSONObject == null) {
        localJSONObject = new JSONObject();
      }
    }
  }
  
  static class CallbackFactory
  {
    JSONObject contextData;
    Class<? extends PushCallback> klass;
    
    protected CallbackFactory() {}
    
    CallbackFactory(Class<? extends PushCallback> paramClass, JSONObject paramJSONObject)
    {
      this.klass = paramClass;
      this.contextData = paramJSONObject;
    }
    
    CallbackFactory(JSONObject paramJSONObject)
      throws ClassNotFoundException
    {
      Parse.logD("com.parse.ParsePushRouter", "Creating factory for class " + paramJSONObject.optString("name"));
      this.klass = Class.forName(paramJSONObject.optString("name"));
      if (this.klass == null) {
        throw new ClassNotFoundException("Missing class definition in " + paramJSONObject);
      }
      this.contextData = paramJSONObject.optJSONObject("data");
    }
    
    PushCallback newCallback()
      throws IllegalAccessException, InstantiationException
    {
      PushCallback localPushCallback = (PushCallback)this.klass.newInstance();
      localPushCallback.setLocalData(this.contextData);
      return localPushCallback;
    }
    
    boolean requiresSubscription()
    {
      return true;
    }
    
    JSONObject toJSON()
    {
      try
      {
        JSONObject localJSONObject = new JSONObject();
        localJSONObject.put("name", this.klass.getCanonicalName());
        localJSONObject.putOpt("data", this.contextData);
        return localJSONObject;
      }
      catch (JSONException localJSONException)
      {
        Parse.logE("com.parse.ParsePushRouter", "Failed to encode route: " + localJSONException.getMessage());
      }
      return null;
    }
  }
  
  static class SingletonFactory
    extends ParsePushRouter.CallbackFactory
  {
    PushCallback singleton;
    
    SingletonFactory(PushCallback paramPushCallback)
    {
      this.singleton = paramPushCallback;
    }
    
    PushCallback newCallback()
    {
      return this.singleton;
    }
    
    boolean requiresSubscription()
    {
      return false;
    }
    
    JSONObject toJSON()
    {
      return null;
    }
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.ParsePushRouter
 * JD-Core Version:    0.7.0.1
 */