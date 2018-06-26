package com.actiknow.actiproject.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actiknow.actiproject.R;
import com.actiknow.actiproject.model.Jobs;

import com.actiknow.actiproject.utils.Utils;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class JobsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    OnItemClickListener mItemClickListener;
    private Activity activity;
    private List<Jobs> jobsList = new ArrayList<Jobs>();
    ProgressBar progressDialog;
    OnItemClickListener onItemClickListener;
    OnLoadMoreListener loadMoreListener;
    boolean isLoading = false, isMoreDataAvailable = true;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;


    public JobsAdapter(Activity activity, List<Jobs> jobsList) {
        this.activity = activity;
        this.jobsList = jobsList;
    }

    @Override
    public int getItemViewType (int position) {
        if (jobsList.get (position).getId () != 0) {
            return VIEW_TYPE_ITEM;
        } else {
            return VIEW_TYPE_LOADING;
        }
//        if (isPositionFooter (position)) {
//            return VIEW_TYPE_LOADING;
//        }
//        return VIEW_TYPE_ITEM;
    }
    private boolean isPositionFooter (int position) {
        return position == jobsList.size ();
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*final LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        final View sView = mInflater.inflate(R.layout.list_item_jobs_list, parent, false);
        return new ViewHolder(sView);*/

        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_jobs_list, parent, false);
            return new ViewHolder2(view, onItemClickListener);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_loading, parent, false);
            return new LoadHolder(view);
        }
        return null;
    }



    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {//        runEnterAnimation (holder.itemView);

        if (position >= getItemCount () - 1 && isMoreDataAvailable && ! isLoading && loadMoreListener != null) {
            isLoading = true;
            loadMoreListener.onLoadMore ();
        }
        if (getItemViewType (position) == VIEW_TYPE_ITEM) {
            ViewHolder2 viewHolder2 = (ViewHolder2) holder;
            final Jobs jobs = jobsList.get(position);
            progressDialog = new ProgressBar(activity);
            Utils.setTypefaceToAllViews(activity, viewHolder2.tvTitle);
            viewHolder2.tvTitle.setText(jobs.getTitle());
            if (jobs.getStatus().length() > 0) {
                viewHolder2.tvCountryName.setText(jobs.getCountry() + ", " + jobs.getStatus() + ", " + jobs.getBudget());
            } else {
                viewHolder2.tvCountryName.setText(jobs.getCountry() + ", " + "NA" + ", " + jobs.getBudget());
            }
            viewHolder2.tvStatus.setText(jobs.getSnippet());
            // holder.tvBudget.setText("Job Budget : " + jobs.getBudget());

            viewHolder2.tvJobPosted.setText("Job Posted : " + jobs.getJob_post() + "  |  ");
            viewHolder2.tvJobFilled.setText("Job Filled : " + jobs.getTotal_job_filled() + "  |  " + jobs.getClient_job_percent());
            viewHolder2.tvTotalHour.setText("Hours : " + jobs.getTotal_hours() + "  |  ");
            viewHolder2.tvTotalSpent.setText("Spent : " + jobs.getTotal_spent());
            viewHolder2.tvMemberSince.setText(jobs.getClient_member_since());
        }
    }


    public void removeItem(int position) {
        jobsList.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(Jobs jobs, int position) {
        jobsList.add(position, jobs);
        // notify item added by position
        notifyItemInserted(position);
    }
    @Override
    public int getItemCount() {
        return jobsList.size();
    }

    public void notifyDataChanged () {
        notifyDataSetChanged ();
        isLoading = false;
    }
    public void setMoreDataAvailable (boolean moreDataAvailable) {
        isMoreDataAvailable = moreDataAvailable;
    }

    public void setLoadMoreListener (OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore ();
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    static class LoadHolder extends RecyclerView.ViewHolder {

        public LoadHolder (View itemView) {
            super (itemView);
        }
    }

    public class ViewHolder2 extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvTitle;
        TextView tvCountryName;
        TextView tvStatus;
     //   TextView tvBudget;

        TextView tvJobPosted;
        TextView tvJobFilled;
        TextView tvTotalHour;
        TextView tvTotalSpent;
        TextView tvMemberSince;


        public RelativeLayout rlMain;
        OnItemClickListener onItemClickListener;

        ProgressBar progressBar;

        public ViewHolder2(View view, OnItemClickListener onItemClickListener) {
            super(view);
            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            tvCountryName = (TextView) view.findViewById(R.id.tvCountryName);
            tvStatus = (TextView) view.findViewById(R.id.tvStatus);
         //   tvBudget = (TextView) view.findViewById(R.id.tvBudget);

            tvJobPosted = (TextView) view.findViewById(R.id.tvJobPosted);
            tvJobFilled = (TextView) view.findViewById(R.id.tvJobFilled);
            tvTotalHour = (TextView) view.findViewById(R.id.tvTotalHour);
            tvTotalSpent = (TextView) view.findViewById(R.id.tvTotalSpent);
            tvMemberSince = (TextView) view.findViewById(R.id.tvMemberSince);
            rlMain = (RelativeLayout) view.findViewById(R.id.rlMain);
            view.setOnClickListener(this);
            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public void onClick(View v) {
            // final Jobs jobDescription = bookingList.get (getLayoutPosition ());
            // activity.overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);
            mItemClickListener.onItemClick(v, getLayoutPosition());

        }
    }
}