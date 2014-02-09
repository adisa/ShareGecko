package com.parse;

import android.os.AsyncTask;

abstract class BackgroundTask<T>
  extends AsyncTask<Void, Void, Void>
{
  private ParseCallback<T> callback;
  private ParseException exception = null;
  private T result = null;
  
  BackgroundTask(ParseCallback<T> paramParseCallback)
  {
    this.callback = paramParseCallback;
  }
  
  static int executeTask(BackgroundTask<?> paramBackgroundTask)
  {
    paramBackgroundTask.execute(new Void[0]);
    return 0;
  }
  
  protected Void doInBackground(Void... paramVarArgs)
  {
    try
    {
      this.result = run();
      return null;
    }
    catch (ParseException localParseException)
    {
      this.exception = localParseException;
    }
    return null;
  }
  
  void executeInThisThread()
  {
    doInBackground(new Void[0]);
    onPostExecute(null);
  }
  
  protected void onPostExecute(Void paramVoid)
  {
    if (this.callback != null) {
      this.callback.internalDone(this.result, this.exception);
    }
  }
  
  public abstract T run()
    throws ParseException;
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.BackgroundTask
 * JD-Core Version:    0.7.0.1
 */