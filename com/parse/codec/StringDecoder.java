package com.parse.codec;

public abstract interface StringDecoder
  extends Decoder
{
  public abstract String decode(String paramString)
    throws DecoderException;
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.codec.StringDecoder
 * JD-Core Version:    0.7.0.1
 */