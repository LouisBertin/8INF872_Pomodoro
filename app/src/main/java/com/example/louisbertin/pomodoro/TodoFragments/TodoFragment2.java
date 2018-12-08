package com.example.louisbertin.pomodoro.TodoFragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.louisbertin.pomodoro.R;
import com.example.louisbertin.pomodoro.entity.Project;
import com.example.louisbertin.pomodoro.repository.ProjectRepository;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TodoFragment2.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class TodoFragment2 extends Fragment {
    private Context mContext;
    private EditText editName;
    private ProjectRepository projectRepository = new ProjectRepository();

    private OnFragmentInteractionListener mListener;

    public TodoFragment2() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_todo_fragment2, container, false);
        addProject(v);

        // initialise Views
        editName = (EditText) v.findViewById(R.id.project_name);

        return v;
    }

    // Initialise it from onAttach()
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void addProject(View v) {
        Button addProjectButton = (Button) v.findViewById(R.id.add_project);

        addProjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // fetch view data
                String name = editName.getText().toString();

                // insert project
                Project project = new Project(name);
                projectRepository.writeNewProject(project);
                editName.setText("");
                Toast.makeText(mContext, "Projet ajouté!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
