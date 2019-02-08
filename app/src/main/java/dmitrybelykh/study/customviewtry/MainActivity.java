package dmitrybelykh.study.customviewtry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.RadioGroup;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private LinearGraph graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        graph = findViewById(R.id.graph);
    }

    @Override
    protected void onResume() {
        super.onResume();
        graph.setData(generateData());
    }

    private ArrayList<Pair<Float, Float>> generateData() {
        ArrayList<Pair<Float, Float>> list = new ArrayList<>();
        float value = 5f;
        for (int i = 0; i < 10; i++) {
            value *= -1;
            list.add(Pair.create(new Float(i), value));
        }
        return list;
    }

    private ArrayList<Pair<Float, Float>> generateSinus() {
        ArrayList<Pair<Float, Float>> data = new ArrayList<>();
        for (int i = 0; i < 360; i++) {
            float x = (float)((double)i * 10. *  (Math.PI) / 180.);
            data.add(Pair.create(x, (float)Math.sin(x)));
        }
        return data;
    }

    private ArrayList<Pair<Float, Float>> generateCosinus() {
        ArrayList<Pair<Float, Float>> data = new ArrayList<>();
        for (int i = 0; i < 360; i++) {
            float x = (float)((double)i * 10. *  (Math.PI) / 180.);
            data.add(Pair.create(x, (float)Math.cos(x)));
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

    public void makeOtherGraph(View view) {
        final ArrayList<Pair<Float, Float>> data;
        RadioGroup rg = findViewById(R.id.radio_group);
        switch (rg.getCheckedRadioButtonId()) {
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
                data = generateSinus();
                break;
        }
        graph.animate().cancel();
        graph.animate().setDuration(500)
                .alpha(0f)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        graph.setData(data);
                        graph.animate().setDuration(1000)
                                .alpha(1f);
                    }
                });
    }
}
