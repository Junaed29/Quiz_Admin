package com.jsc.quizadmin.model;

import com.google.firebase.firestore.DocumentId;

public class StudentResultModel {
    @DocumentId
    String documentId;

    String studentId;
    String studentName;
    String studentBatch;
    String studentDept;
    Long correct;
    Long unanswered;
    Long wrong;

    public StudentResultModel(String studentId, Long correct, Long unanswered, Long wrong) {
        this.studentId = studentId;
        this.correct = correct;
        this.unanswered = unanswered;
        this.wrong = wrong;
    }

    public StudentResultModel(String documentId, String studentId, String studentName, String studentBatch, String studentDept, Long correct, Long unanswered, Long wrong) {
        this.documentId = documentId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentBatch = studentBatch;
        this.studentDept = studentDept;
        this.correct = correct;
        this.unanswered = unanswered;
        this.wrong = wrong;
    }

    public StudentResultModel() {
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentBatch() {
        return studentBatch;
    }

    public void setStudentBatch(String studentBatch) {
        this.studentBatch = studentBatch;
    }

    public String getStudentDept() {
        return studentDept;
    }

    public void setStudentDept(String studentDept) {
        this.studentDept = studentDept;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public Long getCorrect() {
        return correct;
    }

    public void setCorrect(Long correct) {
        this.correct = correct;
    }

    public Long getUnanswered() {
        return unanswered;
    }

    public void setUnanswered(Long unanswered) {
        this.unanswered = unanswered;
    }

    public Long getWrong() {
        return wrong;
    }

    public void setWrong(Long wrong) {
        this.wrong = wrong;
    }

    @Override
    public String toString() {
        return "StudentResultModel{" +
                "documentId='" + documentId + '\'' +
                ", studentId='" + studentId + '\'' +
                ", studentName='" + studentName + '\'' +
                ", studentBatch='" + studentBatch + '\'' +
                ", studentDept='" + studentDept + '\'' +
                ", correct=" + correct +
                ", unanswered=" + unanswered +
                ", wrong=" + wrong +
                '}';
    }

    public String getTotalQuestion(){
        return (correct+wrong+unanswered)+"";
    }
}
