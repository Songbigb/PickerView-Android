package me.ghui.library;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by ghui on 10/18/15.
 */
public class DrawUtils {
	private static Rect tempRect = new Rect();
	private static RectF rectF = new RectF();

	/**
	 * @param text
	 * @param cX
	 * @param cY
	 * @param canvas
	 * @param paint
	 * @return the text bounds
	 */
	public static void drawCenterText(String text, float cX, float cY, Canvas canvas, Paint paint) {
		if (text == null) {
			return;
		}
		Paint.Align align = paint.getTextAlign();
		paint.getTextBounds(text, 0, text.length(), tempRect);
		float x;
		//x
		if (align == Paint.Align.LEFT) {
			x = cX - paint.measureText(text) / 2f - tempRect.left / 2f;
		} else if (align == Paint.Align.CENTER) {
			x = cX - tempRect.left / 2f;
		} else {
			x = cX + paint.measureText(text) / 2f + tempRect.left / 2f;
		}
		float y;
		//y
		Paint.FontMetrics metrics = paint.getFontMetrics();
		float acent = Math.abs(metrics.ascent);
		float descent = Math.abs(metrics.descent);
		y = cY + (acent - descent) / 2f;
		canvas.drawText(text, x, y, paint);
	}

	public static void drawCenterText(String text, float sX, float sY, float paddingH, float paddingV, Canvas canvas, Paint paint) {
		paint.getTextBounds(text, 0, text.length(), tempRect);

		float textWidth = tempRect.width();
		float eX = sX + 2 * paddingH + textWidth;
		float cX = (sX + eX) / 2f;

		float textHeight = tempRect.height();
		float eY = sY + 2 * paddingV + textHeight;
		float cY = (sY + eY) / 2f;

		drawCenterText(text, cX, cY, canvas, paint);
	}

	public static void drawCenterTextRevrsed(String text, float eX, float sY, float paddingH, float paddingV, Canvas canvas, Paint paint) {
		paint.getTextBounds(text, 0, text.length(), tempRect);

		float textWidth = tempRect.width();
		float cX = eX - paddingH - textWidth / 2f;
		float textHeight = tempRect.height();
		float cY = sY + paddingV + textHeight / 2f;

		drawCenterText(text, cX, cY, canvas, paint);
	}

	public static void drawCenterVerticalText(String text, float startX, float centerY, Canvas canvas, Paint paint) {
		if (text == null) {
			return;
		}
		Paint.FontMetrics metrics = paint.getFontMetrics();
		float acent = Math.abs(metrics.ascent);
		float descent = Math.abs(metrics.descent);
		float y = centerY + (acent - descent) / 2f;
		canvas.drawText(text, startX, y, paint);
	}

	public static void drawCenterVerticalTextRevrsed(String text, float endX, float centerY, Canvas canvas, Paint paint) {
		if (text == null) {
			return;
		}
		float textWidth = paint.measureText(text, 0, text.length());
		float startX = endX - textWidth;
		drawCenterVerticalText(text, startX, centerY, canvas, paint);
	}

	public static Paint getScaledTextPaint(Paint paint, float desiredWidth, float maxTextSize, String text) {
		final float testTextSize = 48f;
		paint.setTextSize(testTextSize);
		Rect bounds = new Rect();
		paint.getTextBounds(text, 0, text.length(), bounds);
		float desiredTextSize = testTextSize * desiredWidth / bounds.width();
		if (maxTextSize != -1) {
			desiredTextSize = Math.min(maxTextSize, desiredTextSize);
		}
		paint.setTextSize(desiredTextSize);
		return paint;
	}
}
