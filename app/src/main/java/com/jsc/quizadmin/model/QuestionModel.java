package com.jsc.quizadmin.model;

import com.google.firebase.firestore.DocumentId;

public class QuestionModel {

    @DocumentId
    private String questionId;

    private String question, answer, option_a, option_b, option_c, option_d;
    private long timer;

    public QuestionModel() {
    }

//    public QuestionModel(String question, String option_a, String option_b, String answer, long timer) {
//        this.question = question;
//        this.option_a = option_a;
//        this.option_b = option_b;
//        this.option_c = "null";
//        this.option_d = "null";
//        this.answer = answer;
//        this.timer = timer;
//    }

    public QuestionModel(String question, String answer, String option_a, String option_b, String option_c, String option_d,  long timer) {
        this.question = question;
        this.option_a = option_a;
        this.option_b = option_b;
        if (option_c.isEmpty()) {
            this.option_c = "null";
        } else {
            this.option_c = option_c;
        }

        if (option_d.isEmpty()) {
            this.option_d = "null";
        } else {
            this.option_d = option_d;
        }
        this.answer = answer;
        this.timer = timer;
    }

//    public QuestionModel(String question, String option_a, String option_b, String option_c, String answer, long timer) {
//        this.question = question;
//        this.option_a = option_a;
//        this.option_b = option_b;
//        this.option_c = option_c;
//        this.option_d = "null";
//        this.answer = answer;
//        this.timer = timer;
//    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOption_a() {
        return option_a;
    }

    public void setOption_a(String option_a) {
        this.option_a = option_a;
    }

    public String getOption_b() {
        return option_b;
    }

    public void setOption_b(String option_b) {
        this.option_b = option_b;
    }

    public String getOption_c() {
        return option_c;
    }

    public void setOption_c(String option_c) {
        this.option_c = option_c;
    }

    public String getOption_d() {
        return option_d;
    }

    public void setOption_d(String option_d) {
        this.option_d = option_d;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public long getTimer() {
        return timer;
    }

    public void setTimer(long timer) {
        this.timer = timer;
    }

    @Override
    public String toString() {
        return "QuestionModel{" +
                "question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                ", option_a='" + option_a + '\'' +
                ", option_b='" + option_b + '\'' +
                ", option_c='" + option_c + '\'' +
                ", option_d='" + option_d + '\'' +
                ", timer=" + timer +
                '}';
    }
}
