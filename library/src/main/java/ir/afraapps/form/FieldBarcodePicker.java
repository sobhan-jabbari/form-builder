package ir.afraapps.form;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ir.afraapps.basic.helper.UPermission;
import ir.afraapps.basic.helper.UTypeface;


public class FieldBarcodePicker extends FormLayout implements OnActivityResultListener {
  private static final int REQ_PERMISSION_CAMERA = 200;
  private TextView txtTitle;
  private TextView txtError;
  private EditText edtBarcode;
  private Fragment fragment;
  private TextWatcher textWatcher;

  public FieldBarcodePicker(@NonNull Context context) {
    this(context, null, 0);
  }

  public FieldBarcodePicker(@NonNull Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public FieldBarcodePicker(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.textWatcher = this.getTextWatcher();
    this.init(context, attrs, defStyleAttr);
  }

  private void init(Context context, AttributeSet attrs, int defStyleAttr) {
    this.initViews(context);
  }

  private void initViews(Context context) {
    LayoutInflater.from(context).inflate(R.layout.field_barcode_picker, this, true);
    this.txtTitle = this.findViewById(R.id.txt_title);
    this.txtError = this.findViewById(R.id.txt_error);
    this.edtBarcode = this.findViewById(R.id.edt_barcode);
    this.findViewById(R.id.img_barcode).setOnClickListener((v) -> {
      this.prepareBarcodePicker();
    });
  }

  private void prepareBarcodePicker() {
    if (this.fragment != null) {
      this.prepareFragmentBarcodePicker();
    } else if (this.getContext() instanceof Activity) {
      this.prepareActivityBarcodePicker((Activity)this.getContext());
    }

  }

  private void prepareFragmentBarcodePicker() {
    if (UPermission.isGrantedCamera(this.fragment, 200)) {
      IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this.fragment);
      integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
      integrator.setPrompt("");
      integrator.setBeepEnabled(false);
      integrator.setOrientationLocked(false);
      integrator.initiateScan();
    }
  }

  private void prepareActivityBarcodePicker(Activity activity) {
    if (UPermission.isGrantedCamera(activity, 200)) {
      IntentIntegrator integrator = new IntentIntegrator(activity);
      integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
      integrator.setPrompt("");
      integrator.setBeepEnabled(false);
      integrator.setOrientationLocked(false);
      integrator.initiateScan();
    }
  }

  public void setFragment(Fragment fragment) {
    this.fragment = fragment;
  }

  public void setBarcode(String barcode) {
    this.edtBarcode.setText(barcode);
  }

  public String getBarcode() {
    return this.edtBarcode.getText() == null ? null : this.edtBarcode.getText().toString();
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
        FieldBarcodePicker.this.setError(null);
        FieldBarcodePicker.this.setValue(s == null ? null : s.toString());
      }

      @Override
      public void afterTextChanged(Editable s) {
      }
    };
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
    if (result != null && result.getContents() != null) {
      this.setBarcode(result.getContents());
    }

  }
}
