package com.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseRole
  extends ParseObject
{
  static final String CLASS_NAME = "_Role";
  private static final Pattern NAME_PATTERN = Pattern.compile("^[0-9a-zA-Z_\\- ]+$");
  
  public ParseRole(String paramString)
  {
    this(false);
    setName(paramString);
  }
  
  public ParseRole(String paramString, ParseACL paramParseACL)
  {
    this(paramString);
    setACL(paramParseACL);
  }
  
  ParseRole(boolean paramBoolean)
  {
    super("_Role", paramBoolean);
  }
  
  public static ParseQuery getQuery()
  {
    return new ParseQuery("_Role");
  }
  
  public String getName()
  {
    return getString("name");
  }
  
  public ParseRelation getRoles()
  {
    return getRelation("roles");
  }
  
  public ParseRelation getUsers()
  {
    return getRelation("users");
  }
  
  public void put(String paramString, Object paramObject)
  {
    if ("name".equals(paramString))
    {
      if (getObjectId() != null) {
        throw new IllegalArgumentException("A role's name can only be set before it has been saved.");
      }
      if (!(paramObject instanceof String)) {
        throw new IllegalArgumentException("A role's name must be a String.");
      }
      if (!NAME_PATTERN.matcher((String)paramObject).matches()) {
        throw new IllegalArgumentException("A role's name can only contain alphanumeric characters, _, -, and spaces.");
      }
    }
    super.put(paramString, paramObject);
  }
  
  public void setName(String paramString)
  {
    put("name", paramString);
  }
  
  protected void validateSave()
  {
    if ((getObjectId() == null) && (getName() == null)) {
      throw new IllegalStateException("New roles must specify a name.");
    }
    super.validateSave();
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.ParseRole
 * JD-Core Version:    0.7.0.1
 */