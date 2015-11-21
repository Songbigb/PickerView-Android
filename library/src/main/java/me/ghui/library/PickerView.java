package me.ghui.library;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

/**
 * Created by ghui on 11/21/15.
 */
public class PickerView extends View {
	//scale of height and width
	private float mHWScale = 0.5f;
	private int mTopDividerColor = Color.parseColor("#AACFCFCF");
	private int mTextColor = Color.parseColor("#FF4A4A4A");

	private Context mContext;
	private Paint mPaint;
	private RectF mRectF;
	private List<String> mSelections;
	private int mSelectIndex;

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
		mRectF = new RectF();
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
		select(mSelectIndex);
	}

	public void select(int index) {
		if (index < 0 || mSelections == null || index > mSelections.size() - 1) {
			throw new RuntimeException("invalid select index !");
		}
		mSelectIndex = index;
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
		float textAreaH = cHeight / 5f;
		float lSy = cTop + 2 * textAreaH;
		float lEx = lSx + cWidth * 4 / 6f;
		float lEy = lSy;
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(dividerH);
		mPaint.setColor(mTopDividerColor);
		canvas.drawLine(lSx, lSy, lEx, lEy, mPaint);
		lSy = lEy = lSy + textAreaH;
		canvas.drawLine(lSx, lSy, lEx, lEy, mPaint);

		//2.draw center text
		mPaint.setColor(mTextColor);
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setTextAlign(Paint.Align.CENTER);
		float textPaddingV = textAreaH / 5;
		float textSize = textAreaH - textPaddingV * 2;
		mPaint.setTextSize(textSize);
		float sX = width / 2f;
		float acent = Math.abs(mPaint.getFontMetrics().ascent);
		float descent = Math.abs(mPaint.getFontMetrics().descent);
		float sY = cTop + textAreaH / 2f + (acent - descent) / 2;
		for (int i = 0; i < 5; i++) {
			String text;
			int index = mSelectIndex - (2 - i);
			if (index < 0 || index > mSelections.size() - 1) {
				text = "-";
			} else {
				text = mSelections.get(index);
			}
			canvas.drawText(text, sX, sY, mPaint);
			sY += textAreaH;
		}

	}


	private float dp(float dp) {
		return (int) (mContext.getResources().getDisplayMetrics().density * dp + 0.5);
	}

}
