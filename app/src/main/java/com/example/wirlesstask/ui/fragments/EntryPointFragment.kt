package com.example.wirlesstask.ui.fragments

import androidx.lifecycle.lifecycleScope
import com.example.wirlesstask.core.base.BaseFragment
import com.example.wirlesstask.core.extension.navigateSafe
import com.example.wirlesstask.core.extension.popUpCurrentFragment
import com.example.wirlesstask.databinding.FragmentEntryPointBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class EntryPointFragment : BaseFragment<FragmentEntryPointBinding>() {

    override fun onFragmentReady() {
        showProgress(true)

        lifecycleScope.launchWhenStarted {
            withContext(Dispatchers.Main) {
                delay(3000)
                navigateSafe(
                    EntryPointFragmentDirections.actionEntryPointFragmentToHomeFragment(),
                    navOptions = popUpCurrentFragment()
                )
            }
        }

        //   bluetooth()
    }

    override fun subscribeToObservables() {}

    override fun onRefreshView() {}

}