package com.parse;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

class ParseCommandCache
{
  private static final String TAG = "com.parse.ParseCommandCache";
  private static int filenameCounter = 0;
  private static Object lock = new Object();
  private File cachePath;
  private HashMap<ParseCommand, ParseCallback<Void>> callbacksForCommands = new HashMap();
  private HashMap<File, ParseCommand> commandsInCache = new HashMap();
  private boolean connected = false;
  private Logger log;
  private int maxCacheSizeBytes = 10485760;
  private boolean running = false;
  private Object runningLock;
  private boolean shouldStop = false;
  private TestHelper testHelper = null;
  private int timeoutMaxRetries = 5;
  private double timeoutRetryWaitSeconds = 600.0D;
  
  public ParseCommandCache(Context paramContext)
  {
    lock = new Object();
    this.runningLock = new Object();
    this.log = Logger.getLogger("com.parse.ParseCommandCache");
    this.cachePath = new File(Parse.getParseDir(), "CommandCache");
    this.cachePath.mkdirs();
    if (!Parse.hasPermission("android.permission.ACCESS_NETWORK_STATE")) {
      return;
    }
    final ConnectivityManager localConnectivityManager = (ConnectivityManager)paramContext.getSystemService("connectivity");
    if (localConnectivityManager != null)
    {
      NetworkInfo localNetworkInfo = localConnectivityManager.getActiveNetworkInfo();
      boolean bool1 = false;
      if (localNetworkInfo != null)
      {
        boolean bool2 = localNetworkInfo.isConnected();
        bool1 = false;
        if (bool2) {
          bool1 = true;
        }
      }
      setConnected(bool1);
      paramContext.registerReceiver(new BroadcastReceiver()new IntentFilter
      {
        public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
        {
          if (paramAnonymousIntent.getBooleanExtra("noConnectivity", false))
          {
            ParseCommandCache.this.setConnected(false);
            return;
          }
          NetworkInfo localNetworkInfo = localConnectivityManager.getActiveNetworkInfo();
          ParseCommandCache localParseCommandCache = ParseCommandCache.this;
          boolean bool1 = false;
          if (localNetworkInfo != null)
          {
            boolean bool2 = localNetworkInfo.isConnected();
            bool1 = false;
            if (bool2) {
              bool1 = true;
            }
          }
          localParseCommandCache.setConnected(bool1);
        }
      }, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }
    resume();
  }
  
  /* Error */
  private void maybeRunAllCommandsNow(int paramInt)
  {
    // Byte code:
    //   0: getstatic 41	com/parse/ParseCommandCache:lock	Ljava/lang/Object;
    //   3: astore_2
    //   4: aload_2
    //   5: monitorenter
    //   6: aload_0
    //   7: getfield 62	com/parse/ParseCommandCache:connected	Z
    //   10: ifne +6 -> 16
    //   13: aload_2
    //   14: monitorexit
    //   15: return
    //   16: aload_0
    //   17: getfield 91	com/parse/ParseCommandCache:cachePath	Ljava/io/File;
    //   20: invokevirtual 166	java/io/File:list	()[Ljava/lang/String;
    //   23: astore 4
    //   25: aload 4
    //   27: ifnull +9 -> 36
    //   30: aload 4
    //   32: arraylength
    //   33: ifne +11 -> 44
    //   36: aload_2
    //   37: monitorexit
    //   38: return
    //   39: astore_3
    //   40: aload_2
    //   41: monitorexit
    //   42: aload_3
    //   43: athrow
    //   44: aload 4
    //   46: invokestatic 172	java/util/Arrays:sort	([Ljava/lang/Object;)V
    //   49: aload 4
    //   51: arraylength
    //   52: istore 5
    //   54: iconst_0
    //   55: istore 6
    //   57: iload 6
    //   59: iload 5
    //   61: if_icmpge +717 -> 778
    //   64: aload 4
    //   66: iload 6
    //   68: aaload
    //   69: astore 7
    //   71: new 78	java/io/File
    //   74: dup
    //   75: aload_0
    //   76: getfield 91	com/parse/ParseCommandCache:cachePath	Ljava/io/File;
    //   79: aload 7
    //   81: invokespecial 89	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   84: astore 8
    //   86: aconst_null
    //   87: astore 9
    //   89: new 174	java/io/FileInputStream
    //   92: dup
    //   93: aload 8
    //   95: invokespecial 177	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   98: astore 10
    //   100: new 179	java/io/BufferedInputStream
    //   103: dup
    //   104: aload 10
    //   106: invokespecial 182	java/io/BufferedInputStream:<init>	(Ljava/io/InputStream;)V
    //   109: astore 11
    //   111: new 184	java/io/ByteArrayOutputStream
    //   114: dup
    //   115: invokespecial 185	java/io/ByteArrayOutputStream:<init>	()V
    //   118: astore 12
    //   120: sipush 1024
    //   123: newarray byte
    //   125: astore 21
    //   127: aload 11
    //   129: aload 21
    //   131: invokevirtual 191	java/io/InputStream:read	([B)I
    //   134: istore 22
    //   136: iload 22
    //   138: ifle +60 -> 198
    //   141: aload 12
    //   143: aload 21
    //   145: iconst_0
    //   146: iload 22
    //   148: invokevirtual 195	java/io/ByteArrayOutputStream:write	([BII)V
    //   151: goto -24 -> 127
    //   154: astore 19
    //   156: aload 11
    //   158: astore 9
    //   160: bipush 6
    //   162: invokestatic 199	com/parse/Parse:getLogLevel	()I
    //   165: if_icmplt +17 -> 182
    //   168: aload_0
    //   169: getfield 76	com/parse/ParseCommandCache:log	Ljava/util/logging/Logger;
    //   172: getstatic 205	java/util/logging/Level:SEVERE	Ljava/util/logging/Level;
    //   175: ldc 207
    //   177: aload 19
    //   179: invokevirtual 210	java/util/logging/Logger:log	(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   182: aload 9
    //   184: ifnull +8 -> 192
    //   187: aload 9
    //   189: invokevirtual 213	java/io/InputStream:close	()V
    //   192: iinc 6 1
    //   195: goto -138 -> 57
    //   198: new 215	org/json/JSONObject
    //   201: dup
    //   202: aload 12
    //   204: ldc 217
    //   206: invokevirtual 221	java/io/ByteArrayOutputStream:toString	(Ljava/lang/String;)Ljava/lang/String;
    //   209: invokespecial 222	org/json/JSONObject:<init>	(Ljava/lang/String;)V
    //   212: astore 23
    //   214: aload 11
    //   216: ifnull +8 -> 224
    //   219: aload 11
    //   221: invokevirtual 213	java/io/InputStream:close	()V
    //   224: aload_0
    //   225: getfield 56	com/parse/ParseCommandCache:commandsInCache	Ljava/util/HashMap;
    //   228: aload 8
    //   230: invokevirtual 226	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
    //   233: ifeq +202 -> 435
    //   236: aload_0
    //   237: getfield 56	com/parse/ParseCommandCache:commandsInCache	Ljava/util/HashMap;
    //   240: aload 8
    //   242: invokevirtual 230	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   245: checkcast 232	com/parse/ParseCommand
    //   248: astore 27
    //   250: aload_0
    //   251: getfield 58	com/parse/ParseCommandCache:callbacksForCommands	Ljava/util/HashMap;
    //   254: aload 27
    //   256: invokevirtual 230	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   259: checkcast 234	com/parse/ParseCallback
    //   262: astore 28
    //   264: aload 27
    //   266: iconst_0
    //   267: invokevirtual 237	com/parse/ParseCommand:setCallCallbacksOnFailure	(Z)V
    //   270: aload 27
    //   272: invokevirtual 241	com/parse/ParseCommand:perform	()Ljava/lang/Object;
    //   275: pop
    //   276: aload 28
    //   278: ifnull +234 -> 512
    //   281: new 243	com/parse/ParseCommandCache$4
    //   284: dup
    //   285: aload_0
    //   286: aload 28
    //   288: invokespecial 246	com/parse/ParseCommandCache$4:<init>	(Lcom/parse/ParseCommandCache;Lcom/parse/ParseCallback;)V
    //   291: astore 36
    //   293: new 248	android/os/Handler
    //   296: dup
    //   297: invokestatic 254	android/os/Looper:getMainLooper	()Landroid/os/Looper;
    //   300: invokespecial 257	android/os/Handler:<init>	(Landroid/os/Looper;)V
    //   303: astore 37
    //   305: aload 37
    //   307: aload 36
    //   309: invokevirtual 261	android/os/Handler:post	(Ljava/lang/Runnable;)Z
    //   312: pop
    //   313: aload_0
    //   314: aload 8
    //   316: invokespecial 264	com/parse/ParseCommandCache:removeFile	(Ljava/io/File;)V
    //   319: goto -127 -> 192
    //   322: astore 13
    //   324: bipush 6
    //   326: invokestatic 199	com/parse/Parse:getLogLevel	()I
    //   329: if_icmplt +18 -> 347
    //   332: aload_0
    //   333: getfield 76	com/parse/ParseCommandCache:log	Ljava/util/logging/Logger;
    //   336: getstatic 205	java/util/logging/Level:SEVERE	Ljava/util/logging/Level;
    //   339: ldc_w 266
    //   342: aload 13
    //   344: invokevirtual 210	java/util/logging/Logger:log	(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   347: aload_0
    //   348: aload 8
    //   350: invokespecial 264	com/parse/ParseCommandCache:removeFile	(Ljava/io/File;)V
    //   353: aload 9
    //   355: ifnull -163 -> 192
    //   358: aload 9
    //   360: invokevirtual 213	java/io/InputStream:close	()V
    //   363: goto -171 -> 192
    //   366: astore 16
    //   368: goto -176 -> 192
    //   371: astore 17
    //   373: bipush 6
    //   375: invokestatic 199	com/parse/Parse:getLogLevel	()I
    //   378: if_icmplt +18 -> 396
    //   381: aload_0
    //   382: getfield 76	com/parse/ParseCommandCache:log	Ljava/util/logging/Logger;
    //   385: getstatic 205	java/util/logging/Level:SEVERE	Ljava/util/logging/Level;
    //   388: ldc_w 268
    //   391: aload 17
    //   393: invokevirtual 210	java/util/logging/Logger:log	(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   396: aload_0
    //   397: aload 8
    //   399: invokespecial 264	com/parse/ParseCommandCache:removeFile	(Ljava/io/File;)V
    //   402: aload 9
    //   404: ifnull -212 -> 192
    //   407: aload 9
    //   409: invokevirtual 213	java/io/InputStream:close	()V
    //   412: goto -220 -> 192
    //   415: astore 18
    //   417: goto -225 -> 192
    //   420: astore 14
    //   422: aload 9
    //   424: ifnull +8 -> 432
    //   427: aload 9
    //   429: invokevirtual 213	java/io/InputStream:close	()V
    //   432: aload 14
    //   434: athrow
    //   435: new 232	com/parse/ParseCommand
    //   438: dup
    //   439: aload 23
    //   441: invokespecial 271	com/parse/ParseCommand:<init>	(Lorg/json/JSONObject;)V
    //   444: astore 24
    //   446: aload 24
    //   448: invokevirtual 275	com/parse/ParseCommand:getLocalId	()Ljava/lang/String;
    //   451: ifnull +20 -> 471
    //   454: new 277	com/parse/ParseCommandCache$3
    //   457: dup
    //   458: aload_0
    //   459: invokespecial 279	com/parse/ParseCommandCache$3:<init>	(Lcom/parse/ParseCommandCache;)V
    //   462: astore 26
    //   464: aload 24
    //   466: aload 26
    //   468: invokevirtual 283	com/parse/ParseCommand:setInternalCallback	(Lcom/parse/ParseCommand$InternalCallback;)V
    //   471: aload 24
    //   473: astore 27
    //   475: goto -225 -> 250
    //   478: astore 25
    //   480: bipush 6
    //   482: invokestatic 199	com/parse/Parse:getLogLevel	()I
    //   485: if_icmplt +18 -> 503
    //   488: aload_0
    //   489: getfield 76	com/parse/ParseCommandCache:log	Ljava/util/logging/Logger;
    //   492: getstatic 205	java/util/logging/Level:SEVERE	Ljava/util/logging/Level;
    //   495: ldc_w 285
    //   498: aload 25
    //   500: invokevirtual 210	java/util/logging/Logger:log	(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   503: aload_0
    //   504: aload 8
    //   506: invokespecial 264	com/parse/ParseCommandCache:removeFile	(Ljava/io/File;)V
    //   509: goto -317 -> 192
    //   512: aload_0
    //   513: getfield 60	com/parse/ParseCommandCache:testHelper	Lcom/parse/ParseCommandCache$TestHelper;
    //   516: ifnull -203 -> 313
    //   519: aload_0
    //   520: getfield 60	com/parse/ParseCommandCache:testHelper	Lcom/parse/ParseCommandCache$TestHelper;
    //   523: iconst_1
    //   524: invokevirtual 290	com/parse/ParseCommandCache$TestHelper:notify	(I)V
    //   527: goto -214 -> 313
    //   530: astore 29
    //   532: aload 29
    //   534: invokevirtual 293	com/parse/ParseException:getCode	()I
    //   537: bipush 100
    //   539: if_icmpne +192 -> 731
    //   542: iload_1
    //   543: ifle +293 -> 836
    //   546: iconst_4
    //   547: invokestatic 199	com/parse/Parse:getLogLevel	()I
    //   550: if_icmplt +49 -> 599
    //   553: aload_0
    //   554: getfield 76	com/parse/ParseCommandCache:log	Ljava/util/logging/Logger;
    //   557: new 295	java/lang/StringBuilder
    //   560: dup
    //   561: invokespecial 296	java/lang/StringBuilder:<init>	()V
    //   564: ldc_w 298
    //   567: invokevirtual 302	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   570: aload_0
    //   571: getfield 48	com/parse/ParseCommandCache:timeoutRetryWaitSeconds	D
    //   574: invokevirtual 305	java/lang/StringBuilder:append	(D)Ljava/lang/StringBuilder;
    //   577: ldc_w 307
    //   580: invokevirtual 302	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   583: iload_1
    //   584: invokevirtual 310	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   587: ldc_w 312
    //   590: invokevirtual 302	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   593: invokevirtual 314	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   596: invokevirtual 317	java/util/logging/Logger:info	(Ljava/lang/String;)V
    //   599: invokestatic 323	java/lang/System:currentTimeMillis	()J
    //   602: lstore 30
    //   604: lload 30
    //   606: ldc2_w 324
    //   609: aload_0
    //   610: getfield 48	com/parse/ParseCommandCache:timeoutRetryWaitSeconds	D
    //   613: dmul
    //   614: d2l
    //   615: ladd
    //   616: lstore 32
    //   618: lload 30
    //   620: lload 32
    //   622: lcmp
    //   623: ifge +98 -> 721
    //   626: aload_0
    //   627: getfield 62	com/parse/ParseCommandCache:connected	Z
    //   630: ifeq +10 -> 640
    //   633: aload_0
    //   634: getfield 64	com/parse/ParseCommandCache:shouldStop	Z
    //   637: ifeq +23 -> 660
    //   640: iconst_4
    //   641: invokestatic 199	com/parse/Parse:getLogLevel	()I
    //   644: if_icmplt +13 -> 657
    //   647: aload_0
    //   648: getfield 76	com/parse/ParseCommandCache:log	Ljava/util/logging/Logger;
    //   651: ldc_w 327
    //   654: invokevirtual 317	java/util/logging/Logger:info	(Ljava/lang/String;)V
    //   657: aload_2
    //   658: monitorexit
    //   659: return
    //   660: getstatic 41	com/parse/ParseCommandCache:lock	Ljava/lang/Object;
    //   663: lload 32
    //   665: lload 30
    //   667: lsub
    //   668: invokevirtual 331	java/lang/Object:wait	(J)V
    //   671: invokestatic 323	java/lang/System:currentTimeMillis	()J
    //   674: lstore 30
    //   676: lload 30
    //   678: lload 32
    //   680: ldc2_w 324
    //   683: aload_0
    //   684: getfield 48	com/parse/ParseCommandCache:timeoutRetryWaitSeconds	D
    //   687: dmul
    //   688: d2l
    //   689: lsub
    //   690: lcmp
    //   691: ifge -73 -> 618
    //   694: lload 32
    //   696: ldc2_w 324
    //   699: aload_0
    //   700: getfield 48	com/parse/ParseCommandCache:timeoutRetryWaitSeconds	D
    //   703: dmul
    //   704: d2l
    //   705: lsub
    //   706: lstore 30
    //   708: goto -90 -> 618
    //   711: astore 34
    //   713: aload_0
    //   714: iconst_1
    //   715: putfield 64	com/parse/ParseCommandCache:shouldStop	Z
    //   718: goto -47 -> 671
    //   721: aload_0
    //   722: iload_1
    //   723: iconst_1
    //   724: isub
    //   725: invokespecial 333	com/parse/ParseCommandCache:maybeRunAllCommandsNow	(I)V
    //   728: goto -536 -> 192
    //   731: bipush 6
    //   733: invokestatic 199	com/parse/Parse:getLogLevel	()I
    //   736: if_icmplt +18 -> 754
    //   739: aload_0
    //   740: getfield 76	com/parse/ParseCommandCache:log	Ljava/util/logging/Logger;
    //   743: getstatic 205	java/util/logging/Level:SEVERE	Ljava/util/logging/Level;
    //   746: ldc_w 335
    //   749: aload 29
    //   751: invokevirtual 210	java/util/logging/Logger:log	(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   754: aload_0
    //   755: aload 8
    //   757: invokespecial 264	com/parse/ParseCommandCache:removeFile	(Ljava/io/File;)V
    //   760: aload_0
    //   761: getfield 60	com/parse/ParseCommandCache:testHelper	Lcom/parse/ParseCommandCache$TestHelper;
    //   764: ifnull +72 -> 836
    //   767: aload_0
    //   768: getfield 60	com/parse/ParseCommandCache:testHelper	Lcom/parse/ParseCommandCache$TestHelper;
    //   771: iconst_2
    //   772: invokevirtual 290	com/parse/ParseCommandCache$TestHelper:notify	(I)V
    //   775: goto +61 -> 836
    //   778: aload_2
    //   779: monitorexit
    //   780: return
    //   781: astore 39
    //   783: goto -559 -> 224
    //   786: astore 20
    //   788: goto -596 -> 192
    //   791: astore 15
    //   793: goto -361 -> 432
    //   796: astore 25
    //   798: goto -318 -> 480
    //   801: astore 14
    //   803: aload 11
    //   805: astore 9
    //   807: goto -385 -> 422
    //   810: astore 17
    //   812: aload 11
    //   814: astore 9
    //   816: goto -443 -> 373
    //   819: astore 13
    //   821: aload 11
    //   823: astore 9
    //   825: goto -501 -> 324
    //   828: astore 19
    //   830: aconst_null
    //   831: astore 9
    //   833: goto -673 -> 160
    //   836: goto -644 -> 192
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	839	0	this	ParseCommandCache
    //   0	839	1	paramInt	int
    //   3	776	2	localObject1	Object
    //   39	4	3	localObject2	Object
    //   23	42	4	arrayOfString	String[]
    //   52	10	5	i	int
    //   55	138	6	j	int
    //   69	11	7	str	String
    //   84	672	8	localFile	File
    //   87	745	9	localObject3	Object
    //   98	7	10	localFileInputStream	java.io.FileInputStream
    //   109	713	11	localBufferedInputStream	java.io.BufferedInputStream
    //   118	85	12	localByteArrayOutputStream	java.io.ByteArrayOutputStream
    //   322	21	13	localIOException1	IOException
    //   819	1	13	localIOException2	IOException
    //   420	13	14	localObject4	Object
    //   801	1	14	localObject5	Object
    //   791	1	15	localIOException3	IOException
    //   366	1	16	localIOException4	IOException
    //   371	21	17	localJSONException1	JSONException
    //   810	1	17	localJSONException2	JSONException
    //   415	1	18	localIOException5	IOException
    //   154	24	19	localFileNotFoundException1	java.io.FileNotFoundException
    //   828	1	19	localFileNotFoundException2	java.io.FileNotFoundException
    //   786	1	20	localIOException6	IOException
    //   125	19	21	arrayOfByte	byte[]
    //   134	13	22	k	int
    //   212	228	23	localJSONObject	JSONObject
    //   444	28	24	localParseCommand	ParseCommand
    //   478	21	25	localJSONException3	JSONException
    //   796	1	25	localJSONException4	JSONException
    //   462	5	26	local3	3
    //   248	226	27	localObject6	Object
    //   262	25	28	localParseCallback	ParseCallback
    //   530	220	29	localParseException	ParseException
    //   602	105	30	l1	long
    //   616	79	32	l2	long
    //   711	1	34	localInterruptedException	InterruptedException
    //   291	17	36	local4	4
    //   303	3	37	localHandler	android.os.Handler
    //   781	1	39	localIOException7	IOException
    // Exception table:
    //   from	to	target	type
    //   6	15	39	finally
    //   16	25	39	finally
    //   30	36	39	finally
    //   36	38	39	finally
    //   40	42	39	finally
    //   44	54	39	finally
    //   64	86	39	finally
    //   187	192	39	finally
    //   219	224	39	finally
    //   224	250	39	finally
    //   250	264	39	finally
    //   264	276	39	finally
    //   281	313	39	finally
    //   313	319	39	finally
    //   358	363	39	finally
    //   407	412	39	finally
    //   427	432	39	finally
    //   432	435	39	finally
    //   435	446	39	finally
    //   446	471	39	finally
    //   480	503	39	finally
    //   503	509	39	finally
    //   512	527	39	finally
    //   532	542	39	finally
    //   546	599	39	finally
    //   599	618	39	finally
    //   626	640	39	finally
    //   640	657	39	finally
    //   657	659	39	finally
    //   660	671	39	finally
    //   671	708	39	finally
    //   713	718	39	finally
    //   721	728	39	finally
    //   731	754	39	finally
    //   754	775	39	finally
    //   778	780	39	finally
    //   111	127	154	java/io/FileNotFoundException
    //   127	136	154	java/io/FileNotFoundException
    //   141	151	154	java/io/FileNotFoundException
    //   198	214	154	java/io/FileNotFoundException
    //   89	111	322	java/io/IOException
    //   358	363	366	java/io/IOException
    //   89	111	371	org/json/JSONException
    //   407	412	415	java/io/IOException
    //   89	111	420	finally
    //   160	182	420	finally
    //   324	347	420	finally
    //   347	353	420	finally
    //   373	396	420	finally
    //   396	402	420	finally
    //   435	446	478	org/json/JSONException
    //   264	276	530	com/parse/ParseException
    //   281	313	530	com/parse/ParseException
    //   313	319	530	com/parse/ParseException
    //   512	527	530	com/parse/ParseException
    //   660	671	711	java/lang/InterruptedException
    //   219	224	781	java/io/IOException
    //   187	192	786	java/io/IOException
    //   427	432	791	java/io/IOException
    //   446	471	796	org/json/JSONException
    //   111	127	801	finally
    //   127	136	801	finally
    //   141	151	801	finally
    //   198	214	801	finally
    //   111	127	810	org/json/JSONException
    //   127	136	810	org/json/JSONException
    //   141	151	810	org/json/JSONException
    //   198	214	810	org/json/JSONException
    //   111	127	819	java/io/IOException
    //   127	136	819	java/io/IOException
    //   141	151	819	java/io/IOException
    //   198	214	819	java/io/IOException
    //   89	111	828	java/io/FileNotFoundException
  }
  
  /* Error */
  private void removeFile(File paramFile)
  {
    // Byte code:
    //   0: getstatic 41	com/parse/ParseCommandCache:lock	Ljava/lang/Object;
    //   3: astore_2
    //   4: aload_2
    //   5: monitorenter
    //   6: aload_0
    //   7: getfield 58	com/parse/ParseCommandCache:callbacksForCommands	Ljava/util/HashMap;
    //   10: aload_0
    //   11: getfield 56	com/parse/ParseCommandCache:commandsInCache	Ljava/util/HashMap;
    //   14: aload_1
    //   15: invokevirtual 230	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   18: invokevirtual 340	java/util/HashMap:remove	(Ljava/lang/Object;)Ljava/lang/Object;
    //   21: pop
    //   22: aload_0
    //   23: getfield 56	com/parse/ParseCommandCache:commandsInCache	Ljava/util/HashMap;
    //   26: aload_1
    //   27: invokevirtual 340	java/util/HashMap:remove	(Ljava/lang/Object;)Ljava/lang/Object;
    //   30: pop
    //   31: aconst_null
    //   32: astore 6
    //   34: new 179	java/io/BufferedInputStream
    //   37: dup
    //   38: new 174	java/io/FileInputStream
    //   41: dup
    //   42: aload_1
    //   43: invokespecial 177	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   46: invokespecial 182	java/io/BufferedInputStream:<init>	(Ljava/io/InputStream;)V
    //   49: astore 7
    //   51: new 184	java/io/ByteArrayOutputStream
    //   54: dup
    //   55: invokespecial 185	java/io/ByteArrayOutputStream:<init>	()V
    //   58: astore 8
    //   60: sipush 1024
    //   63: newarray byte
    //   65: astore 15
    //   67: aload 7
    //   69: aload 15
    //   71: invokevirtual 191	java/io/InputStream:read	([B)I
    //   74: istore 16
    //   76: iload 16
    //   78: ifle +40 -> 118
    //   81: aload 8
    //   83: aload 15
    //   85: iconst_0
    //   86: iload 16
    //   88: invokevirtual 195	java/io/ByteArrayOutputStream:write	([BII)V
    //   91: goto -24 -> 67
    //   94: astore 11
    //   96: aload 7
    //   98: astore 12
    //   100: aload 12
    //   102: ifnull +8 -> 110
    //   105: aload 12
    //   107: invokevirtual 213	java/io/InputStream:close	()V
    //   110: aload_1
    //   111: invokevirtual 343	java/io/File:delete	()Z
    //   114: pop
    //   115: aload_2
    //   116: monitorexit
    //   117: return
    //   118: new 215	org/json/JSONObject
    //   121: dup
    //   122: aload 8
    //   124: ldc 217
    //   126: invokevirtual 221	java/io/ByteArrayOutputStream:toString	(Ljava/lang/String;)Ljava/lang/String;
    //   129: invokespecial 222	org/json/JSONObject:<init>	(Ljava/lang/String;)V
    //   132: astore 17
    //   134: new 232	com/parse/ParseCommand
    //   137: dup
    //   138: aload 17
    //   140: invokespecial 271	com/parse/ParseCommand:<init>	(Lorg/json/JSONObject;)V
    //   143: invokevirtual 346	com/parse/ParseCommand:releaseLocalIds	()V
    //   146: aload 7
    //   148: ifnull +81 -> 229
    //   151: aload 7
    //   153: invokevirtual 213	java/io/InputStream:close	()V
    //   156: goto -46 -> 110
    //   159: astore 19
    //   161: goto -51 -> 110
    //   164: astore 9
    //   166: aload 6
    //   168: ifnull +8 -> 176
    //   171: aload 6
    //   173: invokevirtual 213	java/io/InputStream:close	()V
    //   176: aload 9
    //   178: athrow
    //   179: astore_3
    //   180: aload_2
    //   181: monitorexit
    //   182: aload_3
    //   183: athrow
    //   184: astore 14
    //   186: goto -76 -> 110
    //   189: astore 10
    //   191: goto -15 -> 176
    //   194: astore 9
    //   196: aload 7
    //   198: astore 6
    //   200: goto -34 -> 166
    //   203: astore 9
    //   205: aload 7
    //   207: astore 6
    //   209: goto -43 -> 166
    //   212: astore 20
    //   214: aconst_null
    //   215: astore 12
    //   217: goto -117 -> 100
    //   220: astore 18
    //   222: aload 7
    //   224: astore 12
    //   226: goto -126 -> 100
    //   229: goto -119 -> 110
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	232	0	this	ParseCommandCache
    //   0	232	1	paramFile	File
    //   3	178	2	localObject1	Object
    //   179	4	3	localObject2	Object
    //   32	176	6	localObject3	Object
    //   49	174	7	localBufferedInputStream1	java.io.BufferedInputStream
    //   58	65	8	localByteArrayOutputStream	java.io.ByteArrayOutputStream
    //   164	13	9	localObject4	Object
    //   194	1	9	localObject5	Object
    //   203	1	9	localObject6	Object
    //   189	1	10	localIOException1	IOException
    //   94	1	11	localException1	Exception
    //   98	127	12	localBufferedInputStream2	java.io.BufferedInputStream
    //   184	1	14	localIOException2	IOException
    //   65	19	15	arrayOfByte	byte[]
    //   74	13	16	i	int
    //   132	7	17	localJSONObject	JSONObject
    //   220	1	18	localException2	Exception
    //   159	1	19	localIOException3	IOException
    //   212	1	20	localException3	Exception
    // Exception table:
    //   from	to	target	type
    //   51	67	94	java/lang/Exception
    //   67	76	94	java/lang/Exception
    //   81	91	94	java/lang/Exception
    //   118	134	94	java/lang/Exception
    //   151	156	159	java/io/IOException
    //   34	51	164	finally
    //   6	31	179	finally
    //   105	110	179	finally
    //   110	117	179	finally
    //   151	156	179	finally
    //   171	176	179	finally
    //   176	179	179	finally
    //   180	182	179	finally
    //   105	110	184	java/io/IOException
    //   171	176	189	java/io/IOException
    //   51	67	194	finally
    //   67	76	194	finally
    //   81	91	194	finally
    //   118	134	194	finally
    //   134	146	203	finally
    //   34	51	212	java/lang/Exception
    //   134	146	220	java/lang/Exception
  }
  
  private void runEventuallyInternal(ParseCommand paramParseCommand, ParseCallback<Void> paramParseCallback, boolean paramBoolean, ParseObject paramParseObject)
  {
    if (paramParseObject != null) {}
    byte[] arrayOfByte;
    try
    {
      if (paramParseObject.getObjectId() == null) {
        paramParseCommand.setLocalId(paramParseObject.getOrCreateLocalId());
      }
      arrayOfByte = paramParseCommand.toJSONObject().toString().getBytes("UTF-8");
      if (arrayOfByte.length > this.maxCacheSizeBytes)
      {
        if (5 >= Parse.getLogLevel()) {
          this.log.warning("Unable to save command for later because it's too big.");
        }
        if (this.testHelper != null) {
          this.testHelper.notify(4);
        }
        return;
      }
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      do
      {
        if (5 >= Parse.getLogLevel()) {
          this.log.log(Level.WARNING, "UTF-8 isn't supported.  This shouldn't happen.", localUnsupportedEncodingException);
        }
      } while (this.testHelper == null);
      this.testHelper.notify(4);
      return;
    }
    Object localObject1 = lock;
    for (;;)
    {
      try
      {
        String[] arrayOfString = this.cachePath.list();
        if (arrayOfString != null)
        {
          Arrays.sort(arrayOfString);
          int i = 0;
          int j = arrayOfString.length;
          int k = 0;
          if (k < j)
          {
            String str1 = arrayOfString[k];
            i += (int)new File(this.cachePath, str1).length();
            k++;
            continue;
          }
          int m = i + arrayOfByte.length;
          int n = this.maxCacheSizeBytes;
          if (m > n)
          {
            if (paramBoolean)
            {
              if (5 >= Parse.getLogLevel()) {
                this.log.warning("Unable to save command for later because storage is full.");
              }
              try
              {
                return;
              }
              finally {}
            }
            if (5 < Parse.getLogLevel()) {
              break label693;
            }
            this.log.warning("Deleting old commands to make room in command cache.");
            break label693;
            int i3 = this.maxCacheSizeBytes;
            if ((m > i3) && (i2 < arrayOfString.length))
            {
              File localFile2 = this.cachePath;
              int i4 = i2 + 1;
              File localFile3 = new File(localFile2, arrayOfString[i2]);
              m -= (int)localFile3.length();
              removeFile(localFile3);
              i2 = i4;
              continue;
            }
          }
        }
        String str2 = Long.toHexString(System.currentTimeMillis());
        if (str2.length() < 16)
        {
          char[] arrayOfChar2 = new char[16 - str2.length()];
          Arrays.fill(arrayOfChar2, '0');
          StringBuilder localStringBuilder2 = new StringBuilder();
          String str5 = new String(arrayOfChar2);
          str2 = str5 + str2;
        }
        int i1 = filenameCounter;
        filenameCounter = i1 + 1;
        String str3 = Integer.toHexString(i1);
        if (str3.length() < 8)
        {
          char[] arrayOfChar1 = new char[8 - str3.length()];
          Arrays.fill(arrayOfChar1, '0');
          StringBuilder localStringBuilder1 = new StringBuilder();
          String str4 = new String(arrayOfChar1);
          str3 = str4 + str3;
        }
        File localFile1 = File.createTempFile("CachedCommand_" + str2 + "_" + str3 + "_", "", this.cachePath);
        this.commandsInCache.put(localFile1, paramParseCommand);
        if (paramParseCallback != null) {
          this.callbacksForCommands.put(paramParseCommand, paramParseCallback);
        }
        paramParseCommand.retainLocalIds();
        FileOutputStream localFileOutputStream = new FileOutputStream(localFile1);
        BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(localFileOutputStream);
        localBufferedOutputStream.write(arrayOfByte);
        localBufferedOutputStream.close();
        if (this.testHelper != null) {
          this.testHelper.notify(3);
        }
      }
      catch (IOException localIOException)
      {
        if (5 < Parse.getLogLevel()) {
          continue;
        }
        this.log.log(Level.WARNING, "Unable to save command for later.", localIOException);
        lock.notify();
        continue;
      }
      finally
      {
        lock.notify();
      }
      return;
      label693:
      int i2 = 0;
    }
  }
  
  private void runLoop()
  {
    if (4 >= Parse.getLogLevel()) {
      this.log.info("Parse command cache has started processing queued commands.");
    }
    int i;
    for (;;)
    {
      Object localObject7;
      synchronized (this.runningLock)
      {
        if (this.running) {
          return;
        }
        this.running = true;
        this.runningLock.notifyAll();
        synchronized (lock)
        {
          if ((!this.shouldStop) && (!Thread.interrupted()))
          {
            i = 1;
            if (i == 0) {
              break label215;
            }
            localObject7 = lock;
          }
        }
      }
      try
      {
        maybeRunAllCommandsNow(this.timeoutMaxRetries);
        boolean bool = this.shouldStop;
        if (!bool) {}
        try
        {
          lock.wait();
          try
          {
            if (this.shouldStop) {
              break label269;
            }
            i = 1;
            continue;
          }
          finally {}
          localObject2 = finally;
          throw localObject2;
          i = 0;
          continue;
          localObject4 = finally;
          throw localObject4;
        }
        catch (InterruptedException localInterruptedException)
        {
          for (;;)
          {
            this.shouldStop = true;
          }
        }
        throw localObject8;
      }
      catch (Exception localException)
      {
        if (6 >= Parse.getLogLevel()) {
          this.log.log(Level.SEVERE, "saveEventually thread had an error.", localException);
        }
        if (this.shouldStop) {
          break label275;
        }
        i = 1;
      }
      finally
      {
        if (this.shouldStop) {}
      }
    }
    for (;;) {}
    label215:
    synchronized (this.runningLock)
    {
      this.running = false;
      this.runningLock.notifyAll();
      if (4 >= Parse.getLogLevel())
      {
        this.log.info("saveEventually thread has stopped processing commands.");
        return;
      }
    }
    return;
    for (;;)
    {
      break;
      label269:
      i = 0;
      break;
      label275:
      i = 0;
    }
  }
  
  public void clear()
  {
    synchronized (lock)
    {
      File[] arrayOfFile = this.cachePath.listFiles();
      if (arrayOfFile == null) {
        return;
      }
      int i = arrayOfFile.length;
      for (int j = 0; j < i; j++) {
        removeFile(arrayOfFile[j]);
      }
      this.commandsInCache.clear();
      return;
    }
  }
  
  public TestHelper getTestHelper()
  {
    if (this.testHelper == null) {
      this.testHelper = new TestHelper(null);
    }
    return this.testHelper;
  }
  
  public void pause()
  {
    synchronized (this.runningLock)
    {
      if (this.running) {}
      synchronized (lock)
      {
        this.shouldStop = true;
        lock.notify();
        for (;;)
        {
          boolean bool = this.running;
          if (bool) {
            try
            {
              this.runningLock.wait();
            }
            catch (InterruptedException localInterruptedException) {}
          }
        }
      }
    }
  }
  
  public int pendingCount()
  {
    synchronized (lock)
    {
      String[] arrayOfString = this.cachePath.list();
      if (arrayOfString == null)
      {
        i = 0;
        return i;
      }
      int i = arrayOfString.length;
    }
  }
  
  public void resume()
  {
    synchronized (this.runningLock)
    {
      if (!this.running) {
        new Thread("ParseCommandCache.runLoop()")
        {
          public void run()
          {
            ParseCommandCache.this.runLoop();
          }
        }.start();
      }
      try
      {
        this.runningLock.wait();
        return;
      }
      catch (InterruptedException localInterruptedException)
      {
        synchronized (lock)
        {
          this.shouldStop = true;
          lock.notify();
        }
      }
    }
  }
  
  public void runEventually(ParseCommand paramParseCommand, ParseCallback<Void> paramParseCallback, ParseObject paramParseObject)
  {
    Parse.requirePermission("android.permission.ACCESS_NETWORK_STATE");
    runEventuallyInternal(paramParseCommand, paramParseCallback, false, paramParseObject);
  }
  
  public void setConnected(boolean paramBoolean)
  {
    synchronized (lock)
    {
      if (this.connected != paramBoolean)
      {
        this.connected = paramBoolean;
        if (paramBoolean) {
          lock.notify();
        }
      }
      return;
    }
  }
  
  public void setMaxCacheSizeBytes(int paramInt)
  {
    synchronized (lock)
    {
      this.maxCacheSizeBytes = paramInt;
      return;
    }
  }
  
  public void setTimeoutMaxRetries(int paramInt)
  {
    synchronized (lock)
    {
      this.timeoutMaxRetries = paramInt;
      return;
    }
  }
  
  public void setTimeoutRetryWaitSeconds(double paramDouble)
  {
    synchronized (lock)
    {
      this.timeoutRetryWaitSeconds = paramDouble;
      return;
    }
  }
  
  void simulateReboot()
  {
    synchronized (lock)
    {
      this.commandsInCache.clear();
      this.callbacksForCommands.clear();
      return;
    }
  }
  
  public class TestHelper
  {
    public static final int COMMAND_ENQUEUED = 3;
    public static final int COMMAND_FAILED = 2;
    public static final int COMMAND_NOT_ENQUEUED = 4;
    public static final int COMMAND_SUCCESSFUL = 1;
    private static final int MAX_EVENTS = 1000;
    @SuppressLint({"UseSparseArrays"})
    private HashMap<Integer, Semaphore> events = new HashMap();
    
    private TestHelper()
    {
      clear();
    }
    
    public void clear()
    {
      this.events.clear();
      this.events.put(Integer.valueOf(1), new Semaphore(1000));
      this.events.put(Integer.valueOf(2), new Semaphore(1000));
      this.events.put(Integer.valueOf(3), new Semaphore(1000));
      this.events.put(Integer.valueOf(4), new Semaphore(1000));
      Iterator localIterator = this.events.keySet().iterator();
      while (localIterator.hasNext())
      {
        int i = ((Integer)localIterator.next()).intValue();
        ((Semaphore)this.events.get(Integer.valueOf(i))).acquireUninterruptibly(1000);
      }
    }
    
    public void notify(int paramInt)
    {
      ((Semaphore)this.events.get(Integer.valueOf(paramInt))).release();
    }
    
    public int unexpectedEvents()
    {
      int i = 0;
      Iterator localIterator = this.events.keySet().iterator();
      while (localIterator.hasNext())
      {
        int j = ((Integer)localIterator.next()).intValue();
        i += ((Semaphore)this.events.get(Integer.valueOf(j))).availablePermits();
      }
      return i;
    }
    
    public boolean waitFor(int paramInt)
    {
      try
      {
        boolean bool = ((Semaphore)this.events.get(Integer.valueOf(paramInt))).tryAcquire(5000L, TimeUnit.MILLISECONDS);
        return bool;
      }
      catch (InterruptedException localInterruptedException)
      {
        localInterruptedException.printStackTrace();
      }
      return false;
    }
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.ParseCommandCache
 * JD-Core Version:    0.7.0.1
 */