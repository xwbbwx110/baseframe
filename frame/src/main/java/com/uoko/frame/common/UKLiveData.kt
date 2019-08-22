package com.uoko.frame.common

import androidx.annotation.NonNull
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import com.google.gson.Gson


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
        if(errorMsg == null || errorMsg.isEmpty()){
            this.errorCode = 0
            this.errorMsg = ""
        }else{
            val error =  Gson().fromJson(errorMsg,UKBaseResponse::class.java)
            this.errorCode = errorCode
            this.errorMsg = error.message
        }
        value = null

    }


}