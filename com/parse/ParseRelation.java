package com.parse;

import java.util.Collections;
import org.json.JSONException;
import org.json.JSONObject;

public class ParseRelation
{
  private String key;
  private ParseObject parent;
  private String targetClass;
  
  ParseRelation(ParseObject paramParseObject, String paramString)
  {
    this.parent = paramParseObject;
    this.key = paramString;
    this.targetClass = null;
  }
  
  ParseRelation(String paramString)
  {
    this.parent = null;
    this.key = null;
    this.targetClass = paramString;
  }
  
  public void add(ParseObject paramParseObject)
  {
    ParseRelationOperation localParseRelationOperation = new ParseRelationOperation(Collections.singleton(paramParseObject), null);
    this.targetClass = localParseRelationOperation.getTargetClass();
    this.parent.performOperation(this.key, localParseRelationOperation);
  }
  
  JSONObject encodeToJSON()
    throws JSONException
  {
    JSONObject localJSONObject = new JSONObject();
    localJSONObject.put("__type", "Relation");
    localJSONObject.put("className", this.targetClass);
    return localJSONObject;
  }
  
  void ensureParentAndKey(ParseObject paramParseObject, String paramString)
  {
    if (this.parent == null) {
      this.parent = paramParseObject;
    }
    if (this.key == null) {
      this.key = paramString;
    }
    if (this.parent != paramParseObject) {
      throw new IllegalStateException("Internal error. One ParseRelation retrieved from two different ParseObjects.");
    }
    if (!this.key.equals(paramString)) {
      throw new IllegalStateException("Internal error. One ParseRelation retrieved from two different keys.");
    }
  }
  
  public ParseQuery getQuery()
  {
    return new ParseQuery(this.targetClass).whereRelatedTo(this.parent, this.key);
  }
  
  String getTargetClass()
  {
    return this.targetClass;
  }
  
  public void remove(ParseObject paramParseObject)
  {
    ParseRelationOperation localParseRelationOperation = new ParseRelationOperation(null, Collections.singleton(paramParseObject));
    this.targetClass = localParseRelationOperation.getTargetClass();
    this.parent.performOperation(this.key, localParseRelationOperation);
  }
  
  void setTargetClass(String paramString)
  {
    this.targetClass = paramString;
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.ParseRelation
 * JD-Core Version:    0.7.0.1
 */