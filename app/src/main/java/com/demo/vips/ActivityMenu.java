package com.demo.vips;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ActivityMenu extends AppCompatActivity {
    public static final String DATABASE_NUMBER = "ContactDatabases.db";
    //a list to store all the products
    List<com.demo.vips.Contact> contactList;
    RecyclerView recyclerView;
    SQLiteDatabase mDatabase;
    ContactAdapter adapter;
    EditText e_number;
    Button bt_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.demo.vips.R.layout.activity_menu);

        //getting the recyclerview from xml
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDatabase = openOrCreateDatabase(DATABASE_NUMBER, MODE_PRIVATE, null);
        createNumberTable();
        showNumbersFromDatabase();

        e_number = (EditText) findViewById(R.id.e_Number);
        bt_save = (Button) findViewById(R.id.btn_save);

        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Get the Enter data
                String number = e_number.getText().toString().trim();


                if (number.isEmpty() ) {

                    Toast.makeText(ActivityMenu.this, "Fil the field", Toast.LENGTH_SHORT).show();

                } else {

                    String insertSQL = "INSERT INTO Contact \n" +
                            "(Number)\n" +
                            "VALUES \n" +
                            "(?);";

                    //using the same method execsql for inserting values
                    //this time it has two parameters
                    //first is the sql string and second is the parameters that is to be binded with the query
                    mDatabase.execSQL(insertSQL, new String[]{number});
                    Toast.makeText(ActivityMenu.this, "Great! Data Saved", Toast.LENGTH_SHORT).show();
                    adapter.reloadContactsFromDatabase();
                }

            }
        });
    }

    private void showNumbersFromDatabase() {
        //we used rawQuery(sql, selectionargs) for fetching all the employees
        Cursor cursorproduct = mDatabase.rawQuery("SELECT * FROM Contact", null);
        List<com.demo.vips.Contact> contactList = new ArrayList<>();

        //if the cursor has some data
        if (cursorproduct.moveToFirst()) {
            //looping through all the records
            do {
                //pushing each record in the employee list
                contactList.add(new com.demo.vips.Contact(
                        cursorproduct.getInt(0),
                        cursorproduct.getString(1)));
            } while (cursorproduct.moveToNext());
        }
        if (contactList.isEmpty()) {
            Toast.makeText(this, "No number Found in database", Toast.LENGTH_SHORT).show();
        }
        //closing the cursor
        cursorproduct.close();

        //creating the adapter object
        adapter = new ContactAdapter(this, R.layout.custom_contact_item, contactList, mDatabase);

        //adding the adapter to listview
        recyclerView.setAdapter(adapter);

        adapter.reloadContactsFromDatabase();  //this method is in prdouctadapter

    }

    private void createNumberTable() {
        mDatabase.execSQL("CREATE TABLE IF NOT EXISTS Contact " +
                "(\n" +
                "    id INTEGER NOT NULL CONSTRAINT employees_pk PRIMARY KEY AUTOINCREMENT,\n" +
                "    Number varchar(200) NOT NULL\n" +
                ");"

        );
    }
}
