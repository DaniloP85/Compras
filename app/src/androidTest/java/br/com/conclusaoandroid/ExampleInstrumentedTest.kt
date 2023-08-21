package br.com.conclusaoandroid

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @Test
    fun deve_tentar_login_sem_usuario_e_senha(){
        ActivityScenario.launch(Login::class.java)
        Espresso.onView(withId(R.id.btnLogin)).perform(ViewActions.click())
    }

    @Test
    fun deve_preencher_usuario(){
        ActivityScenario.launch(Login::class.java)
        Espresso.onView(withId(R.id.email)).perform(ViewActions.typeText("dpsnqmk@gmail.com"), ViewActions.closeSoftKeyboard())
        Espresso.onView(withId(R.id.btnLogin)).perform(ViewActions.click())
    }

    @Test
    fun deve_preencher_senha(){
        ActivityScenario.launch(Login::class.java)
        Espresso.onView(withId(R.id.password)).perform(ViewActions.typeText("123456"), ViewActions.closeSoftKeyboard())
        Espresso.onView(withId(R.id.btnLogin)).perform((ViewActions.click()))
    }

    @Test
    fun deve_fazer_login(){
        ActivityScenario.launch(Login::class.java)
        Espresso.onView(withId(R.id.email)).perform(ViewActions.typeText("dpsnqmk@gmail.com"), ViewActions.closeSoftKeyboard())
        Espresso.onView(withId(R.id.password)).perform(ViewActions.typeText("123456"), ViewActions.closeSoftKeyboard())
        Espresso.onView(withId(R.id.btnLogin)).perform(ViewActions.click())
    }

    fun ViewInteraction.isDisplayed(): Boolean {
        try {
            check(matches(ViewMatchers.isDisplayed()))
            return true
        } catch (e: NoMatchingViewException) {
            return false
        }
    }
}