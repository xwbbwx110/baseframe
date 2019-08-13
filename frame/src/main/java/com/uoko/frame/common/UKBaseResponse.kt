package com.uoko.frame.common


/**
 * Created by Administrator 拇指/可爱的路人 on 2018/11/29 0029.
 * Email:513421345@qq.com
 * TODO
 */
data class UKBaseResponse<T>(var code: String? = null,
                             var msg: String? = null,
                             var data: T? = null,
                             var total: Int = 0) {

    fun isSuccess() = "000" == code


}