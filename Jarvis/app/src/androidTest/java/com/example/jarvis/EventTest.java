package com.example.jarvis;

import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.test.espresso.contrib.PickerActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class EventTest {

    @Rule
    public ActivityTestRule<CreateEvent> mActivityRule =
            new ActivityTestRule<>(CreateEvent.class);


    @Test
    public void checkIfButtonsClickable() {
        onView(withId(R.id.name_of_event)).perform(typeText("test"), closeSoftKeyboard());
        onView(withId(R.id.tvDate)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2019, 11, 26));
        onView(withText("OK")).perform(click());
        onView(withId(R.id.tvShowLength)).perform(click());
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName()))).perform(PickerActions.setTime(1, 30));
        onView(withText("OK")).perform(click());
        onView(withId(R.id.add_people_to_event)).perform(click());
        pressBack();
        onView(withId(R.id.make_event)).perform(click());
        //onView(withText("")).inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
        //while (true){}
//        onView(withId(R.id.tvShowLength)).perform(typeText("01:30"));
//        onView(withId(R.id.make_event)).perform(click());
    }
//
//    @Test
//    public void checkIfButtonsDisplayed() {
//        onView(withId(R.id.view_profile_bttn)).check(matches(isDisplayed()));
//        onView(withId(R.id.Map_bttn)).check(matches(isDisplayed()));
//        onView(withId(R.id.go_to_chatroom_bttn)).check(matches(isDisplayed()));
//        onView(withId(R.id.create_event_bttn)).check(matches(isDisplayed()));
//    }
}
