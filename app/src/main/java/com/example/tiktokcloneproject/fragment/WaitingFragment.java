package com.example.tiktokcloneproject.fragment;

import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.tiktokcloneproject.R;

public class WaitingFragment extends Fragment {

    Context context = null; String message = "";
    LinearLayout myLayout;

    public static WaitingFragment newInstance(String strArg) {

        WaitingFragment fragment = new WaitingFragment();
        Bundle args = new Bundle();
        args.putString("strArg1", strArg);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = getActivity(); // use this reference to invoke main callbacks

        }
        catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myLayout = (LinearLayout) inflater.inflate(R.layout.fragment_waiting, container, false);

        // add click listeners to the buttons in the fragment

        return myLayout;
    }

}