package com.uoko.frame.common

import okhttp3.MediaType
import okhttp3.ResponseBody


data class UKBaseResponse<T>(var status: Int? = null,
                             var message: String? = null,
                             var data: T? = null,
                             var total: Int = 0) {




}