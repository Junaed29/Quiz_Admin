package com.jsc.quizadmin.model;

import com.google.firebase.firestore.DocumentId;

public class QuestionDetailsModel {

    @DocumentId
    String documentId;

    String questionTitle;
    String courseId;
    String batch;
    String dept;
    String visibility;




    public QuestionDetailsModel() {
    }

    public QuestionDetailsModel(String questionTitle, String courseId, String batch, String dept, String visibility) {
        this.questionTitle = questionTitle;
        this.courseId = courseId;
        this.batch = batch;
        this.dept = dept;
        this.visibility = visibility;
    }


    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }
}
