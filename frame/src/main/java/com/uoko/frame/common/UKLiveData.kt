package com.uoko.frame.common

import androidx.annotation.NonNull
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer


/**
 * 作者: xwb on 2018/8/3
 * 描述:携带了异常状态的livedata
 */
class UKLiveData<T>(var errorCode: Int = 200, var errorMsg: String? = "") : MediatorLiveData<T>(), Observer<T> {


//    private var mListener: ((T?, Boolean, String?) -> Unit)? = null


    /**
     * 200 且无异常 ，数据不为空时更新UI
     */
    private var updateUI:((data:T?)->Unit)? = null

    /**
     * 非200之外的错误
     */
    private var handleErrorInfo:((errorCode:Int,errorMsg:String?) ->Unit)? = null






    override fun onChanged(t: T?) {
        if(errorCode == 200){
            updateUI?.invoke(t)
        }else{
            handleErrorInfo?.invoke(errorCode,errorMsg)
        }

    }

    fun onObserver(@NonNull owner: LifecycleOwner,
                   updateUI: ((data:T?)->Unit),
                   handleErrorInfo:(errorCode:Int,errorMsg:String?) ->Unit) {
        this.updateUI = updateUI
        this.handleErrorInfo = handleErrorInfo
        observe(owner, this)
    }




    fun notifyDataChange(t: T?) {
        this.errorCode = 200
        this.errorMsg = ""
        value = t
    }

    fun notifyDataChangeError(errorCode: Int, errorMsg: String?) {
        this.errorCode = errorCode
        this.errorMsg = errorMsg
        value = null
    }


}