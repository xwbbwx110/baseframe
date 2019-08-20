package com.uoko.baseframe.test

import com.google.gson.JsonObject
import com.uoko.frame.common.UKCall
import com.uoko.frame.net.sendAnimation
import com.uoko.frame.net.sendDialog
import com.uoko.frame.repository.BaseRepository

class TestRepository : BaseRepository() {

    fun testReq() : UKCall<String> {

        /*
         *
         * 这里做一些其他事情
         */
        return  ApiInstance.mainAPi.company(TestVIewModel.loginModel("admin","smart0522")).sendDialog()
    }

}