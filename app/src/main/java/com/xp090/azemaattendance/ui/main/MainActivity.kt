package com.xp090.azemaattendance.ui.main

import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.listener.multi.BaseMultiplePermissionsListener
import com.squareup.picasso.Picasso
import com.xp090.azemaattendance.R
import com.xp090.azemaattendance.ui.login.LoginActivity
import com.xp090.azemaattendance.util.PermissionsHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mainPagerAdapter = MainPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = mainPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        mainViewModel.attendanceRepository.attendanceData.value
            .let { attendance ->
                val defaultTabIndex = when {
                    attendance?.checkOut != null -> 2
                    attendance?.checkIn != null -> 1
                    else -> 0
                }
                viewPager.setCurrentItem(defaultTabIndex, false)}
        mainViewModel.userDataRepository.userData.observe(this, Observer {user ->
            user?.let {
                val headerView = nav_view.getHeaderView(0)
                val fullNameAndProject = "${user.fullName} - ${user.project?.name}"
                headerView.fullname_project.text = fullNameAndProject
                headerView.username.text = user.username
                Picasso.get().load(user.image).placeholder(R.drawable.ic_account).into(headerView.user_image)
            }

        })
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener {item ->
            when (item.itemId) {
                R.id.nav_logout -> {
                    mainViewModel.userDataRepository.logout()
                    startActivity<LoginActivity>()
                    finishAffinity()
                }

            }
            drawer_layout.closeDrawer(GravityCompat.START)
            true
        }

    }

    override fun onStart() {
        super.onStart()
        PermissionsHelper.checkForRequiredPermissions(this, object : BaseMultiplePermissionsListener() {
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
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

}