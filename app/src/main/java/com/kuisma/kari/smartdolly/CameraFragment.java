package com.kuisma.kari.smartdolly;



import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.robobinding.binder.Binders;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CameraFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class CameraFragment extends Fragment {
    public static CameraFragment newInstance() {
        return new CameraFragment();
    }
    public CameraFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return Binders.inflateAndBind(getActivity(), R.layout.fragment_camera, ((MainActivity) getActivity()).getCameraPresentationModel());
        } else {
            return null;
        }
    }
}
