package com.actiknow.actiproject.utils;

public class AppConfigURL {
    public static String version = "v2";
    public static String BASE_URL = "https://upwork-sudhanshu77492652.c9users.io/api/" + version + "/";
   // public static String BASE_URL = "http://upworktool.actipatient.com/upworktool/api/" + version + "/";
    public static  String Login= BASE_URL+"login";
    public static  String FORGOT_PASSWORD= BASE_URL+"driver/forgot-password";
    public static  String FORGOT_CHANGE_PASSWORD= BASE_URL+"driver/change-password";
    public static  String JOBS= BASE_URL+"jobs";
    public static  String JOBS2= BASE_URL+"jobs2";
    public static String JOBS_IDS= BASE_URL+"job_ids";
    public static String REJECT_JOB= BASE_URL+"reject_job";
    public static String ACCEPT_JOB= BASE_URL+"accept_job";
    public static String ACCEPT_REJECTED_JOB= BASE_URL+"accept_rejected_job";
    public static String REJECTED_JOBS= BASE_URL+"rejected_jobs";
    public static String ACCEPTED_JOBS= BASE_URL+"accepted_jobs";
    public static String ACCEPTED_JOBS2= BASE_URL+"accepted_jobs2";
    public static String REJECTED_JOBS2= BASE_URL+"rejected_jobs2";
    public static String ACCEPTED_JOB_IDS= BASE_URL+"accepted_job_ids";
    public static String REJECTED_JOB_IDS= BASE_URL+"rejected_job_ids";
    public static String REJECT_ACCEPTED_JOB= BASE_URL+"reject_accepted_job";
}