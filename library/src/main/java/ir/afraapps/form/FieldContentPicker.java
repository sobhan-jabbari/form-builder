package ir.afraapps.form;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public abstract class FieldContentPicker extends FormLayout {
  private TextView txtTitle;
  private TextView txtContentName;
  private TextView txtError;
  private FrameLayout picLayout;
  @Nullable
  private ContentPickListener contentPickListener;

  abstract String getMimeType();

  public FieldContentPicker(@NonNull Context context) {
    this(context, null, 0);
  }

  public FieldContentPicker(@NonNull Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public FieldContentPicker(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.init(context, attrs, defStyleAttr);
  }

  private void init(Context context, AttributeSet attrs, int defStyleAttr) {
    this.initViews(context);
    if (attrs != null) {
      TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.FieldContentPicker, defStyleAttr, 0);
      if (a.hasValue(R.styleable.FieldContentPicker_android_hint)) {
        CharSequence hint = a.getText(R.styleable.FieldContentPicker_android_hint);
        this.setHint(hint);
      }
      if (a.hasValue(R.styleable.FieldContentPicker_picViewResId)) {
        int picViewResId = a.getResourceId(R.styleable.FieldContentPicker_picViewResId, 0);
        setPicLayout(picViewResId);
      }
      a.recycle();
    }
  }

  protected abstract void onPrePickContent();

  /**
   * @param value in Base64
   * @param name  file name
   */
  protected abstract void setContentValue(String value, String name);

  private void initViews(Context context) {
    LayoutInflater.from(context).inflate(R.layout.field_content_picker, this, true);
    this.txtTitle = this.findViewById(R.id.txt_title);
    this.txtError = this.findViewById(R.id.txt_error);
    this.txtContentName = this.findViewById(R.id.txt_file_name);
    this.picLayout = this.findViewById(R.id.pic_parent);
    this.picLayout.setOnClickListener((v) -> this.prepareContentPicker());
  }


  public void setPicLayout(int resId) {
    if (picLayout != null) {
      picLayout.removeAllViews();
      if (resId <= 0) {
        picLayout.setVisibility(View.GONE);
      } else {
        LayoutInflater.from(getContext()).inflate(resId, picLayout);
        picLayout.setVisibility(View.VISIBLE);
      }
    }
  }

  public void setContentPickListener(@Nullable ContentPickListener contentPickListener) {
    this.contentPickListener = contentPickListener;
  }

  private void prepareContentPicker() {
    onPrePickContent();
    pickContent();
  }

  protected void pickContent() {
    if (contentPickListener != null) {
      contentPickListener.pickContent(getMimeType(), getMin(), getMax());
    }
  }

  public void setContentName(String fileName) {
    if (this.txtContentName != null) {
      this.txtContentName.setText(fileName);
    }
  }

  public void setHint(CharSequence hint) {
    if (this.txtTitle != null) {
      this.txtTitle.setHint(hint);
    }
  }

  @Override
  public void setTitle(CharSequence title) {
    if (this.txtTitle != null) {
      this.txtTitle.setText(title);
    }
  }

  @Override
  public void setError(CharSequence error) {
    super.setError(error);
    this.txtError.setText(error);
  }

  @Override
  public void setError(int error) {
    super.setError(error);
    this.txtError.setText(this.getContext().getString(error));
  }

  @Override
  protected boolean typeValidation() {
    return true;
  }

  @Nullable
  protected File getContent(InputStream inputStream, String fileName) {
    FileOutputStream outputStream = null;
    try {
      File outFile = new File(getContext().getCacheDir(), fileName);
      outputStream = new FileOutputStream(outFile);
      byte[] data = new byte[1024 * 4];
      int count;
      while ((count = inputStream.read(data)) != -1) {
        outputStream.write(data, 0, count);
      }
      return outFile;

    } catch (Exception e) {
      return null;
    } finally {
      try {
        outputStream.flush();
        outputStream.close();
        inputStream.close();
      } catch (Exception e) {
        //
      }
    }
  }

  @Nullable
  protected File loadContent(Uri uri) {
    try {
      InputStream inputStream = this.getContext().getContentResolver().openInputStream(uri);
      String fileName = uri.getLastPathSegment();
      return getContent(inputStream, fileName);
    } catch (Exception e) {
      return null;
    }
  }

  @Nullable
  protected File downloadContent(String path) {
    try {
      HttpURLConnection connection = (HttpURLConnection) new URL(path).openConnection();
      connection.setDoInput(true);
      connection.setRequestMethod("GET");

      String[] parts = path.split("/");
      String fileName = parts[parts.length - 1];
      BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream());
      return getContent(inputStream, fileName);

    } catch (Exception e) {
      return null;
    }
  }

  public interface ContentPickListener {
    void pickContent(String mimeType, int min, int max);
  }

}
