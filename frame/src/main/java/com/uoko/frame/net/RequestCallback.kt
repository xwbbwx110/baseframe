package com.uoko.frame.net

import retrofit2.Call

/**
 * @author xwb
 * 回调定义
 */
class RequestCallback<T> {
    var api: (Call<T>)? = null

     var onSuccess: ((T?) -> Unit)? = null
        public set
     var onComplete: (() -> Unit)? = null
         public set
     var onFailed: ((error: String?, code:Int) -> Unit)? = null
         public set

    var showFailedMsg = false

    internal fun clean() {
        onSuccess = null
        onComplete = null
        onFailed = null
    }

    fun onSuccess(block: (T?) -> Unit) {
        this.onSuccess = block
    }

    fun onComplete(block: () -> Unit) {
        this.onComplete = block
    }

    fun onFailed(block: (error: String?, code:Int) -> Unit) {
        this.onFailed = block
    }


}
