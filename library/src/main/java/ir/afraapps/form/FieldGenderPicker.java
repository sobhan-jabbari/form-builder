package ir.afraapps.form;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ir.afraapps.basic.helper.UView;


public class FieldGenderPicker extends FormLayout {
  public static final int GENDER_UNKNOWN = 0;
  public static final int GENDER_MALE = 1;
  public static final int GENDER_FEMALE = 2;
  private ViewGroup viewUnknown;
  private ViewGroup viewMale;
  private ViewGroup viewFemale;
  private ViewGroup selectedView;
  private int selectedGender;
  private FieldGenderPicker.OnGenderDetectListener onGenderDetectedListener;

  public FieldGenderPicker(@NonNull Context context) {
    this(context, null, 0);
  }

  public FieldGenderPicker(@NonNull Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public FieldGenderPicker(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.init(context);
  }

  private void init(Context context) {
    this.initViews(context);
    this.setGender(GENDER_UNKNOWN);
  }

  private void initViews(Context context) {
    LayoutInflater.from(context).inflate(R.layout.field_gender_picker, this, true);
    this.viewUnknown = this.findViewById(R.id.lytUnknown);
    this.viewMale = this.findViewById(R.id.lytMale);
    this.viewFemale = this.findViewById(R.id.lytFemale);
    this.viewUnknown.setOnClickListener((v) -> this.onValueChanged(GENDER_UNKNOWN));
    this.viewMale.setOnClickListener((v) -> this.onValueChanged(GENDER_MALE));
    this.viewFemale.setOnClickListener((v) -> this.onValueChanged(GENDER_FEMALE));
  }

  public void setGender(int gender) {
    this.selectedGender = gender;
    this.setSelectedView(gender);
  }

  public void setGender(@NonNull String gender) {
    this.setGender(Integer.parseInt(gender));
  }

  public int getGender() {
    return this.selectedGender;
  }

  private void setSelectedView(int gender) {
    this.resetSelectedView();
    switch (gender) {
      case GENDER_UNKNOWN:
        this.viewUnknown.setSelected(true);
        UView.selectViewChildes(this.viewUnknown, true);
        this.selectedView = this.viewUnknown;
        break;
      case GENDER_MALE:
        this.viewMale.setSelected(true);
        UView.selectViewChildes(this.viewMale, true);
        this.selectedView = this.viewMale;
        break;
      case GENDER_FEMALE:
        this.viewFemale.setSelected(true);
        UView.selectViewChildes(this.viewFemale, true);
        this.selectedView = this.viewFemale;
    }

  }

  private void resetSelectedView() {
    if (this.selectedView != null) {
      this.selectedView.setSelected(false);
      UView.selectViewChildes(this.selectedView, false);
    }

  }

  public void setOnGenderDetectedListener(FieldGenderPicker.OnGenderDetectListener onGenderDetectedListener) {
    this.onGenderDetectedListener = onGenderDetectedListener;
  }

  @Override
  public void setValue(String value) {
    super.setValue(value);
    if (value != null) {
      this.setGender(value);
    }

  }

  @Override
  protected boolean typeValidation() {
    return true;
  }

  @Override
  protected CharSequence getEmptyError() {
    return this.getContext().getString(R.string.field_invalid_required);
  }

  private void onValueChanged(int gender) {
    this.setError(null);
    this.setValue(Integer.toString(gender));
    if (this.onGenderDetectedListener != null) {
      this.onGenderDetectedListener.onGenderDetected(gender);
    }

  }

  public interface OnGenderDetectListener {
    void onGenderDetected(int var1);
  }
}
