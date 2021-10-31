package com.app.intentapp.intentapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class MainActivity extends FlutterActivity {
    private static final String INTENT_ACTION =
            "in.gov.uidai.rdservice.face.STATELESS_MATCH";

    private static MethodChannel.Result result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        GeneratedPluginRegistrant.registerWith(this);
        new MethodChannel(getFlutterEngine().getDartExecutor().getBinaryMessenger(), "going.native.for.userdata").setMethodCallHandler(callHandler);
    }

    private MethodChannel.MethodCallHandler callHandler = new MethodChannel.MethodCallHandler() {

        @Override
        public void onMethodCall(MethodCall methodCall,
                                 MethodChannel.Result result) {
            String ekyc = methodCall.argument("ekyc");
            MainActivity.result = result;
            launchApp2(ekyc);
        }
    };
    @SuppressLint("NewApi")
    private void launchApp2(String ekyc){
        Intent sendIntent = new Intent();
        sendIntent.setAction(INTENT_ACTION);
        Bundle bundle = new Bundle();
        //ekyc contains the entire xml

        StatelessMatchRequest statelessMatchRequest = new StatelessMatchRequest();
        statelessMatchRequest.requestId ="850b962e041c11e192340123456789ab";
        statelessMatchRequest.signedDocument = ekyc;
        statelessMatchRequest.language = "en";
        statelessMatchRequest.enableAutoCapture = "true";


        try {
            sendIntent.putExtra("request",statelessMatchRequest.toXml());
            if (sendIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(sendIntent, 123);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        DocumentBuilderFactory dbFactory =
//                DocumentBuilderFactory.newInstance();
//        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//        Document doc = dBuilder.newDocument();
//
//        Element statelessMatchRequest = doc.createElement("statelessMatchRequest");
//        doc.appendChild(statelessMatchRequest);
//
//        // setting attribute to element
//        Attr attr = doc.createAttribute("requestId");
//        attr.setValue("710b962e041c11e192340123456789ab");
//        statelessMatchRequest.setAttributeNode(attr);
//        // setting attribute to element
//        Attr attr1 = doc.createAttribute("signedDocument");
//        attr1.setValue(ekyc);
//        statelessMatchRequest.setAttributeNode(attr1);
//        // setting attribute to element
//        Attr attr2 = doc.createAttribute("documentType");
//        attr2.setValue("Aadhaar(eKYC)");
//        statelessMatchRequest.setAttributeNode(attr2);
//        // setting attribute to element
//        Attr attr3 = doc.createAttribute("language");
//        attr3.setValue("en");
//        statelessMatchRequest.setAttributeNode(attr3);
//
//        TransformerFactory transformerFactory = TransformerFactory.newInstance();
//        Transformer transformer = transformerFactory.newTransformer();
//        DOMSource source = new DOMSource(doc);

//        StringBuilder xmlBuilder = new StringBuilder("<?xml version='1.0' encoding='UTF-8'?>");
//
//        xmlBuilder.append("<statelessMatchRequest requestId = \"710b962e041c11e192340123456789ab\" signedDocument = \"\"documentType=\"Aadhaar(eKYC)\" language = \"en\"");
////        xmlBuilder.append("<Parameter token=\"xyz\">").append("<Value>4</Value>");
////        xmlBuilder.append("</Parameter>");
//        xmlBuilder.append("</statelessMatchRequest>");
//        xmlBuilder.toString();
//        String request = String.format("<statelessMatchRequest requestId = \"710b962e041c11e192340123456789ab\" signedDocument = %s documentType= \"Aadhaar(eKYC)\ language = \"en\"", ekyc);

//        Bundle data1 = new Bundle();
//        data1.putString();


    }

    @Override
    protected void onActivityResult(int req, int res, Intent data) {
        if (req == 123) {
            if (res == Activity.RESULT_OK) {
           // handle the your response here
                result.success(username);
            }
        } else {
            super.onActivityResult(req, res, data);
        }
    }
}


