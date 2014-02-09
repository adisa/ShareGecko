package com.parse.signpost.http;

import com.parse.signpost.OAuth;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class HttpParameters
  implements Map<String, SortedSet<String>>, Serializable
{
  private TreeMap<String, SortedSet<String>> wrappedMap = new TreeMap();
  
  public void clear()
  {
    this.wrappedMap.clear();
  }
  
  public boolean containsKey(Object paramObject)
  {
    return this.wrappedMap.containsKey(paramObject);
  }
  
  public boolean containsValue(Object paramObject)
  {
    Iterator localIterator = this.wrappedMap.values().iterator();
    while (localIterator.hasNext()) {
      if (((SortedSet)localIterator.next()).contains(paramObject)) {
        return true;
      }
    }
    return false;
  }
  
  public Set<Map.Entry<String, SortedSet<String>>> entrySet()
  {
    return this.wrappedMap.entrySet();
  }
  
  public SortedSet<String> get(Object paramObject)
  {
    return (SortedSet)this.wrappedMap.get(paramObject);
  }
  
  public String getAsHeaderElement(String paramString)
  {
    String str = getFirst(paramString);
    if (str == null) {
      return null;
    }
    return paramString + "=\"" + str + "\"";
  }
  
  public String getAsQueryString(Object paramObject)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    String str = OAuth.percentEncode((String)paramObject);
    Set localSet = (Set)this.wrappedMap.get(str);
    if (localSet == null) {
      return str + "=";
    }
    Iterator localIterator = localSet.iterator();
    while (localIterator.hasNext())
    {
      localStringBuilder.append(str + "=" + (String)localIterator.next());
      if (localIterator.hasNext()) {
        localStringBuilder.append("&");
      }
    }
    return localStringBuilder.toString();
  }
  
  public String getFirst(Object paramObject)
  {
    return getFirst(paramObject, false);
  }
  
  public String getFirst(Object paramObject, boolean paramBoolean)
  {
    SortedSet localSortedSet = (SortedSet)this.wrappedMap.get(paramObject);
    String str;
    if ((localSortedSet == null) || (localSortedSet.isEmpty())) {
      str = null;
    }
    do
    {
      return str;
      str = (String)localSortedSet.first();
    } while (!paramBoolean);
    return OAuth.percentDecode(str);
  }
  
  public boolean isEmpty()
  {
    return this.wrappedMap.isEmpty();
  }
  
  public Set<String> keySet()
  {
    return this.wrappedMap.keySet();
  }
  
  public String put(String paramString1, String paramString2)
  {
    return put(paramString1, paramString2, false);
  }
  
  public String put(String paramString1, String paramString2, boolean paramBoolean)
  {
    Object localObject = (SortedSet)this.wrappedMap.get(paramString1);
    if (localObject == null)
    {
      localObject = new TreeSet();
      TreeMap localTreeMap = this.wrappedMap;
      if (paramBoolean) {
        paramString1 = OAuth.percentEncode(paramString1);
      }
      localTreeMap.put(paramString1, localObject);
    }
    if (paramString2 != null)
    {
      if (paramBoolean) {
        paramString2 = OAuth.percentEncode(paramString2);
      }
      ((SortedSet)localObject).add(paramString2);
    }
    return paramString2;
  }
  
  public SortedSet<String> put(String paramString, SortedSet<String> paramSortedSet)
  {
    return (SortedSet)this.wrappedMap.put(paramString, paramSortedSet);
  }
  
  public SortedSet<String> put(String paramString, SortedSet<String> paramSortedSet, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      remove(paramString);
      Iterator localIterator = paramSortedSet.iterator();
      while (localIterator.hasNext()) {
        put(paramString, (String)localIterator.next(), true);
      }
      return get(paramString);
    }
    return (SortedSet)this.wrappedMap.put(paramString, paramSortedSet);
  }
  
  public void putAll(Map<? extends String, ? extends SortedSet<String>> paramMap)
  {
    this.wrappedMap.putAll(paramMap);
  }
  
  public void putAll(Map<? extends String, ? extends SortedSet<String>> paramMap, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      Iterator localIterator = paramMap.keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        put(str, (SortedSet)paramMap.get(str), true);
      }
    }
    this.wrappedMap.putAll(paramMap);
  }
  
  public void putAll(String[] paramArrayOfString, boolean paramBoolean)
  {
    for (int i = 0; i < -1 + paramArrayOfString.length; i += 2) {
      put(paramArrayOfString[i], paramArrayOfString[(i + 1)], paramBoolean);
    }
  }
  
  public void putMap(Map<String, List<String>> paramMap)
  {
    Iterator localIterator = paramMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      Object localObject = get(str);
      if (localObject == null)
      {
        localObject = new TreeSet();
        put(str, (SortedSet)localObject);
      }
      ((SortedSet)localObject).addAll((Collection)paramMap.get(str));
    }
  }
  
  public String putNull(String paramString1, String paramString2)
  {
    return put(paramString1, paramString2);
  }
  
  public SortedSet<String> remove(Object paramObject)
  {
    return (SortedSet)this.wrappedMap.remove(paramObject);
  }
  
  public int size()
  {
    int i = 0;
    Iterator localIterator = this.wrappedMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      i += ((SortedSet)this.wrappedMap.get(str)).size();
    }
    return i;
  }
  
  public Collection<SortedSet<String>> values()
  {
    return this.wrappedMap.values();
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.signpost.http.HttpParameters
 * JD-Core Version:    0.7.0.1
 */