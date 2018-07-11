package com.actiknow.actiproject.dialogFragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actiknow.actiproject.R;
import com.actiknow.actiproject.model.Jobs;
import com.actiknow.actiproject.utils.AppConfigTags;
import com.actiknow.actiproject.utils.AppConfigURL;
import com.actiknow.actiproject.utils.Constants;
import com.actiknow.actiproject.utils.NetworkConnection;
import com.actiknow.actiproject.utils.UserDetailsPref;
import com.actiknow.actiproject.utils.Utils;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class JobDetailFragment extends DialogFragment {
    // ImageView ivCancel;
    TextView tvJobTitle;
    TextView tvCountryName;
    TextView tvPaymentStatus;
    TextView tvBudget;
    TextView tvNoOfJOBS;
    TextView tvJobPosted;
    TextView tvJobHired;
    TextView tvJobSnippet;
    TextView tvUrl;
    TextView tvSkills;
    TextView tvTitle;
    ImageView ivCancel;
    ProgressDialog progressDialog;
    UserDetailsPref userDetailsPref;
    String url;
    int id;
    String job_id;
    String skills;
    int value;
    TextView tvAccept;
    TextView tvReject;
    CoordinatorLayout clMain;
    OnDialogResultListener onDialogResultListener;
    int val;
    LinearLayout llAcceptedRejected;
    TextView tvJobPosted2;
    TextView tvJobFilled;
    TextView tvTotalHour;
    TextView tvTotalSpent;
    TextView tvMemberSince;
    ArrayList<Jobs>job_list;
    int position = -1;

    public JobDetailFragment newInstance(int id, String job_id, int val, ArrayList<Jobs>jobs) {
        JobDetailFragment f = new JobDetailFragment();
        Bundle args = new Bundle();
        args.putInt(AppConfigTags.ID, id);
        args.putString(AppConfigTags.JOB_ID, job_id);
        args.putParcelableArrayList(AppConfigTags.JOBS,jobs);
        args.putInt("value", val);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        Window window = getDialog().getWindow();
        window.getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                    //This is the filter
                    if (event.getAction() != KeyEvent.ACTION_UP)
                        return true;
                    else {
                        getDialog().dismiss();
                        //Hide your keyboard here!!!!!!
                        return true; // pretend we've processed it
                    }
                } else
                    return false; // pass on to be processed as normal
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_fragment_jobs_detail, container, false);
        initView(root);
        initBundle();
        initData();
        initListener();
        initAdapter();
        setData();
        return root;
    }

    private void initView(View root) {
        tvJobTitle = (TextView) root.findViewById(R.id.tvJobTitle);
        tvTitle = (TextView) root.findViewById(R.id.tvTitle);

        tvCountryName = (TextView) root.findViewById(R.id.tvCountryName);
        tvPaymentStatus = (TextView) root.findViewById(R.id.tvPaymentStatus);
        tvBudget = (TextView) root.findViewById(R.id.tvBudget);
        tvNoOfJOBS = (TextView) root.findViewById(R.id.tvNoOfJOBS);
        tvUrl = (TextView) root.findViewById(R.id.tvUrl);
        tvSkills = (TextView) root.findViewById(R.id.tvSkills);
        tvJobPosted = (TextView) root.findViewById(R.id.tvJobPosted);
        tvJobHired = (TextView) root.findViewById(R.id.tvJobHired);
        tvJobSnippet = (TextView) root.findViewById(R.id.tvJobSnippet);
        ivCancel = (ImageView) root.findViewById(R.id.ivCancel);
        tvAccept = (TextView) root.findViewById(R.id.tvAccepted);
        tvReject = (TextView) root.findViewById(R.id.tvRejected);
        clMain = (CoordinatorLayout) root.findViewById(R.id.clMain);

        tvJobPosted2 = (TextView)root.findViewById(R.id.tvJobPosted2);
        tvJobFilled = (TextView) root.findViewById(R.id.tvJobFilled);
        tvTotalHour = (TextView) root.findViewById(R.id.tvTotalHour);
        tvTotalSpent = (TextView) root.findViewById(R.id.tvTotalSpent);
        tvMemberSince = (TextView) root.findViewById(R.id.tvMemberSince);

        llAcceptedRejected = (LinearLayout) root.findViewById(R.id.llAcceptedRejected);
    }

    private void initBundle() {
        Bundle bundle = this.getArguments();
        id = bundle.getInt(AppConfigTags.ID);
        job_id = bundle.getString(AppConfigTags.JOB_ID);
        value = bundle.getInt("value");
        job_list = bundle.getParcelableArrayList(AppConfigTags.JOBS);
        Log.d("id",""+id);
        Log.d("job_id",""+job_id);
        Log.d("value",""+value);
    }

    private void initData() {
        userDetailsPref = UserDetailsPref.getInstance();
        Utils.setTypefaceToAllViews(getActivity(), tvJobTitle);
        progressDialog = new ProgressDialog(getActivity());
    // 0- normal, 1-accepted, 2-rejected
        switch (value) {
            case 0:
                tvAccept.setVisibility(View.VISIBLE);
                tvReject.setVisibility(View.VISIBLE);
                break;
            case 1:
                tvAccept.setVisibility(View.GONE);
                tvReject.setVisibility(View.VISIBLE);
                break;
            case 2:
                tvAccept.setVisibility(View.VISIBLE);
                tvReject.setVisibility(View.GONE);
                break;
        }
    }

    private void initAdapter() {
    }

    private void initListener() {
        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        tvUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             /*   Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra(AppConfigTags.JOB_URL, url);
                startActivity(intent);*/
            }
        });
        tvAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptJob(String.valueOf(id), job_id);
            }
        });
        tvReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rejectJob(String.valueOf(id), job_id);
            }
        });
    }

    private void setData() {
        for (int i = 0; i < job_list.size(); i++) {
            Jobs jobs = job_list.get(i);
            if (jobs.getJob_id().equalsIgnoreCase(job_id)) {
                tvJobTitle.setText(jobs.getTitle());
                tvTitle.setText(jobs.getTitle());
                tvCountryName.setText("Country Name  : " + jobs.getCountry());
                tvPaymentStatus.setText("Payment Status : " + jobs.getStatus());
                tvBudget.setText("Job Budget       : " + jobs.getBudget());
                tvUrl.setText(jobs.getJob_url());
                tvJobPosted.setText("Job Posted : " + jobs.getJob_post()+"  |  ");
                tvJobHired.setText("Job Hire  : " + jobs.getJob_hire());
                tvJobSnippet.setText("Job Snippet  : " + jobs.getSnippet());
                tvSkills.setText("Skills  :  "+jobs.getSkill());
                tvJobPosted2.setText("Job Posted : "+jobs.getTotal_job_posted()+"  |  ");
                tvJobFilled.setText("Job Filled : "+jobs.getTotal_job_filled()+"  |  "+jobs.getClient_job_percent());
                tvTotalHour.setText("Hours : "+jobs.getTotal_hours()+"  |  ");
                tvTotalSpent.setText("Spent : "+jobs.getTotal_spent());
                tvMemberSince.setText(jobs.getClient_member_since());
                position = i;
            }
        }

    }

    private void rejectJob(final String id, final String job_id) {
        if (NetworkConnection.isNetworkAvailable(getActivity())) {
            Utils.showProgressDialog(progressDialog, getResources().getString(R.string.progress_dialog_text_please_wait), true);
            Utils.showLog(Log.INFO, "" + AppConfigTags.URL, AppConfigURL.REJECT_JOB, true);
            StringRequest strRequest1 = new StringRequest(Request.Method.POST, AppConfigURL.REJECT_JOB,
                    new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Utils.showLog(Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject(response);
                                    boolean error = jsonObj.getBoolean(AppConfigTags.ERROR);
                                    String message = jsonObj.getString(AppConfigTags.MESSAGE);
                                    if (!error) {
                                        Utils.showSnackBar(getActivity(), clMain, message, Snackbar.LENGTH_LONG, null, null);
                                        getDialog().dismiss();
                                        userDetailsPref.putIntPref(getActivity(), AppConfigTags.POSITION, position);
                                        //finish();
                                        //startActivity(getIntent());
                                        //   jobsList();
                                        /*userDetailsPref.putStringPref (getActivity(), UserDetailsPref.ID, jsonObj.getString (AppConfigTags.USER_ID));
                                        userDetailsPref.putStringPref (getActivity(), UserDetailsPref.NAME, jsonObj.getString (AppConfigTags.USER_NAME));
                                        userDetailsPref.putStringPref (getActivity(), UserDetailsPref.EMAIL, jsonObj.getString (AppConfigTags.USER_EMAIL));
                                        userDetailsPref.putStringPref (getActivity(), UserDetailsPref.MOBILE, jsonObj.getString (AppConfigTags.USER_MOBILE));
                                        userDetailsPref.putStringPref (getActivity(), UserDetailsPref.LOGIN_KEY, jsonObj.getString (AppConfigTags.USER_LOGIN_KEY));
                                        Intent intent = new Intent(getActivity(),MainActivity.class);
                                        startActivity(intent);
                                        finish ();
                                        overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);*/
                                    } else {
                                        Utils.showSnackBar(getActivity(), clMain, message, Snackbar.LENGTH_LONG, null, null);
                                    }
                                    progressDialog.dismiss();
                                } catch (Exception e) {
                                    progressDialog.dismiss();
                                    Utils.showSnackBar(getActivity(), clMain, getResources().getString(R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                                    e.printStackTrace();
                                }
                            } else {
                                Utils.showSnackBar(getActivity(), clMain, getResources().getString(R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                                Utils.showLog(Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                            progressDialog.dismiss();
                        }
                    },
                    new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Utils.showLog(Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString(), true);
                            NetworkResponse response = error.networkResponse;
                            if (response != null && response.data != null) {
                                Utils.showLog(Log.ERROR, AppConfigTags.ERROR, new String(response.data), true);
                            }
                            Utils.showSnackBar(getActivity(), clMain, getResources().getString(R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                            progressDialog.dismiss();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String>();
                    params.put(AppConfigTags.ID, id);
                    params.put(AppConfigTags.JOB_ID, job_id);
                    params.put(AppConfigTags.STATUS, "1");
                    Utils.showLog(Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put(AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    params.put(AppConfigTags.HEADER_USER_LOGIN_KEY, userDetailsPref.getStringPref(getActivity(), UserDetailsPref.LOGIN_KEY));
                    Utils.showLog(Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            Utils.sendRequest(strRequest1, 60);
        } else {
            Utils.showSnackBar(getActivity(), clMain, getResources().getString(R.string.snackbar_text_no_internet_connection_available), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_go_to_settings), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent dialogIntent = new Intent(Settings.ACTION_SETTINGS);
                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(dialogIntent);
                }
            });
        }
    }

    private void acceptJob(final String id, final String job_id) {
        if (NetworkConnection.isNetworkAvailable(getActivity())) {
            Utils.showProgressDialog(progressDialog, getResources().getString(R.string.progress_dialog_text_please_wait), true);
            Utils.showLog(Log.INFO, "" + AppConfigTags.URL, AppConfigURL.ACCEPT_JOB, true);
            StringRequest strRequest1 = new StringRequest(Request.Method.POST, AppConfigURL.ACCEPT_JOB,
                    new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Utils.showLog(Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject(response);
                                    boolean error = jsonObj.getBoolean(AppConfigTags.ERROR);
                                    String message = jsonObj.getString(AppConfigTags.MESSAGE);
                                    if (!error) {
                                        Utils.showSnackBar2(getActivity(), clMain, message, Snackbar.LENGTH_LONG, null, null);
                                        userDetailsPref.putIntPref(getActivity(), AppConfigTags.POSITION, position);
                                        // finish();
                                        // startActivity(getIntent());
                                        getDialog().dismiss();
                                        /*userDetailsPref.putStringPref (getActivity(), UserDetailsPref.ID, jsonObj.getString (AppConfigTags.USER_ID));
                                        userDetailsPref.putStringPref (getActivity(), UserDetailsPref.NAME, jsonObj.getString (AppConfigTags.USER_NAME));
                                        userDetailsPref.putStringPref (getActivity(), UserDetailsPref.EMAIL, jsonObj.getString (AppConfigTags.USER_EMAIL));
                                        userDetailsPref.putStringPref (getActivity(), UserDetailsPref.MOBILE, jsonObj.getString (AppConfigTags.USER_MOBILE));
                                        userDetailsPref.putStringPref (getActivity(), UserDetailsPref.LOGIN_KEY, jsonObj.getString (AppConfigTags.USER_LOGIN_KEY));
                                        Intent intent = new Intent(getActivity(),MainActivity.class);
                                        startActivity(intent);
                                        finish ();
                                        overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);*/
                                    } else {
                                        Utils.showSnackBar2(getActivity(), clMain, message, Snackbar.LENGTH_LONG, null, null);
                                    }
                                    progressDialog.dismiss();
                                } catch (Exception e) {
                                    progressDialog.dismiss();
                                    Utils.showSnackBar2(getActivity(), clMain, getResources().getString(R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                                    e.printStackTrace();
                                }
                            } else {
                                Utils.showSnackBar2(getActivity(), clMain, getResources().getString(R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                                Utils.showLog(Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                            progressDialog.dismiss();
                        }
                    },
                    new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Utils.showLog(Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString(), true);
                            NetworkResponse response = error.networkResponse;
                            if (response != null && response.data != null) {
                                Utils.showLog(Log.ERROR, AppConfigTags.ERROR, new String(response.data), true);
                            }
                            Utils.showSnackBar2(getActivity(), clMain, getResources().getString(R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                            progressDialog.dismiss();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String>();
                    params.put(AppConfigTags.JOB_PRIMARY_ID, id);
                    params.put(AppConfigTags.JOB_ID, job_id);
                    params.put(AppConfigTags.USER_ID, String.valueOf(userDetailsPref.getIntPref(getActivity(), UserDetailsPref.ID)));
                    Utils.showLog(Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put(AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    params.put(AppConfigTags.USER_LOGIN_KEY, userDetailsPref.getStringPref(getActivity(), UserDetailsPref.LOGIN_KEY));
                    Utils.showLog(Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            Utils.sendRequest(strRequest1, 60);
        } else {
            Utils.showSnackBar2(getActivity(), clMain, getResources().getString(R.string.snackbar_text_no_internet_connection_available), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_go_to_settings), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent dialogIntent = new Intent(Settings.ACTION_SETTINGS);
                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(dialogIntent);
                }
            });
        }
    }


    public void setOnDialogResultListener(OnDialogResultListener listener) {
        this.onDialogResultListener = listener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDialogResultListener != null) {
            onDialogResultListener.onDismiss();
        }
        Utils.hideSoftKeyboard(getActivity());
    }



    public interface OnDialogResultListener {
        public abstract void onDismiss();

    }

}