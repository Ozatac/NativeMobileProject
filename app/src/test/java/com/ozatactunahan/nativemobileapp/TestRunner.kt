package com.ozatactunahan.nativemobileapp

import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    // ViewModel Tests
    com.ozatactunahan.nativemobileapp.ui.home.HomeViewModelTest::class,
    com.ozatactunahan.nativemobileapp.ui.filter.FilterViewModelTest::class,
    com.ozatactunahan.nativemobileapp.ui.dashboard.DashboardViewModelTest::class,
    com.ozatactunahan.nativemobileapp.ui.notifications.NotificationsViewModelTest::class,
    com.ozatactunahan.nativemobileapp.ui.productdetail.ProductDetailViewModelTest::class,
    com.ozatactunahan.nativemobileapp.ui.profile.ProfileViewModelTest::class
)
class TestRunner
