package it.jaschke.alexandria.scan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import it.jaschke.alexandria.AddBook;
import me.dm7.barcodescanner.zbar.BarcodeFormat;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

/**
 * Created by hassanabid on 9/28/15.
 */
public class BarScannerActivity extends Activity implements ZBarScannerView.ResultHandler{

    private static final String LOG_TAG = BarScannerActivity.class.getSimpleName();
    private ZBarScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZBarScannerView(this);    // Programmatically initialize the scanner view
        List<BarcodeFormat> barcodeFormats = new ArrayList<>();
//        barcodeFormats.add(BarcodeFormat.ISBN13);
        barcodeFormats.add(BarcodeFormat.EAN13);
        mScannerView.setFormats(barcodeFormats);
        mScannerView.setAutoFocus(true);
        setContentView(mScannerView);                // Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.v(LOG_TAG, "book code : " + rawResult.getContents()); // Prints scan results
        Log.v(LOG_TAG, "code format: " + rawResult.getBarcodeFormat().getName()); // Prints the scan format (qrcode, pdf417 etc.)
        sendResult(rawResult.getContents());

    }

    private void sendResult(String barCode) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(AddBook.SCAN_CODE, barCode);
        setResult(RESULT_OK,returnIntent);
        finish();
    }
}
