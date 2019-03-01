package dmitrybelykh.study.customviewtry;


import android.accessibilityservice.AccessibilityService;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import java.util.ArrayList;

public class GraphFragment extends Fragment implements GraphFragmentView {

    private LinearGraph graph;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_graph, container, false);
        graph = rootView.findViewById(R.id.graph);

        return rootView;
    }

    @Override
    public void setColor(int color) {
        graph.setGraphColor(color);
    }

    @Override
    public void setData(ArrayList<Pair<Float, Float>> data) {
        graph.setData(data);
    }

    @Override
    public void setInterpolation(boolean on) {
        graph.setInterpolationOn(on);
    }

}
