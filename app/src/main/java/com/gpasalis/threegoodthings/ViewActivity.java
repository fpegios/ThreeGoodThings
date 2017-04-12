package com.gpasalis.threegoodthings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ViewActivity extends AppCompatActivity {

    Note note;
    private static final int MY_REQUEST_CODE = 1000;
    private boolean needRefresh;

    private TextView textDate;
    private TextView textExp1;
    private TextView textExp2;
    private TextView textExp3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("View Experience");

        this.textDate = (TextView)this.findViewById(R.id.date);
        this.textExp1 = (TextView)this.findViewById(R.id.experience_1);
        this.textExp2 = (TextView)this.findViewById(R.id.experience_2);
        this.textExp3 = (TextView)this.findViewById(R.id.experience_3);

        Intent intent = this.getIntent();
        this.note = (Note) intent.getSerializableExtra("note");
        this.textDate.setText(note.getDate());
        this.textExp1.setText(note.getExp1());
        this.textExp2.setText(note.getExp2());
        this.textExp3.setText(note.getExp3());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id==android.R.id.home) {
            this.onBackPressed();
        } else if (id == R.id.action_exit) {
            showExitDialog();
            return true;
        } else if (id == R.id.action_delete) {
            MyDatabaseHelper db = new MyDatabaseHelper(this);
            db.deleteNote(note);
            MainActivity.noteList.remove(note);
            Toast.makeText(getApplicationContext(), "Experience was deleted",Toast.LENGTH_LONG).show();
            // Refresh ListView.
            MainActivity.listViewAdapter.notifyDataSetChanged();

            this.needRefresh = true;
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    // Update a record
    public void buttonUpdateClicked(View view) {
        Intent intent = new Intent(getApplicationContext(), AddEditNoteActivity.class);
        intent.putExtra("note", note);

        this.needRefresh = true;
        // Start AddEditNoteActivity, (with feedback).
        startActivityForResult(intent, MY_REQUEST_CODE);
        finish();
    }

    public void buttonCancelClicked(View view)  {
        // Do nothing, back MainActivity.
        this.onBackPressed();
    }

    private void showExitDialog() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setIcon(getApplicationInfo().loadIcon(getPackageManager()));
        builder.setTitle("Exit");
        builder.setMessage("Are you sure you want to close the application?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                System.exit(0);
            }

        });
        builder.setNegativeButton("NO", null);
        builder.show();
    }

    @Override
    public void finish() {

        // Create Intent
        Intent data = new Intent();

        // Request MainActivity refresh its ListView (or not).
        data.putExtra("needRefresh", needRefresh);

        // Set Result
        this.setResult(Activity.RESULT_OK, data);
        super.finish();
    }
}
