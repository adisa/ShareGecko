package com.parse.codec.net;

import com.parse.codec.DecoderException;
import com.parse.codec.EncoderException;
import com.parse.codec.StringDecoder;
import com.parse.codec.StringEncoder;
import java.io.UnsupportedEncodingException;
import java.util.BitSet;

public class QCodec
  extends RFC1522Codec
  implements StringEncoder, StringDecoder
{
  private static final byte BLANK = 32;
  private static final BitSet PRINTABLE_CHARS = new BitSet(256);
  private static final byte UNDERSCORE = 95;
  private final String charset;
  private boolean encodeBlanks = false;
  
  static
  {
    PRINTABLE_CHARS.set(32);
    PRINTABLE_CHARS.set(33);
    PRINTABLE_CHARS.set(34);
    PRINTABLE_CHARS.set(35);
    PRINTABLE_CHARS.set(36);
    PRINTABLE_CHARS.set(37);
    PRINTABLE_CHARS.set(38);
    PRINTABLE_CHARS.set(39);
    PRINTABLE_CHARS.set(40);
    PRINTABLE_CHARS.set(41);
    PRINTABLE_CHARS.set(42);
    PRINTABLE_CHARS.set(43);
    PRINTABLE_CHARS.set(44);
    PRINTABLE_CHARS.set(45);
    PRINTABLE_CHARS.set(46);
    PRINTABLE_CHARS.set(47);
    for (int i = 48; i <= 57; i++) {
      PRINTABLE_CHARS.set(i);
    }
    PRINTABLE_CHARS.set(58);
    PRINTABLE_CHARS.set(59);
    PRINTABLE_CHARS.set(60);
    PRINTABLE_CHARS.set(62);
    PRINTABLE_CHARS.set(64);
    for (int j = 65; j <= 90; j++) {
      PRINTABLE_CHARS.set(j);
    }
    PRINTABLE_CHARS.set(91);
    PRINTABLE_CHARS.set(92);
    PRINTABLE_CHARS.set(93);
    PRINTABLE_CHARS.set(94);
    PRINTABLE_CHARS.set(96);
    for (int k = 97; k <= 122; k++) {
      PRINTABLE_CHARS.set(k);
    }
    PRINTABLE_CHARS.set(123);
    PRINTABLE_CHARS.set(124);
    PRINTABLE_CHARS.set(125);
    PRINTABLE_CHARS.set(126);
  }
  
  public QCodec()
  {
    this("UTF-8");
  }
  
  public QCodec(String paramString)
  {
    this.charset = paramString;
  }
  
  public Object decode(Object paramObject)
    throws DecoderException
  {
    if (paramObject == null) {
      return null;
    }
    if ((paramObject instanceof String)) {
      return decode((String)paramObject);
    }
    throw new DecoderException("Objects of type " + paramObject.getClass().getName() + " cannot be decoded using Q codec");
  }
  
  public String decode(String paramString)
    throws DecoderException
  {
    if (paramString == null) {
      return null;
    }
    try
    {
      String str = decodeText(paramString);
      return str;
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new DecoderException(localUnsupportedEncodingException.getMessage(), localUnsupportedEncodingException);
    }
  }
  
  protected byte[] doDecoding(byte[] paramArrayOfByte)
    throws DecoderException
  {
    if (paramArrayOfByte == null) {
      return null;
    }
    int i = 0;
    int j = paramArrayOfByte.length;
    int k = 0;
    byte[] arrayOfByte;
    int m;
    if (i < j)
    {
      if (paramArrayOfByte[i] == 95) {
        k = 1;
      }
    }
    else
    {
      if (k == 0) {
        break label99;
      }
      arrayOfByte = new byte[paramArrayOfByte.length];
      m = 0;
      label44:
      if (m >= paramArrayOfByte.length) {
        break label93;
      }
      int n = paramArrayOfByte[m];
      if (n == 95) {
        break label83;
      }
      arrayOfByte[m] = n;
    }
    for (;;)
    {
      m++;
      break label44;
      i++;
      break;
      label83:
      arrayOfByte[m] = 32;
    }
    label93:
    return QuotedPrintableCodec.decodeQuotedPrintable(arrayOfByte);
    label99:
    return QuotedPrintableCodec.decodeQuotedPrintable(paramArrayOfByte);
  }
  
  protected byte[] doEncoding(byte[] paramArrayOfByte)
  {
    byte[] arrayOfByte;
    if (paramArrayOfByte == null) {
      arrayOfByte = null;
    }
    for (;;)
    {
      return arrayOfByte;
      arrayOfByte = QuotedPrintableCodec.encodeQuotedPrintable(PRINTABLE_CHARS, paramArrayOfByte);
      if (this.encodeBlanks) {
        for (int i = 0; i < arrayOfByte.length; i++) {
          if (arrayOfByte[i] == 32) {
            arrayOfByte[i] = 95;
          }
        }
      }
    }
  }
  
  public Object encode(Object paramObject)
    throws EncoderException
  {
    if (paramObject == null) {
      return null;
    }
    if ((paramObject instanceof String)) {
      return encode((String)paramObject);
    }
    throw new EncoderException("Objects of type " + paramObject.getClass().getName() + " cannot be encoded using Q codec");
  }
  
  public String encode(String paramString)
    throws EncoderException
  {
    if (paramString == null) {
      return null;
    }
    return encode(paramString, getDefaultCharset());
  }
  
  public String encode(String paramString1, String paramString2)
    throws EncoderException
  {
    if (paramString1 == null) {
      return null;
    }
    try
    {
      String str = encodeText(paramString1, paramString2);
      return str;
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new EncoderException(localUnsupportedEncodingException.getMessage(), localUnsupportedEncodingException);
    }
  }
  
  public String getDefaultCharset()
  {
    return this.charset;
  }
  
  protected String getEncoding()
  {
    return "Q";
  }
  
  public boolean isEncodeBlanks()
  {
    return this.encodeBlanks;
  }
  
  public void setEncodeBlanks(boolean paramBoolean)
  {
    this.encodeBlanks = paramBoolean;
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.codec.net.QCodec
 * JD-Core Version:    0.7.0.1
 */