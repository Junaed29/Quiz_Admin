package com.jsc.quizadmin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.jsc.quizadmin.R;
import com.jsc.quizadmin.model.QuestionModel;

public class AllQuestionsAdapter extends FirestoreRecyclerAdapter<QuestionModel, AllQuestionsAdapter.QuestionViewHolder> {

    QuestionListener questionListener;

    public AllQuestionsAdapter(@NonNull FirestoreRecyclerOptions<QuestionModel> options, QuestionListener questionListener) {
        super(options);
        this.questionListener = questionListener;
    }


    @Override
    protected void onBindViewHolder(@NonNull QuestionViewHolder holder, int position, @NonNull QuestionModel model) {
        holder.questionTitleTextView.setText(model.getQuestion());
        String answer = "Answer : "+model.getAnswer();
        holder.questionAnswerTextView.setText(answer);
        String timer = model.getTimer()+" Seconds";
        holder.questionTimeTextView.setText(timer);
        //holder.questionOptionTextView.setText(model.getQuestion());
        String option = model.getOption_a();

        if (!model.getOption_b().equals("null")){
            option = option+"\n"+model.getOption_b();
        }

        if (!model.getOption_c().equals("null")){
            option = option+"\n"+model.getOption_c();
        }
        holder.questionOptionTextView.setText(option);
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.single_question_item,parent,false);
        return new QuestionViewHolder(view);
    }

    public class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView    questionTitleTextView ;
        TextView    questionAnswerTextView ;
        TextView    questionOptionTextView ;
        TextView    questionTimeTextView ;
        ImageButton editImageButton;
        ImageButton deleteImageButton;
        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            questionTitleTextView = itemView.findViewById(R.id.question_title);
            questionAnswerTextView = itemView.findViewById(R.id.question_answer);
            questionOptionTextView = itemView.findViewById(R.id.question_other_options);
            questionTimeTextView = itemView.findViewById(R.id.question_time);
            editImageButton = itemView.findViewById(R.id.question_edit);
            deleteImageButton = itemView.findViewById(R.id.question_delete);

            editImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    questionListener.questionEditClicked(getSnapshots().getSnapshot(getAdapterPosition()));
                }
            });

            deleteImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    questionListener.questionDeleteClicked(getSnapshots().getSnapshot(getAdapterPosition()));
                }
            });
        }
    }

    public interface QuestionListener{
        void questionDeleteClicked(DocumentSnapshot snapshot);
        void questionEditClicked(DocumentSnapshot snapshot);
    }
}
