package me.ghui.library;

import android.content.Context;
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
	private float mHWScale = 0.5f;
	private int mTopDividerColor = Color.parseColor("#AACFCFCF");
	private int mTextColor = Color.parseColor("#FF4A4A4A");

	private Context mContext;
	private Paint mPaint;
	private List<String> mSelections;
	private int mSelectIndex;
	int mIndexBeforeScroll;
	private GestureDetectorCompat mGestureDetector;
	private float mTextAreaH;
	public static final int DEFAULT_MAX_OVERSCROLL_SIZE = 2;

	private int mMaxOverScrollSize = DEFAULT_MAX_OVERSCROLL_SIZE;
	private int mSelectionSize;
	private OverScroller mScroller;

	public PickerView(Context context) {
		super(context);
		init();
	}

	public PickerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PickerView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		mContext = getContext();
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mGestureDetector = new GestureDetectorCompat(mContext, new PickerViewGestureListener());
		mScroller = new OverScroller(mContext);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = (int) (width * mHWScale);
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
		scrollTo(0, 144 * (mSelectIndex - 2));
	}

	public void setMaxOverScrolSize(int size) {
		mMaxOverScrollSize = size;
		postInvalidate();
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
		mTextAreaH = cHeight / 5f;
		log("h:" + mTextAreaH);
		float lSy = cTop + 2 * mTextAreaH + getScrollY();
		float lEx = lSx + cWidth * 4 / 6f;
		float lEy = lSy;
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(dividerH);
		mPaint.setColor(mTopDividerColor);
		canvas.drawLine(lSx, lSy, lEx, lEy, mPaint);
		lSy = lEy = lSy + mTextAreaH;
		canvas.drawLine(lSx, lSy, lEx, lEy, mPaint);

		//2.draw center text
		mPaint.setColor(mTextColor);
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setTextAlign(Paint.Align.CENTER);
		float textPaddingV = mTextAreaH / 5;
		float textSize = mTextAreaH - textPaddingV * 2;
		mPaint.setTextSize(textSize);
		float sX = width / 2f;
		float acent = Math.abs(mPaint.getFontMetrics().ascent);
		float descent = Math.abs(mPaint.getFontMetrics().descent);
		float dY = cTop + mTextAreaH / 2f + (acent - descent) / 2;

		int start = mSelectIndex - 2;
		int end = mSelectIndex + 2;
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
		mGestureDetector.onTouchEvent(event);
		return true;
	}

	class PickerViewGestureListener extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			scrollBy(0, (int) distanceY);
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


	private float dp(float dp) {
		return (int) (mContext.getResources().getDisplayMetrics().density * dp + 0.5);
	}

	private void log(String msg) {
		Log.d(TAG, msg);
	}

}
