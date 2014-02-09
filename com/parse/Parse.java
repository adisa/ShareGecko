package com.parse;

import android.content.Context;
import android.util.Log;
import com.parse.codec.binary.Base64;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SimpleTimeZone;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Parse
{
  public static final int LOG_LEVEL_DEBUG = 3;
  public static final int LOG_LEVEL_ERROR = 6;
  public static final int LOG_LEVEL_INFO = 4;
  public static final int LOG_LEVEL_NONE = 2147483647;
  public static final int LOG_LEVEL_VERBOSE = 2;
  public static final int LOG_LEVEL_WARNING = 5;
  private static final String TAG = "com.parse.Parse";
  static Context applicationContext;
  static String applicationId;
  static String clientKey;
  static ParseCommandCache commandCache;
  private static final DateFormat dateFormat;
  private static int logLevel = 6;
  static int maxKeyValueCacheBytes;
  static int maxKeyValueCacheFiles;
  static int maxParseFileSize = 10485760;
  
  static
  {
    maxKeyValueCacheBytes = 2097152;
    maxKeyValueCacheFiles = 1000;
    commandCache = null;
    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    localSimpleDateFormat.setTimeZone(new SimpleTimeZone(0, "GMT"));
    dateFormat = localSimpleDateFormat;
  }
  
  private Parse()
  {
    throw new AssertionError();
  }
  
  static Number addNumbers(Number paramNumber1, Number paramNumber2)
  {
    if ((paramNumber1 instanceof Double)) {
      return Double.valueOf(paramNumber1.doubleValue() + paramNumber2.doubleValue());
    }
    if ((paramNumber1 instanceof Long)) {
      return Long.valueOf(paramNumber1.longValue() + paramNumber2.longValue());
    }
    if ((paramNumber1 instanceof Float)) {
      return Float.valueOf(paramNumber1.floatValue() + paramNumber2.floatValue());
    }
    if ((paramNumber1 instanceof Short)) {
      return Integer.valueOf(paramNumber1.shortValue() + paramNumber2.shortValue());
    }
    if ((paramNumber1 instanceof Byte)) {
      return Integer.valueOf(paramNumber1.byteValue() + paramNumber2.byteValue());
    }
    return Integer.valueOf(paramNumber1.intValue() + paramNumber2.intValue());
  }
  
  /* Error */
  static void checkCacheApplicationId()
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 137	com/parse/Parse:applicationId	Ljava/lang/String;
    //   6: ifnull +134 -> 140
    //   9: new 139	java/io/File
    //   12: dup
    //   13: invokestatic 143	com/parse/Parse:getParseDir	()Ljava/io/File;
    //   16: ldc 144
    //   18: invokespecial 147	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   21: astore_1
    //   22: aload_1
    //   23: invokevirtual 151	java/io/File:exists	()Z
    //   26: istore_2
    //   27: iload_2
    //   28: ifeq +71 -> 99
    //   31: new 153	java/io/RandomAccessFile
    //   34: dup
    //   35: aload_1
    //   36: ldc 155
    //   38: invokespecial 156	java/io/RandomAccessFile:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   41: astore 8
    //   43: aload 8
    //   45: invokevirtual 159	java/io/RandomAccessFile:length	()J
    //   48: l2i
    //   49: newarray byte
    //   51: astore 12
    //   53: aload 8
    //   55: aload 12
    //   57: invokevirtual 163	java/io/RandomAccessFile:readFully	([B)V
    //   60: aload 8
    //   62: invokevirtual 166	java/io/RandomAccessFile:close	()V
    //   65: new 168	java/lang/String
    //   68: dup
    //   69: aload 12
    //   71: ldc 170
    //   73: invokespecial 173	java/lang/String:<init>	([BLjava/lang/String;)V
    //   76: getstatic 137	com/parse/Parse:applicationId	Ljava/lang/String;
    //   79: invokevirtual 177	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   82: istore 13
    //   84: iload 13
    //   86: istore 10
    //   88: iload 10
    //   90: ifne +9 -> 99
    //   93: invokestatic 143	com/parse/Parse:getParseDir	()Ljava/io/File;
    //   96: invokestatic 181	com/parse/Parse:recursiveDelete	(Ljava/io/File;)V
    //   99: new 139	java/io/File
    //   102: dup
    //   103: invokestatic 143	com/parse/Parse:getParseDir	()Ljava/io/File;
    //   106: ldc 144
    //   108: invokespecial 147	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   111: astore_3
    //   112: new 183	java/io/FileOutputStream
    //   115: dup
    //   116: aload_3
    //   117: invokespecial 185	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   120: astore 4
    //   122: aload 4
    //   124: getstatic 137	com/parse/Parse:applicationId	Ljava/lang/String;
    //   127: ldc 170
    //   129: invokevirtual 189	java/lang/String:getBytes	(Ljava/lang/String;)[B
    //   132: invokevirtual 192	java/io/FileOutputStream:write	([B)V
    //   135: aload 4
    //   137: invokevirtual 193	java/io/FileOutputStream:close	()V
    //   140: ldc 2
    //   142: monitorexit
    //   143: return
    //   144: astore_0
    //   145: ldc 2
    //   147: monitorexit
    //   148: aload_0
    //   149: athrow
    //   150: astore 7
    //   152: goto -12 -> 140
    //   155: astore 6
    //   157: goto -17 -> 140
    //   160: astore 5
    //   162: goto -22 -> 140
    //   165: astore 11
    //   167: iconst_0
    //   168: istore 10
    //   170: goto -82 -> 88
    //   173: astore 9
    //   175: iconst_0
    //   176: istore 10
    //   178: goto -90 -> 88
    // Local variable table:
    //   start	length	slot	name	signature
    //   144	5	0	localObject	Object
    //   21	15	1	localFile1	File
    //   26	2	2	bool1	boolean
    //   111	6	3	localFile2	File
    //   120	16	4	localFileOutputStream	FileOutputStream
    //   160	1	5	localFileNotFoundException1	java.io.FileNotFoundException
    //   155	1	6	localUnsupportedEncodingException	UnsupportedEncodingException
    //   150	1	7	localIOException1	IOException
    //   41	20	8	localRandomAccessFile	RandomAccessFile
    //   173	1	9	localFileNotFoundException2	java.io.FileNotFoundException
    //   86	91	10	bool2	boolean
    //   165	1	11	localIOException2	IOException
    //   51	19	12	arrayOfByte	byte[]
    //   82	3	13	bool3	boolean
    // Exception table:
    //   from	to	target	type
    //   3	27	144	finally
    //   31	84	144	finally
    //   93	99	144	finally
    //   99	112	144	finally
    //   112	140	144	finally
    //   112	140	150	java/io/IOException
    //   112	140	155	java/io/UnsupportedEncodingException
    //   112	140	160	java/io/FileNotFoundException
    //   31	84	165	java/io/IOException
    //   31	84	173	java/io/FileNotFoundException
  }
  
  static void checkContext()
  {
    if (applicationContext == null) {
      throw new RuntimeException("applicationContext is null. You must call Parse.initialize(context, applicationId, clientKey) before using the Parse library.");
    }
  }
  
  static void checkInit()
  {
    if (applicationId == null) {
      throw new RuntimeException("applicationId is null. You must call Parse.initialize(context, applicationId, clientKey) before using the Parse library.");
    }
    if (clientKey == null) {
      throw new RuntimeException("clientKey is null. You must call Parse.initialize(context, applicationId, clientKey) before using the Parse library.");
    }
  }
  
  static void clearCacheDir()
  {
    File[] arrayOfFile = getKeyValueCacheDir().listFiles();
    if (arrayOfFile == null) {}
    for (;;)
    {
      return;
      for (int i = 0; i < arrayOfFile.length; i++) {
        arrayOfFile[i].delete();
      }
    }
  }
  
  static void clearFromKeyValueCache(String paramString)
  {
    File localFile = getKeyValueCacheFile(paramString);
    if (localFile != null) {
      localFile.delete();
    }
  }
  
  static List<Object> convertArrayToList(JSONArray paramJSONArray)
  {
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    if (i < paramJSONArray.length())
    {
      Object localObject1 = paramJSONArray.opt(i);
      Object localObject2 = decodeJSONObject(localObject1);
      if (localObject2 != null) {
        localArrayList.add(localObject2);
      }
      for (;;)
      {
        i++;
        break;
        localArrayList.add(localObject1);
      }
    }
    return localArrayList;
  }
  
  static Map<String, Object> convertJSONObjectToMap(JSONObject paramJSONObject)
  {
    HashMap localHashMap = new HashMap();
    Iterator localIterator = paramJSONObject.keys();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      Object localObject1 = paramJSONObject.opt(str);
      Object localObject2 = decodeJSONObject(localObject1);
      if (localObject2 != null) {
        localHashMap.put(str, localObject2);
      } else if ((localObject1 instanceof JSONArray)) {
        localHashMap.put(str, convertArrayToList((JSONArray)localObject1));
      } else {
        localHashMap.put(str, localObject1);
      }
    }
    return localHashMap;
  }
  
  static File createKeyValueCacheFile(String paramString)
  {
    String str = String.valueOf(new Date().getTime()) + '.' + paramString;
    return new File(getKeyValueCacheDir(), str);
  }
  
  static JSONObject dateToObject(Date paramDate)
  {
    JSONObject localJSONObject = new JSONObject();
    String str = encodeDate(paramDate);
    try
    {
      localJSONObject.put("__type", "Date");
      localJSONObject.put("iso", str);
      return localJSONObject;
    }
    catch (JSONException localJSONException)
    {
      throw new RuntimeException(localJSONException);
    }
  }
  
  static Object decodeJSONObject(Object paramObject)
  {
    JSONObject localJSONObject1;
    if ((paramObject instanceof JSONObject))
    {
      localJSONObject1 = (JSONObject)paramObject;
      if (localJSONObject1.optString("__op", null) == null) {}
    }
    else
    {
      try
      {
        ParseFieldOperation localParseFieldOperation = ParseFieldOperations.decode(localJSONObject1);
        return localParseFieldOperation;
      }
      catch (JSONException localJSONException3)
      {
        throw new RuntimeException(localJSONException3);
      }
      return null;
    }
    String str = localJSONObject1.optString("__type", null);
    if (str == null) {
      return convertJSONObjectToMap(localJSONObject1);
    }
    if (str.equals("Date")) {
      return parseDate(localJSONObject1.optString("iso"));
    }
    if (str.equals("Bytes")) {
      return Base64.decodeBase64(localJSONObject1.optString("base64"));
    }
    if (str.equals("Pointer")) {
      return ParseObject.createWithoutData(localJSONObject1.optString("className"), localJSONObject1.optString("objectId"));
    }
    if (str.equals("File")) {
      return new ParseFile(localJSONObject1.optString("name"), localJSONObject1.optString("url"));
    }
    if (str.equals("GeoPoint")) {
      try
      {
        double d1 = localJSONObject1.getDouble("latitude");
        double d2 = localJSONObject1.getDouble("longitude");
        return new ParseGeoPoint(d1, d2);
      }
      catch (JSONException localJSONException2)
      {
        throw new RuntimeException(localJSONException2);
      }
    }
    if (str.equals("Object"))
    {
      JSONObject localJSONObject2 = new JSONObject();
      try
      {
        localJSONObject2.put("data", localJSONObject1);
        ParseObject localParseObject = ParseObject.createWithoutData(localJSONObject1.optString("className"), null);
        localParseObject.mergeAfterFetch(localJSONObject2);
        return localParseObject;
      }
      catch (JSONException localJSONException1)
      {
        throw new RuntimeException(localJSONException1);
      }
    }
    if (str.equals("Relation")) {
      return new ParseRelation(localJSONObject1.optString("className", null));
    }
    return null;
  }
  
  static JSONArray encodeAsJSONArray(List<Object> paramList, boolean paramBoolean)
  {
    JSONArray localJSONArray = new JSONArray();
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      if (!isValidType(localObject)) {
        throw new IllegalArgumentException("invalid type for value in array: " + localObject.getClass().toString());
      }
      localJSONArray.put(maybeEncodeJSONObject(localObject, paramBoolean));
    }
    return localJSONArray;
  }
  
  static String encodeDate(Date paramDate)
  {
    try
    {
      String str = dateFormat.format(paramDate);
      return str;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  static JSONObject encodeJSONObject(Object paramObject, boolean paramBoolean)
  {
    try
    {
      if ((paramObject instanceof Date)) {
        return dateToObject((Date)paramObject);
      }
      if ((paramObject instanceof byte[]))
      {
        JSONObject localJSONObject1 = new JSONObject();
        localJSONObject1.put("__type", "Bytes");
        localJSONObject1.put("base64", Base64.encodeBase64String((byte[])paramObject));
        return localJSONObject1;
      }
    }
    catch (JSONException localJSONException)
    {
      throw new RuntimeException(localJSONException);
    }
    if ((paramObject instanceof ParseObject))
    {
      if (!paramBoolean) {
        throw new IllegalArgumentException("ParseObjects not allowed here");
      }
      return parseObjectToJSONPointer((ParseObject)paramObject);
    }
    if ((paramObject instanceof ParseFile))
    {
      ParseFile localParseFile = (ParseFile)paramObject;
      JSONObject localJSONObject5 = new JSONObject();
      localJSONObject5.put("__type", "File");
      localJSONObject5.put("url", localParseFile.getUrl());
      localJSONObject5.put("name", localParseFile.getName());
      return localJSONObject5;
    }
    if ((paramObject instanceof ParseGeoPoint))
    {
      ParseGeoPoint localParseGeoPoint = (ParseGeoPoint)paramObject;
      JSONObject localJSONObject4 = new JSONObject();
      localJSONObject4.put("__type", "GeoPoint");
      localJSONObject4.put("latitude", localParseGeoPoint.getLatitude());
      localJSONObject4.put("longitude", localParseGeoPoint.getLongitude());
      return localJSONObject4;
    }
    if ((paramObject instanceof ParseACL)) {
      return ((ParseACL)paramObject).toJSONObject();
    }
    if ((paramObject instanceof Map))
    {
      Map localMap = (Map)paramObject;
      localJSONObject2 = new JSONObject();
      Iterator localIterator = localMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        localJSONObject2.put((String)localEntry.getKey(), maybeEncodeJSONObject(localEntry.getValue(), paramBoolean));
      }
    }
    if ((paramObject instanceof ParseRelation))
    {
      JSONObject localJSONObject3 = ((ParseRelation)paramObject).encodeToJSON();
      return localJSONObject3;
    }
    JSONObject localJSONObject2 = null;
    return localJSONObject2;
  }
  
  static ParseCommandCache getCommandCache()
  {
    try
    {
      if (commandCache == null)
      {
        checkContext();
        commandCache = new ParseCommandCache(applicationContext);
      }
      ParseCommandCache localParseCommandCache = commandCache;
      return localParseCommandCache;
    }
    finally {}
  }
  
  static long getKeyValueCacheAge(File paramFile)
  {
    String str = paramFile.getName();
    try
    {
      long l = Long.parseLong(str.substring(0, str.indexOf('.')));
      return l;
    }
    catch (NumberFormatException localNumberFormatException) {}
    return 0L;
  }
  
  static File getKeyValueCacheDir()
  {
    try
    {
      checkContext();
      File localFile = new File(applicationContext.getCacheDir(), "ParseKeyValueCache");
      if (!localFile.isDirectory())
      {
        boolean bool = localFile.mkdir();
        if (!bool) {}
      }
      else
      {
        return localFile;
      }
      throw new RuntimeException("could not create Parse cache directory");
    }
    finally {}
  }
  
  static File getKeyValueCacheFile(String paramString)
  {
    String str = '.' + paramString;
    File[] arrayOfFile = getKeyValueCacheDir().listFiles(new FilenameFilter()
    {
      public boolean accept(File paramAnonymousFile, String paramAnonymousString)
      {
        return paramAnonymousString.endsWith(this.val$suffix);
      }
    });
    if (arrayOfFile.length == 0) {
      return null;
    }
    return arrayOfFile[0];
  }
  
  public static int getLogLevel()
  {
    return logLevel;
  }
  
  static File getParseDir()
  {
    try
    {
      checkContext();
      File localFile = applicationContext.getDir("Parse", 0);
      return localFile;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  static boolean hasPermission(String paramString)
  {
    checkContext();
    return applicationContext.checkCallingOrSelfPermission(paramString) == 0;
  }
  
  public static void initialize(Context paramContext, String paramString1, String paramString2)
  {
    applicationId = paramString1;
    clientKey = paramString2;
    if (paramContext != null)
    {
      applicationContext = paramContext.getApplicationContext();
      checkCacheApplicationId();
      new Thread("Parse.initialize Starting Command Cache")
      {
        public void run()
        {
          Parse.getCommandCache();
        }
      }.start();
    }
  }
  
  static boolean isContainerObject(Object paramObject)
  {
    return ((paramObject instanceof JSONObject)) || ((paramObject instanceof JSONArray)) || ((paramObject instanceof ParseACL)) || ((paramObject instanceof ParseGeoPoint)) || ((paramObject instanceof List)) || ((paramObject instanceof Map));
  }
  
  static boolean isValidType(Object paramObject)
  {
    return ((paramObject instanceof JSONObject)) || ((paramObject instanceof JSONArray)) || ((paramObject instanceof String)) || ((paramObject instanceof Number)) || ((paramObject instanceof Boolean)) || (paramObject == JSONObject.NULL) || ((paramObject instanceof ParseObject)) || ((paramObject instanceof ParseACL)) || ((paramObject instanceof ParseFile)) || ((paramObject instanceof ParseGeoPoint)) || ((paramObject instanceof Date)) || ((paramObject instanceof byte[])) || ((paramObject instanceof List)) || ((paramObject instanceof Map)) || ((paramObject instanceof ParseRelation));
  }
  
  static String join(Collection<String> paramCollection, String paramString)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    Iterator localIterator = paramCollection.iterator();
    if (localIterator.hasNext())
    {
      localStringBuffer.append((String)localIterator.next());
      while (localIterator.hasNext())
      {
        localStringBuffer.append(paramString);
        localStringBuffer.append((String)localIterator.next());
      }
    }
    return localStringBuffer.toString();
  }
  
  static Object jsonFromKeyValueCache(String paramString, long paramLong)
  {
    String str = loadFromKeyValueCache(paramString, paramLong);
    if (str == null) {
      return null;
    }
    JSONTokener localJSONTokener = new JSONTokener(str);
    try
    {
      Object localObject = localJSONTokener.nextValue();
      return localObject;
    }
    catch (JSONException localJSONException)
    {
      logE("com.parse.Parse", "corrupted cache for " + paramString, localJSONException);
      clearFromKeyValueCache(paramString);
    }
    return null;
  }
  
  static Iterable<String> keys(JSONObject paramJSONObject)
  {
    new Iterable()
    {
      public Iterator<String> iterator()
      {
        return this.val$finalObject.keys();
      }
    };
  }
  
  static String loadFromKeyValueCache(String paramString, long paramLong)
  {
    File localFile = getKeyValueCacheFile(paramString);
    if (localFile == null) {}
    Date localDate;
    long l;
    do
    {
      return null;
      localDate = new Date();
      l = Math.max(0L, localDate.getTime() - paramLong);
    } while (getKeyValueCacheAge(localFile) < l);
    localFile.setLastModified(localDate.getTime());
    try
    {
      RandomAccessFile localRandomAccessFile = new RandomAccessFile(localFile, "r");
      byte[] arrayOfByte = new byte[(int)localRandomAccessFile.length()];
      localRandomAccessFile.readFully(arrayOfByte);
      localRandomAccessFile.close();
      String str = new String(arrayOfByte, "UTF-8");
      return str;
    }
    catch (IOException localIOException)
    {
      logE("com.parse.Parse", "error reading from cache", localIOException);
    }
    return null;
  }
  
  private static void log(int paramInt, String paramString1, String paramString2, Throwable paramThrowable)
  {
    if (paramInt >= logLevel)
    {
      if (paramThrowable == null) {
        Log.println(logLevel, paramString1, paramString2);
      }
    }
    else {
      return;
    }
    Log.println(logLevel, paramString1, paramString2 + '\n' + Log.getStackTraceString(paramThrowable));
  }
  
  static void logD(String paramString1, String paramString2)
  {
    logD(paramString1, paramString2, null);
  }
  
  static void logD(String paramString1, String paramString2, Throwable paramThrowable)
  {
    log(3, paramString1, paramString2, paramThrowable);
  }
  
  static void logE(String paramString1, String paramString2)
  {
    logE(paramString1, paramString2, null);
  }
  
  static void logE(String paramString1, String paramString2, Throwable paramThrowable)
  {
    log(6, paramString1, paramString2, paramThrowable);
  }
  
  static void logI(String paramString1, String paramString2)
  {
    logI(paramString1, paramString2, null);
  }
  
  static void logI(String paramString1, String paramString2, Throwable paramThrowable)
  {
    log(4, paramString1, paramString2, paramThrowable);
  }
  
  static void logV(String paramString1, String paramString2)
  {
    logV(paramString1, paramString2, null);
  }
  
  static void logV(String paramString1, String paramString2, Throwable paramThrowable)
  {
    log(2, paramString1, paramString2, paramThrowable);
  }
  
  static void logW(String paramString1, String paramString2)
  {
    logW(paramString1, paramString2, null);
  }
  
  static void logW(String paramString1, String paramString2, Throwable paramThrowable)
  {
    log(5, paramString1, paramString2, paramThrowable);
  }
  
  static Object maybeEncodeJSONObject(Object paramObject, boolean paramBoolean)
  {
    if ((paramObject instanceof List)) {
      paramObject = encodeAsJSONArray((List)paramObject, paramBoolean);
    }
    JSONObject localJSONObject;
    do
    {
      return paramObject;
      if ((paramObject instanceof ParseFieldOperation)) {
        try
        {
          Object localObject = ((ParseFieldOperation)paramObject).encode();
          return localObject;
        }
        catch (JSONException localJSONException)
        {
          throw new RuntimeException(localJSONException);
        }
      }
      localJSONObject = encodeJSONObject(paramObject, paramBoolean);
    } while (localJSONObject == null);
    return localJSONObject;
  }
  
  static Object maybeReferenceAndEncode(Object paramObject)
  {
    if (((paramObject instanceof ParseObject)) && (((ParseObject)paramObject).getObjectId() == null)) {
      throw new IllegalStateException("unable to encode an association with an unsaved ParseObject");
    }
    return maybeEncodeJSONObject(paramObject, true);
  }
  
  static Date parseDate(String paramString)
  {
    try
    {
      Date localDate2 = dateFormat.parse(paramString);
      localDate1 = localDate2;
    }
    catch (ParseException localParseException)
    {
      for (;;)
      {
        logE("com.parse.Parse", "could not parse date: " + paramString, localParseException);
        Date localDate1 = null;
      }
    }
    finally {}
    return localDate1;
  }
  
  static JSONObject parseObjectToJSONPointer(ParseObject paramParseObject)
  {
    JSONObject localJSONObject = new JSONObject();
    try
    {
      if (paramParseObject.getObjectId() != null)
      {
        localJSONObject.put("__type", "Pointer");
        localJSONObject.put("className", paramParseObject.getClassName());
        localJSONObject.put("objectId", paramParseObject.getObjectId());
        return localJSONObject;
      }
      localJSONObject.put("__type", "Pointer");
      localJSONObject.put("className", paramParseObject.getClassName());
      localJSONObject.put("localId", paramParseObject.getOrCreateLocalId());
      return localJSONObject;
    }
    catch (JSONException localJSONException)
    {
      throw new RuntimeException(localJSONException);
    }
  }
  
  static void recursiveDelete(File paramFile)
  {
    try
    {
      if (paramFile.isDirectory())
      {
        File[] arrayOfFile = paramFile.listFiles();
        int i = arrayOfFile.length;
        for (int j = 0; j < i; j++) {
          recursiveDelete(arrayOfFile[j]);
        }
      }
      paramFile.delete();
      return;
    }
    finally {}
  }
  
  static void requirePermission(String paramString)
  {
    if (!hasPermission(paramString)) {
      throw new IllegalStateException("To use this functionality, add this to your AndroidManifest.xml:\n<uses-permission android:name=\"" + paramString + "\" />");
    }
  }
  
  static void saveToKeyValueCache(String paramString1, String paramString2)
  {
    File localFile1 = getKeyValueCacheFile(paramString1);
    if (localFile1 != null) {
      localFile1.delete();
    }
    File localFile2 = createKeyValueCacheFile(paramString1);
    try
    {
      FileOutputStream localFileOutputStream = new FileOutputStream(localFile2);
      localFileOutputStream.write(paramString2.getBytes("UTF-8"));
      localFileOutputStream.close();
      label45:
      File[] arrayOfFile = getKeyValueCacheDir().listFiles();
      int i = arrayOfFile.length;
      int j = 0;
      int k = arrayOfFile.length;
      for (int m = 0; m < k; m++)
      {
        File localFile4 = arrayOfFile[m];
        j = (int)(j + localFile4.length());
      }
      int n;
      if ((i > maxKeyValueCacheFiles) || (j > maxKeyValueCacheBytes))
      {
        Arrays.sort(arrayOfFile, new Comparator()
        {
          public int compare(File paramAnonymousFile1, File paramAnonymousFile2)
          {
            int i = Long.valueOf(paramAnonymousFile1.lastModified()).compareTo(Long.valueOf(paramAnonymousFile2.lastModified()));
            if (i != 0) {
              return i;
            }
            return paramAnonymousFile1.getName().compareTo(paramAnonymousFile2.getName());
          }
        });
        n = arrayOfFile.length;
      }
      for (int i1 = 0;; i1++) {
        if (i1 < n)
        {
          File localFile3 = arrayOfFile[i1];
          i--;
          j = (int)(j - localFile3.length());
          localFile3.delete();
          if ((i > maxKeyValueCacheFiles) || (j > maxKeyValueCacheBytes)) {}
        }
        else
        {
          return;
        }
      }
    }
    catch (IOException localIOException)
    {
      break label45;
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      break label45;
    }
  }
  
  static void setContextIfNeeded(Context paramContext)
  {
    if (applicationContext == null) {
      applicationContext = paramContext;
    }
  }
  
  public static void setLogLevel(int paramInt)
  {
    logLevel = paramInt;
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.Parse
 * JD-Core Version:    0.7.0.1
 */