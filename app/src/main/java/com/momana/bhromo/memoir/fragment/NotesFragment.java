package com.momana.bhromo.memoir.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.momana.bhromo.memoir.R;
import com.momana.bhromo.memoir.activity.ViewActivity;
import com.momana.bhromo.memoir.adapter.NoteGridAdapter;
import com.momana.bhromo.memoir.database.NotesDatabase;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NotesFragment extends Fragment implements NoteGridAdapter.OnItemClickListener {

    private NoteGridAdapter adapter;

    @Bind(R.id.note_grid)           RecyclerView noteGrid;
    @Bind(R.id.placeholder_text)    TextView placeholderText;

    // Fragment Lifecycle
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notes, container, false);
        ButterKnife.bind(this, v);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(getNumberOfColumns(), 1);
        adapter = new NoteGridAdapter(NotesDatabase.getInstance(getContext()).getNotes(), this);
        noteGrid.setHasFixedSize(true);
        noteGrid.setLayoutManager(layoutManager);
        noteGrid.setAdapter(adapter);

        return v;
    }
    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        refreshLayout();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    // Helper Method
    public void refreshLayout() {
        if (adapter.notes.isEmpty()) {
            noteGrid.setVisibility(View.GONE);
            placeholderText.setVisibility(View.VISIBLE);
        } else {
            noteGrid.setVisibility(View.VISIBLE);
            placeholderText.setVisibility(View.GONE);
        }
    }
    public int getNumberOfColumns() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float widthPx = displayMetrics.widthPixels;
        float desiredPx = getResources().getDimensionPixelSize(R.dimen.notes_card_width);
        int columns = Math.round(widthPx / desiredPx);
        return columns > 2 ? columns : 2;
    }

    // OnClick for Recycler Item
    @Override
    public void onItemClicked(int position) {
        Intent intent = new Intent(getActivity(), ViewActivity.class);
        intent.putExtra(ViewActivity.ACTION_TYPE_KEY, ViewActivity.ACTION_TYPE_EDIT);
        intent.putExtra(ViewActivity.NOTE_ID_KEY, adapter.notes.get(position).id);
        startActivity(intent);
    }
    @Override
    public void onLongClicked(View view, final int position) {
        final Context context = getContext();
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.menu_delete);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.note_delete:
                        new AlertDialog.Builder(context)
                                .setTitle("Delete Note")
                                .setMessage("Are you sure you want to delete the note?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        NotesDatabase.getInstance(context).deleteNoteAt(position);
                                        adapter.notifyItemRemoved(position);
                                        refreshLayout();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, null)
                                .show();
                        return true;

                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }
}
