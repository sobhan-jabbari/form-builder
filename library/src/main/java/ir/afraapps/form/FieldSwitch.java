package ir.afraapps.form;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class FieldSwitch extends FieldCompound {
  public FieldSwitch(@NonNull Context context) {
    super(context);
  }

  public FieldSwitch(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public FieldSwitch(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected boolean typeValidation() {
    return true;
  }

  @Override
  protected CharSequence getEmptyError() {
    return this.getContext().getString(R.string.field_invalid_required);
  }

  @Override
  public void initViews(Context context) {
    LayoutInflater.from(context).inflate(R.layout.field_switch, this, true);
  }
}
