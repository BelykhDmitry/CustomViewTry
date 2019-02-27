package dmitrybelykh.study.customviewtry;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class MainActivity extends AppCompatActivity {

    private LinearGraph graph;
    private RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        graph = findViewById(R.id.graph);
        radioGroup = findViewById(R.id.radio_group);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                makeOtherGraph(checkedId);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private ArrayList<Pair<Float, Float>> generateData() {
        ArrayList<Pair<Float, Float>> list = new ArrayList<>();
        list.add(Pair.create(1f, 1f));
        list.add(Pair.create(3f, 4f));
        list.add(Pair.create(5f, 5f));
        list.add(Pair.create(7f, 4f));
        list.add(Pair.create(10f, 7f));
        list.add(Pair.create(12f, 1f));
        list.add(Pair.create(14f, 5f));
        list.add(Pair.create(15f, 4f));
        return list;
    }

    private ArrayList<Pair<Float, Float>> generateSinus() {
        ArrayList<Pair<Float, Float>> data = new ArrayList<>();
        for (int i = 0; i < 360; i++) {
            float x = (float) ((double) i * 10. * (Math.PI) / 180.);
            data.add(Pair.create(x, (float) Math.sin(x)));
        }
        return data;
    }

    private ArrayList<Pair<Float, Float>> generateCosinus() {
        ArrayList<Pair<Float, Float>> data = new ArrayList<>();
        for (int i = 0; i < 360; i++) {
            float x = (float) ((double) i * 10. * (Math.PI) / 180.);
            data.add(Pair.create(x, (float) Math.cos(x)));
        }
        return data;
    }

    private ArrayList<Pair<Float, Float>> generateGiperbola() {
        ArrayList<Pair<Float, Float>> data = new ArrayList<>();
        for (int i = 1; i < 101; i++) {
            float x = i / 10f;
            data.add(Pair.create(x, 1f / x));
        }
        return data;
    }

    public void makeOtherGraph(int checkedId) {
        final ArrayList<Pair<Float, Float>> data;
        switch (checkedId) {
            case R.id.radio_sinus:
                data = generateSinus();
                break;
            case R.id.radio_cosinus:
                data = generateCosinus();
                break;
            case R.id.radio_giperbola:
                data = generateGiperbola();
                break;
            default:
                data = generateData();
                break;
        }
        graph.setData(data);
    }

    public void changeColors(View view) {
        if (((Switch) view).isChecked()) {
            findViewById(R.id.card_view).setBackground(null);
            graph.setGraphColor(getResources().getColor(R.color.colorPrimaryDark));
            findViewById(R.id.card_view).invalidate();
        } else {
            findViewById(R.id.card_view).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            graph.setGraphColor(getResources().getColor(R.color.graphColor));
        }
    }

    public void interpolatorSwitch(View view) {
        if (((Switch) view).isChecked()) {
            graph.setInterpolationOn(true);
        } else {
            graph.setInterpolationOn(false);
        }
    }
}
