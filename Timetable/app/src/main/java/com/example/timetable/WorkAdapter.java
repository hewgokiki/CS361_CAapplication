package com.example.timetable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WorkAdapter extends ListAdapter<Work, WorkAdapter.WorkHolder> {

    private OnItemClickListener listener;

    protected WorkAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Work> DIFF_CALLBACK = new DiffUtil.ItemCallback<Work>() {
        @Override
        public boolean areItemsTheSame(@NonNull Work oldItem, @NonNull Work newItem) {
            Log.d("ItemsSame", "True");
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Work oldItem, @NonNull Work newItem) {
            Log.d("ContentsSame", "True");
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getDescription().equals(newItem.getDescription()) &&
                    oldItem.getDue().equals(newItem.getDue());
        }
    };

    @NonNull
    @Override
    public WorkHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.work_item, parent, false);
        return new WorkHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkHolder holder, int position) {
        Work currentWork = getItem(position);
        holder.textViewTitle.setText(currentWork.getTitle());
        holder.textViewCode.setText(currentWork.getClassCode());
        holder.textViewDescription.setText(currentWork.getDescription());
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        try {
            Date date = sdf.parse(currentWork.getDue().toString());
            SimpleDateFormat sdf2=new SimpleDateFormat("dd/M/yyyy");
            holder.textViewDue.setText(sdf2.format(date.getTime()));
            SimpleDateFormat sdf3=new SimpleDateFormat("EEE-MMM-dd");
            String [] dates = (sdf3.format(date.getTime())).split("-");
            holder.textViewDay.setText(dates[0]);
            holder.textViewDate.setText(dates[2]);
            holder.textViewMonth.setText(dates[1]);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public Work getWorkAt(int position){
        return getItem(position);
    }

    class WorkHolder extends  RecyclerView.ViewHolder{
        private TextView textViewTitle;
        private TextView textViewCode;
        private TextView textViewDescription;
        private TextView textViewDue;

        private TextView textViewDay;
        private TextView textViewDate;
        private TextView textViewMonth;

        public WorkHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewCode = itemView.findViewById(R.id.text_view_code);
            textViewDescription = itemView.findViewById(R.id.text_view_description);
            textViewDue = itemView.findViewById(R.id.text_view_due);

            textViewDay = itemView.findViewById(R.id.day);
            textViewDate = itemView.findViewById(R.id.date);
            textViewMonth = itemView.findViewById(R.id.month);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION){
                        listener.onItemClick(getItem(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(Work work);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
