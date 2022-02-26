package ir.afraapps.form;


import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import ir.afraapps.basic.helper.UText;
import ir.afraapps.basic.helper.UTypeface;
import ir.afraapps.bcalendar.DayOutOfRangeException;
import ir.afraapps.bcalendar.PersianDate;
import ir.afraapps.view.numberpicker.NumberPicker;

public class DialogDateTimePicker extends SheetDialogBase implements View.OnClickListener, NumberPicker.OnValueChangeListener {
  private NumberPicker yearPicker;
  private NumberPicker monthPicker;
  private NumberPicker dayPicker;
  private TimePicker timePicker;
  private TextView titleView;
  private TextView doneView;
  private TextView cancelView;
  private OnValueChangedListener<Long> listener;
  private int minYear;
  private int maxYear;
  private long defaultDate;
  private int backgroundColor = -328966;


  public static void show(FragmentManager fragmentManager, int minYear, int maxYear, long defaultDate, int backgroundColor, OnValueChangedListener<Long> listener) {
    DialogDateTimePicker dialog = new DialogDateTimePicker();
    dialog.setOnValueChangedListener(listener);
    dialog.setMinYear(minYear);
    dialog.setMaxYear(maxYear);
    dialog.setDefaultDate(defaultDate);
    dialog.setCancelable(false);
    dialog.setBackgroundColor(backgroundColor);
    dialog.show(fragmentManager, "DialogDateTimePicker");
  }

  private boolean isLipYear(int year) {
    int y;
    if (year > 0) {
      y = year - 474;
    } else {
      y = 473;
    }

    return (y % 2820 + 474 + 38) * 682 % 2816 < 682;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  @Nullable
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.dialog_date_time_picker, container, false);
    this.yearPicker = root.findViewById(R.id.nmbr_count_down_year);
    this.monthPicker = root.findViewById(R.id.nmbr_count_down_month);
    this.dayPicker = root.findViewById(R.id.nmbr_count_down_day);
    this.timePicker = root.findViewById(R.id.dialog_time_picker);
    this.titleView = root.findViewById(R.id.txt_dialog_date_title);
    this.doneView = root.findViewById(R.id.txt_dialog_date_done);
    this.cancelView = root.findViewById(R.id.txt_dialog_date_cancel);
    root.findViewById(R.id.parent).setBackgroundColor(this.backgroundColor);
    Typeface typeface = UTypeface.get(R.font.sans_light);
    this.yearPicker.setTypeface(typeface);
    this.monthPicker.setTypeface(typeface);
    this.dayPicker.setTypeface(typeface);
    Calendar calendar = Calendar.getInstance();
    if (this.defaultDate != 0L) {
      calendar.setTimeInMillis(this.defaultDate * 1000L);
    }

