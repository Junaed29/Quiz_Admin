package com.jsc.quizadmin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.jsc.quizadmin.R;
import com.jsc.quizadmin.model.QuestionDetailsModel;

public class AllCategoryAdapter extends FirestoreRecyclerAdapter<QuestionDetailsModel, AllCategoryAdapter.QuestionDetailsViewHolder> {

    private Context context;
    private CategoryListener categoryListener;
    public AllCategoryAdapter(@NonNull FirestoreRecyclerOptions<QuestionDetailsModel> options,Context context, CategoryListener categoryListener) {
        super(options);
        this.context = context.getApplicationContext();
        this.categoryListener = categoryListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull QuestionDetailsViewHolder holder, int position, @NonNull QuestionDetailsModel model) {
        holder.quizTitleTextView.setText(model.getQuestionTitle());
        holder.batchTextView.setText(model.getBatch());
        holder.courseIdTextView.setText(model.getCourseId());
        holder.deptTextView.setText(model.getDept());
        String type = "Quiz Type : Exercise";

        if (model.getQuizType()==null) {
            type = "Quiz Type : Exercise";
        }else if (model.getQuizType().contains("test")){
            type = "Quiz Type : Test";
        }
        holder.quizTypeTextView.setText(type);


        holder.visibilityButton.setText(model.getVisibility());
        if (model.getVisibility().equals("private")){
            holder.visibilityButton.setBackground(context.getResources().getDrawable(R.drawable.private_btn_bg, null));
            holder.visibilityButton.setTextColor(context.getResources().getColor(R.color.private_btn_clr,null));
        }else {
            holder.visibilityButton.setBackground(context.getResources().getDrawable(R.drawable.public_btn_bg, null));
            holder.visibilityButton.setTextColor(context.getResources().getColor(R.color.public_btn_clr,null));
        }
    }

    @NonNull
    @Override
    public QuestionDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.single_list_item, parent, false);
        return new QuestionDetailsViewHolder(view);
    }

    public class QuestionDetailsViewHolder extends RecyclerView.ViewHolder {
        TextView quizTitleTextView;
        TextView courseIdTextView;
        TextView deptTextView;
        TextView batchTextView;
        TextView quizTypeTextView;

        Button visibilityButton;

        ImageButton categoryEditImageButton;
        ImageButton categoryDeleteImageButton;


        public QuestionDetailsViewHolder(@NonNull View itemView) {
            super(itemView);

            quizTitleTextView = itemView.findViewById(R.id.catagory_title);
            courseIdTextView = itemView.findViewById(R.id.catagory_courseId);
            deptTextView = itemView.findViewById(R.id.catagory_dept);
            batchTextView = itemView.findViewById(R.id.catagory_batch);
            quizTypeTextView = itemView.findViewById(R.id.quizTipeTextView);

            visibilityButton  = itemView.findViewById(R.id.catagory_visibility);

            categoryDeleteImageButton  = itemView.findViewById(R.id.catagory_delete);
            categoryEditImageButton = itemView.findViewById(R.id.catagory_edit);

            categoryEditImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    categoryListener.categoryEditClicked(getSnapshots().getSnapshot(getAdapterPosition()));
                }
            });

            categoryDeleteImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    categoryListener.categoryDeleteClicked(getSnapshots().getSnapshot(getAdapterPosition()));
                }
            });

            visibilityButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    categoryListener.categoryVisibilityClicked(getSnapshots().getSnapshot(getAdapterPosition()));
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    categoryListener.categoryItemClicked(getSnapshots().getSnapshot(getAdapterPosition()));
                }
            });
        }
    }

    public interface CategoryListener{
        void categoryItemClicked(DocumentSnapshot snapshot);
        void categoryDeleteClicked(DocumentSnapshot snapshot);
        void categoryEditClicked(DocumentSnapshot snapshot);
        void categoryVisibilityClicked(DocumentSnapshot snapshot);
    }
}
