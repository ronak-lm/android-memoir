package com.momana.bhromo.memoir.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.momana.bhromo.memoir.R;
import com.momana.bhromo.memoir.model.Note;
import com.momana.bhromo.memoir.widget.AutoResizeTextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NoteGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public ArrayList<Note> notes;
    private final OnItemClickListener onItemClickListener;

    public NoteGridAdapter(ArrayList<Note> notes, OnItemClickListener onItemClickListener) {
        this.notes = notes;
        this.onItemClickListener = onItemClickListener;
    }

    // Adapter Methods
    @Override
    public int getItemCount() {
        return notes.size();
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup v = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note_grid, parent, false);
        return new NoteViewHolder(v, onItemClickListener);
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Note note = notes.get(position);
        NoteViewHolder holder = (NoteViewHolder) viewHolder;
        holder.noteTitle.setText(note.title);
        holder.noteBody.setText(note.getSimpleBody());
    }

    // ViewHolder for Notes
    public class NoteViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.note_card) CardView noteCard;
        @Bind(R.id.note_title) TextView noteTitle;
        @Bind(R.id.note_body) AutoResizeTextView noteBody;

        public NoteViewHolder(final ViewGroup itemView, final OnItemClickListener onItemClickListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            noteCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClicked(getAdapterPosition());
                }
            });
            noteCard.setOnLongClickListener(new View.OnLongClickListener() {
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