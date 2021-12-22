package ir.afraapps.form;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import ir.afraapps.basic.helper.UColor;
import ir.afraapps.basic.helper.UMetric;
import ir.afraapps.basic.helper.UText;
import ir.afraapps.basic.helper.UTypeface;


public class FieldTextInput extends FormLayout {
  private static final String TAG = FieldTextInput.class.getSimpleName();
  private EditText editText;
  private ImageView passwordToggle;
  private TextView txtError;
  private TextView txtTitle;
  private int fieldInputType;
  private int inernalInputType;
  private List<TextWatcher> listeners;
  private TextWatcher textWatcher;

  public FieldTextInput(@NonNull Context context) {
    this(context, null, 0);
  }

  public FieldTextInput(@NonNull Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public FieldTextInput(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.textWatcher = this.getTextWatcher();
    this.init(context, attrs, defStyleAttr);
  }

  private void init(Context context, AttributeSet attrs, int defStyleAttr) {
    this.initViews(context);
    if (attrs != null) {
      TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.FieldTextInput, defStyleAttr, 0);
      if (this.getFormTint() == null) {
        this.setFormTint(ColorStateList.valueOf(UColor.getAccentColor()));
      }

      CharSequence hint = a.getText(R.styleable.FieldTextInput_android_hint);
      CharSequence text = a.getText(R.styleable.FieldTextInput_android_text);
      if (hint != null) {
        this.setTitle(hint.toString());
      }

      if (text != null) {
        this.setText(text);
      }

      this.fieldInputType = a.getInt(R.styleable.FieldTextInput_fieldInputType, 0);
      this.setFieldInputType(this.fieldInputType);
      int maxLength = a.getInt(R.styleable.FieldTextInput_android_maxLength, 0);
      if (maxLength > 0) {
        this.setMaxLength(maxLength);
      }

      if (a.hasValue(R.styleable.FieldTextInput_android_imeOptions)) {
        this.setImeOptions(a.getInt(R.styleable.FieldTextInput_android_imeOptions, 33554432));
      }

      if (a.hasValue(R.styleable.FieldTextInput_android_textColor)) {
        ColorStateList textColor = a.getColorStateList(R.styleable.FieldTextInput_android_textColor);
        if (textColor != null) {
          this.setTextColor(textColor);
        }
      }

      int bgResId = a.getResourceId(R.styleable.FieldTextInput_android_background, 0);
      if (bgResId != 0) {
        this.setBackgroundResource(bgResId);
      }

      int iconResId = a.getResourceId(R.styleable.FieldTextInput_srcCompat, 0);
      if (iconResId != 0) {
        this.setIcon(iconResId);
      }

      a.recycle();
    } else {
      this.setBackgroundResource(R.drawable.bg_input_normal);
      this.setIcon(0);
    }

    this.editText.setOnFocusChangeListener(this.getEditTextOnFocusChangeListener());
  }

  private void initViews(Context context) {
    LayoutInflater.from(context).inflate(R.layout.field_text_input, this, true);
    this.editText = this.findViewById(R.id.edt);
    this.passwordToggle = this.findViewById(R.id.img_password_toggle);
    this.txtError = this.findViewById(R.id.txt_error);
    this.txtTitle = this.findViewById(R.id.txt_title);
    this.passwordToggle.setClickable(true);
    this.editText.addTextChangedListener(this.textWatcher);
  }

  private OnFocusChangeListener getEditTextOnFocusChangeListener() {
    return (v, hasFocus) -> {
      if (hasFocus) {
        this.setError(null);
        this.txtTitle.setText(this.getTitle());
        this.editText.setHint(null);
      } else if (this.editText.getText() != null && this.editText.getText().length() > 0) {
        this.txtTitle.setText(this.getTitle());
        this.editText.setHint(null);
      } else {
        this.editText.setHint(this.getTitle());
        this.txtTitle.setText(null);
      }

    };
  }

  public void setIcon(@DrawableRes int resId) {
    this.setIcon(resId == 0 ? null : AppCompatResources.getDrawable(this.getContext(), resId));
  }

  public void setIcon(Drawable drawable) {
    if (drawable != null && this.getFormTint() != null) {
      DrawableCompat.setTintList(drawable, this.getFormTint());
    }

    this.setEditTextCompoundDrawables(drawable);
  }

  private void setEditTextCompoundDrawables(Drawable drawable) {
    this.editText.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
  }

  public void setFieldInputType(int type) {
    int inputType;
    switch (type) {
      case 0:
        inputType = 1;
        break;
      case 1:
        inputType = 129;
        break;
      case 2:
        inputType = 18;
        break;
      case 3:
        inputType = 33;
        break;
      case 4:
      case 7:
        inputType = 3;
        break;
      case 5:
      case 8:
        inputType = 2;
        break;
      case 6:
        inputType = 131073;
        break;
      default:
        inputType = 1;
    }

    this.setInternalInputType(inputType);
  }

  public void setTextColor(ColorStateList colors) {
    if (colors == null) {
      throw new NullPointerException();
    } else {
      this.editText.setTextColor(colors);
    }
  }

