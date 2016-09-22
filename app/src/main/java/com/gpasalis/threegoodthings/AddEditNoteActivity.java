package com.gpasalis.threegoodthings;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;
import java.util.regex.Pattern;

public class AddEditNoteActivity extends AppCompatActivity {

    Note note;
    private static final int MODE_CREATE = 1;
    private static final int MODE_EDIT = 2;

    private int mode;
    private EditText textDate;
    private EditText textExp1;
    private EditText textExp2;
    private EditText textExp3;

    private boolean needRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_note);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.textDate = (EditText)this.findViewById(R.id.date);
        this.textExp1 = (EditText)this.findViewById(R.id.experience_1);
        this.textExp2 = (EditText)this.findViewById(R.id.experience_2);
        this.textExp3 = (EditText)this.findViewById(R.id.experience_3);

        Intent intent = this.getIntent();
        this.note = (Note) intent.getSerializableExtra("note");
        if(note == null)  {
            this.mode = MODE_CREATE;
            getSupportActionBar().setTitle("Νέα Καταχώρηση");
        } else  {
            this.mode = MODE_EDIT;
            getSupportActionBar().setTitle("Επεξεργασία Καταχώρησης");
            this.textDate.setText(note.getDate());
            this.textExp1.setText(note.getExp1());
            this.textExp2.setText(note.getExp2());
            this.textExp3.setText(note.getExp3());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id==android.R.id.home) {
            this.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    // User Click on the Save button.
    public void buttonSaveClicked(View view)  {
        MyDatabaseHelper db = new MyDatabaseHelper(this);

        String date = this.textDate.getText().toString();
        String exp_1 = this.textExp1.getText().toString();
        String exp_2 = this.textExp2.getText().toString();
        String exp_3 = this.textExp3.getText().toString();

        // Check all fields not to be empty
        if(date.equals("") || exp_1.equals("") || exp_2.equals("") || exp_3.equals("")) {
            Toast.makeText(getApplicationContext(),
                    "Συμπλήρωσε όλα τα πεδία...", Toast.LENGTH_LONG).show();
            return;
        }

        // Chech Date Format
        boolean isCorrectFormat = date.matches("\\d\\d\\-\\d\\d\\-\\d\\d\\d\\d");
        if (!isCorrectFormat) {
            Toast.makeText(getApplicationContext(),
                    "Συμπλήρωσε την ημερομηνία με την εξής μορφή: ηη-μμ-εεεε", Toast.LENGTH_LONG).show();
            return;
        }

        // Check and prevent false Date
        final Pattern pattern = Pattern.compile("[-\\*'\"]");
        final String[] result = pattern.split(date);
        Log.w("DayPattern", result[0]);
        int day = Integer.parseInt(result[0]);
        Log.w("MonthPattern", result[1]);
        int month = Integer.parseInt(result[1]);
        Log.w("YearPattern", result[2]);
        int year = Integer.parseInt(result[2]);

        if (day <= 0 || day > 31 || month <= 0 || month > 12 || year <= 0){
            Toast.makeText(getApplicationContext(),
                    "Λανθασμένη ημερομηνία!", Toast.LENGTH_LONG).show();
            return;
        }


        // Check if the same date exists
        if (mode == MODE_CREATE) {
            if (db.sameDateExists(date)) {
                Toast.makeText(getApplicationContext(),
                        "Η ημερομηνία " + date + " είναι ήδη καταχωρημένη. Δήλωσε διαφορετική ημερομηνία.", Toast.LENGTH_LONG).show();
                return;
            }
        } else {
            if (!date.equals(note.getDate()) && db.sameDateExists(date) ){
                Toast.makeText(getApplicationContext(),
                        "Η ημερομηνία " + date + " είναι ήδη καταχωρημένη. Δήλωσε διαφορετική ημερομηνία.", Toast.LENGTH_LONG).show();
                return;
            }
        }


        if(mode == MODE_CREATE ) {
            this.note = new Note(date, exp_1, exp_2, exp_3);
            db.addNote(note);
        } else  {
            this.note.setDate(date);
            this.note.setExp1(exp_1);
            this.note.setExp2(exp_2);
            this.note.setExp3(exp_3);
            db.updateNote(note);
            Toast.makeText(getApplicationContext(), "Η καταχώρηση ενημερώθηκε",Toast.LENGTH_LONG).show();
        }

        this.needRefresh = true;
        // Back to MainActivity.
        this.onBackPressed();
    }

    // User Click on the Cancel button.
    public void buttonCancelClicked(View view)  {
        // Do nothing, back MainActivity.
        this.onBackPressed();
    }

    // When completed this Activity,
    // Send feedback to the Activity called it.
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
