package com.parse.codec.net;

import com.parse.codec.DecoderException;

class Utils
{
  static int digit16(byte paramByte)
    throws DecoderException
  {
    int i = Character.digit((char)paramByte, 16);
    if (i == -1) {
      throw new DecoderException("Invalid URL encoding: not a valid digit (radix 16): " + paramByte);
    }
    return i;
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.codec.net.Utils
 * JD-Core Version:    0.7.0.1
 */