package com.geckocap.login;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.Arrays;
import java.util.List;

public class NewPrescription
  extends Activity
{
  private TextView capNum;
  private TextView doneRegister;
  private List<String> doses = Arrays.asList(new String[] { "0 (as needed)", "1", "2", "3" });
  private Spinner patient;
  private String patientName;
  private List<String> patients = Arrays.asList(new String[] { "Joey", "Mary Kate" });
  private TextView prescription;
  private String prescriptionName;
  private Spinner usage;
  private String usageAmount;
  
  public void onCreate(Bundle paramBundle)
  {
    try
    {
      super.onCreate(paramBundle);
      setContentView(2130903056);
      return;
    }
    catch (Exception localException)
    {
      Log.e("ERROR", "ERROR IN CODE: " + localException.toString());
      localException.printStackTrace();
    }
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.geckocap.login.NewPrescription
 * JD-Core Version:    0.7.0.1
 */