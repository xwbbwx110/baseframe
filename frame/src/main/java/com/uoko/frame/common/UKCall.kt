package com.uoko.frame.common

import retrofit2.Call
import kotlin.random.Random

class  UKCall<T>{

    /**
     * msg:显示的提示消息
     * type:提示类型
     * code:当前的标示
     */
     data class StatusInfo(var msg: String="",var type: Int = 0, var code: Int = Random.nextInt(10000))

     companion object{

         /**
          * 进度框- 后台加载 不显示任何东西
          */
         const val SHOW_LOADING_TO_background = 600

         /**
          * 进度框- 加载框
          */
         const val SHOW_LOADING_TO_DIALOG = 601

         /**
          * 进度框- 动画布局
          */
         const val SHOW_LOADING_TO_ANIMATION = 602

         /**
          * 取消加载
          */
         const val DISMISS = 800
     }

     /**
      * 原始call
      */
    var call:Call<T> ? = null



     /**
      * 当前的加载类型
      */
    var loadType = StatusInfo("", SHOW_LOADING_TO_background)
}