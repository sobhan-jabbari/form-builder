package ir.afraapps.form;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckedTextView;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.view.ViewCompat;


public class FieldSpinner extends FormLayout {
  private AppCompatSpinner spinner;
  private TextView txtError;
  private TextView txtTitle;
  private String value;
  private FieldSpinner.AdapterSpinner adapter;
  private CharSequence[] entries;
  private CharSequence[] values;
  private ColorStateList colorStateList;
  private boolean isPreparingAdapter;
  private FieldSpinner.OnItemSelectListener onItemSelectListener;

  public FieldSpinner(@NonNull Context context) {
    this(context, null, 0);
  }

  public FieldSpinner(@NonNull Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public FieldSpinner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.init(context, attrs, defStyleAttr);
  }

  private void init(Context context, AttributeSet attrs, int defStyleAttr) {
    this.initViews(context);
    if (attrs != null) {
      TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.FieldSpinner, defStyleAttr, 0);
      CharSequence title = a.getText(R.styleable.FieldSpinner_sp_title);
      CharSequence[] entries = a.getTextArray(R.styleable.FieldSpinner_android_entries);
      CharSequence[] values = a.getTextArray(R.styleable.FieldSpinner_sp_values);
      if (a.hasValue(R.styleable.FieldSpinner_formTint)) {
        ViewCompat.setBackgroundTintList(this.spinner, a.getColorStateList(R.styleable.FieldSpinner_formTint));
      }

      this.setTitle(title == null ? null : title.toString());
      if (entries != null && entries.length != 0) {
        this.setEntries(entries);
      }

      if (values != null && values.length != 0) {
        this.setValues(values);
      }

      a.recycle();
    }

  }

  private void initViews(Context context) {
    LayoutInflater.from(context).inflate(R.layout.field_spinner, this, true);
    this.spinner = this.findViewById(R.id.spinner);
    this.txtError = this.findViewById(R.id.txt_error);
    this.txtTitle = this.findViewById(R.id.txt_title);
  }

  @Override
  public void setEnabled(boolean enabled) {
    if (this.spinner != null) {
      this.spinner.setEnabled(enabled);
    }

    if (this.txtError != null) {
      this.txtError.setEnabled(enabled);
    }

    if (this.txtTitle != null) {
      this.txtTitle.setEnabled(enabled);
    }

    super.setEnabled(enabled);
  }

  public void setOnItemSelectListener(FieldSpinner.OnItemSelectListener onItemSelectListener) {
    this.onItemSelectListener = onItemSelectListener;
  }

  private void prepareAdapter() {
    this.isPreparingAdapter = true;
    this.adapter = new FieldSpinner.AdapterSpinner(this, this.entries, this.values, this.value);
    this.spinner.setAdapter(this.adapter);
    this.post(() -> this.spinner.setSelection(this.adapter.getSelectedPosition()));
    this.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        FieldSpinner.this.setError(null);
        if (FieldSpinner.this.isPreparingAdapter) {
          FieldSpinner.this.isPreparingAdapter = false;
        } else {
          String value = null;
          if (position > 0) {
            value = FieldSpinner.this.adapter.getValue(position).toString();
          }

          if (!TextUtils.equals(FieldSpinner.this.getValue(), value)) {
            FieldSpinner.this.setValue(value);
            if (FieldSpinner.this.onItemSelectListener != null) {
              FieldSpinner.this.onItemSelectListener.onItemSelect(value);
            }

          }
        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
      }
    });
  }

  public void setSpinnerValue(String value) {
    this.value = value;
    if (this.values != null && this.entries != null) {
      this.prepareAdapter();
    }

  }

  public String getSpinnerValue() {
    return this.value;
  }

  @Override
  public void setValue(String value) {
    super.setValue(value);
    if (!TextUtils.equals(value, this.value)) {
      this.setSpinnerValue(value);
    }

  }

  @Override
  public void setTitle(CharSequence title) {
    super.setTitle(title);
    this.txtTitle.setText(title);
  }

  @Override
  public void setError(CharSequence error) {
    super.setError(error);
    this.txtError.setText(error);
    this.spinner.setActivated(!TextUtils.isEmpty(error));
  }

  @Override
  public void setError(int error) {
    super.setError(error);
    this.txtError.setText(error);
    this.spinner.setActivated(error != 0);
  }

  @Override
  protected boolean typeValidation() {
    return true;
  }

  @Override
  protected CharSequence getEmptyError() {
    return this.getContext().getString(R.string.field_spinner_invalid_required);
  }

  public CharSequence[] getEntries() {
    return this.entries;
  }

  public void setEntries(CharSequence[] entries) {
    if (entries != null && entries.length != 0) {
      this.entries = new CharSequence[entries.length + 1];
      this.entries[0] = this.getContext().getString(R.string.field_spinner_title);
      int index = 1;

      for (CharSequence entry : entries) {
        this.entries[index] = entry;
        ++index;
      }

      if (this.values != null && this.values.length != 0) {
        this.prepareAdapter();
      }

    }
  }

  public void setValues(CharSequence[] values) {
    if (values != null && values.length != 0) {
      this.values = new CharSequence[values.length + 1];
      this.values[0] = "";
      int index = 1;

      for (CharSequence value : values) {
        this.values[index] = value;
        ++index;
      }

      if (this.entries != null && this.entries.length != 0) {
        this.prepareAdapter();
      }

    }
  }

  public CharSequence[] getValues() {
    return this.values;
  }

  public interface OnItemSelectListener {
    void onItemSelect(String var1);
  }

  private static class AdapterSpinner extends BaseAdapter {
    private final CharSequence[] entries;
    private final CharSequence[] values;
    private final CharSequence value;
    private final FieldSpinner spinner;

    AdapterSpinner(FieldSpinner spinner, CharSequence[] entries, CharSequence[] values, CharSequence value) {
      this.entries = entries;
      this.values = values;
      this.value = value;
      this.spinner = spinner;
    }

    public int getSelectedPosition() {
      int position = 0;
      for (CharSequence value : this.values) {
        if (value.equals(this.value)) {
          return position;
        }
        ++position;
      }

      return 0;
    }

    @Override
    public int getCount() {
      return this.values.length;
    }

    @Override
    public CharSequence getItem(int position) {
      return this.entries[position];
    }

    public CharSequence getValue(int position) {
      return this.values[position];
    }

    @Override
    public long getItemId(int position) {
      return 0L;
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
      FieldSpinner.AdapterSpinner.ViewHolder holder;
      if (convertView == null) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_item, parent, false);
        holder = new FieldSpinner.AdapterSpinner.ViewHolder(convertView, this.spinner.isEnabled());
        convertView.setTag(holder);
      } else {
        holder = (FieldSpinner.AdapterSpinner.ViewHolder) convertView.getTag();
      }

      holder.fill(this.getItem(position));
      return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
      CharSequence item = this.getItem(position);
      FieldSpinner.AdapterSpinner.ViewDropHolder holder;
      if (convertView == null) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_drop_item, parent, false);
        boolean isChecked = TextUtils.equals(this.value, this.getValue(position));
        holder = new FieldSpinner.AdapterSpinner.ViewDropHolder(convertView, isChecked);
        convertView.setTag(holder);
      } else {
        holder = (FieldSpinner.AdapterSpinner.ViewDropHolder) convertView.getTag();
      }

      holder.fill(item);
      return convertView;
    }

    private static class ViewDropHolder {
      AppCompatCheckedTextView textView;

      ViewDropHolder(View view, boolean isChecked) {
        this.textView = view.findViewById(R.id.txt_spinner_drop_title);
        this.textView.setChecked(isChecked);
        this.textView.setSelected(isChecked);
      }

      public void fill(CharSequence item) {
        this.textView.setText(item);
      }
    }

    private static class ViewHolder {
      TextView titleView;

      public ViewHolder(View view, boolean isEnabled) {
        this.titleView = view.findViewById(R.id.txt_spinner_title);
        this.titleView.setEnabled(isEnabled);
      }

      public void fill(CharSequence item) {
        this.titleView.setText(item);
      }
    }
  }
}
