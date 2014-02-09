package com.parse;

import android.content.Context;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;
import org.json.JSONException;
import org.json.JSONObject;

public class ParseTestUtils
{
  private static final String TAG = "com.parse.ParseTestUtils";
  private static int serverPort = 9000;
  private static Synchronizer synchronizer;
  
  public static void allowSleep(int paramInt)
  {
    PushService.sleepSemaphore.release(paramInt);
  }
  
  public static void assertFinishes()
  {
    synchronizer.assertFinishes();
  }
  
  public static void assertSlept()
  {
    for (;;)
    {
      if (PushService.sleepSemaphore.availablePermits() == 0) {
        return;
      }
      try
      {
        Thread.sleep(50L);
      }
      catch (InterruptedException localInterruptedException)
      {
        throw new RuntimeException(localInterruptedException.getMessage());
      }
    }
  }
  
  public static void beginFakeSleep()
  {
    PushService.sleepSemaphore = new Semaphore(0);
  }
  
  public static void clearApp()
  {
    ParseCommand localParseCommand = new ParseCommand("clear_app");
    try
    {
      localParseCommand.perform();
      return;
    }
    catch (ParseException localParseException)
    {
      throw new RuntimeException(localParseException.getMessage());
    }
  }
  
  public static void clearCurrentInstallationFromMemory()
  {
    ParseInstallation.currentInstallation = null;
  }
  
  public static void clearFiles()
  {
    recursiveDelete(Parse.getParseDir());
    recursiveDelete(Parse.getKeyValueCacheDir());
    if (Parse.commandCache != null)
    {
      Parse.commandCache.pause();
      Parse.commandCache = null;
    }
  }
  
  public static void clearPushRouterStateFromMemory() {}
  
  public static int commandCacheUnexpectedEvents()
  {
    return Parse.getCommandCache().getTestHelper().unexpectedEvents();
  }
  
  public static int consecutiveFailures()
  {
    return PushService.consecutiveFailures;
  }
  
  public static void disconnectCommandCache()
  {
    Parse.getCommandCache().setConnected(false);
  }
  
  public static void endFakeSleep()
  {
    PushService.sleepSemaphore.release(1000000);
  }
  
  public static void finish()
  {
    synchronizer.finish();
  }
  
  public static String getIgnoreAfterTime()
  {
    return ParsePushRouter.ignoreAfter;
  }
  
  public static String getInstallationId(Context paramContext)
  {
    return ParseInstallation.getCurrentInstallation().getInstallationId();
  }
  
  public static String getLastTime()
  {
    return ParsePushRouter.lastTime;
  }
  
  public static ParseObject getObjectFromDisk(Context paramContext, String paramString)
  {
    return ParseObject.getFromDisk(paramContext, paramString);
  }
  
  static File getParseDir(Context paramContext)
  {
    return paramContext.getDir("Parse", 0);
  }
  
  public static JSONObject getPushRequestJSON(Context paramContext)
  {
    return ParsePushRouter.getPushRequestJSON(paramContext);
  }
  
  public static ParseUser getUserObjectFromDisk(Context paramContext, String paramString)
  {
    return (ParseUser)ParseObject.getFromDisk(paramContext, paramString);
  }
  
  public static void initSynchronizer()
  {
    synchronizer = new Synchronizer();
  }
  
  public static Set<String> keySet(ParseObject paramParseObject)
  {
    return paramParseObject.keySet();
  }
  
  public static ServerSocket mockPushServer()
  {
    serverPort = 1 + serverPort;
    PushService.usePort(serverPort);
    Parse.logI("com.parse.ParseTestUtils", "running mockPushServer on port " + serverPort);
    try
    {
      ServerSocket localServerSocket = new ServerSocket(serverPort);
      return localServerSocket;
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException(localIOException.getMessage());
    }
  }
  
  public static void mockV8Client()
  {
    ParseCommand localParseCommand = new ParseCommand("mock_v8_client");
    try
    {
      localParseCommand.perform();
      return;
    }
    catch (ParseException localParseException)
    {
      throw new RuntimeException(localParseException);
    }
  }
  
  public static int numKeyValueCacheFiles()
  {
    return Parse.getKeyValueCacheDir().listFiles().length;
  }
  
  public static void onPush(Context paramContext, String paramString, PushCallback paramPushCallback)
  {
    ParsePushRouter.addSingletonRoute(paramContext, paramString, paramPushCallback);
    PushService.startServiceIfRequired(paramContext);
  }
  
  public static Set<String> pushRoutes(Context paramContext)
  {
    ParsePushRouter.ensureStateIsLoaded(paramContext);
    return ParsePushRouter.channelRoutes.keySet();
  }
  
