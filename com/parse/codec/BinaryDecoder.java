package com.parse.codec;

public abstract interface BinaryDecoder
  extends Decoder
{
  public abstract byte[] decode(byte[] paramArrayOfByte)
    throws DecoderException;
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.codec.BinaryDecoder
 * JD-Core Version:    0.7.0.1
 */