package com.parse;

import org.json.JSONException;
import org.json.JSONObject;

class ParseIncrementOperation
  implements ParseFieldOperation
{
  private Number amount;
  
  public ParseIncrementOperation(Number paramNumber)
  {
    this.amount = paramNumber;
  }
  
  public Object apply(Object paramObject, ParseObject paramParseObject, String paramString)
  {
    if (paramObject == null) {
      return this.amount;
    }
    if ((paramObject instanceof Number)) {
      return Parse.addNumbers((Number)paramObject, this.amount);
    }
    throw new IllegalArgumentException("You cannot increment a non-number.");
  }
  
  public JSONObject encode()
    throws JSONException
  {
    JSONObject localJSONObject = new JSONObject();
    localJSONObject.put("__op", "Increment");
    localJSONObject.put("amount", this.amount);
    return localJSONObject;
  }
  
  public ParseFieldOperation mergeWithPrevious(ParseFieldOperation paramParseFieldOperation)
  {
    if (paramParseFieldOperation == null) {
      return this;
    }
    if ((paramParseFieldOperation instanceof ParseDeleteOperation)) {
      return new ParseSetOperation(this.amount);
    }
    if ((paramParseFieldOperation instanceof ParseSetOperation))
    {
      Object localObject = ((ParseSetOperation)paramParseFieldOperation).getValue();
      if ((localObject instanceof Number)) {
        return new ParseSetOperation(Parse.addNumbers((Number)localObject, this.amount));
      }
      throw new IllegalArgumentException("You cannot increment a non-number.");
    }
    if ((paramParseFieldOperation instanceof ParseIncrementOperation)) {
      return new ParseIncrementOperation(Parse.addNumbers(((ParseIncrementOperation)paramParseFieldOperation).amount, this.amount));
    }
    throw new IllegalArgumentException("Operation is invalid after previous operation.");
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.ParseIncrementOperation
 * JD-Core Version:    0.7.0.1
 */