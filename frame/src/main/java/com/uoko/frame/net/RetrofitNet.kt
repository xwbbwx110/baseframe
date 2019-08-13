package com.uoko.frame.net

import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.LogUtils
import com.uoko.frame.common.UKCallAdapterFactory
//import com.uoko.ukappframe.net.UKLoggingInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.*


/**
 * 作者: xwb on 2018/7/27
 * 描述:
 */
object RetrofitNet {
    var mOkhttpClient: OkHttpClient

    val DEFAULT_TIMEOUT = 20000L

    var isDebug: Boolean = true
    var hostName: String? = null
    var imageHostName: String? = null

    private var defaultErrorMessage: String = "错误"

//    val httpRequestLoggingInterceptor: UKLoggingInterceptor = UKLoggingInterceptor(2).setLevel(HttpLoggingInterceptor.Level.BODY)
//    private val httpResponseLoggingInterceptor: UKLoggingInterceptor = UKLoggingInterceptor(1).setLevel(HttpLoggingInterceptor.Level.BODY)
    private val version = AppUtils.getAppVersionName()

    //uoko的证书
    private const val cer = "-----BEGIN CERTIFICATE-----\n" +
            "MIIGKzCCBROgAwIBAgIIMkj0LHtrgkcwDQYJKoZIhvcNAQELBQAwgbQxCzAJBgNV\n" +
            "BAYTAlVTMRAwDgYDVQQIEwdBcml6b25hMRMwEQYDVQQHEwpTY290dHNkYWxlMRow\n" +
            "GAYDVQQKExFHb0RhZGR5LmNvbSwgSW5jLjEtMCsGA1UECxMkaHR0cDovL2NlcnRz\n" +
            "LmdvZGFkZHkuY29tL3JlcG9zaXRvcnkvMTMwMQYDVQQDEypHbyBEYWRkeSBTZWN1\n" +
            "cmUgQ2VydGlmaWNhdGUgQXV0aG9yaXR5IC0gRzIwHhcNMTgwNjIyMTAyNDExWhcN\n" +
            "MTkwNjI1MDMyMDM4WjA4MSEwHwYDVQQLExhEb21haW4gQ29udHJvbCBWYWxpZGF0\n" +
            "ZWQxEzARBgNVBAMMCioudW9rby5jb20wggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAw\n" +
            "ggEKAoIBAQDGVsoeeMaRFtSL7BDoVcf146OFsAz0mbpxeCcNL0NdpUNjvMtvF4BI\n" +
            "9t4g40HVWj8Ju7dvl43McmZo2byYVIpujq/kfh0MujqEA24JIrqbBrqfO6fs88z/\n" +
            "EeY75v9m2912q6fca/gAJROuazsI/NqQTdpmMXwjKs4ETIgHi5OLa3NhUtNnnYVS\n" +
            "9ZKtc4fHWUNW6cOb8LCP5f0f9/+OGetIav8z3OV4m4EU0WtJFDih3OQFFocN4CCv\n" +
            "qtzL0vJxrrZpigcjLuzPTp4UxwKL3EJO2yxOAVdes0eDwuQuOc6EDUd3Zd/R6l8u\n" +
            "qvPKwlGKHJMcYI1QaccKxII7p8svVOmzAgMBAAGjggK6MIICtjAMBgNVHRMBAf8E\n" +
            "AjAAMB0GA1UdJQQWMBQGCCsGAQUFBwMBBggrBgEFBQcDAjAOBgNVHQ8BAf8EBAMC\n" +
            "BaAwNwYDVR0fBDAwLjAsoCqgKIYmaHR0cDovL2NybC5nb2RhZGR5LmNvbS9nZGln\n" +
            "MnMxLTg0MC5jcmwwXQYDVR0gBFYwVDBIBgtghkgBhv1tAQcXATA5MDcGCCsGAQUF\n" +
            "BwIBFitodHRwOi8vY2VydGlmaWNhdGVzLmdvZGFkZHkuY29tL3JlcG9zaXRvcnkv\n" +
            "MAgGBmeBDAECATB2BggrBgEFBQcBAQRqMGgwJAYIKwYBBQUHMAGGGGh0dHA6Ly9v\n" +
            "Y3NwLmdvZGFkZHkuY29tLzBABggrBgEFBQcwAoY0aHR0cDovL2NlcnRpZmljYXRl\n" +
            "cy5nb2RhZGR5LmNvbS9yZXBvc2l0b3J5L2dkaWcyLmNydDAfBgNVHSMEGDAWgBRA\n" +
            "wr0njsw0gzCiM9f7bLPwtCyAzjAfBgNVHREEGDAWggoqLnVva28uY29tggh1b2tv\n" +
            "LmNvbTAdBgNVHQ4EFgQUQpWfq1e4kAvSpbmsjMI6G4zP0XMwggEEBgorBgEEAdZ5\n" +
            "AgQCBIH1BIHyAPAAdQCkuQmQtBhYFIe7E6LMZ3AKPDWYBPkb37jjd80OyA3cEAAA\n" +
            "AWQnBqvpAAAEAwBGMEQCIEFDxUB3tVLiuH7pacq1FcxHwOyo5qup1aTs2ZTmQ71R\n" +
            "AiAvc62/f5ui7tVQ9UBY4gU0vnFz1pUEZ0tTtB7n3ads9wB3AHR+2oMxrTMQkSGc\n" +
            "ziVPQnDCv/1eQiAIxjc1eeYQe8xWAAABZCcGrRkAAAQDAEgwRgIhAN4ZW7IeWmpM\n" +
            "vu5KWJiy4IvdDuFEhh18gzmPDKWhlnwdAiEAvR7DvYvKNY+IwvbUjI+hCYGgYeYZ\n" +
            "ki7m0KRyrfAIaIYwDQYJKoZIhvcNAQELBQADggEBAFdlOI/RDKtGn17BoNnkToqM\n" +
            "T8aK6fFBb1kjrpwtPhzmoC1Pedw+kTTPSnK1N9SbXnbho2hQ7dRRR40cHmgLlTzK\n" +
            "rXZ3N3mEH0rt0bXnYb1jOm+eMl/dDC087rKt5QDeu9+LaxfkKrDpJ/+0RqXoH2L/\n" +
            "z7pat7el8dFgGnbz8jsdf0Cw8T00hYk7e2Jb6l7VOD1ORCJeyDlPnJq2Fv/0GXYg\n" +
            "uwUlw7Dpd1kOO/PiYKzNDrl46ozus0Me8NFN8hp94LDEtt9G391uE9Gr8bqj8dfx\n" +
            "0156PSthZd9l7S+vpzg38fP/MqUyG/qfRHYRFeyuO4zMPZ33zlmmaDxMnSFw/E4=\n" +
            "-----END CERTIFICATE-----"

