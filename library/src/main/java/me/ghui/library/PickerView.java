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
	private Context mContext;
	private Paint mPaint;
	private List<String> mSelections;
	private int mSelectIndex = 0;
	private int mLastSelectIndex = 0;
	private GestureDetectorCompat mGestureDetector;
	private float mCellHeight;
	private float mCellWidth;
	private int mDisplaySize;
	private int mHalfDisplaySize = 2;
	private int mSize;
	private OverScroller mScroller;
	private float mTextSize;
	private float mCellPaddingV;
	private float mCellPaddingH;
	private int mTextColor;
	private int mDividerColor;
	private int mMaxOverScrollSize;
	private boolean mFling = false;
	private boolean mTapUp = false;
	private boolean mScroll = false;
	private PickerListener mPickChangeListener;

	public PickerView(Context context) {
		this(context, null);
	}

	public PickerView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PickerView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(attrs);
	}

	private void init(AttributeSet attrs) {
		mContext = getContext();
		TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.PickerView, 0, 0);
		if (ta != null) {
			try {
				mTextSize = ta.getDimension(R.styleable.PickerView_pvTextSize, dp(25));
				mCellPaddingV = ta.getDimension(R.styleable.PickerView_pvTextPaddingV, mTextSize / 5);
				mCellPaddingH = ta.getDimension(R.styleable.PickerView_pvTextPaddingH, mTextSize / 5);
				mCellHeight = mTextSize + 2 * mCellPaddingV;
				mCellWidth = mTextSize + 2 * mCellPaddingH;
				mTextColor = ta.getColor(R.styleable.PickerView_pvTextColor, Color.BLACK);
				mDividerColor = ta.getColor(R.styleable.PickerView_pvDividerColor, Color.GRAY);
				mDisplaySize = ta.getInt(R.styleable.PickerView_pvDisplaySize, 5);
				mHalfDisplaySize = mDisplaySize / 2;
			} finally {
				ta.recycle();
			}
		}
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setTextSize(mTextSize);
		mGestureDetector = new GestureDetectorCompat(mContext, new PickerViewGestureListener());
		mScroller = new OverScroller(mContext);
		mMaxOverScrollSize = Math.max(mHalfDisplaySize, 2);
		measureCellWidth();
		select(0);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight());
	}

	private int measureWidth(int widthSpec) {
		int mode = MeasureSpec.getMode(widthSpec);
		int size = MeasureSpec.getSize(widthSpec);
		if (mode == MeasureSpec.AT_MOST) {
			size = (int) mCellWidth;
		} else {
			size = (int) Math.max(size, mCellWidth);
		}
		return MeasureSpec.makeMeasureSpec(size, mode);
	}

	private int measureHeight() {
		int size = (int) (mCellHeight * mDisplaySize);
		return MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
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
		measureCellWidth();
		select(selectIndex);
	}

	private void measureCellWidth() {
		if (mSelections == null || mSelections.isEmpty()) {
			return;
		}
		float newSize;
		for (String cell : mSelections) {
			newSize = mPaint.measureText(cell) + 2 * mCellPaddingH;
			mCellWidth = Math.max(mCellWidth, newSize);
		}
	}

	public void select(int index) {
		if (index < 0 || index > mSize - 1 && mSize != 0) {
			throw new RuntimeException("invalid select index !");
		}
		mSelectIndex = index;
		//scroll the index item to center
		scrollTo(0, (int) (mCellHeight * (mSelectIndex - mHalfDisplaySize)));
		invalidate();
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
		float lSy = cTop + (mHalfDisplaySize) * mCellHeight + getScrollY();
		float lEx = lSx + cWidth * 4 / 6f;
		float lEy = lSy;
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(dividerH);
		mPaint.setColor(mDividerColor);
		canvas.drawLine(lSx, lSy, lEx, lEy, mPaint);
		lSy = lEy = lSy + mCellHeight;
		canvas.drawLine(lSx, lSy, lEx, lEy, mPaint);

		//2.draw center text
		mPaint.setColor(mTextColor);
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setTextAlign(Paint.Align.CENTER);
		float sX = width / 2f;
		float acent = Math.abs(mPaint.getFontMetrics().ascent);
		float descent = Math.abs(mPaint.getFontMetrics().descent);
		float dY = cTop + mCellHeight / 2f + (acent - descent) / 2;

		int start = mSelectIndex - mHalfDisplaySize - mMaxOverScrollSize;
		int end = mSelectIndex + mHalfDisplaySize + mMaxOverScrollSize;

		float sY = start * mCellHeight;
		for (int i = start; i <= end; i++) {
			String text;
			if (i >= 0 && i < mSize) {
				text = mSelections.get(i);
			} else {
				text = "-";
			}
			canvas.drawText(text, sX, sY + dY, mPaint);
			sY += mCellHeight;
		}
	}


	class PickerViewGestureListener extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			mTapUp = true;
			int deltaY = (int) ((-mHalfDisplaySize + Math.floor(e.getY() / mCellHeight)) * mCellHeight);
			scrollY(deltaY);
			return true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			Log.e("test", "onScroll");
			mScroll = true;
			float deltaY;
			int scrollY = getScrollY();
			if (scrollY < -(mHalfDisplaySize + mMaxOverScrollSize) * mCellHeight) {
				deltaY = 0;
			} else if (scrollY < -(mHalfDisplaySize) * mCellHeight) {
				deltaY = distanceY / 4;
			} else if (scrollY > (mSize - mHalfDisplaySize + mMaxOverScrollSize) * mCellHeight) {
				deltaY = 0;
			} else if (scrollY > (mSize - 1 - mHalfDisplaySize) * mCellHeight) {
				deltaY = distanceY / 4;
			} else {
				deltaY = distanceY;
			}
			scrollBy(0, (int) deltaY);
			refreshSelectedIndex();
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			Log.e("test", "onFling");
			int minY = (int) (-(mHalfDisplaySize) * mCellHeight);
			int maxY = (int) ((mSize - mHalfDisplaySize - 1) * mCellHeight);
			int overY = (int) (mMaxOverScrollSize * mCellHeight);
			mFling = true;
			mScroller.fling(0, getScrollY(), 0, (int) -(velocityY), 0, 0, minY, maxY, 0, overY);
			ViewCompat.postInvalidateOnAnimation(PickerView.this);
			return true;
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean consume = mGestureDetector.onTouchEvent(event);
		if (!mFling && !mTapUp && mScroll && MotionEvent.ACTION_UP == event.getAction()) {
			mScroll = false;
			int scrollY = getScrollY();
			int deltaY;
			if (scrollY < -mHalfDisplaySize * mCellHeight) {
				deltaY = (int) (-mHalfDisplaySize * mCellHeight - scrollY);
			} else if (scrollY > (mSize - 1 - mHalfDisplaySize) * mCellHeight) {
				deltaY = (int) ((mSize - 1 - mHalfDisplaySize) * mCellHeight - scrollY);
			} else {
				deltaY = (int) ((mSelectIndex - mHalfDisplaySize) * mCellHeight - getScrollY());
			}
			scrollY(deltaY);
			consume = true;
		}
		return consume || super.onTouchEvent(event);
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(0, mScroller.getCurrY());
			refreshSelectedIndex();
		} else {
			if (mFling) {
				mFling = false;
				autoScrollY();
			}
			if (mTapUp) {
				mTapUp = false;
				autoScrollY();
			}

		}
	}

	private void scrollY(int deltaY) {
		mScroller.startScroll(0, getScrollY(), 0, deltaY);
		invalidate();
	}

	private void autoScrollY() {
		int deltaY = (int) ((mSelectIndex - mHalfDisplaySize) * mCellHeight - getScrollY());
		scrollY(deltaY);
	}

	private void refreshSelectedIndex(int offset) {
		mLastSelectIndex = mSelectIndex;
		mSelectIndex = (int) (offset / mCellHeight + mHalfDisplaySize);
		if (mSelectIndex < 0) {
			mSelectIndex = 0;
		} else if (mSelectIndex > mSize - 1) {
			mSelectIndex = mSize - 1;
		}
		invalidate();
		if (mPickChangeListener != null) {
			mPickChangeListener.onPicking(mSelectIndex);
			if (mLastSelectIndex != mSelectIndex) {
				mPickChangeListener.onPicked(mSelectIndex);
			}
		}
	}

	private void refreshSelectedIndex() {
		refreshSelectedIndex(getScrollY());
	}

	private int dp(float dp) {
		return (int) (mContext.getResources().getDisplayMetrics().density * dp + 0.5);
	}

	public void setPickChangeListener(PickerListener listener) {
		mPickChangeListener = listener;
	}

	public interface PickerListener {
		void onPicking(int index);

		// TODO(ghui): 11/28/15
		void onPicked(int index);
	}

	private void log(String msg) {
		Log.e(TAG, msg);
	}


}
