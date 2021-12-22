package ir.afraapps.form;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import ir.afraapps.basic.helper.UText;
import ir.afraapps.basic.helper.UTypeface;
import ir.afraapps.bcalendar.DayOutOfRangeException;
import ir.afraapps.bcalendar.PersianDate;
import ir.afraapps.view.numberpicker.NumberPicker;


public class DialogDatePicker extends SheetDialogBase implements View.OnClickListener, NumberPicker.OnValueChangeListener {
  private static final String TAG = "DialogDatePicker";
  private static final boolean D = false;
  private NumberPicker yearPicker;
  private NumberPicker monthPicker;
  private NumberPicker dayPicker;
  private TextView titleView;
  private TextView doneView;
  private TextView cancelView;
  private OnValueChangedListener<PersianDate> listener;
  private int minYear;
  private int maxYear;
  private PersianDate defaultDate;
  private int backgroundColor = -328966;

  public DialogDatePicker() {
  }

  public static void show(FragmentManager fragmentManager, int minYear, int maxYear, PersianDate defaultDate, int backgroundColor, OnValueChangedListener<PersianDate> listener) {
    DialogDatePicker dialog = new DialogDatePicker();
    dialog.setOnValueChangedListener(listener);
    dialog.setMinYear(minYear);
    dialog.setMaxYear(maxYear);
    dialog.setDefaultDate(defaultDate);
    dialog.setCancelable(false);
    dialog.setBackgroundColor(backgroundColor);
    dialog.show(fragmentManager, "DialogDatePicker");
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
    View root = inflater.inflate(R.layout.dialog_date_picker, container, false);
    this.yearPicker = root.findViewById(R.id.nmbr_count_down_year);
    this.monthPicker = root.findViewById(R.id.nmbr_count_down_month);
    this.dayPicker = root.findViewById(R.id.nmbr_count_down_day);
    this.titleView = root.findViewById(R.id.txt_dialog_date_title);
    this.doneView = root.findViewById(R.id.txt_dialog_date_done);
    this.cancelView = root.findViewById(R.id.txt_dialog_date_cancel);
    View sheetView = root.findViewById(R.id.parent);
    sheetView.setBackgroundColor(this.backgroundColor);
    Typeface typeface = UTypeface.get(R.font.sans_light);
    this.yearPicker.setTypeface(typeface);
    this.monthPicker.setTypeface(typeface);
    this.dayPicker.setTypeface(typeface);
    PersianDate persian = this.defaultDate == null ? new PersianDate() : this.defaultDate;
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

  public PersianDate getDefaultDate() {
    return this.defaultDate;
  }

  public void setDefaultDate(PersianDate defaultDate) {
    this.defaultDate = defaultDate;
  }

  public void setOnValueChangedListener(OnValueChangedListener<PersianDate> listener) {
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
        if (this.listener != null) {
          this.listener.onValueChanged(persianDate);
        }

        this.hideBottomSheet();
      } catch (DayOutOfRangeException var3) {
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
