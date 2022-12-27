package com.example.wirlesstask.ui.acvtivities

import android.os.Bundle
import com.example.wirlesstask.core.base.BaseActivity
import com.example.wirlesstask.core.extension.show
import com.example.wirlesstask.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun onActivityReady(savedInstanceState: Bundle?) {

    }

    override fun viewInit() {

    }

    override fun onLoading(loading: Boolean) {
        super.onLoading(loading)
        binding.includeLoading.root.show(loading)
    }
}