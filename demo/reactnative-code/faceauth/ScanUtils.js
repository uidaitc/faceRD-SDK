import { NativeModules, Platform } from 'react-native'
import { parseXML } from '../api/handlers/MAadhaarResponseParser'
import { getAadhaarDataFromBytes } from './ScannerFileUtil'
var bigInt = require('big-integer')
var pako = require('pako')

const RNScanUtil = NativeModules.RNScanUtil
const RNCrypto = NativeModules.RNCrypto
const RNTUtility = NativeModules.RNTUtility

export const scanData = async data => {  
  if (data.startsWith('<QPDB')) {
    let xmlJson = await parseXML(data)
    xmlJson = xmlJson.QPDB.$
    let infoJson = {}
    infoJson['name'] = xmlJson.n || ''
    infoJson['uid'] = xmlJson.u || ''
    infoJson['gender'] = xmlJson.g || ''
    infoJson['dob'] = xmlJson.d || ''
    infoJson['address'] = xmlJson.a || ''
    infoJson['sign'] = xmlJson.s || ''
    infoJson['mobileNumber'] = xmlJson.m || ''
    infoJson['image'] = xmlJson.i || ''
    infoJson['isXml'] = true
    let signatureVal
    if (Platform.OS != 'ios') {
      signatureVal = await RNScanUtil.verifySignature(data, infoJson['sign'], infoJson['uid'], true)
      if (signatureVal && signatureVal.signVal == '') {
        return 'This is not a UIDAI compliance QR code'
      } else {
        return infoJson
      }
    } else {
      let signObj = {
        data: data,
        sign: infoJson['sign'],
        isXml: true
      }
      signatureValue = await RNTUtility.qrcodeSignature(signObj)
      if (!signatureValue) {
        infoJson['isDataVerified'] = false
        // return infoJson
        // return 'This is not a UIDAI compliance QR code'
      } else {
        infoJson['isDataVerified'] = true

      }
      return infoJson

    }
  } else if (data.startsWith('</?xml') || data.startsWith('<?xml')) {
    return 'This is not a UIDAI compliance QR code'
  } else {
    try {
      let bigIntData = bigInt(data)
      bigIntData = bigIntData.value
      var largeNumber = bigInt(data, 10)
      str2 = largeNumber.toArray(16).value
      let dataByte = []
      for (let i = 0; i < str2.length; i += 2) {
        let byte = str2[i]
        byte = byte << 4
        byte = byte | str2[i + 1]
        dataByte.push(byte)
      }
      const unzipped = pako.inflate(dataByte)
      return getAadhaarDataFromBytes(unzipped, data)
    } catch (error) {
      return 'Unable to parse'
    }
  }
}
//added 7/9
export const scanFaceAuthData = async data => {

    try {
      var base64Payload = data.split(".")[1];
      var payload = Buffer.from(base64Payload, 'base64');
      var FaceAuthData = JSON.parse(payload.toString());
      console.log(FaceAuthData);
      // if(FaceAuthData.client_id=='face_auth_client'){
        if(FaceAuthData.client_id==null){

        let data = {"txn_id":FaceAuthData.client_id,
        "user_id":FaceAuthData.user_id,
        // client_id:FaceAuthData.client_id
        client_id:"face_auth_client"
       }
        await RNScanUtil.invokeCaptureIntent(FaceAuthData.txn_id,FaceAuthData.user_id,FaceAuthData.session_id);
        return data
      }
      else{
      return 'This is not a UIDAI Face Auth QR Code'
    }
      

    } catch (error) {
      return 'Unable to parse the Face Auth QR Code'
    }
  
}
//
export const j2kPng = async (data) => {
  return await RNScanUtil.convertPngToJpegFormat(data)
}

export const validateHash = async (data) => {
  if (Platform.OS === 'ios') {
    return await RNCrypto.validateHashString(data);
  } else {
    return await RNCrypto.validateHashString(data.emailNMobText, data.lastDigitNumber);
  }
}