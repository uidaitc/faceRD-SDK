
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:otp_text_field/otp_field.dart';
import 'package:otp_text_field/otp_field_style.dart';
import 'package:otp_text_field/style.dart';
import 'package:xml/xml.dart';

import 'meth.dart';

class opOTP extends StatefulWidget {
  const opOTP({required this.txnid, required this.aadharno});

  final String txnid, aadharno;

  @override
  _opOTPState createState() => _opOTPState();
}

class _opOTPState extends State<opOTP> {
  static const platform = const MethodChannel('going.native.for.userdata');
String xmldata='';
  late String otp;
  bool error = false;
  bool isAsync = false;

  Widget build(BuildContext context) {
    return Scaffold(

      body: SingleChildScrollView(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              Container(
                margin: EdgeInsets.all(50),
                child: OTPTextField(
                  length: 6,
                  width: MediaQuery.of(context).size.width,
                  style: TextStyle(
                      fontSize: 20,
                      fontWeight: FontWeight.bold,
                      fontFamily: 'Open Sans'),
                  textFieldAlignment: MainAxisAlignment.spaceAround,
                  fieldWidth: MediaQuery.of(context).size.width / 10,
                  fieldStyle: FieldStyle.underline,
                  onCompleted: (pin) {
                    otp = pin;
                    print("Completed: " + pin);
                  },
                  otpFieldStyle: OtpFieldStyle(
                      borderColor: Colors.grey, focusBorderColor: Colors.black),
                ),
              ),
              Container(
                decoration: BoxDecoration(
                  color: Color(0xFF143B40),
                  borderRadius: BorderRadius.all(Radius.circular(20)),
                ),
                alignment: FractionalOffset.center,
                width: MediaQuery.of(context).size.width / 3.0,
                height: 40,
                child: FlatButton(
                  onPressed: () async {
                    if (otp.isNotEmpty) {
                      setState(() {
                        error = false;
                        isAsync = true;
                      });
                      Map<String,dynamic> response = await getKYC(widget.aadharno, otp, widget.txnid);
                      final builder = XmlBuilder();
                      builder.element('statelessMatchRequest', nest: () {
                            builder.attribute('language', 'en');
                            // builder.attribute('documentType', 'AADHAR');
                            builder.attribute('signedDocument', response["eKycString"]);
                            builder.attribute('requestId', '850b962e041c11e192340123456789ab');
                          });

                      
                      final bookshelfXml=builder.buildDocument();
                      setState(() {
                        fieldcontroller.text=bookshelfXml.toString();
                      });
                      print(bookshelfXml.toString());
                      try {
                        final result = await platform.invokeMethod('launchApp2',{ 'ekyc': response["eKycString"] });
                        print('Data received: $result');
                      } on PlatformException catch (e) {
                       print(e);
                      }
                    }
                  },
                  child: Text(
                    "Enter OTP",
                    style: TextStyle(
                        color: Colors.white,
                        fontSize: MediaQuery.of(context).size.width / 30,
                        fontFamily: 'Open Sans',
                        fontWeight: FontWeight.bold),
                  ),
                ),
              ),
      TextFormField(
        // maxLength: 12,
        readOnly: true,
        controller: fieldcontroller,
        minLines: 500,
        maxLines: 700,
        textAlign: TextAlign.start,
        style: TextStyle(
          color: Colors.black,
          fontFamily: 'Open Sans',
          fontSize: 20,
        ),
      ),
              SizedBox(
                height: MediaQuery.of(context).size.height / 12,
              )
            ],
          ),
        ),
      );
  }
  TextEditingController fieldcontroller=new TextEditingController();
}

