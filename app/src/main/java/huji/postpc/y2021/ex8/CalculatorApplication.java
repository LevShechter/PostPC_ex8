package huji.postpc.y2021.ex8;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.work.impl.model.Preference;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CalculatorApplication extends Application {
    Context context;
    SharedPreferences sp;
    ArrayList<CalculatorClass> calculators_arr;
    public CalculatorApplication(Context context) {
        this.context = context;
        this.sp = PreferenceManager.getDefaultSharedPreferences(context);
        calculators_arr = new ArrayList<>();
        String json_item_str = sp.getString("calculators_arr", "");
        boolean empty_str = json_item_str.equals("");
        if(! empty_str)
        {
            Type type = new TypeToken<ArrayList<CalculatorClass>>(){}.getType();
            calculators_arr = new Gson().fromJson(json_item_str, type);
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
        calculators_arr = new ArrayList<>();
    }

    public void save_calculation(ArrayList<CalculatorClass> calculators_arr) {
        this.calculators_arr = calculators_arr;
        SharedPreferences.Editor calculators_arr1 = sp.edit().putString("calculators_arr", new Gson().toJson(calculators_arr));
        calculators_arr1.apply();

    }
}
