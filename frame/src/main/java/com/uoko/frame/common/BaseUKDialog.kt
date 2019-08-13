package com.uoko.baseframe.common

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import kotlin.random.Random

/**
 * 作者: xwb on 2017/12/14
 * 描述:
 */
abstract class BaseUKDialog : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(getLayoutId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view, context!!)
        initData(context!!, savedInstanceState)
    }

    /**
     * 设置Dialog点击外部区域是否隐藏
     * @param cancel
     */
    fun setCanceledOnTouchOutside(cancel: Boolean) {
        if (dialog != null) {
            dialog?.setCanceledOnTouchOutside(cancel)
        }

    }

    fun setDialogHeight(hei: Int) {
        setDialogWidthAndHeight(ViewGroup.LayoutParams.WRAP_CONTENT, hei)
    }

    fun setDialogWidth(wid: Int) {
        setDialogWidthAndHeight(wid, ViewGroup.LayoutParams.WRAP_CONTENT)

    }

    fun setDialogWidthAndHeight(wid: Int, hei: Int) {
        dialog?.window!!.setLayout(wid, hei)
    }

    fun show(fragmentManager: FragmentManager) {
        try {
            show(fragmentManager, "uk"+Random(1000).nextInt())
        } catch (e: IllegalStateException) {
            //Can not perform this action after onSaveInstanceState
            e.printStackTrace()
        }
    }

    protected abstract fun getLayoutId(): Int
    protected abstract fun initView(view: View, curContext: Context)
    protected abstract fun initData(curContext: Context, savedInstanceState: Bundle?)

    /**
     * 设置显示信息
     * @param message
     */
    abstract fun setShowMsg(vararg message: String): BaseUKDialog
}
