package me.ghui.library;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Toast;

/**
 * Created by ghui on 11/29/15.
 */
public class DisplayManager {
	private static DisplayManager mInstance;
	private PopupWindow mPopupWindow;
	private View mRootView;
	private Context mContext;


	private DisplayManager(Context context) {
		mContext = context;
		init();
	}

	public static DisplayManager getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new DisplayManager(context);
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
		mRootView = view;
		mPopupWindow.setContentView(mRootView);
	}


	public void show(View view) {
		if (mPopupWindow != null && !mPopupWindow.isShowing()) {
			mPopupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
		}
	}

	public void dismiss() {
		if (mPopupWindow != null) {
			Toast.makeText(mContext, "dismiss", Toast.LENGTH_SHORT).show();
			mPopupWindow.dismiss();
		}
	}

	public int[] getPickedIndexs() {
		return null;
	}

}


