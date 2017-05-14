package com.news.skynet.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.chyrta.onboarder.OnboarderActivity;
import com.chyrta.onboarder.OnboarderPage;
import com.news.skynet.R;

import java.util.ArrayList;
import java.util.List;

/**
 *   Splash.java
 *
 *   This is a simple tutorial activity that is created as an add on.
 *
 *   This activity is launched when the user clicks the about us button in the
 *
 *   navigation drawer menu.
 *
 */

public class Splash extends OnboarderActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // individula screen and the information
        OnboarderPage onboarderPage1 = new OnboarderPage("SkyNet News Reader", "Read News as simple list feeds", R.drawable.first);
        OnboarderPage onboarderPage2 = new OnboarderPage("Neat and Clean", "Read all the news on your fly", R.drawable.second);
        OnboarderPage onboarderPage3 = new OnboarderPage("Launch", "Say Hello!!!", R.drawable.third);

        onboarderPage1.setBackgroundColor(R.color.onboarder_bg_1);
        onboarderPage2.setBackgroundColor(R.color.onboarder_bg_2);
        onboarderPage3.setBackgroundColor(R.color.onboarder_bg_3);

        List<OnboarderPage> pages = new ArrayList<>();

        pages.add(onboarderPage1);
        pages.add(onboarderPage2);
        pages.add(onboarderPage3);

        for (OnboarderPage page : pages) {
            page.setTitleColor(R.color.primary_text);
            page.setDescriptionColor(R.color.secondary_text);
        }

        setOnboardPagesReady(pages);

    }


    // action to be performed when the skip button is pressed
    @Override
    public void onSkipButtonPressed() {

    }

    // action to be performed when the finish button is pressed
    @Override
    public void onFinishButtonPressed() {
        finish();
    }

}
