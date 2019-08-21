package com.uoko.baseframe.test

import com.blankj.utilcode.util.LogUtils
import com.uoko.frame.common.BaseUokoViewModel
import com.uoko.frame.common.InstallRepository
import com.uoko.frame.common.UKLiveData

@InstallRepository(modelRepository = TestRepository::class)
class TestVIewModel : BaseUokoViewModel<TestRepository>() {

    val liveData = UKLiveData<List<Any>>()

    fun testFun() {

        //直接获取数据改变
//        subcribe(liveData,getRepository().testReq())

        subcribe2(liveData,getRepository().testReq()){

            LogUtils.e(it.toString())

        }


    }

    fun requestdata() {

    }


    data class loginModel(val username:String,val password:String)

}