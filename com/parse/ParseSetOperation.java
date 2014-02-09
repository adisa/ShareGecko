package com.parse;

class ParseSetOperation
  implements ParseFieldOperation
{
  private Object value;
  
  public ParseSetOperation(Object paramObject)
  {
    this.value = paramObject;
  }
  
  public Object apply(Object paramObject, ParseObject paramParseObject, String paramString)
  {
    return this.value;
  }
  
  public Object encode()
  {
    return Parse.maybeEncodeJSONObject(this.value, true);
  }
  
  public Object getValue()
  {
    return this.value;
  }
  
  public ParseFieldOperation mergeWithPrevious(ParseFieldOperation paramParseFieldOperation)
  {
    return this;
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.ParseSetOperation
 * JD-Core Version:    0.7.0.1
 */