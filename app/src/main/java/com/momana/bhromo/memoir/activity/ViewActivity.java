package com.momana.bhromo.memoir.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.momana.bhromo.memoir.R;
import com.momana.bhromo.memoir.database.NotesDatabase;
import com.momana.bhromo.memoir.model.Note;
import com.onegravity.rteditor.RTEditText;
import com.onegravity.rteditor.RTManager;
import com.onegravity.rteditor.api.RTApi;
import com.onegravity.rteditor.api.RTMediaFactoryImpl;
import com.onegravity.rteditor.api.RTProxyImpl;

import java.util.Calendar;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ViewActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    public static final String NOTE_ID_KEY = "note_id";
    public static final String ACTION_TYPE_KEY = "action_type";
    public static final int ACTION_TYPE_NEW = 0;
    public static final int ACTION_TYPE_EDIT = 1;

    private static final int PLACE_PICKER_REQUEST = 42;
    private static final int EDIT_BODY_REQUEST = 43;

    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private static final int PERMISSION_ACCESS_FINE_LOCATION = 12;

    private GoogleApiClient mGoogleApiClient;
    private RTManager rtManager;

    private Note note;

    @Bind(R.id.toolbar)             Toolbar toolbar;
    @Bind(R.id.date_dayOfMonth)     TextView dayOfMonth;
    @Bind(R.id.date_dayOfWeek)      TextView dayOfWeek;
    @Bind(R.id.date_monthAndYear)   TextView monthAndYear;
    @Bind(R.id.time_hhmm)           TextView timeHHMM;
    @Bind(R.id.time_tt)             TextView timeTT;
    @Bind(R.id.note_title)          TextView noteTitle;
    @Bind(R.id.note_body)           RTEditText noteBody;
    @Bind(R.id.location_holder)     View locationHolder;
    @Bind(R.id.location_text)       TextView locationText;
    @Bind(R.id.background_tint)     View backgroundTint;
    @Bind(R.id.progress_circle)     View progressCircle;
    @Bind(R.id.loading_text)        View loadingText;

    // Activity Lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        ButterKnife.bind(this);

        // Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Setup RTEditing
        RTApi rtApi = new RTApi(this, new RTProxyImpl(this), new RTMediaFactoryImpl(this, true));
        rtManager = new RTManager(rtApi, savedInstanceState);
        rtManager.registerEditor(noteBody, true);

        // Setup UI
        int actionType = getIntent().getIntExtra(ACTION_TYPE_KEY, ACTION_TYPE_NEW);
        String noteId;
        if (actionType == ACTION_TYPE_EDIT) {
            noteId = getIntent().getStringExtra(NOTE_ID_KEY);
            note = NotesDatabase.getInstance(this).getNoteByID(noteId);
            refreshLayout();
        } else {
            noteId = UUID.randomUUID().toString();
            note = new Note(noteId, "Untitled", "");
            NotesDatabase.getInstance(this).addNote(note);
            refreshLayout();
        }

        // Google API Client
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, null)
                .build();
        mGoogleApiClient.connect();
    }
    @Override
    protected void onResume() {
        super.onResume();
        backgroundTint.setVisibility(View.GONE);
        progressCircle.setVisibility(View.GONE);
        loadingText.setVisibility(View.GONE);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        rtManager.onDestroy(isFinishing());
        mGoogleApiClient.disconnect();
        ButterKnife.unbind(this);
    }
    @Override
    protected void onStop() {
        NotesDatabase.getInstance(this).commit();
        super.onStop();
    }

    // Helper methods
    public void refreshLayout() {
        dayOfMonth.setText(note.getDayOfMonth());
        dayOfWeek.setText(note.getDayOfWeek());
        monthAndYear.setText(note.getMonthAndYear());

        timeHHMM.setText(note.getHourAndMin());
        timeTT.setText(note.getAMPM());

        if (note.title.length() == 0) {
            noteTitle.setText("Tap to Enter Title");
        } else {
            noteTitle.setText(note.title);
        }

        if (note.body.length() == 0) {
            noteBody.setText("Click here to edit...");
        } else {
            noteBody.setRichTextEditing(true, note.body);
        }

        if (note.location.placeName.length() == 0) {
            locationText.setText(R.string.view_note_location);
        } else {
            locationText.setText(note.location.placeName);
        }

        backgroundTint.setVisibility(View.GONE);
        progressCircle.setVisibility(View.GONE);
        loadingText.setVisibility(View.GONE);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        rtManager.onSaveInstanceState(outState);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace(this, data);
            note.setLocation(place);
            refreshLayout();
        } else if (requestCode == EDIT_BODY_REQUEST && resultCode == RESULT_OK) {
            note.body = data.getStringExtra(EditActivity.NOTE_BODY_KEY);
            refreshLayout();
        }
    }

    // Toolbar Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_delete:
                new AlertDialog.Builder(this)
                        .setTitle("Delete Note")
                        .setMessage("Are you sure you want to delete the note?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NotesDatabase.getInstance(getApplicationContext()).deleteNoteWithId(note.id);
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
                return true;

            case R.id.action_share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, note.title);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, note.body);
                startActivity(Intent.createChooser(sharingIntent, "Share using..."));
                return true;

            default:
                return false;
        }
    }

    // Location functions
    public boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ignored) {}
        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ignored) {}

        return !(!gps_enabled && !network_enabled);
    }
    public void getCurrentLocation() {
        try {
            // On Marshmallow request for permissions first
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSION_ACCESS_FINE_LOCATION);
                } else {
                    return;
                }
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                            PERMISSION_ACCESS_COARSE_LOCATION);
                } else {
                    return;
                }
            }

            PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(mGoogleApiClient, null);
            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                @Override
                public void onResult(@NonNull PlaceLikelihoodBuffer likelyPlaces) {
                    Place place = null;
                    float max = 0;
                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                        float likelihood = placeLikelihood.getLikelihood();
                        if (likelihood > max) {
                            place = placeLikelihood.getPlace();
                            max = placeLikelihood.getLikelihood();
                        }
                    }
                    if (place == null) {
                        Toast.makeText(getApplicationContext(), R.string.note_location_unable, Toast.LENGTH_SHORT).show();
                    } else {
                        note.setLocation(place);
                        refreshLayout();
                    }
                    likelyPlaces.release();
                }
            });
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), R.string.note_location_unable, Toast.LENGTH_SHORT).show();
        }
    }

    // Click Methods
    @OnClick(R.id.date_holder)
    public void onDateClicked() {
        DatePickerDialog.newInstance(this, note.calendar.get(Calendar.YEAR),
                note.calendar.get(Calendar.MONTH), note.calendar.get(Calendar.DAY_OF_MONTH))
                .show(getFragmentManager(), "datePicker");
    }
    @OnClick(R.id.time_holder)
    public void onTimeClicked() {
        TimePickerDialog.newInstance(this, note.calendar.get(Calendar.HOUR_OF_DAY),
                note.calendar.get(Calendar.MINUTE), true).show(getFragmentManager(), "timePicker");
    }
    @OnClick(R.id.note_title)
    public void onNoteTitleClicked() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.view_note_title);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_title_edit, (ViewGroup) findViewById(android.R.id.content), false);
        builder.setView(viewInflated);

        final EditText nameEditText = (EditText) viewInflated.findViewById(R.id.edit_name);
        if (note.title.length() > 0) {
            nameEditText.setText(note.title);
        }

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = nameEditText.getText().toString().trim();
                if (name.length() == 0) {
                    Toast.makeText(getApplicationContext(), R.string.note_title_error, Toast.LENGTH_SHORT).show();
                } else {
                    note.title = nameEditText.getText().toString();
                    refreshLayout();
                    dialog.dismiss();
                }
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

        nameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                nameEditText.post(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(nameEditText, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
            }
        });
        nameEditText.requestFocus();
    }
    @OnClick(R.id.edit_button)
    public void onNoteBodyClicked() {
        progressCircle.setVisibility(View.GONE);
        backgroundTint.setVisibility(View.VISIBLE);
        loadingText.setVisibility(View.VISIBLE);

        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra(EditActivity.NOTE_BODY_KEY, note.body);
        startActivityForResult(intent, EDIT_BODY_REQUEST);
    }
    @OnClick(R.id.location_holder)
    public void onLocationClicked() {
        final Activity activity = this;
        PopupMenu popupMenu = new PopupMenu(this, locationHolder);
        if (note.location.placeName.length() == 0) {
            popupMenu.inflate(R.menu.menu_location);
        } else {
            popupMenu.inflate(R.menu.menu_location_remove);
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.location_remove:
                        new AlertDialog.Builder(activity)
                                .setTitle("Remove Location")
                                .setMessage("Are you sure you want to remove the location?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        note.location.placeName = "";
                                        note.location.latitude = 0;
                                        note.location.latitude = 0;
                                        refreshLayout();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, null)
                                .show();
                        return true;

                    case R.id.location_detect:
                        if (!isLocationEnabled()) {
                            Toast.makeText(getApplicationContext(), R.string.note_location_enable, Toast.LENGTH_SHORT).show();
                            Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(onGPS);
                        } else if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {
                            Toast.makeText(getApplicationContext(), R.string.location_play_error, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.location_wait, Toast.LENGTH_SHORT).show();
                            getCurrentLocation();
                        }
                        return true;

                    case R.id.location_select:
                        backgroundTint.setVisibility(View.VISIBLE);
                        progressCircle.setVisibility(View.VISIBLE);
                        loadingText.setVisibility(View.GONE);
                        try {
                            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                            startActivityForResult(builder.build(activity), PLACE_PICKER_REQUEST);
                        } catch (Exception ex) {
                            Toast.makeText(getApplicationContext(), R.string.location_play_error, Toast.LENGTH_SHORT).show();
                            backgroundTint.setVisibility(View.GONE);
                            progressCircle.setVisibility(View.GONE);
                        }
                        return true;

                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    // Date Time Setters
    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        note.calendar.set(year, monthOfYear, dayOfMonth);
        refreshLayout();
    }
    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        note.calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        note.calendar.set(Calendar.MINUTE, minute);
        refreshLayout();
    }
}
