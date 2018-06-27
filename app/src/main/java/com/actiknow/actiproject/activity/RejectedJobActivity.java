package com.actiknow.actiproject.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.actiknow.actiproject.R;
import com.actiknow.actiproject.adapter.RejectedJobsAdapter;
import com.actiknow.actiproject.dialogFragment.JobDetailFragment;
import com.actiknow.actiproject.model.AcceptedJobs;
import com.actiknow.actiproject.model.RejectedJobs;
import com.actiknow.actiproject.utils.AppConfigTags;
import com.actiknow.actiproject.utils.AppConfigURL;
import com.actiknow.actiproject.utils.Constants;
import com.actiknow.actiproject.utils.NetworkConnection;
import com.actiknow.actiproject.utils.RecyclerViewMargin;
import com.actiknow.actiproject.utils.UserDetailsPref;
import com.actiknow.actiproject.utils.Utils;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class RejectedJobActivity extends AppCompatActivity {
    RecyclerView rvJobs;
    UserDetailsPref userDetailsPref;
    CoordinatorLayout clMain;
    RejectedJobsAdapter rejectedJobsAdapter;
    ProgressDialog progressDialog;
    ImageView ivBack;
    String arrayResponse;
    private Paint p = new Paint();
    private View view;
    List<RejectedJobs> rejectedJobsList = new ArrayList<>();
    final Handler handler = new Handler();
    ArrayList<Integer> job_ids = new ArrayList<>();
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rejected_jobs);
        initView();
        initData();
        initAdapter();
        initListener();
        rejectedJobsList2(0);
        count = 1;

    
    }


    protected void onResume() {
        super.onResume();

        final int delay = 20000; //milliseconds
        handler.postDelayed(new Runnable() {
            public void run() {
                getRejectedJobId();
                handler.postDelayed(this, delay);

            }
        }, delay);
    }

    protected void onPause() {
        Log.d("onstop","onstop");
        handler.removeCallbacksAndMessages(null);
        super.onPause();
    }

    private void initListener() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RejectedJobActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });



        rejectedJobsAdapter.SetOnItemClickListener(new RejectedJobsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                RejectedJobs rejectedJobs = rejectedJobsList.get(position);
                android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                JobDetailFragment dialog = new JobDetailFragment().newInstance(rejectedJobs.getId(),rejectedJobs.getJob_id(), 2);
                dialog.show(ft, "jobs");

            }
        });
    }

    private void initAdapter() {
        rejectedJobsAdapter = new RejectedJobsAdapter(RejectedJobActivity.this, rejectedJobsList);
        rvJobs.setAdapter(rejectedJobsAdapter);
        rvJobs.setHasFixedSize(true);
        rvJobs.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvJobs.setItemAnimator(new DefaultItemAnimator());
        rvJobs.addItemDecoration(new RecyclerViewMargin((int) Utils.pxFromDp(RejectedJobActivity.this, 16), (int) Utils.pxFromDp(RejectedJobActivity.this, 16), (int) Utils.pxFromDp(RejectedJobActivity.this, 16), (int) Utils.pxFromDp(RejectedJobActivity.this, 16), 1, 0, RecyclerViewMargin.LAYOUT_MANAGER_LINEAR, RecyclerViewMargin.ORIENTATION_VERTICAL));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rvJobs);

    }

    private void initView() {
        clMain = (CoordinatorLayout) findViewById(R.id.clMain);
        rvJobs = (RecyclerView) findViewById(R.id.rvJobs);
        ivBack = (ImageView) findViewById(R.id.ivBack);
    }
    private void initData() {
        userDetailsPref = UserDetailsPref.getInstance();
        progressDialog = new ProgressDialog(this);
       // String response = userDetailsPref.getStringPref(this, UserDetailsPref.RESPONSE);
      /*  if (response != null) {

            try {
                arrayResponse = response;
                JSONObject jsonObj = new JSONObject(response);
                boolean is_error = jsonObj.getBoolean(AppConfigTags.ERROR);
                String message = jsonObj.getString(AppConfigTags.MESSAGE);
                if (!is_error) {

                    JSONArray jsonArrayRejectedJobs = jsonObj.getJSONArray(AppConfigTags.REJECTED_JOB);
                    for (int i = 0; i < jsonArrayRejectedJobs.length(); i++) {
                        JSONObject jsonObjectRejectedJobs = jsonArrayRejectedJobs.getJSONObject(i);
                        rejectedJobsList.add(new RejectedJobs(jsonObjectRejectedJobs.getInt(AppConfigTags.ID),
                                jsonObjectRejectedJobs.getString(AppConfigTags.JOB_ID),
                                jsonObjectRejectedJobs.getString(AppConfigTags.JOB_TITLE),
                                jsonObjectRejectedJobs.getString(AppConfigTags.JOB_BUDGET),
                                jsonObjectRejectedJobs.getString(AppConfigTags.JOB_SNIPPET),
                                jsonObjectRejectedJobs.getString(AppConfigTags.JOB_COUNTRY),
                                jsonObjectRejectedJobs.getString(AppConfigTags.JOB_SKILL),
                                jsonObjectRejectedJobs.getString(AppConfigTags.JOB_PAYMENT_VERIFICATION_STATUS),
                                jsonObjectRejectedJobs.getInt(AppConfigTags.JOB_JOB_POSTED),
                                jsonObjectRejectedJobs.getInt(AppConfigTags.JOB_JOB_POST_HIRES),
                                jsonObjectRejectedJobs.getString(AppConfigTags.JOB_URL),
                                jsonObjectRejectedJobs.getString(AppConfigTags.REJECTED_BY)));

                    }

                    //RejectedJobsAdapter.notifyDataSetChanged();
                    rejectedJobsAdapter = new RejectedJobsAdapter(RejectedJobActivity.this, rejectedJobsList);
                    rvJobs.setAdapter(rejectedJobsAdapter);
                    rvJobs.setHasFixedSize(true);
                    rvJobs.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                    rvJobs.setItemAnimator(new DefaultItemAnimator());
                    rvJobs.addItemDecoration(new RecyclerViewMargin((int) Utils.pxFromDp(RejectedJobActivity.this, 16), (int) Utils.pxFromDp(RejectedJobActivity.this, 16), (int) Utils.pxFromDp(RejectedJobActivity.this, 16), (int) Utils.pxFromDp(RejectedJobActivity.this, 16), 1, 0, RecyclerViewMargin.LAYOUT_MANAGER_LINEAR, RecyclerViewMargin.ORIENTATION_VERTICAL));
                    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
                    itemTouchHelper.attachToRecyclerView(rvJobs);

                }
            } catch (Exception e) {
                e.printStackTrace();
                Utils.showSnackBar2(RejectedJobActivity.this, clMain, getResources().getString(R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
            }
        } else {
            Utils.showSnackBar2(RejectedJobActivity.this, clMain, getResources().getString(R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
            Utils.showLog(Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
        }*/
    }

    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();

            Log.e("ID",""+rejectedJobsList.get(position).getId()+ "JOB_ID - " + rejectedJobsList.get(position).getJob_id());
            acceptJob(String.valueOf(rejectedJobsList.get(position).getId()), rejectedJobsList.get(position).getJob_id());
            final RejectedJobs deletedItem = rejectedJobsList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();
            rejectedJobsAdapter.removeItem(position);
            Snackbar snackbar = Snackbar.make(clMain, "Job Rejected", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // undo is selected, restore the deleted item
                    rejectedJobsAdapter.restoreItem(deletedItem, deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            Bitmap icon;
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                View itemView = viewHolder.itemView;
                float height = (float) itemView.getBottom() - (float) itemView.getTop();
                float width = height / 3;

                if (dX > 0) {
                    p.setColor(Color.parseColor("#388E3C"));
                    RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                    c.drawRect(background, p);
                    icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_drawer);
                    RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                    c.drawBitmap(icon, null, icon_dest, p);
                } else {
                    p.setColor(Color.parseColor("#D32F2F"));
                    RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                    c.drawRect(background, p);
                    icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_drawer);
                    RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                    c.drawBitmap(icon, null, icon_dest, p);
                }
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

    };

    private void acceptJob (final String id, final String job_id) {
        if (NetworkConnection.isNetworkAvailable (RejectedJobActivity.this)) {
            Utils.showProgressDialog (progressDialog, getResources ().getString (R.string.progress_dialog_text_please_wait), true);
            Utils.showLog (Log.INFO, "" + AppConfigTags.URL, AppConfigURL.ACCEPT_REJECTED_JOB, true);
            StringRequest strRequest1 = new StringRequest (Request.Method.POST, AppConfigURL.ACCEPT_REJECTED_JOB,
                    new com.android.volley.Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject(response);
                                    boolean error = jsonObj.getBoolean (AppConfigTags.ERROR);
                                    String message = jsonObj.getString (AppConfigTags.MESSAGE);
                                    if (!error) {
                                      //  rejectedJobsAdapter.notifyDataSetChanged();
                                        /*rejectedJobsList.clear();
                                        JSONArray jsonArrayRejectedJobs = jsonObj.getJSONArray(AppConfigTags.REJECTED_JOB);
                                        for (int i = 0; i < jsonArrayRejectedJobs.length(); i++) {
                                            JSONObject jsonObjectRejectedJobs = jsonArrayRejectedJobs.getJSONObject(i);
                                            rejectedJobsList.add(new RejectedJobs(jsonObjectRejectedJobs.getInt(AppConfigTags.ID),
                                                    jsonObjectRejectedJobs.getString(AppConfigTags.JOB_ID),
                                                    jsonObjectRejectedJobs.getString(AppConfigTags.JOB_TITLE),
                                                    jsonObjectRejectedJobs.getString(AppConfigTags.JOB_BUDGET),
                                                    jsonObjectRejectedJobs.getString(AppConfigTags.JOB_SNIPPET),
                                                    jsonObjectRejectedJobs.getString(AppConfigTags.JOB_COUNTRY),
                                                    jsonObjectRejectedJobs.getString(AppConfigTags.JOB_PAYMENT_VERIFICATION_STATUS),
                                                    jsonObjectRejectedJobs.getInt(AppConfigTags.JOB_JOB_POSTED),
                                                    jsonObjectRejectedJobs.getInt(AppConfigTags.JOB_JOB_POST_HIRES),
                                                    jsonObjectRejectedJobs.getString(AppConfigTags.JOB_URL),
                                                    jsonObjectRejectedJobs.getString(AppConfigTags.REJECTED_BY)));

                                        }

                                        //RejectedJobsAdapter.notifyDataSetChanged();
                                        rejectedJobsAdapter = new RejectedJobsAdapter(RejectedJobActivity.this, rejectedJobsList);
                                        rvJobs.setAdapter(rejectedJobsAdapter);
                                        rvJobs.setHasFixedSize(true);
                                        rvJobs.setLayoutManager(new LinearLayoutManager(RejectedJobActivity.this, LinearLayoutManager.VERTICAL, false));
                                        rvJobs.setItemAnimator(new DefaultItemAnimator());
                                        rvJobs.addItemDecoration(new RecyclerViewMargin((int) Utils.pxFromDp(RejectedJobActivity.this, 16), (int) Utils.pxFromDp(RejectedJobActivity.this, 16), (int) Utils.pxFromDp(RejectedJobActivity.this, 16), (int) Utils.pxFromDp(RejectedJobActivity.this, 16), 1, 0, RecyclerViewMargin.LAYOUT_MANAGER_LINEAR, RecyclerViewMargin.ORIENTATION_VERTICAL));
                                        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
                                        itemTouchHelper.attachToRecyclerView(rvJobs);*/
                                        Utils.showSnackBar2 (RejectedJobActivity.this, clMain, message, Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);

                                    }
                                    progressDialog.dismiss ();
                                } catch (Exception e) {
                                    progressDialog.dismiss ();
                                    Utils.showSnackBar2 (RejectedJobActivity.this, clMain, getResources ().getString (R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                    e.printStackTrace ();
                                }
                            } else {
                                Utils.showSnackBar2 (RejectedJobActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                Utils.showLog (Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                            progressDialog.dismiss ();
                        }
                    },
                    new com.android.volley.Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            Utils.showLog (Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString (), true);
                            NetworkResponse response = error.networkResponse;
                            if (response != null && response.data != null) {
                                Utils.showLog (Log.ERROR, AppConfigTags.ERROR, new String(response.data), true);
                            }
                            Utils.showSnackBar2 (RejectedJobActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                            progressDialog.dismiss ();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams () throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String>();
                    params.put (AppConfigTags.JOB_PRIMARY_ID, id);
                    params.put (AppConfigTags.JOB_ID, job_id);
                    params.put (AppConfigTags.USER_ID, String.valueOf(userDetailsPref.getIntPref(RejectedJobActivity.this, UserDetailsPref.ID)));
                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders () throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put (AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    params.put (AppConfigTags.USER_LOGIN_KEY, userDetailsPref.getStringPref(RejectedJobActivity.this, UserDetailsPref.LOGIN_KEY));
                    Utils.showLog (Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            Utils.sendRequest (strRequest1, 60);
        } else {
            Utils.showSnackBar2 (this, clMain, getResources ().getString (R.string.snackbar_text_no_internet_connection_available), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_go_to_settings), new View.OnClickListener () {
                @Override
                public void onClick (View v) {
                    Intent dialogIntent = new Intent(Settings.ACTION_SETTINGS);
                    dialogIntent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity (dialogIntent);
                }
            });
        }
    }





    public void rejectedJobsList() {
        if (NetworkConnection.isNetworkAvailable(RejectedJobActivity.this)) {
            Utils.showLog(Log.INFO, AppConfigTags.URL, AppConfigURL.REJECTED_JOBS, true);
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            //Utils.showProgressDialog(progressDialog, getResources().getString(R.string.progress_dialog_text_please_wait), true);
            StringRequest strRequest = new StringRequest(Request.Method.GET, AppConfigURL.REJECTED_JOBS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            userDetailsPref.putStringPref(RejectedJobActivity.this, UserDetailsPref.RESPONSE, response);
                            Utils.showLog(Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                rejectedJobsList.clear();
                                try {
                                    JSONObject jsonObj = new JSONObject(response);
                                    boolean is_error = jsonObj.getBoolean(AppConfigTags.ERROR);
                                    String message = jsonObj.getString(AppConfigTags.MESSAGE);
                                    if (!is_error) {
                                        JSONArray jsonArrayRejectedJobs = jsonObj.getJSONArray(AppConfigTags.REJECTED_JOB);
                                        for (int i = 0; i < jsonArrayRejectedJobs.length(); i++) {
                                            JSONObject jsonObjectRejectedJobs = jsonArrayRejectedJobs.getJSONObject(i);
                                            rejectedJobsList.add(new RejectedJobs(jsonObjectRejectedJobs.getInt(AppConfigTags.ID),
                                                    jsonObjectRejectedJobs.getString(AppConfigTags.JOB_ID),
                                                    jsonObjectRejectedJobs.getString(AppConfigTags.JOB_TITLE),
                                                    jsonObjectRejectedJobs.getString(AppConfigTags.JOB_BUDGET),
                                                    jsonObjectRejectedJobs.getString(AppConfigTags.JOB_SNIPPET),
                                                    jsonObjectRejectedJobs.getString(AppConfigTags.JOB_COUNTRY),
                                                    jsonObjectRejectedJobs.getString(AppConfigTags.JOB_SKILL),
                                                    jsonObjectRejectedJobs.getString(AppConfigTags.JOB_PAYMENT_VERIFICATION_STATUS),
                                                    jsonObjectRejectedJobs.getInt(AppConfigTags.JOB_JOB_POSTED),
                                                    jsonObjectRejectedJobs.getInt(AppConfigTags.JOB_JOB_POST_HIRES),
                                                    jsonObjectRejectedJobs.getString(AppConfigTags.JOB_URL),
                                                    jsonObjectRejectedJobs.getString(AppConfigTags.REJECTED_BY),
                                                    jsonObjectRejectedJobs.getString(AppConfigTags.CLIENT_TOTAL_JOB_POSTED),
                                                    jsonObjectRejectedJobs.getString(AppConfigTags.CLIENT_TOTAL_SPENT),
                                                    jsonObjectRejectedJobs.getString(AppConfigTags.CLIENT_TOTAL_JOB_FILLED),
                                                    jsonObjectRejectedJobs.getString(AppConfigTags.CLIENT_MEMBER_SINCE),
                                                    jsonObjectRejectedJobs.getString(AppConfigTags.CLIENT_TOTAL_HOURS),
                                                    jsonObjectRejectedJobs.getString(AppConfigTags.CLIENT_JOB_PERCENT)
                                                    ));

                                        }
                                        rejectedJobsAdapter.notifyDataSetChanged();

                                        //progressDialog.dismiss();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Utils.showSnackBar(RejectedJobActivity.this, clMain, getResources().getString(R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                                }
                            } else {
                                Utils.showSnackBar(RejectedJobActivity.this, clMain, getResources().getString(R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                                Utils.showLog(Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Utils.showLog(Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString(), true);
                            NetworkResponse response = error.networkResponse;
                            if (response != null && response.data != null) {
                                Utils.showLog(Log.ERROR, AppConfigTags.ERROR, new String(response.data), true);
                            }
                            Utils.showSnackBar(RejectedJobActivity.this, clMain, getResources().getString(R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String>();
                    Utils.showLog(Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    UserDetailsPref userDetailsPref = UserDetailsPref.getInstance();
                    params.put(AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    params.put(AppConfigTags.HEADER_USER_LOGIN_KEY, userDetailsPref.getStringPref(RejectedJobActivity.this, UserDetailsPref.LOGIN_KEY));
                    Utils.showLog(Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            Utils.sendRequest(strRequest, 30);
        } else {
            Utils.showSnackBar(RejectedJobActivity.this, clMain, getResources().getString(R.string.snackbar_text_no_internet_connection_available), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_go_to_settings), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent dialogIntent = new Intent(Settings.ACTION_SETTINGS);
                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(dialogIntent);
                }
            });
        }
    }

    public void rejectedJobsList2(final int offset) {
        if (NetworkConnection.isNetworkAvailable(RejectedJobActivity.this)) {
            if (offset > 0) {
                rejectedJobsList.add(new RejectedJobs());
                rejectedJobsAdapter.notifyItemInserted(rejectedJobsList.size() - 1);
            }
            Utils.showLog(Log.INFO, AppConfigTags.URL, AppConfigURL.REJECTED_JOBS2+"/"+offset, true);
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            if(count == 0) {
                Utils.showProgressDialog(progressDialog, getResources().getString(R.string.progress_dialog_text_please_wait), true);
            }
            StringRequest strRequest = new StringRequest(Request.Method.GET, AppConfigURL.REJECTED_JOBS2+"/"+offset,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            userDetailsPref.putStringPref(RejectedJobActivity.this, UserDetailsPref.RESPONSE, response);
                            Utils.showLog(Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                rejectedJobsList.clear();
                                try {
                                    JSONObject jsonObj = new JSONObject(response);
                                    boolean is_error = jsonObj.getBoolean(AppConfigTags.ERROR);
                                    String message = jsonObj.getString(AppConfigTags.MESSAGE);
                                    if (!is_error) {
                                        if (offset > 0) {
                                            rejectedJobsList.remove(rejectedJobsList.size() - 1);
                                        } else {
                                            rejectedJobsAdapter.setMoreDataAvailable(true);
                                            rejectedJobsList.clear();
                                        }
                                        JSONArray jsonArrayRejectedJobs = jsonObj.getJSONArray(AppConfigTags.REJECTED_JOB);
                                        for (int i = 0; i < jsonArrayRejectedJobs.length(); i++) {
                                            JSONObject jsonObjectRejectedJobs = jsonArrayRejectedJobs.getJSONObject(i);
                                            rejectedJobsList.add(new RejectedJobs(jsonObjectRejectedJobs.getInt(AppConfigTags.ID),
                                                    jsonObjectRejectedJobs.getString(AppConfigTags.JOB_ID),
                                                    jsonObjectRejectedJobs.getString(AppConfigTags.JOB_TITLE),
                                                    jsonObjectRejectedJobs.getString(AppConfigTags.JOB_BUDGET),
                                                    jsonObjectRejectedJobs.getString(AppConfigTags.JOB_SNIPPET),
                                                    jsonObjectRejectedJobs.getString(AppConfigTags.JOB_COUNTRY),
                                                    jsonObjectRejectedJobs.getString(AppConfigTags.JOB_SKILL),
                                                    jsonObjectRejectedJobs.getString(AppConfigTags.JOB_PAYMENT_VERIFICATION_STATUS),
                                                    jsonObjectRejectedJobs.getInt(AppConfigTags.JOB_JOB_POSTED),
                                                    jsonObjectRejectedJobs.getInt(AppConfigTags.JOB_JOB_POST_HIRES),
                                                    jsonObjectRejectedJobs.getString(AppConfigTags.JOB_URL),
                                                    jsonObjectRejectedJobs.getString(AppConfigTags.REJECTED_BY),
                                                    jsonObjectRejectedJobs.getString(AppConfigTags.CLIENT_TOTAL_JOB_POSTED),
                                                    jsonObjectRejectedJobs.getString(AppConfigTags.CLIENT_TOTAL_SPENT),
                                                    jsonObjectRejectedJobs.getString(AppConfigTags.CLIENT_TOTAL_JOB_FILLED),
                                                    jsonObjectRejectedJobs.getString(AppConfigTags.CLIENT_MEMBER_SINCE),
                                                    jsonObjectRejectedJobs.getString(AppConfigTags.CLIENT_TOTAL_HOURS),
                                                    jsonObjectRejectedJobs.getString(AppConfigTags.CLIENT_JOB_PERCENT)));

                                        }
                                        if (jsonArrayRejectedJobs.length() == 0) {
                                            rejectedJobsAdapter.setMoreDataAvailable(false);
                                            Utils.showSnackBar(
                                                    RejectedJobActivity.this,
                                                    clMain, "No More Job available",
                                                    Snackbar.LENGTH_LONG, "DISMISS",
                                                    null);
                                        }
                                        rejectedJobsAdapter.notifyDataChanged();
                                        progressDialog.dismiss();
                                    } else {
                                        rejectedJobsAdapter.setMoreDataAvailable(true);
                                        Utils.showSnackBar(RejectedJobActivity.this, clMain, "Error occurred",
                                                Snackbar.LENGTH_INDEFINITE, "RETRY",
                                                new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        rejectedJobsList2(offset);
                                                    }
                                                });


                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    if (offset > 0) {
                                        rejectedJobsAdapter.setMoreDataAvailable(true);
                                        Utils.showSnackBar(
                                                RejectedJobActivity.this,
                                                clMain, "Unable to fetch more_jobs",
                                                Snackbar.LENGTH_INDEFINITE, "Retry",
                                                new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        rejectedJobsList2(offset);
                                                    }
                                                });
                                    }
                                }
                            } else {
                                if (offset > 0) {
                                    rejectedJobsAdapter.setMoreDataAvailable(true);
                                    Utils.showSnackBar(
                                            RejectedJobActivity.this,
                                            clMain, "Unable to fetch more_jobs",
                                            Snackbar.LENGTH_INDEFINITE, "Retry",
                                            new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    rejectedJobsList2(offset);
                                                }
                                            });
                                }
                                Utils.showSnackBar(RejectedJobActivity.this, clMain, getResources().getString(R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Utils.showLog(Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString(), true);
                            NetworkResponse response = error.networkResponse;
                            if (response != null && response.data != null) {
                                Utils.showLog(Log.ERROR, AppConfigTags.ERROR, new String(response.data), true);
                            }
                            if (offset > 0) {
                                rejectedJobsAdapter.setMoreDataAvailable(true);
                                Utils.showSnackBar(RejectedJobActivity.this, clMain, "Unable to fetch more_jobs", Snackbar.LENGTH_INDEFINITE, "Retry",
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                rejectedJobsList2(offset);
                                            }
                                        });
                            } else {
//                                swipeRefreshLayout.setRefreshing(false);
                            }
                            Utils.showSnackBar(RejectedJobActivity.this, clMain, getResources().getString(R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                        progressDialog.dismiss();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String>();
                    Utils.showLog(Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    UserDetailsPref userDetailsPref = UserDetailsPref.getInstance();
                    params.put(AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    params.put(AppConfigTags.HEADER_USER_LOGIN_KEY, userDetailsPref.getStringPref(RejectedJobActivity.this, UserDetailsPref.LOGIN_KEY));
                    Utils.showLog(Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            Utils.sendRequest(strRequest, 30);
        } else {
            if (offset > 0) {
                rejectedJobsAdapter.setMoreDataAvailable(true);
                Utils.showSnackBar(
                        RejectedJobActivity.this,
                        clMain, "Unable to fetch more_jobs",
                        Snackbar.LENGTH_INDEFINITE, "Retry",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                rejectedJobsList2(offset);
                            }
                        });

            } else {
                Utils.showSnackBar(RejectedJobActivity.this, clMain, getResources().getString(R.string.snackbar_text_no_internet_connection_available), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_go_to_settings), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent dialogIntent = new Intent(Settings.ACTION_SETTINGS);
                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(dialogIntent);
                    }
                });
            }
        }
    }

    private void getRejectedJobId() {
        if (NetworkConnection.isNetworkAvailable(RejectedJobActivity.this)) {
            //  Utils.showProgressDialog(progressDialog, getResources().getString(R.string.progress_dialog_text_please_wait), true);
            Utils.showLog(Log.INFO, "" + AppConfigTags.URL, AppConfigURL.REJECTED_JOB_IDS, true);
            StringRequest strRequest1 = new StringRequest(Request.Method.GET, AppConfigURL.REJECTED_JOB_IDS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Utils.showLog(Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject(response);
                                    boolean error = jsonObj.getBoolean(AppConfigTags.ERROR);
                                    String message = jsonObj.getString(AppConfigTags.MESSAGE);
                                    if (!error) {
                                        JSONArray jsonArrayJobIds = jsonObj.getJSONArray(AppConfigTags.JOB_IDS);
                                        for (int i = 0; i < jsonArrayJobIds.length(); i++) {
                                            JSONObject jsonObjectJobIds = jsonArrayJobIds.getJSONObject(i);
                                            job_ids.add(jsonObjectJobIds.getInt(AppConfigTags.ID));
                                            int k = 0;
                                            for (RejectedJobs jobs : rejectedJobsList) {
                                                if (jsonObjectJobIds.getInt(AppConfigTags.ID) == jobs.getId()) {
                                                    rejectedJobsAdapter.removeItem(k);
                                                }
                                                k++;
                                            }
                                        }

                                        rejectedJobsAdapter.notifyDataSetChanged();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Utils.showLog(Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Utils.showLog(Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString(), true);
                            NetworkResponse response = error.networkResponse;
                            if (response != null && response.data != null) {
                                Utils.showLog(Log.ERROR, AppConfigTags.ERROR, new String(response.data), true);
                            }
                        }
                    }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put(AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    params.put(AppConfigTags.USER_LOGIN_KEY, userDetailsPref.getStringPref(RejectedJobActivity.this, UserDetailsPref.LOGIN_KEY));
                    Utils.showLog(Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            Utils.sendRequest(strRequest1, 60);
        }
    }
}
