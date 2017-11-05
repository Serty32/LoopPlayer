package com.eyelevel.loopplayer;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * Created by sinelnikovserhii on 02.11.17.
 */

public class VideoActivity extends Activity {
    private static final String KEY_PIN = "pin";
    private static final String KEY_VIDEO = "video";
    VideoView videoView;
    Button btnPin;
    String pin;
    String videoUri;
    long currentTime = 0;
    static int count = 0;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        hideNavigationMenu();
        videoView = (VideoView) findViewById(R.id.video);
        btnPin = (Button)findViewById(R.id.btnPin);
        btnPin.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        pin = (String) getIntent().getSerializableExtra(KEY_PIN);
        videoUri = (String) getIntent().getSerializableExtra(KEY_VIDEO);
        try {
            blockPhysicalButtons();
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "Main Error", Toast.LENGTH_LONG).show();
        }

        videoView.setVideoURI(Uri.parse(videoUri));
        videoView.start();

        videoView
                .setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.start();
                    }
                });


     /*   Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("video/*");
        startActivityForResult(pickIntent, VIDEO_PICKER_SELECT);
        */

        btnPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((System.currentTimeMillis() - currentTime) <= 1000 || currentTime == 0)) {
                    count++;
                } else {
                    // TODO: когда нажимаем до 5 раз и время проходит, то надо уже 6 нажатий. Может надо где-то обнулять время.
                    count = 1;
                }
                currentTime = System.currentTimeMillis();
                if (count == 5) {
                    createAlertDialog();
                }
            }
        });
       /* View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
        */
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            Uri selectedMediaUri = data.getData();
            videoView.setVideoURI(selectedMediaUri);
            videoView.start();

            videoView
                    .setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.start();
                        }
                    });
        }
    }


 /*   @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP && ((System.currentTimeMillis() - currentTime) <= 1000 || currentTime == 0)) {
            count++;
        } else {
            // TODO: когда нажимаем до 5 раз и время проходит, то надо уже 6 нажатий. Может надо где-то обнулять время.
            count = 0;
        }
        currentTime = System.currentTimeMillis();
        if (count == 5) {
            createAlertDialog();
        }
        return super.onKeyDown(keyCode, event);
    }
    */

    public void hideNavigationMenu() {

        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    public void createAlertDialog() {
        final AlertDialog.Builder alertDialogAutorization = new AlertDialog.Builder(VideoActivity.this);
        alertDialogAutorization.setTitle("PIN");
        alertDialogAutorization.setMessage("Set PIN");
        final EditText indexAutorization = new EditText(VideoActivity.this);
        indexAutorization.setInputType(InputType.TYPE_CLASS_NUMBER);
        indexAutorization.requestFocus();
        LinearLayout.LayoutParams lpAutorization = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        indexAutorization.setLayoutParams(lpAutorization);
        alertDialogAutorization.setView(indexAutorization);

        alertDialogAutorization.setPositiveButton("Home", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (pin.equals(indexAutorization.getText().toString())) {
                    Intent intent = new Intent(VideoActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    hideNavigationMenu();
                    Toast.makeText(VideoActivity.this, "Bad PIN", Toast.LENGTH_SHORT).show();
                    count = 0;
                    currentTime = 0;
                }
            }
        });

        alertDialogAutorization.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                hideNavigationMenu();
                currentTime = 0;
                count = 0;
                dialog.cancel();
            }
        });
        alertDialogAutorization.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void blockPhysicalButtons() throws FileNotFoundException {
        String fullFile = "";

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream("/system/usr/keylayout/Vendor_046d_Product_b501.kl"), StandardCharsets.UTF_8))){
            String line;
            while ((line = reader.readLine()) != null) {
                fullFile += line;
            }
            clearTheFile();
        } catch (IOException e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }

        fullFile.replace("key114","#key114");
        fullFile.replace("key115","#key115");
        fullFile.replace("key152","#key152");

        byte[] keysAfterReplace = fullFile.getBytes();

        try (FileOutputStream fileWithReplaceKeys = new FileOutputStream("/system/usr/keylayout/Vendor_046d_Product_b501.kl")) {

            fileWithReplaceKeys.write(keysAfterReplace);

        } catch (IOException e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void clearTheFile() throws IOException {
        FileWriter fwOb = new FileWriter("/system/usr/keylayout/Vendor_046d_Product_b501.kl", false);
        PrintWriter pwOb = new PrintWriter(fwOb, false);
        pwOb.flush();
        pwOb.close();
        fwOb.close();
    }
}