package com.nnc.webmark1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditDataActivity extends AppCompatActivity {
    private static final String TAG = "EditDataActivity";
    private Button btnSave,btnDelete,btnCancel;
    private EditText editable_name;
    DatabaseHelper mDatabaseHelper;
    private String selectedName;
    private String selectedLink;
    private int selectedID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_bookmark);
        btnSave= (Button)findViewById(R.id.edit_saveButton);
        btnDelete= (Button)findViewById(R.id.edit_deleteButton);
        btnCancel= (Button)findViewById(R.id.edit_cancelButton);
        editable_name=(EditText) findViewById(R.id.edit_edittextForChange);
        mDatabaseHelper = new DatabaseHelper(this);
        Intent receivedIntent = getIntent();

        selectedID = receivedIntent.getIntExtra("id",-1);
        selectedName = receivedIntent.getStringExtra("name");
        selectedLink = receivedIntent.getStringExtra("theurl");

        editable_name.setText(selectedName);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String updatedText = editable_name.getText().toString();
                if(updatedText.contains("'")){
                    //replace it with something -----------------
                    updatedText = updatedText.replace("'", "ߴ");


                }
                if(!updatedText.equals("")){
                    mDatabaseHelper.updateName(updatedText,selectedID,selectedName);
                    Intent mainActIntent = new Intent (EditDataActivity.this,MainActivity.class);
                    startActivity(mainActIntent);
                }
                else{
                    toastMessage("You must enter a name");
                }
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //deletes it from database
                mDatabaseHelper.deleteItem(selectedID,selectedName,selectedLink);
                Intent mainActIntent = new Intent (EditDataActivity.this,MainActivity.class);
                startActivity(mainActIntent);
                toastMessage("removed from bookshelf");
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainActIntent = new Intent (EditDataActivity.this,MainActivity.class);
                startActivity(mainActIntent);
            }
        });




    }
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

}
