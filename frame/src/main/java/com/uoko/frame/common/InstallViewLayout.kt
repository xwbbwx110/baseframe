package com.uoko.frame.common

import java.lang.annotation.Inherited

/**
 * 作者: xwb
 */

@Target(AnnotationTarget.CLASS)
@Inherited //表示可以被继承\
annotation class InstallViewLayout(val viewId: Int)
