package in.gov.uidai.mAadhaarPlus.scanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;

//import org.jetbrains.annotations.NotNull;
import org.openJpeg.OpenJPEGJavaDecoder;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.zip.GZIPInputStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import in.gov.uidai.mAadhaarPlus.R;
import in.gov.uidai.mAadhaarPlus.scanner.models.CaptureResponse;
import in.gov.uidai.mAadhaarPlus.scanner.models.CustOpts;
import in.gov.uidai.mAadhaarPlus.scanner.models.NameValue;
import in.gov.uidai.mAadhaarPlus.scanner.models.Opts;
import in.gov.uidai.mAadhaarPlus.scanner.models.PidOptions;
import in.gov.uidai.mAadhaarPlus.utils.Constants;
//import in.gov.uidai.mAadhaarPlus.utils.Constants;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;

public class ScannerModule extends ReactContextBaseJavaModule {
    private AadhaarQR aadhaarQR=new AadhaarQR();
    private final static String SIGN_KEY="sign_key";
    private final Charset CHARSET=Charset.forName("ISO-8859-1");
    private boolean isVerified;
    private String sha256;
    private String address[]=new String[11];
    private boolean isXML;
    Context context;
    PackageManager packageManager;

    private HashMap _$_findViewCache;


    public ScannerModule(@Nonnull ReactApplicationContext reactContext) {
        super(reactContext);
//        getCurrentActivity().registerReceiver(captureResponseReceiver, new IntentFilter("in.gov.uidai.rdservice.face.CAPTURE_RESULT"));

    }


    @ReactMethod
    public void invokeCaptureIntent(String txn_ID,String user_Id,String session_ID) throws Exception {

        Intent intent = new Intent(Constants.CAPTURE_INTENT);
        if (intent.resolveActivity(getCurrentActivity().getPackageManager()) != null) {
            intent.putExtra(
                Constants.CAPTURE_INTENT_REQUEST,
                createPidOptions(txn_ID)
            );
            intent.putExtra("userId",user_Id.toString());
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getCurrentActivity());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("userId",user_Id.toString());
            editor.putString("txn_ID",txn_ID.toString());
            editor.putString("session_ID",session_ID);
            editor.apply();
            getCurrentActivity().startActivity(intent);

        } else {
            handleAppDoesNotExist();
        }
    }

    private String createPidOptions(String txn_Id) throws Exception {
        String txnId = txn_Id;//UUID.randomUUID().toString(); need to pass the txtid
        PidOptions pidOptions = new PidOptions();

        CustOpts custOptions = new CustOpts();
        custOptions.nameValues.add(new NameValue("txnId", txnId));
        pidOptions.custOpts = custOptions;

        Opts opts = new Opts();
        opts.pidVer = "2.0";
//        opts.wadh = Constants.WADH_KEY; //commented on 27/08as need to be emplty for faceAuth
        opts.wadh = "";
        pidOptions.opts = opts;

        pidOptions.env = "S"; //for pre-prod and P for prod
        pidOptions.ver = "1.0";
        return pidOptions.toXML();
    }

    public void  handleAppDoesNotExist() {
        Log.i("Handle","Inside Handler");
        Toast.makeText(getCurrentActivity(),"Hello FaceAuth App is not installed ",Toast.LENGTH_LONG).show();

    }

}
