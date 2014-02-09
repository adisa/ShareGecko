package com.parse;

import org.json.JSONException;

abstract interface ParseFieldOperation
{
  public abstract Object apply(Object paramObject, ParseObject paramParseObject, String paramString);
  
  public abstract Object encode()
    throws JSONException;
  
  public abstract ParseFieldOperation mergeWithPrevious(ParseFieldOperation paramParseFieldOperation);
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.ParseFieldOperation
 * JD-Core Version:    0.7.0.1
 */