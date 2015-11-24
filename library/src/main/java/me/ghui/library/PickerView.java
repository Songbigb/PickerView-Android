package me.ghui.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.GestureDetectorCompat;
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
	int mIndexBeforeScroll;
	private GestureDetectorCompat mGestureDetector;
	private float mTextAreaH;
	private int mMaxOverScrollSize;
	private int mDisplaySize;
	private int mSelectionSize;
	private OverScroller mScroller;
	private float mTextSize;
	private float mTextPaddingV;
	private int mTextColor;
	private int mDividerColor;

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
				mMaxOverScrollSize = ta.getInt(R.styleable.PickerView_pvOverScrollSize, 2);
				mDisplaySize = ta.getInt(R.styleable.PickerView_pvDisplaySize, 5);
			} finally {
				ta.recycle();
			}
		}
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mGestureDetector = new GestureDetectorCompat(mContext, new PickerViewGestureListener());
		mScroller = new OverScroller(mContext);
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
		mSelectionSize = mSelections == null ? 0 : mSelections.size();
		select(selectIndex);
	}

	public void select(int index) {
		if (index < 0 || mSelections == null || index > mSelections.size() - 1) {
			throw new RuntimeException("invalid select index !");
		}
		mSelectIndex = index;
		mIndexBeforeScroll = mSelectIndex;
		scrollTo(0, (int) (mTextAreaH * (mSelectIndex - mDisplaySize / 2)));
	}

	public void setMaxOverScrolSize(int size) {
		mMaxOverScrollSize = size;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int height = getMeasuredHeight();
		int width = getMeasuredWidth();
		int cLeft = getPaddingLeft();
		int cTop = getPaddingTop();
		int cRight = width - getPaddingRight();
		int cBottom = height - getPaddingBottom();
		int cHeight = cBottom - cTop;
		int cWidth = cRight - cLeft;

		float dividerH = dp(1);
		//1.draw center line
		float lSx = cLeft + cWidth / 6f;
		float lSy = cTop + (mDisplaySize / 2) * mTextAreaH + getScrollY();
		float lEx = lSx + cWidth * 4 / 6f;
		float lEy = lSy;
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(dividerH);
		mPaint.setColor(mTextColor);
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

		if (mSelectIndex == 0) {
			start -= mMaxOverScrollSize;
		} else if (mSelectIndex == mSelections.size() - 1) {
			end += mMaxOverScrollSize;
		}
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
	public boolean onTouchEvent(MotionEvent event) {
		boolean result = mGestureDetector.onTouchEvent(event);
		if (event.getAction() == MotionEvent.ACTION_UP) {
			int scrollY = getScrollY();
			if (scrollY < -(mMaxOverScrollSize + mDisplaySize / 2) * mTextAreaH) {
				//scroll back to index =0

			} else if (scrollY > (mSelectionSize - mDisplaySize / 2) * mTextAreaH) {
				//scroll back to index = size-1
			} else {
				autoSettle();
			}
		}
		return true;
	}

	class PickerViewGestureListener extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			int scrollY = getScrollY();
			float deltaY = distanceY;
			if (scrollY < -(mMaxOverScrollSize + mDisplaySize / 2) * mTextAreaH) {
//				log("disY 1");
				deltaY = 0;
			} else if (scrollY < -(mDisplaySize / 2) * mTextAreaH) {
//				log("disY 2");
				deltaY /= 4;
			} else if (scrollY > (mSelectionSize - mDisplaySize / 2 - 1 + mMaxOverScrollSize) * mTextAreaH) {
//				log("disY 4");
				deltaY = 0;
			} else if (scrollY > (mSelectionSize - mDisplaySize / 2) * mTextAreaH) {
//				log("disY 3");
				deltaY /= 4;
			} else {
				deltaY = distanceY;
			}
			scrollBy(0, (int) deltaY);
			refreshCenter();
			return true;
		}
	}

	private void refreshCenter() {
		int scrollY = getScrollY();
		int deltaIndex = Math.round(scrollY / mTextAreaH);
		mSelectIndex = mIndexBeforeScroll + deltaIndex;
		log("scrollY:" + scrollY + ",delta:" + deltaIndex + ",mSelectIndex:" + mSelectIndex);
		invalidate();
	}

	private void autoSettle() {

	}


	private float dp(float dp) {
		return (int) (mContext.getResources().getDisplayMetrics().density * dp + 0.5);
	}

	private void log(String msg) {
		Log.d(TAG, msg);
	}

}
