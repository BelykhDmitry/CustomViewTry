package dmitrybelykh.study.customviewtry;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Switch;

import java.lang.ref.WeakReference;

public class GraphSettingsFragment extends Fragment {

    private RadioGroup mRadioGroup;
    private WeakReference<OnGraphSettingsFragmentListener> mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_graph_settings, container, false);

        mRadioGroup = rootView.findViewById(R.id.radio_group);
        mRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            mListener.get().onRadioGroupChange(checkedId);
        });

        Switch switch1 = rootView.findViewById(R.id.switch1);
        switch1.setOnCheckedChangeListener((buttonView, isChecked) -> {changeColors(isChecked);});

        Switch interpolationSwitch = rootView.findViewById(R.id.interpolator_switch);
        interpolationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {interpolatorSwitch(isChecked);});

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnGraphSettingsFragmentListener) {
            mListener = new WeakReference<>((OnGraphSettingsFragmentListener) context);
        }
    }

    @Override
    public void onDetach() {
        mListener.clear();
        mListener = null;
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        int id = mRadioGroup.getCheckedRadioButtonId();
        if (id != -1) {
            mListener.get().onRadioGroupChange(id);
        }
    }

    public void interpolatorSwitch(boolean isChecked) {
        mListener.get().onInterpolationSwitch(isChecked);
    }

    public void changeColors(boolean change) {
        mListener.get().onThemeChangeToLight(change);
    }

    interface OnGraphSettingsFragmentListener {
        void onRadioGroupChange(int checkedId);
        void onInterpolationSwitch(boolean switchOn);
        void onThemeChangeToLight(boolean switchOn);
    }

}
