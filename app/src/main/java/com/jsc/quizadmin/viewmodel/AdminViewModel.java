package com.jsc.quizadmin.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jsc.quizadmin.model.QuestionDetailsModel;
import com.jsc.quizadmin.model.QuestionModel;
import com.jsc.quizadmin.repository.AdminRepository;

public class AdminViewModel extends ViewModel implements AdminRepository.OnFirestoreAddQuestionDetails {
    private MutableLiveData<String> feedBackLiveData = new MutableLiveData<>();

    private AdminRepository repository = new AdminRepository(this);


    public LiveData<String> addQuestionDetails(QuestionDetailsModel questionDetailsModel, String teacherId) {
        repository.addQuestionDetails(questionDetailsModel, teacherId);
        return feedBackLiveData;
    }

    public LiveData<String> addQuestions(QuestionModel questionModel, String teacherId) {
        repository.addQuestions(questionModel, teacherId);
        return feedBackLiveData;
    }

    @Override
    public void feedBack(String feedback) {
        feedBackLiveData.setValue(feedback);
    }
}