  public void setTextColor(@ColorInt int color) {
    this.editText.setTextColor(color);
  }

  public final ColorStateList getTextColors() {
    return this.editText.getTextColors();
  }

  private void setInternalInputType(int inputType) {
    this.inernalInputType = inputType;
    this.editText.setInputType(inputType);
    this.checkPasswordToggle();
  }

  public void setOnEditorActionListener(TextView.OnEditorActionListener editorActionListener) {
    if (this.editText != null) {
      this.editText.setOnEditorActionListener(editorActionListener);
    }

  }

  public void setMaxLength(int maxLength) {
    InputFilter[] filters = new InputFilter[]{new InputFilter.LengthFilter(maxLength)};
    this.editText.setFilters(filters);
  }

  public void setImeOptions(int imeOptions) {
    if (this.editText != null) {
      this.editText.setImeOptions(imeOptions);
    }

  }

  public int getFieldInputType() {
    return this.fieldInputType;
  }

  @Override
  public void setEnabled(boolean enabled) {
    if (this.editText != null) {
      this.editText.setEnabled(enabled);
    }

    if (this.passwordToggle != null) {
      this.passwordToggle.setEnabled(enabled);
    }

    if (this.txtError != null) {
      this.txtError.setEnabled(enabled);
    }

    if (this.txtTitle != null) {
      this.txtTitle.setEnabled(enabled);
    }

    super.setEnabled(enabled);
  }

  @SuppressLint({"ClickableViewAccessibility"})
  private void checkPasswordToggle() {
    if (this.isPassword()) {
      this.passwordToggle.setOnTouchListener((v, event) -> {
        Editable editable = this.editText.getText();
        switch (event.getAction()) {
          case 0:
            this.editText.setInputType(1);
            this.editText.setTypeface(UTypeface.get(R.font.sans));
            if (editable.length() > 0) {
              this.editText.setSelection(editable.length());
            }
            break;
          case 1:
          case 3:
            this.editText.setInputType(this.inernalInputType);
            this.editText.setTypeface(UTypeface.get(R.font.sans));
            if (editable.length() > 0) {
              this.editText.setSelection(editable.length());
            }
          case 2:
        }

        return false;
      });
      this.passwordToggle.setVisibility(VISIBLE);
    } else {
      this.passwordToggle.setVisibility(GONE);
    }

  }

  public boolean isNumberInput() {
    int variation = this.inernalInputType & 4095;
    return variation == 2;
  }

  public boolean isPassword() {
    int variation = this.inernalInputType & 4095;
    return variation == 129 || variation == 18 || variation == 225;
  }

  public boolean isMulltiLineText() {
    int variation = this.inernalInputType & 4095;
    return variation == 131073;
  }

  public void setHint(CharSequence hint) {
    this.editText.setHint(hint);
  }

  public void setHint(@StringRes int hint) {
    this.editText.setHint(hint);
  }

  public CharSequence getHint() {
    return this.editText.getHint();
  }

  public void setText(CharSequence text) {
    this.editText.setText(text);
    if (TextUtils.isEmpty(text)) {
      this.editText.setHint(this.getTitle());
      this.txtTitle.setText(null);
    } else {
      this.txtTitle.setText(this.getTitle());
      this.editText.setHint(null);
    }

  }

  public void setText(@StringRes int text) {
    this.setText(this.getContext().getString(text));
  }

  public CharSequence getText() {
    return this.editText.getText();
  }

  @Override
  public void setValue(String value) {
    super.setValue(value);
    if (this.editText != null && !TextUtils.equals(this.getText(), value)) {
      this.setText(value);
    }

  }

  @Override
  public void setTitle(String title) {
    super.setTitle(title);
    this.setHint(title);
  }

  @Override
  public void setError(String error) {
    super.setError(error);
    if (this.txtError != null) {
      this.txtError.setText(error);
      this.editText.setActivated(!TextUtils.isEmpty(error));
    }

  }

  @Override
  public void setError(CharSequence error) {
    super.setError(error);
    if (this.txtError != null) {
      this.txtError.setText(error);
      this.editText.setActivated(!TextUtils.isEmpty(error));
    }

  }

  @Override
  public void setError(@StringRes int error) {
    super.setError(error);
    if (this.txtError != null) {
      this.txtError.setText(error);
      this.editText.setActivated(error != 0);
    }

  }

  @Override
  protected boolean typeValidation() {
    switch (this.fieldInputType) {
      case 0:
      case 6:
        return this.isValidText();
      case 1:
      case 2:
        return this.isValidPassword();
      case 3:
        return this.isValidEmail();
      case 4:
        return this.isValidPhone();
      case 5:
      case 8:
        return this.isValidNumber();
      case 7:
        return this.isValidMobile();
      default:
        return true;
    }
  }

  @Override
  protected String getEmptyError() {
    return this.getContext().getString(R.string.field_invalid_required);
  }

  @Override
  public void setBackground(Drawable background) {
    if (this.editText != null) {
      this.editText.setBackground(background);
    }

  }

