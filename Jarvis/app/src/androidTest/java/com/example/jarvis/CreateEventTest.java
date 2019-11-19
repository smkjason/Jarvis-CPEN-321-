package com.example.jarvis;

import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;
import android.widget.DatePicker;

import org.hamcrest.Description;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.app.PendingIntent.getActivity;
import static org.hamcrest.Matchers.not;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.Root;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;

public class CreateEventTest {

    private static final String TAG = "espressoCE";

    @Rule
    public final ActivityTestRule<CreateEvent> mActivityTestRule =
            new ActivityTestRule<>(CreateEvent.class, true, true);

    private String eventName = "BBQ";

    @Before
    public void setUp() throws Exception {
    }


    @Test
    public void testuserinputscenario(){

        int tvDate = R.id.tvDate;
        Espresso.onView(withId(R.id.name_of_event)).perform(typeText(eventName));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(tvDate)).perform(click());
        Espresso.onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(2019, 12, 25));
        closeSoftKeyboard();
        Espresso.onView(withText("OK")).perform(click());
        try {
            Thread.sleep(5000);
        }catch(InterruptedException e){
            Log.e(TAG, "sleeping exception", e);
        }
        Espresso.onView(withId(R.id.name_of_event)).check(matches(withText(eventName)));
        Espresso.onView(withId(tvDate)).check(matches(withText("25/12/2019")));
//        Espresso.onView(withText(R.string.CREATEVENT_TOAST))
//                .inRoot(withDecorView(not(getActivity().getWindow().getDecorView())))
//                .check(matches(isDisplayed()));
        //Espresso.onView(withText(R.string.CREATEVENT_TOAST)).inRoot(MobileViewMatchers.isToast()).check(matches(isDisplayed()));
    }

    @After
    public void tearDown() throws Exception {
    }

}