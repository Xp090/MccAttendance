package com.xp090.azemaattendance.ui.main.dailyreport

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar

import com.xp090.azemaattendance.R
import kotlinx.android.synthetic.main.fragment_check_in_out.*
import kotlinx.android.synthetic.main.fragment_daily_report.*
import kotlinx.android.synthetic.main.fragment_daily_report.container
import org.koin.androidx.viewmodel.ext.android.viewModel

class DailyReportFragment : Fragment() {



    private val dailyReportViewModel: DailyReportViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_daily_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSend.setOnClickListener {
            dailyReportViewModel.submitDailyReport(dailyReportText.text.toString())
        }
        dailyReportText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                btnSend.isEnabled = !TextUtils.isEmpty(s!!.toString().trim())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
        dailyReportViewModel.attendanceData.observe(this, Observer {
            if (it?.checkIn != null) {
                if (!TextUtils.isEmpty(dailyReportText.text.toString().trim())) btnSend.isEnabled = true
                dailyReportText.isEnabled = true
            }else{
                btnSend.isEnabled = false
                dailyReportText.isEnabled = false
            }
        })

        dailyReportViewModel.dailyReportEvent.observe(this, Observer {

            if (it != null) {
                if (it.isLoading) {
                    btnSend.visibility = View.GONE
                    drLoading.visibility = View.VISIBLE
                    dailyReportText.isEnabled = false
                } else {
                    btnSend.visibility = View.VISIBLE
                    drLoading.visibility = View.GONE
                    dailyReportText.isEnabled = true
                    if (it.success != null) {
                        dailyReportText.setText("")
                        Snackbar.make(container,R.string.dr_sent_message, Snackbar.LENGTH_LONG).show()
                    } else if (it.error != null) {
                        Snackbar.make(container,R.string.unable_dr_message, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        })

    }

}
