package com.uoko.frame.dialog

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.uoko.frame.R
import com.uoko.frame.common.dp2px
import com.uoko.frame.expansion.click

/**
 * 作者: xwb on 2018/8/7
 * 描述:
 */
class UKLoadingLayout : FrameLayout {
    private val LOADING_TAG = "wb_loading_tag"

    private lateinit var mLoadView: LinearLayout

    private lateinit var mErrorView: LinearLayout

    private lateinit var mLAVLoadImage: LottieAnimationView

    private lateinit var mTvHintContent: TextView

    private lateinit var mTvErrorText: TextView

    private lateinit var mBtnOperation: TextView

    private lateinit var mErrorIm: ImageView

    private var mContentTxtColor: Int = 0

    private var mBtnTxtColor: Int = 0

    private var mDefaultColor: Int = 0

    private var autoLoading = false
    private val INLOADING = 1
    private val COMPLETE = 2
    private val ERROR = 3
    private val EMPTY = 4
    private var currentState: Int = INLOADING

    @JvmOverloads
    constructor(context: Context, attributes: AttributeSet? = null, defStyleAttr: Int = 0) : super(
        context,
        attributes,
        defStyleAttr
    ) {


        mDefaultColor = ContextCompat.getColor(context, R.color.black)
        val typedArray = context.obtainStyledAttributes(attributes, R.styleable.UKLoadingLayout)

        mContentTxtColor = if (typedArray.hasValue(R.styleable.UKLoadingLayout_contentColor))
            typedArray.getColor(
                R.styleable.UKLoadingLayout_contentColor,
                ContextCompat.getColor(context, mDefaultColor)
            ) else mDefaultColor

        mBtnTxtColor = if (typedArray.hasValue(R.styleable.UKLoadingLayout_btnTxtColor))
            typedArray.getColor(R.styleable.UKLoadingLayout_btnTxtColor, mDefaultColor) else mDefaultColor
        autoLoading = typedArray.getBoolean(R.styleable.UKLoadingLayout_defaultLoading, false)
        typedArray.recycle()

    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        buildLoadView(context)
        buildErrorView(context)
        //默认为inLoading状态
        if (autoLoading) {
            inLoadingNoAnim()
        }
    }

    fun createChildView(context: Context) {
        buildLoadView(context)
        buildErrorView(context)
    }

    fun setLoadPadding(padding: Int) {
        mLoadView.setPadding(0, 0, 0, padding)
        mErrorView.setPadding(0, 0, 0, padding)
    }

    private fun buildLoadView(context: Context) {
        mLoadView = LinearLayout(context)
        mLoadView.isClickable = true
        mLoadView.let {
            it.tag = LOADING_TAG
            it.orientation = LinearLayout.VERTICAL
            it.gravity = Gravity.CENTER
            val loadViewLayoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            loadViewLayoutParams.gravity = Gravity.CENTER
            it.layoutParams = loadViewLayoutParams
            it.setBackgroundColor(ContextCompat.getColor(context, R.color.gray_F5F6F7))
        }

        mLAVLoadImage = LottieAnimationView(context)

        val loadImLayoutParams = LayoutParams(context.dp2px(120f), context.dp2px(120f))
        mLAVLoadImage.layoutParams = loadImLayoutParams
        mLAVLoadImage.repeatCount = LottieDrawable.INFINITE
        mLAVLoadImage.setAnimation(R.raw.testloading)
        mLAVLoadImage.playAnimation()

        val loadTextLayoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
//        loadTextLayoutParams.topMargin = context.dp2px(12f)
        mTvHintContent = TextView(context)
        mTvHintContent.setTextColor(mDefaultColor)
        mTvHintContent.gravity = Gravity.CENTER
        mTvHintContent.text = context.getString(R.string.default_loading_text)
        mTvHintContent.layoutParams = loadTextLayoutParams
        mLoadView.addView(mLAVLoadImage)
        mLoadView.addView(mTvHintContent)
    }

    private fun buildErrorView(context: Context) {
        mErrorView = LinearLayout(context)
        mErrorView.isClickable = true
        mErrorView.tag = LOADING_TAG
        mErrorView.orientation = LinearLayout.VERTICAL
        mErrorView.gravity = Gravity.CENTER
        mErrorView.setBackgroundColor(ContextCompat.getColor(context, R.color.gray_F5F6F7))
        val loadViewLayoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        loadViewLayoutParams.gravity = Gravity.CENTER
        mErrorView.layoutParams = loadViewLayoutParams

        mErrorIm = ImageView(context)
        val loadImLayoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        mErrorIm.scaleType = ImageView.ScaleType.CENTER_INSIDE
        mErrorIm.setImageResource(R.drawable.icon_error)
        mErrorIm.layoutParams = loadImLayoutParams
        val loadTxtLayoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        loadTxtLayoutParams.topMargin = context.dp2px(12f)
        mTvErrorText = TextView(context)
        mTvErrorText.setTextColor(mDefaultColor)
        mTvErrorText.gravity = Gravity.CENTER
        mTvErrorText.text = context.getString(R.string.loading_failed_text)
        mTvErrorText.layoutParams = loadTxtLayoutParams
        val operationBtnLayoutParams = LayoutParams(context.dp2px(100f), context.dp2px(35f))
        operationBtnLayoutParams.topMargin = context.dp2px(12f)
        mBtnOperation = TextView(context)
        mBtnOperation.gravity = Gravity.CENTER
        mBtnOperation.text = context.getString(R.string.loading_retry)
        mBtnOperation.setTextColor(ContextCompat.getColor(context, R.color.white))
        mBtnOperation.setBackgroundColor(ContextCompat.getColor(context, R.color.blue_btn))
        mBtnOperation.layoutParams = operationBtnLayoutParams

        mErrorView.addView(mErrorIm)
        mErrorView.addView(mTvErrorText)
        mErrorView.addView(mBtnOperation)
        mErrorView.alpha = 0.0f
    }

