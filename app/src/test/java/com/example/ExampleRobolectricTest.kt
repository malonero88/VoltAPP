package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ui.viewmodel.CalculatorViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("VoltCalc", appName)
  }

  @Test
  fun `test dark mode toggle and persistence`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val prefs = app.getSharedPreferences("voltcalc_settings", Context.MODE_PRIVATE)
    prefs.edit().clear().commit()

    val viewModel = CalculatorViewModel(app)
    assertFalse("Default dark mode should be false", viewModel.isDarkMode.value)

    viewModel.toggleDarkMode()
    assertTrue("Dark mode should be true after toggle", viewModel.isDarkMode.value)
    assertTrue("SharedPreferences should have stored true", prefs.getBoolean("pref_dark_mode", false))

    // Recreating viewModel simulates app restart
    val newViewModel = CalculatorViewModel(app)
    assertTrue("New ViewModel should restore dark mode preference from SharedPreferences", newViewModel.isDarkMode.value)

    newViewModel.setDarkMode(false)
    assertFalse("Dark mode should be false after setDarkMode(false)", newViewModel.isDarkMode.value)
    assertFalse("SharedPreferences should store false", prefs.getBoolean("pref_dark_mode", true))
  }
}
