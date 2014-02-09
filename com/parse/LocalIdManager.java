package com.parse;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import org.json.JSONException;
import org.json.JSONObject;

class LocalIdManager
{
  private static LocalIdManager defaultInstance;
  private File diskPath = new File(Parse.getParseDir(), "LocalId");
  private Random random;
  
  private LocalIdManager()
  {
    this.diskPath.mkdirs();
    this.random = new Random();
  }
  
  public static LocalIdManager getDefaultInstance()
  {
    try
    {
      if (defaultInstance == null) {
        defaultInstance = new LocalIdManager();
      }
      LocalIdManager localLocalIdManager = defaultInstance;
      return localLocalIdManager;
    }
    finally {}
  }
  
  private MapEntry getMapEntry(String paramString)
  {
    try
    {
      if (!isLocalId(paramString)) {
        throw new IllegalStateException("Tried to get invalid local id: \"" + paramString + "\".");
      }
    }
    finally {}
    File localFile = new File(this.diskPath, paramString);
    MapEntry localMapEntry;
    if (!localFile.exists()) {
      localMapEntry = new MapEntry(null);
    }
    for (;;)
    {
      return localMapEntry;
      JSONObject localJSONObject = ParseObject.getDiskObject(localFile);
      localMapEntry = new MapEntry(null);
      localMapEntry.retainCount = localJSONObject.optInt("retainCount", 0);
      localMapEntry.objectId = localJSONObject.optString("objectId", null);
    }
  }
  
  private boolean isLocalId(String paramString)
  {
    if (!paramString.startsWith("local_")) {
      return false;
    }
    for (int i = 6;; i++)
    {
      if (i >= paramString.length()) {
        break label58;
      }
      int j = paramString.charAt(i);
      if (((j < 48) || (j > 57)) && ((j < 97) || (j > 102))) {
        break;
      }
    }
    label58:
    return true;
  }
  
  private void putMapEntry(String paramString, MapEntry paramMapEntry)
  {
    try
    {
      if (!isLocalId(paramString)) {
        throw new IllegalStateException("Tried to get invalid local id: \"" + paramString + "\".");
      }
    }
    finally {}
    JSONObject localJSONObject = new JSONObject();
    try
    {
      localJSONObject.put("retainCount", paramMapEntry.retainCount);
      if (paramMapEntry.objectId != null) {
        localJSONObject.put("objectId", paramMapEntry.objectId);
      }
      File localFile = new File(this.diskPath, paramString);
      if (!this.diskPath.exists()) {
        this.diskPath.mkdirs();
      }
      ParseObject.saveDiskObject(localFile, localJSONObject);
      return;
    }
    catch (JSONException localJSONException)
    {
      throw new IllegalStateException("Error creating local id map entry.", localJSONException);
    }
  }
  
  private void removeMapEntry(String paramString)
  {
    try
    {
      if (!isLocalId(paramString)) {
        throw new IllegalStateException("Tried to get invalid local id: \"" + paramString + "\".");
      }
    }
    finally {}
    new File(this.diskPath, paramString).delete();
  }
  
  boolean clear()
    throws IOException
  {
    for (;;)
    {
      int k;
      try
      {
        String[] arrayOfString = this.diskPath.list();
        bool = false;
        if (arrayOfString == null) {
          return bool;
        }
        int i = arrayOfString.length;
        bool = false;
        if (i == 0) {
          continue;
        }
        int j = arrayOfString.length;
        k = 0;
        if (k >= j) {
          break label114;
        }
        String str = arrayOfString[k];
        if (!new File(this.diskPath, str).delete()) {
          throw new IOException("Unable to delete file " + str + " in localId cache.");
        }
      }
      finally {}
      k++;
      continue;
      label114:
      boolean bool = true;
    }
  }
  
  String createLocalId()
  {
    String str;
    try
    {
      long l = this.random.nextLong();
      str = "local_" + Long.toHexString(l);
      if (!isLocalId(str)) {
        throw new IllegalStateException("Generated an invalid local id: \"" + str + "\". " + "This should never happen. Please email feedback@parse.com");
      }
    }
    finally {}
    return str;
  }
  
  String getObjectId(String paramString)
  {
    try
    {
      String str = getMapEntry(paramString).objectId;
      return str;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  /* Error */
  void releaseLocalIdOnDisk(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: aload_1
    //   4: invokespecial 176	com/parse/LocalIdManager:getMapEntry	(Ljava/lang/String;)Lcom/parse/LocalIdManager$MapEntry;
    //   7: astore_3
    //   8: aload_3
    //   9: iconst_m1
    //   10: aload_3
    //   11: getfield 94	com/parse/LocalIdManager$MapEntry:retainCount	I
    //   14: iadd
    //   15: putfield 94	com/parse/LocalIdManager$MapEntry:retainCount	I
    //   18: aload_3
    //   19: getfield 94	com/parse/LocalIdManager$MapEntry:retainCount	I
    //   22: ifle +12 -> 34
    //   25: aload_0
    //   26: aload_1
    //   27: aload_3
    //   28: invokespecial 179	com/parse/LocalIdManager:putMapEntry	(Ljava/lang/String;Lcom/parse/LocalIdManager$MapEntry;)V
    //   31: aload_0
    //   32: monitorexit
    //   33: return
    //   34: aload_0
    //   35: aload_1
    //   36: invokespecial 181	com/parse/LocalIdManager:removeMapEntry	(Ljava/lang/String;)V
    //   39: goto -8 -> 31
    //   42: astore_2
    //   43: aload_0
    //   44: monitorexit
    //   45: aload_2
    //   46: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	47	0	this	LocalIdManager
    //   0	47	1	paramString	String
    //   42	4	2	localObject	Object
    //   7	21	3	localMapEntry	MapEntry
    // Exception table:
    //   from	to	target	type
    //   2	31	42	finally
    //   34	39	42	finally
  }
  
  void retainLocalIdOnDisk(String paramString)
  {
    try
    {
      MapEntry localMapEntry = getMapEntry(paramString);
      localMapEntry.retainCount = (1 + localMapEntry.retainCount);
      putMapEntry(paramString, localMapEntry);
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  void setObjectId(String paramString1, String paramString2)
  {
    MapEntry localMapEntry;
    try
    {
      localMapEntry = getMapEntry(paramString1);
      if (localMapEntry.retainCount <= 0) {
        break label53;
      }
      if (localMapEntry.objectId != null) {
        throw new IllegalStateException("Tried to set an objectId for a localId that already has one.");
      }
    }
    finally {}
    localMapEntry.objectId = paramString2;
    putMapEntry(paramString1, localMapEntry);
    label53:
  }
  
  private class MapEntry
  {
    String objectId;
    int retainCount;
    
    private MapEntry() {}
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.LocalIdManager
 * JD-Core Version:    0.7.0.1
 */