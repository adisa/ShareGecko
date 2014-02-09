package com.parse.codec;

public abstract interface StringEncoder
  extends Encoder
{
  public abstract String encode(String paramString)
    throws EncoderException;
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.codec.StringEncoder
 * JD-Core Version:    0.7.0.1
 */