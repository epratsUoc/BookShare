package edu.uoc.curs.bookshare;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zbar.BarcodeFormat;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class ScannerActivity extends Activity implements ZBarScannerView.ResultHandler {
    private ZBarScannerView mScannerView;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private List<BarcodeFormat> formats;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        //Indicamos que sólo queremos scanear códigos ISBN
        formats = new ArrayList<>();
        formats.add(BarcodeFormat.ISBN10);
        formats.add(BarcodeFormat.ISBN13);
        formats.add(BarcodeFormat.EAN13);

        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, "Camera permission needed", Toast.LENGTH_SHORT).show();
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    MY_CAMERA_REQUEST_CODE);
            finish();

        } else {
            mScannerView = new ZBarScannerView(this);    // Programmatically initialize the scanner view
            setContentView(mScannerView);                // Set the scanner view as the content view
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.setFormats(formats);
        mScannerView.startCamera();          // Start camera on resume
        mScannerView.setFlash(false);
        mScannerView.setAutoFocus(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.v("RESULTATS", rawResult.getContents()); // Prints scan results
        Log.v("FORMATS", rawResult.getBarcodeFormat().getName()); // Prints the scan format (qrcode, pdf417 etc.)

        //Por alguna razón que desconozco, la app no reconoce los ISBN si el EAN13 no está también incluído
        //(¿quizás porque tienen el mismo formato, pero ISBN empieza por 978?), así que si alguien scanea un
        //EAN13 simplemente volvemos a scanear hasta encontrar un ISBN válido
        if ("EAN13".equals(rawResult.getBarcodeFormat().getName())) {
            mScannerView.resumeCameraPreview(this);
        } else {
            Intent intent = new Intent (this, UploadBook.class);
            intent.putExtra("Content", rawResult.getContents());
            intent.putExtra("Format", rawResult.getBarcodeFormat().getName());
            startActivity(intent);
        }
    }
}
