package com.eyelevel.loopplayer;


import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int VIDEO_PICKER_SELECT = 1;
    private static final String KEY_PIN = "pin";
    private static final String KEY_VIDEO = "video";
    EditText edPin;
    Button btnChose;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edPin = (EditText) findViewById(R.id.edPin);
        btnChose = (Button) findViewById(R.id.btnChoseVideo);

        btnChose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edPin.getText().toString().length() < 4) {
                    Toast.makeText(MainActivity.this, "You must write 4 number", Toast.LENGTH_SHORT).show();
                } else {

                    Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                    pickIntent.setType("video/*");
                    startActivityForResult(pickIntent, VIDEO_PICKER_SELECT);

                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            Uri selectedMediaUri = data.getData();

            Intent intent = new Intent(MainActivity.this, VideoActivity.class);
            intent.putExtra(KEY_PIN, edPin.getText().toString());
            intent.putExtra(KEY_VIDEO, selectedMediaUri.toString());
            startActivity(intent);
        }
    }
}