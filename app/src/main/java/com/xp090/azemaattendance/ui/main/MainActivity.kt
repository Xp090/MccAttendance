package com.xp090.azemaattendance.ui.main

import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.listener.multi.BaseMultiplePermissionsListener
import com.xp090.azemaattendance.R
import com.xp090.azemaattendance.util.PermissionsHelper

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mainPagerAdapter = MainPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = mainPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)

    }

    override fun onStart() {
        super.onStart()
        PermissionsHelper.checkForRequiredPermissions(this,object: BaseMultiplePermissionsListener() {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                report?.let {
                    if (!it.areAllPermissionsGranted()) {
                        finish()
                        finishAffinity()
                    }
                }
            }
        })
    }

}