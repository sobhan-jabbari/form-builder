package ir.afraapps.form;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.LayoutInflater;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class FieldFilePicker extends FormLayout implements OnActivityResultListener {
  private static final String TAG = "FieldFilePicker";
  private static final boolean D = false;
  private static final int REQUEST_CODE = 160;
  private TextView txtTitle;
  private TextView txtFileName;
  private TextView txtError;
  private Fragment fragment;

  public FieldFilePicker(@NonNull Context context) {
    this(context, null, 0);
  }

  public FieldFilePicker(@NonNull Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public FieldFilePicker(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.init(context, attrs, defStyleAttr);
  }

  private void init(Context context, AttributeSet attrs, int defStyleAttr) {
    this.initViews(context);
    if (attrs != null) {
      TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.FieldFilePicker, defStyleAttr, 0);
      if (a.hasValue(R.styleable.FieldFilePicker_android_hint)) {
        CharSequence hint = a.getText(R.styleable.FieldFilePicker_android_hint);
        this.setTitle(hint.toString());
      }

      a.recycle();
    }

  }

  private void initViews(Context context) {
    LayoutInflater.from(context).inflate(R.layout.field_file_picker, this, true);
    this.txtTitle = this.findViewById(R.id.txt_title);
    this.txtError = this.findViewById(R.id.txt_error);
    this.txtFileName = this.findViewById(R.id.txt_file_name);
    this.findViewById(R.id.img_add_file).setOnClickListener((v) -> {
      this.prepareFilePicker();
    });
  }

  private void prepareFilePicker() {
    if (this.fragment != null) {
      this.prepareFragmentFilePicker();
    } else if (this.getContext() instanceof Activity) {
      this.prepareActivityFilePicker((Activity)this.getContext());
    }

  }

  private void prepareFragmentFilePicker() {
    Intent intent = new Intent("android.intent.action.GET_CONTENT");
    intent.setType("*/*");
    this.fragment.startActivityForResult(intent, 160);
  }

  private void prepareActivityFilePicker(Activity activity) {
    Intent intent = new Intent("android.intent.action.GET_CONTENT");
    intent.setType("*/*");
    activity.startActivityForResult(intent, 160);
  }

  public void setFragment(Fragment fragment) {
    this.fragment = fragment;
  }

  private void setFileName(String base64) {
    if (!TextUtils.isEmpty(base64)) {
      (new Thread(() -> {
        String[] parts = base64.split(",");
        if (parts != null && parts.length > 1) {
          String fileName = parts[1];
          this.post(() -> {
            this.txtFileName.setText(fileName);
          });
        }

      })).start();
    }
  }

  @Override
  public void setTitle(String title) {
    if (this.txtTitle != null) {
      this.txtTitle.setText(title);
    }

  }

  @Override
  public void setError(String error) {
    super.setError(error);
    this.txtError.setText(error);
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

  @Override
  protected String getEmptyError() {
    return this.getContext().getString(R.string.field_picker_file_invalid_required);
  }

  @Override
  public void setValue(String value) {
    super.setValue(value);
    if (value != null) {
      this.setFileName(value);
    }

  }

  private void saveFile(Uri fileUri) {
    if (fileUri != null) {
      (new Thread(() -> {
        try {
          InputStream inputStream;
          String fileName;
          if ("content".equals(fileUri.getScheme())) {
            inputStream = this.getContext().getContentResolver().openInputStream(fileUri);
            fileName = fileUri.getLastPathSegment();
          } else {
            File file = new File(fileUri.getPath());
            inputStream = new FileInputStream(file);
            fileName = file.getName();
          }

          byte[] data = new byte[inputStream.available()];
          inputStream.read(data, 0, data.length);

          try {
            inputStream.close();
          } catch (IOException var6) {
          }

          String base64 = Base64.encodeToString(data, 0);
          this.post(() -> {
            this.setError(null);
            this.setValue(base64 + "," + fileName);
          });
        } catch (Exception var7) {
        }

      })).start();
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == 160 && resultCode == -1 && data != null) {
      this.saveFile(data.getData());
    }

  }
}
