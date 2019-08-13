package com.uoko.frame.net

import okhttp3.ResponseBody

class UokoHttpException(val responseBody: ResponseBody?, val httpCode: Int, val originalCode: Int = 200, msg: String? = null) : RuntimeException(msg)
