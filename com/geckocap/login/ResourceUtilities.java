package com.geckocap.login;

import java.util.HashMap;

public class ResourceUtilities
{
  static final int[] badges;
  static final String[] daysOfWeek = { "", "Sun", "Mon", "Tues", "Wed", "Thurs", "Fri", "Sat" };
  static final int[] faceId = { 2131296327, 2131296328, 2131296329, 2131296330, 2131296331, 2131296332, 2131296333 };
  static final HashMap<String, Integer> faces;
  static final String[] monthAbr;
  static final int[] rowId;
  static final int[] rowIdMonth;
  
  static
  {
    badges = new int[] { 2131296364, 2131296365, 2131296367, 2131296366, 2131296368, 2131296369 };
    monthAbr = new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
    faces = new HashMap();
    rowId = new int[] { 2131296414, 2131296415, 2131296416, 2131296417, 2131296418, 2131296419, 2131296420 };
    rowIdMonth = new int[] { 2131296320, 2131296321, 2131296322, 2131296323 };
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.geckocap.login.ResourceUtilities
 * JD-Core Version:    0.7.0.1
 */