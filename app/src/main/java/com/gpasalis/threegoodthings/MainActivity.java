package com.gpasalis.threegoodthings;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;

    private static final int MENU_ITEM_VIEW = 111;
    private static final int MENU_ITEM_EDIT = 222;
    private static final int MENU_ITEM_CREATE = 333;
    private static final int MENU_ITEM_DELETE = 444;

    private static final int MY_REQUEST_CODE = 1000;

    public static final List<Note> noteList = new ArrayList<Note>();
    public static ArrayAdapter<Note> listViewAdapter;
    private int notesCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.listView);
        MyDatabaseHelper db = new MyDatabaseHelper(this);

        List<Note> list =  db.getAllNotes();

        this.noteList.clear();
        this.noteList.addAll(list);

        // Define a new Adapter
        // 1 - Context
        // 2 - Layout for the row
        // 3 - ID of the TextView to which the data is written
        // 4 - the List of data

        this.listViewAdapter = new ArrayAdapter<Note>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, this.noteList);

        // Assign adapter to ListView
        this.listView.setAdapter(this.listViewAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                final Note selectedNote = (Note) listView.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), ViewActivity.class);
                intent.putExtra("note", selectedNote);
                // Start ViewActivity, (with feedback).
                startActivityForResult(intent, MY_REQUEST_CODE);
            }
        });

        // Register the ListView for Context menu
        registerForContextMenu(this.listView);


        // Alert Dialog when there are no notes
        if (db.getNotesCount() == 0) {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setIcon(getApplicationInfo().loadIcon(getPackageManager()));
            builder.setTitle("Δεν υπάρχει καταχώρηση");
            builder.setMessage("Θέλεις να προσθέσεις την πρώτη σου καταχώρηση;");
            builder.setPositiveButton("ΝΑΙ", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    createRecord();
                }

            });
            builder.setNegativeButton("ΟΧΙ", null);
            builder.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_exit) {
            showExitDialog();
            return true;
        } else if (id == R.id.action_delete) {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setIcon(getApplicationInfo().loadIcon(getPackageManager()));
            builder.setTitle("Διαγραφή όλων");
            builder.setMessage("Είστε σίγουροι πως θέλετε να διαγράψετε όλες τις καταχωρήσεις;");
            builder.setPositiveButton("ΝΑΙ", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteAllRecords();
                }

            });
            builder.setNegativeButton("ΟΧΙ", null);
            builder.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
                                    ContextMenu.ContextMenuInfo menuInfo)    {

        super.onCreateContextMenu(menu, view, menuInfo);
        menu.setHeaderTitle("Επιλογές");

        // groupId, itemId, order, title
        menu.add(0, MENU_ITEM_VIEW , 0, "Προβολή");
        menu.add(0, MENU_ITEM_CREATE , 1, "Νέα Καταχώρηση");
        menu.add(0, MENU_ITEM_EDIT , 2, "Επεξεργασία");
        menu.add(0, MENU_ITEM_DELETE, 4, "Διαγραφή");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        final Note selectedNote = (Note) this.listView.getItemAtPosition(info.position);

        if(item.getItemId() == MENU_ITEM_VIEW){
            viewRecord(selectedNote);
        }
        else if(item.getItemId() == MENU_ITEM_CREATE){
            createRecord();
        }
        else if(item.getItemId() == MENU_ITEM_EDIT ){
            updateRecord(selectedNote);
        }
        else if(item.getItemId() == MENU_ITEM_DELETE){
            deleteRecord(selectedNote);
        }
        else {
            return false;
        }
        return true;
    }

    public void onClickFAB(View view)  {
        createRecord();
    }

    // View a record
    private void viewRecord(Note note) {
        Intent intent = new Intent(getApplicationContext(), ViewActivity.class);
        intent.putExtra("note", note);
        // Start ViewActivity, (with feedback).
        startActivityForResult(intent, MY_REQUEST_CODE);
    }

    // Create a record
    private void createRecord() {
        Intent intent = new Intent(this, AddEditNoteActivity.class);
        // Start AddEditNoteActivity, (with feedback).
        this.startActivityForResult(intent, MY_REQUEST_CODE);
    }

    // Update a record
    private void updateRecord(Note note) {
        Intent intent = new Intent(getApplicationContext(), AddEditNoteActivity.class);
        intent.putExtra("note", note);
        // Start AddEditNoteActivity, (with feedback).
        startActivityForResult(intent, MY_REQUEST_CODE);
    }

    // Delete a record
    private void deleteRecord(Note note)  {
        MyDatabaseHelper db = new MyDatabaseHelper(this);
        db.deleteNote(note);
        this.noteList.remove(note);
        Toast.makeText(getApplicationContext(), "Η καταχώρηση διαγράφηκε",Toast.LENGTH_LONG).show();
        // Refresh ListView.
        listViewAdapter.notifyDataSetChanged();
    }

    // Delete all records
    private void deleteAllRecords() {
        MyDatabaseHelper db = new MyDatabaseHelper(this);
        db.deleteAllRecords();
        Toast.makeText(getApplicationContext(), "Όλες οι καταχωρήσεις διαγράφηκαν",Toast.LENGTH_LONG).show();
        // Refresh ListView.
        this.noteList.clear();
        List<Note> list=  db.getAllNotes();
        this.noteList.addAll(list);

        // Notify the data change (To refresh the ListView).
        this.listViewAdapter.notifyDataSetChanged();
    }

    private void showExitDialog() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setIcon(getApplicationInfo().loadIcon(getPackageManager()));
        builder.setTitle("Έξοδος");
        builder.setMessage("Είσαι σίγουρος πως θέλεις να κλείσεις την εφαρμογή;");
        builder.setPositiveButton("ΝΑΙ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                System.exit(0);
            }

        });
        builder.setNegativeButton("ΟΧΙ", null);
        builder.show();
    }

    // When AddEditNoteActivity completed, it sends feedback.
    // (If you start it using startActivityForResult ())
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == MY_REQUEST_CODE ) {
            boolean needRefresh = data.getBooleanExtra("needRefresh",true);
            // Refresh ListView
            if(needRefresh) {
                this.noteList.clear();
                MyDatabaseHelper db = new MyDatabaseHelper(this);
                List<Note> list=  db.getAllNotes();
                this.noteList.addAll(list);

                // Notify the data change (To refresh the ListView).
                this.listViewAdapter.notifyDataSetChanged();
            }
        }
    }

}