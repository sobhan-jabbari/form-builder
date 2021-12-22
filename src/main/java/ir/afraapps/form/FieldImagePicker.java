package ir.afraapps.form;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ir.afraapps.gviews.CircularImageView;


public class FieldImagePicker extends FormLayout implements OnActivityResultListener {
  private static final String TAG = "FieldImagePicker";
  private static final boolean D = false;
  private TextView txtTitle;
  private TextView txtError;
  private CircularImageView imageView;
  private Fragment fragment;

  public FieldImagePicker(@NonNull Context context) {
    this(context, null, 0);
  }

  public FieldImagePicker(@NonNull Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public FieldImagePicker(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.init(context, attrs, defStyleAttr);
  }

  private void init(Context context, AttributeSet attrs, int defStyleAttr) {
    this.initViews(context);
    if (attrs != null) {
      TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.FieldImagePicker, defStyleAttr, 0);
      if (a.hasValue(R.styleable.FieldImagePicker_android_hint)) {
        CharSequence hint = a.getText(R.styleable.FieldImagePicker_android_hint);
        this.setTitle(hint.toString());
      }

      int min = a.getInt(R.styleable.FieldImagePicker_minSize, 0);
      int max = a.getInt(R.styleable.FieldImagePicker_maxSize, 0);
      this.setMin(min);
      this.setMax(max);
      a.recycle();
    }

  }

  private void initViews(Context context) {
    LayoutInflater.from(context).inflate(R.layout.field_image_picker, this, true);
    this.txtTitle = this.findViewById(R.id.txt_title);
    this.txtError = this.findViewById(R.id.txt_error);
    this.imageView = this.findViewById(R.id.imgPic);
    this.findViewById(R.id.parent).setOnClickListener((v) -> {
      this.prepareImagePicker();
    });
  }

  private void prepareImagePicker() {
    if (this.getMin() == 0) {
      this.setMin(200);
    }

    if (this.getMax() == 0) {
      this.setMax(400);
    }

    if (this.fragment != null) {
      this.prepareFragmentImagePicker();
    } else if (this.getContext() instanceof Activity) {
      this.prepareActivityImagePicker((Activity)this.getContext());
    }

  }

  private void prepareFragmentImagePicker() {
    CropImage.startPickImageActivity(this.getContext(), this.fragment);
  }

  private void prepareActivityImagePicker(Activity activity) {
    CropImage.startPickImageActivity(activity);
  }

  public void setFragment(Fragment fragment) {
    this.fragment = fragment;
  }

  public void setImage(String base64) {
    if (!TextUtils.isEmpty(base64)) {
      (new Thread(() -> {
        byte[] data = Base64.decode(base64, 0);
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        if (bitmap != null) {
          this.post(() -> {
            this.imageView.setImageBitmap(bitmap);
            this.imageView.setColor(-3355444);
          });
        }

      })).start();
    }
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
    return this.getContext().getString(R.string.field_picker_image_invalid_required);
  }

  @Override
  public void setValue(String value) {
    super.setValue(value);
    if (value != null) {
      this.setImage(value);
    }

  }

  @TargetApi(23)
  private void requestPermissions() {
    if (this.fragment != null) {
      this.fragment.requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 201);
    } else if (this.getContext() instanceof Activity) {
      Activity activity = (Activity)this.getContext();
      activity.requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 201);
    }

  }

  private void startCropImageActivity(Uri imageUri) {
    if (this.fragment != null) {
      this.startCropImageActivityFromFragment(imageUri);
    } else if (this.getContext() instanceof Activity) {
      this.startCropImageActivityFromActivity((Activity)this.getContext(), imageUri);
    }

  }

  private void startCropImageActivityFromFragment(Uri imageUri) {
    CropImage.activity(imageUri).setMultiTouchEnabled(true).setAllowFlipping(false).setMinCropResultSize(this.getMin(), this.getMin()).setMaxCropResultSize(this.getMax(), this.getMax()).start(this.getContext(), this.fragment);
  }

  private void startCropImageActivityFromActivity(Activity activity, Uri imageUri) {
    CropImage.activity(imageUri).setMultiTouchEnabled(true).setAllowFlipping(false).setMinCropResultSize(this.getMin(), this.getMin()).setMaxCropResultSize(this.getMax(), this.getMax()).start(activity);
  }

  private void saveImage(Uri imageUri) {
    if (imageUri != null) {
      (new Thread(() -> {
        try {
          InputStream inputStream;
          if ("content".equals(imageUri.getScheme())) {
            inputStream = this.getContext().getContentResolver().openInputStream(imageUri);
          } else {
            inputStream = new FileInputStream(new File(imageUri.getPath()));
          }

          byte[] data = new byte[inputStream.available()];
          inputStream.read(data, 0, data.length);

          try {
            inputStream.close();
          } catch (IOException var5) {
          }

          String base64 = Base64.encodeToString(data, 0);
          this.post(() -> {
            this.setError(null);
            this.setValue(base64);
          });
        } catch (Exception var6) {
        }

      })).start();
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch(requestCode) {
      case 200:
        if (resultCode == -1) {
          Uri imageUri = CropImage.getPickImageResultUri(this.getContext(), data);
          if (CropImage.isReadExternalStoragePermissionsRequired(this.getContext(), imageUri)) {
            this.requestPermissions();
          } else {
            this.startCropImageActivity(imageUri);
          }
        }
        break;
      case 203:
        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        if (resultCode == -1 && result != null) {
          this.saveImage(result.getUri());
        }
    }

  }
}
