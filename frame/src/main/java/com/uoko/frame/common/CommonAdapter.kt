package com.uoko.frame.common

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * 作者: xwb on 2018/8/5
 * 描述:
 */
abstract class CommonAdapter<T, K : BaseViewHolder>(var ItemId: Int,var pageSize:Int = 0) : BaseQuickAdapter<T, K>(ItemId) {

    companion object{

        const val PAGE_SIZE = 10

        //commonAdapter 普通加载，下拉刷新，上拉加载更多
        const val LOAD_TO_NORMAL = 300
        const val LOAD_TO_REFRESH = 301
        const val LOAD_TO_MORE = 302
    }

    var isHowLoad: Int = LOAD_TO_NORMAL
    var nextPage = 1

    /**
     * 数据集
     * @param currentPage,当前页码
     * @param totalPage，总页数
     * 当currentPage或tatalPage为空时，使用dataList.size与Constant.PAGE_SIZE进行对比
     */
    fun installData(dataList: List<T>?, currentPage: Int? = null, totalPage: Int? = null) {
        when (isHowLoad) {
            LOAD_TO_NORMAL -> {
                nextPage = 2
                setNewData(dataList)
                if (currentPage?.let { cpage -> totalPage?.let { cpage >= it } }
                                ?: (dataList?.size ?: 0 < if(pageSize> PAGE_SIZE) pageSize else PAGE_SIZE)) {
                    //最后一页
                    loadMoreEnd(true)
                }
            }
            LOAD_TO_REFRESH -> {
                nextPage = 2
                setNewData(dataList)
                if (currentPage?.let { cpage -> totalPage?.let { cpage >= it } }
                                ?: (dataList?.size ?: 0 < if(pageSize> PAGE_SIZE) pageSize else PAGE_SIZE)) {
                    //最后一页
                    loadMoreEnd(true)
                }
            }
            LOAD_TO_MORE -> {
                nextPage++
                dataList?.let {
                    addData(it)
                }
                if (currentPage?.let { cpage -> totalPage?.let { cpage >= it } }
                                ?: (dataList?.size ?: 0 < if(pageSize> PAGE_SIZE) pageSize else PAGE_SIZE)) {
                    loadMoreEnd(true)
                } else {
                    loadMoreComplete()
                }
            }
        }
    }
}