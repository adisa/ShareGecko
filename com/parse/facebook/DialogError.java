package com.parse.facebook;

public class DialogError
  extends Throwable
{
  private static final long serialVersionUID = 1L;
  private int mErrorCode;
  private String mFailingUrl;
  
  public DialogError(String paramString1, int paramInt, String paramString2)
  {
    super(paramString1);
    this.mErrorCode = paramInt;
    this.mFailingUrl = paramString2;
  }
  
  int getErrorCode()
  {
    return this.mErrorCode;
  }
  
  String getFailingUrl()
  {
    return this.mFailingUrl;
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.facebook.DialogError
 * JD-Core Version:    0.7.0.1
 */