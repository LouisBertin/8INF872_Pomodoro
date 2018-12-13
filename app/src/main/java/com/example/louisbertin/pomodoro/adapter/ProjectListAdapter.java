package com.example.louisbertin.pomodoro.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.louisbertin.pomodoro.R;
import com.example.louisbertin.pomodoro.TodoFragments.TodoFragment1;
import com.example.louisbertin.pomodoro.entity.Project;
import com.example.louisbertin.pomodoro.repository.ProjectRepository;

import java.util.ArrayList;

public class ProjectListAdapter extends RecyclerView.Adapter<ProjectListAdapter.MyViewHolder> {
    private ArrayList mDataset;
    public Context mContext;
    public TodoFragment1 mFragment;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyViewHolder extends RecyclerView.ViewHolder {
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

                    deleteModal(projectIdToDelete);
                }
            });
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ProjectListAdapter(ArrayList<Project> myDataset, TodoFragment1 todoFragment1) {
        mDataset = myDataset;
        mFragment = todoFragment1;
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

    /**
     *
     * Private
     *
     */

    private void deleteModal(final String projectId) {
        // display dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Suppression du projet")
                .setMessage("Êtes-vous sûr de vouloir supprimer ce projet ?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ProjectRepository projectRepository = new ProjectRepository();
                        // delete project
                        projectRepository.deleteProjectById(projectId);
                        // redraw fragment
                        FragmentManager manager = ((AppCompatActivity) mContext).getSupportFragmentManager();
                        manager.beginTransaction()
                                .detach(mFragment)
                                .attach(mFragment)
                                .commit();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }
}
