package me.ghui.library;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by ghui on 11/21/15.
 */
public class PickerView extends View {
	//scale of height and width
	private float mHWScale = 0.5f;
	private String mOkText = "Ok";
	private String mCancleText = "Cancel";
	private int mTopDividerColor = Color.parseColor("#FF9B9B9B");
	private int mBtnColor = Color.parseColor("#FF4A4A4A");

	private Context mContext;
	private Paint mPaint;
	private RectF mRectF;

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

	public void setOkText(String okText) {
		mOkText = okText;
		postInvalidate();
	}

	public void setCancleText(String cancleText) {
		mCancleText = cancleText;
		postInvalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = (int) (width * mHWScale);
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.getMode(heightMeasureSpec));
		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
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

		//1. draw top divider
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setColor(mTopDividerColor);
		float dividerH = dp(1);
		mPaint.setStrokeWidth(dividerH);
		canvas.drawLine(cLeft, cTop + dividerH / 2f, cRight, cTop + dividerH / 2f, mPaint);

		//2. draw cancel btn
		float textSize = dp(16);
		mPaint.setColor(mBtnColor);
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setTextSize(textSize);
		float textPadding = textSize / 2f;
		float sX = cLeft;
		float sY = cTop;
		DrawUtils.drawCenterText(mCancleText, sX, sY, textPadding, textPadding, canvas, mPaint);

		//3. draw ok btn
		float eX = cRight;
		sY = cTop;
		DrawUtils.drawCenterTextRevrsed(mOkText, eX, sY, textPadding, textPadding, canvas, mPaint);
	}

	private float dp(float dp) {
		return (int) (mContext.getResources().getDisplayMetrics().density * dp + 0.5);
	}

}
