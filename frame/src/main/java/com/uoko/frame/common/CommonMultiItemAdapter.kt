package com.uoko.frame.common

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.MultiItemEntity

/**
 * Created by Administrator 可爱的路人 on 2018/8/14 0014.
 * Email:513421345@qq.com
 * TODO
 */
abstract class CommonMultiItemAdapter<T : MultiItemEntity, K : BaseViewHolder> : BaseQuickAdapter<T, K>(null) {

    companion object{

        const val PAGE_SIZE = 10

        //commonAdapter 普通加载，下拉刷新，上拉加载更多
        const val LOAD_TO_NORMAL = 300
        const val LOAD_TO_REFRESH = 301
        const val LOAD_TO_MORE = 302
    }



    var isHowLoad: Int = LOAD_TO_NORMAL
    var nextPage = 1

    fun installData(dataList: List<T>) {
        when (isHowLoad) {
            LOAD_TO_NORMAL -> {
                nextPage = 1
                setNewData(dataList)
            }
           LOAD_TO_REFRESH -> {
                nextPage = 1
                setNewData(dataList)
            }
           LOAD_TO_MORE -> {
                addData(dataList)
                if (dataList.size < PAGE_SIZE) {
                    loadMoreEnd(true)
                } else {
                    loadMoreComplete()
                    nextPage++
                }
            }
        }
    }
}