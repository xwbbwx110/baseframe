package com.uoko.frame.net

import com.blankj.utilcode.util.LogUtils
import com.google.common.net.HttpHeaders
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer
import okio.GzipSource
import java.io.EOFException
import java.io.IOException
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

/**
 * Created by Administrator 拇指/可爱的路人 on 2018/11/16 0016.
 * Email:513421345@qq.com
 * 用于打印网络请求和响应
 * @param isRequest 0全部，1打印响应 2打印请求
 */
class UKLoggingInterceptor(var isRequest: Int) : Interceptor {
    private val UTF8 = Charset.forName("UTF-8")

    @Volatile
    private var level: HttpLoggingInterceptor.Level? = null

    fun setLevel(level: HttpLoggingInterceptor.Level): UKLoggingInterceptor {
        this.level = level
        return this
    }

    fun getLevel(): HttpLoggingInterceptor.Level? {
        return this.level
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val level = this.level
        val request = chain.request()
        if (level == HttpLoggingInterceptor.Level.NONE) {
            return chain.proceed(request)
        } else {
            val logBody = level == HttpLoggingInterceptor.Level.BODY
            val logHeaders = logBody || level == HttpLoggingInterceptor.Level.HEADERS
            val requestBody = request.body
            val hasRequestBody = requestBody != null
            val connection = chain.connection()
            var requestStartMessage = ""
            if (isRequest == 0 || isRequest == 2) {
                val requestLogs = ArrayList<String>(20)
                requestLogs.add("这是请求")

                if (!logHeaders && hasRequestBody) {
                    requestStartMessage = "$requestStartMessage (${requestBody?.contentLength()
                            ?: 0}- byte body)"
                } else {
                    requestStartMessage = "--> ${request.method} ${request.url} ${connection?.protocol()?.toString()
                            ?: ""}"
                }
                requestLogs.add(requestStartMessage)

                if (logHeaders) {
                    if (hasRequestBody) {
                        requestBody?.contentType()?.let {
                            requestLogs.add("Content-Type: $it")
                        }

                        if (requestBody?.contentLength() != -1L) {
                            requestLogs.add("Content-Length: ${requestBody?.contentLength() ?: 0}")
                        }
                    }

                    val headers = request.headers
                    var i = 0

                    val count = headers.size
                    while (i < count) {
                        val name = headers.name(i)
                        if (!"Content-Type".equals(name, ignoreCase = true) && !"Content-Length".equals(name, ignoreCase = true)) {
                            requestLogs.add("$name : ${headers.value(i)}")
                        }
                        ++i
                    }

                    if (logBody && hasRequestBody) {
                        if (this.bodyHasUnknownEncoding(request.headers)) {
                            requestLogs.add("--> END " + request.method + " (encoded body omitted)")
                        } else {
                            val buffer = Buffer()
                            requestBody!!.writeTo(buffer)
                            var charset: Charset? = UTF8
                            val contentType = requestBody.contentType()
                            if (contentType != null) {
                                charset = contentType.charset(UTF8)
                            }

                            requestLogs.add("")
                            if (isPlaintext(buffer)) {
                                requestLogs.add(buffer.readString(charset!!))
                                requestLogs.add("--> END ${request.method} (${requestBody.contentLength()} -byte body)")
                            } else {
                                requestLogs.add("--> END ${request.method} (binary ${requestBody.contentLength()} -byte body omitted)")
                            }
                        }
                    } else {
                        requestLogs.add("--> END ${request.method}")
                    }
                }
                LogUtils.iTag("GsonRequest", *requestLogs.toArray())
            }
            val responseLogs = ArrayList<String>()
            responseLogs.add("这是响应")

            val response: Response
            try {
                response = chain.proceed(request)
            } catch (var27: Exception) {
                responseLogs.add("<-- HTTP FAILED: $var27")
                throw var27
            }
            if (isRequest == 0 || isRequest == 1) {
                val startNs = System.nanoTime()


                val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
                val responseBody = response.body
                val contentLength = responseBody?.contentLength()
                val bodySize = if (contentLength != -1L) contentLength.toString() + "-byte" else "unknown-length"
                responseLogs.add("${response.code} ${if (response.message.isNullOrEmpty()) "" else response.message} ${response.request.url} ($tookMs ms( ${if (!logHeaders) ", $bodySize body)" else ")"} )")
                if (logHeaders) {
                    val headers = response.headers
                    var i = 0

                    val count = headers.size
                    while (i < count) {
                        responseLogs.add(headers.name(i) + ": " + headers.value(i))
                        ++i
                    }



//                    if (logBody ) {
//                        if (!this.bodyHasUnknownEncoding(response.headers)) {
//                            val source = responseBody!!.source()!!
//                            source.request(9223372036854775807L)
//                            var buffer = source.buffer()
//                            if ("gzip".equals(headers.get("Content-Encoding"), ignoreCase = true)) {
//                                var gzippedResponseBody: GzipSource? = null
//
//                                try {
//                                    gzippedResponseBody = GzipSource(buffer.clone())
//                                    buffer = Buffer()
//                                    buffer.writeAll(gzippedResponseBody)
//                                } finally {
//                                    gzippedResponseBody?.close()
//
//                                }
//                            }
//
//                            var charset: Charset? = UTF8
//                            val contentType = responseBody.contentType()
//                            if (contentType != null) {
//                                charset = contentType.charset(UTF8)
//                            }
//
//                            if (!isPlaintext(buffer)) {
//                                responseLogs.add("")
//                                LogUtils.iTag("GsonRequest", *responseLogs.toArray())
//                                return response
//                            }
//
//                            if (contentLength != 0L) {
//                                responseLogs.add("")
//                                responseLogs.add(buffer.clone().readString(charset!!))
//                            }
//                        }
//                    }
                }
                LogUtils.iTag("GsonRequest", *responseLogs.toArray())
            }
            return response
        }
    }

    internal fun isPlaintext(buffer: Buffer): Boolean {
        try {
            val prefix = Buffer()
            val byteCount = if (buffer.size < 64L) buffer.size else 64L
            buffer.copyTo(prefix, 0L, byteCount)

            var i = 0
            while (i < 16 && !prefix.exhausted()) {
                val codePoint = prefix.readUtf8CodePoint()
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false
                }
                ++i
            }

            return true
        } catch (var6: EOFException) {
            return false
        }

    }

    private fun bodyHasUnknownEncoding(headers: Headers): Boolean {
        val contentEncoding = headers.get("Content-Encoding")
        return contentEncoding != null && !contentEncoding.equals("identity", ignoreCase = true) && !contentEncoding.equals("gzip", ignoreCase = true)
    }
}