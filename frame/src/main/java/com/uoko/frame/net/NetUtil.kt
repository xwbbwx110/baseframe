package com.uoko.frame.net

import com.blankj.utilcode.util.LogUtils
import com.google.gson.Gson
import com.uoko.frame.common.UKBaseResponse
import com.uoko.frame.common.UKCall
import kotlinx.coroutines.*
import retrofit2.Callback
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException

/**
 * @author xwb
 * 扩展方法，方便发送请求,协程的会在viewmodel销毁也销毁
 */
fun <T> CoroutineScope.exeuctionRequest(call: UKCall<T>, delyTime:Long = 0L, va: RequestCallback<T>.() ->Unit) {
    val back = RequestCallback<T>()
    back.va()
    back.api = call.call
    this.launch(Dispatchers.Main) {

        var exception:java.lang.Exception? = null

        val work = async(Dispatchers.IO) {
            delay(delyTime)
            try {
                back.api?.execute()
            } catch (e: ConnectException) {
                exception = e
                null
            } catch (e:SocketTimeoutException ) {
                exception = e
                null
            }catch (e: Exception) {
                exception = e
                null
            }
        }
        work.invokeOnCompletion {
            if (work.isCancelled) {
                back.api?.cancel()
            }
        }

        val response = work.await()


        if(response !=null){
            if (response.isSuccessful) {
                back.onSuccess?.invoke(response.body())

            } else {

                back.onFailed?.invoke(response.errorBody()?.string()?:"",response.code())
            }
        }else{
            LogUtils.e(exception.toString())

            when (exception) {
                is ConnectException -> back.onFailed?.invoke("连接错误！",1000)
                is SocketTimeoutException -> back.onFailed?.invoke("连接超时！", 1000)
                else -> back.onFailed?.invoke("连接错误！",1000)
            }

        }



    }
}