    init {
        mOkhttpClient = OkHttpClient().newBuilder().readTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                .sslSocketFactory(getSSLSocketFactory(), UokoTrustManager())
//                .hostnameVerifier(getHostnameVerifier())
                .addInterceptor(HttpHeaderInterceptor())
                .addInterceptor(ErrorInterceptor())
//                .addInterceptor(httpResponseLoggingInterceptor)
//                .addInterceptor(UKCacheInterceptor())
                .build()
    }

    fun showLog(showLog: Boolean) {
//        if (showLog) {
//            httpRequestLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
//            httpResponseLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
//        } else {
//            httpRequestLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE)
//            httpResponseLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE)
//        }
    }

    internal class HttpHeaderInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {

            val request = chain.request()
                    .newBuilder()
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .addHeader("req_src", "uoko_app")
                    .addHeader("version", version)
                    .addHeader("os_name", "Android")
                    .build()

            return chain.proceed(request)

        }
    }

    fun addInterceptor(vararg interceptor: Interceptor) {
        val builder = mOkhttpClient.newBuilder()
        interceptor.forEach {
            builder.addInterceptor(it)
        }
        mOkhttpClient = builder.build()
    }


    private fun isHttpExpired(response: Response): Boolean {
        return response.code != 200/* || let {
            try {
                val json = JSONObject(response.peekBody(1024 * 1024).string())
                return json.has("code") && EnumHttpCode.SUCCESS.code != json.getString("code")
            } catch (e: Exception) {
                return false
            }
        }*/
    }

    internal class ErrorInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {

            val request = chain.request()
            val originalResponse = chain.proceed(request)

            if (isHttpExpired(originalResponse)) {
                val body = originalResponse.body

                body?.let {
                    val bodyStr = it.string()
                    var msg: String? = null
                    var code: String? = null
                    try {
                        val json = JSONObject(bodyStr)
                        if (json.has("msg")) {
                            msg = json.getString("msg")
                        } else if (json.has("message")) {
                            msg = json.getString("message")
                        }
                        if (json.has("code")) {
                            code = json.getString("code")
                        } else if (json.has("status")) {
                            code = json.getString("status")
                        }
                    } catch (e: Exception) {
                    }
                    throw UokoHttpException(it, code?.toIntOrNull()
                            ?: originalResponse.code,
                            originalResponse.code,
                            msg ?: defaultErrorMessage
                    )

                }
            }
            return originalResponse
        }
    }

    fun <T> createApi(baseUrl: String, apiClass: Class<T>): T {
        return Retrofit.Builder().client(mOkhttpClient)
                .baseUrl(baseUrl)
                .addCallAdapterFactory(UKCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(apiClass)
    }

    /**
     * 验证证书
     */
    private fun getSSLSocketFactory(): SSLSocketFactory {
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf(UokoTrustManager()), SecureRandom())
        return sslContext.socketFactory
    }

