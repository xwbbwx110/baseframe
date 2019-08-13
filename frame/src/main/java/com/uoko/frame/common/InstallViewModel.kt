package com.uoko.frame.common

import com.uoko.frame.repository.BaseRepository
import java.lang.annotation.Inherited
import kotlin.reflect.KClass

/**
 * 作者: xwb
 */

@Target(AnnotationTarget.CLASS)
@Inherited //表示可以被继承
annotation class InstallViewModel(val viewModelclass: KClass<out BaseUokoViewModel<BaseRepository>>)
