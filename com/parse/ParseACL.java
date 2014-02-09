package com.parse;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONObject;

public class ParseACL
{
  private static final String PUBLIC_KEY = "*";
  private static final String UNRESOLVED_KEY = "*unresolved";
  private static ParseACL defaultACL;
  private static boolean defaultACLUsesCurrentUser;
  private static ParseACL defaultACLWithCurrentUser;
  private static WeakReference<ParseUser> lastCurrentUser;
  private JSONObject permissionsById = new JSONObject();
  private boolean shared;
  private ParseUser unresolvedUser;
  
  public ParseACL() {}
  
  public ParseACL(ParseUser paramParseUser)
  {
    this();
    setReadAccess(paramParseUser, true);
    setWriteAccess(paramParseUser, true);
  }
  
  static ParseACL createACLFromJSONObject(JSONObject paramJSONObject)
  {
    localParseACL = new ParseACL();
    Iterator localIterator1 = Parse.keys(paramJSONObject).iterator();
    while (localIterator1.hasNext())
    {
      String str = (String)localIterator1.next();
      try
      {
        JSONObject localJSONObject = paramJSONObject.getJSONObject(str);
        Iterator localIterator2 = Parse.keys(localJSONObject).iterator();
        while (localIterator2.hasNext()) {
          localParseACL.setAccess((String)localIterator2.next(), str, true);
        }
        return localParseACL;
      }
      catch (JSONException localJSONException)
      {
        throw new RuntimeException("could not decode ACL: " + localJSONException.getMessage());
      }
    }
  }
  
  private boolean getAccess(String paramString1, String paramString2)
  {
    try
    {
      JSONObject localJSONObject = this.permissionsById.optJSONObject(paramString2);
      if (localJSONObject == null) {
        return false;
      }
      if (localJSONObject.has(paramString1))
      {
        boolean bool = localJSONObject.getBoolean(paramString1);
        return bool;
      }
    }
    catch (JSONException localJSONException)
    {
      throw new RuntimeException("JSON failure with ACL: " + localJSONException.getMessage());
    }
    return false;
  }
  
  static ParseACL getDefaultACL()
  {
    if (defaultACLUsesCurrentUser)
    {
      if (lastCurrentUser != null) {}
      for (ParseUser localParseUser = (ParseUser)lastCurrentUser.get(); ParseUser.getCurrentUser() == null; localParseUser = null) {
        return defaultACL;
      }
      if (localParseUser != ParseUser.getCurrentUser())
      {
        defaultACLWithCurrentUser = defaultACL.copy();
        defaultACLWithCurrentUser.setShared(true);
        defaultACLWithCurrentUser.setReadAccess(ParseUser.getCurrentUser(), true);
        defaultACLWithCurrentUser.setWriteAccess(ParseUser.getCurrentUser(), true);
        lastCurrentUser = new WeakReference(ParseUser.getCurrentUser());
      }
      return defaultACLWithCurrentUser;
    }
    return defaultACL;
  }
  
  private void prepareUnresolvedUser(ParseUser paramParseUser)
  {
    if (this.unresolvedUser != paramParseUser)
    {
      this.permissionsById.remove("*unresolved");
      this.unresolvedUser = paramParseUser;
      paramParseUser.registerSaveListener(new UserResolutionListener(this));
    }
  }
  
  private void resolveUser(ParseUser paramParseUser)
  {
    if (paramParseUser != this.unresolvedUser) {
      return;
    }
    try
    {
      if (this.permissionsById.has("*unresolved"))
      {
        this.permissionsById.put(paramParseUser.getObjectId(), this.permissionsById.get("*unresolved"));
        this.permissionsById.remove("*unresolved");
      }
      this.unresolvedUser = null;
      return;
    }
    catch (JSONException localJSONException)
    {
      throw new RuntimeException(localJSONException);
    }
  }
  
  private void setAccess(String paramString1, String paramString2, boolean paramBoolean)
  {
    JSONObject localJSONObject;
    try
    {
      localJSONObject = this.permissionsById.optJSONObject(paramString2);
      if (localJSONObject == null)
      {
        if (!paramBoolean) {
          return;
        }
        localJSONObject = new JSONObject();
        this.permissionsById.put(paramString2, localJSONObject);
      }
      if (paramBoolean)
      {
        localJSONObject.put(paramString1, true);
        return;
      }
    }
    catch (JSONException localJSONException)
    {
      throw new RuntimeException("JSON failure with ACL: " + localJSONException.getMessage());
    }
    localJSONObject.remove(paramString1);
    if (localJSONObject.length() == 0) {
      this.permissionsById.remove(paramString2);
    }
  }
  
  public static void setDefaultACL(ParseACL paramParseACL, boolean paramBoolean)
  {
    defaultACLWithCurrentUser = null;
    lastCurrentUser = null;
    if (paramParseACL != null)
    {
      defaultACL = paramParseACL.copy();
      defaultACL.setShared(true);
      defaultACLUsesCurrentUser = paramBoolean;
      return;
    }
    defaultACL = null;
  }
  
  private void setUnresolvedReadAccess(ParseUser paramParseUser, boolean paramBoolean)
  {
    prepareUnresolvedUser(paramParseUser);
    setReadAccess("*unresolved", paramBoolean);
  }
  
  private void setUnresolvedWriteAccess(ParseUser paramParseUser, boolean paramBoolean)
  {
    prepareUnresolvedUser(paramParseUser);
    setWriteAccess("*unresolved", paramBoolean);
  }
  
  private static void validateRoleState(ParseRole paramParseRole)
  {
    if (paramParseRole.getObjectId() == null) {
      throw new IllegalArgumentException("Roles must be saved to the server before they can be used in an ACL.");
    }
  }
  
