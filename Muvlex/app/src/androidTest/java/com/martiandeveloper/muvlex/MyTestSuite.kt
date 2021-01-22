package com.martiandeveloper.muvlex

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.launchActivity
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.martiandeveloper.muvlex.view.main.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MyTestSuite {

    @get:Rule
    var activityScenarioRule = activityScenarioRule<MainActivity>()

    // Drive the activity to a new state
    @Test
    fun testEvent() {
        val scenario = activityScenarioRule.scenario
        scenario.moveToState(Lifecycle.State.DESTROYED)
    }

    // Determine the current activity state
    @Test
    fun testEvent2() {

        val scenario = launchActivity<MainActivity>()

        scenario.onActivity {
            //startActivity(Intent(activity, MyOtherActivity::class.java))
        }

        //val originalActivityState = scenario.state

    }

}
