package ir.afraapps.form;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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
    LayoutInflater.from(context).inflate(R.layout.field_file_picker, this, true);
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


  public void saveContent(Uri uri) {
    if (uri != null) {
      (new Thread(() -> {
        try {
          InputStream inputStream;
          String fileName;
          if ("content".equals(uri.getScheme())) {
            inputStream = this.getContext().getContentResolver().openInputStream(uri);
            fileName = uri.getLastPathSegment();
          } else {
            File file = new File(uri.getPath());
            inputStream = new FileInputStream(file);
            fileName = file.getName();
          }

          byte[] data = new byte[inputStream.available()];
          //noinspection ResultOfMethodCallIgnored
          inputStream.read(data, 0, data.length);

          try {
            inputStream.close();
          } catch (IOException ex) {
            //
          }

          String base64 = Base64.encodeToString(data, 0);
          this.post(() -> {
            this.setError(null);
            setContentValue(base64, fileName);
          });
        } catch (Exception ex) {
          //
        }

      })).start();
    }
  }

  public interface ContentPickListener {
    void pickContent(String mimeType, int min, int max);
  }

}
