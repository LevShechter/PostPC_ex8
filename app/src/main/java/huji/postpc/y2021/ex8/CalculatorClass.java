package huji.postpc.y2021.ex8;

import java.io.Serializable;
import java.util.Random;


enum calculator_status {
    in_progress,
    finished_calculating_found_roots,
    finished_calculating_found_prime
}

public class CalculatorClass  implements Comparable<CalculatorClass>, Serializable{
    public int calc_id;
    public long first_num_root;
    public long sec_num_root;
    public calculator_status calculator_status;
    public int progress_percent;
    public String calc_work_id_str;
    long given_num_to_find_root_for;
    long cur_num_to_calc_root_for;

    public CalculatorClass(long given_num_to_find_root_for)
    {
        this.calc_id = new Random().nextInt(999999);
        this.given_num_to_find_root_for = given_num_to_find_root_for;
        this.cur_num_to_calc_root_for = 2;
        this.first_num_root = 0;
        this.sec_num_root = 0;
        this.progress_percent = 0;
        this.calculator_status = huji.postpc.y2021.ex8.calculator_status.in_progress;
        this.calc_work_id_str = "";

    }

    @Override
    public int compareTo(CalculatorClass calculatorClass) {
        huji.postpc.y2021.ex8.calculator_status in_progress = huji.postpc.y2021.ex8.calculator_status.in_progress;
        if (this.calculator_status != in_progress && calculatorClass.calculator_status == in_progress) {
            return 1;
        }
        if (this.calculator_status == in_progress && calculatorClass.calculator_status != in_progress)
        {
            return -1;
        }
        boolean b = this.given_num_to_find_root_for > calculatorClass.given_num_to_find_root_for;
        if(b)
        {
            return 1;
        }
        else
        {
            return -1;
        }
    }
}
