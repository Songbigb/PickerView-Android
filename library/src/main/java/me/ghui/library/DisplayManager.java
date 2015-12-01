package me.ghui.library;

import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

/**
 * Created by ghui on 11/29/15.
 */
public class DisplayManager {
	private static DisplayManager mInstance;
	private PopupWindow mPopupWindow;


	private DisplayManager() {
		init();
	}

	public static DisplayManager getInstance() {
		if (mInstance == null) {
			mInstance = new DisplayManager();
		}
		return mInstance;
	}

	private void init() {
		mPopupWindow = new PopupWindow();
		mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		mPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopupWindow.setAnimationStyle(R.style.AnimationPopup);
		mPopupWindow.setTouchable(true);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setOutsideTouchable(true);
	}

	public void setLayout(View view) {
		mPopupWindow.setContentView(view);
	}

	public void show(View view) {
		if (mPopupWindow != null && !mPopupWindow.isShowing()) {
			mPopupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
		}
	}

	public void dismiss() {
		if (mPopupWindow != null) {
			mPopupWindow.dismiss();
		}
	}


}


