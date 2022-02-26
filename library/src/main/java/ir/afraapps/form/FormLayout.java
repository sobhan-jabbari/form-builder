package ir.afraapps.form;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import java.util.concurrent.atomic.AtomicBoolean;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

public abstract class FormLayout extends FrameLayout {
  private static final String TAG = "FormLayout";
  protected static final boolean D = false;
  private String oldValue;
  private String value;
  private String title;
  private String error;
  private boolean isRequired;
  private int min;
  private int max;
  private String color;
  private String regex;
  private ColorStateList formTint;
  private FormLayout.OnValueChangedListener onValueChangedListener;
  private final AtomicBoolean isFirst;

  public FormLayout(@NonNull Context context) {
    super(context, null);
    this.isFirst = new AtomicBoolean(true);
  }

  public FormLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public FormLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.isFirst = new AtomicBoolean(true);
    this.init(context, attrs, defStyleAttr);
  }

  private void init(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    if (attrs != null) {
      TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.FormLayout, defStyleAttr, 0);
      this.isRequired = a.getBoolean(R.styleable.FormLayout_isRequired, false);
      if (a.hasValue(R.styleable.FormLayout_formTint)) {
        this.formTint = a.getColorStateList(R.styleable.FormLayout_formTint);
      }

      a.recycle();
    }

  }

  public ColorStateList getFormTint() {
    return this.formTint;
  }

  public void setFormTint(ColorStateList formTint) {
    this.formTint = formTint;
  }

  public void setOnValueChangedListener(FormLayout.OnValueChangedListener onValueChangedListener) {
    this.onValueChangedListener = onValueChangedListener;
  }

  public String getValue() {
    return this.value;
  }

  public void setValue(String value) {
    this.value = value;
    if (this.onValueChangedListener != null) {
      this.onValueChangedListener.onValueChanged(value);
    }

    if (this.isFirst.compareAndSet(true, false)) {
      this.oldValue = value;
    }

  }

  public String getTitle() {
    return this.title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getError() {
    return this.error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public void setError(CharSequence error) {
    this.error = error == null ? null : error.toString();
  }

  public void setError(@StringRes int error) {
    if (error == 0) {
      this.error = null;
    } else {
      this.error = this.getContext().getString(error);
    }

  }

  public boolean isRequired() {
    return this.isRequired;
  }

  public void setRequired(boolean required) {
    this.isRequired = required;
  }

  public int getMin() {
    return this.min;
  }

  public void setMin(int min) {
    this.min = min;
  }

  public int getMax() {
    return this.max;
  }

  public void setMax(int max) {
    this.max = max;
  }

  public String getColor() {
    return this.color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public String getRegex() {
    return this.regex;
  }

  public void setRegex(String regex) {
    this.regex = regex;
  }

  public boolean hasNewValue() {
    return !TextUtils.equals(this.value, this.oldValue);
  }

  public boolean valueIsEmpty() {
    return TextUtils.isEmpty(this.getValue());
  }

  public boolean isValid() {
    if (this.isRequired) {
      if (this.valueIsEmpty()) {
        this.setError(this.getEmptyError());
        return false;
      } else {
        return this.regexValidation() && this.typeValidation();
      }
    } else {
      return this.valueIsEmpty() || this.regexValidation() && this.typeValidation();
    }
  }

  protected boolean regexValidation() {
    return this.regex == null || this.getValue().matches(this.regex);
  }

  protected abstract boolean typeValidation();

  protected abstract String getEmptyError();

  public interface OnValueChangedListener {
    void onValueChanged(String var1);
  }
}
