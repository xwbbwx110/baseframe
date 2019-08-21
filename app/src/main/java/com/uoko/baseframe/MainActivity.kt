package com.uoko.baseframe

import android.os.Bundle
import com.chad.library.adapter.base.BaseViewHolder
import com.uoko.baseframe.test.TestVIewModel
import com.uoko.frame.common.BaseActivity
import com.uoko.frame.common.InstallViewModel
import com.uoko.frame.dialog.UKLoadingLayout
import com.uoko.frame.expansion.installAdapter
import kotlinx.android.synthetic.main.activity_main.*

/**
 * test
 */
@InstallViewModel(viewModelclass = TestVIewModel::class)
class MainActivity : BaseActivity<TestVIewModel>(){
    override fun LayoutId(): Int {
   return R.layout.activity_main
    }





    override fun initData(savedInstanceState: Bundle?) {
//        viewModel.testFun()



        viewModel.liveData.onObserver(this,
            {

        },{ code,msg ->


        })


        rec_ly.installAdapter<Int,BaseViewHolder>(0){ baseViewHolder, data ->




        }


        ddddd.setOnClickListener { viewModel.testFun() }





    }

    override fun initListener() {
    }

    override fun installLoadingLayout(): UKLoadingLayout? {
        return an_lay
    }




}
