package se.leanbit.sats.fragments;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import se.leanbit.sats.R;

public class CustomCircleDraw extends View
{
    private final Rect textBounds = new Rect();
    private boolean mDrawCircleFill = true;
    private Paint mPaintFill;
    private Paint mPaintEmpty;
    private Paint mPaintDivider;
    private Paint mPaintRectangle;
    private Paint mPaintText;
    private Paint mPaintWeekDates;
    private int mWidth;
    private int mHeight;
    private float mCircleSize;
    private String mWeekdates;
    private float mTopBarHeight;
    private float mBottomBarHeight;
    private int mMaxAntalPass;
    private Bitmap mPinkMarker;
    private int mAntalPass;
    private float mSegmentHeight;
    private float mCircleStroke;
    private int mPassNextWeek;
    private int mPassLastWeek;
    private float mPinkMarkerSize;
    private Boolean mIsPastWeek;
    private Boolean mIsCurrentWeek;
    private boolean mIsLastBeforeWeek;

    public CustomCircleDraw(Context context, AttributeSet attrs)
    {
        super(context);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CustomCircleDraw,
                0, 0);
        init();
    }

    private void init()
    {
        mPaintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintEmpty = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintDivider = new Paint((Paint.ANTI_ALIAS_FLAG));
        mPaintText = new Paint((Paint.ANTI_ALIAS_FLAG));
        mPaintRectangle = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintWeekDates = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText.setColor(Color.WHITE);
        mPaintFill.setColor(getResources().getColor(R.color.circle_orange));
        mPaintEmpty.setColor(getResources().getColor(R.color.circle_orange));
        mPaintEmpty.setStyle(Paint.Style.STROKE);
        mCircleStroke = getResources().getDimension(R.dimen.circle_width);
        mPaintEmpty.setStrokeWidth(mCircleStroke);
        mCircleSize = getResources().getDimension(R.dimen.circle_size);
        mPaintDivider.setColor(Color.GRAY);
        mPaintDivider.setStrokeWidth(getResources().getDimension(R.dimen.stroke_width) / 2);
        mPaintText.setTextSize(getResources().getDimension(R.dimen.size_of_text));
        mPaintWeekDates.setTextSize(getResources().getDimension(R.dimen.size_of_text));
        mPaintRectangle.setColor(Color.WHITE);
        mPaintRectangle.setStyle(Paint.Style.FILL);
        mAntalPass = 0;
        mMaxAntalPass = 0;
        mIsCurrentWeek = false;
        mTopBarHeight = getResources().getDimension(R.dimen.height_of_top_rectangle);
        mBottomBarHeight = getResources().getDimension(R.dimen.height_of_bottom_rectangle);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.now_marker);
        mPinkMarkerSize = getResources().getDimension(R.dimen.pink_marker_size);
        mPinkMarker = Bitmap.createScaledBitmap(bitmap,(int)mPinkMarkerSize,(int)mPinkMarkerSize,false);
        mIsCurrentWeek = true;
        mIsPastWeek = false;
    }

    public void drawCircleFill(boolean drawCircleFill)
    {
        mDrawCircleFill = drawCircleFill;
        invalidate();
        requestLayout();
    }

    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        canvas.drawRect(0, 0, mWidth, mTopBarHeight, mPaintRectangle);
        mSegmentHeight = (mHeight - (mTopBarHeight + mBottomBarHeight)) / (mMaxAntalPass + 1);
        drawLines(canvas);

        if (mIsPastWeek)
        {
            if(mIsLastBeforeWeek)
            {
                drawCirclesBeforeLine(canvas);
                canvas.drawCircle(mWidth / 2, circlePosition(), mCircleSize, mPaintFill);
            }
            else
            {
                drawCirclesBeforeLine(canvas);
                drawCirclesAfterLine(canvas);
                canvas.drawCircle(mWidth / 2, circlePosition(), mCircleSize, mPaintFill);
            }

        }
        else
        {
            canvas.drawCircle(mWidth / 2, circlePosition(), mCircleSize - mCircleStroke / 2, mPaintEmpty);
            mPaintText.setColor(Color.BLACK);
        }

        if (mIsCurrentWeek)
        {
            canvas.drawBitmap(mPinkMarker, (mWidth / 2) - mPinkMarkerSize/2, mTopBarHeight -mPinkMarkerSize/5, null);
        }
        drawTextCentred(canvas, mPaintText, "" + mAntalPass, mWidth / 2, circlePosition());
        canvas.drawRect(0, mHeight - mBottomBarHeight, mWidth, mHeight, mPaintRectangle);
        canvas.drawLine(0, mHeight - mBottomBarHeight, mWidth, mHeight - mBottomBarHeight, mPaintDivider);
        drawTextCentred(canvas, mPaintWeekDates, mWeekdates, mWidth / 2, mHeight - (mBottomBarHeight) / 2); //weekdatesText
        canvas.drawLine(0, mHeight-1, mWidth, mHeight-1, mPaintDivider);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
       // Log.d("onSizeChanged", "sizeChanged...............x");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void drawTextCentred(Canvas canvas, Paint mPaintText, String text, float cx, float cy)
    {
        mPaintText.getTextBounds(text, 0, text.length(), textBounds);
        canvas.drawText(text, cx - textBounds.exactCenterX(), cy - textBounds.exactCenterY(), mPaintText);
    }

    public void drawLines(Canvas canvas)
    {
        if (mMaxAntalPass < 7)
        {
            for (int i = 0; i < mMaxAntalPass + 1; i++)
            {
                canvas.drawLine(0, (mSegmentHeight * (i)) + mTopBarHeight, mWidth, (mSegmentHeight * (i)) + mTopBarHeight, mPaintDivider);
            }

        }
        else
        {
            for (int i = 0; i < 8; i++)
            {
                canvas.drawLine(0, (mSegmentHeight * (i)+ mTopBarHeight), mWidth, (mSegmentHeight * (i)+ mTopBarHeight), mPaintDivider);
            }
        }
    }

    public void setWeekDates(String weekdates)
    {
        mWeekdates = weekdates;
        invalidate();
        requestLayout();
    }

    public void setMaxAntalPass(int maxAntalPass)
    {
        this.mMaxAntalPass = maxAntalPass;
        invalidate();
        requestLayout();
    }

    public void setIsCurrentWeek(Boolean isCurrentWeek)
    {
        this.mIsCurrentWeek = isCurrentWeek;
        invalidate();
        requestLayout();
    }

    void setAntalPass(int antalPass)
    {
        this.mAntalPass = antalPass;
        invalidate();
        requestLayout();
    }

    private void drawCirclesAfterLine(Canvas canvas)
    {
        canvas.drawLine(mWidth / 2, circlePosition()
                , mWidth + ((mWidth / 2)), nextCirclePos(), mPaintEmpty);
    }

    private void drawCirclesBeforeLine(Canvas canvas)
    {
        canvas.drawLine(mWidth / 2, circlePosition(), -(mWidth / 2), lastCirclePos(), mPaintEmpty);
    }

    private float nextCirclePos()
    {
        if (mPassNextWeek == -1)
        {
            return mHeight - mBottomBarHeight - (mSegmentHeight * mAntalPass);
        }
        return mHeight - mBottomBarHeight - (mSegmentHeight * mPassNextWeek);
    }

    private float lastCirclePos()
    {
        if (mPassLastWeek == -1)
        {
            return mHeight - mBottomBarHeight - (mSegmentHeight * mAntalPass);
        }
        return mHeight - mBottomBarHeight - (mSegmentHeight * mPassLastWeek);
    }

    float circlePosition()
    {
        return mHeight - mBottomBarHeight - (mSegmentHeight * mAntalPass);
    }

    public void setPassNextWeek(int passNextWeek)
    {
        this.mPassNextWeek = passNextWeek;
        invalidate();
        requestLayout();
    }

    public void setPassLastWeek(int passLastWeek)
    {
        this.mPassLastWeek = passLastWeek;
        invalidate();
        requestLayout();
    }
    public void isPastWeek(Boolean isPastWeek){
        this.mIsPastWeek = isPastWeek;
        invalidate();
        requestLayout();
    }

    public void isLastBeforeWeek(boolean is_last_before_week)
    {
        this.mIsLastBeforeWeek = is_last_before_week;
        invalidate();
        requestLayout();
    }
}
