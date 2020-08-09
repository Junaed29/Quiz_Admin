package com.jsc.quizadmin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jsc.quizadmin.R;
import com.jsc.quizadmin.model.StudentResultModel;

import java.util.List;

public class AllResultAdapter extends RecyclerView.Adapter<AllResultAdapter.ResultViewHolder> {

    private List<StudentResultModel> resultModelList;



    ResultListener resultListener;

    public AllResultAdapter(ResultListener resultListener) {
        this.resultListener = resultListener;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.single_result_item, parent, false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        holder.studentId.setText(resultModelList.get(position).getStudentId());
        String correctAnswer = resultModelList.get(position).getCorrect()+"";
        holder.totalCorrect.setText(correctAnswer);
        holder.totalQuestion.setText(resultModelList.get(position).getTotalQuestion());

        Long correct = resultModelList.get(position).getCorrect();
        Long wrong = resultModelList.get(position).getWrong();
        Long unanswered = resultModelList.get(position).getUnanswered();

        long total = correct + wrong + unanswered;

        long percent = (correct * 100) / total;

        String percentage = Long.toString(percent) +"%";
        holder.percentageTextView.setText(percentage);
        holder.percentProgressBar.setProgress((int) percent);
    }

    @Override
    public int getItemCount() {
        return resultModelList.size();
    }

    public void setResultModelList(List<StudentResultModel> resultModelList) {
        this.resultModelList = resultModelList;
    }

    public class ResultViewHolder extends RecyclerView.ViewHolder{
        TextView studentId;
        TextView percentageTextView;
        TextView totalQuestion;
        TextView totalCorrect;
        ProgressBar percentProgressBar;

        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            studentId  = itemView.findViewById(R.id.result_studentId);
            percentageTextView  = itemView.findViewById(R.id.result_percent);
            totalQuestion  = itemView.findViewById(R.id.result_total_question);
            totalCorrect  = itemView.findViewById(R.id.result_correct_answer);
            percentProgressBar  = itemView.findViewById(R.id.result_progressBar);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resultListener.onItemClick(getAdapterPosition());
                }
            });
        }
    }


    public interface ResultListener{
        void onItemClick(int position);
    }
}
