package com.uoko.baseframe.test

import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.LogUtils
import com.uoko.frame.common.BaseUokoViewModel
import com.uoko.frame.common.InstallRepository
import com.uoko.frame.common.UKLiveData
import com.uoko.frame.net.exeuctionRequest
import kotlinx.coroutines.launch

@InstallRepository(modelRepository = TestRepository::class)
class TestVIewModel : BaseUokoViewModel<TestRepository>() {

    val liveData = UKLiveData<List<Any>>()

    fun testFun() {

        //直接获取数据改变
//        subcribe(liveData,getRepository().testReq())

        subcribe2(liveData,getRepository().testReq()){

            subcribe2(liveData,getRepository().testReq())
        }


    }

    fun requestdata() {

    }


    data class loginModel(val username:String,val password:String)

}