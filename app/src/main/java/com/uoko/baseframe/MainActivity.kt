package com.uoko.baseframe

import android.os.Bundle
import com.uoko.frame.common.BaseActivity
import com.uoko.frame.common.InstallViewLayout
import com.uoko.frame.common.InstallViewModel
import com.uoko.frame.dialog.UKLoadingLayout
import com.uoko.baseframe.test.TestVIewModel
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
        viewModel.testFun()


        viewModel.liveData.onObserver(this,
            {

        },{ code,msg ->


        })

    }

    override fun initListener() {
    }

    override fun installLoadingLayout(): UKLoadingLayout? {
        return an_lay
    }




}
