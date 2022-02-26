package ir.afraapps.form;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ir.afraapps.gviews.CircularImageView;


public class FieldImagePicker extends FieldContentPicker {
  private CircularImageView imageView;

  public FieldImagePicker(@NonNull Context context) {
    super(context);
  }

  public FieldImagePicker(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public FieldImagePicker(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.init(context, attrs, defStyleAttr);
  }

  private void init(Context context, AttributeSet attrs, int defStyleAttr) {
    this.initViews();
    if (attrs != null) {
      TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.FieldImagePicker, defStyleAttr, 0);
      int min = a.getInt(R.styleable.FieldImagePicker_minSize, 0);
      int max = a.getInt(R.styleable.FieldImagePicker_maxSize, 0);
      this.setMin(min);
      this.setMax(max);
      a.recycle();
    }
  }

  private void initViews() {
    setPicLayout(R.layout.field_image_pic_layout);
    this.imageView = this.findViewById(R.id.imgPic);
  }

  @Override
  String getMimeType() {
    return "Image/*";
  }

  @Override
  protected void onPrePickContent() {
    if (this.getMin() == 0) {
      this.setMin(200);
    }
    if (this.getMax() == 0) {
      this.setMax(400);
    }
  }

  @Override
  protected void setContentValue(String value, String name) {
    setValue(value);
  }

  @Override
  protected String getEmptyError() {
    return this.getContext().getString(R.string.field_picker_image_invalid_required);
  }

  @Override
  public void setValue(String value) {
    super.setValue(value);
    this.setImage(value);
  }

  private void setImage(@Nullable String base64) {
    if (!TextUtils.isEmpty(base64)) {
      (new Thread(() -> {
        byte[] data = Base64.decode(base64, 0);
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        if (bitmap != null) {
          this.post(() -> {
            if (imageView != null) {
              this.imageView.setImageBitmap(bitmap);
              this.imageView.setColor(-3355444);
            }

          });
        }

      })).start();
    } else {
      if (imageView != null) {
        this.imageView.setImageDrawable(null);
        this.imageView.setColor(Color.TRANSPARENT);
      }
    }
  }

}
