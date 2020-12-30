package com.example.studybuddy;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NoticeAdapter extends FirebaseRecyclerAdapter<Notice, NoticeAdapter.NoticeHolder> {

    public NoticeAdapter(@NonNull FirebaseRecyclerOptions<Notice> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull NoticeHolder holder, int position, @NonNull Notice notice) {
        holder.Name.setText(notice.getName());
        holder.NoticeContent.setText(notice.getText());

        String date = new SimpleDateFormat("EE dd-MM-yy hh:mm a").format(new Date(notice.getTime()*1000));
        holder.Time.setText(date);
    }

    @NonNull
    @Override
    public NoticeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notice_row, parent, false);
        return new NoticeHolder(view);
    }

    class NoticeHolder extends RecyclerView.ViewHolder{

        TextView Name,NoticeContent, Time;
        public NoticeHolder(@NonNull View itemView) {
            super(itemView);

            Name = itemView.findViewById(R.id.list_tv_1);
            NoticeContent = itemView.findViewById(R.id.list_tv_2);
            Time = itemView.findViewById(R.id.list_tv_3);
        }
    }


}
