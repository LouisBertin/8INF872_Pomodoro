package com.example.louisbertin.pomodoro.TodoFragments;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.louisbertin.pomodoro.R;
import com.example.louisbertin.pomodoro.repository.TodoRepository;

import java.util.ArrayList;

public class AddTodosActivity extends AppCompatActivity {
    private Context mContext;
    private LinearLayout buttonLayout;
    private String projectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todos);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            projectId = extras.getString("projectId");
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonLayout = (LinearLayout) findViewById(R.id.todos_add_container);
                // add edittext
                EditText et = new EditText(view.getContext());
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                et.setLayoutParams(p);
                et.setHint("Ajoutez du texte");
                et.setTextColor(getResources().getColor(R.color.backgroundMain));
                buttonLayout.addView(et);
            }
        });
    }

    public void getFormData(View view) {
        final int childNb = buttonLayout.getChildCount();
        ArrayList<String> todos = new ArrayList<>();

        for (int i = 0; i < childNb; i++) {
            EditText v = (EditText) buttonLayout.getChildAt(i);
            todos.add(v.getText().toString());
        }

        TodoRepository todoRepository = new TodoRepository();
        todoRepository.writeTodos(projectId, todos);
    }
}
