package ir.afraapps.form;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import ir.afraapps.bcalendar.PersianDate;

public class FieldDateTimePicker extends FormLayout implements OnValueChangedListener<Long> {
  private TextView txtTitle;
  private TextView txtValue;
  private TextView txtError;
  private ImageView iconView;
  private ViewGroup lytParent;
  private int minYear;
  private int maxYear;
  private long date;
  private int dialogBackgroundColor;
  private FieldDateTimePicker.OnDateTimePickedListener onDateTimePickedListener;

  public FieldDateTimePicker(@NonNull Context context) {
    this(context, null, 0);
  }

  public FieldDateTimePicker(@NonNull Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public FieldDateTimePicker(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.dialogBackgroundColor = -328966;
    this.init(context, attrs, defStyleAttr);
  }

  private void init(Context context, AttributeSet attrs, int defStyleAttr) {
    this.initViews(context);
    if (attrs != null) {
      TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.FieldDatePicker, defStyleAttr, 0);
      CharSequence hint = a.getText(R.styleable.FieldDatePicker_android_hint);
      this.setTitle(hint == null ? null : hint.toString());
      this.minYear = a.getInt(R.styleable.FieldDatePicker_android_min, 0);
      this.maxYear = a.getInt(R.styleable.FieldDatePicker_android_max, 0);
      this.dialogBackgroundColor = a.getColor(R.styleable.FieldDatePicker_dialogBackgroundColor, this.dialogBackgroundColor);
      int id = a.getResourceId(R.styleable.FieldDatePicker_srcCompat, -1);
      if (id != -1) {
        this.setIcon(id);
      }

      a.recycle();
    }

  }

  private void initViews(Context context) {
    LayoutInflater.from(context).inflate(R.layout.field_date_picker, this, true);
    this.lytParent = this.findViewById(R.id.field_parent);
    this.txtTitle = this.findViewById(R.id.txt_title);
    this.txtValue = this.findViewById(R.id.txt_value);
    this.txtError = this.findViewById(R.id.txt_error);
    this.iconView = this.findViewById(R.id.imgIcon);
    this.findViewById(R.id.parent).setOnClickListener((v) -> this.prepareDatePicker());
  }

  private void prepareDatePicker() {
    PersianDate persianDate = this.date == 0L ? new PersianDate() : PersianDate.fromCalendarTime(this.date);
    if (this.minYear == 0) {
      this.minYear = persianDate.getYear() - 50;
    }

    if (this.maxYear == 0) {
      this.maxYear = persianDate.getYear() + 50;
    }

    if (this.getContext() instanceof AppCompatActivity) {
      AppCompatActivity activity = (AppCompatActivity) this.getContext();
      DialogDateTimePicker.show(activity.getSupportFragmentManager(), this.minYear, this.maxYear, this.date / 1000L, this.dialogBackgroundColor, this);
    }

  }

  public void setDate(String value) {
    long time = Long.valueOf(value);
    this.setDate(time);
  }

  public void setDate(long time) {
    if (this.date != time * 1000L) {
      this.date = time * 1000L;
      if (this.getValue() == null) {
        this.setValue("" + time);
      }

      this.updateLabel();
    }
  }

  public int getMinYear() {
    return this.minYear;
  }

  public void setMinYear(int minYear) {
    this.minYear = minYear;
  }

  public int getMaxYear() {
    return this.maxYear;
  }

  public void setMaxYear(int maxYear) {
    this.maxYear = maxYear;
  }

  public long getDate() {
    return this.date / 1000L;
  }

  public void setIcon(@NonNull Drawable drawable) {
    this.iconView.setImageDrawable(drawable);
  }

  public void setIcon(@DrawableRes int resId) {
    Drawable drawable = AppCompatResources.getDrawable(this.getContext(), resId);
    if (drawable != null) {
      this.iconView.setImageDrawable(drawable);
    }

  }

  public void setOnDateTimePickedListener(FieldDateTimePicker.OnDateTimePickedListener onDateTimePickedListener) {
    this.onDateTimePickedListener = onDateTimePickedListener;
  }

  private void updateLabel() {
    PersianDate persian = PersianDate.fromCalendarTime(this.date);
    this.txtValue.setText(persian.toString());
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
    this.lytParent.setActivated(!TextUtils.isEmpty(error));
  }

  @Override
  public void setError(CharSequence error) {
    super.setError(error);
    this.txtError.setText(error);
    this.lytParent.setActivated(!TextUtils.isEmpty(error));
  }

  @Override
  public void setError(int error) {
    super.setError(error);
    this.txtError.setText(this.getContext().getString(error));
    this.lytParent.setActivated(error != 0);
  }

  @Override
  protected boolean typeValidation() {
    return true;
  }

  @Override
  protected String getEmptyError() {
    return this.getContext().getString(R.string.field_picker_date_invalid_required);
  }

  @Override
  public void setValue(String value) {
    super.setValue(value);
    if (value != null) {
      this.setDate(value);
    }

  }

  @Override
  public void onValueChanged(Long time) {
    this.setError(null);
    this.setValue(time == null ? "0" : "" + time);
    if (this.onDateTimePickedListener != null) {
      this.onDateTimePickedListener.onDateTimePicked(time == null ? 0L : time);
    }

  }

  public interface OnDateTimePickedListener {
    void onDateTimePicked(Long var1);
  }
}
