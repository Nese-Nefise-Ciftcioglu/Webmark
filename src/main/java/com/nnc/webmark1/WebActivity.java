package com.nnc.webmark1;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class WebActivity extends AppCompatActivity {
    private static final String TAG = "WebActivity";
    DatabaseHelper mDatabaseHelper;
    private String selectedName;
    private int selectedID;
    private String selectedUrl;
    private String updatedUrl;
    private Button addBookmarkBtn;
    WebView webview;
    private boolean isChecked = false;
    private InterstitialAd mInterstitialAd;
    //prefstuff
    public static final String SHARED_PREFS = "sharedPrefs";
    public static String SWITCH ="Switch";
    boolean isdarkcheckedbitches=false;




    private void createPersonalizedAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        createInterstitialAd(adRequest);
    }

    private void createInterstitialAd(AdRequest adRequest ) {
        InterstitialAd.load(this,"ca-app-pub-6518685517845474/2084154938", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.d(TAG, "onAdLoaded");
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                Log.d("TAG", "The ad was dismissed.");
                                Intent mainActIntent = new Intent (WebActivity.this,MainActivity.class);
                                startActivity(mainActIntent);
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when fullscreen content failed to show.
                                Log.d("TAG", "The ad failed to show.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null;
                                Log.d("TAG", "The ad was shown.");
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d(TAG, loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view_layout);

        //loadSharedpref
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);

        isdarkcheckedbitches = sharedPreferences.getBoolean(SWITCH,false);

        //ADMOB SDK
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                createPersonalizedAd();
            }
        });




        mDatabaseHelper = new DatabaseHelper(this);
        //get the intent
        Intent receivedIntent = getIntent();

        selectedID = receivedIntent.getIntExtra("id",-1);
        selectedName = receivedIntent.getStringExtra("name");
        selectedUrl = receivedIntent.getStringExtra("theurl");
        webview = findViewById(R.id.webview);

        webview.setWebChromeClient(new WebChromeClient());
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setAppCacheEnabled(true);
        webview.getSettings().setLoadsImagesAutomatically(true);
        webview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient());
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setDisplayZoomControls(false);
        webview.getSettings().setDomStorageEnabled(true);

        webview.loadUrl(selectedUrl);

        //Start of dynamic title code---------------------
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setTitle(selectedName);
        }

        //End of dynamic title code----------------------


        addBookmarkBtn = findViewById(R.id.addBookmarkButton);
        addBookmarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatedUrl = webview.getUrl();
                if(!updatedUrl.equals(selectedUrl)) {
                    mDatabaseHelper.updateLink(updatedUrl, selectedID, selectedUrl);
                    //ADS---------------
                    if (mInterstitialAd != null) {
                        mInterstitialAd.show(WebActivity.this);

                    } else {
                        Log.d("TAG", "The interstitial ad wasn't ready yet.");
                        //toastMessage("ad not ready yet");
                        Intent mainActIntent = new Intent (WebActivity.this,MainActivity.class);
                        startActivity(mainActIntent);
                    }
                    //ADS---------------
                    //Intent mainActIntent = new Intent (WebActivity.this,MainActivity.class);
                    //startActivity(mainActIntent);
                    toastMessage("Bookmark set successfully");
                }
                else{
                    toastMessage("Bookmark is already set to this page");
                }
            }
        });

    }


    @Override
    public void onBackPressed(){
        if(webview.canGoBack()){
            webview.goBack();
        }
        else{
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_webview, menu);
        MenuItem checkable = menu.findItem(R.id.action_darkmode_web);
        MenuItem checkableForButton = menu.findItem(R.id.action_disableBookmark_web);
        checkableForButton.setChecked(isChecked);
        checkable.setChecked(isdarkcheckedbitches);


        if(isdarkcheckedbitches) {
            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                WebSettingsCompat.setForceDark(webview.getSettings(), WebSettingsCompat.FORCE_DARK_ON);


            }
        }
        else{
            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                WebSettingsCompat.setForceDark(webview.getSettings(), WebSettingsCompat.FORCE_DARK_OFF);



            }
        }


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_darkmode_web) {
            switch (item.getItemId()) {
                case R.id.action_darkmode_web:
                    isChecked = !item.isChecked();
                    if(isChecked) {
                        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                            WebSettingsCompat.setForceDark(webview.getSettings(), WebSettingsCompat.FORCE_DARK_ON);
                        }
                    }
                    else{
                        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                            WebSettingsCompat.setForceDark(webview.getSettings(), WebSettingsCompat.FORCE_DARK_OFF);



                        }
                    }
                    item.setChecked(isChecked);
                    //SaveSharedpref
                    SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(SWITCH,item.isChecked());

                    editor.apply();

                    return true;
                default:


                    return false;
            }
        }
        else if(id == R.id.action_refreshPage_web){
            webview.reload();
        }
        else if (id == R.id.action_disableBookmark_web){
            switch (item.getItemId()) {
                case R.id.action_disableBookmark_web:
                    isChecked = !item.isChecked();
                    if(isChecked) {
                        Button bookMarkButton=(Button)findViewById(R.id.addBookmarkButton);
                        bookMarkButton.setVisibility(View.INVISIBLE);
                    }
                    else if(!isChecked){
                        Button bookMarkButton=(Button)findViewById(R.id.addBookmarkButton);
                        bookMarkButton.setVisibility(View.VISIBLE);
                    }
                    item.setChecked(isChecked);

                    return true;
                default:
                    return false;
            }
        }
        else if (id == R.id.action_returnBookshelf_web){
            Intent mainActIntent = new Intent (WebActivity.this,MainActivity.class);
            startActivity(mainActIntent);
        }
        else if(id == R.id.action_copyLink){
            copyToClipboard();
        }

        return super.onOptionsItemSelected(item);
    }
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
    private class WebViewClientt extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url){
            view.loadUrl(url);
            return true;
}

    }
    public void copyToClipboard(){
        String url = webview.getUrl();
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", url);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getApplicationContext(), "Link Copied!",
                Toast.LENGTH_SHORT).show();
    }
}



