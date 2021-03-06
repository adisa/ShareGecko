package com.parse;

import com.parse.entity.mime.HttpMultipartMode;
import com.parse.entity.mime.MultipartEntity;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

class CountingMultipartEntity
  extends MultipartEntity
{
  private final ProgressCallback progressCallback;
  
  public CountingMultipartEntity(ProgressCallback paramProgressCallback)
  {
    this.progressCallback = paramProgressCallback;
  }
  
  public CountingMultipartEntity(HttpMultipartMode paramHttpMultipartMode, ProgressCallback paramProgressCallback)
  {
    super(paramHttpMultipartMode);
    this.progressCallback = paramProgressCallback;
  }
  
  public CountingMultipartEntity(HttpMultipartMode paramHttpMultipartMode, String paramString, Charset paramCharset, ProgressCallback paramProgressCallback)
  {
    super(paramHttpMultipartMode, paramString, paramCharset);
    this.progressCallback = paramProgressCallback;
  }
  
  public void writeTo(OutputStream paramOutputStream)
    throws IOException
  {
    super.writeTo(new CountingOutputStream(paramOutputStream, this.progressCallback, getContentLength()));
  }
  
  public static class CountingOutputStream
    extends FilterOutputStream
  {
    private boolean hasReportedDone = false;
    private final ProgressCallback progressCallback;
    private long totalSize;
    private long uploadedSize;
    
    public CountingOutputStream(OutputStream paramOutputStream, ProgressCallback paramProgressCallback, long paramLong)
    {
      super();
      this.progressCallback = paramProgressCallback;
      this.totalSize = paramLong;
      this.uploadedSize = 0L;
    }
    
    private void notifyCallback()
    {
      if (this.hasReportedDone) {}
      final int i;
      do
      {
        return;
        i = Math.round(100.0F * ((float)this.uploadedSize / (float)this.totalSize));
        BackgroundTask.executeTask(new BackgroundTask(this.progressCallback)
        {
          public Integer run()
            throws ParseException
          {
            return Integer.valueOf(i);
          }
        });
      } while (i != 100);
      this.hasReportedDone = true;
    }
    
    public void write(int paramInt)
      throws IOException
    {
      this.out.write(paramInt);
      this.uploadedSize = (1L + this.uploadedSize);
      notifyCallback();
    }
    
    public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      this.out.write(paramArrayOfByte, paramInt1, paramInt2);
      this.uploadedSize += paramInt2;
      notifyCallback();
    }
  }
}


/* Location:           C:\Users\adisa\GeckoCap\com.geckocap.login-1-dex2jar.jar
 * Qualified Name:     com.parse.CountingMultipartEntity
 * JD-Core Version:    0.7.0.1
 */