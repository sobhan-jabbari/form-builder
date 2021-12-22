package ir.afraapps.form;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;


public class DialogTimePicker extends SheetDialogBase implements View.OnClickListener {
  private static final String TAG = "DialogTimePicker";
  private TimePicker timePicker;
  private OnValueChangedListener<Clock> listener;
  private Clock defaultTime;
  private TextView btnDone;
  private TextView btnCancel;
  private int backgroundColor = -328966;

  public DialogTimePicker() {
  }

  public static void show(FragmentManager fragmentManager, Clock defaultTime, int backgroundColor, OnValueChangedListener<Clock> listener) {
    DialogTimePicker dialog = new DialogTimePicker();
    dialog.setOnValueChangedListener(listener);
    dialog.setDefaultTime(defaultTime);
    dialog.setCancelable(false);
    dialog.setBackgroundColor(backgroundColor);
    dialog.show(fragmentManager, "DialogTimePicker");
  }

  @Override
  @Nullable
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.dialog_time_picker, container, false);
    this.timePicker = root.findViewById(R.id.dialog_time_picker);
    root.findViewById(R.id.parent).setBackgroundColor(this.backgroundColor);
    if (this.defaultTime != null) {
      this.setHour(this.defaultTime.getHour());
      this.setMinute(this.defaultTime.getMinute());
    }

    this.btnDone = root.findViewById(R.id.txt_dialog_time_done);
    this.btnCancel = root.findViewById(R.id.txt_dialog_time_cancel);
    this.btnDone.setOnClickListener(this);
    this.btnCancel.setOnClickListener(this);
    return root;
  }

  public void setOnValueChangedListener(OnValueChangedListener<Clock> listener) {
    this.listener = listener;
  }

  public Clock getDefaultTime() {
    return this.defaultTime;
  }

  public void setDefaultTime(Clock defaultTime) {
    this.defaultTime = defaultTime;
  }

  public void setBackgroundColor(int backgroundColor) {
    this.backgroundColor = backgroundColor;
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

  @Override
  public void onClick(View v) {
    if (v == this.btnDone) {
      Clock clock = new Clock(this.getHour(), this.getMinute());
      if (this.listener != null) {
        this.listener.onValueChanged(clock);
      }

      this.hideBottomSheet();
    } else if (v == this.btnCancel) {
      this.hideBottomSheet();
    }

  }
}
