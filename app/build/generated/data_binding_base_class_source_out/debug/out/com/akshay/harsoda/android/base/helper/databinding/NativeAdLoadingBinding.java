// Generated by view binder compiler. Do not edit!
package com.akshay.harsoda.android.base.helper.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.akshay.harsoda.android.base.helper.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class NativeAdLoadingBinding implements ViewBinding {
  @NonNull
  private final ShimmerFrameLayout rootView;

  @NonNull
  public final FrameLayout loadingNativeAdContainer;

  private NativeAdLoadingBinding(@NonNull ShimmerFrameLayout rootView,
      @NonNull FrameLayout loadingNativeAdContainer) {
    this.rootView = rootView;
    this.loadingNativeAdContainer = loadingNativeAdContainer;
  }

  @Override
  @NonNull
  public ShimmerFrameLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static NativeAdLoadingBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static NativeAdLoadingBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.native_ad_loading, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static NativeAdLoadingBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.loading_native_ad_container;
      FrameLayout loadingNativeAdContainer = ViewBindings.findChildViewById(rootView, id);
      if (loadingNativeAdContainer == null) {
        break missingId;
      }

      return new NativeAdLoadingBinding((ShimmerFrameLayout) rootView, loadingNativeAdContainer);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}