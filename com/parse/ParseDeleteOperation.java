package com.parse;

import org.json.JSONException;
import org.json.JSONObject;

class ParseDeleteOperation
  implements ParseFieldOperation
{
  private static final ParseDeleteOperation defaultInstance = new ParseDeleteOperation();
  
  public static ParseDeleteOperation getInstance()
  {
    return defaultInstance;
  }
  
  public Object apply(Object paramObject, ParseObject paramParseObject, String paramString)
  {
    return null;
  }
  
  public JSONObject encode()
    throws JSONException
  {
    JSONObject localJSONObject = new JSONObject();
    localJSONObject.put("__op", "Delete");
    return localJSONObject;
  }
  
  public ParseFieldOperation mergeWithPrevious(ParseFieldOperation paramParseFieldOperation)
  {
    return this;
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.ParseDeleteOperation
 * JD-Core Version:    0.7.0.1
 */