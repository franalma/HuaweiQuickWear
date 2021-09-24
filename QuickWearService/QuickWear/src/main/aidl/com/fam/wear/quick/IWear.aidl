// IWear.aidl
package com.fam.wear.quick;

// Declare any non-default types here with import statements

interface IWear {
    void startCommunication();
    void setInternaConfiguration (String watchAppPackageName, String watchAppFingerPrint);
    void sendData (String data);
}