package com.actiknow.actiproject.helper;

/**
 * Created by Actiknow on 19-02-2018.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.actiknow.actiproject.model.Jobs;
import com.actiknow.actiproject.utils.AppConfigTags;
import com.actiknow.actiproject.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHandler extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "upwork";

    // Table Names
    private static final String TABLE_JOBS = "tbl_jobs";



    // Banners Table - column names
    private static final String ID = "id";
    private static final String JOB_PRIMARY_ID = "job_primary_id";
    private static final String JOB_ID = "job_id";
    private static final String JOB_TITLE = "job_title";
    private static final String JOB_BUDGET = "job_budget";
    private static final String JOB_SNIPPET = "job_snippet";
    private static final String JOB_COUNTRY = "job_country";
    private static final String JOB_SKILL = "job_skill";
    private static final String JOB_PAYMENT_VERIFICATION_STATUS = "job_payment_verification_status";
    private static final String JOB_JOB_POSTED = "job_posted";
    private static final String JOB_JOB_POST_HIRES = "job_post_hires";
    private static final String JOB_URL = "job_url";


    public static final String CLIENT_TOTAL_JOB_POSTED = "client_total_job_posted";
    public static final String CLIENT_TOTAL_SPENT = "client_total_spent";
    public static final String CLIENT_TOTAL_JOB_FILLED = "client_total_job_filled";
    public static final String CLIENT_MEMBER_SINCE = "client_member_since";
    public static final String CLIENT_TOTAL_HOURS = "client_total_hours";
    public static final String CLIENT_JOB_PERCENT = "client_job_percent";



    // Notes table Create Statements
    private static final String CREATE_TABLE_JOBS = "CREATE TABLE "
            + TABLE_JOBS + "(" +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            JOB_PRIMARY_ID + " INTEGER," +
            JOB_ID + " TEXT," +
            JOB_TITLE + " TEXT," +
            JOB_BUDGET + " TEXT," +
            JOB_SNIPPET + " TEXT," +
            JOB_COUNTRY + " TEXT," +
            JOB_SKILL + " TEXT," +
            JOB_PAYMENT_VERIFICATION_STATUS + " TEXT," +
            JOB_JOB_POSTED + " INTEGER," +
            JOB_JOB_POST_HIRES + " INTEGER," +
            JOB_URL + " TEXT,"+
            CLIENT_TOTAL_JOB_POSTED + " TEXT,"+
            CLIENT_TOTAL_SPENT + " TEXT,"+
            CLIENT_TOTAL_JOB_FILLED + " TEXT,"+
            CLIENT_MEMBER_SINCE + " TEXT,"+
            CLIENT_TOTAL_HOURS + " TEXT,"+
            CLIENT_JOB_PERCENT + " TEXT" + ")";


    // Notes table Create Statements

    Context mContext;
    private boolean LOG_FLAG = false;

    public DatabaseHandler (Context context) {
        super (context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate (SQLiteDatabase db) {
        db.execSQL (CREATE_TABLE_JOBS);
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL ("DROP TABLE IF EXISTS " + TABLE_JOBS);
        onCreate (db);
    }


    public void closeDB () {
        SQLiteDatabase db = this.getReadableDatabase ();
        if (db != null && db.isOpen ())
            db.close ();
    }

    private String getDateTime () {
        SimpleDateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss", Locale.getDefault ());
        Date date = new Date ();
        return dateFormat.format (date);
    }

    public void insertAllJobs (List<Jobs> jobList) {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Inserting all banners", LOG_FLAG);
        db.beginTransaction ();
        try {
            ContentValues values = new ContentValues ();
            for (Jobs jobs : jobList) {
                values.put (JOB_PRIMARY_ID, jobs.getId());
                values.put (JOB_ID, jobs.getId());
                values.put (JOB_TITLE, jobs.getTitle());
                values.put (JOB_BUDGET, jobs.getBudget());
                values.put (JOB_SNIPPET, jobs.getSnippet());
                values.put (JOB_COUNTRY, jobs.getCountry());
                values.put (JOB_SKILL, jobs.getSkill());
                values.put(JOB_PAYMENT_VERIFICATION_STATUS, jobs.getStatus());
                values.put (JOB_JOB_POSTED, jobs.getJob_post());
                values.put (JOB_JOB_POST_HIRES, jobs.getJob_hire());
                values.put (JOB_URL, jobs.getJob_url());
                db.insert (TABLE_JOBS, null, values);
            }
            db.setTransactionSuccessful ();
        } finally {
            db.endTransaction ();
        }
    }

    public ArrayList<Jobs> getAllJobs () {
        ArrayList<Jobs> jobList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase ();
        String selectQuery = "SELECT  * FROM " + TABLE_JOBS;
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Get all jobs", LOG_FLAG);
        Cursor c = db.rawQuery (selectQuery, null);
        if (c.moveToFirst ()) {
            do {
                Jobs jobs = new Jobs (
                        c.getInt ((c.getColumnIndex (JOB_PRIMARY_ID))),
                        c.getString ((c.getColumnIndex (JOB_ID))),
                        c.getString ((c.getColumnIndex (JOB_TITLE))),
                        c.getString ((c.getColumnIndex (JOB_BUDGET))),
                        c.getString ((c.getColumnIndex (JOB_SNIPPET))),
                        c.getString ((c.getColumnIndex (JOB_COUNTRY))),
                        c.getString ((c.getColumnIndex (JOB_SKILL))),
                        c.getString ((c.getColumnIndex (JOB_PAYMENT_VERIFICATION_STATUS))),
                        c.getInt ((c.getColumnIndex (JOB_JOB_POSTED))),
                        c.getInt ((c.getColumnIndex (JOB_JOB_POST_HIRES))),
                        c.getString ((c.getColumnIndex (JOB_URL))),
                        c.getString ((c.getColumnIndex (CLIENT_TOTAL_JOB_POSTED))),
                        c.getString ((c.getColumnIndex (CLIENT_TOTAL_SPENT))),
                        c.getString ((c.getColumnIndex (CLIENT_TOTAL_JOB_FILLED))),
                        c.getString ((c.getColumnIndex (CLIENT_MEMBER_SINCE))),
                        c.getString ((c.getColumnIndex (CLIENT_TOTAL_HOURS))),
                        c.getString ((c.getColumnIndex (CLIENT_JOB_PERCENT)))

                );
                jobList.add (jobs);
            } while (c.moveToNext ());
        }
        return jobList;
    }
    public void deleteAllJobs () {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Delete all filters", LOG_FLAG);
        db.execSQL ("delete from " + TABLE_JOBS);
    }

   /* public long createJobs (Jobs job) {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Creating Jobs", LOG_FLAG);
        ContentValues values = new ContentValues ();
        values.put (JOB_PRIMARY_ID, job.getId());
        values.put (JOB_ID, job.getId());
        values.put (JOB_TITLE, job.getTitle());
        values.put (JOB_BUDGET, job.getBudget());
        values.put (JOB_SNIPPET, job.getSnippet());
        values.put (JOB_COUNTRY, job.getCountry());
        values.put(JOB_PAYMENT_VERIFICATION_STATUS, job.getStatus());
        values.put (JOB_JOB_POSTED, job.getJob_post());
        values.put (JOB_JOB_POST_HIRES, job.getJob_hire());
        values.put (JOB_URL, job.getJob_url());
        return db.insert (TABLE_JOBS, null, values);
    }*/

   /* public boolean isEventExist (int event_id) {
        String countQuery = "SELECT * FROM " + TABLE_EVENTS + " WHERE " + EVNT_ID + " = " + event_id;
        SQLiteDatabase db = this.getReadableDatabase ();
        Cursor cursor = db.rawQuery (countQuery, null);
        int count = cursor.getCount ();
        cursor.close ();
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    public int updateEventDetails (int event_id, String details) {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Update event details in event id = " + event_id, LOG_FLAG);
        ContentValues values = new ContentValues ();
        values.put (EVNT_DETAILS, details);
        return db.update (TABLE_EVENTS, values, EVNT_ID + " = ?", new String[] {String.valueOf (event_id)});
    }

    public String getEventDetails (int event_id) {
        SQLiteDatabase db = this.getReadableDatabase ();
        String selectQuery = "SELECT * FROM " + TABLE_EVENTS + " WHERE " + EVNT_ID + " = " + event_id;
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Get event details where event ID = " + event_id, LOG_FLAG);
        Cursor c = db.rawQuery (selectQuery, null);
        if (c != null)
            c.moveToFirst ();
        return c.getString (c.getColumnIndex (EVNT_DETAILS));
    }

    public int updateEventFloorPlan (int event_id, String floor_plan) {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Update event floor plan in event id = " + event_id, LOG_FLAG);
        ContentValues values = new ContentValues ();
        values.put (EVNT_FLOOR_PLAN, floor_plan);
        return db.update (TABLE_EVENTS, values, EVNT_ID + " = ?", new String[] {String.valueOf (event_id)});
    }*/


/*
    public void deleteFilter (String category) {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Delete filter where filter = " + category, LOG_FLAG);
        db.execSQL ("DELETE FROM " + TABLE_FILTER + " WHERE " + FILTER_CATEGORY + " = '" + category + "'");
    }

    public void deleteAllFilters () {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Delete all filters", LOG_FLAG);
        db.execSQL ("delete from " + TABLE_FILTER);
    }*/

}
