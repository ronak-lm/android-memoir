package com.momana.bhromo.memoir.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.momana.bhromo.memoir.R;
import com.momana.bhromo.memoir.activity.ViewActivity;
import com.momana.bhromo.memoir.adapter.NotesCalendarAdapter;
import com.momana.bhromo.memoir.database.NotesDatabase;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.momana.bhromo.memoir.adapter.NotesCalendarAdapter.*;

public class CalendarFragment extends Fragment implements OnItemClickListener {

    @Bind(R.id.calendar_grid) RecyclerView calendarGrid;
    private NotesCalendarAdapter adapter;

    // Fragment life cycle
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_calendar, container, false);
        ButterKnife.bind(this, v);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        adapter = new NotesCalendarAdapter(getContext(), this);
        calendarGrid.setHasFixedSize(true);
        calendarGrid.setLayoutManager(layoutManager);
        calendarGrid.setAdapter(adapter);

        return v;
    }
    @Override
    public void onResume() {
        super.onResume();
        adapter.refreshNotes();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    // Click listeners
    @Override
    public void onItemClicked(int position) {
        position--;
        Intent intent = new Intent(getActivity(), ViewActivity.class);
        intent.putExtra(ViewActivity.ACTION_TYPE_KEY, ViewActivity.ACTION_TYPE_EDIT);
        intent.putExtra(ViewActivity.NOTE_ID_KEY, adapter.notes.get(position).id);
        startActivity(intent);
    }
    @Override
    public void onLongClicked(View view, int position) {
        final int finalPosition = position - 1;
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
                                        NotesDatabase.getInstance(context).deleteNoteAt(finalPosition);
                                        adapter.notifyItemRemoved(finalPosition);
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
