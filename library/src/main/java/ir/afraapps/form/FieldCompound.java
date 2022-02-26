package ir.afraapps.form;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public abstract class FieldCompound extends FormLayout {
  private TextView txtTitle;
  private CompoundButton button;
  private CompoundButton.OnCheckedChangeListener listener;

  public FieldCompound(@NonNull Context context) {
    this(context, null, 0);
  }

  public FieldCompound(@NonNull Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public FieldCompound(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.init(context, attrs, defStyleAttr);
  }

  private void init(Context context, AttributeSet attrs, int defStyleAttr) {
    this.initViews(context);
    ViewGroup parent = this.findViewById(R.id.parent);
    this.txtTitle = this.findViewById(R.id.txt_title);
    this.button = this.findViewById(R.id.compound_button);
    this.button.setOnCheckedChangeListener((buttonView, isCheckedx) -> {
      this.setValue(isCheckedx ? "1" : "0");
      if (this.listener != null) {
        this.listener.onCheckedChanged(buttonView, isCheckedx);
      }

    });
    parent.setOnClickListener((v) -> this.setChecked(!this.isChecked()));
    if (attrs != null) {
      TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.FieldCompound, defStyleAttr, 0);
      String title = a.getString(R.styleable.FieldCompound_android_hint);
      boolean isChecked = a.getBoolean(R.styleable.FieldCompound_android_checked, false);
      this.setTitle(title);
      this.setChecked(isChecked);
      a.recycle();
    }

  }

  public abstract void initViews(Context var1);

  private void setChecked(String value) {
    if (this.button != null) {
      this.button.setChecked("1".equals(value));
    }

  }

  private String getChecked() {
    return this.isChecked() ? "1" : "0";
  }

  public void setChecked(boolean isChecked) {
    if (this.button != null) {
      if (this.button.isChecked() != isChecked) {
        this.button.setChecked(isChecked);
      } else if (this.getValue() == null) {
        this.setValue(isChecked ? "1" : "0");
      }
    }

  }

  public boolean isChecked() {
    return this.button != null && this.button.isChecked();
  }

  public void setOnCkeckedChangeListener(CompoundButton.OnCheckedChangeListener listener) {
    this.listener = listener;
  }

  @Override
  public void setValue(String value) {
    super.setValue(value);
    if (!this.getChecked().equals(value)) {
      this.setChecked(value);
    }

  }

  @Override
  public void setTitle(String title) {
    super.setTitle(title);
    if (this.txtTitle != null) {
      this.txtTitle.setText(title);
    }

  }
}
