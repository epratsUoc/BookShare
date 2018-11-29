package edu.uoc.curs.bookshare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class UploadBook extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_book);

        Button scan = findViewById(R.id.scan_button);
        TextView format_textview = findViewById(R.id.scan_format);
        TextView content_textview = findViewById(R.id.scan_content);

        Intent intent = getIntent();
        String content = intent.getStringExtra("Content");
        String format = intent.getStringExtra("Format");

        format_textview.setText(format);
        content_textview.setText(content);

    }
}
