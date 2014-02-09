package com.parse;

public abstract class LogInCallback
  extends ParseCallback<ParseUser>
{
  public abstract void done(ParseUser paramParseUser, ParseException paramParseException);
  
  void internalDone(ParseUser paramParseUser, ParseException paramParseException)
  {
    done(paramParseUser, paramParseException);
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.LogInCallback
 * JD-Core Version:    0.7.0.1
 */