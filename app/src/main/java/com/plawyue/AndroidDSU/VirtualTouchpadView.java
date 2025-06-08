package com.plawyue.AndroidDSU;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class VirtualTouchpadView extends View {

    public interface TouchDataCallback {
        void onTouchData(byte[] data);
    }

    private TouchDataCallback callback;
    private final Paint paint = new Paint();
    private final Rect bounds = new Rect();

    // 存储两个触摸点的状态
    private final int[] activePointerIds = {-1, -1}; // 当前活动的两个触摸点ID
    private final int[] lastNormalizedX = new int[2]; // 两个点的最后X坐标
    private final int[] lastNormalizedY = new int[2]; // 两个点的最后Y坐标
    private final boolean[] isActive = new boolean[2]; // 两个点的激活状态

    public VirtualTouchpadView(Context context) {
        super(context);
        init();
    }

    public VirtualTouchpadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(60);
        drawable.setColor(ContextCompat.getColor(this.getContext(), R.color.buttonx));
        drawable.setStroke(1, ContextCompat.getColor(this.getContext(), R.color.spical));
        setBackground(drawable);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.background));
    }

    public void setTouchDataCallback(TouchDataCallback callback) {
        this.callback = callback;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        getDrawingRect(bounds);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(bounds, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        int pointerIndex = event.getActionIndex();
        int pointerId = event.getPointerId(pointerIndex);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                // 尝试将新触摸点分配到空闲槽位
                allocatePointerSlot(pointerId, event, pointerIndex);
                performHapticFeedback(HapticFeedbackConstants.CONFIRM);
                break;

            case MotionEvent.ACTION_MOVE:
                // 更新所有活动触摸点
                for (int i = 0; i < activePointerIds.length; i++) {
                    if (activePointerIds[i] != -1) {
                        int idx = event.findPointerIndex(activePointerIds[i]);
                        if (idx != -1) {
                            updatePointer(i, event, idx, true);
                        }
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // 释放所有触摸点
                for (int i = 0; i < activePointerIds.length; i++) {
                    if (activePointerIds[i] != -1) {
                        updatePointer(i, event, 0, false);
                        activePointerIds[i] = -1;
                    }
                }
                performHapticFeedback(HapticFeedbackConstants.CONFIRM);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                // 释放特定触摸点
                for (int i = 0; i < activePointerIds.length; i++) {
                    if (activePointerIds[i] == pointerId) {
                        updatePointer(i, event, pointerIndex, false);
                        activePointerIds[i] = -1;
                        performHapticFeedback(HapticFeedbackConstants.CONFIRM);
                        break;
                    }
                }
                break;
        }
        return true;
    }

    // 将新触摸点分配到空闲槽位
    private void allocatePointerSlot(int pointerId, MotionEvent event, int pointerIndex) {
        for (int i = 0; i < activePointerIds.length; i++) {
            if (activePointerIds[i] == -1) {
                activePointerIds[i] = pointerId;
                updatePointer(i, event, pointerIndex, true);
                break;
            }
        }
    }

    // 更新触摸点状态
    private void updatePointer(int slotIndex, MotionEvent event, int pointerIndex, boolean active) {
        float x = event.getX(pointerIndex);
        float y = event.getY(pointerIndex);

        lastNormalizedX[slotIndex] = normalizeCoordinatex(x, getWidth());
        lastNormalizedY[slotIndex] = normalizeCoordinatey(y, getHeight());
        isActive[slotIndex] = active;

        // 生成并发送触摸数据
        if (callback != null) {
            callback.onTouchData(createDualTouchData());
        }
        invalidate();
    }

    private int normalizeCoordinatex(float value, float max) {
        return (int) mapValue(value, 0, max, -DsuCtrlType.TOUCH_X_AXIS_MAX, DsuCtrlType.TOUCH_X_AXIS_MAX);
    }

    private int normalizeCoordinatey(float value, float max) {
        return (int) mapValue(value, 0, max, -DsuCtrlType.TOUCH_Y_AXIS_MAX, DsuCtrlType.TOUCH_Y_AXIS_MAX);
    }

    private static double mapValue(double value, double oldMin, double oldMax, double newMin, double newMax) {
        if (oldMin == oldMax) return 0;
        double scale = (newMax - newMin) / (oldMax - oldMin);
        return (value - oldMin) * scale + newMin;
    }

    // 生成包含两个触摸点数据的字节数组（12字节）
    private byte[] createDualTouchData() {
        ByteBuffer buffer = ByteBuffer.allocate(12).order(ByteOrder.LITTLE_ENDIAN);

        // 填充第一个触摸点数据
        buffer.put((byte) (isActive[0] ? 1 : 0));
        buffer.put((byte) (activePointerIds[0] & 0xFF));
        buffer.putShort((short) (lastNormalizedX[0] & 0xFFFF));
        buffer.putShort((short) (lastNormalizedY[0] & 0xFFFF));

        // 填充第二个触摸点数据
        buffer.put((byte) (isActive[1] ? 1 : 0));
        buffer.put((byte) (activePointerIds[1] & 0xFF));
        buffer.putShort((short) (lastNormalizedX[1] & 0xFFFF));
        buffer.putShort((short) (lastNormalizedY[1] & 0xFFFF));

        return buffer.array();
    }

    // 获取最后发送的触摸数据（12字节）
    public byte[] getLastTouchData() {
        return createDualTouchData();
    }
}