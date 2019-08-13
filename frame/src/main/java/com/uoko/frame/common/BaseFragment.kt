package com.uoko.frame.common

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.uoko.frame.dialog.LoadingDialog
import com.uoko.frame.dialog.UKLoadingLayout
import com.uoko.frame.repository.BaseRepository
import kotlin.reflect.KClass

abstract class BaseFragment<out V : BaseUokoViewModel<BaseRepository>> : Fragment() {

    protected var mViewInit: Boolean = false
    protected var mFirstViewInit: Boolean = true
    protected var mVisible: Boolean = false
    protected var mFirst: Boolean = true
    protected val viewModel: V by lazy(::viewModelfr)
    protected var contentView: View? = null

    private val dialog: LoadingDialog by lazy(::dialogInstance)

    private val hintDialogMap: MutableMap<Int, LoadingDialog?> by lazy {
        mutableMapOf<Int, LoadingDialog?>()
    }

    /**
     * 布局ID
     */
    abstract fun LayoutId(): Int

//
//
//    private fun getViewID(): Int {
//
//        val annotation = javaClass.getAnnotation(InstallViewLayout::class.java)
//
//        return annotation?.viewId ?: 0
//
//    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (contentView == null) {
            contentView = if (LayoutId() != 0) {
                inflater.inflate(LayoutId(), container, false)
            } else {
                super.onCreateView(inflater, container, savedInstanceState)
            }
        }
        return contentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (mFirstViewInit) {
            mFirstViewInit = false
            initView(view)
        }
        mViewInit = true
    }



    /**
     * 在viewPager中，fragment的view从界面中detach后再可见时setUserVisibleHint执行在前
     * ，可能会导致view未attach便执行lazyLoad了，所以要在这里
     */
    override fun onDestroyView() {
        super.onDestroyView()
        mViewInit = false
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        mVisible = isVisibleToUser
        checkLazyLoad()

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        mVisible = !hidden
        checkLazyLoad()
    }

    /**
     * 写死在布局中的情况，setUserVisibleHint和OnHiddenChanged不会执行
     */
    override fun onResume() {
        super.onResume()
        checkLazyLoad()
    }


    override fun onPause() {
        super.onPause()
        if (mViewInit) {
            onUnVisible()
        }


    }

    /**
     * 检查fragment是否为第一次可见
     */
    fun checkLazyLoad() {
        if (mViewInit) {
            if (mVisible) {
                onCanVisible()
            } else {
                onUnVisible()
            }
        }
        if (mVisible && mViewInit && mFirst) {
            mFirst = false
            lazyLoad()
        }
    }

    /**
     * 不可见
     */
    open fun onUnVisible() {
    }

    open fun onCanVisible() {
    }

    open fun installLoadingLayout(): UKLoadingLayout? {

        return null
    }

    /**
     * fragment第一次对用户可见
     */
    abstract fun lazyLoad()

    /**
     * 实例化view，kotlin基本可以不做了
     * 在viewpager中，onCreateView onViewCreated 可能会多次执行，inflate  View和findViewById只需要执行一次，
     * 这个需要自行在各子类中判断
     */
    abstract fun initView(view: View)

    /**
     * 数据初始化完成
     */
    abstract fun initListener()



    private fun dialogInstance(): LoadingDialog {
        return LoadingDialog.newInstance()

    }


    private fun showHintDialog(code: Int) {


        if (!hintDialogMap.containsKey(code)) {
            hintDialogMap[code] = dialogInstance()
        }
        activity?.supportFragmentManager?.let { hintDialogMap[code]?.show(it) }

    }


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



    protected fun viewModel(): V {
        return viewModel
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

    override fun onDestroy() {
        super.onDestroy()

    }




}