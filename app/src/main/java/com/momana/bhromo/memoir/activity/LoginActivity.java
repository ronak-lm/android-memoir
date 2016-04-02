package com.momana.bhromo.memoir.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.momana.bhromo.memoir.R;
import com.momana.bhromo.memoir.database.NotesDatabase;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @Bind(R.id.password)        EditText password;
    @Bind(R.id.splash_page)     View splashPage;
    @Bind(R.id.login_page)      View loginPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (!NotesDatabase.getInstance(this).isPasswordEnabled()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
            }, 750);
        } else {
            ButterKnife.bind(this);
            splashPage.setVisibility(View.GONE);
            loginPage.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.login_button)
    public void onLoginButtonClicked() {
        String pass = password.getText().toString().trim();
        if (pass.length() == 0) {
            Toast.makeText(getApplicationContext(), R.string.pass_error_empty, Toast.LENGTH_SHORT).show();
        } else {
            if (NotesDatabase.getInstance(this).isPasswordCorrect(pass)) {
                finish();
                startActivity(new Intent(this, MainActivity.class));
            } else {
                Toast.makeText(this, R.string.login_error, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
