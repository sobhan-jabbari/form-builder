package ir.afraapps.form;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ir.afraapps.basic.helper.UTypeface;


public class FieldBarcode extends FormLayout {
  private TextView txtTitle;
  private TextView txtError;
  private EditText edtBarcode;
  private final TextWatcher textWatcher;
  @Nullable
  private BarcodePickListener barcodePickListener;

  public FieldBarcode(@NonNull Context context) {
    this(context, null, 0);
  }

  public FieldBarcode(@NonNull Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public FieldBarcode(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.textWatcher = this.getTextWatcher();
    this.init(context);
  }

  private void init(Context context) {
    this.initViews(context);
  }

  private void initViews(Context context) {
    LayoutInflater.from(context).inflate(R.layout.field_barcode_picker, this, true);
    this.txtTitle = this.findViewById(R.id.txt_title);
    this.txtError = this.findViewById(R.id.txt_error);
    this.edtBarcode = this.findViewById(R.id.edt_barcode);
    this.findViewById(R.id.img_barcode).setOnClickListener((v) -> this.pickBarcode());
  }

  private void pickBarcode() {
    if (barcodePickListener != null) {
      barcodePickListener.pickBarcode();
    }
  }

  public void setBarcodePickListener(@Nullable BarcodePickListener barcodePickListener) {
    this.barcodePickListener = barcodePickListener;
  }

  public void setBarcode(String barcode) {
    this.edtBarcode.setText(barcode);
  }

  public String getBarcode() {
    return this.edtBarcode.getText() == null ? null : this.edtBarcode.getText().toString();
  }

  @Override
  public void setTitle(CharSequence title) {
    if (this.txtTitle != null) {
      this.txtTitle.setText(title);
    }
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
    return this.getContext().getString(R.string.field_picker_barcode_invalid_required);
  }

  @Override
  public void setValue(String value) {
    super.setValue(value);
    if (value != null) {
      this.setBarcode(value);
    }

  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    if (!this.isInEditMode() && this.edtBarcode != null) {
      this.edtBarcode.setTypeface(UTypeface.get(R.font.sans));
      this.edtBarcode.addTextChangedListener(this.textWatcher);
    }

  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    if (this.edtBarcode != null) {
      this.edtBarcode.removeTextChangedListener(this.textWatcher);
    }

  }

  private TextWatcher getTextWatcher() {
    return new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        FieldBarcode.this.setError(null);
        FieldBarcode.this.setValue(s == null ? null : s.toString());
      }

      @Override
      public void afterTextChanged(Editable s) {
      }
    };
  }

  public interface BarcodePickListener {
    void pickBarcode();
  }

}
