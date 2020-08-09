package com.jsc.quizadmin.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.jsc.quizadmin.R;

import java.util.ArrayList;

public class ResultTableAdapter extends RecyclerView.Adapter<ResultTableAdapter.TableViewHolder> {
    private static final String TAG = "ResultTableAdapter";

    public ResultTableAdapter(Context context) {
        this.context = context;
    }

    Context context;
    ArrayList<String> questionArrayList;
    ArrayList<String> actualAnswerArrayList;
    ArrayList<String> selectedAnswerArrayList;

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.result_table_item, parent, false);
        return new TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: "+questionArrayList.get(position));
        holder.questionTextView.setText(questionArrayList.get(position));
        holder.selectedTextView.setText(selectedAnswerArrayList.get(position));
        holder.actualTextView.setText(actualAnswerArrayList.get(position));
        if (selectedAnswerArrayList.get(position).equals(actualAnswerArrayList.get(position))){
            holder.indicatorCardView.setCardBackgroundColor(context.getResources().getColor(R.color.public_btn_clr, null));
        }else {
            holder.indicatorCardView.setCardBackgroundColor(context.getResources().getColor(R.color.private_btn_clr, null));
        }
    }

    @Override
    public int getItemCount() {
        return questionArrayList.size();
    }

    public void setAllArrayList(ArrayList<String> questionArrayList,ArrayList<String> actualAnswerArrayList,ArrayList<String>selectedAnswerArrayList) {
        this.questionArrayList = questionArrayList;
        this.actualAnswerArrayList = actualAnswerArrayList;
        this.selectedAnswerArrayList = selectedAnswerArrayList;
    }



    public class TableViewHolder extends RecyclerView.ViewHolder{
        TextView questionTextView;
        TextView actualTextView;
        TextView selectedTextView;
        CardView indicatorCardView;

        public TableViewHolder(@NonNull View itemView) {
            super(itemView);

            questionTextView = itemView.findViewById(R.id.questionItemTextView);
            actualTextView = itemView.findViewById(R.id.actualItemTextView);
            selectedTextView = itemView.findViewById(R.id.selectedItemTextView);
            indicatorCardView = itemView.findViewById(R.id.cardView);
        }
    }
}