//    private fun getHostnameVerifier(): HostnameVerifier {
//        //这主要是防止别人改包，利用app向其它的域名发送数据，不太需要呢
//        return HostnameVerifier { hostName, sslSession ->
//            LogUtils.eTag("httpsVerify", "hostName:$hostName")
//            this@RetrofitNet.isDebug || sslSession.peerHost == this@RetrofitNet.hostName || sslSession.peerHost == this@RetrofitNet.imageHostName
//        }
//    }

    /**
     * 此处返回的是RootTrustManager:X509ExtendedTrustManager:X509TrustManager:TrustManager
     */
    private fun getTrustManager(): Array<X509TrustManager?> {
        val certificateFactory = CertificateFactory.getInstance("X.509")
        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        keyStore.load(null)
        keyStore.setCertificateEntry("0", certificateFactory.generateCertificate(cer.byteInputStream()))
        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(keyStore)
        val trustManager = trustManagerFactory.trustManagers
        val result = kotlin.arrayOfNulls<X509TrustManager>(trustManager.size)
        trustManager?.forEachIndexed { index, it -> result[index] = it as X509TrustManager }
        return result
    }

    class UokoTrustManager : X509TrustManager {
        /**
         * 假装检查了一下
         */
        override fun checkClientTrusted(chain: Array<out java.security.cert.X509Certificate>, authType: String?) {
        }

        /**
         * 这里真的检查了一下
         */
        override fun checkServerTrusted(chain: Array<out java.security.cert.X509Certificate>?, authType: String?) {
            if (isDebug) {
                return
            }
            if (chain?.isEmpty() != false || authType.isNullOrEmpty()) {
                throw IllegalArgumentException("证书不能为空")
            }
            LogUtils.eTag("httpsVerify", ConvertUtils.bytes2HexString(chain[0].signature))
            if (ConvertUtils.bytes2HexString(chain[0].signature) != "5765388FD10CAB469F5EC1A0D9E44E8A8C4FC68AE9F1416F5923AE9C2D3E1CE6A02D4F79DC3E9134CF4A72B537D49B5E76E1A36850EDD451478D1C1E680B953CCAAD76773779841F4AEDD1B5E761BD633A6F9E325FDD0C2D3CEEB2ADE500DEBBDF8B6B17E42AB0E927FFB446A5E81F62FFCFBA5AB7B7A5F1D1601A76F3F23B1D7F40B0F13D3485893B7B625BEA5ED5383D4E44225EC8394F9C9AB616FFF4197620BB0525C3B0E977590E3BF3E260ACCD0EB978EA8CEEB3431EF0D14DF21A7DE0B0C4B6DF46DFDD6E13D1ABF1BAA3F1D7F1D35E7A3D2B6165DF65ED2FAFA73837F1F3FF32A5321BFA9F44761115ECAE3B8CCC3D9DF7CE59A6683C4C9D2170FC4E") {
                throw CertificateException("证书不匹配")
            }
        }

        override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
            return arrayOf()
        }
    }
}