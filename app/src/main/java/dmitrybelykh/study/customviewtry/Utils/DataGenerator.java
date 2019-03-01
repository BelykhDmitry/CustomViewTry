package dmitrybelykh.study.customviewtry.Utils;

import android.util.Pair;

import java.util.ArrayList;

public class DataGenerator {
    public ArrayList<Pair<Float, Float>> generateData() {
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

    public ArrayList<Pair<Float, Float>> generateSinus() {
        ArrayList<Pair<Float, Float>> data = new ArrayList<>();
        for (int i = 0; i < 360; i++) {
            float x = (float) ((double) i * 10. * (Math.PI) / 180.);
            data.add(Pair.create(x, (float) Math.sin(x)));
        }
        return data;
    }

    public ArrayList<Pair<Float, Float>> generateCosinus() {
        ArrayList<Pair<Float, Float>> data = new ArrayList<>();
        for (int i = 0; i < 360; i++) {
            float x = (float) ((double) i * 10. * (Math.PI) / 180.);
            data.add(Pair.create(x, (float) Math.cos(x)));
        }
        return data;
    }

    public ArrayList<Pair<Float, Float>> generateGiperbola() {
        ArrayList<Pair<Float, Float>> data = new ArrayList<>();
        for (int i = 1; i < 101; i++) {
            float x = i / 10f;
            data.add(Pair.create(x, 1f / x));
        }
        return data;
    }
}
