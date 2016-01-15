package com.example.william.myapplication;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;

public class JobSearchActivity extends ActionBarActivity {

    JobSearch jobSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.job_layout);

        ProgressBar loading = (ProgressBar) findViewById(R.id.progressBar4);
        ListView lv = (ListView) findViewById(R.id.job_list_view);
        final JobSearchAdapter jobSearchAdapter = new JobSearchAdapter(this);

        jobSearch = new JobSearch(jobSearchAdapter);
        jobSearch.setLoading( loading );
        jobSearch.search(true);

        lv.setAdapter(jobSearchAdapter);
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            int previousLastPosition = 0;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                final int lastPosition = firstVisibleItem + visibleItemCount;
                if (lastPosition == totalItemCount) {
                    if (previousLastPosition != lastPosition) {
                        jobSearch.setPage(jobSearch.getPage() + 1);
                        jobSearch.search(false);
                    }
                    previousLastPosition = lastPosition;
                } else if (lastPosition < previousLastPosition - 5) {
                    resetLastIndex();
                }
            }

            public void resetLastIndex() {
                previousLastPosition = 0;
            }
        });
    }
}
