package com.parse.codec.net;

import com.parse.codec.DecoderException;
import com.parse.codec.EncoderException;
import com.parse.codec.StringDecoder;
import com.parse.codec.StringEncoder;
import com.parse.codec.binary.Base64;
import java.io.UnsupportedEncodingException;

public class BCodec
  extends RFC1522Codec
  implements StringEncoder, StringDecoder
{
  private final String charset;
  
  public BCodec()
  {
    this("UTF-8");
  }
  
  public BCodec(String paramString)
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
    throw new DecoderException("Objects of type " + paramObject.getClass().getName() + " cannot be decoded using BCodec");
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
  {
    if (paramArrayOfByte == null) {
      return null;
    }
    return Base64.decodeBase64(paramArrayOfByte);
  }
  
  protected byte[] doEncoding(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      return null;
    }
    return Base64.encodeBase64(paramArrayOfByte);
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
    throw new EncoderException("Objects of type " + paramObject.getClass().getName() + " cannot be encoded using BCodec");
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
    return "B";
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.codec.net.BCodec
 * JD-Core Version:    0.7.0.1
 */