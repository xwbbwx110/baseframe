package com.uoko.baseframe.test

import com.google.gson.JsonObject
import com.uoko.frame.common.UKCall
import retrofit2.http.*

/**
 * 作者: xwb
 * 描述:
 */
interface ApiService {

    /**
     * 查询公司
     */
    @POST("api/company/company_users/companies")
    fun company(@Body data:TestVIewModel.loginModel): UKCall<String>


}