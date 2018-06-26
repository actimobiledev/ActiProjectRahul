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
import com.actiknow.actiproject.model.AcceptedJobs;
import com.actiknow.actiproject.model.RejectedJobs;
import com.actiknow.actiproject.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class RejectedJobsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    OnItemClickListener mItemClickListener;
    OnItemClickListener onItemClickListener;
    OnLoadMoreListener loadMoreListener;
    private Activity activity;
    private List<RejectedJobs> rejectedJobsList = new ArrayList<RejectedJobs>();
    ProgressBar progressDialog;

   // OnLoadMoreListener loadMoreListener;
    boolean isLoading = false, isMoreDataAvailable = true;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public RejectedJobsAdapter(Activity activity, List<RejectedJobs> rejectedJobsList) {
        this.activity = activity;
        this.rejectedJobsList = rejectedJobsList;
    }

    @Override
    public int getItemViewType (int position) {
        if (rejectedJobsList.get (position).getId () != 0) {
            return VIEW_TYPE_ITEM;
        } else {
            return VIEW_TYPE_LOADING;
        }
//        if (isPositionFooter (position)) {
//            return VIEW_TYPE_LOADING;
//        }
//        return VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_jobs_list, parent, false);
            return new RejectedJobsAdapter.ViewHolder2(view, mItemClickListener);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_loading, parent, false);
            return new JobsAdapter.LoadHolder(view);
        }
        return null;
    }



    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position >= getItemCount () - 1 && isMoreDataAvailable && ! isLoading && loadMoreListener != null) {
            isLoading = true;
            loadMoreListener.onLoadMore ();
        }
        if (getItemViewType (position) == VIEW_TYPE_ITEM) {
            ViewHolder2 holder2 = (ViewHolder2) holder;
            final RejectedJobs jobs = rejectedJobsList.get(position);
            progressDialog = new ProgressBar(activity);
            Utils.setTypefaceToAllViews(activity, holder2.tvTitle);
            holder2.tvTitle.setText(jobs.getTitle());


            //  Log.d("job status","no value"+jobs.getStatus());
            if (jobs.getStatus() != null && !jobs.getStatus().isEmpty() && !jobs.getStatus().equals("null")){
                if (jobs.getStatus().length() > 0) {
                    holder2.tvCountryName.setText(jobs.getCountry() + ", " + jobs.getStatus() + ", " + jobs.getBudget());
                } else {
                    holder2.tvCountryName.setText(jobs.getCountry() + ", " + "NA" + ", " + jobs.getBudget());
                }
            }else{
                holder2.tvCountryName.setText(jobs.getCountry() + ", " + "NA" + ", " + jobs.getBudget());
            }

            holder2.tvStatus.setText(jobs.getSnippet());
            //   holder.tvBudget.setText("Job Budget : " + jobs.getBudget());

            holder2.tvJobPosted.setText("Job Posted : " + jobs.getJob_post() + "  |  ");
            holder2.tvJobFilled.setText("Job Filled : " + jobs.getTotal_job_filled() + "  |  " + jobs.getClient_job_percent());
            holder2.tvTotalHour.setText("Hours : " + jobs.getTotal_hours() + "  |  ");
            holder2.tvTotalSpent.setText("Hours : " + jobs.getTotal_spent());
            holder2.tvMemberSince.setText(jobs.getClient_member_since());

        }
    }

    public void removeItem(int position) {
        rejectedJobsList.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(RejectedJobs rejectedJobs, int position) {
        rejectedJobsList.add(position, rejectedJobs);
        // notify item added by position
        notifyItemInserted(position);
    }
    @Override
    public int getItemCount() {
        return rejectedJobsList.size();
    }


    public void notifyDataChanged () {
        notifyDataSetChanged ();
        isLoading = false;
    }
    public void setMoreDataAvailable (boolean moreDataAvailable) {
        isMoreDataAvailable = moreDataAvailable;
    }

    public void setLoadMoreListener (RejectedJobsAdapter.OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    public void SetOnItemClickListener(RejectedJobsAdapter.OnItemClickListener mItemClickListener) {
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
        TextView tvBudget;
        public RelativeLayout rlMain;

        TextView tvJobPosted;
        TextView tvJobFilled;
        TextView tvTotalHour;
        TextView tvTotalSpent;
        TextView tvMemberSince;

        ProgressBar progressBar;

        public ViewHolder2(View view, OnItemClickListener onItemClickListener) {
            super(view);
            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            tvCountryName = (TextView) view.findViewById(R.id.tvCountryName);
            tvStatus = (TextView) view.findViewById(R.id.tvStatus);
            tvBudget = (TextView) view.findViewById(R.id.tvBudget);
            rlMain = (RelativeLayout) view.findViewById(R.id.rlMain);
            tvJobPosted = (TextView) view.findViewById(R.id.tvJobPosted);
            tvJobFilled = (TextView) view.findViewById(R.id.tvJobFilled);
            tvTotalHour = (TextView) view.findViewById(R.id.tvTotalHour);
            tvTotalSpent = (TextView) view.findViewById(R.id.tvTotalSpent);
            tvMemberSince = (TextView) view.findViewById(R.id.tvMemberSince);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // final Jobs jobDescription = bookingList.get (getLayoutPosition ());
            // activity.overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);
            mItemClickListener.onItemClick(v, getLayoutPosition());

        }
    }
}