package com.parse.codec.language;

import com.parse.codec.EncoderException;
import com.parse.codec.StringEncoder;
import java.util.Locale;

final class SoundexUtils
{
  static String clean(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      return paramString;
    }
    int i = paramString.length();
    char[] arrayOfChar = new char[i];
    int j = 0;
    int k = 0;
    int m;
    if (j < i)
    {
      if (!Character.isLetter(paramString.charAt(j))) {
        break label100;
      }
      m = k + 1;
      arrayOfChar[k] = paramString.charAt(j);
    }
    for (;;)
    {
      j++;
      k = m;
      break;
      if (k == i) {
        return paramString.toUpperCase(Locale.ENGLISH);
      }
      return new String(arrayOfChar, 0, k).toUpperCase(Locale.ENGLISH);
      label100:
      m = k;
    }
  }
  
  static int difference(StringEncoder paramStringEncoder, String paramString1, String paramString2)
    throws EncoderException
  {
    return differenceEncoded(paramStringEncoder.encode(paramString1), paramStringEncoder.encode(paramString2));
  }
  
  static int differenceEncoded(String paramString1, String paramString2)
  {
    int i;
    if ((paramString1 == null) || (paramString2 == null)) {
      i = 0;
    }
    for (;;)
    {
      return i;
      int j = Math.min(paramString1.length(), paramString2.length());
      i = 0;
      for (int k = 0; k < j; k++) {
        if (paramString1.charAt(k) == paramString2.charAt(k)) {
          i++;
        }
      }
    }
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.codec.language.SoundexUtils
 * JD-Core Version:    0.7.0.1
 */