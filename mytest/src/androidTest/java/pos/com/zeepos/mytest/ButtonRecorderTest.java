package pos.com.zeepos.mytest;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.alfredbase.BaseActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Arif S. on 6/24/19
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ButtonRecorderTest {
    @Rule
    public ActivityTestRule<BaseActivity> mNotesActivityTestRule =
            new ActivityTestRule<>(BaseActivity.class);

    @Test
    public void clickAddNoteButton_opensAddNoteUi() throws Exception {
        onView(withId(com.alfredbase.R.id.textContainer)).perform(click());
    }
}
