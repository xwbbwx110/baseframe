package com.uoko.baseframe.test

import com.uoko.frame.common.UKBaseResponse
import com.uoko.frame.common.UKCall
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * 作者: xwb
 * 描述:
 */
interface ApiService {

    /**
     * 查询公司
     */
    @POST("api/company/company_users/companies")
    fun company(@Body data:TestVIewModel.loginModel): UKCall<UKBaseResponse<List<Any>>>


}