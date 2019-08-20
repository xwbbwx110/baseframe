package com.uoko.baseframe.test

data class TestUserdata(
    val data: List<Data?>?,
    val message: Any?,
    val rel: Boolean?,
    val status: Int?
) {
    data class Data(
        val companyName: String?,
        val id: String?,
        val routeUrl: String?
    )
}