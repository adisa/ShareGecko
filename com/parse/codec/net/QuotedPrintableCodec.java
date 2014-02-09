package com.parse.codec.net;

import com.parse.codec.BinaryDecoder;
import com.parse.codec.BinaryEncoder;
import com.parse.codec.DecoderException;
import com.parse.codec.EncoderException;
import com.parse.codec.StringDecoder;
import com.parse.codec.StringEncoder;
import com.parse.codec.binary.StringUtils;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.BitSet;

public class QuotedPrintableCodec
  implements BinaryEncoder, BinaryDecoder, StringEncoder, StringDecoder
{
  private static final byte ESCAPE_CHAR = 61;
  private static final BitSet PRINTABLE_CHARS = new BitSet(256);
  private static final byte SPACE = 32;
  private static final byte TAB = 9;
  private final String charset;
  
  static
  {
    for (int i = 33; i <= 60; i++) {
      PRINTABLE_CHARS.set(i);
    }
    for (int j = 62; j <= 126; j++) {
      PRINTABLE_CHARS.set(j);
    }
    PRINTABLE_CHARS.set(9);
    PRINTABLE_CHARS.set(32);
  }
  
  public QuotedPrintableCodec()
  {
    this("UTF-8");
  }
  
  public QuotedPrintableCodec(String paramString)
  {
    this.charset = paramString;
  }
  
  public static final byte[] decodeQuotedPrintable(byte[] paramArrayOfByte)
    throws DecoderException
  {
    if (paramArrayOfByte == null) {
      return null;
    }
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    int i = 0;
    if (i < paramArrayOfByte.length)
    {
      int j = paramArrayOfByte[i];
      int k;
      if (j == 61) {
        k = i + 1;
      }
      for (;;)
      {
        try
        {
          int m = Utils.digit16(paramArrayOfByte[k]);
          i = k + 1;
          localByteArrayOutputStream.write((char)(Utils.digit16(paramArrayOfByte[i]) + (m << 4)));
          i++;
        }
        catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
        {
          throw new DecoderException("Invalid quoted-printable encoding", localArrayIndexOutOfBoundsException);
        }
        localByteArrayOutputStream.write(j);
      }
    }
    return localByteArrayOutputStream.toByteArray();
  }
  
  private static final void encodeQuotedPrintable(int paramInt, ByteArrayOutputStream paramByteArrayOutputStream)
  {
    paramByteArrayOutputStream.write(61);
    int i = Character.toUpperCase(Character.forDigit(0xF & paramInt >> 4, 16));
    int j = Character.toUpperCase(Character.forDigit(paramInt & 0xF, 16));
    paramByteArrayOutputStream.write(i);
    paramByteArrayOutputStream.write(j);
  }
  
  public static final byte[] encodeQuotedPrintable(BitSet paramBitSet, byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      return null;
    }
    if (paramBitSet == null) {
      paramBitSet = PRINTABLE_CHARS;
    }
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    int i = 0;
    if (i < paramArrayOfByte.length)
    {
      int j = paramArrayOfByte[i];
      if (j < 0) {
        j += 256;
      }
      if (paramBitSet.get(j)) {
        localByteArrayOutputStream.write(j);
      }
      for (;;)
      {
        i++;
        break;
        encodeQuotedPrintable(j, localByteArrayOutputStream);
      }
    }
    return localByteArrayOutputStream.toByteArray();
  }
  
  public Object decode(Object paramObject)
    throws DecoderException
  {
    if (paramObject == null) {
      return null;
    }
    if ((paramObject instanceof byte[])) {
      return decode((byte[])paramObject);
    }
    if ((paramObject instanceof String)) {
      return decode((String)paramObject);
    }
    throw new DecoderException("Objects of type " + paramObject.getClass().getName() + " cannot be quoted-printable decoded");
  }
  
  public String decode(String paramString)
    throws DecoderException
  {
    if (paramString == null) {
      return null;
    }
    try
    {
      String str = decode(paramString, getDefaultCharset());
      return str;
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new DecoderException(localUnsupportedEncodingException.getMessage(), localUnsupportedEncodingException);
    }
  }
  
  public String decode(String paramString1, String paramString2)
    throws DecoderException, UnsupportedEncodingException
  {
    if (paramString1 == null) {
      return null;
    }
    return new String(decode(StringUtils.getBytesUsAscii(paramString1)), paramString2);
  }
  
  public byte[] decode(byte[] paramArrayOfByte)
    throws DecoderException
  {
    return decodeQuotedPrintable(paramArrayOfByte);
  }
  
  public Object encode(Object paramObject)
    throws EncoderException
  {
    if (paramObject == null) {
      return null;
    }
    if ((paramObject instanceof byte[])) {
      return encode((byte[])paramObject);
    }
    if ((paramObject instanceof String)) {
      return encode((String)paramObject);
    }
    throw new EncoderException("Objects of type " + paramObject.getClass().getName() + " cannot be quoted-printable encoded");
  }
  
  public String encode(String paramString)
    throws EncoderException
  {
    if (paramString == null) {
      return null;
    }
    try
    {
      String str = encode(paramString, getDefaultCharset());
      return str;
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new EncoderException(localUnsupportedEncodingException.getMessage(), localUnsupportedEncodingException);
    }
  }
  
  public String encode(String paramString1, String paramString2)
    throws UnsupportedEncodingException
  {
    if (paramString1 == null) {
      return null;
    }
    return StringUtils.newStringUsAscii(encode(paramString1.getBytes(paramString2)));
  }
  
  public byte[] encode(byte[] paramArrayOfByte)
  {
    return encodeQuotedPrintable(PRINTABLE_CHARS, paramArrayOfByte);
  }
  
  public String getDefaultCharset()
  {
    return this.charset;
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.codec.net.QuotedPrintableCodec
 * JD-Core Version:    0.7.0.1
 */