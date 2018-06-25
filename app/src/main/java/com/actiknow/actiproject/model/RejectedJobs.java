package com.actiknow.actiproject.model;

/**
 * Created by l on 17/01/2018.
 */

public class RejectedJobs {
    int id,job_post,job_hire;
    String job_id,title,snippet,country,skill,status,budget,job_url,rejected_by,total_job_posted,total_spent,total_job_filled,client_member_since,total_hours,client_job_percent;

    public RejectedJobs(int id, String job_id, String title, String budget , String snippet, String country, String skill, String status , int job_post, int job_hire, String job_url, String rejected_by,
                        String total_job_posted,String total_spent,String total_job_filled,String client_member_since,String total_hours,String client_job_percent){
        this.id = id;
        this.job_id = job_id;
        this.title = title;
        this.snippet = snippet;
        this.country = country;
        this.skill = skill;
        this.status = status;
        this.budget = budget;
        this.job_post = job_post;
        this.job_hire = job_hire;
        this.job_url = job_url;
        this.rejected_by = rejected_by;
        this.total_job_posted = total_job_posted;
        this.total_spent = total_spent;
        this.total_job_filled = total_job_filled;
        this.client_member_since = client_member_since;
        this.total_hours = total_hours;
        this.client_job_percent = client_job_percent;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRejected_by() {
        return rejected_by;
    }

    public void setRejected_by(String rejected_by) {
        this.rejected_by = rejected_by;
    }

    public String getJob_url() {
        return job_url;
    }

    public void setJob_url(String job_url) {
        this.job_url = job_url;
    }

    public String getJob_id() {
        return job_id;
    }

    public void setJob_id(String job_id) {
        this.job_id = job_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public int getJob_post() {
        return job_post;
    }

    public void setJob_post(int job_post) {
        this.job_post = job_post;
    }

    public int getJob_hire() {
        return job_hire;
    }

    public void setJob_hire(int job_hire) {
        this.job_hire = job_hire;
    }

    public String getTotal_job_posted() {
        return total_job_posted;
    }

    public void setTotal_job_posted(String total_job_posted) {
        this.total_job_posted = total_job_posted;
    }

    public String getTotal_spent() {
        return total_spent;
    }

    public void setTotal_spent(String total_spent) {
        this.total_spent = total_spent;
    }

    public String getTotal_job_filled() {
        return total_job_filled;
    }

    public void setTotal_job_filled(String total_job_filled) {
        this.total_job_filled = total_job_filled;
    }

    public String getClient_member_since() {
        return client_member_since;
    }

    public void setClient_member_since(String client_member_since) {
        this.client_member_since = client_member_since;
    }

    public String getTotal_hours() {
        return total_hours;
    }

    public void setTotal_hours(String total_hours) {
        this.total_hours = total_hours;
    }

    public String getClient_job_percent() {
        return client_job_percent;
    }

    public void setClient_job_percent(String client_job_percent) {
        this.client_job_percent = client_job_percent;
    }
}


