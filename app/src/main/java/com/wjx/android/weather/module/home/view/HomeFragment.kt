package com.wjx.android.weather.module.home.view

import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.viewpager.widget.ViewPager
import com.wjx.android.weather.R
import com.wjx.android.weather.base.view.BaseLifeCycleFragment
import com.wjx.android.weather.common.Constant
import com.wjx.android.weather.common.util.CommonUtil
import com.wjx.android.weather.common.util.SPreference
import com.wjx.android.weather.databinding.HomeFragmentBinding
import com.wjx.android.weather.model.Place
import com.wjx.android.weather.module.home.adapter.HomeDetailAdapter
import com.wjx.android.weather.module.home.viewmodel.HomeDetailViewModel
import com.zhpan.indicator.enums.IndicatorSlideMode
import com.zhpan.indicator.enums.IndicatorStyle
import kotlinx.android.synthetic.main.home_detail_fragment.*
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.home_fragment.view.*

class HomeFragment : BaseLifeCycleFragment<HomeDetailViewModel, HomeFragmentBinding>() {
    override fun getLayoutId(): Int = R.layout.home_fragment

    private var mPosition: Int by SPreference(Constant.POSITION, 0)

    private val mPlaceNameList = arrayListOf<String>()

    override fun initView() {
        super.initView()
        if (mPosition != 0) {
            home_viewpager.setCurrentItem(mPosition, false)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_city -> {
                Navigation.findNavController(home_container)
                    .navigate(R.id.action_homeFragment_to_choosePlaceFragment)
            }
            R.id.action_more -> {
                CommonUtil.showToast(requireContext(), getString(R.string.more))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initToolbar(title: String?) {
        home_bar.home_title.text = title
        home_bar.setTitle("")
        (requireActivity() as AppCompatActivity).setSupportActionBar(home_bar)
    }

    override fun initData() {
        super.initData()
        mViewModel.queryAllPlace()
    }

    override fun initDataObserver() {
        super.initDataObserver()

        mViewModel.mPlaceData.observe(this, Observer { response ->
            response?.let {
                initHomeDetailFragment(it)
                changeTitle(mPosition)
                if (mPlaceNameList.isEmpty()) {
                    Navigation.findNavController(home_normal_view)
                        .navigate(R.id.action_homeFragment_to_choosePlaceFragment)
                }
            }
        })
    }

    private fun initHomeDetailFragment(dataList: MutableList<Place>) {
        val tabs = arrayListOf<String>()
        val fragments = arrayListOf<Fragment>()
        for (data in dataList) {
            tabs.add(data.name)
            fragments.add(
                HomeDetailFragment.newInstance(
                    data.name,
                    data.location.lng,
                    data.location.lat
                )
            )
        }
        mPlaceNameList.addAll(tabs)
        home_viewpager.adapter = HomeDetailAdapter(childFragmentManager, tabs, fragments)
        //设置监听
        home_viewpager.addOnPageChangeListener(TitlePageChangeListener())

        indicator_view
            .setSliderColor(
                CommonUtil.getColor(requireContext(), R.color.dark),
                CommonUtil.getColor(requireContext(), R.color.always_white_text)
            )
            .setSliderWidth(resources.getDimension(R.dimen.safe_padding))
            .setSliderHeight(resources.getDimension(R.dimen.safe_padding))
            .setSlideMode(IndicatorSlideMode.WORM)
            .setIndicatorStyle(IndicatorStyle.CIRCLE)
            .setupWithViewPager(home_viewpager)
    }

    private inner class TitlePageChangeListener : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {
            changeTitle(position)
        }
    }

    private fun changeTitle(position: Int) {
        home_bar.home_title.text = mPlaceNameList[position]
    }
}