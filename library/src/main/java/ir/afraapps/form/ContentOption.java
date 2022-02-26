package ir.afraapps.form;

import androidx.annotation.Nullable;

/**
 * In the name of Allah
 * <p>
 * Created by Ali Jabbari on 2/26/2022.
 */
public class ContentOption {
  public final String mimeType;
  public final int min;
  public final int max;

  public ContentOption(String mimeType, @Nullable Integer min, @Nullable Integer max) {
    this.mimeType = mimeType;
    this.min = min;
    this.max = max;
  }
}
