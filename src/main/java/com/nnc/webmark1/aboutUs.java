package com.nnc.webmark1;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
//import android.provider.CalendarContract;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.nnc.webmark1.R;

import java.util.Calendar;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class aboutUs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        Element adsElement = new Element();
        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.mukkemmel)
                .setDescription(" \"Webmark\" is a simple application that makes it easy for you to read content on the web such as webtoons. \"Webmark\" saves the last link you left. In this way, you can continue where you left off next time.")
                .addItem(new Element().setTitle("Version 1.0"))
                .addGroup("CONNECT WITH US!")
                .addEmail("nncentertainment35@gmail.com")
                //.addWebsite("Your website/")
                .addPlayStore("com.nnc.webmark1")   //Replace all this with your package name
                .addInstagram("nnc_technologies")    //Your instagram id
                //.addYoutube("your youtube")   //Enter your youtube link here (replace with my channel link)
                .addItem(createCopyright())
                .create();
        setContentView(aboutPage);
    }
    private Element createCopyright()
    {
        Element copyright = new Element();
        @SuppressLint("DefaultLocale") final String copyrightString = String.format("Copyright %d by NNC technologies", Calendar.getInstance().get(Calendar.YEAR));
        copyright.setTitle(copyrightString);
        //copyright.setIcon(R.mipmap.ic_launcher);
        copyright.setGravity(Gravity.CENTER);
        copyright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(aboutUs.this,copyrightString,Toast.LENGTH_SHORT).show();
            }
        });
        return copyright;
    }
}