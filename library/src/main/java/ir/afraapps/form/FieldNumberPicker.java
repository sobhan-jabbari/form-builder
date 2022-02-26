package ir.afraapps.form;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ir.afraapps.basic.helper.UText;
import ir.afraapps.basic.helper.UTypeface;
import ir.afraapps.view.numberpicker.NumberPicker;


public class FieldNumberPicker extends FormLayout {
  private TextView txtTitle;
  private NumberPicker numberPicker;
  private boolean isScrolling;
  private boolean valueChanged;
  private final NumberPicker.OnValueChangeListener onValueChangeListener;
  private final NumberPicker.OnScrollListener onScrollListener;

  public FieldNumberPicker(@NonNull Context context) {
    this(context, null, 0);
  }

  public FieldNumberPicker(@NonNull Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public FieldNumberPicker(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.onValueChangeListener = this.getOnValueChangedListener();
    this.onScrollListener = this.getOnScrollListener();
    this.init(context, attrs, defStyleAttr);
  }

  private void init(Context context, AttributeSet attrs, int defStyleAttr) {
    this.initViews(context);
    if (attrs != null) {
      TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.FieldNumberPicker, defStyleAttr, 0);
      int min = a.getInt(R.styleable.FieldNumberPicker_android_min, 0);
      int max = a.getInt(R.styleable.FieldNumberPicker_android_max, 24);
      this.setMin(min);
      this.setMax(max);
      a.recycle();
    }

    this.numberPicker.setOnValueChangedListener(this.onValueChangeListener);
    this.numberPicker.setOnScrollListener(this.onScrollListener);
  }

  private void initViews(Context context) {
    LayoutInflater.from(context).inflate(R.layout.field_number_picker, this, true);
    this.txtTitle = this.findViewById(R.id.txt_title);
    this.numberPicker = this.findViewById(R.id.number_picker);
    this.numberPicker.setTypeface(UTypeface.getSansLight());
  }

  private void prepareNumberPicker(int minValue, int maxValue) {
    this.setMinValue(minValue);
    this.setMaxValue(maxValue);
    this.numberPicker.setDisplayedValues(this.getDisplayedValues(minValue, maxValue));
    if (this.getValue() != null) {
      this.numberPicker.setValue(Integer.parseInt(this.getValue()));
    }

  }

  private NumberPicker.OnValueChangeListener getOnValueChangedListener() {
    return (picker, oldVal, newVal) -> this.valueChanged = true;
  }

  private NumberPicker.OnScrollListener getOnScrollListener() {
    return (view, scrollState) -> {
      if (scrollState != 0) {
        this.isScrolling = true;
      } else {
        this.isScrolling = false;
        this.numberPicker.postDelayed(this::onChangedValue, 400L);
      }
    };
  }

  private void onChangedValue() {
    if (!this.isScrolling && this.valueChanged) {
      this.valueChanged = false;
      this.setError(null);
      this.setValue(Integer.toString(this.numberPicker.getValue()));
    }
  }

  private String[] getDisplayedValues(int min, int max) {
    int range = max - min + 1;
    String[] displayedValues = new String[range];

    for (int i = 0; i < range; ++i) {
      displayedValues[i] = UText.formatNumber(min + i);
    }

    return displayedValues;
  }

  public void setMinValue(int minValue) {
    if (this.numberPicker != null) {
      this.numberPicker.setMinValue(minValue);
    }

  }

  public void setMaxValue(int maxValue) {
    if (this.numberPicker != null) {
      this.numberPicker.setMaxValue(maxValue);
    }

  }

  @Override
  public void setMin(int min) {
    super.setMin(min);
    this.prepareNumberPicker(min, this.getMax());
  }

  @Override
  public void setMax(int max) {
    super.setMax(max);
    this.prepareNumberPicker(this.getMin(), max);
  }

  @Override
  protected boolean typeValidation() {
    return this.isValidNumber();
  }

  @Override
  protected String getEmptyError() {
    return this.getContext().getString(R.string.field_invalid_required);
  }

  @Override
  public void setTitle(String title) {
    if (this.txtTitle != null) {
      this.txtTitle.setText(title);
    }

  }

  private boolean isValidNumber() {
    int number = Integer.parseInt(this.getValue());
    if (this.getMin() > 0 && number < this.getMin()) {
      this.setError(this.getContext().getString(R.string.field_number_invalid_max, UText.formatNumber(this.getMin())));
      return false;
    } else if (this.getMax() > 0 && number > this.getMax()) {
      this.setError(this.getContext().getString(R.string.field_number_invalid_max, UText.formatNumber(this.getMax())));
      return false;
    } else {
      return true;
    }
  }
}
