package com.uoko.frame.common

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.uoko.frame.R
import com.uoko.frame.dialog.LoadingDialog
import com.uoko.frame.dialog.UKLoadingLayout
import com.uoko.frame.repository.BaseRepository
import kotlinx.android.synthetic.main.layout_base_activity.*
import kotlin.reflect.KClass

abstract class BaseActivity<out V : BaseUokoViewModel<BaseRepository>> : AppCompatActivity() {
    /**
     * 布局ID
     */
    abstract fun LayoutId(): Int


    protected val viewModel: V by lazy(::viewModelfr)

    private val dialog: LoadingDialog by lazy(::dialogInstance)

    private val hintDialogMap: MutableMap<Int, LoadingDialog?> by lazy {
        mutableMapOf<Int, LoadingDialog?>()
    }


    abstract fun initData(savedInstanceState: Bundle?)


    /**
     * 只放一些需要在初始化之后才实现的事件监听
     */
    abstract fun initListener()

    protected var mToolbar: UokoToolbar? = null

    protected open fun showBackIcon(): Boolean {
        return true
    }

    protected open fun showToolbar(): Boolean {
        return true
    }


    @SuppressLint("ResourceType")
    override fun setContentView(layoutResID: Int) {
        if (showToolbar()) {
            super.setContentView(R.layout.layout_base_activity)
            mToolbar = toolbar_base
            setSupportActionBar(mToolbar)
            mToolbar!!.popupTheme = R.style.uk_toobar_style
            mToolbar!!.setTitleGravity(0)
            if (showBackIcon()) {
                mToolbar!!.setNavigationIcon(R.drawable.icon_back)
            } else {
                mToolbar?.showBackIcon(showBackIcon())
            }
            mToolbar!!.setNavigationOnClickListener {
                if (showBackIcon()) {
                    finish()
                }
            }
            vs_base.layoutResource = layoutResID
            vs_base.inflate()
        } else {
            super.setContentView(layoutResID)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            MIUISetStatusBarLightMode(true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }


        setContentView(LayoutId())
        initData(savedInstanceState)

    }


//    private fun getViewID(): Int {
//
//        val annotation = javaClass.getAnnotation(InstallViewLayout::class.java)
//
//        return annotation?.viewId ?: 0
//
//    }


    /**
     * 实例化当前view的 ViewModel
     *  使用lazy
     */
    private fun viewModelfr(): V {
        val annotation = javaClass.getAnnotation(InstallViewModel::class.java)

        val kClass: KClass<V>

        if (annotation != null) {
            kClass = annotation.viewModelclass as KClass<V>
        } else {
            throw  RuntimeException("@InstallViewModel(ViewModelObject =  xx.class)注解,没有声明")
        }



        return stepViewModel(kClass)
    }


    private fun dialogInstance(): LoadingDialog {
        return LoadingDialog.newInstance()

    }


    private fun showHintDialog(code: Int) {


        if (!hintDialogMap.containsKey(code)) {
            hintDialogMap[code] = dialogInstance()
        }
        hintDialogMap[code]?.show(supportFragmentManager)

    }


    open fun installLoadingLayout(): UKLoadingLayout? {

        return null
    }


    private fun stepViewModel(kclass: KClass<V>): V {

        val viewModel = ViewModelProviders.of(this).get(kclass.java)
        viewModel.mListener.observe(this, Observer {
            initListener()
        })




        viewModel.mLoadType.observe(this, Observer {

            when (it.type) {

                UKCall.SHOW_LOADING_TO_background -> {
                    //什么都做
                }


                UKCall.SHOW_LOADING_TO_DIALOG -> {

                    showHintDialog(it.code)

                }


                UKCall.SHOW_LOADING_TO_ANIMATION -> {
                    installLoadingLayout()?.inLoading(it.msg)
                }

                UKCall.DISMISS -> {
                    hintDialogMap[it.code]?.dismiss()
                    if (hintDialogMap.containsKey(it.code))
                        hintDialogMap.remove(it.code)
                    installLoadingLayout()?.loadComplete()
                }
            }

        })


        return viewModel
    }



    private fun MIUISetStatusBarLightMode(dark: Boolean): Boolean {
        var result = false
        if (window != null) {
            val clazz = window.javaClass
            try {
                var darkModeFlag = 0
                val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
                if (layoutParams != null) {
                    val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
                    darkModeFlag = field.getInt(layoutParams)
                    val extraFlagField = clazz.getMethod("setExtraFlags", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
                    if (dark) {
                        extraFlagField.invoke(window, darkModeFlag, darkModeFlag) //状态栏透明且黑色字体
                    }
                }
                result = true
            } catch (e: Exception) {

            }

        }
        return result
    }

}