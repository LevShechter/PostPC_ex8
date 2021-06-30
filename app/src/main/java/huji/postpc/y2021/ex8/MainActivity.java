package huji.postpc.y2021.ex8;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;
import static java.util.jar.Pack200.Unpacker.PROGRESS;

import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.UUID;



//enum calculator_status {
//    in_progress,
//    finished_calculating_found_roots,
//    finished_calculating_found_prime
//}


public class MainActivity extends AppCompatActivity {
    Context context;
    FloatingActionButton do_calculation_button;
    EditText edit_text_put_number;
    RecyclerView recycle_view_roots;
    Data.Builder builder;
    CalculatorApplication calculatorApplication;
    AdapterActivity adapterActivity;
    CalculatorHolder calculatorHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;
        calculatorApplication = new CalculatorApplication(this);
        edit_text_put_number = findViewById(R.id.edit_text_put_number);
        do_calculation_button = findViewById(R.id.do_calculation_button);
        recycle_view_roots = findViewById(R.id.recycle_view_roots);
        calculatorHolder = new CalculatorHolder();
        calculatorHolder.calculators_arr = calculatorApplication.calculators_arr;
        boolean not_null_context = (context != null);

        if (not_null_context)
        {
            this.adapterActivity = new AdapterActivity(calculatorHolder, WorkManager.getInstance(context), calculatorApplication);
        }
        recycle_view_roots.setAdapter(adapterActivity);
        LinearLayoutManager layout = new LinearLayoutManager(this);
        recycle_view_roots.setLayoutManager(layout);
        DividerItemDecoration decor = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recycle_view_roots.addItemDecoration(decor);
        builder = new Data.Builder();
        do_calculation_button.setOnClickListener(v->
        {
            try {
                String s = edit_text_put_number.getText().toString();
                long given_num_to_find_root_for = Long.parseLong(s);
                boolean not_first_calc = calculatorHolder.not_first_calc(given_num_to_find_root_for);
                if(!not_first_calc)
                {
                    CalculatorClass calculator = new CalculatorClass(given_num_to_find_root_for);

                    performCalculation(true, calculator);
                }

            }
            catch (NumberFormatException e)
            {
                Toast toast = Toast.makeText(this, "error", Toast.LENGTH_SHORT);
                toast.show();
            }

        });
        int calc_idx = 0;
        while (calc_idx < calculatorHolder.calculators_arr.size())
        {
            if (calculatorHolder.calculators_arr.get(calc_idx).calculator_status == calculator_status.in_progress)
            {
                performCalculation(false, calculatorHolder.calculators_arr.get(calc_idx));
            }
            calc_idx ++;

        }
    }

    public void performCalculation(boolean first_calc, CalculatorClass calculator)
    {
        if(first_calc)
        {
            calculatorHolder.put_new_calculation(calculator);
            calculatorApplication.save_calculation(calculatorHolder.calculators_arr);
            adapterActivity.notifyItemInserted(calculatorHolder.calculators_arr.indexOf(calculator));
        }
        builder.putInt("calc_id", calculator.calc_id);
        builder.putLong("given_num_to_find_root_for", calculator.given_num_to_find_root_for);
        builder.putLong("cur_num_to_calc_root_for", calculator.cur_num_to_calc_root_for);
        Data build = builder.build();
        OneTimeWorkRequest.Builder builder = new OneTimeWorkRequest.Builder(WorkerCalculateRoots.class);
        OneTimeWorkRequest oneTimeWorkRequest = builder.setInputData(build).build();
        WorkManager.getInstance(this).enqueue(oneTimeWorkRequest);
        UUID id = oneTimeWorkRequest.getId();
        WorkManager instance = WorkManager.getInstance(getApplicationContext());
        LiveData<WorkInfo> workInfoLiveData = instance.getWorkInfoByIdLiveData(id);
        workInfoLiveData.observeForever(workInfo ->
        {
            if (!(workInfo == null))
            {
                WorkInfo.State workInfoState = workInfo.getState();
                if(workInfoState == WorkInfo.State.SUCCEEDED)
                {
                    Data outputData = workInfo.getOutputData();
                    int calc_id = outputData.getInt("calc_id", -1);
                    CalculatorClass calculatorClassInst = calculatorHolder.find_calculator_from_id(calc_id);
                    if(!(calculatorClassInst == null))
                    {
                        calculatorClassInst.first_num_root = outputData.getLong("first_num_root", 0);
                        calculatorClassInst.sec_num_root = outputData.getLong("sec_num_root", 0);
                        calculatorHolder.calculate_ended(calculator, calculator_status.finished_calculating_found_roots);
                        calculatorApplication.save_calculation(calculatorHolder.calculators_arr);
                        adapterActivity.notifyDataSetChanged();
                        int position = calculatorHolder.calculators_arr.indexOf(calculator);
                        RecyclerView.ViewHolder viewHolderForLayoutPosition = recycle_view_roots.findViewHolderForLayoutPosition(position);
                        AdapterActivity.CalculatorViewHolder calculatorViewHolder = (AdapterActivity.CalculatorViewHolder) viewHolderForLayoutPosition;
                        if (!(calculatorViewHolder == null))
                        {
                            calculatorViewHolder.set_calculator_view(calculator);
                        }

                    }
                }
                else if(workInfoState == WorkInfo.State.FAILED)
                {
                    Data calc_output = workInfo.getOutputData();
                    if (calc_failed(calc_output))
                    {
                        long cur_num_to_calc_root_for = calc_output.getLong("cur_num_to_calc_root_for", 2);
                        calculator.cur_num_to_calc_root_for = cur_num_to_calc_root_for;
                        calculator.progress_percent = calc_output.getInt("progress_percent", 0);
                        int progress_percent = calc_output.getInt("progress_percent", 0);
                        apply_changes(workInfo.getId().toString(), progress_percent);
                        calculatorApplication.save_calculation(calculatorHolder.calculators_arr);
                        performCalculation(false, calculator);

                    }
                }
                int progress_percent = workInfo.getOutputData().getInt("progress_percent", 0);
                String s = workInfo.getId().toString();
                apply_changes(s, progress_percent);
            }
        });
    }
    private String create_string_from_calc(CalculatorClass calculator) {
        String calc_result_string;
        switch(calculator.calculator_status) {
            case in_progress:
                calc_result_string="performing calculation to find roots for the given number " + calculator.given_num_to_find_root_for + " is currently :" +calculator.progress_percent + "% of his calculation progress";
                break;
            case finished_calculating_found_roots:
                calc_result_string="the found roots are: " + calculator.given_num_to_find_root_for+ ": " +calculator.first_num_root + "x" + calculator.sec_num_root;
                break;
            case finished_calculating_found_prime:
                calc_result_string="the given number  " +calculator.given_num_to_find_root_for + ": is prime";
                break;
            default:
                calc_result_string = "error occurred while trying to perform calculation";
        }
        return calc_result_string;
    }
    boolean calc_failed(Data calc_output) {
        if(!calc_output.getBoolean("resume_calculating", true))
        {
            int calc_id1 = calc_output.getInt("calc_id", -1);
            CalculatorClass calculator = calculatorHolder.find_calculator_from_id(calc_id1);
            calculator.calculator_status = calculator_status.finished_calculating_found_prime;
            adapterActivity.notifyDataSetChanged();
            RecyclerView.ViewHolder viewHolderForLayoutPosition1 = recycle_view_roots.findViewHolderForLayoutPosition(calculatorHolder.calculators_arr.indexOf(calculator));
            AdapterActivity.CalculatorViewHolder calculatorViewHolder = (AdapterActivity.CalculatorViewHolder) viewHolderForLayoutPosition1;
            if(!(calculatorViewHolder==null))
            {
                calculatorViewHolder.set_calculator_view(calculator);
            }
            calculator_status finished_calculating_found_prime = calculator_status.finished_calculating_found_prime;
            calculatorHolder.calculate_ended(calculator, finished_calculating_found_prime);
            calculatorApplication.save_calculation(calculatorHolder.calculators_arr);
            return false;
        }
        return true;

    }

    void apply_changes(String s, int progress_percent) {
        int indx_calc = 0;
        while (indx_calc < calculatorHolder.calculators_arr.size())
        {
            CalculatorClass calculator = calculatorHolder.calculators_arr.get(indx_calc);
            if(calculator.calc_work_id_str.equals(s))
            {
                calculator.progress_percent = progress_percent;
                RecyclerView.ViewHolder viewHolderForLayoutPosition = recycle_view_roots.findViewHolderForLayoutPosition(indx_calc);
                AdapterActivity.CalculatorViewHolder calculatorViewHolder = (AdapterActivity.CalculatorViewHolder) viewHolderForLayoutPosition;
                if(!(calculatorViewHolder == null))
                {
                    if(progress_percent >= 99)
                    {
                        calculatorViewHolder.number_progress_bar.setProgress(100);
                    }
                    else if(progress_percent <= 1)
                    {
                        calculatorViewHolder.number_progress_bar.setProgress(0);

                    }
                    calculatorViewHolder.number_progress_bar.setProgress(progress_percent);
                    calculatorViewHolder.view_chosen_number.setText(create_string_from_calc(calculator));


                }
            }
            indx_calc ++;
        }

    }




}