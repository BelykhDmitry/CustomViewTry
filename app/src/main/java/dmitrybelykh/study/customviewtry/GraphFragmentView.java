package dmitrybelykh.study.customviewtry;

import android.util.Pair;

import java.util.ArrayList;

interface GraphFragmentView {
    void setColor(int color);

    void setData(ArrayList<Pair<Float, Float>> data);

    void setInterpolation(boolean on);
}
