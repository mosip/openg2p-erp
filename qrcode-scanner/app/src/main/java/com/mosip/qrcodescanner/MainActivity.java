package com.mosip.qrcodescanner;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import com.google.crypto.tink.proto.Ed25519;
import com.google.crypto.tink.subtle.Ed25519Sign;
import com.google.crypto.tink.subtle.Ed25519Verify;
import com.google.crypto.tink.subtle.Hex;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private TextView messageView;
    private TextView signatureView;
    private TextView statusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        messageView = findViewById(R.id.textView2);
        signatureView = findViewById(R.id.textView3);
        statusView = findViewById(R.id.textView6);

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA}, PackageManager.PERMISSION_GRANTED);

    }

    public void ScanButton(View view) {
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.initiateScan();
    }

   // @RequiresApi(api = Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode, data);
        if(intentResult != null) {
            if(intentResult.getContents() == null ){
                textView.setText("Cancelled");
            }
            else{
                textView.setText(intentResult.getContents());
            }
        }

        try{

            String jsonMessage = textView.getText().toString();
            JSONObject qrMessage = new JSONObject(jsonMessage);
            //JSONObject qrMessageArray = parser.getJSONObject(s);

            String subject = qrMessage.getString("subject");
            //messageView.setText(subject);


            String signature = qrMessage.getString("signature");
            //signatureView.setText(signature);
//            Ed25519Sign.KeyPair pair = Ed25519Sign.KeyPair.newKeyPair();
//
//            String privKey = Base64.getEncoder().encodeToString(pair.getPrivateKey());
//            String publicKey = Base64.getEncoder().encodeToString(pair.getPublicKey());

            String publicKey = "tWOWYT9twCd2SNCNMsSTaZFTICeafaStU5FAdZcHaG4=";
            byte[] publicKeyDecoded = Base64.getDecoder().decode(publicKey.getBytes());
            messageView.setText(publicKey);

            //Ed25519Sign signed = new Ed25519Sign(pair.getPrivateKey());
            String subject1 = "{\"Suffix\":\"\",\"lName\":\"banto\",\"fName\":\"aaron-o-niel\",\"mName\":\"simbaco\",\"sex\":\"Male\",\"BF\":\"[1,7]\",\"DOB\":\"November 12, 1993\",\"POB\":\"City of Manila,NCR, CITY OF MANILA, FIRST DISTRICT\",\"PCN\":\"5073-0634-8709-1024\"}";
            System.out.println("Message: " +  subject);
            System.out.println("Message 1: " +  subject1);
            if(subject==subject1) {
                System.out.println("Both Are Equal");
            } else {
                System.out.println("There Are Difference");
            }
            //signature = "Wm/mTiVtkOO3kppoPtrWbkTaVE6+bE/9AVZ3ct/1IyjsvwQJm9lOk7XDzBK4BCmWCDB6V/+I0k6KTpIXXiXVCQ==";

            //byte[] signatureDecoded = signed.sign(subject.getBytes());
            //signature = Base64.getEncoder().encodeToString(signatureDecoded);


            byte[] signatureDecoded = Base64.getDecoder().decode(signature.trim().getBytes());

            signatureView.setText(signature);
            Ed25519Verify ed = new Ed25519Verify(publicKeyDecoded);
            ed.verify(signatureDecoded, subject.trim().getBytes());
            //signature = Base64.getEncoder().encodeToString(signatureBytes);

            statusView.setTextColor(Color.GREEN);
            statusView.setText("Verified Successfully");

            Intent intent = new Intent();

            if (intent != null) {

                intent.putExtra("date_issued", qrMessage.getString("DateIssued"));
                intent.putExtra("issuer", qrMessage.getString("Issuer"));
                JSONObject subjectJSON = qrMessage.getJSONObject("subject");
                intent.putExtra("suffix" , subjectJSON.getString("Suffix"));
                intent.putExtra("lname" , subjectJSON.getString("lName"));
                intent.putExtra("fname" , subjectJSON.getString("fName"));
                intent.putExtra("mname" , subjectJSON.getString("mName"));
                intent.putExtra("sex" , subjectJSON.getString("sex"));
                intent.putExtra("bf" , subjectJSON.getString("BF"));
                intent.putExtra("dob" , subjectJSON.getString("DOB"));
                intent.putExtra("pob" , subjectJSON.getString("POB"));
                intent.putExtra("pcn" , subjectJSON.getString("PCN"));

                setResult(RESULT_OK, intent);
                finish();
            }

        }catch(JSONException pe) {
            System.out.println("position: " + pe.getStackTrace());
            statusView.setTextColor(Color.RED);
            statusView.setText(pe.getMessage());
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getStackTrace());
            statusView.setTextColor(Color.RED);
            statusView.setText(ex.getMessage());
        }

        super.onActivityResult(requestCode, resultCode, data);

    }


}