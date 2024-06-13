// Generated by view binder compiler. Do not edit!
package com.example.soundtt.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.soundtt.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class RhythmhardBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final Button btnback;

  @NonNull
  public final Button btnpause;

  @NonNull
  public final Button btnstart;

  @NonNull
  public final TextView tvbad;

  @NonNull
  public final TextView tvex;

  @NonNull
  public final TextView tvgood;

  @NonNull
  public final TextView tvgreat;

  private RhythmhardBinding(@NonNull ConstraintLayout rootView, @NonNull Button btnback,
      @NonNull Button btnpause, @NonNull Button btnstart, @NonNull TextView tvbad,
      @NonNull TextView tvex, @NonNull TextView tvgood, @NonNull TextView tvgreat) {
    this.rootView = rootView;
    this.btnback = btnback;
    this.btnpause = btnpause;
    this.btnstart = btnstart;
    this.tvbad = tvbad;
    this.tvex = tvex;
    this.tvgood = tvgood;
    this.tvgreat = tvgreat;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static RhythmhardBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static RhythmhardBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.rhythmhard, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static RhythmhardBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btnback;
      Button btnback = ViewBindings.findChildViewById(rootView, id);
      if (btnback == null) {
        break missingId;
      }

      id = R.id.btnpause;
      Button btnpause = ViewBindings.findChildViewById(rootView, id);
      if (btnpause == null) {
        break missingId;
      }

      id = R.id.btnstart;
      Button btnstart = ViewBindings.findChildViewById(rootView, id);
      if (btnstart == null) {
        break missingId;
      }

      id = R.id.tvbad;
      TextView tvbad = ViewBindings.findChildViewById(rootView, id);
      if (tvbad == null) {
        break missingId;
      }

      id = R.id.tvex;
      TextView tvex = ViewBindings.findChildViewById(rootView, id);
      if (tvex == null) {
        break missingId;
      }

      id = R.id.tvgood;
      TextView tvgood = ViewBindings.findChildViewById(rootView, id);
      if (tvgood == null) {
        break missingId;
      }

      id = R.id.tvgreat;
      TextView tvgreat = ViewBindings.findChildViewById(rootView, id);
      if (tvgreat == null) {
        break missingId;
      }

      return new RhythmhardBinding((ConstraintLayout) rootView, btnback, btnpause, btnstart, tvbad,
          tvex, tvgood, tvgreat);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}