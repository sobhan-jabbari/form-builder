package ir.afraapps.form;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ir.afraapps.basic.helper.UMetric;


public class FieldSpinnerMultiSelect extends FormLayout {
  private TextView txtError;
  private TextView txtTitle;
  private TextView spinnerItem;
  private CharSequence[] entries;
  private CharSequence[] values;
  private FieldSpinnerMultiSelect.AdapterMultiSelect adapter;

  public FieldSpinnerMultiSelect(@NonNull Context context) {
    this(context, null, 0);
  }

  public FieldSpinnerMultiSelect(@NonNull Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public FieldSpinnerMultiSelect(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.init(context, attrs, defStyleAttr);
  }

  private void init(Context context, AttributeSet attrs, int defStyleAttr) {
    this.initViews(context);
    if (attrs != null) {
      TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.FieldSpinner, defStyleAttr, 0);
      CharSequence title = a.getText(R.styleable.FieldSpinner_sp_title);
      this.entries = a.getTextArray(R.styleable.FieldSpinner_android_entries);
      this.values = a.getTextArray(R.styleable.FieldSpinner_sp_values);
      if (a.hasValue(R.styleable.FieldSpinner_formTint)) {
        ViewCompat.setBackgroundTintList(this.spinnerItem, a.getColorStateList(R.styleable.FieldSpinner_formTint));
      }

      this.setTitle(title == null ? null : title.toString());
      this.setEntries(this.entries);
      this.setValues(this.values);
      a.recycle();
    }

  }

  private void initViews(Context context) {
    LayoutInflater.from(context).inflate(R.layout.field_spinner_multi_select, this, true);
    this.txtError = this.findViewById(R.id.txt_error);
    this.txtTitle = this.findViewById(R.id.txt_title);
    this.spinnerItem = this.findViewById(R.id.spinner_item);
    this.spinnerItem.setOnClickListener((v) -> this.showDialog());
    this.adapter = new FieldSpinnerMultiSelect.AdapterMultiSelect(this);
  }

