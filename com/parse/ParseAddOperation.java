package com.parse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class ParseAddOperation
  implements ParseFieldOperation
{
  protected final ArrayList<Object> objects = new ArrayList();
  
  public ParseAddOperation(Object paramObject)
  {
    this.objects.add(paramObject);
  }
  
  public ParseAddOperation(Collection<?> paramCollection)
  {
    this.objects.addAll(paramCollection);
  }
  
  public Object apply(Object paramObject, ParseObject paramParseObject, String paramString)
  {
    if (paramObject == null) {
      return this.objects;
    }
    if ((paramObject instanceof JSONArray)) {
      return new JSONArray((ArrayList)apply(ParseFieldOperations.jsonArrayAsArrayList((JSONArray)paramObject), paramParseObject, paramString));
    }
    if ((paramObject instanceof List))
    {
      ArrayList localArrayList = new ArrayList((List)paramObject);
      localArrayList.addAll(this.objects);
      return localArrayList;
    }
    throw new IllegalArgumentException("Operation is invalid after previous operation.");
  }
  
  public JSONObject encode()
    throws JSONException
  {
    JSONObject localJSONObject = new JSONObject();
    localJSONObject.put("__op", "Add");
    localJSONObject.put("objects", Parse.maybeReferenceAndEncode(this.objects));
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
        ArrayList localArrayList3 = ParseFieldOperations.jsonArrayAsArrayList((JSONArray)localObject);
        localArrayList3.addAll(this.objects);
        return new ParseSetOperation(new JSONArray(localArrayList3));
      }
      if ((localObject instanceof List))
      {
        ArrayList localArrayList2 = new ArrayList((List)localObject);
        localArrayList2.addAll(this.objects);
        return new ParseSetOperation(localArrayList2);
      }
      throw new IllegalArgumentException("You can only add an item to a List or JSONArray.");
    }
    if ((paramParseFieldOperation instanceof ParseAddOperation))
    {
      ArrayList localArrayList1 = new ArrayList(((ParseAddOperation)paramParseFieldOperation).objects);
      localArrayList1.addAll(this.objects);
      return new ParseAddOperation(localArrayList1);
    }
    throw new IllegalArgumentException("Operation is invalid after previous operation.");
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.ParseAddOperation
 * JD-Core Version:    0.7.0.1
 */