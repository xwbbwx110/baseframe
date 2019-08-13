package com.uoko.frame.net

import com.uoko.frame.common.UKCall
import kotlinx.coroutines.*
import java.io.IOException
import java.net.ConnectException

/**
 * @author xwb
 * 扩展方法，方便发送请求,协程的会在viewmodel销毁也销毁
 */
fun <T> CoroutineScope.exeuctionRequest(call: UKCall<T>, delyTime:Long = 0L, va: RequestCallback<T>.() ->Unit) {
    val back = RequestCallback<T>()
    back.va()
    back.api = call.call
    this.launch(Dispatchers.Main) {
        val work = async(Dispatchers.IO) {
            delay(delyTime)
            try {
                back.api?.execute()
            } catch (e: ConnectException) {
                null
            } catch (e: IOException) {
                null
            }
        }
        work.invokeOnCompletion {
            if (work.isCancelled) {
                back.api?.cancel()
            }
        }
        val response = work.await()

        response?.let {

            if (response.isSuccessful) {
                back.onSuccess?.invoke(response.body())
            } else {
                when (response.code()) {
                    401 -> {//验证问题


                    }
                    500 -> {
                        println("内部服务器错误")
                    }
                }
                println(response.errorBody())
            }

        }
    }
}
