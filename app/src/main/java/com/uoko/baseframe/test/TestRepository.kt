package com.uoko.baseframe.test

import com.uoko.frame.common.UKBaseResponse
import com.uoko.frame.common.UKCall
import com.uoko.frame.net.sendDialog
import com.uoko.frame.repository.BaseRepository

class TestRepository : BaseRepository() {

    fun testReq() : UKCall<UKBaseResponse<List<Any>>> {

        /*
         *
         * 这里做一些其他事情
         */
        return  ApiInstance.mainAPi.company(TestVIewModel.loginModel("adsmin","smart0522")).sendDialog()
    }

}