    fun inLoading(hintMsg: String = context.getString(R.string.default_loading_text)) {
        currentState = INLOADING
        visibility = View.VISIBLE
        removeView(mErrorView)

        if (mLoadView.parent == null) {
            try {
                addView(mLoadView)
            } catch (e: Exception) {
                return
            }
        }
        applyAnimationAlphas(mLoadView, 1f, 1.0f, 300)
        mLAVLoadImage.resumeAnimation()

        applyAnimation(mLAVLoadImage, 0f, 1f, -160f, 0f)
        applyAnimation(mTvHintContent, 0f, 1f, 160f, 0f)
        mTvHintContent.text = if(hintMsg.isEmpty()) "不晃会被撞到地上" else hintMsg
    }

    fun inLoadingNoAnim(hintMsg: String = context.getString(R.string.default_loading_text)) {
        currentState = INLOADING
        visibility = View.VISIBLE
        removeView(mErrorView)

        if (mLoadView.parent == null) {
            try {
                addView(mLoadView)
            } catch (e: Exception) {
                return
            }
        }
//        mLoadView.alpha = 1F
//        mLAVLoadImage.alpha = 1F
//        mTvHintContent.alpha = 1F
        mLAVLoadImage.resumeAnimation()
        mTvHintContent.text = hintMsg
    }

    fun loadComplete() {
//        isAddLoadView = false
        if (currentState == COMPLETE) {
            return
        }
        currentState = COMPLETE
        removeView(mErrorView)
        removeView(mLoadView)
//        applyAnimation(mLAVLoadImage, 1f, 0f, 0f, -160f)
//        applyAnimation(mTvHintContent, 1f, 0f, 0f, 160f)
//        applyAnimationAlpha(mLoadView, 1.0f, 0.0f, 400)
        mLAVLoadImage.pauseAnimation()
    }

    fun loadFailed(errorMessage: String?,action:(() ->Unit)? = null) {
        if (currentState == ERROR) {
            mTvErrorText.text = errorMessage ?: context.getString(R.string.default_request_error_server)
            mBtnOperation.click {
                action?.invoke()
            }
            return
        }
        currentState = ERROR
        visibility = View.VISIBLE
        removeView(mLoadView)
        mBtnOperation.visibility = View.VISIBLE

        if (mErrorView.parent == null) {
            try {
                addView(mErrorView)
            } catch (e: Exception) {

            }
        }
        applyAnimationAlphas(mErrorView, 1f, 1.0f, 300)
        applyAnimation(mErrorIm, 0f, 1f, -160f, 0f)
        applyAnimation(mTvErrorText, 0f, 1f, 160f, 0f)
        applyAnimation(mBtnOperation, 0f, 1f, 200f, 0f)

        mBtnOperation.click {

            action?.invoke()
        }

        mErrorIm.setImageResource(R.drawable.img_server_error)
        //fixme 这里的提示语全部统一了
        mTvErrorText.text = errorMessage ?: context.getString(R.string.default_request_error_server)
        mLAVLoadImage.pauseAnimation()
    }

    fun loadEmpty(tipMessage: CharSequence? = null) {
        if (currentState == EMPTY) {
            return
        }
        currentState = EMPTY
        visibility = View.VISIBLE
        removeView(mLoadView)

        if ((mErrorView.parent == null)) {

            try {
                addView(mErrorView)
            } catch (e: Exception) {

            }
        }
        applyAnimationAlphas(mErrorView, 1f, 1.0f, 300)
        applyAnimation(mErrorIm, 0f, 1f, -160f, 0f)
        applyAnimation(mTvErrorText, 0f, 1f, 160f, 0f)
        mBtnOperation.visibility = View.GONE
        mErrorIm.setImageResource(R.drawable.img_data_empty)
        mTvErrorText.text = tipMessage ?: context.getString(R.string.no_data)
        mTvErrorText.setMovementMethod(LinkMovementMethod.getInstance())
        mLAVLoadImage.pauseAnimation()
    }

    private fun applyAnimationAlphas(view: LinearLayout, vaStart: Float, vaEnd: Float, dur: Int) {
        val vaAlpha = ObjectAnimator.ofFloat(vaStart, vaEnd)
        vaAlpha.duration = dur.toLong()
        vaAlpha.addUpdateListener { valueAnimator ->
            view.alpha = valueAnimator.animatedValue as Float
        }
        vaAlpha.start()
    }

    private fun applyAnimation(view: View, vaStart: Float, vaEnd: Float, startTranslationY: Float, endTranslationY: Float) {
        //值动画
        val vaAlphaAnim = ObjectAnimator.ofFloat(vaStart, vaEnd)
        vaAlphaAnim.duration = 450
        vaAlphaAnim.addUpdateListener { valueAnimator -> view.alpha = valueAnimator.animatedValue as Float }
        //spring弹簧动画
        val springAn = SpringAnimation(view, DynamicAnimation.TRANSLATION_Y, endTranslationY)
        springAn.setStartValue(startTranslationY)
        springAn.spring.stiffness = SpringForce.STIFFNESS_VERY_LOW
        springAn.spring.dampingRatio = SpringForce.DAMPING_RATIO_LOW_BOUNCY

        vaAlphaAnim.start()
        springAn.start()

    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mLAVLoadImage?.cancelAnimation()
    }




}