  ParseACL copy()
  {
    ParseACL localParseACL = new ParseACL();
    try
    {
      localParseACL.permissionsById = new JSONObject(this.permissionsById.toString());
      localParseACL.unresolvedUser = this.unresolvedUser;
      if (this.unresolvedUser != null) {
        this.unresolvedUser.registerSaveListener(new UserResolutionListener(localParseACL));
      }
      return localParseACL;
    }
    catch (JSONException localJSONException)
    {
      throw new RuntimeException(localJSONException);
    }
  }
  
  public boolean getPublicReadAccess()
  {
    return getReadAccess("*");
  }
  
  public boolean getPublicWriteAccess()
  {
    return getWriteAccess("*");
  }
  
  public boolean getReadAccess(ParseUser paramParseUser)
  {
    if (paramParseUser == this.unresolvedUser) {
      return getReadAccess("*unresolved");
    }
    if (paramParseUser.getObjectId() == null) {
      throw new IllegalArgumentException("cannot getReadAccess for a user with null id");
    }
    return getReadAccess(paramParseUser.getObjectId());
  }
  
  public boolean getReadAccess(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("cannot getReadAccess for null userId");
    }
    return getAccess("read", paramString);
  }
  
  public boolean getRoleReadAccess(ParseRole paramParseRole)
  {
    validateRoleState(paramParseRole);
    return getRoleReadAccess(paramParseRole.getName());
  }
  
  public boolean getRoleReadAccess(String paramString)
  {
    return getReadAccess("role:" + paramString);
  }
  
  public boolean getRoleWriteAccess(ParseRole paramParseRole)
  {
    validateRoleState(paramParseRole);
    return getRoleWriteAccess(paramParseRole.getName());
  }
  
  public boolean getRoleWriteAccess(String paramString)
  {
    return getWriteAccess("role:" + paramString);
  }
  
  public boolean getWriteAccess(ParseUser paramParseUser)
  {
    if (paramParseUser == this.unresolvedUser) {
      return getWriteAccess("*unresolved");
    }
    if (paramParseUser.getObjectId() == null) {
      throw new IllegalArgumentException("cannot getWriteAccess for a user with null id");
    }
    return getWriteAccess(paramParseUser.getObjectId());
  }
  
  public boolean getWriteAccess(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("cannot getWriteAccess for null userId");
    }
    return getAccess("write", paramString);
  }
  
  boolean hasUnresolvedUser()
  {
    return this.unresolvedUser != null;
  }
  
  boolean isShared()
  {
    return this.shared;
  }
  
  public void setPublicReadAccess(boolean paramBoolean)
  {
    setReadAccess("*", paramBoolean);
  }
  
  public void setPublicWriteAccess(boolean paramBoolean)
  {
    setWriteAccess("*", paramBoolean);
  }
  
  public void setReadAccess(ParseUser paramParseUser, boolean paramBoolean)
  {
    if (paramParseUser.getObjectId() == null)
    {
      if (paramParseUser.isLazy())
      {
        setUnresolvedReadAccess(paramParseUser, paramBoolean);
        return;
      }
      throw new IllegalArgumentException("cannot setReadAccess for a user with null id");
    }
    setReadAccess(paramParseUser.getObjectId(), paramBoolean);
  }
  
  public void setReadAccess(String paramString, boolean paramBoolean)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("cannot setReadAccess for null userId");
    }
    setAccess("read", paramString, paramBoolean);
  }
  
  public void setRoleReadAccess(ParseRole paramParseRole, boolean paramBoolean)
  {
    validateRoleState(paramParseRole);
    setRoleReadAccess(paramParseRole.getName(), paramBoolean);
  }
  
  public void setRoleReadAccess(String paramString, boolean paramBoolean)
  {
    setReadAccess("role:" + paramString, paramBoolean);
  }
  
  public void setRoleWriteAccess(ParseRole paramParseRole, boolean paramBoolean)
  {
    validateRoleState(paramParseRole);
    setRoleWriteAccess(paramParseRole.getName(), paramBoolean);
  }
  
  public void setRoleWriteAccess(String paramString, boolean paramBoolean)
  {
    setWriteAccess("role:" + paramString, paramBoolean);
  }
  
  void setShared(boolean paramBoolean)
  {
    this.shared = paramBoolean;
  }
  
  public void setWriteAccess(ParseUser paramParseUser, boolean paramBoolean)
  {
    if (paramParseUser.getObjectId() == null)
    {
      if (paramParseUser.isLazy())
      {
        setUnresolvedWriteAccess(paramParseUser, paramBoolean);
        return;
      }
      throw new IllegalArgumentException("cannot setWriteAccess for a user with null id");
    }
    setWriteAccess(paramParseUser.getObjectId(), paramBoolean);
  }
  
  public void setWriteAccess(String paramString, boolean paramBoolean)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("cannot setWriteAccess for null userId");
    }
    setAccess("write", paramString, paramBoolean);
  }
  
  JSONObject toJSONObject()
  {
    return this.permissionsById;
  }
  
  private static class UserResolutionListener
    extends GetCallback
  {
    private final WeakReference<ParseACL> parent;
    
    public UserResolutionListener(ParseACL paramParseACL)
    {
      this.parent = new WeakReference(paramParseACL);
    }
    
    public void done(ParseObject paramParseObject, ParseException paramParseException)
    {
      try
      {
        ParseACL localParseACL = (ParseACL)this.parent.get();
        if (localParseACL != null) {
          localParseACL.resolveUser((ParseUser)paramParseObject);
        }
        return;
      }
      finally
      {
        paramParseObject.unregisterSaveListener(this);
      }
    }
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.ParseACL
 * JD-Core Version:    0.7.0.1
 */