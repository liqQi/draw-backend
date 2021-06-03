package com.chyorange.drawandguess.models;

public class DrawingUser {

    private String drawingUserId;
    private String drawingUserNickName;
    private Question question;

    public String getDrawingUserId() {
        return drawingUserId;
    }

    public void setDrawingUserId(String drawingUserId) {
        this.drawingUserId = drawingUserId;
    }

    public String getDrawingUserNickName() {
        return drawingUserNickName;
    }

    public void setDrawingUserNickName(String drawingUserNickName) {
        this.drawingUserNickName = drawingUserNickName;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }
}
