package huji.postpc.y2021.ex8;

import android.app.Activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class CalculatorHolder extends Activity {
    ArrayList<CalculatorClass> calculators_arr;
    public CalculatorHolder()
    {
        calculators_arr = new ArrayList<>();
    }


    public boolean not_first_calc(long cur_num_to_calc_root_for) {
        for (CalculatorClass calc : calculators_arr) {
            if (calc.cur_num_to_calc_root_for == cur_num_to_calc_root_for) {
                return true;
            }
        }
        return false;
    }

    public void put_new_calculation(CalculatorClass calculator) {
        calculators_arr.add(calculator);
        Collections.sort(calculators_arr);
    }

    public CalculatorClass find_calculator_from_id(int calc_id){
        for (CalculatorClass calculator : calculators_arr) {
            if (calculator.calc_id == calc_id) {
                return calculator;
            }
        }
        return null;
    }

    public void delete_calculation(CalculatorClass calculator)
    {
        calculators_arr.remove(calculator);
        Collections.sort(calculators_arr);

    }

    public void calculate_ended (CalculatorClass calculator,calculator_status calculator_status){
        calculator.calculator_status = calculator_status;
        calculator.progress_percent = 100;
        Collections.sort(calculators_arr);
    }


}
