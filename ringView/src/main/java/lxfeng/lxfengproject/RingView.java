package lxfeng.lxfengproject;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * 项目名称：LxfengProject
 * 类描述：
 * 创建人：liuxiufeng
 * 创建时间：2016/4/18 23:03
 */
public class RingView extends View {

    private static final String TAG = RingView.class.getSimpleName();

    private final int DEFAULT_SIZE = 200;
    private final int DEFAULT_RING_WIDTH = 20;//环形的宽度
    private final int DEFAULT_START_ANGLE = 30;//起始角度
    private final int DEFAULT_MAX_PROGRESS = 200;//默认最大进度
    private final int DEFAULT_PRIMARY_COLOR = 0XFF00FF00;
    private final int DEFAULT_SECONDARY_COLOR = 0X5500FF00;


    private Paint mPrimaryPaint;
    private Paint mSecondaryPaint;

    private int mPrimaryColor = DEFAULT_PRIMARY_COLOR;
    private int mSecondaryColor = DEFAULT_SECONDARY_COLOR;

    private int mWidth;
    private int mHeight;

    private int mCenterX;
    private int mCenterY;

    private int mRingWidth = DEFAULT_RING_WIDTH;
    private float mStartAngle = DEFAULT_START_ANGLE;
    private float mSweepAngle = 0;

    public RingView(Context context) {
        super(context);
        init();
    }

    public RingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RingView);
        mPrimaryColor = ta.getColor(R.styleable.RingView_ring_primary_color, DEFAULT_PRIMARY_COLOR);
        mSecondaryColor =
                ta.getColor(R.styleable.RingView_ring_secondary_color, DEFAULT_SECONDARY_COLOR);
        mRingWidth = ta.getDimensionPixelSize(R.styleable.RingView_ring_width, DEFAULT_RING_WIDTH);
        mStartAngle = ta.getInt(R.styleable.RingView_ring_start_angle, DEFAULT_START_ANGLE);
        ta.recycle();
        init();
    }

    private void init() {
        mPrimaryPaint = new Paint();
        mPrimaryPaint.setColor(mPrimaryColor);
        mPrimaryPaint.setAntiAlias(true);
        mPrimaryPaint.setStrokeWidth(mRingWidth);
        mPrimaryPaint.setStyle(Paint.Style.STROKE);
        mPrimaryPaint.setStrokeCap(Paint.Cap.ROUND);

        mSecondaryPaint = new Paint();
        mSecondaryPaint.setColor(mSecondaryColor);
        mSecondaryPaint.setAntiAlias(true);
        mSecondaryPaint.setStrokeWidth(mRingWidth);
        mSecondaryPaint.setStyle(Paint.Style.STROKE);
        mSecondaryPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(DEFAULT_SIZE, DEFAULT_SIZE);
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(DEFAULT_SIZE, heightSpecSize);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, DEFAULT_SIZE);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();
        mWidth = getWidth() - paddingLeft - paddingRight;
        mHeight = getHeight() - paddingTop - paddingBottom;
        mCenterX = getWidth() / 2;
        mCenterY = getHeight() / 2;
        RectF rectF =
                new RectF(mCenterX - mWidth / 2, mCenterY - mHeight / 2, mCenterX + mWidth / 2,
                        mCenterY + mHeight / 2);
        canvas.drawArc(rectF, 90 + mStartAngle, 360 - mStartAngle * 2, false, mSecondaryPaint);
        if(mSweepAngle > 0) {
            canvas.drawArc(rectF, 90 + mStartAngle, mSweepAngle, false, mPrimaryPaint);
        }
    }

    public void updateProgress(int progress) {
        if (progress >= DEFAULT_MAX_PROGRESS) return;
        mSweepAngle = progress * (360 - mStartAngle * 2) / DEFAULT_MAX_PROGRESS;
        invalidate();
    }

}
