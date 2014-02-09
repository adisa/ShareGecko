package com.parse.codec.binary;

import com.parse.codec.BinaryDecoder;
import com.parse.codec.BinaryEncoder;
import com.parse.codec.DecoderException;
import java.io.UnsupportedEncodingException;

public class Hex
  implements BinaryEncoder, BinaryDecoder
{
  public static final String DEFAULT_CHARSET_NAME = "UTF-8";
  private static final char[] DIGITS_LOWER = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102 };
  private static final char[] DIGITS_UPPER = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70 };
  private final String charsetName;
  
  public Hex()
  {
    this.charsetName = "UTF-8";
  }
  
  public Hex(String paramString)
  {
    this.charsetName = paramString;
  }
  
  public static byte[] decodeHex(char[] paramArrayOfChar)
    throws DecoderException
  {
    int i = paramArrayOfChar.length;
    if ((i & 0x1) != 0) {
      throw new DecoderException("Odd number of characters.");
    }
    byte[] arrayOfByte = new byte[i >> 1];
    int j = 0;
    int k = 0;
    while (k < i)
    {
      int m = toDigit(paramArrayOfChar[k], k) << 4;
      int n = k + 1;
      int i1 = m | toDigit(paramArrayOfChar[n], n);
      k = n + 1;
      arrayOfByte[j] = ((byte)(i1 & 0xFF));
      j++;
    }
    return arrayOfByte;
  }
  
  public static char[] encodeHex(byte[] paramArrayOfByte)
  {
    return encodeHex(paramArrayOfByte, true);
  }
  
  public static char[] encodeHex(byte[] paramArrayOfByte, boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (char[] arrayOfChar = DIGITS_LOWER;; arrayOfChar = DIGITS_UPPER) {
      return encodeHex(paramArrayOfByte, arrayOfChar);
    }
  }
  
  protected static char[] encodeHex(byte[] paramArrayOfByte, char[] paramArrayOfChar)
  {
    int i = paramArrayOfByte.length;
    char[] arrayOfChar = new char[i << 1];
    int j = 0;
    int k = 0;
    while (j < i)
    {
      int m = k + 1;
      arrayOfChar[k] = paramArrayOfChar[((0xF0 & paramArrayOfByte[j]) >>> 4)];
      k = m + 1;
      arrayOfChar[m] = paramArrayOfChar[(0xF & paramArrayOfByte[j])];
      j++;
    }
    return arrayOfChar;
  }
  
  public static String encodeHexString(byte[] paramArrayOfByte)
  {
    return new String(encodeHex(paramArrayOfByte));
  }
  
  protected static int toDigit(char paramChar, int paramInt)
    throws DecoderException
  {
    int i = Character.digit(paramChar, 16);
    if (i == -1) {
      throw new DecoderException("Illegal hexadecimal character " + paramChar + " at index " + paramInt);
    }
    return i;
  }
  
  /* Error */
  public Object decode(Object paramObject)
    throws DecoderException
  {
    // Byte code:
    //   0: aload_1
    //   1: instanceof 74
    //   4: ifeq +18 -> 22
    //   7: aload_1
    //   8: checkcast 74	java/lang/String
    //   11: invokevirtual 113	java/lang/String:toCharArray	()[C
    //   14: astore 4
    //   16: aload 4
    //   18: invokestatic 115	com/parse/codec/binary/Hex:decodeHex	([C)[B
    //   21: areturn
    //   22: aload_1
    //   23: checkcast 116	[C
    //   26: checkcast 116	[C
    //   29: astore_3
    //   30: aload_3
    //   31: astore 4
    //   33: goto -17 -> 16
    //   36: astore_2
    //   37: new 54	com/parse/codec/DecoderException
    //   40: dup
    //   41: aload_2
    //   42: invokevirtual 119	java/lang/ClassCastException:getMessage	()Ljava/lang/String;
    //   45: aload_2
    //   46: invokespecial 122	com/parse/codec/DecoderException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   49: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	50	0	this	Hex
    //   0	50	1	paramObject	Object
    //   36	10	2	localClassCastException	java.lang.ClassCastException
    //   29	2	3	arrayOfChar	char[]
    //   14	18	4	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	16	36	java/lang/ClassCastException
    //   16	22	36	java/lang/ClassCastException
    //   22	30	36	java/lang/ClassCastException
  }
  
  public byte[] decode(byte[] paramArrayOfByte)
    throws DecoderException
  {
    try
    {
      byte[] arrayOfByte = decodeHex(new String(paramArrayOfByte, getCharsetName()).toCharArray());
      return arrayOfByte;
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new DecoderException(localUnsupportedEncodingException.getMessage(), localUnsupportedEncodingException);
    }
  }
  
  /* Error */
  public Object encode(Object paramObject)
    throws com.parse.codec.EncoderException
  {
    // Byte code:
    //   0: aload_1
    //   1: instanceof 74
    //   4: ifeq +22 -> 26
    //   7: aload_1
    //   8: checkcast 74	java/lang/String
    //   11: aload_0
    //   12: invokevirtual 128	com/parse/codec/binary/Hex:getCharsetName	()Ljava/lang/String;
    //   15: invokevirtual 139	java/lang/String:getBytes	(Ljava/lang/String;)[B
    //   18: astore 5
    //   20: aload 5
    //   22: invokestatic 76	com/parse/codec/binary/Hex:encodeHex	([B)[C
    //   25: areturn
    //   26: aload_1
    //   27: checkcast 141	[B
    //   30: checkcast 141	[B
    //   33: astore 4
    //   35: aload 4
    //   37: astore 5
    //   39: goto -19 -> 20
    //   42: astore_3
    //   43: new 135	com/parse/codec/EncoderException
    //   46: dup
    //   47: aload_3
    //   48: invokevirtual 119	java/lang/ClassCastException:getMessage	()Ljava/lang/String;
    //   51: aload_3
    //   52: invokespecial 142	com/parse/codec/EncoderException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   55: athrow
    //   56: astore_2
    //   57: new 135	com/parse/codec/EncoderException
    //   60: dup
    //   61: aload_2
    //   62: invokevirtual 132	java/io/UnsupportedEncodingException:getMessage	()Ljava/lang/String;
    //   65: aload_2
    //   66: invokespecial 142	com/parse/codec/EncoderException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   69: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	70	0	this	Hex
    //   0	70	1	paramObject	Object
    //   56	10	2	localUnsupportedEncodingException	UnsupportedEncodingException
    //   42	10	3	localClassCastException	java.lang.ClassCastException
    //   33	3	4	arrayOfByte	byte[]
    //   18	20	5	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	20	42	java/lang/ClassCastException
    //   20	26	42	java/lang/ClassCastException
    //   26	35	42	java/lang/ClassCastException
    //   0	20	56	java/io/UnsupportedEncodingException
    //   20	26	56	java/io/UnsupportedEncodingException
    //   26	35	56	java/io/UnsupportedEncodingException
  }
  
  public byte[] encode(byte[] paramArrayOfByte)
  {
    return StringUtils.getBytesUnchecked(encodeHexString(paramArrayOfByte), getCharsetName());
  }
  
  public String getCharsetName()
  {
    return this.charsetName;
  }
  
  public String toString()
  {
    return super.toString() + "[charsetName=" + this.charsetName + "]";
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.codec.binary.Hex
 * JD-Core Version:    0.7.0.1
 */