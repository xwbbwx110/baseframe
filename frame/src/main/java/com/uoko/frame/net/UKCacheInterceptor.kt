//package com.uoko.ukappframe.net
//
//import android.text.TextUtils
//import com.blankj.utilcode.util.CacheDiskUtils
//import com.blankj.utilcode.util.EncryptUtils
//import com.blankj.utilcode.util.NetworkUtils
//import okhttp3.*
//import okio.Buffer
//import org.json.JSONObject
//import java.io.IOException
//
///**
// * Created by Administrator 拇指/可爱的路人 on 2018/11/20 0020.
// * Email:513421345@qq.com
// * 接口缓存，post有效，使用header来控制
// */
//class UKCacheInterceptor : Interceptor {
//    @Throws(IOException::class)
//    override fun intercept(chain: Interceptor.Chain): Response {
//        var request = chain.request()
//        //请求地址
//        val url = request.url
//        //请求方式
//        val method = request.method
//        if (method != "POST" && method != "GET") {
//            return responseByDefault(chain, request)
//        }
//        //请求体
//        val body = request.body
//
//        val typeHeader = request.header(UKNetCacheConstant.CACHE_TYPE_KEY)
//        val timeHeader = request.header(UKNetCacheConstant.CACHE_TIME_KEY)
//        var cacheTime = 0
//        try {
//            cacheTime = Integer.parseInt(timeHeader)
//        } catch (e: Exception) {
//        }
//
//        if (TextUtils.isEmpty(typeHeader) || cacheTime <= 0) {
//            //默认为只使用网络
//            return responseByDefault(chain, request)
//        }
//        request = request.newBuilder().removeHeader(UKNetCacheConstant.CACHE_TYPE_KEY)
//                .removeHeader(UKNetCacheConstant.CACHE_TIME_KEY)
//                .build()
//        val cacheKey = getCacheKey(getRequestContent(method, body), url.toString())
//        when (typeHeader) {
//            UKNetCacheConstant.CACHE_FIRST -> {
//                //先从缓存中查找，缓存过期或没有再网络
//                val response = responseByCache(cacheKey, request)
//                return response ?: responseByDefaultSaveCache(cacheKey, chain, request, cacheTime)
//            }
//            UKNetCacheConstant.CACHE_IF_NET_FAIL -> {
//                //有网时从网络获取，无网时先读缓存，缓存不存在时从网络获取
//                return if (NetworkUtils.isConnected()) {
//                    //网络请求，并保存缓存
//                    responseByDefaultSaveCache(cacheKey, chain, request, cacheTime)
//                } else {
//                    val response1 = responseByCache(cacheKey, request)
//                    response1 ?: responseByDefault(chain, request)
//                }
//            }
//            UKNetCacheConstant.NET_WORK_ONLY -> {
//                //这是默认的,(2019-3-22 by 拇指：不要将内容存下来，浪费空间)
////                return responseByDefaultSaveCache(cacheKey, chain, request, cacheTime)
//                return responseByDefault(chain, request)
//            }
//            UKNetCacheConstant.CACHE_ONLY -> {
//                //强行从缓存读
////                return responseByCacheForce(cacheKey, request)
//            }
//            else -> {
//                //不在规定中的
//                return responseByDefault(chain, request)
//            }
//        }
//    }
//
//    /**
//     * 从缓存读，没有时返回空
//     *
//     * @return
//     */
////    private fun responseByCache(key: String, request: Request): Response? {
////        val cacheResponseContent = CacheDiskUtils.getInstance(UKNetCacheConstant.HTTP_CACHE_DIR)
////                .getString(key)
////        return if (TextUtils.isEmpty(cacheResponseContent)) {
////            null
////        } else {
////            Response.Builder().code(200).body(ResponseBody.create(MediaType.parse(
////                    "application/json"), cacheResponseContent))
////                    .request(request)
////                    .message("OK")
////                    .addHeader("isCache", "true")
////                    .protocol(Protocol.HTTP_1_1)
////                    .build()
////        }
////    }
//
//    /**
//     * 强行读缓存，没有时code -1  message 缓存不存在
//     *
//     * @return
//     */
////    private fun responseByCacheForce(key: String, request: Request): Response {
////        var cacheResponseContent = CacheDiskUtils.getInstance(UKNetCacheConstant.HTTP_CACHE_DIR)
////                .getString(key)
////        var message = "OK"
////        if (TextUtils.isEmpty(cacheResponseContent)) {
////            cacheResponseContent = "{\"code\":-1,\"message\":\"缓存不存在\",\"data\":null}"
////            message = "缓存不存在"
////        }
////        return Response.Builder().code(200).body(ResponseBody.create(MediaType.pr(
////                "application/json"), cacheResponseContent))
////                .request(request)
////                .protocol(Protocol.HTTP_1_1)
////                .addHeader("isCache", "true")
////                .message(message)
////                .build()
////    }
//
//    @Throws(IOException::class)
//    private fun responseByDefault(chain: Interceptor.Chain, request: Request): Response {
//        return chain.proceed(request)
//    }
//
//    /**
//     * 正常的请求，但是要将结果存下来以备将来拿缓存使用
//     * 1024*1024,1MB的内容有可能会溢出，目前来看还好
//     * @param time 缓存有效期，秒
//     * @return ..
//     * @throws IOException
//     */
//    @Throws(IOException::class)
//    private fun responseByDefaultSaveCache(key: String, chain: Interceptor.Chain, request: Request, time: Int): Response {
//        val response = chain.proceed(request)
//        var responseContent = ""
//        val peekBody = response.peekBody(1024 * 1024)
//        if (peekBody != null && response.code == 200) {
//            val responseBody = peekBody.string()
//            try {
//                val jo = JSONObject(responseBody)
//                if (jo.getString("code") == "000") {
//                    CacheDiskUtils.getInstance(UKNetCacheConstant.HTTP_CACHE_DIR).put(key, responseContent, time)
//                }
//            } catch (e: java.lang.Exception) {
//                e.printStackTrace()
//            }
//        }
//        return response
//    }
//
//    /**
//     * 缓存关键字
//     *
//     * @return
//     */
//    private fun getCacheKey(content: String?, url: String): String {
//        val sb = StringBuilder()
//        sb.append(url)
//        return EncryptUtils.encryptMD5ToString(sb.append(content).toString().toByteArray())
//    }
//
//    /**
//     * 请求内容
//     *
//     * @param method 请求方法
//     * @param body   请求体
//     * @return
//     */
//    private fun getRequestContent(method: String, body: RequestBody?): String? {
//        if ("POST" == method.toUpperCase()) {
//            val buffer = Buffer()
//            try {
//                body!!.writeTo(buffer)
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//
//            return String(buffer.readByteArray())
//        }
//        return null
//    }
//}