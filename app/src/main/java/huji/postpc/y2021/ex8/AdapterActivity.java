package huji.postpc.y2021.ex8;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkManager;

import java.util.UUID;

public class AdapterActivity extends RecyclerView.Adapter<AdapterActivity.ViewHolder> {
    CalculatorApplication calculatorApplication;
    public CalculatorHolder calculatorHolder;
    public WorkManager workManager;

    public AdapterActivity(CalculatorHolder calculatorHolder, WorkManager instance, CalculatorApplication calculatorApplication) {
        this.calculatorApplication = calculatorApplication;
        this.calculatorHolder = calculatorHolder;
        this.workManager = instance;
    }

    @NonNull
    @Override
    public AdapterActivity.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater from = LayoutInflater.from(parent.getContext());
        View inflate = from.inflate(R.layout.activity_calc_view, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CalculatorClass calculator = this.calculatorHolder.calculators_arr.get(holder.getLayoutPosition());
        String string_from_calc = holder.create_string_from_calc(calculator);
        holder.view_chosen_number.setText(string_from_calc);
        holder.delete_number_button.setOnClickListener(v ->
        {
            if (calculator.calculator_status == calculator_status.in_progress) {
                UUID id = UUID.fromString(calculator.calc_work_id_str);
                workManager.cancelWorkById(id);
            }
            this.calculatorHolder.delete_calculation(calculator);
            calculatorApplication.save_calculation(this.calculatorHolder.calculators_arr);
            notifyItemRangeRemoved(holder.getLayoutPosition(), 1);
        });
        if (calculator.calculator_status == calculator_status.in_progress) {
            holder.number_progress_bar.setProgress(calculator.progress_percent);
            holder.view_chosen_number.setText(holder.create_string_from_calc(calculator));
        } else {
            holder.number_progress_bar.setVisibility(View.INVISIBLE);
        }

    }


    @Override
    public int getItemCount() {
        return calculatorHolder.calculators_arr.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView view_chosen_number;
        Button delete_number_button;
        public ProgressBar number_progress_bar;
        ConstraintLayout perform_calc_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.perform_calc_layout = itemView.findViewById(R.id.perform_calc_layout);
            this.view_chosen_number = itemView.findViewById(R.id.view_chosen_number_calc_activity);
            this.delete_number_button = itemView.findViewById(R.id.delete_number_button);
            this.number_progress_bar = itemView.findViewById(R.id.number_progress_bar);

        }
        public void set_calculator_view(CalculatorClass calculator) {
            view_chosen_number.setText(create_string_from_calc(calculator));
            number_progress_bar.setVisibility(View.GONE);
        }

        public String create_string_from_calc(CalculatorClass calculator) {
            String calc_result_string;
            switch (calculator.calculator_status) {
                case in_progress:
                    calc_result_string = "performing calculation to find roots for the given number " + calculator.given_num_to_find_root_for + " is currently :" + calculator.progress_percent + "% of his calculation progress";
                    break;
                case finished_calculating_found_roots:
                    calc_result_string = "the found roots are: " + calculator.given_num_to_find_root_for + ": " + calculator.first_num_root + "x" + calculator.sec_num_root;
                    break;
                case finished_calculating_found_prime:
                    calc_result_string = "the given number  " + calculator.given_num_to_find_root_for + ": is prime";
                    break;
                default:
                    calc_result_string = "error occurred while trying to perform calculation";
            }
            return calc_result_string;
        }



    }
}
