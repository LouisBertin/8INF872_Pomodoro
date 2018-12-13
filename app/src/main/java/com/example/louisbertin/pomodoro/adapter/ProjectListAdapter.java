package com.example.louisbertin.pomodoro.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.louisbertin.pomodoro.R;
import com.example.louisbertin.pomodoro.entity.Project;

import java.util.ArrayList;

public class ProjectListAdapter extends RecyclerView.Adapter<ProjectListAdapter.MyViewHolder> {
    private ArrayList mDataset;
    public static Context mContext;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView projectName;
        public Button projectDeleteButton;

        public MyViewHolder(View itemView) {
            super(itemView);

            // get views
            projectName = (TextView) itemView.findViewById(R.id.project_name);
            projectDeleteButton = (Button) itemView.findViewById(R.id.project_delete_button);

            // on delete button click
            projectDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get project id
                    String projectIdToDelete = projectDeleteButton.getTag().toString();

                    Toast.makeText(mContext, projectIdToDelete, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ProjectListAdapter(ArrayList<Project> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ProjectListAdapter.MyViewHolder onCreateViewHolder(final ViewGroup parent,
                                                              int viewType) {
        // fetch context
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.projects_recyclerview, parent, false);

        // Return a new holder instance
        MyViewHolder viewHolder = new MyViewHolder(contactView);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Project project = (Project) mDataset.get(position);
        holder.projectName.setText(project.getTitle());
        holder.projectDeleteButton.setTag(project.getUuid());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
