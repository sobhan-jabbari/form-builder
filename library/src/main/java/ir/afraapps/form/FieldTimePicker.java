package ir.afraapps.form;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;


public class FieldTimePicker extends FormLayout implements OnValueChangedListener<Clock> {
  private TextView txtTitle;
  private TextView txtValue;
  private TextView txtError;
  private ImageView iconView;
  private int dialogBackgroundColor;
  private Clock time;
  private FieldTimePicker.OnTimePickedListener onTimePickedListener;

  public FieldTimePicker(@NonNull Context context) {
    this(context, null, 0);
  }

  public FieldTimePicker(@NonNull Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public FieldTimePicker(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.dialogBackgroundColor = -328966;
    this.init(context, attrs, defStyleAttr);
  }

  private void init(Context context, AttributeSet attrs, int defStyleAttr) {
    this.initViews(context);
    if (attrs != null) {
      TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.FieldTimePicker, defStyleAttr, 0);
      CharSequence hint = a.getText(R.styleable.FieldTimePicker_android_hint);
      this.setTitle(hint == null ? null : hint.toString());
      this.dialogBackgroundColor = a.getColor(R.styleable.FieldDatePicker_dialogBackgroundColor, this.dialogBackgroundColor);
      int id = a.getResourceId(R.styleable.FieldTimePicker_srcCompat, -1);
      if (id != -1) {
        this.setIcon(id);
      }

      a.recycle();
    }

  }

  private void initViews(Context context) {
    LayoutInflater.from(context).inflate(R.layout.field_time_picker, this, true);
    this.txtTitle = this.findViewById(R.id.txt_title);
    this.txtValue = this.findViewById(R.id.txt_value);
    this.txtError = this.findViewById(R.id.txt_error);
    this.iconView = this.findViewById(R.id.imgIcon);
    this.findViewById(R.id.parent).setOnClickListener((v) -> this.prepareTimePicker());
  }

  private void prepareTimePicker() {
    if (this.getContext() instanceof AppCompatActivity) {
      AppCompatActivity activity = (AppCompatActivity) this.getContext();
      DialogTimePicker.show(activity.getSupportFragmentManager(), this.time, this.dialogBackgroundColor, this);
    }
  }

  public void setTime(String value) {
    String[] parts = value.split(":");
    this.setTime(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
  }

  public void setTime(int hour, int minute) {
    this.time = new Clock(hour, minute);
    this.updateLabel();
  }

  public void setTime(Clock time) {
    this.time = time;
    this.updateLabel();
  }

  public void setTime(long time) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(time * 1000L);
    this.time = new Clock(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
    this.updateLabel();
  }

  public String getTime() {
    return this.time == null ? null : this.time.getHour() + ":" + this.time.getMinute();
  }

  public long getTimeInMillis() {
    Calendar calendar = Calendar.getInstance();
    if (this.time == null) {
      return calendar.getTimeInMillis() / 1000L;
    } else {
      calendar.set(Calendar.HOUR_OF_DAY, this.time.getHour());
      calendar.set(Calendar.MINUTE, this.time.getMinute());
      return calendar.getTimeInMillis() / 1000L;
    }
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

  public void setOnTimePickedListener(FieldTimePicker.OnTimePickedListener onTimePickedListener) {
    this.onTimePickedListener = onTimePickedListener;
  }

  private void updateLabel() {
    this.txtValue.setText(this.time.getHour() + ":" + this.time.getMinute());
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
    return this.getContext().getString(R.string.field_picker_time_invalid_required);
  }

  @Override
  public void setValue(String value) {
    super.setValue(value);
    if (value != null) {
      this.setTime(value);
    }

  }

  @Override
  public void onValueChanged(Clock time) {
    this.setError(null);
    this.setValue(time.getHour() + ":" + time.getMinute());
    if (this.onTimePickedListener != null) {
      this.onTimePickedListener.onDatePicked(time);
    }

  }

  public interface OnTimePickedListener {
    void onDatePicked(Clock var1);
  }
}
