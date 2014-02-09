package com.parse;

import android.content.Context;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SimpleTimeZone;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ParseObject
{
  static final String API_VERSION = "2";
  private static final String TAG = "com.parse.ParseObject";
  static final String VERSION_NAME = "1.1.9";
  private static final DateFormat impreciseDateFormat;
  private static final Map<String, ParseObjectFactory<?>> objectFactories;
  static String server = "https://api.parse.com";
  private String className;
  private Date createdAt;
  private final Map<String, Boolean> dataAvailability;
  boolean dirty;
  private final Map<String, Object> estimatedData;
  private boolean hasBeenFetched;
  private final Map<Object, ParseJSONCacheItem> hashedObjects;
  private Boolean isRunning = Boolean.valueOf(false);
  private String localId;
  private String objectId;
  protected final LinkedList<Map<String, ParseFieldOperation>> operationSetQueue;
  private final ParseMulticastDelegate<ParseObject> saveEvent = new ParseMulticastDelegate();
  private final Map<String, Object> serverData;
  private Date updatedAt;
  
  static
  {
    objectFactories = new HashMap();
    registerFactory("_User", new ParseObjectFactory()
    {
      public Class<? extends ParseUser> getExpectedType()
      {
        return ParseUser.class;
      }
      
      public ParseUser getNew(boolean paramAnonymousBoolean)
      {
        return new ParseUser(paramAnonymousBoolean);
      }
    });
    registerFactory("_Role", new ParseObjectFactory()
    {
      public Class<? extends ParseRole> getExpectedType()
      {
        return ParseRole.class;
      }
      
      public ParseRole getNew(boolean paramAnonymousBoolean)
      {
        return new ParseRole(paramAnonymousBoolean);
      }
    });
    registerFactory("_Installation", new ParseObjectFactory()
    {
      public Class<? extends ParseInstallation> getExpectedType()
      {
        return ParseInstallation.class;
      }
      
      public ParseInstallation getNew(boolean paramAnonymousBoolean)
      {
        return new ParseInstallation(paramAnonymousBoolean);
      }
    });
    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    localSimpleDateFormat.setTimeZone(new SimpleTimeZone(0, "GMT"));
    impreciseDateFormat = localSimpleDateFormat;
  }
  
  public ParseObject(String paramString)
  {
    this(paramString, false);
  }
  
  ParseObject(String paramString, boolean paramBoolean)
  {
    if ((getClass().equals(ParseObject.class)) && (objectFactories.containsKey(paramString)) && (!((ParseObjectFactory)objectFactories.get(paramString)).getExpectedType().isInstance(this))) {
      throw new IllegalArgumentException("You must create this type of ParseObject using ParseObject.create()");
    }
    this.localId = null;
    this.serverData = new HashMap();
    this.operationSetQueue = new LinkedList();
    this.operationSetQueue.add(new HashMap());
    this.estimatedData = new HashMap();
    this.hashedObjects = new HashMap();
    this.dataAvailability = new HashMap();
    this.className = paramString;
    if (!paramBoolean)
    {
      if ((!(this instanceof ParseUser)) && (!(this instanceof ParseInstallation)) && (ParseACL.getDefaultACL() != null)) {
        setACL(ParseACL.getDefaultACL());
      }
      this.hasBeenFetched = true;
      this.dirty = true;
      return;
    }
    this.dirty = false;
    this.hasBeenFetched = false;
  }
  
  private void applyOperations(Map<String, ParseFieldOperation> paramMap, Map<String, Object> paramMap1)
  {
    Iterator localIterator = paramMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      Object localObject = ((ParseFieldOperation)paramMap.get(str)).apply(paramMap1.get(str), this, str);
      if (localObject != null) {
        paramMap1.put(str, localObject);
      } else {
        paramMap1.remove(str);
      }
    }
  }
  
  private boolean canBeSerialized()
  {
    if (!canBeSerializedAsValue(this.estimatedData)) {}
    while ((isDataAvailable("ACL")) && (getACL(false) != null) && (getACL(false).hasUnresolvedUser())) {
      return false;
    }
    return true;
  }
  
  private static boolean canBeSerializedAsValue(Object paramObject)
  {
    boolean bool5;
    boolean bool2;
    if ((paramObject instanceof ParseObject)) {
      if (((ParseObject)paramObject).getObjectId() != null)
      {
        bool5 = true;
        bool2 = bool5;
      }
    }
    boolean bool1;
    do
    {
      for (;;)
      {
        return bool2;
        bool5 = false;
        break;
        if ((paramObject instanceof Map))
        {
          Iterator localIterator2 = ((Map)paramObject).values().iterator();
          do
          {
            if (!localIterator2.hasNext()) {
              break;
            }
          } while (canBeSerializedAsValue(localIterator2.next()));
          return false;
        }
        if ((paramObject instanceof JSONArray))
        {
          JSONArray localJSONArray = (JSONArray)paramObject;
          int i = 0;
          if (i >= localJSONArray.length()) {
            break label244;
          }
          try
          {
            boolean bool4 = canBeSerializedAsValue(localJSONArray.get(i));
            bool2 = false;
            if (bool4) {
              i++;
            }
          }
          catch (JSONException localJSONException2)
          {
            throw new RuntimeException("Unable to find related objects for saving.", localJSONException2);
          }
        }
      }
      if ((paramObject instanceof JSONObject))
      {
        JSONObject localJSONObject = (JSONObject)paramObject;
        Iterator localIterator1 = localJSONObject.keys();
        for (;;)
        {
          if (!localIterator1.hasNext()) {
            break label244;
          }
          try
          {
            boolean bool3 = canBeSerializedAsValue(localJSONObject.get((String)localIterator1.next()));
            if (!bool3) {
              return false;
            }
          }
          catch (JSONException localJSONException1)
          {
            throw new RuntimeException("Unable to find related objects for saving.", localJSONException1);
          }
        }
      }
      if ((!(paramObject instanceof ParseACL)) || (!((ParseACL)paramObject).hasUnresolvedUser())) {
        break label244;
      }
      bool1 = canBeSerializedAsValue(ParseUser.getCurrentUser());
      bool2 = false;
    } while (!bool1);
    label244:
    return true;
  }
  
  private void checkForChangesToMutableContainer(String paramString, Object paramObject)
  {
    if (Parse.isContainerObject(paramObject))
    {
      ParseJSONCacheItem localParseJSONCacheItem1 = (ParseJSONCacheItem)this.hashedObjects.get(paramObject);
      if (localParseJSONCacheItem1 == null) {
        throw new IllegalArgumentException("ParseObject contains container item that isn't cached.");
      }
      try
      {
        ParseJSONCacheItem localParseJSONCacheItem2 = new ParseJSONCacheItem(paramObject);
        if (!localParseJSONCacheItem1.equals(localParseJSONCacheItem2)) {
          performOperation(paramString, new ParseSetOperation(paramObject));
        }
        return;
      }
      catch (JSONException localJSONException)
      {
        throw new RuntimeException(localJSONException);
      }
    }
    this.hashedObjects.remove(paramObject);
  }
  
  private void checkGetAccess(String paramString)
  {
    if (!isDataAvailable(paramString)) {
      throw new IllegalStateException("ParseObject has no data for this key.  Call fetchIfNeeded() to get the data.");
    }
  }
  
  private void checkpointMutableContainer(Object paramObject)
  {
    if (Parse.isContainerObject(paramObject)) {}
    try
    {
      ParseJSONCacheItem localParseJSONCacheItem = new ParseJSONCacheItem(paramObject);
      this.hashedObjects.put(paramObject, localParseJSONCacheItem);
      return;
    }
    catch (JSONException localJSONException)
    {
      throw new RuntimeException(localJSONException);
    }
  }
  
  private static void collectDirtyChildren(Object paramObject, List<ParseObject> paramList, List<ParseFile> paramList1)
  {
    collectDirtyChildren(paramObject, paramList, paramList1, new IdentityHashMap(), new IdentityHashMap());
  }
  
  private static void collectDirtyChildren(Object paramObject, List<ParseObject> paramList, List<ParseFile> paramList1, IdentityHashMap<ParseObject, ParseObject> paramIdentityHashMap1, IdentityHashMap<ParseObject, ParseObject> paramIdentityHashMap2)
  {
    if ((paramObject instanceof List))
    {
      Iterator localIterator3 = ((List)paramObject).iterator();
      while (localIterator3.hasNext()) {
        collectDirtyChildren(localIterator3.next(), paramList, paramList1, paramIdentityHashMap1, paramIdentityHashMap2);
      }
    }
    if ((paramObject instanceof Map))
    {
      Iterator localIterator2 = ((Map)paramObject).values().iterator();
      while (localIterator2.hasNext()) {
        collectDirtyChildren(localIterator2.next(), paramList, paramList1, paramIdentityHashMap1, paramIdentityHashMap2);
      }
    }
    if ((paramObject instanceof JSONArray))
    {
      JSONArray localJSONArray = (JSONArray)paramObject;
      int i = 0;
      for (;;)
      {
        if (i >= localJSONArray.length()) {
          break label266;
        }
        try
        {
          collectDirtyChildren(localJSONArray.get(i), paramList, paramList1, paramIdentityHashMap1, paramIdentityHashMap2);
          i++;
        }
        catch (JSONException localJSONException2)
        {
          RuntimeException localRuntimeException2 = new RuntimeException("Invalid JSONArray on object.", localJSONException2);
          throw localRuntimeException2;
        }
      }
    }
    if ((paramObject instanceof JSONObject))
    {
      JSONObject localJSONObject = (JSONObject)paramObject;
      Iterator localIterator1 = localJSONObject.keys();
      for (;;)
      {
        if (!localIterator1.hasNext()) {
          break label266;
        }
        try
        {
          collectDirtyChildren(localJSONObject.get((String)localIterator1.next()), paramList, paramList1, paramIdentityHashMap1, paramIdentityHashMap2);
        }
        catch (JSONException localJSONException1)
        {
          RuntimeException localRuntimeException1 = new RuntimeException("Invalid JSONDictionary on object.", localJSONException1);
          throw localRuntimeException1;
        }
      }
    }
    if ((paramObject instanceof ParseACL)) {
      if (((ParseACL)paramObject).hasUnresolvedUser()) {
        collectDirtyChildren(ParseUser.getCurrentUser(), paramList, paramList1, paramIdentityHashMap1, paramIdentityHashMap2);
      }
    }
    label266:
    ParseFile localParseFile;
    do
    {
      do
      {
        for (;;)
        {
          return;
          if (!(paramObject instanceof ParseObject)) {
            break;
          }
          ParseObject localParseObject = (ParseObject)paramObject;
          if (localParseObject.getObjectId() != null) {}
          IdentityHashMap localIdentityHashMap2;
          for (Object localObject = new IdentityHashMap(); !paramIdentityHashMap1.containsKey(localParseObject); localObject = localIdentityHashMap2)
          {
            IdentityHashMap localIdentityHashMap1 = new IdentityHashMap(paramIdentityHashMap1);
            localIdentityHashMap1.put(localParseObject, localParseObject);
            collectDirtyChildren(localParseObject.estimatedData, paramList, paramList1, localIdentityHashMap1, (IdentityHashMap)localObject);
            if (localParseObject.isDirty(false)) {
              paramList.add(localParseObject);
            }
            return;
            if (paramIdentityHashMap2.containsKey(localParseObject)) {
              throw new RuntimeException("Found a circular dependency while saving.");
            }
            localIdentityHashMap2 = new IdentityHashMap(paramIdentityHashMap2);
            localIdentityHashMap2.put(localParseObject, localParseObject);
          }
        }
      } while (!(paramObject instanceof ParseFile));
      localParseFile = (ParseFile)paramObject;
    } while (localParseFile.getUrl() != null);
    paramList1.add(localParseFile);
  }
  
  private ParseCommand constructDeleteCommand(boolean paramBoolean)
    throws ParseException
  {
    ParseCommand localParseCommand = new ParseCommand("delete");
    localParseCommand.enableRetrying();
    localParseCommand.put("classname", this.className);
    JSONObject localJSONObject = new JSONObject();
    try
    {
      localJSONObject.put("objectId", this.objectId);
      localParseCommand.put("data", localJSONObject);
      return localParseCommand;
    }
    catch (JSONException localJSONException)
    {
      throw new RuntimeException(localJSONException.getMessage());
    }
  }
  
  public static ParseObject create(String paramString)
  {
    if (objectFactories.containsKey(paramString)) {
      return ((ParseObjectFactory)objectFactories.get(paramString)).getNew(false);
    }
    return new ParseObject(paramString, false);
  }
  
  public static ParseObject createWithoutData(String paramString1, String paramString2)
  {
    if (objectFactories.containsKey(paramString1)) {}
    for (ParseObject localParseObject = ((ParseObjectFactory)objectFactories.get(paramString1)).getNew(true);; localParseObject = new ParseObject(paramString1, true))
    {
      localParseObject.setObjectId(paramString2);
      localParseObject.dirty = false;
      return localParseObject;
    }
  }
  
  private Map<String, ParseFieldOperation> currentOperations()
  {
    return (Map)this.operationSetQueue.getLast();
  }
  
  private static void deepSave(Object paramObject)
    throws ParseException
  {
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    collectDirtyChildren(paramObject, localArrayList1, localArrayList2);
    Iterator localIterator1 = localArrayList2.iterator();
    while (localIterator1.hasNext()) {
      ((ParseFile)localIterator1.next()).save();
    }
    IdentityHashMap localIdentityHashMap = new IdentityHashMap();
    Iterator localIterator2 = localArrayList1.iterator();
    while (localIterator2.hasNext()) {
      localIdentityHashMap.put((ParseObject)localIterator2.next(), Boolean.valueOf(true));
    }
    Set localSet = localIdentityHashMap.keySet();
    Object localObject = new ArrayList(localSet);
    while (((List)localObject).size() > 0)
    {
      ArrayList localArrayList3 = new ArrayList();
      ArrayList localArrayList4 = new ArrayList();
      Iterator localIterator3 = ((List)localObject).iterator();
      while (localIterator3.hasNext())
      {
        ParseObject localParseObject2 = (ParseObject)localIterator3.next();
        if (localParseObject2.canBeSerialized()) {
          localArrayList3.add(localParseObject2);
        } else {
          localArrayList4.add(localParseObject2);
        }
      }
      localObject = localArrayList4;
      if (localArrayList3.size() == 0) {
        throw new RuntimeException("Unable to save a PFObject with a relation to a cycle.");
      }
      if ((ParseUser.getCurrentUser() != null) && (ParseUser.getCurrentUser().isLazy()) && (localArrayList3.contains(ParseUser.getCurrentUser())))
      {
        ParseUser.getCurrentUser().save();
        localArrayList3.remove(ParseUser.getCurrentUser());
        if (localArrayList3.size() == 0) {
          break;
        }
      }
      else
      {
        JSONArray localJSONArray1 = new JSONArray();
        ArrayList localArrayList5 = new ArrayList();
        Iterator localIterator4 = localArrayList3.iterator();
        while (localIterator4.hasNext())
        {
          ParseObject localParseObject1 = (ParseObject)localIterator4.next();
          localParseObject1.validateSave();
          ParseCommand localParseCommand2 = localParseObject1.constructSaveCommand();
          localParseObject1.startSave();
          if (localParseCommand2 != null)
          {
            localJSONArray1.put(localParseCommand2.toJSONObject());
            localArrayList5.add(localParseCommand2.op);
          }
        }
        ParseCommand localParseCommand1 = new ParseCommand("multi");
        localParseCommand1.put("commands", localJSONArray1);
        JSONArray localJSONArray2 = (JSONArray)localParseCommand1.perform();
        int i = 0;
        while (i < localArrayList3.size())
        {
          String str = (String)localArrayList5.get(i);
          try
          {
            JSONObject localJSONObject = localJSONArray2.getJSONObject(i);
            ((ParseObject)localArrayList3.get(i)).handleSaveResult(str, localJSONObject);
            i++;
          }
          catch (JSONException localJSONException)
          {
            throw new RuntimeException(localJSONException.getMessage());
          }
        }
      }
    }
  }
  
  static void deleteDiskObject(Context paramContext, String paramString)
  {
    try
    {
      Parse.setContextIfNeeded(paramContext);
      File localFile = new File(Parse.getParseDir(), paramString);
      if (localFile != null) {
        localFile.delete();
      }
      return;
    }
    finally {}
  }
  
  public static List<ParseObject> fetchAll(List<ParseObject> paramList)
    throws ParseException
  {
    if (paramList.size() == 0) {}
    for (;;)
    {
      return paramList;
      ArrayList localArrayList = new ArrayList();
      String str = ((ParseObject)paramList.get(0)).getClassName();
      for (int i = 0; i < paramList.size(); i++)
      {
        if (!((ParseObject)paramList.get(i)).getClassName().equals(str)) {
          throw new IllegalArgumentException("All objects should have the same class");
        }
        if (((ParseObject)paramList.get(i)).getObjectId() == null) {
          throw new IllegalArgumentException("All objects must exist on the server");
        }
        localArrayList.add(((ParseObject)paramList.get(i)).getObjectId());
      }
      ParseQuery localParseQuery = new ParseQuery(str);
      localParseQuery.whereContainedIn("objectId", localArrayList);
      List localList = localParseQuery.find();
      HashMap localHashMap = new HashMap();
      Iterator localIterator = localList.iterator();
      while (localIterator.hasNext())
      {
        ParseObject localParseObject2 = (ParseObject)localIterator.next();
        localHashMap.put(localParseObject2.getObjectId(), localParseObject2);
      }
      for (int j = 0; j < paramList.size(); j++)
      {
        ParseObject localParseObject1 = (ParseObject)localHashMap.get(((ParseObject)paramList.get(j)).getObjectId());
        if (localParseObject1 == null) {
          throw new RuntimeException("Object id " + ((ParseObject)paramList.get(j)).getObjectId() + " does not exist");
        }
        ((ParseObject)paramList.get(j)).mergeFromObject(localParseObject1);
        ((ParseObject)paramList.get(j)).hasBeenFetched = true;
      }
    }
  }
  
  public static List<ParseObject> fetchAllIfNeeded(List<ParseObject> paramList)
    throws ParseException
  {
    ArrayList localArrayList = new ArrayList();
    String str1 = null;
    Iterator localIterator1 = paramList.iterator();
    while (localIterator1.hasNext())
    {
      ParseObject localParseObject3 = (ParseObject)localIterator1.next();
      if (!localParseObject3.isDataAvailable())
      {
        if ((str1 != null) && (!str1.equals(localParseObject3.getClassName()))) {
          throw new IllegalArgumentException("All objects should have the same class");
        }
        str1 = localParseObject3.getClassName();
        String str2 = localParseObject3.getObjectId();
        if (str2 != null) {
          localArrayList.add(str2);
        }
      }
    }
    if (localArrayList.size() == 0) {
      return paramList;
    }
    ParseQuery localParseQuery = new ParseQuery(str1);
    localParseQuery.whereContainedIn("objectId", localArrayList);
    List localList = localParseQuery.find();
    HashMap localHashMap = new HashMap();
    Iterator localIterator2 = localList.iterator();
    while (localIterator2.hasNext())
    {
      ParseObject localParseObject2 = (ParseObject)localIterator2.next();
      localHashMap.put(localParseObject2.getObjectId(), localParseObject2);
    }
    int i = 0;
    label201:
    if (i < paramList.size()) {
      if (!((ParseObject)paramList.get(i)).isDataAvailable()) {
        break label235;
      }
    }
    for (;;)
    {
      i++;
      break label201;
      break;
      label235:
      ParseObject localParseObject1 = (ParseObject)localHashMap.get(((ParseObject)paramList.get(i)).getObjectId());
      if (localParseObject1 == null) {
        throw new RuntimeException("Object id " + ((ParseObject)paramList.get(i)).getObjectId() + " does not exist");
      }
      ((ParseObject)paramList.get(i)).mergeFromObject(localParseObject1);
      ((ParseObject)paramList.get(i)).hasBeenFetched = true;
    }
  }
  
  public static void fetchAllIfNeededInBackground(final List<ParseObject> paramList, FindCallback paramFindCallback)
  {
    BackgroundTask.executeTask(new BackgroundTask(paramFindCallback)
    {
      public List<ParseObject> run()
        throws ParseException
      {
        return ParseObject.fetchAllIfNeeded(paramList);
      }
    });
  }
  
  public static void fetchAllInBackground(final List<ParseObject> paramList, FindCallback paramFindCallback)
  {
    BackgroundTask.executeTask(new BackgroundTask(paramFindCallback)
    {
      public List<ParseObject> run()
        throws ParseException
      {
        return ParseObject.fetchAll(paramList);
      }
    });
  }
  
  private ParseObject fetchIfNeeded(boolean paramBoolean)
    throws ParseException
  {
    if (isDataAvailable()) {
      return this;
    }
    fetch(paramBoolean);
    return this;
  }
  
  private static void findUnsavedChildren(Object paramObject, List<ParseObject> paramList)
  {
    if ((paramObject instanceof List))
    {
      Iterator localIterator2 = ((List)paramObject).iterator();
      while (localIterator2.hasNext()) {
        findUnsavedChildren(localIterator2.next(), paramList);
      }
    }
    if ((paramObject instanceof Map))
    {
      Iterator localIterator1 = ((Map)paramObject).values().iterator();
      while (localIterator1.hasNext()) {
        findUnsavedChildren(localIterator1.next(), paramList);
      }
    }
    if ((paramObject instanceof ParseObject))
    {
      ParseObject localParseObject = (ParseObject)paramObject;
      if (localParseObject.isDirty())
      {
        localParseObject.checkIfRunning(true);
        paramList.add(localParseObject);
      }
    }
  }
  
  private ParseACL getACL(boolean paramBoolean)
  {
    checkGetAccess("ACL");
    Object localObject = this.estimatedData.get("ACL");
    if (localObject == null) {
      return null;
    }
    if (!(localObject instanceof ParseACL)) {
      throw new RuntimeException("only ACLs can be stored in the ACL key");
    }
    if ((paramBoolean) && (((ParseACL)localObject).isShared()))
    {
      ParseACL localParseACL = ((ParseACL)localObject).copy();
      this.estimatedData.put("ACL", localParseACL);
      addToHashedObjects(localParseACL);
      return localParseACL;
    }
    return (ParseACL)localObject;
  }
  
  static String getApplicationId()
  {
    Parse.checkInit();
    return Parse.applicationId;
  }
  
  static JSONObject getDiskObject(Context paramContext, String paramString)
  {
    try
    {
      Parse.setContextIfNeeded(paramContext);
      JSONObject localJSONObject = getDiskObject(new File(Parse.getParseDir(), paramString));
      return localJSONObject;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  /* Error */
  static JSONObject getDiskObject(File paramFile)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: aload_0
    //   4: invokevirtual 591	java/io/File:exists	()Z
    //   7: istore_2
    //   8: aconst_null
    //   9: astore_3
    //   10: iload_2
    //   11: ifne +8 -> 19
    //   14: ldc 2
    //   16: monitorexit
    //   17: aload_3
    //   18: areturn
    //   19: new 593	java/io/RandomAccessFile
    //   22: dup
    //   23: aload_0
    //   24: ldc_w 595
    //   27: invokespecial 596	java/io/RandomAccessFile:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   30: astore 4
    //   32: aload 4
    //   34: invokevirtual 599	java/io/RandomAccessFile:length	()J
    //   37: l2i
    //   38: newarray byte
    //   40: astore 6
    //   42: aload 4
    //   44: aload 6
    //   46: invokevirtual 603	java/io/RandomAccessFile:readFully	([B)V
    //   49: aload 4
    //   51: invokevirtual 606	java/io/RandomAccessFile:close	()V
    //   54: new 223	java/lang/String
    //   57: dup
    //   58: aload 6
    //   60: ldc_w 608
    //   63: invokespecial 611	java/lang/String:<init>	([BLjava/lang/String;)V
    //   66: astore 7
    //   68: new 613	org/json/JSONTokener
    //   71: dup
    //   72: aload 7
    //   74: invokespecial 614	org/json/JSONTokener:<init>	(Ljava/lang/String;)V
    //   77: astore 8
    //   79: new 284	org/json/JSONObject
    //   82: dup
    //   83: aload 8
    //   85: invokespecial 617	org/json/JSONObject:<init>	(Lorg/json/JSONTokener;)V
    //   88: astore 9
    //   90: aload 9
    //   92: astore_3
    //   93: goto -79 -> 14
    //   96: astore 5
    //   98: aconst_null
    //   99: astore_3
    //   100: goto -86 -> 14
    //   103: astore 10
    //   105: aconst_null
    //   106: astore_3
    //   107: goto -93 -> 14
    //   110: astore_1
    //   111: ldc 2
    //   113: monitorexit
    //   114: aload_1
    //   115: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	116	0	paramFile	File
    //   110	5	1	localObject1	Object
    //   7	4	2	bool	boolean
    //   9	98	3	localObject2	Object
    //   30	20	4	localRandomAccessFile	java.io.RandomAccessFile
    //   96	1	5	localIOException	java.io.IOException
    //   40	19	6	arrayOfByte	byte[]
    //   66	7	7	str	String
    //   77	7	8	localJSONTokener	org.json.JSONTokener
    //   88	3	9	localJSONObject	JSONObject
    //   103	1	10	localJSONException	JSONException
    // Exception table:
    //   from	to	target	type
    //   19	68	96	java/io/IOException
    //   79	90	103	org/json/JSONException
    //   3	8	110	finally
    //   19	68	110	finally
    //   68	79	110	finally
    //   79	90	110	finally
  }
  
  static ParseObject getFromDisk(Context paramContext, String paramString)
  {
    JSONObject localJSONObject = getDiskObject(paramContext, paramString);
    if (localJSONObject == null) {
      return null;
    }
    try
    {
      ParseObject localParseObject = createWithoutData(localJSONObject.getString("classname"), null);
      localParseObject.mergeFromServer(localJSONObject);
      return localParseObject;
    }
    catch (JSONException localJSONException) {}
    return null;
  }
  
  private boolean hasDirtyChildren()
  {
    ArrayList localArrayList = new ArrayList();
    findUnsavedChildren(this.estimatedData, localArrayList);
    return localArrayList.size() > 0;
  }
  
  private static Date impreciseParseDate(String paramString)
  {
    try
    {
      Date localDate2 = impreciseDateFormat.parse(paramString);
      localDate1 = localDate2;
    }
    catch (java.text.ParseException localParseException)
    {
      for (;;)
      {
        Parse.logE("com.parse.ParseObject", "could not parse date: " + paramString, localParseException);
        Date localDate1 = null;
      }
    }
    finally {}
    return localDate1;
  }
  
  private boolean isDataAvailable(String paramString)
  {
    return (isDataAvailable()) || ((this.dataAvailability.containsKey(paramString)) && (((Boolean)this.dataAvailability.get(paramString)).booleanValue()));
  }
  
  private boolean isDirty(boolean paramBoolean)
  {
    checkForChangesToMutableContainers();
    return (this.dirty) || (currentOperations().size() > 0) || ((paramBoolean) && (hasDirtyChildren()));
  }
  
  private void mergeAfterSave(JSONObject paramJSONObject, boolean paramBoolean)
  {
    Map localMap = (Map)this.operationSetQueue.removeFirst();
    if (paramJSONObject == null)
    {
      Iterator localIterator = localMap.keySet().iterator();
      if (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        ParseFieldOperation localParseFieldOperation1 = (ParseFieldOperation)localMap.get(str);
        ParseFieldOperation localParseFieldOperation2 = (ParseFieldOperation)((Map)this.operationSetQueue.getFirst()).get(str);
        if (localParseFieldOperation2 != null) {}
        for (ParseFieldOperation localParseFieldOperation3 = localParseFieldOperation2.mergeWithPrevious(localParseFieldOperation1);; localParseFieldOperation3 = localParseFieldOperation1)
        {
          ((Map)this.operationSetQueue.getFirst()).put(str, localParseFieldOperation3);
          break;
        }
      }
    }
    else
    {
      applyOperations(localMap, this.serverData);
      mergeFromServer(paramJSONObject);
      rebuildEstimatedData();
    }
  }
  
  private void rebuildEstimatedData()
  {
    this.estimatedData.clear();
    this.estimatedData.putAll(this.serverData);
    Iterator localIterator = this.operationSetQueue.iterator();
    while (localIterator.hasNext()) {
      applyOperations((Map)localIterator.next(), this.estimatedData);
    }
  }
  
  private static void registerFactory(String paramString, ParseObjectFactory<?> paramParseObjectFactory)
  {
    objectFactories.put(paramString, paramParseObjectFactory);
  }
  
  public static void saveAll(List<ParseObject> paramList)
    throws ParseException
  {
    deepSave(paramList);
  }
  
  public static void saveAllInBackground(List<ParseObject> paramList)
  {
    saveAllInBackground(paramList, null);
  }
  
  public static void saveAllInBackground(final List<ParseObject> paramList, SaveCallback paramSaveCallback)
  {
    BackgroundTask.executeTask(new BackgroundTask(paramSaveCallback)
    {
      public Void run()
        throws ParseException
      {
        ParseObject.saveAll(paramList);
        return null;
      }
    });
  }
  
  static void saveDiskObject(Context paramContext, String paramString, JSONObject paramJSONObject)
  {
    try
    {
      Parse.setContextIfNeeded(paramContext);
      saveDiskObject(new File(Parse.getParseDir(), paramString), paramJSONObject);
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  /* Error */
  static void saveDiskObject(File paramFile, JSONObject paramJSONObject)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: new 700	java/io/FileOutputStream
    //   6: dup
    //   7: aload_0
    //   8: invokespecial 703	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   11: astore_2
    //   12: aload_2
    //   13: aload_1
    //   14: invokevirtual 704	org/json/JSONObject:toString	()Ljava/lang/String;
    //   17: ldc_w 608
    //   20: invokevirtual 708	java/lang/String:getBytes	(Ljava/lang/String;)[B
    //   23: invokevirtual 711	java/io/FileOutputStream:write	([B)V
    //   26: aload_2
    //   27: invokevirtual 712	java/io/FileOutputStream:close	()V
    //   30: ldc 2
    //   32: monitorexit
    //   33: return
    //   34: astore 5
    //   36: goto -6 -> 30
    //   39: astore 4
    //   41: goto -11 -> 30
    //   44: astore_3
    //   45: ldc 2
    //   47: monitorexit
    //   48: aload_3
    //   49: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	50	0	paramFile	File
    //   0	50	1	paramJSONObject	JSONObject
    //   11	16	2	localFileOutputStream	java.io.FileOutputStream
    //   44	5	3	localObject	Object
    //   39	1	4	localIOException	java.io.IOException
    //   34	1	5	localUnsupportedEncodingException	java.io.UnsupportedEncodingException
    // Exception table:
    //   from	to	target	type
    //   3	30	34	java/io/UnsupportedEncodingException
    //   3	30	39	java/io/IOException
    //   3	30	44	finally
  }
  
  private void setObjectIdInternal(String paramString)
  {
    this.objectId = paramString;
    if (this.localId != null)
    {
      LocalIdManager.getDefaultInstance().setObjectId(this.localId, this.objectId);
      this.localId = null;
    }
  }
  
  public void add(String paramString, Object paramObject)
  {
    addAll(paramString, Arrays.asList(new Object[] { paramObject }));
  }
  
  public void addAll(String paramString, Collection<?> paramCollection)
  {
    checkIfRunning();
    performOperation(paramString, new ParseAddOperation(paramCollection));
  }
  
  public void addAllUnique(String paramString, Collection<?> paramCollection)
  {
    checkIfRunning();
    performOperation(paramString, new ParseAddUniqueOperation(paramCollection));
  }
  
  void addToHashedObjects(Object paramObject)
  {
    try
    {
      this.hashedObjects.put(paramObject, new ParseJSONCacheItem(paramObject));
      return;
    }
    catch (JSONException localJSONException)
    {
      throw new IllegalArgumentException("Couldn't serialize container value to JSON.");
    }
  }
  
  public void addUnique(String paramString, Object paramObject)
  {
    addAllUnique(paramString, Arrays.asList(new Object[] { paramObject }));
  }
  
  protected void checkForChangesToMutableContainers()
  {
    Iterator localIterator = this.estimatedData.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      checkForChangesToMutableContainer(str, this.estimatedData.get(str));
    }
    this.hashedObjects.keySet().retainAll(this.estimatedData.values());
  }
  
  protected void checkIfRunning()
  {
    checkIfRunning(false);
  }
  
  protected void checkIfRunning(boolean paramBoolean)
  {
    synchronized (this.isRunning)
    {
      if (this.isRunning.booleanValue()) {
        throw new RuntimeException("This object has an outstanding network connection. You have to wait until it's done.");
      }
    }
    if (paramBoolean) {
      this.isRunning = Boolean.valueOf(true);
    }
  }
  
  protected void clearChanges()
  {
    currentOperations().clear();
    rebuildEstimatedData();
  }
  
  protected ParseCommand constructSaveCommand()
    throws ParseException
  {
    if (!isDirty()) {
      return null;
    }
    JSONObject localJSONObject = toJSONObjectForSaving();
    if (this.objectId == null) {}
    for (String str = "create";; str = "update")
    {
      ParseCommand localParseCommand = new ParseCommand(str);
      localParseCommand.enableRetrying();
      localParseCommand.put("classname", this.className);
      try
      {
        localParseCommand.put("data", localJSONObject.getJSONObject("data"));
        return localParseCommand;
      }
      catch (JSONException localJSONException)
      {
        throw new RuntimeException("could not decode data");
      }
    }
  }
  
  public boolean containsKey(String paramString)
  {
    return this.estimatedData.containsKey(paramString);
  }
  
  protected void copyChangesFrom(ParseObject paramParseObject)
  {
    Map localMap = (Map)paramParseObject.operationSetQueue.getFirst();
    Iterator localIterator = localMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      performOperation(str, (ParseFieldOperation)localMap.get(str));
    }
  }
  
  public void delete()
    throws ParseException
  {
    delete(true);
  }
  
  protected void delete(boolean paramBoolean)
    throws ParseException
  {
    if (paramBoolean) {
      checkIfRunning(true);
    }
    try
    {
      validateDelete();
      String str = this.objectId;
      if (str == null) {
        return;
      }
      constructDeleteCommand(true).perform();
      this.dirty = true;
      return;
    }
    finally
    {
      finishedRunning();
    }
  }
  
  public void deleteEventually()
  {
    deleteEventually(null);
  }
  
  public void deleteEventually(final DeleteCallback paramDeleteCallback)
  {
    BackgroundTask.executeTask(new BackgroundTask(null)
    {
      public Void run()
        throws ParseException
      {
        Parse.getCommandCache().runEventually(ParseObject.this.constructDeleteCommand(false), paramDeleteCallback, ParseObject.this);
        return null;
      }
    });
    this.dirty = true;
  }
  
  public void deleteInBackground()
  {
    deleteInBackground(null);
  }
  
  public void deleteInBackground(DeleteCallback paramDeleteCallback)
  {
    checkIfRunning(true);
    validateDelete();
    BackgroundTask.executeTask(new BackgroundTask(paramDeleteCallback)
    {
      public Void run()
        throws ParseException
      {
        ParseObject.this.delete(false);
        return null;
      }
    });
  }
  
  public ParseObject fetch()
    throws ParseException
  {
    return fetch(true);
  }
  
  protected ParseObject fetch(boolean paramBoolean)
    throws ParseException
  {
    if (paramBoolean) {
      checkIfRunning(true);
    }
    try
    {
      if (this.objectId == null) {
        throw new IllegalArgumentException("Cannot refresh an object that hasn't been saved to the server.");
      }
    }
    finally
    {
      finishedRunning();
    }
    ParseCommand localParseCommand = new ParseCommand("get");
    localParseCommand.enableRetrying();
    localParseCommand.put("classname", this.className);
    JSONObject localJSONObject = new JSONObject();
    try
    {
      localJSONObject.put("objectId", this.objectId);
      localParseCommand.put("data", localJSONObject);
      handleFetchResult((JSONObject)localParseCommand.perform());
      finishedRunning();
      return this;
    }
    catch (JSONException localJSONException)
    {
      throw new RuntimeException(localJSONException.getMessage());
    }
  }
  
  public ParseObject fetchIfNeeded()
    throws ParseException
  {
    return fetchIfNeeded(true);
  }
  
  public void fetchIfNeededInBackground(GetCallback paramGetCallback)
  {
    if (isDataAvailable()) {
      paramGetCallback.internalDone(this, null);
    }
    checkIfRunning(true);
    BackgroundTask.executeTask(new BackgroundTask(paramGetCallback)
    {
      public ParseObject run()
        throws ParseException
      {
        return ParseObject.this.fetchIfNeeded(false);
      }
    });
  }
  
  public void fetchInBackground(GetCallback paramGetCallback)
  {
    checkIfRunning(true);
    BackgroundTask.executeTask(new BackgroundTask(paramGetCallback)
    {
      public ParseObject run()
        throws ParseException
      {
        ParseObject.this.fetch(false);
        return ParseObject.this;
      }
    });
  }
  
  protected void finishedRunning()
  {
    synchronized (this.isRunning)
    {
      this.isRunning = Boolean.valueOf(false);
      return;
    }
  }
  
  public Object get(String paramString)
  {
    checkGetAccess(paramString);
    Object localObject;
    if (!this.estimatedData.containsKey(paramString)) {
      localObject = null;
    }
    do
    {
      return localObject;
      localObject = this.estimatedData.get(paramString);
      if (((localObject instanceof ParseACL)) && (paramString.equals("ACL")))
      {
        ParseACL localParseACL1 = (ParseACL)localObject;
        if (localParseACL1.isShared())
        {
          ParseACL localParseACL2 = localParseACL1.copy();
          this.estimatedData.put("ACL", localParseACL2);
          addToHashedObjects(localParseACL2);
          return getACL();
        }
      }
    } while (!(localObject instanceof ParseRelation));
    ((ParseRelation)localObject).ensureParentAndKey(this, paramString);
    return localObject;
  }
  
  public ParseACL getACL()
  {
    return getACL(true);
  }
  
  public boolean getBoolean(String paramString)
  {
    checkGetAccess(paramString);
    if (!this.estimatedData.containsKey(paramString)) {}
    Object localObject;
    do
    {
      return false;
      localObject = this.estimatedData.get(paramString);
    } while (!(localObject instanceof Boolean));
    return ((Boolean)localObject).booleanValue();
  }
  
  public byte[] getBytes(String paramString)
  {
    checkGetAccess(paramString);
    if (!this.estimatedData.containsKey(paramString)) {
      return null;
    }
    Object localObject = this.estimatedData.get(paramString);
    if (!(localObject instanceof byte[])) {
      return null;
    }
    return (byte[])localObject;
  }
  
  public String getClassName()
  {
    return this.className;
  }
  
  public Date getCreatedAt()
  {
    return this.createdAt;
  }
  
  public Date getDate(String paramString)
  {
    checkGetAccess(paramString);
    if (!this.estimatedData.containsKey(paramString)) {
      return null;
    }
    Object localObject = this.estimatedData.get(paramString);
    if (!(localObject instanceof Date)) {
      return null;
    }
    return (Date)localObject;
  }
  
  public double getDouble(String paramString)
  {
    Number localNumber = getNumber(paramString);
    if (localNumber == null) {
      return 0.0D;
    }
    return localNumber.doubleValue();
  }
  
  public int getInt(String paramString)
  {
    Number localNumber = getNumber(paramString);
    if (localNumber == null) {
      return 0;
    }
    return localNumber.intValue();
  }
  
  public JSONArray getJSONArray(String paramString)
  {
    checkGetAccess(paramString);
    if (!this.estimatedData.containsKey(paramString)) {
      return null;
    }
    Object localObject = this.estimatedData.get(paramString);
    if ((localObject instanceof List))
    {
      localObject = Parse.encodeAsJSONArray((List)localObject, true);
      put(paramString, localObject);
    }
    if (!(localObject instanceof JSONArray)) {
      return null;
    }
    return (JSONArray)localObject;
  }
  
  public JSONObject getJSONObject(String paramString)
  {
    checkGetAccess(paramString);
    if (!this.estimatedData.containsKey(paramString)) {
      return null;
    }
    Object localObject = this.estimatedData.get(paramString);
    if ((localObject instanceof Map))
    {
      localObject = Parse.encodeJSONObject(localObject, true);
      put(paramString, localObject);
    }
    if (!(localObject instanceof JSONObject)) {
      return null;
    }
    return (JSONObject)localObject;
  }
  
  public <T> List<T> getList(String paramString)
  {
    if (!this.estimatedData.containsKey(paramString)) {}
    Object localObject;
    do
    {
      return null;
      localObject = this.estimatedData.get(paramString);
      if ((localObject instanceof JSONArray))
      {
        localObject = Parse.convertArrayToList((JSONArray)localObject);
        put(paramString, localObject);
      }
    } while (!(localObject instanceof List));
    return (List)localObject;
  }
  
  public long getLong(String paramString)
  {
    Number localNumber = getNumber(paramString);
    if (localNumber == null) {
      return 0L;
    }
    return localNumber.longValue();
  }
  
  public <V> Map<String, V> getMap(String paramString)
  {
    if (!this.estimatedData.containsKey(paramString)) {}
    Object localObject;
    do
    {
      return null;
      localObject = this.estimatedData.get(paramString);
      if ((localObject instanceof JSONObject))
      {
        localObject = Parse.convertJSONObjectToMap((JSONObject)localObject);
        put(paramString, localObject);
      }
    } while (!(localObject instanceof Map));
    return (Map)localObject;
  }
  
  public Number getNumber(String paramString)
  {
    checkGetAccess(paramString);
    if (!this.estimatedData.containsKey(paramString)) {
      return null;
    }
    Object localObject = this.estimatedData.get(paramString);
    if (!(localObject instanceof Number)) {
      return null;
    }
    return (Number)localObject;
  }
  
  public String getObjectId()
  {
    return this.objectId;
  }
  
  public String getOrCreateLocalId()
  {
    try
    {
      if (this.localId != null) {
        break label42;
      }
      if (this.objectId != null) {
        throw new IllegalStateException("Attempted to get a localId for an object with an objectId.");
      }
    }
    finally {}
    this.localId = LocalIdManager.getDefaultInstance().createLocalId();
    label42:
    String str = this.localId;
    return str;
  }
  
  public ParseGeoPoint getParseGeoPoint(String paramString)
  {
    checkGetAccess(paramString);
    if (!this.estimatedData.containsKey(paramString)) {
      return null;
    }
    Object localObject = this.estimatedData.get(paramString);
    if (!(localObject instanceof ParseGeoPoint)) {
      return null;
    }
    return (ParseGeoPoint)localObject;
  }
  
  public ParseObject getParseObject(String paramString)
  {
    Object localObject = get(paramString);
    if (!(localObject instanceof ParseObject)) {
      return null;
    }
    return (ParseObject)localObject;
  }
  
  public ParseUser getParseUser(String paramString)
  {
    Object localObject = get(paramString);
    if (!(localObject instanceof ParseUser)) {
      return null;
    }
    return (ParseUser)localObject;
  }
  
  public ParseRelation getRelation(String paramString)
  {
    ParseRelation localParseRelation = new ParseRelation(this, paramString);
    Object localObject = this.estimatedData.get(paramString);
    if ((localObject instanceof ParseRelation)) {
      localParseRelation.setTargetClass(((ParseRelation)localObject).getTargetClass());
    }
    return localParseRelation;
  }
  
  public String getString(String paramString)
  {
    checkGetAccess(paramString);
    if (!this.estimatedData.containsKey(paramString)) {
      return null;
    }
    Object localObject = this.estimatedData.get(paramString);
    if (!(localObject instanceof String)) {
      return null;
    }
    return (String)localObject;
  }
  
  public Date getUpdatedAt()
  {
    return this.updatedAt;
  }
  
  void handleFetchResult(JSONObject paramJSONObject)
  {
    mergeAfterFetch(paramJSONObject);
    this.hasBeenFetched = true;
  }
  
  protected void handleSaveResult(String paramString, JSONObject paramJSONObject)
  {
    if ((paramString.equals("create")) || (paramString.equals("user_signup"))) {}
    for (boolean bool = true;; bool = false)
    {
      mergeAfterSave(paramJSONObject, bool);
      this.saveEvent.invoke(this, null);
      return;
    }
  }
  
  public boolean has(String paramString)
  {
    return containsKey(paramString);
  }
  
  public boolean hasSameId(ParseObject paramParseObject)
  {
    return (getClassName() != null) && (getObjectId() != null) && (getClassName().equals(paramParseObject.getClassName())) && (getObjectId().equals(paramParseObject.getObjectId()));
  }
  
  public void increment(String paramString)
  {
    increment(paramString, Integer.valueOf(1));
  }
  
  public void increment(String paramString, Number paramNumber)
  {
    checkIfRunning();
    performOperation(paramString, new ParseIncrementOperation(paramNumber));
  }
  
  public boolean isDataAvailable()
  {
    return this.hasBeenFetched;
  }
  
  protected boolean isDirty()
  {
    return isDirty(true);
  }
  
  public Set<String> keySet()
  {
    return Collections.unmodifiableSet(this.estimatedData.keySet());
  }
  
  protected void mergeAfterFetch(JSONObject paramJSONObject)
  {
    mergeFromServer(paramJSONObject);
    this.operationSetQueue.clear();
    this.operationSetQueue.add(new HashMap());
    rebuildEstimatedData();
  }
  
  protected void mergeFromObject(ParseObject paramParseObject)
  {
    this.objectId = paramParseObject.objectId;
    this.createdAt = paramParseObject.createdAt;
    this.updatedAt = paramParseObject.updatedAt;
    this.serverData.clear();
    this.serverData.putAll(paramParseObject.serverData);
    if (this.operationSetQueue.size() != 1) {
      throw new IllegalStateException("Attempt ot mergeFromObject during a save.");
    }
    this.operationSetQueue.clear();
    this.operationSetQueue.add(new HashMap());
    this.dirty = false;
    rebuildEstimatedData();
  }
  
  protected void mergeFromServer(JSONObject paramJSONObject)
  {
    this.dirty = false;
    try
    {
      if ((paramJSONObject.has("id")) && (this.objectId == null))
      {
        setObjectIdInternal(paramJSONObject.getString("id"));
        this.hasBeenFetched = true;
      }
      if (paramJSONObject.has("created_at"))
      {
        String str5 = paramJSONObject.getString("created_at");
        if (str5 != null) {
          this.createdAt = impreciseParseDate(str5);
        }
      }
      if (paramJSONObject.has("updated_at"))
      {
        String str4 = paramJSONObject.getString("updated_at");
        if (str4 != null) {
          this.updatedAt = impreciseParseDate(str4);
        }
      }
      if (paramJSONObject.has("pointers"))
      {
        JSONObject localJSONObject3 = paramJSONObject.getJSONObject("pointers");
        Iterator localIterator2 = localJSONObject3.keys();
        while (localIterator2.hasNext())
        {
          String str3 = (String)localIterator2.next();
          JSONArray localJSONArray = localJSONObject3.getJSONArray(str3);
          this.serverData.put(str3, createWithoutData(localJSONArray.optString(0), localJSONArray.optString(1)));
        }
      }
      if (!paramJSONObject.has("data")) {
        break label605;
      }
    }
    catch (JSONException localJSONException)
    {
      throw new RuntimeException(localJSONException);
    }
    JSONObject localJSONObject1 = paramJSONObject.getJSONObject("data");
    Iterator localIterator1 = localJSONObject1.keys();
    while (localIterator1.hasNext())
    {
      String str1 = (String)localIterator1.next();
      this.dataAvailability.put(str1, Boolean.valueOf(true));
      if (str1.equals("objectId"))
      {
        setObjectIdInternal(localJSONObject1.getString(str1));
        this.hasBeenFetched = true;
      }
      else if (str1.equals("createdAt"))
      {
        this.createdAt = Parse.parseDate(localJSONObject1.getString(str1));
      }
      else if (str1.equals("updatedAt"))
      {
        this.updatedAt = Parse.parseDate(localJSONObject1.getString(str1));
      }
      else if (str1.equals("ACL"))
      {
        ParseACL localParseACL = ParseACL.createACLFromJSONObject(localJSONObject1.getJSONObject(str1));
        this.serverData.put("ACL", localParseACL);
        addToHashedObjects(localParseACL);
      }
      else if ((!str1.equals("__type")) && (!str1.equals("className")))
      {
        Object localObject1 = localJSONObject1.get(str1);
        Object localObject2 = Parse.decodeJSONObject(localObject1);
        if (localObject2 != null)
        {
          if (Parse.isContainerObject(localObject2))
          {
            if ((localObject2 instanceof JSONArray)) {
              localObject2 = Parse.convertArrayToList((JSONArray)localObject2);
            }
            addToHashedObjects(localObject2);
          }
          this.serverData.put(str1, localObject2);
        }
        else
        {
          if (Parse.isContainerObject(localObject1))
          {
            if ((localObject1 instanceof JSONArray)) {
              localObject1 = Parse.convertArrayToList((JSONArray)localObject1);
            }
            if ((localObject1 instanceof JSONObject))
            {
              JSONObject localJSONObject2 = (JSONObject)localObject1;
              if ((localJSONObject2.has("__type")) && (localJSONObject2.getString("__type").equals("Relation")))
              {
                String str2 = localJSONObject2.getString("className");
                localObject1 = new ParseRelation(this, str1);
                ((ParseRelation)localObject1).setTargetClass(str2);
              }
            }
            addToHashedObjects(localObject1);
          }
          this.serverData.put(str1, localObject1);
        }
      }
    }
    label605:
    if ((this.updatedAt == null) && (this.createdAt != null)) {
      this.updatedAt = this.createdAt;
    }
    this.dirty = false;
    rebuildEstimatedData();
  }
  
  void performOperation(String paramString, ParseFieldOperation paramParseFieldOperation)
  {
    Object localObject = paramParseFieldOperation.apply(this.estimatedData.get(paramString), this, paramString);
    if (localObject != null) {
      this.estimatedData.put(paramString, localObject);
    }
    for (;;)
    {
      ParseFieldOperation localParseFieldOperation = paramParseFieldOperation.mergeWithPrevious((ParseFieldOperation)currentOperations().get(paramString));
      currentOperations().put(paramString, localParseFieldOperation);
      checkpointMutableContainer(localObject);
      this.dataAvailability.put(paramString, Boolean.TRUE);
      return;
      this.estimatedData.remove(paramString);
    }
  }
  
  public void put(String paramString, Object paramObject)
  {
    checkIfRunning();
    if (paramString == null) {
      throw new IllegalArgumentException("key may not be null.");
    }
    if (paramObject == null) {
      throw new IllegalArgumentException("value may not be null.");
    }
    if (((paramObject instanceof ParseFile)) && (((ParseFile)paramObject).isDirty())) {
      throw new IllegalArgumentException("ParseFile must be saved before being set on a ParseObject.");
    }
    if (!Parse.isValidType(paramObject)) {
      throw new IllegalArgumentException("invalid type for value: " + paramObject.getClass().toString());
    }
    performOperation(paramString, new ParseSetOperation(paramObject));
    checkpointMutableContainer(paramObject);
  }
  
  public void refresh()
    throws ParseException
  {
    fetch();
  }
  
  public void refreshInBackground(final RefreshCallback paramRefreshCallback)
  {
    fetchInBackground(new GetCallback()
    {
      public void done(ParseObject paramAnonymousParseObject, ParseException paramAnonymousParseException)
      {
        if (paramRefreshCallback != null) {
          paramRefreshCallback.internalDone(paramAnonymousParseObject, paramAnonymousParseException);
        }
      }
    });
  }
  
  void registerSaveListener(GetCallback paramGetCallback)
  {
    this.saveEvent.subscribe(paramGetCallback);
  }
  
  public void remove(String paramString)
  {
    checkIfRunning();
    if (get(paramString) != null) {
      performOperation(paramString, ParseDeleteOperation.getInstance());
    }
  }
  
  public void removeAll(String paramString, Collection<?> paramCollection)
  {
    checkIfRunning();
    performOperation(paramString, new ParseRemoveOperation(paramCollection));
  }
  
  public void save()
    throws ParseException
  {
    save(true);
  }
  
  protected void save(boolean paramBoolean)
    throws ParseException
  {
    if (paramBoolean) {
      checkIfRunning(true);
    }
    try
    {
      validateSave();
      if ((isDataAvailable("ACL")) && (getACL(false) != null) && (getACL(false).hasUnresolvedUser()))
      {
        ParseUser.getCurrentUser().save();
        if (getACL(false).hasUnresolvedUser()) {
          throw new IllegalStateException("ACL has an unresolved ParseUser. Save or sign up before attempting to serialize the ACL.");
        }
      }
    }
    finally
    {
      finishedRunning();
    }
    deepSave(this.estimatedData);
    ParseCommand localParseCommand = constructSaveCommand();
    if (localParseCommand == null)
    {
      finishedRunning();
      return;
    }
    startSave();
    localParseCommand.setInternalCallback(new ParseCommand.InternalCallback()
    {
      public void perform(ParseCommand paramAnonymousParseCommand, Object paramAnonymousObject)
      {
        ParseObject.this.handleSaveResult(paramAnonymousParseCommand.op, (JSONObject)paramAnonymousObject);
      }
    });
    localParseCommand.perform();
    finishedRunning();
  }
  
  public void saveEventually()
  {
    saveEventually(null);
  }
  
  public void saveEventually(SaveCallback paramSaveCallback)
  {
    ArrayList localArrayList = new ArrayList();
    findUnsavedChildren(this.estimatedData, localArrayList);
    Iterator localIterator = localArrayList.iterator();
    while (localIterator.hasNext()) {
      ((ParseObject)localIterator.next()).saveEventually();
    }
    ParseCommandCache localParseCommandCache = Parse.getCommandCache();
    try
    {
      ParseCommand localParseCommand = constructSaveCommand();
      startSave();
      localParseCommand.setInternalCallback(new ParseCommand.InternalCallback()
      {
        public void perform(ParseCommand paramAnonymousParseCommand, Object paramAnonymousObject)
        {
          ParseObject.this.handleSaveResult(paramAnonymousParseCommand.op, (JSONObject)paramAnonymousObject);
        }
      });
      localParseCommandCache.runEventually(localParseCommand, paramSaveCallback, this);
      return;
    }
    catch (ParseException localParseException)
    {
      throw new IllegalStateException("Unable to saveEventually.", localParseException);
    }
  }
  
  public void saveInBackground()
  {
    saveInBackground(null);
  }
  
  public void saveInBackground(SaveCallback paramSaveCallback)
  {
    checkIfRunning(true);
    BackgroundTask.executeTask(new BackgroundTask(paramSaveCallback)
    {
      public Void run()
        throws ParseException
      {
        ParseObject.this.save(false);
        return null;
      }
    });
  }
  
  void saveToDisk(Context paramContext, String paramString)
  {
    if (isDirty()) {
      throw new RuntimeException("Can't serialize a dirty object to disk.");
    }
    saveDiskObject(paramContext, paramString, toJSONObjectForDataFile());
  }
  
  public void setACL(ParseACL paramParseACL)
  {
    put("ACL", paramParseACL);
  }
  
  public void setObjectId(String paramString)
  {
    checkIfRunning();
    this.dirty = true;
    setObjectIdInternal(paramString);
  }
  
  protected void startSave()
  {
    this.operationSetQueue.addLast(new HashMap());
  }
  
  JSONObject toJSONObjectForDataFile()
  {
    checkForChangesToMutableContainers();
    JSONObject localJSONObject1 = new JSONObject();
    JSONObject localJSONObject2 = new JSONObject();
    for (;;)
    {
      try
      {
        Iterator localIterator = this.serverData.keySet().iterator();
        if (!localIterator.hasNext()) {
          break;
        }
        String str = (String)localIterator.next();
        Object localObject = this.serverData.get(str);
        if ((Parse.isContainerObject(localObject)) && (this.hashedObjects.containsKey(localObject))) {
          localJSONObject2.put(str, ((ParseJSONCacheItem)this.hashedObjects.get(localObject)).getJSONObject());
        } else {
          localJSONObject2.put(str, Parse.maybeEncodeJSONObject(localObject, true));
        }
      }
      catch (JSONException localJSONException)
      {
        throw new RuntimeException("could not serialize object to JSON");
      }
    }
    if (this.createdAt != null) {
      localJSONObject2.put("createdAt", Parse.encodeDate(this.createdAt));
    }
    if (this.updatedAt != null) {
      localJSONObject2.put("updatedAt", Parse.encodeDate(this.updatedAt));
    }
    if (this.objectId != null) {
      localJSONObject2.put("objectId", this.objectId);
    }
    localJSONObject1.put("data", localJSONObject2);
    localJSONObject1.put("classname", this.className);
    if (this.operationSetQueue.size() != 1) {
      throw new IllegalArgumentException("Attempt to serialize an object with saves in progress.");
    }
    localJSONObject2.put("operations", Parse.maybeReferenceAndEncode(currentOperations()));
    return localJSONObject1;
  }
  
  protected JSONObject toJSONObjectForSaving()
  {
    checkForChangesToMutableContainers();
    JSONObject localJSONObject1 = new JSONObject();
    JSONObject localJSONObject2 = new JSONObject();
    for (;;)
    {
      try
      {
        Map localMap = currentOperations();
        Iterator localIterator = localMap.keySet().iterator();
        if (!localIterator.hasNext()) {
          break;
        }
        String str = (String)localIterator.next();
        ParseFieldOperation localParseFieldOperation = (ParseFieldOperation)localMap.get(str);
        if ((localParseFieldOperation instanceof ParseSetOperation))
        {
          Object localObject = ((ParseSetOperation)localParseFieldOperation).getValue();
          if ((Parse.isContainerObject(localObject)) && (this.hashedObjects.containsKey(localObject)))
          {
            localJSONObject2.put(str, ((ParseJSONCacheItem)this.hashedObjects.get(localObject)).getJSONObject());
            continue;
          }
        }
        localJSONObject2.put(str, Parse.maybeEncodeJSONObject(localParseFieldOperation, true));
      }
      catch (JSONException localJSONException)
      {
        throw new RuntimeException("could not serialize object to JSON");
      }
    }
    if (this.objectId != null) {
      localJSONObject2.put("objectId", this.objectId);
    }
    localJSONObject1.put("data", localJSONObject2);
    localJSONObject1.put("classname", this.className);
    return localJSONObject1;
  }
  
  void unregisterSaveListener(GetCallback paramGetCallback)
  {
    this.saveEvent.unsubscribe(paramGetCallback);
  }
  
  protected void validateDelete() {}
  
  protected void validateSave() {}
  
  private static abstract interface ParseObjectFactory<T extends ParseObject>
  {
    public abstract Class<? extends T> getExpectedType();
    
    public abstract T getNew(boolean paramBoolean);
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.ParseObject
 * JD-Core Version:    0.7.0.1
 */