    PersianDate persian = PersianDate.fromCalendar(calendar);
    this.fillDateTitleView(persian);
    this.yearPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
    this.monthPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
    this.dayPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
    this.yearPicker.setMinValue(this.minYear);
    this.yearPicker.setMaxValue(this.maxYear);
    this.yearPicker.setValue(persian.getYear());
    this.yearPicker.setDisplayedValues(this.getPersianYearDisplayedValues());
    this.monthPicker.setMinValue(1);
    this.monthPicker.setMaxValue(12);
    this.monthPicker.setValue(persian.getMonth());
    this.monthPicker.setDisplayedValues(this.getPersianMonthDisplayedValues());
    this.dayPicker.setMinValue(1);
    this.prepareDayPickerValueRange(this.monthPicker.getValue());
    this.dayPicker.setValue(persian.getDayOfMonth());
    this.yearPicker.setOnValueChangedListener(this);
    this.monthPicker.setOnValueChangedListener(this);
    this.dayPicker.setOnValueChangedListener(this);
    this.setHour(calendar.get(Calendar.HOUR_OF_DAY));
    this.setMinute(calendar.get(Calendar.MINUTE));
    this.doneView.setOnClickListener(this);
    this.cancelView.setOnClickListener(this);
    return root;
  }

  private String[] getPersianYearDisplayedValues() {
    int range = this.maxYear - this.minYear + 1;
    String[] displayedValues = new String[range];

    for (int i = 0; i < range; ++i) {
      displayedValues[i] = UText.formatNumber(this.minYear + i);
    }

    return displayedValues;
  }

  private String[] getPersianMonthDisplayedValues() {
    PersianDate persianDate = new PersianDate();
    String[] displayedValues = new String[12];

    for (int i = 0; i < 12; ++i) {
      persianDate.setMonth(1 + i);
      persianDate.setDayOfMonth(1);
      displayedValues[i] = UText.formatNumber(persianDate.getMonthName());
    }

    return displayedValues;
  }

  private String[] getPersianDayDisplayedValues(int maxDay) {
    String[] displayedValues = new String[maxDay];

    for (int i = 0; i < maxDay; ++i) {
      int day = 1 + i;
      displayedValues[i] = UText.formatNumber(day);
    }

    return displayedValues;
  }

  private int getHour() {
    return Build.VERSION.SDK_INT < 23 ? this.timePicker.getCurrentHour() : this.timePicker.getHour();
  }

  private void setHour(int hour) {
    if (Build.VERSION.SDK_INT < 23) {
      this.timePicker.setCurrentHour(hour);
    } else {
      this.timePicker.setHour(hour);
    }

  }

  private int getMinute() {
    return Build.VERSION.SDK_INT < 23 ? this.timePicker.getCurrentMinute() : this.timePicker.getMinute();
  }

  private void setMinute(int minute) {
    if (Build.VERSION.SDK_INT < 23) {
      this.timePicker.setCurrentMinute(minute);
    } else {
      this.timePicker.setMinute(minute);
    }

  }

  private void prepareDayPickerValueRange(int month) {
    if (month <= 6) {
      this.dayPicker.setDisplayedValues(null);
      this.dayPicker.setMaxValue(31);
      this.dayPicker.setDisplayedValues(this.getPersianDayDisplayedValues(31));
    } else if (month > 6 && month < 12) {
      this.dayPicker.setDisplayedValues(null);
      this.dayPicker.setMaxValue(30);
      this.dayPicker.setDisplayedValues(this.getPersianDayDisplayedValues(30));
    } else if (month == 12) {
      if (this.isLipYear(this.yearPicker.getValue())) {
        this.dayPicker.setDisplayedValues(null);
        this.dayPicker.setMaxValue(30);
        this.dayPicker.setDisplayedValues(this.getPersianDayDisplayedValues(30));
      } else {
        this.dayPicker.setDisplayedValues(null);
        this.dayPicker.setMaxValue(29);
        this.dayPicker.setDisplayedValues(this.getPersianDayDisplayedValues(29));
      }
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

  public void setBackgroundColor(int backgroundColor) {
    this.backgroundColor = backgroundColor;
  }

  public long getDefaultDate() {
    return this.defaultDate;
  }

  public void setDefaultDate(long defaultDate) {
    this.defaultDate = defaultDate;
  }

  public void setOnValueChangedListener(OnValueChangedListener<Long> listener) {
    this.listener = listener;
  }

  private void fillDateTitleView(PersianDate persian) {
    this.titleView.setText(persian.toString());
  }

  @Override
  public void onClick(View v) {
    if (v == this.doneView) {
      try {
        PersianDate persianDate = new PersianDate(this.yearPicker.getValue(), this.monthPicker.getValue(), this.dayPicker.getValue());
        Calendar calendar = persianDate.toCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, this.getHour());
        calendar.set(Calendar.MINUTE, this.getMinute());
        if (this.listener != null) {
          this.listener.onValueChanged(calendar.getTimeInMillis() / 1000L);
        }

        this.hideBottomSheet();
      } catch (DayOutOfRangeException var4) {
      }
    } else if (v == this.cancelView) {
      this.hideBottomSheet();
    }

  }

  @Override
  public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
    int dMax;
    int mMax;
    if (picker == this.monthPicker) {
      dMax = this.yearPicker.getMaxValue();
      mMax = this.yearPicker.getMinValue();
      if (oldVal == 12 && newVal == 1) {
        if (this.yearPicker.getValue() + 1 <= dMax) {
          this.yearPicker.setValue(this.yearPicker.getValue() + 1);
        }
      } else if (oldVal == 1 && newVal == 12 && this.yearPicker.getValue() - 1 >= mMax) {
        this.yearPicker.setValue(this.yearPicker.getValue() - 1);
      }

      this.prepareDayPickerValueRange(this.monthPicker.getValue());
    } else if (picker == this.dayPicker) {
      dMax = this.dayPicker.getMaxValue();
      mMax = this.monthPicker.getMaxValue();
      int mMin = this.monthPicker.getMinValue();
      int yMax = this.yearPicker.getMaxValue();
      int yMin = this.yearPicker.getMinValue();
      if (oldVal < newVal) {
        if (newVal == 2) {
          this.prepareDayPickerValueRange(this.monthPicker.getValue());
        }

        if (oldVal == 1 && newVal == dMax) {
          if (this.monthPicker.getValue() == mMin) {
            if (this.yearPicker.getValue() - 1 >= yMin) {
              this.monthPicker.setValue(mMax);
              this.yearPicker.setValue(this.yearPicker.getValue() - 1);
            }
          } else {
            this.monthPicker.setValue(this.monthPicker.getValue() - 1);
          }
        }
      } else {
        if (oldVal == 2) {
          if (this.monthPicker.getValue() == mMin) {
            this.prepareDayPickerValueRange(mMax);
          } else {
            this.prepareDayPickerValueRange(this.monthPicker.getValue() - 1);
          }
        }

        if (oldVal == dMax && newVal == 1) {
          if (this.monthPicker.getValue() == mMax) {
            if (this.yearPicker.getValue() + 1 <= yMax) {
              this.monthPicker.setValue(1);
              this.yearPicker.setValue(this.yearPicker.getValue() + 1);
            }
          } else {
            this.monthPicker.setValue(this.monthPicker.getValue() + 1);
          }
        }
      }
    }

    try {
      PersianDate persian = new PersianDate(this.yearPicker.getValue(), this.monthPicker.getValue(), this.dayPicker.getValue());
      this.fillDateTitleView(persian);
    } catch (DayOutOfRangeException var9) {
      this.titleView.setText("");
    }

  }
}

