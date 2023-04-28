package com.example.singmeapp

import android.app.Activity
import android.os.AsyncTask
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import com.example.singmeapp.adapters.TrackAdapter
import com.example.singmeapp.fragments.PlayerPlayerFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matcher
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    lateinit var trackName: String
    @Test
    @Throws(Exception::class)
    fun registration() {
        // Запускаем SplashcreenActivity
        val activityScenario = ActivityScenario.launch(SplashScreenActivity::class.java)

        // Ожидаем открытия MainActivity
        onView(withId(R.id.mainActyvityCoordinatorLayout)).check(matches(isDisplayed()))

        // Закрываем SplashcreenActivity
        activityScenario.close()

        // переход на фрагмент регистрации
        onView(withId(R.id.bSignUp2)).perform(click())

        //заполнение формы
        onView(withId(R.id.etNameInRegistration)).perform(typeText("Ivan"))
        onView(withId(R.id.etRealNameInRegistration)).perform(replaceText("Иван"))
        onView(withId(R.id.etLastNameInRegistration)).perform(replaceText("Тузов"))
        onView(withId(R.id.etEmailInRegistration)).perform(typeText("mopitos325@fectode.com"))
        onView(withId(R.id.etPasswordInRegistration)).perform(typeText("12345678"))
        onView(withId(R.id.etRepeatPasswordInRegistration)).perform(typeText("12345678"))
        Espresso.pressBack()
        onView(withId(R.id.datePicker1)).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.datePicker1)).perform(PickerActions.setDate(2001, 4, 1))
        onView(withId(R.id.textView)).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.rbMale)).perform(click())
        onView(withId(R.id.bSignUpReg)).perform(click())

        // ожидание перехода на фрагмент авторизации
        onView(isRoot()).perform(waitFor(5000))

        // проверка открытого окна авторизации
        onView(withId(R.id.etEmailInLogin)).check(matches(isDisplayed()))
        onView(withId(R.id.etPasswordInLogin)).check(matches(isDisplayed()))
    }

    @Test
    @Throws(Exception::class)
    fun addTrackInLibrary(){
        val activityScenario = ActivityScenario.launch(SplashScreenActivity::class.java)

        // Ожидаем открытия MainActivity
        onView(withId(R.id.mainActyvityCoordinatorLayout)).check(matches(isDisplayed()))

        // Закрываем SplashcreenActivity
        activityScenario.close()

        onView(withId(R.id.catalogueFragment)).perform(click())

        onView(withId(R.id.catalogueNewsProgressLayout)).perform(actionWithAssertions(waitUntilViewIsNotDisplayed()))

        onView(withId(R.id.rvCatalogueNewTracks)).check(matches(isDisplayed()))

        onView(withId(R.id.rvCatalogueNewTracks))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    clickChildViewWithId(R.id.ibItemTrackMenu)
                )
            ).perform(getText())


        onView(withId(R.id.bottomSheetMenu)).perform(actionWithAssertions(waitUntilBottomSheetMenuInDisplayed()))
        onView(withId(R.id.trackMenu)).perform(actionWithAssertions(waitUntilViewIsNotDisplayed()))
        onView(withId(R.id.trackMenu)).check(matches(isDisplayed()))
        onView(withId(R.id.tvAddTrackToLove)).perform(click())
        onView(withId(R.id.myLibraryFragment)).perform(click())
        onView(withText(trackName)).check(matches(isDisplayed()))
    }

    var currentActivity: Activity? = null

    fun getActivityInstance(): Activity? {
        getInstrumentation().runOnMainSync(Runnable {
            val resumedActivities: Collection<*> =
                ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED)
            if (resumedActivities.iterator().hasNext()) {
                currentActivity = resumedActivities.iterator().next() as Activity?
            }
        })
        return currentActivity
    }

    @Test
    @Throws(Exception::class)
    fun player(){

        val activityScenario = ActivityScenario.launch(SplashScreenActivity::class.java)

        // Ожидаем открытия MainActivity
        onView(withId(R.id.mainActyvityCoordinatorLayout)).check(matches(isDisplayed()))

        // Закрываем SplashcreenActivity
        activityScenario.close()


        onView(withId(R.id.myLibraryFragment)).perform(click())

        onView(withId(R.id.myLibraryProgressLayout)).perform(actionWithAssertions(waitUntilViewIsNotDisplayed()))


        onView(withId(R.id.player)).perform(openBottomSheetBehaviour())


        Thread{
            onView(withId(R.id.rcView))
                .perform(
                    RecyclerViewActions.actionOnItemAtPosition<TrackAdapter.TrackHolder>(0, click()),
                )
            onView(withId(R.id.ibPlay)).perform(click())
            onView(withId(R.id.ibPlay)).perform(click())
            onView(withId(R.id.seekBar)).perform(setProgress(10))
            onView(withId(R.id.ibMusicRight)).perform(click())
            onView(allOf(withText("B.Y.O.B"), withId(R.id.tvPlayerTrackName))).perform(waitUntilViewIsDisplayed())
            onView(withId(R.id.player)).perform(closeBottomSheetBehaviour())
            onView(withId(R.id.ibClose)).perform(click())
        }

    }

    @Test
    fun test(){

    }


    fun waitFor(delay: Long): ViewAction? {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = isRoot()
            override fun getDescription(): String = "wait for $delay milliseconds"
            override fun perform(uiController: UiController, v: View?) {
                uiController.loopMainThreadForAtLeast(delay)
            }
        }
    }

    fun clickChildViewWithId(id: Int): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View?>? {
                return null
            }

            override fun getDescription(): String? {
                return "Click on a child view with specified id."
            }

            override fun perform(uiController: UiController?, view: View) {
                val v = view.findViewById<View>(id)
                v.performClick()
            }
        }
    }

    fun getText(): ViewAction{
        return object : ViewAction{
            override fun getDescription(): String {
                return ""
            }

            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(View::class.java)
            }

            override fun perform(uiController: UiController?, view: View?) {
                val text = view?.findViewById<View>(R.id.tvItemTrackName) as TextView
                trackName = text.text.toString()
            }

        }
    }
    fun waitUntilViewIsNotDisplayed(): ViewAction {
        return object : ViewAction {
            override fun getDescription(): String {
                return "Wait until the view is not displayed"
            }

            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(View::class.java)
            }

            override fun perform(uiController: UiController?, view: View?) {
                if (view?.visibility == View.VISIBLE) {
                    val timeout = 3000L // таймаут в миллисекундах
                    val startTime = System.currentTimeMillis()
                    while (System.currentTimeMillis() - startTime < timeout) {
                        if (view.visibility == View.GONE) {
                            return // когда view станет невидимой, прерываем ожидание
                        }
                        uiController?.loopMainThreadForAtLeast(50) // пауза между итерациями
                    }
                }

            }
        }
    }

    fun waitUntilViewIsDisplayed(): ViewAction {
        return object : ViewAction {
            override fun getDescription(): String {
                return "Wait until the view is not displayed"
            }

            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(View::class.java)
            }

            override fun perform(uiController: UiController?, view: View?) {
                if (view?.visibility == View.GONE || view?.visibility == View.INVISIBLE) {
                    val timeout = 3000L // таймаут в миллисекундах
                    val startTime = System.currentTimeMillis()
                    while (System.currentTimeMillis() - startTime < timeout) {
                        if (view.visibility == View.VISIBLE) {
                            return // когда view станет невидимой, прерываем ожидание
                        }
                        uiController?.loopMainThreadForAtLeast(50) // пауза между итерациями
                    }
                }

            }
        }
    }

    fun waitUntilBottomSheetMenuInDisplayed(): ViewAction {
        return object : ViewAction {
            override fun getDescription(): String {
                return "Wait until the view is not displayed"
            }

            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(View::class.java)
            }

            override fun perform(uiController: UiController?, view: View?) {
                val bottomSheet = view!!.findViewById<View>(R.id.bottomSheetMenu)
                val bs = BottomSheetBehavior.from(bottomSheet)
                if (bs.state != BottomSheetBehavior.STATE_EXPANDED ) {
                    val timeout = 3000L // таймаут в миллисекундах
                    val startTime = System.currentTimeMillis()
                    while (System.currentTimeMillis() - startTime < timeout) {
                        if (bs.state == BottomSheetBehavior.STATE_EXPANDED) {
                            return // когда view станет невидимой, прерываем ожидание
                        }
                        uiController?.loopMainThreadForAtLeast(50) // пауза между итерациями
                    }
                }

            }
        }
    }

    fun openBottomSheetBehaviour() : ViewAction{
        return object : ViewAction {
            override fun getDescription(): String {
                return ""
            }

            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(View::class.java)
            }

            override fun perform(uiController: UiController?, view: View?) {
                val bottomSheet = view!!.findViewById<View>(R.id.player)
                val bs = BottomSheetBehavior.from(bottomSheet)
                bs.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    fun closeBottomSheetBehaviour() : ViewAction{
        return object : ViewAction {
            override fun getDescription(): String {
                return ""
            }

            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(View::class.java)
            }

            override fun perform(uiController: UiController?, view: View?) {
                val bottomSheet = view!!.findViewById<View>(R.id.player)
                val bs = BottomSheetBehavior.from(bottomSheet)
                bs.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    fun setProgress(progress: Int): ViewAction? {
        return object : ViewAction {
            override fun perform(uiController: UiController, view: View) {
                val seekBar = view as SeekBar
                seekBar.progress = progress
            }

            override fun getDescription(): String {
                return "Set a progress on a SeekBar"
            }

            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(SeekBar::class.java)
            }
        }
    }
}