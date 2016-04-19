package lxfeng.elasticscrollview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

/**
 * 项目名称：LxfengProject
 * 类描述：
 * 创建人：liuxiufeng
 */
public class ElasticScrollView extends ScrollView {

    private static final String TAG = ElasticScrollView.class.getSimpleName();
    private static final int DEFAULT_ELASTIC_DISTANCE = 300;
    private static final float SCROLL_PATIO = 0.5f;
    private static final String DEFAULT_TEXT = "下拉回弹";

    private View mInnerView;
    private int mTouchSlop;
    private Rect mOutRect = new Rect();
    private int mTextLength;
    private int mScrollDistance;
    private boolean isAnimationEnd = true;
    private float mStartY;

    private Paint mPaint = new Paint();

    public ElasticScrollView(Context context) {
        super(context);
        init(context);
    }

    public ElasticScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ElasticScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(30);
        Rect rect = new Rect();
        mPaint.getTextBounds(DEFAULT_TEXT,0,DEFAULT_TEXT.length(),rect);
        mTextLength = rect.width();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 0) {
            mInnerView = getChildAt(0);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mStartY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                return Math.abs(ev.getY() - mStartY) > mTouchSlop;//检测是否为滚动
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float preY = mStartY == 0 ? ev.getY() : mStartY;
                float nowY = ev.getY();
                int deltaY = (int)(nowY - preY);
                mStartY = nowY;
                Log.d(TAG,"deltaY:"+deltaY);
                if (isPullDown() && isAnimationEnd) {
                    Log.d(TAG,"start pull down");
                    if (mOutRect.isEmpty()) {
                        mOutRect.set(mInnerView.getLeft(), mInnerView.getTop(),
                                mInnerView.getRight(), mInnerView.getBottom());
                    }
                    if (deltaY > DEFAULT_ELASTIC_DISTANCE) deltaY = DEFAULT_ELASTIC_DISTANCE;
                    int moveDistance = (int)(deltaY * SCROLL_PATIO);
                    mScrollDistance += moveDistance;
                    if (mScrollDistance <= DEFAULT_ELASTIC_DISTANCE && deltaY > 0) {
                        mInnerView.layout(mInnerView.getLeft(), mInnerView.getTop() + moveDistance,
                                mInnerView.getRight(), mInnerView.getBottom() + moveDistance);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isStartAnimation()){
                    startElasticAnimation();
                }
                break;
        }

        return super.onTouchEvent(ev);
    }

    private boolean isPullDown() {
        return getScrollY() == 0;
    }

    private boolean isStartAnimation(){
        return !mOutRect.isEmpty();
    }

    private void startElasticAnimation(){
        Log.d(TAG,"OutRect:"+mOutRect.top);
        Log.d(TAG, "InnerView:" + mInnerView.getTop());
        TranslateAnimation ta = new TranslateAnimation(0,0,0,mOutRect.top - mInnerView.getTop());
        ta.setDuration(400);
        ta.setInterpolator(new DecelerateInterpolator());
        mInnerView.setAnimation(ta);
        ta.start();
        ta.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isAnimationEnd = false;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mInnerView.clearAnimation();
                mInnerView.layout(mOutRect.left, mOutRect.top, mOutRect.right, mOutRect.bottom);
                mOutRect.setEmpty();
                mScrollDistance = 0;
                isAnimationEnd = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText(DEFAULT_TEXT,(getWidth()- mTextLength) / 2, 70, mPaint);
    }
}
