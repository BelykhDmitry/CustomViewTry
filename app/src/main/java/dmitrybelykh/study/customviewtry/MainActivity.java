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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import dmitrybelykh.study.customviewtry.Utils.DataGenerator;

public class MainActivity extends AppCompatActivity implements GraphSettingsFragment.OnGraphSettingsFragmentListener {

    private DataGenerator dataGenerator;
    private GraphFragmentView graphFragmentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataGenerator = new DataGenerator();

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        GraphFragment graphFragment;
        GraphSettingsFragment graphSettingsFragment;
        if (savedInstanceState == null) {
            graphFragment = new GraphFragment();
            graphSettingsFragment = new GraphSettingsFragment();
            graphFragmentView = graphFragment;
            ft.add(R.id.graph_container, graphFragment, GraphFragment.class.getName())
                    .add(R.id.settings_container, graphSettingsFragment, GraphSettingsFragment.class.getName())
                    .commit();
        } else {
            graphFragment = (GraphFragment) fm.findFragmentByTag(GraphFragment.class.getName());
            graphSettingsFragment = (GraphSettingsFragment)
                    fm.findFragmentByTag(GraphSettingsFragment.class.getName());
        }
        graphFragmentView = graphFragment;
    }



    public void makeOtherGraph(int checkedId) {
        final ArrayList<Pair<Float, Float>> data;
        switch (checkedId) {
            case R.id.radio_sinus:
                data = dataGenerator.generateSinus();
                break;
            case R.id.radio_cosinus:
                data = dataGenerator.generateCosinus();
                break;
            case R.id.radio_giperbola:
                data = dataGenerator.generateGiperbola();
                break;
            default:
                data = dataGenerator.generateData();
                break;
        }
        graphFragmentView.setData(data);
    }

    public void changeColors(boolean switchOn) {
        if (switchOn) {
            findViewById(R.id.card_view).setBackgroundResource(0);
            graphFragmentView.setColor((getResources().getColor(R.color.colorPrimaryDark)));
            findViewById(R.id.card_view).invalidate();
        } else {
            findViewById(R.id.card_view).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            graphFragmentView.setColor(getResources().getColor(R.color.graphColor));
        }
    }

    @Override
    public void onRadioGroupChange(int checkedId) {
        makeOtherGraph(checkedId);
    }

    @Override
    public void onInterpolationSwitch(boolean switchOn) {
        graphFragmentView.setInterpolation(switchOn);
    }

    @Override
    public void onThemeChangeToLight(boolean switchOn) {
        changeColors(switchOn);
    }

}
