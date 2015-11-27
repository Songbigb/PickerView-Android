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
	private GestureDetectorCompat mGestureDetector;
	private float mCellHeight;
	private float mCellWidth;
	private int mDisplaySize;
	private int mSize;
	private OverScroller mScroller;
	private float mTextSize;
	private float mCellPaddingV;
	private float mCellPaddingH;
	private int mTextColor;
	private int mDividerColor;
	private int mMaxOverScrollSize;
	private boolean mFling = false;
	private int mHalfSize = 2;

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
				mCellPaddingV = ta.getDimension(R.styleable.PickerView_pvTextPaddingV, mTextSize / 5);
				mCellPaddingH = ta.getDimension(R.styleable.PickerView_pvTextPaddingH, mTextSize / 5);
				mCellHeight = mTextSize + 2 * mCellPaddingV;
				mCellWidth = mTextSize + 2 * mCellPaddingH;
				mTextColor = ta.getColor(R.styleable.PickerView_pvTextColor, Color.BLACK);
				mDividerColor = ta.getColor(R.styleable.PickerView_pvDividerColor, Color.GRAY);
				mDisplaySize = ta.getInt(R.styleable.PickerView_pvDisplaySize, 5);
				mHalfSize = mDisplaySize / 2;
			} finally {
				ta.recycle();
			}
		}
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setTextSize(mTextSize);
		mGestureDetector = new GestureDetectorCompat(mContext, new PickerViewGestureListener());
		mScroller = new OverScroller(mContext);
		mMaxOverScrollSize = Math.max(mHalfSize, 2);
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
		scrollTo(0, (int) (mCellHeight * (mSelectIndex - mHalfSize)));
		postInvalidate();
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
		float lSy = cTop + (mHalfSize) * mCellHeight + getScrollY();
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

		int start = mSelectIndex - mHalfSize;
		int end = mSelectIndex + mHalfSize;
		float sY = start * mCellHeight;
		for (int i = start; i <= end; i++) {
			String text;
			if (i >= 0 && mSelections != null && i < mSelections.size()) {
				text = mSelections.get(i);
			} else {
				text = "-";
			}
			canvas.drawText(text, sX, sY + dY, mPaint);
			sY += mCellHeight;
		}
	}


	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
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
		if (!mFling && MotionEvent.ACTION_UP == event.getAction()) {
			int scrollY = getScrollY();
			if (scrollY < -mHalfSize * mCellHeight) {
				mScroller.startScroll(0, scrollY, 0, (int) (-mHalfSize * mCellHeight - scrollY));
				refresh();
			} else if (scrollY > (mSize - 1 - mHalfSize) * mCellHeight) {
				mScroller.startScroll(0, scrollY, 0, (int) ((mSize - 1 - mHalfSize) * mCellHeight - scrollY));
				refresh();
			} else {
				autoSettle();
			}
			consume = true;
		}
		return consume | super.onTouchEvent(event);
	}

	class PickerViewGestureListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			float deltaY;
			int scrollY = getScrollY();
			if (scrollY < -(mHalfSize + mMaxOverScrollSize) * mCellHeight) {
				deltaY = 0;
			} else if (scrollY < -(mHalfSize) * mCellHeight) {
				deltaY = distanceY / 4;
			} else if (scrollY > (mSize - mHalfSize + mMaxOverScrollSize) * mCellHeight) {
				deltaY = 0;
			} else if (scrollY > (mSize - 1 - mHalfSize) * mCellHeight) {
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
			int minY = (int) (-(mHalfSize) * mCellHeight);
			int maxY = (int) ((mSize - 1 - mHalfSize) * mCellHeight);
			int overY = (int) (mMaxOverScrollSize * mCellHeight);
			mFling = true;
			mScroller.fling(0, getScrollY(), 0, (int) -(velocityY), 0, 0, minY, maxY, 0, overY);
			ViewCompat.postInvalidateOnAnimation(PickerView.this);
			return true;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			int offset = (int) (getScrollY() + e.getY() - mHalfSize * mCellHeight);
			refresh(offset);
			return true;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			if (!mScroller.isFinished()) {
				mScroller.forceFinished(false);
			}
			mFling = false;
			return true;
		}
	}

	private void refresh(int offset) {
		mSelectIndex = (int) (offset / mCellHeight + mHalfSize);
		invalidate();
	}

	private void refresh() {
		refresh(getScrollY());
	}

	private void autoSettle() {
		mScroller.startScroll(0, getScrollY(), 0, (int) ((mSelectIndex - mHalfSize) * mCellHeight - getScrollY()));
		refresh();
	}

	private float dp(float dp) {
		return (int) (mContext.getResources().getDisplayMetrics().density * dp + 0.5);
	}

	private void log(String msg) {
		Log.d(TAG, msg);
	}

}
