package com.jsc.quizadmin.repository;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jsc.quizadmin.model.QuestionDetailsModel;
import com.jsc.quizadmin.model.QuestionModel;

public class AdminRepository {
    private FirebaseFirestore db;
    private OnFirestoreAddQuestionDetails addQuestionDetails;

    public AdminRepository(OnFirestoreAddQuestionDetails addQuestionDetails) {
        db = FirebaseFirestore.getInstance();
        this.addQuestionDetails = addQuestionDetails;
    }

    public void addQuestionDetails(QuestionDetailsModel questionDetailsModel, String teacherId) {
        db.collection("AllQuestions").document(teacherId)
                .set(questionDetailsModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    addQuestionDetails.feedBack("Successfully Created");
                } else {
                    addQuestionDetails.feedBack(task.getException().getMessage());
                }
            }
        });
    }

    public void addQuestions(QuestionModel questionModel, String teacherId) {
        db.collection("AllQuestions").document(teacherId)
                .set(questionModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    addQuestionDetails.feedBack("Successfully Created");
                } else {
                    addQuestionDetails.feedBack(task.getException().getMessage());
                }
            }
        });
    }

    public interface OnFirestoreAddQuestionDetails {
        public void feedBack(String feedback);
    }
}
