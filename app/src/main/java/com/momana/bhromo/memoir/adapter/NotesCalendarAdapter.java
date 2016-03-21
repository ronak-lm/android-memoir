package com.momana.bhromo.memoir.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.momana.bhromo.memoir.R;
import com.momana.bhromo.memoir.database.NotesDatabase;
import com.momana.bhromo.memoir.model.Note;
import com.samsistemas.calendarview.widget.CalendarView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NotesCalendarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_CALENDAR = 1;
    private static final int VIEW_TYPE_NOTE = 2;

    private Context context;
    public static Date currentDate;
    public ArrayList<Note> notes;
    private final OnItemClickListener onItemClickListener;

    // Constructor
    public NotesCalendarAdapter(Context context, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.currentDate = new Date();
        this.notes = NotesDatabase.getInstance(context).getNotesOfDate(currentDate);
        this.onItemClickListener = onItemClickListener;
    }

    // Adapter Methods
    @Override
    public int getItemCount() {
        return notes.size() + 1;
    }
    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_CALENDAR;
        } else {
            return VIEW_TYPE_NOTE;
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_CALENDAR) {
            ViewGroup v = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar, parent, false);
            return new CalendarViewHolder(v);
        } else {
            ViewGroup v = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note_list, parent, false);
            return new NoteViewHolder(v, onItemClickListener);
        }
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_NOTE) {
            Note note = notes.get(position - 1);
            NoteViewHolder holder = (NoteViewHolder) viewHolder;
            holder.noteTitle.setText(note.title);
            holder.noteBody.setText(note.getSimpleBodyWithoutLines());
        }
    }

    // Helper Methods
    public void refreshNotes() {
        notes = NotesDatabase.getInstance(context).getNotesOfDate(currentDate);
        notifyDataSetChanged();
        if (notes.size() == 0) {
            SimpleDateFormat fmt = new SimpleDateFormat("dd MMM yyyy");
            Toast.makeText(context, "No notes found for " + fmt.format(currentDate), Toast.LENGTH_SHORT).show();
        }
    }

    // ViewHolders
    public class CalendarViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.calendar_view) CalendarView calendarView;

        public CalendarViewHolder(final ViewGroup itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            calendarView.setOnDateSelectedListener(new CalendarView.OnDateSelectedListener() {
                @Override
                public void onDateSelected(@NonNull Date date) {
                    currentDate = date;
                    refreshNotes();
                }
            });
        }
    }
    public class NoteViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.note_item)   View noteItem;
        @Bind(R.id.note_title)  TextView noteTitle;
        @Bind(R.id.note_body)   TextView noteBody;

        public NoteViewHolder(final ViewGroup itemView, final OnItemClickListener onItemClickListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            noteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClicked(getAdapterPosition());
                }
            });
            noteItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onItemClickListener.onLongClicked(view, getAdapterPosition());
                    return true;
                }
            });
        }
    }

    // Click Listener Interface
    public interface OnItemClickListener {
        void onItemClicked(int position);
        void onLongClicked(View view, int position);
    }
}