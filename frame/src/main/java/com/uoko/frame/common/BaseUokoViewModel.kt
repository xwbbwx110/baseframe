package com.uoko.frame.common

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uoko.frame.net.exeuctionRequest
import com.uoko.frame.repository.BaseRepository

/**
 * 作者: xwb on 2018/7/25
 * 描述: 基类的viewmodel，activity或fragment观察viewModel数据，如果数据变化需要通知数据改变
 * @see notifyDataChange
 *
 * ps:ViewModel 不应该持有view的引用，只通过观察和订阅的模式交互数据
 *
 *
 * viewModelScope发起一个协程，会自动在viewmodel销毁的时候取消携程，如果想要销毁不取消 则使用GlobalScope发起
 */
open class BaseUokoViewModel<out D : BaseRepository> : ViewModel() {
    /**
     * 绑定监听
     */
    val mListener: MutableLiveData<String> = MutableLiveData()


    /**
     * 响应加载类型
     */
    val mLoadType: MutableLiveData<UKCall.StatusInfo> = MutableLiveData()


    fun registerListener() {
        mListener.value = "addListener"
    }


    /**
     * 通知数据改变
     */
    protected fun <T : MutableLiveData<*>> notifyDataChange(t: T?, data: Any) {
        t!!.value = data
    }


    private val res1: D by lazy(::initRepository)

    fun getRepository(): D {
        return res1
    }

    protected open fun initRepository(): D {
        val annotation = javaClass.getAnnotation(InstallRepository::class.java)

        return annotation?.modelRepository?.java?.newInstance() as D
    }


    /**
     * model:监听数据变换到UI
     * call:当前请求
     * action:数据变化前的额外操作
     */
    fun <T> subcribe(model: UKLiveData<T>?, call: UKCall<T>, action: ((T?) -> Unit)? = null) {


        mLoadType.value = call.loadType


        viewModelScope.exeuctionRequest(call) {
            onSuccess {
                call.loadType.type = UKCall.DISMISS
                mLoadType.value = call.loadType
                action?.invoke(it)
                model?.notifyDataChange(it)
            }


            onFailed { error, code ->
                call.loadType.type = UKCall.DISMISS
                mLoadType.value = call.loadType
                model?.notifyDataChangeError(errorCode = code, errorMsg = error)
            }

            onComplete {
                call.loadType.type = UKCall.DISMISS
                mLoadType.value = call.loadType
            }
        }


    }

    /**
     * model:监听数据变换到UI
     * call:当前请求
     * action:数据变化前的额外操作
     * 这个无法拿到请求的code,需要用到code的使用
     * @see subcribe
     */
    fun <T> subcribe2(
        model: UKLiveData<T>?,
        call: UKCall<UKBaseResponse<T>>,
        action: ((T?) -> Unit)? = null
    ) {


        mLoadType.value = call.loadType


        //viewModelScope发起一个协程
        viewModelScope.exeuctionRequest(call) {
            onSuccess {
                call.loadType.type = UKCall.DISMISS
                mLoadType.value = call.loadType
                if (it?.status == 200) {
                    action?.invoke(it.data)
                    model?.notifyDataChange(it.data)
                } else {
                    model?.notifyDataChangeError(
                        errorCode = it?.status ?: 0,
                        errorMsg = it?.message
                    )
                }
            }


            onFailed { error, code ->
                call.loadType.type = UKCall.DISMISS
                mLoadType.value = call.loadType
                model?.notifyDataChangeError(errorCode = code, errorMsg = error)
            }

            onComplete {
                call.loadType.type = UKCall.DISMISS
                mLoadType.value = call.loadType
            }
        }


    }

}
