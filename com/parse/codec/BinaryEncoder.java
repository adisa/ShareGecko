package com.parse.codec;

public abstract interface BinaryEncoder
  extends Encoder
{
  public abstract byte[] encode(byte[] paramArrayOfByte)
    throws EncoderException;
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.codec.BinaryEncoder
 * JD-Core Version:    0.7.0.1
 */