package com.uoko.baseframe.test

/**
 * Created by Administrator 可爱的路人 on 2018/8/31 0031.
 * Email:513421345@qq.com
 * TODO
 */
interface Urls {
    companion object {
        val DEBUG = false
        //七牛文件夹
        val ENVIRONMENT = "AssetManager"
        val CRM_URL = "https://api.apiopen.top/"
        val CRM_HOST = "api-gateway.uoko.com"
        //七牛图片地址 val domain = "https://image.uoko.com/"
        val QINIU_DOMAIN = "https://image.uoko.com/"
        val QINIU_TOKEN_URL = "https://hh.uoko.com/api/app/house/qiniu/getconfig"
    }
}