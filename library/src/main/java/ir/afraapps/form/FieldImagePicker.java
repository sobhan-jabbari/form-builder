package ir.afraapps.form;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ir.afraapps.gviews.CircularImageView;


public class FieldImagePicker extends FieldContentPicker {
  private CircularImageView imageView;

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
    setContentName(name);
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

  public void setImageBitmap(Bitmap bitmap) {
    if (imageView != null) {
      this.imageView.setImageBitmap(bitmap);
      this.imageView.setColor(-3355444);
    }
  }

  private void setImage(@Nullable String path) {
    if (!TextUtils.isEmpty(path)) {
      (new Thread(() -> {
        File file = null;
        if (path.startsWith("http")) {
          file = downloadContent(path);
        } else if (path.startsWith("content")) {
          file = loadContent(Uri.parse(path));
        } else if (path.startsWith("file")) {
          file = new File(path);
        }


        if (file == null || !file.exists()) {
          this.post(() -> {
            if (imageView != null) {
              this.imageView.setImageDrawable(null);
              this.imageView.setColor(Color.TRANSPARENT);
            }
          });
        } else {
          Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
          if (bitmap != null) {
            this.post(() -> {
              if (imageView != null) {
                this.imageView.setImageBitmap(bitmap);
                this.imageView.setColor(-3355444);
              }
            });
          }
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