  @Override
  public void setBackgroundResource(int resid) {
    if (resid == 0) {
      resid = R.drawable.bg_input_normal;
      if (!this.isInEditMode()) {
        LayoutParams params = (LayoutParams) this.txtError.getLayoutParams();
        int margin = UMetric.toDIP(4);
        params.leftMargin = margin;
        params.rightMargin = margin;
        this.txtError.setLayoutParams(params);
      }
    }

    this.editText.setBackgroundResource(resid);
  }

  public void addTextChangedListener(TextWatcher listener) {
    if (this.listeners == null) {
      this.listeners = new ArrayList();
    }

    this.listeners.add(listener);
  }

  public void removeTextChangedListener(TextWatcher listener) {
    if (this.listeners != null) {
      int i = this.listeners.indexOf(listener);
      if (i >= 0) {
        this.listeners.remove(i);
      }
    }

  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    if (!this.isInEditMode() && this.editText != null) {
    }

  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
  }

  private TextWatcher getTextWatcher() {
    return new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (FieldTextInput.this.listeners != null) {

          for (TextWatcher watcher : FieldTextInput.this.listeners) {
            watcher.beforeTextChanged(s, start, count, after);
          }
        }

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        FieldTextInput.this.setError(null);
        if (FieldTextInput.this.listeners != null) {

          for (TextWatcher watcher : FieldTextInput.this.listeners) {
            watcher.onTextChanged(s, start, before, count);
          }
        }

      }

      @Override
      public void afterTextChanged(Editable s) {
        if (FieldTextInput.this.fieldInputType == 8) {
          CharSequence target = s.subSequence(0, s.length());
          boolean hasZero = false;
          if (TextUtils.isDigitsOnly(s) && s.length() > 1) {
            while (target.length() > 1 && target.toString().startsWith("0")) {
              hasZero = true;
              target = target.subSequence(1, s.length());
            }
          }

          if (hasZero) {
            s.replace(0, s.length(), target);
          }
        }

        FieldTextInput.this.setValue(s == null ? null : s.toString());
        if (FieldTextInput.this.listeners != null) {

          for (TextWatcher watcher : FieldTextInput.this.listeners) {
            watcher.afterTextChanged(s);
          }
        }

      }
    };
  }

  private boolean isValidText() {
    String text = this.getValue();
    if (this.getMin() > 0 && text.length() < this.getMin()) {
      this.setError(this.getContext().getString(R.string.field_text_invalid_min, UText.formatNumber(this.getMin())));
      return false;
    } else if (this.getMax() > 0 && text.length() > this.getMax()) {
      this.setError(this.getContext().getString(R.string.field_text_invalid_max, UText.formatNumber(this.getMax())));
      return false;
    } else {
      return true;
    }
  }

  private boolean isValidPassword() {
    return this.isValidText();
  }

  private boolean isValidEmail() {
    boolean isValidEmail = UText.isValidEmail(this.getValue());
    if (!isValidEmail) {
      this.setError(this.getContext().getString(R.string.field_text_invalid_mail));
    }

    return isValidEmail;
  }

  private boolean isValidPhone() {
    boolean isValidPhoneNumber = UText.isValidAllPhoneNumber(this.getValue());
    if (!isValidPhoneNumber) {
      this.setError(this.getContext().getString(R.string.field_number_invalid_phone));
    }

    return isValidPhoneNumber;
  }

  private boolean isValidMobile() {
    boolean isValidMobile = UText.isValidMobileNumber(this.getValue());
    if (!isValidMobile) {
      this.setError(this.getContext().getString(R.string.field_number_invalid_mobile));
    }

    return isValidMobile;
  }

  private boolean isValidNumber() {
    String value = this.getValue();
    if (value != null && TextUtils.isDigitsOnly(value)) {
      int number = Integer.parseInt(this.getValue());
      if (this.getMin() > 0 && number < this.getMin()) {
        this.setError(this.getContext().getString(R.string.field_number_invalid_min, UText.formatNumber(this.getMin())));
        return false;
      } else if (this.getMax() > 0 && number > this.getMax()) {
        this.setError(this.getContext().getString(R.string.field_number_invalid_max, UText.formatNumber(this.getMax())));
        return false;
      } else {
        return true;
      }
    } else {
      return false;
    }
  }

  public static class Type {
    public static final int TEXT = 0;
    public static final int TEXT_PASSWORD = 1;
    public static final int NUMBER_PASSWORD = 2;
    public static final int EMAIL = 3;
    public static final int PHONE = 4;
    public static final int NUMBER = 5;
    public static final int TEXT_MULTI_LINE = 6;
    public static final int MOBILE = 7;
    public static final int NUMBER_PURE = 8;

    public Type() {
    }
  }

  class PreventFirstZeroInputFilter implements InputFilter {
    PreventFirstZeroInputFilter() {
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
      if (source == null) {
        return null;
      } else {
        CharSequence target = source.subSequence(0, source.length());
        if (TextUtils.isDigitsOnly(source) && source.length() > 1) {
          while (target.length() > 0 && target.toString().startsWith("0")) {
            target.subSequence(1, source.length());
          }
        }

        Log.i("textInput", "filtered text: " + target);
        return target;
      }
    }
  }
}
