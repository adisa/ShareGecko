package com.parse;

import com.parse.codec.digest.DigestUtils;
import org.json.JSONException;
import org.json.JSONObject;

class ParseJSONCacheItem
{
  private String hashValue;
  private JSONObject json = new JSONObject();
  
  public ParseJSONCacheItem(Object paramObject)
    throws JSONException
  {
    this.json.put("object", Parse.maybeEncodeJSONObject(paramObject, true));
    this.hashValue = DigestUtils.md5Hex(this.json.toString());
  }
  
  public boolean equals(ParseJSONCacheItem paramParseJSONCacheItem)
  {
    return this.hashValue.equals(paramParseJSONCacheItem.getHashValue());
  }
  
  public String getHashValue()
  {
    return this.hashValue;
  }
  
  public Object getJSONObject()
  {
    try
    {
      Object localObject = this.json.get("object");
      return localObject;
    }
    catch (JSONException localJSONException) {}
    return null;
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.ParseJSONCacheItem
 * JD-Core Version:    0.7.0.1
 */