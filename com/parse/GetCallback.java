package com.parse;

public abstract class GetCallback
  extends ParseCallback<ParseObject>
{
  public abstract void done(ParseObject paramParseObject, ParseException paramParseException);
  
  final void internalDone(ParseObject paramParseObject, ParseException paramParseException)
  {
    done(paramParseObject, paramParseException);
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.GetCallback
 * JD-Core Version:    0.7.0.1
 */