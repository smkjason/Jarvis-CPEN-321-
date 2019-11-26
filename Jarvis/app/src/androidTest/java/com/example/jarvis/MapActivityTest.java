package com.example.jarvis;

import android.util.Log;
import android.view.View;

import androidx.test.espresso.PerformException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.util.HumanReadables;
import androidx.test.espresso.util.TreeIterables;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeoutException;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class MapActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);


    @Test
    public void gotoMap() {
        onView(withId(R.id.sign_in_button)).perform(click());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Log.e("Error", "InterruptedException", e);
        }

        onView(isRoot()).perform(waitId(R.id.create_event_bttn, 5000));
//        onView(withId(R.id.events_recyclerview))
//             .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
//        onView(withId(R.id.tvDate)).perform(click());
//        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2019, 11, 26));
//        onView(withText("OK")).perform(click());
//        onView(withId(R.id.tvShowLength)).perform(click());
//        onView(withClassName(Matchers.equalTo(TimePicker.class.getName()))).perform(PickerActions.setTime(1, 30));
//        onView(withText("OK")).perform(click());
//        onView(withId(R.id.add_people_to_event)).perform(click());
//        onView(withId(R.id.search_recyclerView))
//                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
//        pressBack();
//        //check textbox??
//        //onView(withId(R.id.make_event)).perform(click());
//
//        onView(withId(R.id.invitations_bttn)).perform(click()); //get to invite dialog
//        onView(withText("Espresso Test")).check(matches(isDisplayed()));
//        onView(withId(R.id.rvInvites)).perform(
//                RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.Accept_pending_bttn)));
//        onView(withId(R.id.add_pt_bttn)).perform(click());
//
//        onView(withId(R.id.choosedate_pop_bttn_st)).perform(click());//select time
//        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2019, 11, 26));
//        onView(withText("OK")).perform(click());
//        onView(withId(R.id.choosetime_pop_bttn_st)).perform(click());
//        onView(withClassName(Matchers.equalTo(TimePicker.class.getName()))).perform(PickerActions.setTime(1, 30));
//        onView(withText("OK")).perform(click());
//        onView(withId(R.id.enddate_bttn)).perform(click());
//        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2019, 11, 26));
//        onView(withText("OK")).perform(click());
//        onView(withId(R.id.endtime_bttn)).perform(click());
//        onView(withClassName(Matchers.equalTo(TimePicker.class.getName()))).perform(PickerActions.setTime(1, 30));
//        onView(withText("OK")).perform(click());
//        onView(withText("OK")).perform(click());
//        onView(withId(R.id.finish_pt_bttn)).perform(click());
//
//        //finalize time
//        onView(withId(R.id.my_events_bttn)).perform(click());
//        onView(withText("Espresso Test")).perform(click());
////        onView(withId(R.id.tentative_recyclerView))
////                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
//        onView(withId(R.id.select_recyclerView))
//             .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        pressBack();

        //Event is created

       // while (true){}




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
    public static ViewAction waitId(final int viewId, final long millis) { //wait fo some object to appear wait for a specific time
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "wait for a specific view with id <" + viewId + "> during " + millis + " millis.";
            }

            @Override
            public void perform(final UiController uiController, final View view) {
                uiController.loopMainThreadUntilIdle();
                final long startTime = System.currentTimeMillis();
                final long endTime = startTime + millis;
                final Matcher<View> viewMatcher = withId(viewId);

                do {
                    for (View child : TreeIterables.breadthFirstViewTraversal(view)) {
                        // found view with required ID
                        if (viewMatcher.matches(child)) {
                            return;
                        }
                    }

                    uiController.loopMainThreadForAtLeast(50);
                }
                while (System.currentTimeMillis() < endTime);

                // timeout happens
                throw new PerformException.Builder()
                        .withActionDescription(this.getDescription())
                        .withViewDescription(HumanReadables.describe(view))
                        .withCause(new TimeoutException())
                        .build();
            }
        };
    }

}