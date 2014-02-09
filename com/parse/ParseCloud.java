package com.parse;

import java.util.Map;

public class ParseCloud
{
  public static <T> T callFunction(String paramString, Map<String, ?> paramMap)
    throws ParseException
  {
    return convertCloudResponse(constructCallCommand(paramString, paramMap).perform());
  }
  
  public static <T> void callFunctionInBackground(String paramString, Map<String, ?> paramMap, FunctionCallback<T> paramFunctionCallback)
  {
    BackgroundTask.executeTask(new BackgroundTask(paramFunctionCallback)
    {
      public T run()
        throws ParseException
      {
        return ParseCloud.convertCloudResponse(this.val$command.perform());
      }
    });
  }
  
  private static ParseCommand constructCallCommand(String paramString, Map<String, ?> paramMap)
  {
    ParseCommand localParseCommand = new ParseCommand("client_function");
    localParseCommand.put("data", Parse.encodeJSONObject(paramMap, false));
    localParseCommand.put("function", paramString);
    return localParseCommand;
  }
  
  private static Object convertCloudResponse(Object paramObject)
  {
    Object localObject = Parse.decodeJSONObject(paramObject);
    if (localObject == null) {
      return paramObject;
    }
    return localObject;
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.ParseCloud
 * JD-Core Version:    0.7.0.1
 */