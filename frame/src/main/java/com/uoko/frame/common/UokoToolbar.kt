package com.uoko.frame.common

import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.SizeUtils
import java.lang.reflect.Field
import java.util.concurrent.TimeUnit
import com.uoko.frame.R
import com.uoko.frame.expansion.click
import kotlinx.android.synthetic.main.partner_layout_search_title.view.*

/**
 * Created by Administrator 可爱的路人 on 2018/8/15 0015.
 * Email:513421345@qq.com
 * TODO
 */
open class UokoToolbar : Toolbar {
    //0居左  1居中
    private var titleGravity: Int = 1
    private var titleColor: Int = 0
    private var titleSize: Int = 0
    private var showHome: Boolean = true
    private var searchLayout: View? = null
    val autoAdapter = ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, ArrayList<String>())

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initBack(context)
        searchLayout?.clearFocus()
        val a = context.obtainStyledAttributes(attrs, R.styleable.UokoToolbar)
        a?.apply {
            //0居左   1居中  2剩余位置居中
            titleGravity = getInt(R.styleable.UokoToolbar_titleGravityToolBar, 0)
            titleColor = getColor(R.styleable.UokoToolbar_titleColor, ContextCompat.getColor(context, R.color.gray_FDFDFD))
            titleSize = getDimensionPixelSize(R.styleable.UokoToolbar_titleSize, context.resources.getDimensionPixelSize(R.dimen.font_title))
            recycle()
        }

        tvTitle = TextView(context)
        val lpTitle = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        tvTitle?.ellipsize = TextUtils.TruncateAt.MARQUEE
        tvTitle?.gravity = when (titleGravity) {
            0 -> Gravity.START or Gravity.CENTER_VERTICAL
            else -> Gravity.CENTER
        }
        tvTitle?.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize.toFloat())
        tvTitle?.setTextColor(titleColor)
        tvTitle?.setLines(1)
        tvTitle?.setSingleLine(true)
        tvTitle?.marqueeRepeatLimit = 1
        tvTitle?.layoutParams = lpTitle
        addView(tvTitle)
        tvTitle?.let {
            it.click {   val methodStartMarquee = TextView::class.java.getDeclaredMethod("startMarquee")
                methodStartMarquee.isAccessible = true
                methodStartMarquee.invoke(tvTitle)
                methodStartMarquee.isAccessible = false }
        }
    }

    /**
     * 0标题
     * 1搜索框
     * 设置标题栏为搜索框
     * @param hint 搜索框提示文字
     */
    fun setSearchType(hint: String?, actionListener: TextView.OnEditorActionListener?,
                      saveListener: ((searchKey: String) -> Unit?)? = null,
                      getSearchKeyListener: ((consumer: Consumer<List<String>?>?) -> Unit)? = null) {
        removeView(tvTitle)
        tvTitle = null
        searchLayout = LayoutInflater.from(context).inflate(R.layout.partner_layout_search_title, this, false)
        val etInput: AutoCompleteTextView = searchLayout!!.findViewById(R.id.et_search_title_input)
        val btnClear: ImageView = searchLayout!!.findViewById(R.id.btn_search_title_clear)
        etInput.hint = hint
        etInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s != null && s.isNotEmpty()) {
                    btnClear.visibility = View.VISIBLE
                } else {
                    btnClear.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        actionListener?.let {
            val acl = it
            etInput.setOnEditorActionListener { v, actionId, event ->
                if (etInput.text.toString().isNotBlank()) {
                    saveListener?.invoke(etInput.text.toString())
                }
                acl.onEditorAction(v, actionId, event)
            }
        }

        etInput.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                KeyboardUtils.hideSoftInput(v)
            }
        }
        btnClear.setOnClickListener {
            etInput.setText("")
            actionListener?.onEditorAction(etInput, EditorInfo.IME_ACTION_SEARCH, null)
        }
        addView(searchLayout)
        etInput.setAdapter<ArrayAdapter<String>?>(autoAdapter)
        etInput.threshold = 0
        getSearchKeyListener?.invoke(Consumer {
            autoAdapter.clear()
            autoAdapter.addAll(it)
        })
    }

    /**
     * 标题，没有调用{@link #setSearchType}的情况下设置,否则是没用的
     *
     */
    override fun setTitle(resId: Int) {
        tvTitle?.text = title
        tvTitle?.isSelected = true

    }

    /**
     * 标题，没有调用{@link #setSearchType}的情况下设置,否则是没用的
     *
     */
    override fun setTitle(title: CharSequence?) {
        tvTitle?.text = title
        tvTitle?.isSelected = true

    }

    private var tvTitle: TextView? = TextView(context)

    fun initBack(context: Context?) {
        if (context is BaseActivity<*>) {
            context.setSupportActionBar(this)
            if (showHome) {
                context.supportActionBar?.setDisplayHomeAsUpEnabled(true)
                super.setNavigationOnClickListener { context.finish() }
            } else {
                context.supportActionBar?.setDisplayHomeAsUpEnabled(false)
                context.supportActionBar?.setDisplayShowHomeEnabled(false)
                tvTitle?.setPadding(SizeUtils.dp2px(15f), 0, 0, 0)
            }
        } else if (context is FragmentActivity) {
            super.setNavigationOnClickListener {
                context.finish()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, measuredHeight + paddingTop + paddingBottom)
    }

    /**
     * 搜索框的大小位置
     *
     */
    private fun resizeSearchSize() {
        val mMenuViewField: Field = Toolbar::class.java.getDeclaredField("mMenuView")
        val mNavButtonViewField: Field = Toolbar::class.java.getDeclaredField("mNavButtonView")

        mMenuViewField.isAccessible = true
        mNavButtonViewField.isAccessible = true

        val mMenuView = mMenuViewField.get(this)
        val mNavButtonView = mNavButtonViewField.get(this)
        val menuLeft = mMenuView?.let {
            val itLeft = (it as View).left
            val layTop = (measuredHeight - it.measuredHeight) / 2 + paddingTop / 2
            it.layout(itLeft, layTop, it.right, layTop + it.measuredHeight)
            if (itLeft == 0) {
                measuredWidth
            } else {
                itLeft
            }
        } ?: measuredWidth
        val mNavButtonRight = mNavButtonView?.let {
            val itRight = (it as View).right
            val layTop = (measuredHeight - it.measuredHeight) / 2 + paddingTop / 2
            it.layout(it.left, layTop, it.right, layTop + it.measuredHeight)
            itRight
        } ?: 0
        searchLayout?.layoutParams?.width = menuLeft - mNavButtonRight - SizeUtils.dp2px(15F)
        searchLayout?.layoutParams?.height = measuredHeight - paddingTop - paddingBottom - SizeUtils.dp2px(10F)
        searchLayout?.layout(mNavButtonRight
                , paddingTop + SizeUtils.dp2px(5F)
                , menuLeft - SizeUtils.dp2px(15F)
                , measuredHeight - paddingBottom - SizeUtils.dp2px(5F)
        )
    }

    /**
     * 标题的大小位置
     *
     */
    private fun resizeTitleTextSize() {
        val mMenuViewField: Field = Toolbar::class.java.getDeclaredField("mMenuView")
        val mNavButtonViewField: Field = Toolbar::class.java.getDeclaredField("mNavButtonView")

        mMenuViewField.isAccessible = true
        mNavButtonViewField.isAccessible = true

        val mMenuView = mMenuViewField.get(this)
        val mNavButtonView = mNavButtonViewField.get(this)
        val menuLeft = mMenuView?.let {
            val itLeft = (it as View).left
            val layTop = (measuredHeight - it.measuredHeight) / 2 + paddingTop / 2
            it.layout(itLeft, layTop, it.right, layTop + it.measuredHeight)
            itLeft
        } ?: 0
        val mNavButtonRight = mNavButtonView?.let {
            val itRight = (it as View).right
            val layTop = (measuredHeight - it.measuredHeight) / 2 + paddingTop / 2
            it.layout(it.left, layTop, it.right, layTop + it.measuredHeight)
            itRight
        } ?: 0

        val maxMargin = Math.max(measuredWidth - menuLeft, mNavButtonRight)
        val titleLeft = when (titleGravity) {
            1 -> maxMargin
            else -> mNavButtonRight
        }
        val titleRight = if (titleGravity == 2) menuLeft else measuredWidth - maxMargin
        tvTitle?.layoutParams?.width = titleRight - titleLeft
        tvTitle?.layoutParams?.height = measuredHeight - paddingTop - paddingBottom
        tvTitle?.layout(titleLeft, paddingTop, titleRight, paddingTop + (tvTitle?.measuredHeight
                ?: 0))
    }

    /**
     * @param gravity 0左  1整体居中  2剩下的位置居中
     */
    fun setTitleGravity(gravity: Int) {
        titleGravity = gravity
        tvTitle?.gravity = when (titleGravity) {
            0 -> Gravity.START or Gravity.CENTER_VERTICAL
            else -> Gravity.CENTER
        }
        requestLayout()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        searchLayout?.let {
            resizeSearchSize()
        }
        tvTitle?.let {
            resizeTitleTextSize()
        }
    }

    private var listener: View.OnClickListener? = null

    override fun setNavigationOnClickListener(listener: OnClickListener?) {
        super.setNavigationOnClickListener(listener)
        this.listener = listener
    }

    /**
     * 返回键隐藏，
     */
    fun showBackIcon(show: Boolean = true) {
        showHome = show
        if (!showHome) {
            if (context is BaseActivity<*>) {
                (context as BaseActivity<*>).supportActionBar?.setDisplayHomeAsUpEnabled(false)
                (context as BaseActivity<*>).supportActionBar?.setDisplayShowHomeEnabled(false)
                tvTitle?.setPadding(SizeUtils.dp2px(15f), 0, 0, 0)
            }
        }
    }

    fun setTitleColor(color: Int) {
        titleColor = color
        tvTitle?.setTextColor(titleColor)
    }

    fun setTitleSize(size: Int) {
        titleSize = size
        tvTitle?.setTextSize(TypedValue.COMPLEX_UNIT_PX, size.toFloat())
    }

    fun setSearchKey(it: String) {
        et_search_title_input.setText(it)
    }

}