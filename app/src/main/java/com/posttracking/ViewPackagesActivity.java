package com.posttracking;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ViewPackagesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_packages);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
