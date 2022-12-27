package com.example.wirlesstask.core.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewbinding.ViewBinding
import com.example.wirlesstask.R
import com.example.wirlesstask.core.extension.*
import com.example.wirlesstask.databinding.FragmentBaseToolbarBinding

abstract class BaseViewBinding<B : ViewBinding> : Fragment() {

    private var _bindingBase: FragmentBaseToolbarBinding? = null
    private val bindingBase: FragmentBaseToolbarBinding get() = _bindingBase!!

    private var _binding: B? = null
    protected val binding: B get() = _binding!!

    protected abstract fun onRefreshView()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _bindingBase = FragmentBaseToolbarBinding.inflate(layoutInflater)

        _binding = bindView()
        bindingBase.baseBarFrame.addView(
            binding.root, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        bindingBase.baseBarFrame.initSwipeRefresh { onRefreshView() }

        return bindingBase.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onSwipeRefreshStatus(false)
        showToolbar(false)
        bindingBase.baseToolbar.setNavigationOnClickListener { closeFragment() }
    }

    fun setToolBarConfigs(
        @StringRes intRes: Int, showBackIcon: Boolean = false, showActionButton: Boolean = false,
        @DrawableRes actionButtonIcon: Int? = null
    ) {
        showToolbar(true)
        bindingBase.baseToolbar.title = getString(intRes)
        bindingBase.btnAction.show(showActionButton)
        showToolbarNavigationBack(showBackIcon)
        actionButtonIcon?.let { bindingBase.btnAction.setImageResource(it) }
    }

    fun updateActionButtonDrawable(  @DrawableRes actionButtonIcon: Int? = null){
        if (actionButtonIcon!=null) { bindingBase.btnAction.setImageResource(actionButtonIcon) }
        else bindingBase.btnAction.show(false)

    }
    private fun showToolbar(status: Boolean = true) {
        bindingBase.baseAppBar.show(status)
    }

    fun onSwipeRefreshStatus(status: Boolean = true) {
        bindingBase.baseBarFrame.isEnabled = status
    }

    fun stopSwipeRefreshLoading() {
        bindingBase.baseBarFrame.isRefreshing = false
    }

    fun onActionButtonClickListener(onClick: () -> Unit = {}) {
        bindingBase.btnAction.setOnClickListener { onClick.invoke() }
    }

    private fun showToolbarNavigationBack(show: Boolean) {
        bindingBase.baseToolbar.navigationIcon = if (show)
            requireContext().getDrawableFromRes(R.drawable.ic_back_arrow)
        else
            null
    }

    private fun closeFragment() {
        activity?.onBackPressedDispatcher?.onBackPressed()
    }

    fun showProgress(show: Boolean = true) {
        if (requireActivity() is BaseActivity<*>) {
            castToActivity<BaseActivity<*>> {
                it?.onLoading(show)
            }
        } else "Can't start progress".toLog(TAG)
    }

    override fun onDestroyView() {
        showProgress(false)
        super.onDestroyView()
        _bindingBase = null
        _binding = null
    }

    private fun SwipeRefreshLayout.initSwipeRefresh(onRefresh: () -> Unit) {
        setColorSchemeColors(
            requireContext().getColorFromRes(R.color.colorNetworkConnected),
            requireContext().getColorFromRes(R.color.colorNetworkWaiting),
            requireContext().getColorFromRes(R.color.colorNetworkLost),
        )
        setOnRefreshListener { onRefresh() }
    }

    companion object {
        private const val TAG = "BaseViewBinding"
    }
}