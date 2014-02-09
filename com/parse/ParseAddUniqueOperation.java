package com.parse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class ParseAddUniqueOperation
  implements ParseFieldOperation
{
  protected LinkedHashSet<Object> objects = new LinkedHashSet();
  
  public ParseAddUniqueOperation(Object paramObject)
  {
    this.objects.add(paramObject);
  }
  
  public ParseAddUniqueOperation(Collection<?> paramCollection)
  {
    this.objects.addAll(paramCollection);
  }
  
  public Object apply(Object paramObject, ParseObject paramParseObject, String paramString)
  {
    if (paramObject == null) {
      return new ArrayList(this.objects);
    }
    if ((paramObject instanceof JSONArray)) {
      return new JSONArray((ArrayList)apply(ParseFieldOperations.jsonArrayAsArrayList((JSONArray)paramObject), paramParseObject, paramString));
    }
    if ((paramObject instanceof List))
    {
      ArrayList localArrayList1 = new ArrayList(this.objects);
      localArrayList1.removeAll((List)paramObject);
      ArrayList localArrayList2 = new ArrayList((List)paramObject);
      localArrayList2.addAll(localArrayList1);
      return localArrayList2;
    }
    throw new IllegalArgumentException("Operation is invalid after previous operation.");
  }
  
  public JSONObject encode()
    throws JSONException
  {
    JSONObject localJSONObject = new JSONObject();
    localJSONObject.put("__op", "AddUnique");
    localJSONObject.put("objects", Parse.maybeReferenceAndEncode(new ArrayList(this.objects)));
    return localJSONObject;
  }
  
  public ParseFieldOperation mergeWithPrevious(ParseFieldOperation paramParseFieldOperation)
  {
    if (paramParseFieldOperation == null) {
      return this;
    }
    if ((paramParseFieldOperation instanceof ParseDeleteOperation)) {
      return new ParseSetOperation(this.objects);
    }
    if ((paramParseFieldOperation instanceof ParseSetOperation))
    {
      Object localObject = ((ParseSetOperation)paramParseFieldOperation).getValue();
      if ((localObject instanceof JSONArray))
      {
        ArrayList localArrayList2 = new ArrayList(this.objects);
        localArrayList2.removeAll((List)localObject);
        ArrayList localArrayList3 = ParseFieldOperations.jsonArrayAsArrayList((JSONArray)localObject);
        localArrayList3.addAll(localArrayList2);
        return new ParseSetOperation(new JSONArray(localArrayList3));
      }
      if ((localObject instanceof List))
      {
        ArrayList localArrayList4 = new ArrayList(this.objects);
        localArrayList4.removeAll((List)localObject);
        ArrayList localArrayList5 = new ArrayList((List)localObject);
        localArrayList5.addAll(localArrayList4);
        return new ParseSetOperation(localArrayList5);
      }
      throw new IllegalArgumentException("You can only add an item to a List or JSONArray.");
    }
    if ((paramParseFieldOperation instanceof ParseAddUniqueOperation))
    {
      ArrayList localArrayList1 = new ArrayList(((ParseAddUniqueOperation)paramParseFieldOperation).objects);
      localArrayList1.addAll(this.objects);
      return new ParseAddUniqueOperation(localArrayList1);
    }
    throw new IllegalArgumentException("Operation is invalid after previous operation.");
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.ParseAddUniqueOperation
 * JD-Core Version:    0.7.0.1
 */