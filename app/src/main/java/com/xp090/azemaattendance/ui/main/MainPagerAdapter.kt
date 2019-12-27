package com.xp090.azemaattendance.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.xp090.azemaattendance.R
import com.xp090.azemaattendance.data.type.CheckType
import com.xp090.azemaattendance.ui.main.checkinout.ARG_CHECK_TYPE
import com.xp090.azemaattendance.ui.main.checkinout.CheckInOutFragment
import com.xp090.azemaattendance.ui.main.dailyreport.DailyReportFragment
import com.xp090.azemaattendance.util.ext.withArguments

private val TAB_TITLES = arrayOf(
    R.string.check_in,
    R.string.check_out,
    R.string.daily_report

)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class MainPagerAdapter(private val context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
       return when(position){
           0 -> CheckInOutFragment().withArguments(ARG_CHECK_TYPE to CheckType.CheckIn)
           1 -> CheckInOutFragment().withArguments(ARG_CHECK_TYPE to CheckType.CheckOut)
           2 -> DailyReportFragment()
           else -> throw Exception("Tab fragment not found")
       }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        return TAB_TITLES.size
    }
}