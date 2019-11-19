package com.example.jarvis;



import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
public class SearchFriendsTest {

    @Rule
    public ActivityTestRule<CreateEvent> mActivityRule =
            new ActivityTestRule<>(CreateEvent.class);

    @Test
    public void checkUserSavedInAddedFriends() {
        onView(withId(R.id.add_friends)).perform(click());
        onView(withId(R.id.search_recyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        pressBack();

        onView(withId(R.id.add_people_to_event)).check(matches(withText("jarviscpen321.1@gmail.com")));
    }

}
