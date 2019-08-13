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
     * 房源图片
     * /musicRankings
     */
    @GET("musicRankings")
    fun getHouseImages(): UKCall<JsonObject>



}