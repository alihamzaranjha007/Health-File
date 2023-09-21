package com.example.healthfile;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.imageViewHolder> implements Filterable {

    private Context mContext;
    private List<Upload> mUpload;
    private List<Upload> mUploadAll;

    private OnItemClickListener listener;

    //s
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public ImageAdapter(Context context, List<Upload> uploads){
        mContext= context;
        this.mUpload= uploads;
        this.mUploadAll= new ArrayList<>(mUpload);
    }
    public void setOnItemClickListener(OnItemClickListener clickListener){
        listener= clickListener;
    }
//end
    @Override
    public imageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(mContext).inflate(R.layout.image_item, parent, false);
        return new imageViewHolder(v,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull imageViewHolder holder, int position) {
        Upload uploadCurrent= mUpload.get(position);
        holder.textViewReport.setText(uploadCurrent.getrType());
        holder.textViewHospital.setText(uploadCurrent.gethName());
        holder.textViewDoctor.setText(uploadCurrent.getdName());
        holder.textViewDate.setText(uploadCurrent.getvDate());
        holder.textViewRemarks.setText(uploadCurrent.getrMarks());


        Picasso.get()
                .load(uploadCurrent.getmImgUrl())
                .placeholder(R.drawable.ic_menu_gallery)
                .fit()
                .centerCrop()
                .into(holder.imageView);
        //
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,zoomImage.class);
                intent.putExtra("zoomImage",uploadCurrent.getmImgUrl());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mUpload.size();
    }




    //to filter
    @Override
    public Filter getFilter() {
        return filter;
    }
    Filter filter= new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            List<Upload> filteredList= new ArrayList<>();
            if (constraint.toString().isEmpty()){
                filteredList.addAll(mUploadAll);
            } else {
                for (Upload data: mUploadAll){
                    if (data.getrType().toLowerCase().contains(constraint.toString().trim().toLowerCase())){
                        filteredList.add(data);
                    }
                }
            }

            FilterResults filterResults= new FilterResults();
            filterResults.values= filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mUpload.clear();
            mUpload.addAll((Collection<? extends Upload>) results.values);
            notifyDataSetChanged();
        }
    };
    //filter end






    public class imageViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewReport;
        public TextView textViewHospital;
        public TextView textViewDoctor;
        public TextView textViewDate;
        public TextView textViewRemarks;
        public ImageView imageView;
        public Button delBTN;


        public imageViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            textViewReport= itemView.findViewById(R.id.reportName);
            textViewHospital= itemView.findViewById(R.id.hospitalName);
            textViewDoctor= itemView.findViewById(R.id.doctorName);
            textViewDate= itemView.findViewById(R.id.visitingDate);
            textViewRemarks= itemView.findViewById(R.id.remarks);
            imageView= itemView.findViewById(R.id.image_view_upload);
            delBTN= itemView.findViewById(R.id.deleteBTN);

            delBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(getBindingAdapterPosition());
                }
            });

        }

    }
}


