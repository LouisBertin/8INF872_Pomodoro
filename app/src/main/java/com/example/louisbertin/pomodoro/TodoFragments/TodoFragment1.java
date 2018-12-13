package com.example.louisbertin.pomodoro.TodoFragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.louisbertin.pomodoro.R;
import com.example.louisbertin.pomodoro.adapter.ProjectListAdapter;
import com.example.louisbertin.pomodoro.entity.Project;
import com.example.louisbertin.pomodoro.repository.DataListener;
import com.example.louisbertin.pomodoro.repository.UserRepository;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TodoFragment1.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class TodoFragment1 extends Fragment {

    private Context mContext;
    private View v;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private OnFragmentInteractionListener mListener;

    public TodoFragment1() {
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
        v =  inflater.inflate(R.layout.fragment_todo_fragment1, container, false);

        // display projects
        mRecyclerView = (RecyclerView) v.findViewById(R.id.my_recycler_view);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        getProjects();
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
     * Private
     */

    /**
     * display all projects
     */
    private void getProjects() {

        // fetch user projects
        final LinearLayout linearLayout = v.findViewById(R.id.user_projects);
        UserRepository userRepository = new UserRepository();

        userRepository.getProjects(new DataListener() {
            @Override
            public void newDataReceived(ArrayList<Project> recipeList) {
                linearLayout.removeAllViews();

                // user projectListAdapter
                mAdapter = new ProjectListAdapter(recipeList, TodoFragment1.this);
                mRecyclerView.setAdapter(mAdapter);
            }
        });
    }

    // check if fragment is visible
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        // Make sure that we are currently visible
        if (this.isVisible()) {
            // If we are becoming invisible, then...
            if (isVisibleToUser) {
                getProjects();
            }
        }
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
}
