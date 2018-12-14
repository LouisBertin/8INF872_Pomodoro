package com.example.louisbertin.pomodoro.TodoFragments;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.louisbertin.pomodoro.R;

public class ProjectActivity extends AppCompatActivity {
    private String projectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            projectId = extras.getString("projectId");
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.project_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.project_add_todos:
                // add todos activity
                Intent myIntent = new Intent(this, AddTodosActivity.class);
                myIntent.putExtra("projectId", projectId);
                startActivity(myIntent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
