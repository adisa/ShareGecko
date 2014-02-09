package com.parse.codec.binary;

import com.parse.codec.BinaryDecoder;
import com.parse.codec.BinaryEncoder;
import com.parse.codec.DecoderException;
import com.parse.codec.EncoderException;

public class BinaryCodec
  implements BinaryDecoder, BinaryEncoder
{
  private static final int[] BITS = { 1, 2, 4, 8, 16, 32, 64, 128 };
  private static final int BIT_0 = 1;
  private static final int BIT_1 = 2;
  private static final int BIT_2 = 4;
  private static final int BIT_3 = 8;
  private static final int BIT_4 = 16;
  private static final int BIT_5 = 32;
  private static final int BIT_6 = 64;
  private static final int BIT_7 = 128;
  private static final byte[] EMPTY_BYTE_ARRAY;
  private static final char[] EMPTY_CHAR_ARRAY = new char[0];
  
  static
  {
    EMPTY_BYTE_ARRAY = new byte[0];
  }
  
  public static byte[] fromAscii(byte[] paramArrayOfByte)
  {
    byte[] arrayOfByte;
    if (isEmpty(paramArrayOfByte)) {
      arrayOfByte = EMPTY_BYTE_ARRAY;
    }
    for (;;)
    {
      return arrayOfByte;
      arrayOfByte = new byte[paramArrayOfByte.length >> 3];
      int i = 0;
      for (int j = -1 + paramArrayOfByte.length; i < arrayOfByte.length; j -= 8)
      {
        for (int k = 0; k < BITS.length; k++) {
          if (paramArrayOfByte[(j - k)] == 49) {
            arrayOfByte[i] = ((byte)(arrayOfByte[i] | BITS[k]));
          }
        }
        i++;
      }
    }
  }
  
  public static byte[] fromAscii(char[] paramArrayOfChar)
  {
    byte[] arrayOfByte;
    if ((paramArrayOfChar == null) || (paramArrayOfChar.length == 0)) {
      arrayOfByte = EMPTY_BYTE_ARRAY;
    }
    for (;;)
    {
      return arrayOfByte;
      arrayOfByte = new byte[paramArrayOfChar.length >> 3];
      int i = 0;
      for (int j = -1 + paramArrayOfChar.length; i < arrayOfByte.length; j -= 8)
      {
        for (int k = 0; k < BITS.length; k++) {
          if (paramArrayOfChar[(j - k)] == '1') {
            arrayOfByte[i] = ((byte)(arrayOfByte[i] | BITS[k]));
          }
        }
        i++;
      }
    }
  }
  
  private static boolean isEmpty(byte[] paramArrayOfByte)
  {
    return (paramArrayOfByte == null) || (paramArrayOfByte.length == 0);
  }
  
  public static byte[] toAsciiBytes(byte[] paramArrayOfByte)
  {
    byte[] arrayOfByte;
    if (isEmpty(paramArrayOfByte)) {
      arrayOfByte = EMPTY_BYTE_ARRAY;
    }
    for (;;)
    {
      return arrayOfByte;
      arrayOfByte = new byte[paramArrayOfByte.length << 3];
      int i = 0;
      for (int j = -1 + arrayOfByte.length; i < paramArrayOfByte.length; j -= 8)
      {
        int k = 0;
        if (k < BITS.length)
        {
          if ((paramArrayOfByte[i] & BITS[k]) == 0) {
            arrayOfByte[(j - k)] = 48;
          }
          for (;;)
          {
            k++;
            break;
            arrayOfByte[(j - k)] = 49;
          }
        }
        i++;
      }
    }
  }
  
  public static char[] toAsciiChars(byte[] paramArrayOfByte)
  {
    char[] arrayOfChar;
    if (isEmpty(paramArrayOfByte)) {
      arrayOfChar = EMPTY_CHAR_ARRAY;
    }
    for (;;)
    {
      return arrayOfChar;
      arrayOfChar = new char[paramArrayOfByte.length << 3];
      int i = 0;
      for (int j = -1 + arrayOfChar.length; i < paramArrayOfByte.length; j -= 8)
      {
        int k = 0;
        if (k < BITS.length)
        {
          if ((paramArrayOfByte[i] & BITS[k]) == 0) {
            arrayOfChar[(j - k)] = '0';
          }
          for (;;)
          {
            k++;
            break;
            arrayOfChar[(j - k)] = '1';
          }
        }
        i++;
      }
    }
  }
  
  public static String toAsciiString(byte[] paramArrayOfByte)
  {
    return new String(toAsciiChars(paramArrayOfByte));
  }
  
  public Object decode(Object paramObject)
    throws DecoderException
  {
    if (paramObject == null) {
      return EMPTY_BYTE_ARRAY;
    }
    if ((paramObject instanceof byte[])) {
      return fromAscii((byte[])paramObject);
    }
    if ((paramObject instanceof char[])) {
      return fromAscii((char[])paramObject);
    }
    if ((paramObject instanceof String)) {
      return fromAscii(((String)paramObject).toCharArray());
    }
    throw new DecoderException("argument not a byte array");
  }
  
  public byte[] decode(byte[] paramArrayOfByte)
  {
    return fromAscii(paramArrayOfByte);
  }
  
  public Object encode(Object paramObject)
    throws EncoderException
  {
    if (!(paramObject instanceof byte[])) {
      throw new EncoderException("argument not a byte array");
    }
    return toAsciiChars((byte[])paramObject);
  }
  
  public byte[] encode(byte[] paramArrayOfByte)
  {
    return toAsciiBytes(paramArrayOfByte);
  }
  
  public byte[] toByteArray(String paramString)
  {
    if (paramString == null) {
      return EMPTY_BYTE_ARRAY;
    }
    return fromAscii(paramString.toCharArray());
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.codec.binary.BinaryCodec
 * JD-Core Version:    0.7.0.1
 */