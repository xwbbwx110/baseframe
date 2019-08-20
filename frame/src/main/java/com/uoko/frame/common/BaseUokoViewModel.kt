package com.uoko.frame.common

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.uoko.frame.net.exeuctionRequest
import com.uoko.frame.repository.BaseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

/**
 * 作者: xwb on 2018/7/25
 * 描述: 基类的viewmodel，activity或fragment观察viewModel数据，如果数据变化需要通知数据改变
 * @see notifyDataChange
 *
 * ps:ViewModel 不应该持有view的引用，只通过观察和订阅的模式交互数据
 *
 */
open class BaseUokoViewModel<out D : BaseRepository> : ViewModel() , CoroutineScope{


    private  var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job


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
    fun <T> subcribe(model: UKLiveData<T>?, call: UKCall<T>, action:((T?) ->Unit)?=null){


        mLoadType.value = call.loadType


        exeuctionRequest(call){
            onSuccess {
                call.loadType.type = UKCall.DISMISS
                mLoadType.value = call.loadType
                action?.invoke(it)
                model?.notifyDataChange(it)
            }


            onFailed { error, code ->
                call.loadType.type = UKCall.DISMISS
                mLoadType.value = call.loadType
                model?.notifyDataChangeError(errorCode = code,errorMsg = error)
            }

            onComplete {
                call.loadType.type = UKCall.DISMISS
                mLoadType.value = call.loadType
            }
        }


    }

    override fun onCleared() {
        super.onCleared()

        job.cancel()

    }
}
