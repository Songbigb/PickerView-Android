package me.ghui.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.OverScroller;

import java.util.List;

/**
 * Created by ghui on 11/21/15.
 */
public class PickerView extends View {
	private final String TAG = "PickerView";
	//scale of height and width
	private Context mContext;
	private Paint mPaint;
	private List<String> mSelections;
	private int mSelectIndex;
	private GestureDetectorCompat mGestureDetector;
	private float mTextAreaH;
	private int mDisplaySize;
	private int mSize;
	private OverScroller mScroller;
	private float mTextSize;
	private float mTextPaddingV;
	private int mTextColor;
	private int mDividerColor;
	private int mMaxOverScrollSize;
	private boolean mFling;


	public PickerView(Context context) {
		super(context);
		init(null);
	}

	public PickerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	public PickerView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(attrs);
	}

	private void init(AttributeSet attrs) {
		mContext = getContext();
		TypedArray ta = attrs == null ? null : getContext().obtainStyledAttributes(attrs, R.styleable.PickerView);
		if (ta != null) {
			try {
				mTextSize = ta.getDimension(R.styleable.PickerView_pvTextSize, dp(25));
				mTextPaddingV = mTextSize / 5f;
				mTextAreaH = mTextSize + 2 * mTextPaddingV;
				mTextColor = ta.getColor(R.styleable.PickerView_pvTextColor, Color.BLACK);
				mDividerColor = ta.getColor(R.styleable.PickerView_pvDividerColor, Color.GRAY);
				mDisplaySize = ta.getInt(R.styleable.PickerView_pvDisplaySize, 5);
			} finally {
				ta.recycle();
			}
		}
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mGestureDetector = new GestureDetectorCompat(mContext, new PickerViewGestureListener());
		mScroller = new OverScroller(mContext);
		mMaxOverScrollSize = Math.max(mDisplaySize / 2, 2);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int height = (int) (mDisplaySize * mTextAreaH);
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.getMode(heightMeasureSpec));
		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
	}

	public void setSelections(List<String> selections) {
		if (selections != null && selections.size() > 0) {
			mSelectIndex = selections.size() / 2;
		} else {
			mSelectIndex = 0;
		}
		setSelections(selections, mSelectIndex);
	}

	public void setSelections(List<String> selections, int selectIndex) {
		mSelections = selections;
		mSize = mSelections == null ? 0 : mSelections.size();
		select(selectIndex);
	}

	public void select(int index) {
		if (index < 0 || mSelections == null || index > mSelections.size() - 1) {
			throw new RuntimeException("invalid select index !");
		}
		mSelectIndex = index;
		//scroll the index item to the center
		scrollTo(0, (int) (mTextAreaH * (mSelectIndex - mDisplaySize / 2)));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int width = getMeasuredWidth();
		int cLeft = getPaddingLeft();
		int cTop = getPaddingTop();
		int cRight = width - getPaddingRight();
		int cWidth = cRight - cLeft;

		float dividerH = dp(1);
		//1.draw center line
		float lSx = cLeft + cWidth / 6f;
		float lSy = cTop + (mDisplaySize / 2) * mTextAreaH + getScrollY();
		float lEx = lSx + cWidth * 4 / 6f;
		float lEy = lSy;
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(dividerH);
		mPaint.setColor(mDividerColor);
		canvas.drawLine(lSx, lSy, lEx, lEy, mPaint);
		lSy = lEy = lSy + mTextAreaH;
		canvas.drawLine(lSx, lSy, lEx, lEy, mPaint);

		//2.draw center text
		mPaint.setColor(mTextColor);
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setTextAlign(Paint.Align.CENTER);
		mPaint.setTextSize(mTextSize);
		float sX = width / 2f;
		float acent = Math.abs(mPaint.getFontMetrics().ascent);
		float descent = Math.abs(mPaint.getFontMetrics().descent);
		float dY = cTop + mTextAreaH / 2f + (acent - descent) / 2;

		int start = mSelectIndex - mDisplaySize / 2;
		int end = mSelectIndex + mDisplaySize / 2;
		float sY = start * mTextAreaH;
		for (int i = start; i <= end; i++) {
			String text;
			if (i >= 0 && i < mSelections.size()) {
				text = mSelections.get(i);
			} else {
				text = "-";
			}
			canvas.drawText(text, sX, sY + dY, mPaint);
			sY += mTextAreaH;
		}
	}


	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
//			log("currY:" + mScroller.getCurrY());
			scrollTo(0, mScroller.getCurrY());
			refresh();
		} else {
			if (mFling) {
				mFling = false;
				autoSettle();
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean consume = mGestureDetector.onTouchEvent(event);
		if (MotionEvent.ACTION_UP == event.getAction()) {
			int scrollY = getScrollY();
			int halfSize = mDisplaySize / 2;
			if (scrollY < -halfSize * mTextAreaH) {
				mScroller.startScroll(0, scrollY, 0, (int) (-halfSize * mTextAreaH - scrollY));
				refresh();
			} else if (scrollY > (mSize - 1 - halfSize) * mTextAreaH) {
				mScroller.startScroll(0, scrollY, 0, (int) ((mSize - 1 - halfSize) * mTextAreaH - scrollY));
				refresh();
			} else {
				autoSettle();
			}
		}
		return true;
	}

	class PickerViewGestureListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			float deltaY;
			int scrollY = getScrollY();
			if (scrollY < -(mDisplaySize / 2 + mMaxOverScrollSize) * mTextAreaH) {
				deltaY = 0;
			} else if (scrollY < -(mDisplaySize / 2) * mTextAreaH) {
				deltaY = distanceY / 4;
			} else if (scrollY > (mSize - mDisplaySize / 2 + mMaxOverScrollSize) * mTextAreaH) {
				deltaY = 0;
			} else if (scrollY > (mSize - 1 - mDisplaySize / 2) * mTextAreaH) {
				deltaY = distanceY / 4;
			} else {
				deltaY = distanceY;
			}
			scrollBy(0, (int) deltaY);
			refresh();
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			int minY = (int) (-(mDisplaySize / 2) * mTextAreaH);
			int maxY = (int) ((mSize - mDisplaySize / 2) * mTextAreaH);
			int overY = (int) (mMaxOverScrollSize * mTextAreaH);
			mScroller.fling(0, getScrollY(), 0, (int) -velocityY, 0, 0, minY, maxY, 0, overY);
			mFling = true;
			ViewCompat.postInvalidateOnAnimation(PickerView.this);
			return true;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			int offset = (int) (getScrollY() + e.getY() - mDisplaySize / 2 * mTextAreaH);
			refresh(offset);
			return true;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			if (!mScroller.isFinished()) {
				mScroller.forceFinished(false);
			}
			mFling = false;
			if (null != getParent()) {
				getParent().requestDisallowInterceptTouchEvent(true);
			}
			return true;
		}

	}

	private void refresh(int offset) {
		mSelectIndex = (int) (offset / mTextAreaH + mDisplaySize / 2);
		invalidate();
	}

	private void refresh() {
		refresh(getScrollY());
	}

	private void autoSettle() {
		mScroller.startScroll(0, getScrollY(), 0, (int) ((mSelectIndex - mDisplaySize / 2) * mTextAreaH - getScrollY()));
		refresh();
	}

	private float dp(float dp) {
		return (int) (mContext.getResources().getDisplayMetrics().density * dp + 0.5);
	}

	private void log(String msg) {
		Log.d(TAG, msg);
	}

}
