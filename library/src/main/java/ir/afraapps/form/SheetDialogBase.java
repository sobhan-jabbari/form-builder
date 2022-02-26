package ir.afraapps.form;


import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class SheetDialogBase extends AppCompatDialogFragment {

  @Nullable
  private BottomSheetBehavior sheetBehavior;
  private BottomSheetDialog dialog;


  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  @NonNull
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    this.dialog = new BottomSheetDialog(this.getContext(), this.getTheme());
    this.dialog.setOnShowListener((dialog1) -> {
      BottomSheetDialog d = (BottomSheetDialog) dialog1;
      FrameLayout bottomSheet = d.findViewById(R.id.design_bottom_sheet);
      this.sheetBehavior = BottomSheetBehavior.from(bottomSheet);
      this.sheetBehavior.setHideable(true);
      this.sheetBehavior.setSkipCollapsed(true);
      this.sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
      this.sheetBehavior.addBottomSheetCallback(this.getBottomSheetCallback());
    });
    return this.dialog;
  }

  protected void hideBottomSheet() {
    if (this.sheetBehavior != null) {
      this.sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

  }

  private BottomSheetBehavior.BottomSheetCallback getBottomSheetCallback() {
    return new BottomSheetBehavior.BottomSheetCallback() {
      @Override
      public void onStateChanged(@NonNull View bottomSheet, int newState) {
        if (newState == BottomSheetBehavior.STATE_HIDDEN) {
          SheetDialogBase.this.dismiss();
        }
      }

      @Override
      public void onSlide(@NonNull View bottomSheet, float slideOffset) {
      }
    };
  }

  public void onBackPressed() {
    this.hideBottomSheet();
  }
}

