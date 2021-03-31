package com.banglabs.pipslover.datahandler;


public class FeedbackBundle {

    String email, feedback;

    public FeedbackBundle(String email, String feedback) {
        this.email = email;
        this.feedback = feedback;
    }

    public String getEmail() {
        return email;
    }

    public String getFeedback() {
        return feedback;
    }




}
