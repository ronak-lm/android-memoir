package com.momana.bhromo.memoir.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.momana.bhromo.memoir.R;
import com.onegravity.rteditor.RTEditText;
import com.onegravity.rteditor.RTManager;
import com.onegravity.rteditor.RTToolbar;
import com.onegravity.rteditor.api.RTApi;
import com.onegravity.rteditor.api.RTMediaFactoryImpl;
import com.onegravity.rteditor.api.RTProxyImpl;
import com.onegravity.rteditor.api.format.RTFormat;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EditActivity extends AppCompatActivity {

    public static final String NOTE_BODY_KEY = "note_body";
    private RTManager rtManager;

    @Bind(R.id.toolbar)                 Toolbar toolbar;
    @Bind(R.id.edit_body)               RTEditText editBody;
    @Bind(R.id.rte_toolbar)             RTToolbar rtToolbar;
    @Bind(R.id.rte_toolbar_container)   ViewGroup rtToolbarContainer;

    // Activity Lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.bind(this);

        // Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Rich Text Editing
        RTApi rtApi = new RTApi(this, new RTProxyImpl(this), new RTMediaFactoryImpl(this, true));
        rtManager = new RTManager(rtApi, savedInstanceState);
        if (rtToolbar != null) {
            rtManager.registerToolbar(rtToolbarContainer, rtToolbar);
        }
        rtManager.registerEditor(editBody, true);

        // Setup Layout
        String noteBody = getIntent().getStringExtra(NOTE_BODY_KEY);
        editBody.setRichTextEditing(true, noteBody);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        rtManager.onSaveInstanceState(outState);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        rtManager.onDestroy(isFinishing());
        ButterKnife.unbind(this);
    }

    // Back button
    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        data.putExtra(NOTE_BODY_KEY, editBody.getText(RTFormat.HTML));
        setResult(RESULT_OK, data);
        super.onBackPressed();
    }

    // Toolbar Menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {

            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return false;
        }
    }
}
