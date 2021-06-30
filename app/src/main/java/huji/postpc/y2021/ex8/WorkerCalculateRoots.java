package huji.postpc.y2021.ex8;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.common.util.concurrent.ListenableFuture;

public class WorkerCalculateRoots extends Worker {
    Data.Builder builder;
    int current_progress = 0;
    static String progress = "progress";
    public WorkerCalculateRoots(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        Data.Builder builder = new Data.Builder().putInt(progress, 0);
        setProgressAsync(builder.build());
    }

    @NonNull
    @Override
    public Result doWork() {
        builder = new Data.Builder();
        long init_time_mil_sec = System.currentTimeMillis();
        long given_num_to_find_root_for = getInputData().getLong("given_num_to_find_root_for", 0);
        int calc_id = getInputData().getInt("calc_id", -1);
        long cur_num_to_calc_root_for = getInputData().getLong("cur_num_to_calc_root_for", 2);
//        while (num < given_num_to_find_root_for / 2)
        for (long num = cur_num_to_calc_root_for; num < given_num_to_find_root_for / 2; num++) {

            if(reached_max_calc_time(init_time_mil_sec, calc_id, given_num_to_find_root_for, cur_num_to_calc_root_for))
            {
                int other_progress = (int) ( (100.0 * num) /  (0.5 + given_num_to_find_root_for));
                if (current_progress != other_progress) {
                    current_progress = other_progress;
                    ListenableFuture<Void> voidListenableFuture = setProgressAsync(new Data.Builder().putInt(progress, current_progress).build());
                }
                builder.putBoolean("resume_calculating", true);
                builder.putInt("progress_percent", current_progress);
                builder.putLong("cur_num_to_calc_root_for", num);
                return Result.failure(builder.build());
            }
            if(given_num_to_find_root_for % num == 0)
            {
                builder.putInt("calc_id", calc_id);
                builder.putLong("first_num_root", num);
                builder.putLong("second_num_root", given_num_to_find_root_for/num);
                builder.putLong("given_num_to_find_root_for", given_num_to_find_root_for);
                builder.putInt("progress_percent", current_progress);
                return Result.success(builder.build());
            }
        }
        builder.putInt("calc_id", calc_id);
        builder.putLong("given_num_to_find_root_for", given_num_to_find_root_for);
        builder.putBoolean("resume_calculating", false);
        return Result.failure(builder.build());

    }

    private boolean reached_max_calc_time(long init_time_mil_sec, int calc_id, long given_num_to_find_root_for, long cur_num_to_calc_root_for) {
        long current_time_mil_sec = System.currentTimeMillis() - init_time_mil_sec;
        if(current_time_mil_sec >= 10000)
        {
            builder.putInt("calc_id", calc_id);
            builder.putLong("given_num_to_find_root_for", given_num_to_find_root_for);
            builder.putLong("cur_num_to_calc_root_for", cur_num_to_calc_root_for);
            builder.putBoolean("resume_calculating", true);
            return true;
        }
        return false;
    }
}
