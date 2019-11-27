package com.example.jarvis;

import android.util.Log;
import android.view.View;

import androidx.test.espresso.PerformException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
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
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class MapActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);


    @Test
    public void tooEarlyMapTest() {
        onView(withId(R.id.sign_in_button)).perform(click());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Log.e("Error", "InterruptedException", e);
        }

        onView(isRoot()).perform(waitId(R.id.create_event_bttn, 5000));
        onView(withText("CHAT")).perform(click());
        onView(withText("TooEarlyMap")).perform(click());
        onView(withId(R.id.btnMap)).perform(click());

        onView(withId(R.id.refresh)).perform(click());

        onView(withText("Too Early to get other people's locations!"))
                .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));

    }

    @Test
    public void NormalMapTest() {
        onView(withId(R.id.sign_in_button)).perform(click());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Log.e("Error", "InterruptedException", e);
        }

        onView(isRoot()).perform(waitId(R.id.create_event_bttn, 5000));
        onView(withText("CHAT")).perform(click());
        onView(withText("NormalMapTest2")).perform(click());
        onView(withId(R.id.btnMap)).perform(click());

        onView(withId(R.id.refresh)).perform(click());


    }

    @Test
    public void LocationPermissionTest() {
        onView(withId(R.id.sign_in_button)).perform(click());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Log.e("Error", "InterruptedException", e);
        }

        onView(isRoot()).perform(waitId(R.id.create_event_bttn, 5000));
        onView(withText("CHAT")).perform(click());
        onView(withText("LocationPermissonTest")).perform(click());
        onView(withId(R.id.btnMap)).perform(click());

        onView(withId(R.id.refresh)).perform(click());

        onView(withText("Some users have not shared their locations"))
                .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .check(matches((isDisplayed())));

    }

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