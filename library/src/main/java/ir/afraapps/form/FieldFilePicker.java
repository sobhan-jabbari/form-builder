package ir.afraapps.form;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class FieldFilePicker extends FieldContentPicker {


  public FieldFilePicker(@NonNull Context context) {
    super(context);
  }

  public FieldFilePicker(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public FieldFilePicker(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    setPicLayout(R.layout.field_file_pic_layout);
  }

  @Override
  protected String getEmptyError() {
    return this.getContext().getString(R.string.field_picker_file_invalid_required);
  }

  @Override
  String getMimeType() {
    return "*/*";
  }

  @Override
  protected void onPrePickContent() {
  }

  @Override
  protected void setContentValue(String value, String name) {
    setValue(value + "," + name);
  }

  @Override
  public void setValue(String value) {
    super.setValue(value);
    if (value != null) {
      this.extractContentName(value);
    } else {
      setContentName(null);
    }
  }

  private void extractContentName(String base64) {
    if (!TextUtils.isEmpty(base64)) {
      (new Thread(() -> {
        String[] parts = base64.split(",");
        if (parts != null && parts.length > 1) {
          String fileName = parts[1];
          this.post(() -> this.setContentName(fileName));
        }
      })).start();
    }
  }
}
