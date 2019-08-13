package com.uoko.frame.net

import com.uoko.frame.common.UKCall


/**
 * @author xwb
 *
 *  请求类型
 */
/*
普通
 */
fun <T> UKCall<T>.sendNormal(): UKCall<T> {

    loadType = UKCall.StatusInfo("", UKCall.SHOW_LOADING_TO_background)

    return this
}


/*
dialog
 */
fun <T> UKCall<T>.sendDialog(hitMsg:String = ""): UKCall<T> {

    loadType = UKCall.StatusInfo(hitMsg, UKCall.SHOW_LOADING_TO_DIALOG)

    return this
}


/*
动画
 */
fun <T> UKCall<T>.sendAnimation(hitMsg:String = ""): UKCall<T> {

    loadType = UKCall.StatusInfo(hitMsg, UKCall.SHOW_LOADING_TO_ANIMATION)

    return this
}