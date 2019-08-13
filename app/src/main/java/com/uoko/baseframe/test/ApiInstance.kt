package com.uoko.baseframe.test

import com.uoko.baseframe.BuildConfig
import com.uoko.frame.net.RetrofitNet

/**
 * Created by Administrator 可爱的路人 on 2018/8/14 0014.
 * Email:513421345@qq.com
 * TODO
 */
object ApiInstance {
    init {
        RetrofitNet.isDebug = BuildConfig.DEBUG || Urls.DEBUG
        RetrofitNet.hostName = Urls.CRM_HOST
        RetrofitNet.imageHostName = "hh.uoko.com"
    }

    val mainAPi: ApiService by lazy {
        RetrofitNet.createApi(Urls.CRM_URL, ApiService::class.java)
    }
}
