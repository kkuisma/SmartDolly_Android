package com.kuisma.kari.smartdolly;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.robobinding.binder.Binders;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DollyFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class DollyFragment extends Fragment {
    public static DollyFragment newInstance() {
        return new DollyFragment();
    }
    public DollyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(savedInstanceState == null) {
            return Binders.inflateAndBind(getActivity(), R.layout.fragment_dolly, ((MainActivity) getActivity()).getDollyPresentationModel());
        } else {
            return null;
        }
    }
}
