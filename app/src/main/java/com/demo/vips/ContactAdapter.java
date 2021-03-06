package com.demo.vips;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ProductViewHolder> {

    int custom_list_item;
    SQLiteDatabase mDatabase;


    //this context we will use to inflate the layout
    private Context mCtx;

    //we are storing all the products in a list
    private List<com.demo.vips.Contact> contactList;

    //getting the context and product list with constructor
    public ContactAdapter(Context mCtx, int custom_list_item, List<com.demo.vips.Contact> contactList, SQLiteDatabase mDatabase) {
        this.mCtx = mCtx;
        this.custom_list_item = custom_list_item;
        this.mDatabase = mDatabase;
        this.contactList = contactList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(com.demo.vips.R.layout.custom_contact_item, null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        //getting the product of the specified position
        final com.demo.vips.Contact contact = contactList.get(position);

        //binding the data with the viewholder views
        holder.textViewNumber.setText(contact.getNumber());

        holder.editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateContact(contact);
            }
        });

        holder.deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);
                builder.setTitle("Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String sql = "DELETE FROM Contact WHERE id = ?";
                        mDatabase.execSQL(sql, new Integer[]{contact.getId()});
                        Toast.makeText(mCtx, "Deleted successfully!", Toast.LENGTH_SHORT).show();

                        reloadContactsFromDatabase(); //Reload List
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


    }
    void reloadContactsFromDatabase() {
        Cursor cursorproduct1 = mDatabase.rawQuery("SELECT * FROM Contact", null);
        if (cursorproduct1.moveToFirst()) {
            contactList.clear();
            do {
                contactList.add(new com.demo.vips.Contact(
                        cursorproduct1.getInt(0),
                        cursorproduct1.getString(1)));
            } while (cursorproduct1.moveToNext());
        }
        cursorproduct1.close();
        notifyDataSetChanged();
    }
    private void updateContact(final com.demo.vips.Contact contact) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);

        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.dialog_update_contact, null);
        builder.setView(view);

        final EditText editTextNumber = view.findViewById(R.id.editTextNumber);

        editTextNumber.setText(contact.getNumber());

        final AlertDialog dialog = builder.create();
        dialog.show();

        // CREATE METHOD FOR EDIT THE FORM
        view.findViewById(R.id.buttonUpdateEmployee).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = editTextNumber.getText().toString().trim();

                if (number.isEmpty()) {
                    editTextNumber.setError("Number can't be blank");
                    editTextNumber.requestFocus();
                    return;
                }

                String sql = "UPDATE Contact \n" +
                        "SET Number = ?\n" +
                        "WHERE id = ?;\n";

                mDatabase.execSQL(sql, new String[]{number, String.valueOf(contact.getId())});
                Toast.makeText(mCtx, "Number Updated successfully!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                notifyDataSetChanged();
                reloadContactsFromDatabase();
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }


    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView textViewNumber;
        ImageView editbtn, deletebtn;

        public ProductViewHolder(View itemView) {
            super(itemView);

            textViewNumber = itemView.findViewById(R.id.textViewNumber);

            deletebtn = itemView.findViewById(R.id.buttonDeleteContact);
            editbtn = itemView.findViewById(R.id.buttonEditstudent);
        }
    }
}