  public static void reconnectCommandCache()
  {
    Parse.getCommandCache().setConnected(true);
  }
  
  public static void recursiveDelete(File paramFile)
  {
    if (!paramFile.exists()) {
      return;
    }
    if (paramFile.isDirectory())
    {
      File[] arrayOfFile = paramFile.listFiles();
      int i = arrayOfFile.length;
      for (int j = 0; j < i; j++) {
        recursiveDelete(arrayOfFile[j]);
      }
    }
    paramFile.delete();
  }
  
  public static void resetCommandCache()
  {
    ParseCommandCache localParseCommandCache = Parse.getCommandCache();
    ParseCommandCache.TestHelper localTestHelper = localParseCommandCache.getTestHelper();
    localParseCommandCache.clear();
    localTestHelper.clear();
  }
  
  public static void saveObjectToDisk(ParseObject paramParseObject, Context paramContext, String paramString)
  {
    paramParseObject.saveToDisk(paramContext, paramString);
  }
  
  public static void saveStringToDisk(String paramString1, Context paramContext, String paramString2)
  {
    File localFile = new File(getParseDir(paramContext), paramString2);
    try
    {
      FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
      localFileOutputStream.write(paramString1.getBytes("UTF-8"));
      localFileOutputStream.close();
      return;
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException) {}catch (IOException localIOException) {}
  }
  
  public static void setCommandInitialDelay(double paramDouble)
  {
    ParseCommand.setInitialDelay(paramDouble);
  }
  
  public static void setMaxKeyValueCacheBytes(int paramInt)
  {
    Parse.maxKeyValueCacheBytes = paramInt;
  }
  
  public static void setMaxKeyValueCacheFiles(int paramInt)
  {
    Parse.maxKeyValueCacheFiles = paramInt;
  }
  
  public static int setPushHistoryLength(int paramInt)
  {
    int i = ParsePushRouter.maxHistory;
    ParsePushRouter.maxHistory = paramInt;
    return i;
  }
  
  public static void setUpPushTest(Context paramContext)
  {
    StandardPushCallback.disableNotifications = true;
    StandardPushCallback.totalNotifications = 0;
    PushService.sleepSemaphore = null;
    PushService.useServer("localhost");
    useServer("http://10.0.2.2:3000");
    ParsePushRouter.clearStateFromDisk(paramContext);
    ParseInstallation.clearCurrentInstallationFromDisk(paramContext);
    initSynchronizer();
  }
  
  public static void start(int paramInt)
  {
    synchronizer.start(paramInt);
  }
  
  public static void startServiceIfRequired(Context paramContext)
  {
    PushService.startServiceIfRequired(paramContext);
  }
  
  public static void tearDownPushTest(Context paramContext)
  {
    clearFiles();
    ParseInstallation.clearCurrentInstallationFromDisk(paramContext);
    ParsePushRouter.clearStateFromDisk(paramContext);
    PushService.socket = null;
  }
  
  public static String toDeterministicString(Object paramObject)
  {
    try
    {
      String str = ParseCommand.toDeterministicString(paramObject);
      return str;
    }
    catch (JSONException localJSONException)
    {
      throw new RuntimeException(localJSONException.getMessage());
    }
  }
  
  public static int totalNotifications()
  {
    return StandardPushCallback.totalNotifications;
  }
  
  public static void unmockV8Client()
  {
    ParseCommand localParseCommand = new ParseCommand("unmock_v8_client");
    try
    {
      localParseCommand.perform();
      return;
    }
    catch (ParseException localParseException)
    {
      throw new RuntimeException(localParseException);
    }
  }
  
  public static String useBadServerPort()
  {
    return useServer("http://10.0.2.2:6666");
  }
  
  public static void useDevPushServer()
  {
    PushService.useServer("10.0.2.2");
  }
  
  public static String useInvalidServer()
  {
    return useServer("http://invalid.server:3000");
  }
  
  public static String useServer(String paramString)
  {
    String str = ParseObject.server;
    ParseObject.server = paramString;
    return str;
  }
  
  public static boolean waitForCommandCacheEnqueue()
  {
    return Parse.getCommandCache().getTestHelper().waitFor(3);
  }
  
  public static boolean waitForCommandCacheFailure()
  {
    return Parse.getCommandCache().getTestHelper().waitFor(2);
  }
  
  public static boolean waitForCommandCacheSuccess()
  {
    return Parse.getCommandCache().getTestHelper().waitFor(1);
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.ParseTestUtils
 * JD-Core Version:    0.7.0.1
 */