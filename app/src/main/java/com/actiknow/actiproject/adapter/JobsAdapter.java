package com.actiknow.actiproject.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actiknow.actiproject.R;
import com.actiknow.actiproject.dialogFragment.JobDetailFragment;
import com.actiknow.actiproject.model.Jobs;

import com.actiknow.actiproject.utils.AppConfigTags;
import com.actiknow.actiproject.utils.UserDetailsPref;
import com.actiknow.actiproject.utils.Utils;


import java.util.ArrayList;
import java.util.List;

public class JobsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    OnItemClickListener mItemClickListener;
    private Activity activity;
    private ArrayList<Jobs> jobsList = new ArrayList<Jobs>();
    ProgressBar progressDialog;
    OnItemClickListener onItemClickListener;
    OnLoadMoreListener loadMoreListener;
    boolean isLoading = false, isMoreDataAvailable = true;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    int type = 0;
    UserDetailsPref userDetailsPref;

    public JobsAdapter(Activity activity, ArrayList<Jobs> jobsList, int type) {
        this.activity = activity;
        this.jobsList = jobsList;
        this.type = type;
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
                viewHolder2.tvCountryName.setText(jobs.getCountry() + ", " + jobs.getBudget() + ", " + jobs.getTotal_spent());
            } else {
                viewHolder2.tvCountryName.setText(jobs.getCountry() + ", " + jobs.getBudget() + ", " + "NA");
            }
            viewHolder2.tvStatus.setText(jobs.getSnippet());
            viewHolder2.tvJobPosted.setText(jobs.getJob_post() + "  |  "+ jobs.getTotal_job_filled() + "  |  " + jobs.getClient_job_percent());
            viewHolder2.tvTotalHour.setText(jobs.getTotal_hours() + "  |  "+jobs.getClient_member_since());
            viewHolder2.tvSnippet.setText(jobs.getSnippet());
            viewHolder2.tvRejectedBy.setVisibility(View.GONE);
            if(type == 2) {
                viewHolder2.tvRejectedBy.setVisibility(View.VISIBLE);
                viewHolder2.tvRejectedBy.setText("Rejected By: " + jobs.getRejected_by());
            }else if (type == 1){
                viewHolder2.tvRejectedBy.setVisibility(View.VISIBLE);
                viewHolder2.tvRejectedBy.setText("Accepted By: " + jobs.getRejected_by());
            }
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
        TextView tvTotalHour;
        TextView tvSnippet;
        TextView tvRejectedBy;


        public RelativeLayout rlMain;
        OnItemClickListener onItemClickListener;

        ProgressBar progressBar;

        public ViewHolder2(View view, OnItemClickListener onItemClickListener) {
            super(view);
            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            tvCountryName = (TextView) view.findViewById(R.id.tvCountryName);
            tvStatus = (TextView) view.findViewById(R.id.tvStatus);
            tvJobPosted = (TextView) view.findViewById(R.id.tvJobPosted);
            tvTotalHour = (TextView) view.findViewById(R.id.tvTotalHour);
            tvSnippet = (TextView) view.findViewById(R.id.tvSnippet);
            tvRejectedBy = (TextView) view.findViewById(R.id.tvRejectedBy);
            rlMain = (RelativeLayout) view.findViewById(R.id.rlMain);
            view.setOnClickListener(this);
            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public void onClick(View v) {
            userDetailsPref = UserDetailsPref.getInstance();
            // final Jobs jobDescription = bookingList.get (getLayoutPosition ());
            // activity.overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);
            if(mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getLayoutPosition());
            }else{
                Jobs jobs = jobsList.get(getLayoutPosition());
                android.app.FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
                JobDetailFragment dialog = new JobDetailFragment().newInstance(jobs.getId(), jobs.getJob_id(), 0, jobsList);
                dialog.setOnDialogResultListener(new JobDetailFragment.OnDialogResultListener() {
                    @Override
                    public void onDismiss() {
                        if(userDetailsPref.getIntPref(activity, AppConfigTags.POSITION) != -1){
                            removeItem(userDetailsPref.getIntPref(activity, AppConfigTags.POSITION));
                            notifyDataChanged();
                        }
                        userDetailsPref.putIntPref(activity, AppConfigTags.POSITION, -1);
                    }
                });
                dialog.show(ft, "jobs");
            }
        }
    }
}