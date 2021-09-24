package com.fam.wear.service.stubs;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;

import com.fam.wear.quick.IWear;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.wearengine.HiWear;
import com.huawei.wearengine.auth.AuthCallback;
import com.huawei.wearengine.auth.Permission;
import com.huawei.wearengine.device.Device;
import com.huawei.wearengine.device.DeviceClient;
import com.huawei.wearengine.p2p.Message;
import com.huawei.wearengine.p2p.P2pClient;
import com.huawei.wearengine.p2p.Receiver;
import com.huawei.wearengine.p2p.SendCallback;

import java.nio.charset.StandardCharsets;
import java.util.List;


public class Wear extends IWear.Stub {

    public static String TAG = "WEAR";


    private String watchAppPackageName;
    private String watchAppFingerPrint;
    private MediaPlayer player;
    private Context context;
    private DeviceClient deviceClient;
    private P2pClient p2pClientRecv;
    private P2pClient p2pClientSender;
    private Receiver receiver;
    private Device connectedDevice;

    AuthCallback authCallback = new AuthCallback() {
        @Override
        public void onOk(Permission[] permissions) {
            System.out.println("---auth ok");

        }

        @Override
        public void onCancel() {
            System.out.println("---auth onCancel");
        }
    };

    public Wear(Context context) {
        this.context = context;
        this.initWear();
        wearInitDeviceClient();
    }

    private void getConnectedDevice() {
        Task<List<Device>> task = deviceClient.getBondedDevices()
                .addOnSuccessListener(new OnSuccessListener<List<Device>>() {
                    @Override
                    public void onSuccess(List<Device> devices) {
                        connectedDevice = devices.get(0);
                    }
                });
    }

    private void wearInitDeviceClient() {
        deviceClient = HiWear.getDeviceClient(context);
        Task<Boolean> task = deviceClient.hasAvailableDevices();
        task.addOnSuccessListener(new OnSuccessListener<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                System.out.println("-----result: " + aBoolean);
                getConnectedDevice();

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        System.out.println("----failure: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
    }

    private void initWear() {
        HiWear.getAuthClient(context)
                .requestPermission(authCallback, Permission.DEVICE_MANAGER, Permission.NOTIFY)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void successVoid) {
                        Log.d(TAG, "getAuthClient onSuccess");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.d(TAG, "getAuthClient onFailure");
                        e.printStackTrace();
                    }
                });


    }

    private void initReceiver() {
        Receiver receiver = new Receiver() {
            @Override
            public void onReceiveMessage(Message message) {
                if (message.getType() == Message.MESSAGE_TYPE_DATA) {
                    // Process messages sent by your wearable app.
                    String msg = new String(message.getData());
                    System.out.println("---receiving a message: " + msg);
                    manageReceivedMessage(msg);
                } else if (message.getType() == Message.MESSAGE_TYPE_FILE) {
                    // Process the file sent by your wearable app.
                }
            }
        };
        p2pClientRecv.registerReceiver(connectedDevice, receiver);
    }

    private void manageReceivedMessage(String data){
        int value = Integer.parseInt(data);
        switch (value){
            case 0: {
                player.stop();
                break;
            }
            case 1: {
                makeSound(true);
                break;
            }
            case 10:{
                makeSound(false);
            }
        }
    }

    private void sendMessage(String message) {
        Message.Builder builder = new Message.Builder();
        builder.setPayload(message.getBytes(StandardCharsets.UTF_8));
        Message sendMessage = builder.build();
        SendCallback sendCallback = new SendCallback() {
            @Override
            public void onSendResult(int resultCode) {
                System.out.println("--send result: "+resultCode);
            }

            @Override
            public void onSendProgress(long progress) {
            }
        };
        p2pClientSender.send(connectedDevice, sendMessage, sendCallback)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void successVoid) {
                        // Related processing logic for your app after the send task is successfully executed.
                        System.out.println("---ok send");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // Related processing logic for your app after the send task fails.
                        System.out.println("----Failing sending");
                        e.printStackTrace();
                    }
                });
    }

    private void initCommunications() {
        //rec
        p2pClientRecv = HiWear.getP2pClient(context);
        p2pClientRecv.setPeerPkgName(this.watchAppPackageName);
        p2pClientRecv.setPeerFingerPrint(this.watchAppFingerPrint);

        //sender
        p2pClientSender = HiWear.getP2pClient(context);
        p2pClientSender.setPeerPkgName(this.watchAppPackageName);
        p2pClientSender.setPeerFingerPrint(this.watchAppFingerPrint);


        initReceiver();
    }


    @Override
    public void startCommunication() throws RemoteException {
        this.initCommunications();
    }

    @Override
    public void setInternaConfiguration(String watchAppPackageName, String watchAppFingerPrint) throws RemoteException {
        this.watchAppFingerPrint = watchAppFingerPrint;
        this.watchAppPackageName = watchAppPackageName;
    }

    @Override
    public void sendData(String data) throws RemoteException {
        Log.d(TAG, "Sending data to watch from service: "+data);
        this.sendMessage(data);
    }

    private void makeSound(boolean looping ) {
        player = MediaPlayer.create(this.context,
                Settings.System.DEFAULT_RINGTONE_URI);
        player.setLooping(looping);
        player.start();
    }
}
