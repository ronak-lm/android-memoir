package com.momana.bhromo.memoir.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.momana.bhromo.memoir.R;
import com.momana.bhromo.memoir.database.NotesDatabase;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)         Toolbar toolbar;
    @Bind(R.id.switch_status)   TextView switchStatus;
    @Bind(R.id.toggle_switch)   SwitchCompat switchCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        boolean isChecked = NotesDatabase.getInstance(this).isPasswordEnabled();
        switchCompat.setChecked(isChecked);
        if (isChecked) {
            switchStatus.setText("Enabled");
        } else {
            switchStatus.setText("Disabled");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return false;
        }
    }

    @OnClick(R.id.pass_toggle)
    public void onPassToggleClicked() {
        boolean isChecked = switchCompat.isChecked();
        if (isChecked) {
            showPasswordRemoveDialog();
        } else {
            showPasswordSetDialog();
        }
    }
    public void showPasswordSetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.settings_pass);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_password_set, (ViewGroup) findViewById(android.R.id.content), false);
        builder.setView(viewInflated);

        final EditText passEditText = (EditText) viewInflated.findViewById(R.id.et_pass);
        final EditText confirmEditText = (EditText) viewInflated.findViewById(R.id.et_confirm_pass);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { }
        });
        builder.setNegativeButton(android.R.string.cancel, null);

        final AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass1 = passEditText.getText().toString().trim();
                String pass2 = confirmEditText.getText().toString().trim();
                if (pass1.length() != 0) {
                    if (pass1.equals(pass2)) {
                        NotesDatabase.getInstance(getApplicationContext()).setPassword(pass1);
                        switchStatus.setText("Enabled");
                        switchCompat.setChecked(true);
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.pass_error_match, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.pass_error_empty, Toast.LENGTH_SHORT).show();
                }
            }
        });

        passEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                passEditText.post(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(passEditText, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
            }
        });
        passEditText.requestFocus();
    }
    public void showPasswordRemoveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.settings_pass);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_password_remove, (ViewGroup) findViewById(android.R.id.content), false);
        builder.setView(viewInflated);

        final EditText passEditText = (EditText) viewInflated.findViewById(R.id.et_pass);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        final AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = passEditText.getText().toString().trim();
                if (pass.length() != 0) {
                    if (NotesDatabase.getInstance(getApplicationContext()).isPasswordCorrect(pass)) {
                        NotesDatabase.getInstance(getApplicationContext()).removePassword();
                        switchStatus.setText("Disabled");
                        switchCompat.setChecked(false);
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.login_error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.pass_error_empty, Toast.LENGTH_SHORT).show();
                }
            }
        });

        passEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                passEditText.post(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(passEditText, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
            }
        });
        passEditText.requestFocus();
    }
}
