package com.udacity

import android.animation.*
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlin.properties.Delegates


class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var attrSet = context.obtainStyledAttributes(attrs, R.styleable.LoadingButton)
    private var widthSize = 0
    private var heightSize = 0
    private var currentPercentage = 0
    private val ovalSpace = RectF()

    private val loadingCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply{
        style = Paint.Style.FILL
        color = attrSet.getColor(R.styleable.LoadingButton_circleColor, Color.RED)
    }

    //Main button in unclicked state
    private val rectPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply{
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 50f
        color = attrSet.getColor(R.styleable.LoadingButton_buttonColor, Color.BLACK)
    }

    //Button on clicked state and animation
    private val filledRectPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply{
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 50f
        color = context.getColor(R.color.colorPrimaryDark)
    }


    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply{
        textAlign = Paint.Align.CENTER
        textSize = resources.getDimension(R.dimen.default_text_size)
        color = context.getColor(R.color.button_standard_text_color)
        typeface = Typeface.create("", Typeface.BOLD)
    }

    private val valueAnimator = ValueAnimator()

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Loading -> {
                animateProgress()
            }
            ButtonState.Completed -> {
                valueAnimator.cancel()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        when(buttonState){
            is ButtonState.Completed -> drawUnclickedButton(canvas)
            is ButtonState.Loading -> {
                drawUnclickedButton(canvas)

                drawLoadingButton(canvas)

                drawOrangeCircle(canvas)
            }
        }
        //Drawing Button Text, depending on the state
        drawButtonText(canvas)
    }

    //drawing the unclicked button
    private fun drawUnclickedButton(canvas: Canvas){
        val emptyRect = RectF(0f, heightSize.toFloat(), widthSize.toFloat(), 0f)
        canvas.drawRect(emptyRect, rectPaint)
        invalidate()
    }

    //drawing the button TEXT, depending on the state
    private fun drawButtonText(canvas: Canvas){
        val buttonText = when(buttonState){
            is ButtonState.Completed -> resources.getString(R.string.button_download)
            else -> resources.getString(R.string.button_loading)
        }
        resources.getString(R.string.button_download)
        val textHeight: Float = textPaint.descent() - textPaint.ascent()
        val textOffset: Float = textHeight / 2 - textPaint.descent()
        canvas.drawText(buttonText, (widthSize/2).toFloat(), heightSize.toFloat() / 2 + textOffset, textPaint)
        invalidate()
    }
    //drawing the loading button
    private fun drawLoadingButton(canvas: Canvas){
        val percentageToFill = getCurrentPercentageToFill()
        val fillingRect = RectF(0f, heightSize.toFloat(), percentageToFill, 0f)

        //animating the filled button
        canvas.drawRect(fillingRect, filledRectPaint)
        invalidate()
    }

    //drawing the orange circle
    private fun drawOrangeCircle(canvas: Canvas){
        val horizontalCenter = ((width/2)+(width/3)).toFloat()
        val verticalCenter = (height/2).toFloat()
        val ovalSize = 30
        ovalSpace.set(
            horizontalCenter - ovalSize,
            verticalCenter - ovalSize,
            horizontalCenter + ovalSize,
            verticalCenter + ovalSize
        )

        val percentageToFill = getCurrentPercentageToFill()

        //animating the filled button
        canvas.drawArc(ovalSpace, 0f, percentageToFill, false, loadingCirclePaint)
        invalidate()
    }

    private fun getCurrentPercentageToFill() = (widthSize * (currentPercentage / PERCENTAGE_DIVIDER)).toFloat()

    fun animateProgress() {
        //holdes animation values from 0 to 100
        val valuesHolder = PropertyValuesHolder.ofFloat("percentage", 0f, 100f)

        //instance of ValueAnimator
        valueAnimator.apply {
            setValues(valuesHolder)
            //need to set the duration to the duration of the download
            duration = 1000
            addUpdateListener {
                val percentage = it.getAnimatedValue(PERCENTAGE_VALUE_HOLDER) as Float
                currentPercentage = percentage.toInt()
                invalidate()
            }
        }
        valueAnimator.start()
        valueAnimator.repeatCount = ObjectAnimator.INFINITE
        valueAnimator.repeatMode = ObjectAnimator.RESTART
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    companion object {
        const val PERCENTAGE_DIVIDER = 100.0
        const val PERCENTAGE_VALUE_HOLDER = "percentage"
    }

    override fun performClick(): Boolean {
        if (super.performClick()) return true

        invalidate()
        return true
    }
}