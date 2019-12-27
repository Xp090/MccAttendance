package com.xp090.azemaattendance.ui.main.dailyreport

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.xp090.azemaattendance.R

class DailyReportFragment : Fragment() {

    companion object {
        fun newInstance() = DailyReportFragment()
    }

    private lateinit var viewModel: DailyReportViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_daily_report, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(DailyReportViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
