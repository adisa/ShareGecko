package com.parse;

public abstract class SignUpCallback
  extends ParseCallback<Void>
{
  public abstract void done(ParseException paramParseException);
  
  final void internalDone(Void paramVoid, ParseException paramParseException)
  {
    done(paramParseException);
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.SignUpCallback
 * JD-Core Version:    0.7.0.1
 */