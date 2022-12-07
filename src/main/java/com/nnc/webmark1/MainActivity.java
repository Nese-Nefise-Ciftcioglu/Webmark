package com.nnc.webmark1;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    DatabaseHelper mDatabaseHelper;
    private ListView mListView;
    public static String theurl;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText newbookpopup_name, newbookpopup_link;
    private Button newbookpopup_cancel,newbookpopup_save;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle(getResources().getString(R.string.activity_name));




        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDatabaseHelper = new DatabaseHelper(this);
        mListView = (ListView) findViewById(R.id.listView);
        populateListView();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewBookmarkDialog();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_contactUs) {
            //Contact us
            Intent contactUs = new Intent (MainActivity.this, aboutUs.class);
            startActivity(contactUs);


            return true;
        }
        else if(id == R.id.action_removeAds){
            //Navigate to webmarkpro page
            toastMessage("Coming soon...");
        }
        else if(id == R.id.action_turnonadblock){
            //Navigate to webmarkpro page
            toastMessage("Coming soon...");
        }
        else if(id == R.id.action_loginbygoogle){
            //Navigate to webmarkpro page
            toastMessage("Coming soon...");
        }


        return super.onOptionsItemSelected(item);
    }

    public void createNewBookmarkDialog(){
        dialogBuilder = new AlertDialog.Builder(this);
        final View newBookPopopView=getLayoutInflater().inflate(R.layout.popup,null);
        newbookpopup_name = (EditText) newBookPopopView.findViewById(R.id.newbookpopup_name);
        newbookpopup_link = (EditText) newBookPopopView.findViewById(R.id.newbookpopup_link);
        newbookpopup_save = (Button) newBookPopopView.findViewById(R.id.saveButton);
        newbookpopup_cancel = (Button) newBookPopopView.findViewById(R.id.cancelButton);
        dialogBuilder.setView(newBookPopopView);
        dialog = dialogBuilder.create();
        dialog.show();

        newbookpopup_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //define save button here
                String name = newbookpopup_name.getText().toString();
                if(name.contains("'")){
                    //replace it with something -----------------
                    name = name.replace("'", "ߴ");
                }

                String link = newbookpopup_link.getText().toString();
                if(newbookpopup_name.length() !=0 && newbookpopup_link.length() !=0){
                    AddData(name,link);
                    newbookpopup_name.setText("");
                    newbookpopup_link.setText("");
                    populateListView();
                    dialog.dismiss();
                }
                else{
                    toastMessage("You must put something in the text field");
                }

            }
        });


        newbookpopup_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //define cancel button here


                dialog.dismiss();
            }
        });
    }

    public void AddData (String name, String link){
        boolean insertData = mDatabaseHelper.addData(name,link);
        if (insertData){
            toastMessage("Bookmark successfully added");

        }
        else{
            toastMessage("Something went wrong");
        }

    }
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    private void populateListView(){
        Log.d(TAG, "populateListView: Displaying data in the ListView");

        Cursor data = mDatabaseHelper.getData();
        Cursor dataLink = mDatabaseHelper.getData();
        ArrayList<String>listData = new ArrayList<>();
        ArrayList<String>listUrl = new ArrayList<>();


        while(data.moveToNext()){
            listData.add(data.getString(1));
            listUrl.add(data.getString(2));
        }
        //Create the list adapter
        ListAdapter adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, listData);
        mListView.setAdapter(adapter);

         mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = adapterView.getItemAtPosition(i).toString();
                Log.d(TAG, "onItemClick: You Clicked on " +name);

                Cursor data = mDatabaseHelper.getItemID(name);

                int itemID = -1;
                while(data.moveToNext()){
                    itemID = data.getInt(0);
                }
                Cursor dataLink = mDatabaseHelper.getItemLink(name);

                String itemLink ="";
                while(dataLink.moveToNext()){
                    itemLink = dataLink.getString(0);
                }
                if (itemID > -1){
                    //String theurl =listUrl.get(itemID-1);
                    Log.d(TAG, "onItemClick: The ID is: " + itemID);
                    Log.d(TAG, "onItemClick: The URL is: " + itemLink);
                    Intent webintent = new Intent (MainActivity.this,WebActivity.class);
                    webintent.putExtra("id", itemID);
                    webintent.putExtra("name",name);
                    webintent.putExtra("theurl",itemLink);
                    startActivity(webintent);
                }
                else{
                    toastMessage("No ID associated with that name");
                }

            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                Log.v("long clicked","pos: " + pos);
                String name = arg0.getItemAtPosition(pos).toString();
                Log.d(TAG, "onItemClick: You Clicked on " +name);

                Cursor data = mDatabaseHelper.getItemID(name);

                int itemID = -1;
                while(data.moveToNext()){
                    itemID = data.getInt(0);
                }
                Cursor dataLink = mDatabaseHelper.getItemLink(name);

                String itemLink ="";
                while(dataLink.moveToNext()){
                    itemLink = dataLink.getString(0);
                }
                if (itemID > -1) {
                    //String theurl = listUrl.get(itemID - 1);
                    Log.d(TAG, "onItemClick: The ID is: " + itemID);
                    Log.d(TAG, "onItemClick: The URL is: " + itemLink);
                    Intent editintent = new Intent (MainActivity.this,EditDataActivity.class);
                    editintent.putExtra("id", itemID);
                    editintent.putExtra("name",name);
                    editintent.putExtra("theurl",itemLink);
                    startActivity(editintent);
                }
                else{
                    toastMessage("No ID associated with that name");
                }

                return true;
            }
        });

    }
    @Override
    public void onBackPressed(){
        finishAffinity();
        finish();
        System.exit(0);

    }






}