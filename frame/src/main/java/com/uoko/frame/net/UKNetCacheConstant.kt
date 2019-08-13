package com.uoko.frame.net

/**
 * Created by Administrator 拇指/可爱的路人 on 2018/11/20 0020.
 * Email:513421345@qq.com
 * TODO
 */
class UKNetCacheConstant {
    companion object {
        /**
         * 仅使用缓存
         * 缓存不存在时返回 code -1  message:缓存不存在
         */
        const val CACHE_FIRST = "cache_first"
        /**
         * 缓存优先
         * 网络良好，先检查缓存，再从服务端获取
         */
        const val CACHE_ONLY="cache_only"
        /**
         * 仅使用网络，默认模式
         * 不论怎么都发起网络请求
         */
        const val NET_WORK_ONLY="net_work_only"
        /**
         * 当网络未连接时使用缓存
         * 没网的时候使用缓存，此时缓存不存在的话再使用网络
         */
        const val CACHE_IF_NET_FAIL="cache_if_net_fail"
        
        const val SEC = 1
        const val MIN = 60
        const val HOUR = 3600
        const val DAY = 86400
        const val MONTH = 30 * DAY
        const val YEAR = 12 * MONTH
        const val HTTP_CACHE_DIR = "api"
        const val CACHE_TYPE_KEY = "cache_type"
        //缓存有效时间
        const val CACHE_TIME_KEY = "cache_time"
    }
}