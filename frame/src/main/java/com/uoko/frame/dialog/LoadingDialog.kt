package com.uoko.frame.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.uoko.frame.R
import com.uoko.baseframe.common.BaseUKDialog


/**
 * 作者: xwb on 2018/1/3
 * 描述:
 */

class LoadingDialog : BaseUKDialog() {
    private var tipStrs: Array<out String>? = null
    internal var dialog_tip_txt: TextView? = null


    override fun getLayoutId(): Int {
        return    R.layout.dl_loading_layout
    }

    override fun setShowMsg(vararg strs: String): BaseUKDialog {
        if (dialog_tip_txt != null) {
            dialog_tip_txt?.text = strs[0]
        }
        tipStrs = strs
        return this
    }

    override fun initView(view: View, curContext: Context) {
        dialog_tip_txt = view.findViewById(R.id.dialog_tip_txt)
        if (tipStrs?.isNotEmpty() == true && tipStrs!![0].isNotBlank()) {
            dialog_tip_txt?.text = tipStrs?.get(0)
        }
    }

    override fun initData(curContext: Context, savedInstanceState: Bundle?) {
        setCanceledOnTouchOutside(false)
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    }

    companion object {
        fun newInstance(): LoadingDialog {
            val args = Bundle()

            val fragment = LoadingDialog()
            fragment.arguments = args
            return fragment
        }
    }
}
