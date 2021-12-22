package ir.afraapps.form;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class FieldCheckBox extends FieldCompound {
  public FieldCheckBox(@NonNull Context context) {
    super(context);
  }

  public FieldCheckBox(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public FieldCheckBox(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected boolean typeValidation() {
    return true;
  }

  @Override
  protected String getEmptyError() {
    return this.getContext().getString(R.string.field_invalid_required);
  }

  @Override
  public void initViews(Context context) {
    LayoutInflater.from(context).inflate(R.layout.field_checkbox, this, true);
  }
}