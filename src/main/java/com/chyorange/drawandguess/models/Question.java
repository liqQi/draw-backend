package com.chyorange.drawandguess.models;

public class Question {
    private String question;
    private String hintType;
    private String hintCount;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getHintType() {
        return hintType;
    }

    public void setHintType(String hintType) {
        this.hintType = hintType;
    }

    public String getHintCount() {
        return hintCount;
    }

    public void setHintCount(String hintCount) {
        this.hintCount = hintCount;
    }
}
