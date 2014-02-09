package com.parse;

import com.parse.auth.ParseAuthenticationProvider;
import com.parse.auth.ParseAuthenticationProvider.ParseAuthenticationCallback;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ParseUser
  extends ParseObject
{
  static final String CLASS_NAME = "_User";
  private static final String CURRENT_USER_FILENAME = "currentUser";
  private static Map<String, ParseAuthenticationProvider> authenticationProviders = new HashMap();
  private static boolean autoUserEnabled;
  private static ParseUser currentUser;
  private static boolean currentUserMatchesDisk = false;
  private final JSONObject authData = new JSONObject();
  private boolean isCurrentUser = false;
  private boolean isLazy = false;
  private boolean isNew;
  private final Set<String> linkedServiceNames = new HashSet();
  private String password;
  private final Set<String> readOnlyLinkedServiceNames = Collections.unmodifiableSet(this.linkedServiceNames);
  private String sessionToken;
  
  public ParseUser()
  {
    this(false);
  }
  
  ParseUser(boolean paramBoolean)
  {
    super("_User", paramBoolean);
  }
  
  private static void checkApplicationContext()
  {
    if (Parse.applicationContext == null) {
      throw new RuntimeException("You must call Parse.initialize(context, oauthKey, oauthSecret) before using the Parse library.");
    }
  }
  
  static void clearCurrentUserFromMemory()
  {
    currentUser = null;
    currentUserMatchesDisk = false;
  }
  
  private static ParseCommand constructLogInCommand(String paramString1, String paramString2)
    throws ParseException
  {
    ParseCommand localParseCommand = new ParseCommand("user_login");
    localParseCommand.put("username", paramString1);
    localParseCommand.put("user_password", paramString2);
    return localParseCommand;
  }
  
  private static ParseCommand constructPasswordResetCommand(String paramString)
  {
    ParseCommand localParseCommand = new ParseCommand("user_request_password_reset");
    localParseCommand.put("email", paramString);
    return localParseCommand;
  }
  
  private ParseCommand constructSignUpCommand()
    throws ParseException
  {
    ParseCommand localParseCommand = constructSaveCommand();
    localParseCommand.setOp("user_signup");
    return localParseCommand;
  }
  
  private ParseCommand constructSignUpOrLoginCommand()
    throws ParseException
  {
    ParseCommand localParseCommand = new ParseCommand("user_signup_or_login");
    JSONObject localJSONObject = toJSONObjectForSaving();
    Iterator localIterator = localJSONObject.keys();
    for (;;)
    {
      String str;
      if (localIterator.hasNext()) {
        str = (String)localIterator.next();
      }
      try
      {
        Object localObject = localJSONObject.get(str);
        if ((localObject instanceof JSONObject))
        {
          localParseCommand.put(str, (JSONObject)localObject);
          continue;
        }
        if ((localObject instanceof JSONArray))
        {
          localParseCommand.put(str, (JSONArray)localObject);
          continue;
        }
        if ((localObject instanceof String))
        {
          localParseCommand.put(str, (String)localObject);
          continue;
        }
        localParseCommand.put(str, localJSONObject.getInt(str));
      }
      catch (JSONException localJSONException) {}
      if (this.password != null) {
        localParseCommand.put("user_password", this.password);
      }
      return localParseCommand;
    }
  }
  
  static void disableAutomaticUser()
  {
    autoUserEnabled = false;
  }
  
  public static void enableAutomaticUser()
  {
    autoUserEnabled = true;
  }
  
  public static ParseUser getCurrentUser()
  {
    
    if (currentUser != null) {
      return currentUser;
    }
    if (currentUserMatchesDisk)
    {
      if (isAutomaticUserEnabled()) {
        ParseAnonymousUtils.lazyLogIn();
      }
      return currentUser;
    }
    currentUserMatchesDisk = true;
    ParseObject localParseObject = getFromDisk(Parse.applicationContext, "currentUser");
    if (localParseObject == null)
    {
      if (isAutomaticUserEnabled()) {
        ParseAnonymousUtils.lazyLogIn();
      }
      return currentUser;
    }
    currentUser = (ParseUser)localParseObject;
    currentUser.isCurrentUser = true;
    return currentUser;
  }
  
  public static ParseQuery getQuery()
  {
    return new ParseQuery("_User");
  }
  
  static boolean isAutomaticUserEnabled()
  {
    return autoUserEnabled;
  }
  
  private void linkWith(final ParseAuthenticationProvider paramParseAuthenticationProvider, final SaveCallback paramSaveCallback)
  {
    paramParseAuthenticationProvider.authenticate(new ParseAuthenticationProvider.ParseAuthenticationCallback()
    {
      public void onCancel()
      {
        paramSaveCallback.internalDone(null, null);
      }
      
      public void onError(Throwable paramAnonymousThrowable)
      {
        paramSaveCallback.internalDone(null, new ParseException(paramAnonymousThrowable));
      }
      
      public void onSuccess(JSONObject paramAnonymousJSONObject)
      {
        ParseUser.this.linkWith(paramParseAuthenticationProvider.getAuthType(), paramAnonymousJSONObject, paramSaveCallback);
      }
    });
  }
  
  public static ParseUser logIn(String paramString1, String paramString2)
    throws ParseException
  {
    if (paramString1 == null) {
      throw new IllegalArgumentException("Must specify a username for the user to log in with");
    }
    if (paramString2 == null) {
      throw new IllegalArgumentException("Must specify a password for the user to log in with");
    }
    Object localObject = constructLogInCommand(paramString1, paramString2).perform();
    if (localObject == JSONObject.NULL) {
      throw new ParseException(101, "invalid login credentials");
    }
    ParseUser localParseUser = new ParseUser();
    localParseUser.handleFetchResult((JSONObject)localObject);
    saveCurrentUser(localParseUser);
    return localParseUser;
  }
  
  public static void logInInBackground(final String paramString1, final String paramString2, LogInCallback paramLogInCallback)
  {
    BackgroundTask.executeTask(new BackgroundTask(paramLogInCallback)
    {
      public ParseUser run()
        throws ParseException
      {
        return ParseUser.logIn(paramString1, paramString2);
      }
    });
  }
  
  static ParseUser logInLazyUser(String paramString, JSONObject paramJSONObject)
  {
    ParseUser localParseUser = new ParseUser();
    localParseUser.isCurrentUser = true;
    localParseUser.isLazy = true;
    try
    {
      localParseUser.authData.put(paramString, paramJSONObject);
      localParseUser.linkedServiceNames.add(paramString);
      currentUser = localParseUser;
      currentUserMatchesDisk = false;
      return localParseUser;
    }
    catch (JSONException localJSONException)
    {
      throw new RuntimeException(localJSONException);
    }
  }
  
  private static void logInWith(ParseAuthenticationProvider paramParseAuthenticationProvider, final LogInCallback paramLogInCallback)
  {
    paramParseAuthenticationProvider.authenticate(new ParseAuthenticationProvider.ParseAuthenticationCallback()
    {
      public void onCancel()
      {
        paramLogInCallback.internalDone(null, null);
      }
      
      public void onError(Throwable paramAnonymousThrowable)
      {
        paramLogInCallback.internalDone(null, new ParseException(paramAnonymousThrowable));
      }
      
      public void onSuccess(JSONObject paramAnonymousJSONObject)
      {
        ParseUser.logInWith(this.val$authenticator.getAuthType(), paramAnonymousJSONObject, paramLogInCallback);
      }
    });
  }
  
  static void logInWith(String paramString, LogInCallback paramLogInCallback)
  {
    if (!authenticationProviders.containsKey(paramString)) {
      throw new IllegalArgumentException("No authentication provider could be found for the provided authType");
    }
    logInWith((ParseAuthenticationProvider)authenticationProviders.get(paramString), paramLogInCallback);
  }
  
  static void logInWith(final String paramString, final JSONObject paramJSONObject, final LogInCallback paramLogInCallback)
  {
    BackgroundTask local6 = new BackgroundTask(paramLogInCallback)
    {
      public ParseUser run()
        throws ParseException
      {
        final ParseUser localParseUser = new ParseUser();
        try
        {
          localParseUser.authData.put(paramString, paramJSONObject);
          localParseUser.linkedServiceNames.add(paramString);
          localParseUser.startSave();
          ParseCommand localParseCommand = localParseUser.constructSignUpOrLoginCommand();
          localParseCommand.setInternalCallback(new ParseCommand.InternalCallback()
          {
            public void perform(ParseCommand paramAnonymous2ParseCommand, Object paramAnonymous2Object)
            {
              localParseUser.handleSaveResult(paramAnonymous2ParseCommand.op, (JSONObject)paramAnonymous2Object);
            }
          });
          localParseCommand.perform();
          localParseUser.synchronizeAuthData(paramString);
          ParseUser.saveCurrentUser(localParseUser);
          return localParseUser;
        }
        catch (JSONException localJSONException)
        {
          throw new ParseException(localJSONException);
        }
      }
    };
    if ((getCurrentUser() != null) && (ParseAnonymousUtils.isLinked(getCurrentUser())))
    {
      if (getCurrentUser().isLazy())
      {
        BackgroundTask.executeTask(new BackgroundTask(paramLogInCallback)
        {
          public ParseUser run()
            throws ParseException
          {
            int i = 0;
            ParseUser localParseUser1 = ParseUser.getCurrentUser();
            JSONObject localJSONObject = localParseUser1.authData.optJSONObject("anonymous");
            ParseUser.getCurrentUser().stripAnonymity();
            try
            {
              localParseUser1.authData.put(paramString, paramJSONObject);
              localParseUser1.linkedServiceNames.add(paramString);
              localParseUser1.resolveLaziness(true);
              i = 1;
              ParseUser localParseUser2 = ParseUser.getCurrentUser();
              return localParseUser2;
            }
            catch (JSONException localJSONException)
            {
              throw new ParseException(localJSONException);
            }
            finally
            {
              if (i == 0)
              {
                localParseUser1.authData.remove(paramString);
                localParseUser1.linkedServiceNames.remove(paramString);
                localParseUser1.restoreAnonymity(localJSONObject);
              }
            }
          }
        });
        return;
      }
      getCurrentUser().linkWith(paramString, paramJSONObject, new SaveCallback()
      {
        public void done(ParseException paramAnonymousParseException)
        {
          if (paramAnonymousParseException != null) {
            if (paramAnonymousParseException.getCode() != 208) {}
          }
          while (paramLogInCallback == null)
          {
            BackgroundTask.executeTask(this.val$logInWithTask);
            do
            {
              return;
            } while (paramLogInCallback == null);
            paramLogInCallback.internalDone(null, paramAnonymousParseException);
            return;
          }
          paramLogInCallback.internalDone(ParseUser.getCurrentUser(), null);
        }
      });
      return;
    }
    BackgroundTask.executeTask(local6);
  }
  
  public static void logOut()
  {
    
    if (currentUser != null)
    {
      Iterator localIterator = currentUser.getLinkedServiceNames().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        currentUser.logOutWith(str);
      }
      currentUser.isCurrentUser = false;
      currentUser.sessionToken = null;
    }
    currentUserMatchesDisk = true;
    currentUser = null;
    new File(Parse.getParseDir(), "currentUser").delete();
  }
  
  private void logOutWith(ParseAuthenticationProvider paramParseAuthenticationProvider)
  {
    paramParseAuthenticationProvider.deauthenticate();
  }
  
  static void registerAuthenticationProvider(ParseAuthenticationProvider paramParseAuthenticationProvider)
  {
    authenticationProviders.put(paramParseAuthenticationProvider.getAuthType(), paramParseAuthenticationProvider);
    if (getCurrentUser() != null) {
      getCurrentUser().synchronizeAuthData(paramParseAuthenticationProvider.getAuthType());
    }
  }
  
  public static void requestPasswordReset(String paramString)
    throws ParseException
  {
    constructPasswordResetCommand(paramString).perform();
  }
  
  public static void requestPasswordResetInBackground(final String paramString, RequestPasswordResetCallback paramRequestPasswordResetCallback)
  {
    BackgroundTask.executeTask(new BackgroundTask(paramRequestPasswordResetCallback)
    {
      public Void run()
        throws ParseException
      {
        ParseUser.requestPasswordReset(paramString);
        return null;
      }
    });
  }
  
  /* Error */
  private void resolveLaziness(boolean paramBoolean)
    throws ParseException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 324	com/parse/ParseUser:isLazy	()Z
    //   4: ifne +4 -> 8
    //   7: return
    //   8: aload_0
    //   9: getfield 60	com/parse/ParseUser:linkedServiceNames	Ljava/util/Set;
    //   12: invokeinterface 386 1 0
    //   17: ifne +14 -> 31
    //   20: aload_0
    //   21: iload_1
    //   22: invokevirtual 389	com/parse/ParseUser:signUp	(Z)V
    //   25: aload_0
    //   26: iconst_0
    //   27: putfield 48	com/parse/ParseUser:isLazy	Z
    //   30: return
    //   31: iload_1
    //   32: ifeq +8 -> 40
    //   35: aload_0
    //   36: iconst_1
    //   37: invokevirtual 392	com/parse/ParseUser:checkIfRunning	(Z)V
    //   40: aload_0
    //   41: invokespecial 82	com/parse/ParseUser:constructSignUpOrLoginCommand	()Lcom/parse/ParseCommand;
    //   44: astore_3
    //   45: aload_3
    //   46: new 394	com/parse/ParseUser$9
    //   49: dup
    //   50: aload_0
    //   51: invokespecial 396	com/parse/ParseUser$9:<init>	(Lcom/parse/ParseUser;)V
    //   54: invokevirtual 400	com/parse/ParseCommand:setInternalCallback	(Lcom/parse/ParseCommand$InternalCallback;)V
    //   57: aload_0
    //   58: invokevirtual 403	com/parse/ParseUser:startSave	()V
    //   61: aload_3
    //   62: invokevirtual 251	com/parse/ParseCommand:perform	()Ljava/lang/Object;
    //   65: checkcast 52	org/json/JSONObject
    //   68: astore 4
    //   70: aload 4
    //   72: ldc_w 405
    //   75: invokevirtual 409	org/json/JSONObject:optBoolean	(Ljava/lang/String;)Z
    //   78: ifeq +13 -> 91
    //   81: aload_0
    //   82: iconst_0
    //   83: putfield 48	com/parse/ParseUser:isLazy	Z
    //   86: aload_0
    //   87: invokevirtual 412	com/parse/ParseUser:finishedRunning	()V
    //   90: return
    //   91: new 2	com/parse/ParseUser
    //   94: dup
    //   95: invokespecial 261	com/parse/ParseUser:<init>	()V
    //   98: astore 5
    //   100: aload 5
    //   102: aload 4
    //   104: invokevirtual 264	com/parse/ParseUser:handleFetchResult	(Lorg/json/JSONObject;)V
    //   107: aload 5
    //   109: invokestatic 93	com/parse/ParseUser:saveCurrentUser	(Lcom/parse/ParseUser;)V
    //   112: goto -26 -> 86
    //   115: astore_2
    //   116: aload_0
    //   117: invokevirtual 412	com/parse/ParseUser:finishedRunning	()V
    //   120: aload_2
    //   121: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	122	0	this	ParseUser
    //   0	122	1	paramBoolean	boolean
    //   115	6	2	localObject	Object
    //   44	18	3	localParseCommand	ParseCommand
    //   68	35	4	localJSONObject	JSONObject
    //   98	10	5	localParseUser	ParseUser
    // Exception table:
    //   from	to	target	type
    //   40	86	115	finally
    //   91	112	115	finally
  }
  
  private void restoreAnonymity(JSONObject paramJSONObject)
  {
    if (paramJSONObject != null) {
      this.linkedServiceNames.add("anonymous");
    }
    try
    {
      this.authData.put("anonymous", paramJSONObject);
      return;
    }
    catch (JSONException localJSONException)
    {
      throw new RuntimeException(localJSONException);
    }
  }
  
  private static void saveCurrentUser(ParseUser paramParseUser)
  {
    
    if (currentUser != paramParseUser) {
      logOut();
    }
    paramParseUser.isCurrentUser = true;
    paramParseUser.synchronizeAllAuthData();
    paramParseUser.saveToDisk(Parse.applicationContext, "currentUser");
    currentUserMatchesDisk = true;
    currentUser = paramParseUser;
  }
  
  private void stripAnonymity()
  {
    if (ParseAnonymousUtils.isLinked(this)) {
      this.linkedServiceNames.remove("anonymous");
    }
    try
    {
      this.authData.put("anonymous", JSONObject.NULL);
      this.dirty = true;
      return;
    }
    catch (JSONException localJSONException)
    {
      throw new RuntimeException(localJSONException);
    }
  }
  
  private void synchronizeAllAuthData()
  {
    if (this.authData != null)
    {
      Iterator localIterator = this.authData.keys();
      while (localIterator.hasNext()) {
        synchronizeAuthData((String)localIterator.next());
      }
    }
  }
  
  private void synchronizeAuthData(String paramString)
  {
    if (!isCurrentUser()) {}
    ParseAuthenticationProvider localParseAuthenticationProvider;
    do
    {
      do
      {
        return;
      } while (!authenticationProviders.containsKey(paramString));
      localParseAuthenticationProvider = (ParseAuthenticationProvider)authenticationProviders.get(paramString);
    } while (localParseAuthenticationProvider.restoreAuthentication(this.authData.optJSONObject(localParseAuthenticationProvider.getAuthType())));
    unlinkFromInBackground(paramString);
  }
  
  void cleanUpAuthData()
  {
    if (!isCurrentUser()) {}
    for (;;)
    {
      return;
      Iterator localIterator = this.authData.keys();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        if (this.authData.isNull(str))
        {
          localIterator.remove();
          this.linkedServiceNames.remove(str);
          if (authenticationProviders.containsKey(str)) {
            ((ParseAuthenticationProvider)authenticationProviders.get(str)).restoreAuthentication(null);
          }
        }
      }
    }
  }
  
  protected ParseCommand constructSaveCommand()
    throws ParseException
  {
    ParseCommand localParseCommand = super.constructSaveCommand();
    if (localParseCommand == null) {
      localParseCommand = null;
    }
    do
    {
      return localParseCommand;
      if (this.password != null) {
        localParseCommand.put("user_password", this.password);
      }
    } while (this.authData.length() <= 0);
    localParseCommand.put("auth_data", this.authData);
    return localParseCommand;
  }
  
  public ParseUser fetch()
    throws ParseException
  {
    if (isLazy()) {}
    do
    {
      return this;
      super.fetch();
      cleanUpAuthData();
    } while (!isCurrentUser());
    saveCurrentUser(this);
    return this;
  }
  
  public ParseUser fetchIfNeeded()
    throws ParseException
  {
    return (ParseUser)super.fetchIfNeeded();
  }
  
  public String getEmail()
  {
    return getString("email");
  }
  
  Set<String> getLinkedServiceNames()
  {
    return this.readOnlyLinkedServiceNames;
  }
  
  public String getSessionToken()
  {
    return this.sessionToken;
  }
  
  public String getUsername()
  {
    return getString("username");
  }
  
  public boolean isAuthenticated()
  {
    return (isLazy()) || ((this.sessionToken != null) && (getCurrentUser() != null) && (getObjectId().equals(getCurrentUser().getObjectId())));
  }
  
  boolean isCurrentUser()
  {
    return this.isCurrentUser;
  }
  
  boolean isLazy()
  {
    return this.isLazy;
  }
  
  public boolean isNew()
  {
    return this.isNew;
  }
  
  void linkWith(String paramString, SaveCallback paramSaveCallback)
  {
    if (!authenticationProviders.containsKey(paramString)) {
      throw new IllegalArgumentException("No authentication provider could be found for the provided authType");
    }
    linkWith((ParseAuthenticationProvider)authenticationProviders.get(paramString), paramSaveCallback);
  }
  
  void linkWith(final String paramString, final JSONObject paramJSONObject, SaveCallback paramSaveCallback)
  {
    BackgroundTask.executeTask(new BackgroundTask(paramSaveCallback)
    {
      /* Error */
      public Void run()
        throws ParseException
      {
        // Byte code:
        //   0: aload_0
        //   1: getfield 20	com/parse/ParseUser$11:this$0	Lcom/parse/ParseUser;
        //   4: astore_1
        //   5: aload_1
        //   6: invokestatic 42	com/parse/ParseUser:access$000	(Lcom/parse/ParseUser;)Lorg/json/JSONObject;
        //   9: aload_0
        //   10: getfield 22	com/parse/ParseUser$11:val$authType	Ljava/lang/String;
        //   13: aload_0
        //   14: getfield 24	com/parse/ParseUser$11:val$authData	Lorg/json/JSONObject;
        //   17: invokevirtual 48	org/json/JSONObject:put	(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;
        //   20: pop
        //   21: aload_1
        //   22: invokestatic 52	com/parse/ParseUser:access$100	(Lcom/parse/ParseUser;)Ljava/util/Set;
        //   25: aload_0
        //   26: getfield 22	com/parse/ParseUser$11:val$authType	Ljava/lang/String;
        //   29: invokeinterface 58 2 0
        //   34: pop
        //   35: aload_0
        //   36: getfield 24	com/parse/ParseUser$11:val$authData	Lorg/json/JSONObject;
        //   39: ldc 60
        //   41: invokevirtual 64	org/json/JSONObject:optJSONObject	(Ljava/lang/String;)Lorg/json/JSONObject;
        //   44: astore 5
        //   46: aload_0
        //   47: getfield 20	com/parse/ParseUser$11:this$0	Lcom/parse/ParseUser;
        //   50: invokestatic 68	com/parse/ParseUser:access$600	(Lcom/parse/ParseUser;)V
        //   53: aload_1
        //   54: iconst_1
        //   55: putfield 72	com/parse/ParseUser:dirty	Z
        //   58: aload_1
        //   59: invokevirtual 76	com/parse/ParseUser:save	()V
        //   62: aload_1
        //   63: aload_0
        //   64: getfield 22	com/parse/ParseUser$11:val$authType	Ljava/lang/String;
        //   67: invokestatic 80	com/parse/ParseUser:access$400	(Lcom/parse/ParseUser;Ljava/lang/String;)V
        //   70: aconst_null
        //   71: areturn
        //   72: astore_2
        //   73: new 31	com/parse/ParseException
        //   76: dup
        //   77: aload_2
        //   78: invokespecial 83	com/parse/ParseException:<init>	(Ljava/lang/Throwable;)V
        //   81: athrow
        //   82: astore 7
        //   84: aload_0
        //   85: getfield 20	com/parse/ParseUser$11:this$0	Lcom/parse/ParseUser;
        //   88: aload 5
        //   90: invokestatic 87	com/parse/ParseUser:access$800	(Lcom/parse/ParseUser;Lorg/json/JSONObject;)V
        //   93: aload 7
        //   95: athrow
        //   96: astore 6
        //   98: aload_0
        //   99: getfield 20	com/parse/ParseUser$11:this$0	Lcom/parse/ParseUser;
        //   102: aload 5
        //   104: invokestatic 87	com/parse/ParseUser:access$800	(Lcom/parse/ParseUser;Lorg/json/JSONObject;)V
        //   107: new 31	com/parse/ParseException
        //   110: dup
        //   111: aload 6
        //   113: invokespecial 83	com/parse/ParseException:<init>	(Ljava/lang/Throwable;)V
        //   116: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	117	0	this	11
        //   4	59	1	localParseUser	ParseUser
        //   72	6	2	localJSONException	JSONException
        //   44	59	5	localJSONObject	JSONObject
        //   96	16	6	localException	java.lang.Exception
        //   82	12	7	localParseException	ParseException
        // Exception table:
        //   from	to	target	type
        //   5	58	72	org/json/JSONException
        //   58	70	82	com/parse/ParseException
        //   58	70	96	java/lang/Exception
      }
    });
  }
  
  void logOutWith(String paramString)
  {
    if ((authenticationProviders.containsKey(paramString)) && (this.linkedServiceNames.contains(paramString))) {
      logOutWith((ParseAuthenticationProvider)authenticationProviders.get(paramString));
    }
  }
  
  protected void mergeFromObject(ParseObject paramParseObject)
  {
    super.mergeFromObject(paramParseObject);
    if ((paramParseObject instanceof ParseUser))
    {
      this.sessionToken = ((ParseUser)paramParseObject).sessionToken;
      this.isNew = ((ParseUser)paramParseObject).isNew();
      Iterator localIterator1 = this.authData.keys();
      while (localIterator1.hasNext())
      {
        localIterator1.next();
        localIterator1.remove();
      }
      Iterator localIterator2 = ((ParseUser)paramParseObject).authData.keys();
      while (localIterator2.hasNext())
      {
        String str = (String)localIterator2.next();
        try
        {
          Object localObject = ((ParseUser)paramParseObject).authData.get(str);
          this.authData.put(str, localObject);
        }
        catch (JSONException localJSONException)
        {
          throw new RuntimeException("A JSONException occurred where one was not possible.");
        }
      }
      this.linkedServiceNames.clear();
      this.linkedServiceNames.addAll(((ParseUser)paramParseObject).linkedServiceNames);
    }
  }
  
  /* Error */
  protected void mergeFromServer(JSONObject paramJSONObject)
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: invokespecial 514	com/parse/ParseObject:mergeFromServer	(Lorg/json/JSONObject;)V
    //   5: aload_1
    //   6: ldc_w 516
    //   9: invokevirtual 519	org/json/JSONObject:has	(Ljava/lang/String;)Z
    //   12: ifeq +14 -> 26
    //   15: aload_0
    //   16: aload_1
    //   17: ldc_w 516
    //   20: invokevirtual 520	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   23: putfield 348	com/parse/ParseUser:sessionToken	Ljava/lang/String;
    //   26: aload_1
    //   27: ldc_w 454
    //   30: invokevirtual 519	org/json/JSONObject:has	(Ljava/lang/String;)Z
    //   33: ifeq +114 -> 147
    //   36: aload_1
    //   37: ldc_w 454
    //   40: invokevirtual 523	org/json/JSONObject:getJSONObject	(Ljava/lang/String;)Lorg/json/JSONObject;
    //   43: astore 4
    //   45: aload 4
    //   47: invokevirtual 166	org/json/JSONObject:keys	()Ljava/util/Iterator;
    //   50: astore 5
    //   52: aload 5
    //   54: invokeinterface 172 1 0
    //   59: ifeq +88 -> 147
    //   62: aload 5
    //   64: invokeinterface 176 1 0
    //   69: checkcast 178	java/lang/String
    //   72: astore 6
    //   74: aload_0
    //   75: getfield 55	com/parse/ParseUser:authData	Lorg/json/JSONObject;
    //   78: aload 6
    //   80: aload 4
    //   82: aload 6
    //   84: invokevirtual 182	org/json/JSONObject:get	(Ljava/lang/String;)Ljava/lang/Object;
    //   87: invokevirtual 282	org/json/JSONObject:put	(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;
    //   90: pop
    //   91: aload 4
    //   93: aload 6
    //   95: invokevirtual 446	org/json/JSONObject:isNull	(Ljava/lang/String;)Z
    //   98: ifne +15 -> 113
    //   101: aload_0
    //   102: getfield 60	com/parse/ParseUser:linkedServiceNames	Ljava/util/Set;
    //   105: aload 6
    //   107: invokeinterface 288 2 0
    //   112: pop
    //   113: aload_0
    //   114: aload 6
    //   116: invokespecial 88	com/parse/ParseUser:synchronizeAuthData	(Ljava/lang/String;)V
    //   119: goto -67 -> 52
    //   122: astore_3
    //   123: new 117	java/lang/RuntimeException
    //   126: dup
    //   127: aload_3
    //   128: invokespecial 291	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
    //   131: athrow
    //   132: astore 9
    //   134: new 117	java/lang/RuntimeException
    //   137: dup
    //   138: aload 9
    //   140: invokevirtual 526	org/json/JSONException:getMessage	()Ljava/lang/String;
    //   143: invokespecial 121	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   146: athrow
    //   147: aload_1
    //   148: ldc_w 405
    //   151: invokevirtual 519	org/json/JSONObject:has	(Ljava/lang/String;)Z
    //   154: ifeq +14 -> 168
    //   157: aload_0
    //   158: aload_1
    //   159: ldc_w 405
    //   162: invokevirtual 529	org/json/JSONObject:getBoolean	(Ljava/lang/String;)Z
    //   165: putfield 483	com/parse/ParseUser:isNew	Z
    //   168: return
    //   169: astore_2
    //   170: new 117	java/lang/RuntimeException
    //   173: dup
    //   174: aload_2
    //   175: invokespecial 291	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
    //   178: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	179	0	this	ParseUser
    //   0	179	1	paramJSONObject	JSONObject
    //   169	6	2	localJSONException1	JSONException
    //   122	6	3	localJSONException2	JSONException
    //   43	49	4	localJSONObject	JSONObject
    //   50	13	5	localIterator	Iterator
    //   72	43	6	str	String
    //   132	7	9	localJSONException3	JSONException
    // Exception table:
    //   from	to	target	type
    //   36	52	122	org/json/JSONException
    //   52	113	122	org/json/JSONException
    //   113	119	122	org/json/JSONException
    //   15	26	132	org/json/JSONException
    //   157	168	169	org/json/JSONException
  }
  
  public void put(String paramString, Object paramObject)
  {
    if (paramString.equals("username")) {
      stripAnonymity();
    }
    super.put(paramString, paramObject);
  }
  
  public void remove(String paramString)
  {
    if (paramString == "username") {
      throw new IllegalArgumentException("Can't remove the username key.");
    }
    super.remove(paramString);
  }
  
  public void save(boolean paramBoolean)
    throws ParseException
  {
    if (isLazy()) {
      resolveLaziness(paramBoolean);
    }
    do
    {
      return;
      super.save(paramBoolean);
      cleanUpAuthData();
    } while (!isCurrentUser());
    saveCurrentUser(this);
  }
  
  public void setEmail(String paramString)
  {
    checkIfRunning();
    put("email", paramString);
  }
  
  public void setPassword(String paramString)
  {
    checkIfRunning();
    this.password = paramString;
    this.dirty = true;
  }
  
  public void setUsername(String paramString)
  {
    checkIfRunning();
    put("username", paramString);
  }
  
  public void signUp()
    throws ParseException
  {
    signUp(true);
  }
  
  /* Error */
  protected void signUp(boolean paramBoolean)
    throws ParseException
  {
    // Byte code:
    //   0: iload_1
    //   1: ifeq +8 -> 9
    //   4: aload_0
    //   5: iconst_1
    //   6: invokevirtual 392	com/parse/ParseUser:checkIfRunning	(Z)V
    //   9: aload_0
    //   10: invokevirtual 547	com/parse/ParseUser:getUsername	()Ljava/lang/String;
    //   13: ifnull +13 -> 26
    //   16: aload_0
    //   17: invokevirtual 547	com/parse/ParseUser:getUsername	()Ljava/lang/String;
    //   20: invokevirtual 548	java/lang/String:length	()I
    //   23: ifne +21 -> 44
    //   26: new 241	java/lang/IllegalArgumentException
    //   29: dup
    //   30: ldc_w 550
    //   33: invokespecial 244	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   36: athrow
    //   37: astore_2
    //   38: aload_0
    //   39: invokevirtual 412	com/parse/ParseUser:finishedRunning	()V
    //   42: aload_2
    //   43: athrow
    //   44: aload_0
    //   45: getfield 199	com/parse/ParseUser:password	Ljava/lang/String;
    //   48: ifnonnull +14 -> 62
    //   51: new 241	java/lang/IllegalArgumentException
    //   54: dup
    //   55: ldc_w 552
    //   58: invokespecial 244	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   61: athrow
    //   62: aload_0
    //   63: invokevirtual 478	com/parse/ParseUser:getObjectId	()Ljava/lang/String;
    //   66: astore_3
    //   67: aload_3
    //   68: ifnull +65 -> 133
    //   71: aload_0
    //   72: getfield 55	com/parse/ParseUser:authData	Lorg/json/JSONObject;
    //   75: ldc_w 414
    //   78: invokevirtual 519	org/json/JSONObject:has	(Ljava/lang/String;)Z
    //   81: ifeq +41 -> 122
    //   84: aload_0
    //   85: getfield 55	com/parse/ParseUser:authData	Lorg/json/JSONObject;
    //   88: ldc_w 414
    //   91: invokevirtual 182	org/json/JSONObject:get	(Ljava/lang/String;)Ljava/lang/Object;
    //   94: getstatic 255	org/json/JSONObject:NULL	Ljava/lang/Object;
    //   97: if_acmpne +25 -> 122
    //   100: aload_0
    //   101: iconst_0
    //   102: invokevirtual 553	com/parse/ParseUser:save	(Z)V
    //   105: aload_0
    //   106: invokevirtual 412	com/parse/ParseUser:finishedRunning	()V
    //   109: return
    //   110: astore 6
    //   112: new 78	com/parse/ParseException
    //   115: dup
    //   116: aload 6
    //   118: invokespecial 554	com/parse/ParseException:<init>	(Ljava/lang/Throwable;)V
    //   121: athrow
    //   122: new 241	java/lang/IllegalArgumentException
    //   125: dup
    //   126: ldc_w 556
    //   129: invokespecial 244	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   132: athrow
    //   133: aload_0
    //   134: getfield 560	com/parse/ParseUser:operationSetQueue	Ljava/util/LinkedList;
    //   137: invokevirtual 563	java/util/LinkedList:size	()I
    //   140: iconst_1
    //   141: if_icmple +14 -> 155
    //   144: new 241	java/lang/IllegalArgumentException
    //   147: dup
    //   148: ldc_w 565
    //   151: invokespecial 244	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   154: athrow
    //   155: invokestatic 318	com/parse/ParseUser:getCurrentUser	()Lcom/parse/ParseUser;
    //   158: ifnull +100 -> 258
    //   161: invokestatic 318	com/parse/ParseUser:getCurrentUser	()Lcom/parse/ParseUser;
    //   164: invokestatic 322	com/parse/ParseAnonymousUtils:isLinked	(Lcom/parse/ParseUser;)Z
    //   167: ifeq +91 -> 258
    //   170: aload_0
    //   171: invokevirtual 431	com/parse/ParseUser:isCurrentUser	()Z
    //   174: ifeq +14 -> 188
    //   177: new 241	java/lang/IllegalArgumentException
    //   180: dup
    //   181: ldc_w 567
    //   184: invokespecial 244	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   187: athrow
    //   188: aload_0
    //   189: invokevirtual 570	com/parse/ParseUser:checkForChangesToMutableContainers	()V
    //   192: invokestatic 318	com/parse/ParseUser:getCurrentUser	()Lcom/parse/ParseUser;
    //   195: invokevirtual 570	com/parse/ParseUser:checkForChangesToMutableContainers	()V
    //   198: invokestatic 318	com/parse/ParseUser:getCurrentUser	()Lcom/parse/ParseUser;
    //   201: aload_0
    //   202: invokevirtual 573	com/parse/ParseUser:copyChangesFrom	(Lcom/parse/ParseObject;)V
    //   205: invokestatic 318	com/parse/ParseUser:getCurrentUser	()Lcom/parse/ParseUser;
    //   208: iconst_1
    //   209: putfield 429	com/parse/ParseUser:dirty	Z
    //   212: invokestatic 318	com/parse/ParseUser:getCurrentUser	()Lcom/parse/ParseUser;
    //   215: aload_0
    //   216: getfield 199	com/parse/ParseUser:password	Ljava/lang/String;
    //   219: invokevirtual 575	com/parse/ParseUser:setPassword	(Ljava/lang/String;)V
    //   222: invokestatic 318	com/parse/ParseUser:getCurrentUser	()Lcom/parse/ParseUser;
    //   225: aload_0
    //   226: invokevirtual 547	com/parse/ParseUser:getUsername	()Ljava/lang/String;
    //   229: invokevirtual 577	com/parse/ParseUser:setUsername	(Ljava/lang/String;)V
    //   232: aload_0
    //   233: invokevirtual 580	com/parse/ParseUser:clearChanges	()V
    //   236: invokestatic 318	com/parse/ParseUser:getCurrentUser	()Lcom/parse/ParseUser;
    //   239: invokevirtual 582	com/parse/ParseUser:save	()V
    //   242: aload_0
    //   243: invokestatic 318	com/parse/ParseUser:getCurrentUser	()Lcom/parse/ParseUser;
    //   246: invokevirtual 583	com/parse/ParseUser:mergeFromObject	(Lcom/parse/ParseObject;)V
    //   249: aload_0
    //   250: invokestatic 93	com/parse/ParseUser:saveCurrentUser	(Lcom/parse/ParseUser;)V
    //   253: aload_0
    //   254: invokevirtual 412	com/parse/ParseUser:finishedRunning	()V
    //   257: return
    //   258: aload_0
    //   259: invokespecial 585	com/parse/ParseUser:constructSignUpCommand	()Lcom/parse/ParseCommand;
    //   262: astore 4
    //   264: aload 4
    //   266: ifnonnull +8 -> 274
    //   269: aload_0
    //   270: invokevirtual 412	com/parse/ParseUser:finishedRunning	()V
    //   273: return
    //   274: aload 4
    //   276: new 587	com/parse/ParseUser$1
    //   279: dup
    //   280: aload_0
    //   281: invokespecial 588	com/parse/ParseUser$1:<init>	(Lcom/parse/ParseUser;)V
    //   284: invokevirtual 400	com/parse/ParseCommand:setInternalCallback	(Lcom/parse/ParseCommand$InternalCallback;)V
    //   287: aload_0
    //   288: invokevirtual 403	com/parse/ParseUser:startSave	()V
    //   291: aload 4
    //   293: invokevirtual 251	com/parse/ParseCommand:perform	()Ljava/lang/Object;
    //   296: pop
    //   297: aload_0
    //   298: invokestatic 93	com/parse/ParseUser:saveCurrentUser	(Lcom/parse/ParseUser;)V
    //   301: aload_0
    //   302: iconst_1
    //   303: putfield 483	com/parse/ParseUser:isNew	Z
    //   306: aload_0
    //   307: invokevirtual 412	com/parse/ParseUser:finishedRunning	()V
    //   310: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	311	0	this	ParseUser
    //   0	311	1	paramBoolean	boolean
    //   37	6	2	localObject	Object
    //   66	2	3	str	String
    //   262	30	4	localParseCommand	ParseCommand
    //   110	7	6	localJSONException	JSONException
    // Exception table:
    //   from	to	target	type
    //   9	26	37	finally
    //   26	37	37	finally
    //   44	62	37	finally
    //   62	67	37	finally
    //   71	105	37	finally
    //   112	122	37	finally
    //   122	133	37	finally
    //   133	155	37	finally
    //   155	188	37	finally
    //   188	253	37	finally
    //   258	264	37	finally
    //   274	306	37	finally
    //   71	105	110	org/json/JSONException
  }
  
  public void signUpInBackground(SignUpCallback paramSignUpCallback)
  {
    checkIfRunning(true);
    BackgroundTask.executeTask(new BackgroundTask(paramSignUpCallback)
    {
      public Void run()
        throws ParseException
      {
        ParseUser.this.signUp(false);
        return null;
      }
    });
  }
  
  /* Error */
  JSONObject toJSONObjectForDataFile()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 598	com/parse/ParseObject:toJSONObjectForDataFile	()Lorg/json/JSONObject;
    //   4: astore_1
    //   5: aload_0
    //   6: getfield 348	com/parse/ParseUser:sessionToken	Ljava/lang/String;
    //   9: ifnull +15 -> 24
    //   12: aload_1
    //   13: ldc_w 516
    //   16: aload_0
    //   17: getfield 348	com/parse/ParseUser:sessionToken	Ljava/lang/String;
    //   20: invokevirtual 282	org/json/JSONObject:put	(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;
    //   23: pop
    //   24: aload_0
    //   25: getfield 55	com/parse/ParseUser:authData	Lorg/json/JSONObject;
    //   28: invokevirtual 452	org/json/JSONObject:length	()I
    //   31: ifle +15 -> 46
    //   34: aload_1
    //   35: ldc_w 454
    //   38: aload_0
    //   39: getfield 55	com/parse/ParseUser:authData	Lorg/json/JSONObject;
    //   42: invokevirtual 282	org/json/JSONObject:put	(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;
    //   45: pop
    //   46: aload_1
    //   47: areturn
    //   48: astore 4
    //   50: new 117	java/lang/RuntimeException
    //   53: dup
    //   54: ldc_w 600
    //   57: invokespecial 121	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   60: athrow
    //   61: astore_2
    //   62: new 117	java/lang/RuntimeException
    //   65: dup
    //   66: ldc_w 602
    //   69: invokespecial 121	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   72: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	73	0	this	ParseUser
    //   4	43	1	localJSONObject	JSONObject
    //   61	1	2	localJSONException1	JSONException
    //   48	1	4	localJSONException2	JSONException
    // Exception table:
    //   from	to	target	type
    //   12	24	48	org/json/JSONException
    //   34	46	61	org/json/JSONException
  }
  
  /* Error */
  protected JSONObject toJSONObjectForSaving()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 603	com/parse/ParseObject:toJSONObjectForSaving	()Lorg/json/JSONObject;
    //   4: astore_1
    //   5: aload_0
    //   6: getfield 348	com/parse/ParseUser:sessionToken	Ljava/lang/String;
    //   9: ifnull +15 -> 24
    //   12: aload_1
    //   13: ldc_w 516
    //   16: aload_0
    //   17: getfield 348	com/parse/ParseUser:sessionToken	Ljava/lang/String;
    //   20: invokevirtual 282	org/json/JSONObject:put	(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;
    //   23: pop
    //   24: aload_0
    //   25: getfield 55	com/parse/ParseUser:authData	Lorg/json/JSONObject;
    //   28: invokevirtual 452	org/json/JSONObject:length	()I
    //   31: ifle +15 -> 46
    //   34: aload_1
    //   35: ldc_w 454
    //   38: aload_0
    //   39: getfield 55	com/parse/ParseUser:authData	Lorg/json/JSONObject;
    //   42: invokevirtual 282	org/json/JSONObject:put	(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;
    //   45: pop
    //   46: aload_1
    //   47: areturn
    //   48: astore 4
    //   50: new 117	java/lang/RuntimeException
    //   53: dup
    //   54: ldc_w 600
    //   57: invokespecial 121	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   60: athrow
    //   61: astore_2
    //   62: new 117	java/lang/RuntimeException
    //   65: dup
    //   66: ldc_w 602
    //   69: invokespecial 121	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   72: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	73	0	this	ParseUser
    //   4	43	1	localJSONObject	JSONObject
    //   61	1	2	localJSONException1	JSONException
    //   48	1	4	localJSONException2	JSONException
    // Exception table:
    //   from	to	target	type
    //   12	24	48	org/json/JSONException
    //   34	46	61	org/json/JSONException
  }
  
  boolean unlinkFrom(String paramString)
    throws ParseException
  {
    if (paramString == null) {}
    while (!this.authData.has(paramString)) {
      return false;
    }
    try
    {
      this.authData.put(paramString, JSONObject.NULL);
      this.dirty = true;
      label34:
      save();
      return true;
    }
    catch (JSONException localJSONException)
    {
      break label34;
    }
  }
  
  void unlinkFromInBackground(String paramString)
  {
    unlinkFromInBackground(paramString, null);
  }
  
  void unlinkFromInBackground(final String paramString, final SaveCallback paramSaveCallback)
  {
    if ((paramString != null) && (this.authData.has(paramString))) {}
    try
    {
      this.authData.put(paramString, JSONObject.NULL);
      this.dirty = true;
      label32:
      saveInBackground(new SaveCallback()
      {
        public void done(ParseException paramAnonymousParseException)
        {
          if (paramAnonymousParseException == null)
          {
            ParseUser.this.authData.remove(paramString);
            ParseUser.this.linkedServiceNames.remove(paramString);
            if (ParseUser.authenticationProviders.containsKey(paramString)) {
              ((ParseAuthenticationProvider)ParseUser.authenticationProviders.get(paramString)).restoreAuthentication(null);
            }
          }
          if (paramSaveCallback != null) {
            paramSaveCallback.internalDone(null, paramAnonymousParseException);
          }
        }
      });
      do
      {
        return;
      } while (paramSaveCallback == null);
      paramSaveCallback.internalDone(null, null);
      return;
    }
    catch (JSONException localJSONException)
    {
      break label32;
    }
  }
  
  protected void validateDelete()
  {
    super.validateDelete();
    if ((!isAuthenticated()) && (isDirty())) {
      throw new IllegalArgumentException("Cannot delete a ParseUser that is not authenticated.");
    }
  }
  
  protected void validateSave()
  {
    if (getObjectId() == null) {
      throw new IllegalArgumentException("Cannot save a ParseUser until it has been signed up. Call signUp first.");
    }
    if ((!isAuthenticated()) && (isDirty()) && (!getObjectId().equals(getCurrentUser().getObjectId()))) {
      throw new IllegalArgumentException("Cannot save a ParseUser that is not authenticated.");
    }
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.ParseUser
 * JD-Core Version:    0.7.0.1
 */