  @SuppressLint({"InflateParams"})
  private void showDialog() {
    if (this.entries != null && this.values != null) {
      AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
      builder.setTitle(this.getTitle());
      builder.setPositiveButton(null, null);
      builder.setNegativeButton(null, null);
      LayoutInflater inflater = LayoutInflater.from(this.getContext());
      RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.recyclerview, null);
      recyclerView.setPadding(0, 0, 0, UMetric.toDIP(38));
      recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
      recyclerView.setItemAnimator(new DefaultItemAnimator());
      recyclerView.setAdapter(this.adapter);
      TextView titleView = (TextView) inflater.inflate(R.layout.alert_title, null);
      titleView.setText(this.getTitle());
      builder.setCustomTitle(titleView);
      builder.setView(recyclerView);
      builder.create().show();
    }
  }

  @Override
  public void setTitle(String title) {
    super.setTitle(title);
    this.txtTitle.setText(title);
  }

  @Override
  public void setError(String error) {
    super.setError(error);
    this.txtError.setText(error);
  }

  @Override
  public void setError(int error) {
    super.setError(error);
    this.txtError.setText(error);
  }

  @Override
  protected boolean typeValidation() {
    return true;
  }

  @Override
  protected String getEmptyError() {
    return this.getContext().getString(R.string.field_spinner_invalid_required);
  }

  @Override
  public void setValue(String value) {
    super.setValue(value);
    if (TextUtils.isEmpty(value)) {
      this.spinnerItem.setText(R.string.field_spinner_title);
    } else {
      this.spinnerItem.setText(value);
    }

    this.adapter.setValue(value);
  }

  public CharSequence[] getEntries() {
    return this.entries;
  }

  public void setEntries(CharSequence[] entries) {
    this.entries = entries;
    this.adapter.setEntries(entries);
  }

  public void setValues(CharSequence[] values) {
    this.values = values;
    this.adapter.setValues(values);
  }

  private static class AdapterMultiSelect extends RecyclerView.Adapter<AdapterMultiSelect.ViewHolder> {
    CharSequence[] entries;
    CharSequence[] values;
    boolean[] selections;
    String value;
    FieldSpinnerMultiSelect field;

    public AdapterMultiSelect(FieldSpinnerMultiSelect field) {
      this.field = field;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setEntries(CharSequence[] entries) {
      this.entries = entries;
      if (this.values != null) {
        this.extractValue();
        this.notifyDataSetChanged();
      }

    }

    @SuppressLint("NotifyDataSetChanged")
    public void setValues(CharSequence[] values) {
      this.values = values;
      if (values != null && values.length > 0) {
        this.selections = new boolean[this.entries.length];
      }

      if (this.entries != null) {
        this.extractValue();
        this.notifyDataSetChanged();
      }

    }

    public String getValue() {
      return this.value;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setValue(String value) {
      if (!TextUtils.equals(this.value, value)) {
        this.value = value;
        if (this.values != null && this.entries != null) {
          this.extractValue();
          this.notifyDataSetChanged();
        }

      }
    }

    private void extractValue() {
      if (this.value == null) {
        for (int i = this.selections.length - 1; i >= 0; --i) {
          this.selections[i] = false;
        }
      } else {
        String[] parts = this.value.split(",");
        int index = 0;
        CharSequence[] var4 = this.values;

        for (CharSequence v : var4) {
          boolean selected = false;

          for (String part : parts) {
            if (TextUtils.equals(v, part)) {
              selected = true;
            }
          }

          this.selections[index] = selected;
          ++index;
        }
      }

    }

    private void catenateValue() {
      StringBuilder stringBuilder = new StringBuilder();

      for (int i = 0; i < this.selections.length; ++i) {
        if (this.selections[i]) {
          stringBuilder.append(this.values[i]).append(",");
        }
      }

      this.value = stringBuilder.toString();
      if (!TextUtils.isEmpty(this.value)) {
        this.value = this.value.substring(0, this.value.length() - 1);
      } else {
        this.value = null;
      }

    }

    @Override
    public void onBindViewHolder(@NonNull FieldSpinnerMultiSelect.AdapterMultiSelect.ViewHolder holder, int position) {
      holder.bind(this.entries[position], this.selections[position]);
      holder.parent.setOnClickListener((v) -> {
        boolean isSelected = !holder.checkBox.isChecked();
        this.selections[position] = isSelected;
        holder.setSelected(isSelected);
        this.catenateValue();
        this.field.setValue(this.value);
        this.field.setError(null);
      });
    }

    @Override
    @NonNull
    public FieldSpinnerMultiSelect.AdapterMultiSelect.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      LayoutInflater inflater = LayoutInflater.from(parent.getContext());
      return new FieldSpinnerMultiSelect.AdapterMultiSelect.ViewHolder(inflater.inflate(R.layout.spinner_multi_select_item, parent, false));
    }

    @Override
    public int getItemCount() {
      return this.entries == null ? 0 : this.entries.length;
    }

    public static class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
      private final ViewGroup parent;
      private final AppCompatCheckBox checkBox;
      private final TextView txtTitle;

      ViewHolder(View view) {
        super(view);
        this.parent = view.findViewById(R.id.parent);
        this.checkBox = view.findViewById(R.id.checkbox);
        this.txtTitle = view.findViewById(R.id.txt_title);
        this.checkBox.setClickable(false);
        this.checkBox.setFocusable(false);
      }

      void bind(CharSequence title, boolean isSelected) {
        this.txtTitle.setText(title == null ? null : title.toString());
        this.setSelected(isSelected);
      }

      void setSelected(boolean isSelected) {
        this.txtTitle.setSelected(isSelected);
        this.checkBox.setSelected(isSelected);
        this.checkBox.setChecked(isSelected);
      }
    }
  }
}
