//package com.example.woga1.navigation;
//
//import android.app.AlertDialog;
//import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothSocket;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.AsyncTask;
//import android.util.Log;
//import android.widget.Toast;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.Set;
//import java.util.UUID;
//
//import static android.app.Activity.RESULT_CANCELED;
//import static android.app.Activity.RESULT_OK;
//
///**
// * Created by woga1 on 2017-08-20.
// */
//
//public class Bluetooth {
//
//    //runs while listening for incoming connections.
//    private class ConnectTask extends AsyncTask<Void, Void, Boolean> {
//
//        //블루투스 연결중
//        private BluetoothSocket mBluetoothSocket = null;
//        private BluetoothDevice mBluetoothDevice = null;
//
//        ConnectTask(BluetoothDevice bluetoothDevice) {
//            mBluetoothDevice = bluetoothDevice;
//            mConnectedDeviceName = bluetoothDevice.getName();
//
//            //SPP
//            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
//
//            // Get a BluetoothSocket for a connection with the
//            // given BluetoothDevice
//            try {
//                mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(uuid);
//                Log.d( TAG, "create socket for "+mConnectedDeviceName);
//
//            } catch (IOException e) {
//                Log.e( TAG, "socket create failed " + e.getMessage());
//            }
//            Log.e("ConnectTask","true");
////            mConnectionStatus.setText("connecting...");
//            Toast.makeText(getApplicationContext(),"connected to "+mConnectedDeviceName,Toast.LENGTH_SHORT).show();
//        }
//
//
//        @Override
//        protected Boolean doInBackground(Void... params) {
//
//            // Always cancel discovery because it will slow down a connection
//            mBluetoothAdapter.cancelDiscovery();
//            Log.e("doInBackground","true");
//            // Make a connection to the BluetoothSocket
//            try {
//                // This is a blocking call and will only return on a
//                // successful connection or an exception
//                mBluetoothSocket.connect();
//            } catch (IOException e) {
//                // Close the socket
//                try {
//                    mBluetoothSocket.close();
//                } catch (IOException e2) {
//                    Log.e(TAG, "unable to close() " +
//                            " socket during connection failure", e2);
//                }
//
//                return false;
//            }
//
//            return true;
//        }
//
//
//        @Override
//        protected void onPostExecute(Boolean isSucess) {
//            Log.e("onPostExecute","true");
//            if ( isSucess ) {
//                connected(mBluetoothSocket);
//            }
//            else{
//
//                isConnectionError = true;
//                Log.d( TAG,  "Unable to connect device");
//                showErrorDialog("Unable to connect device");
//            }
//        }
//    }
//
//
//    public void connected( BluetoothSocket socket ) {
//        mConnectedTask = new ConnectedTask(socket);
//        mConnectedTask.execute();
//        Log.e("message12","true");
//    }
//
//
//    /**
//     * This thread runs during a connection with a remote device.
//     * It handles all incoming and outgoing transmissions.
//     */
//    private class ConnectedTask extends AsyncTask<Void, String, Boolean> {
//        //블루투스 연결완료
//        private InputStream mInputStream = null;
//        private OutputStream mOutputStream = null;
//        private BluetoothSocket mBluetoothSocket = null;
//
//        ConnectedTask(BluetoothSocket socket){
//
//            mBluetoothSocket = socket;
//            try {
//                mInputStream = mBluetoothSocket.getInputStream();
//                mOutputStream = mBluetoothSocket.getOutputStream();
//            } catch (IOException e) {
//                Log.e(TAG, "socket not created", e );
//            }
//
//            Log.d( TAG, "connected to "+mConnectedDeviceName);
////            mConnectionStatus.setText( "connected to "+mConnectedDeviceName);
//            Toast.makeText(getApplicationContext(),"connected to "+mConnectedDeviceName,Toast.LENGTH_SHORT).show();
//            Log.e("ConnectedTask","true");
//        }
//
//
//        @Override
//        protected Boolean doInBackground(Void... params) {
//
//            byte [] readBuffer = new byte[1024];
//            int readBufferPosition = 0;
//            Log.e("doInBackground1024","true");
//            // Keep listening to the InputStream while connected
//            while (true) {
//
//                if ( isCancelled() )
//                    return false;
//
//                try {
//                    int bytesAvailable = mInputStream.available();
//                    if(bytesAvailable > 0) {
//
//                        byte[] packetBytes = new byte[bytesAvailable];
//                        // Read from the InputStream
//                        mInputStream.read(packetBytes);
//
//                        for(int i=0;i<bytesAvailable;i++) {
//
//                            byte b = packetBytes[i];
//                            if(b == '\n')
//                            {
//                                byte[] encodedBytes = new byte[readBufferPosition];
//                                System.arraycopy(readBuffer, 0, encodedBytes, 0,
//                                        encodedBytes.length);
//                                String recvMessage = new String(encodedBytes, "UTF-8");
//
//                                readBufferPosition = 0;
//
//                                Log.d(TAG, "recv message: " + recvMessage);
//                                publishProgress(recvMessage);
//                            }
//                            else
//                            {
//                                readBuffer[readBufferPosition++] = b;
//                            }
//                        }
//                    }
//                } catch (IOException e) {
//
//                    Log.e(TAG, "disconnected", e);
//                    return false;
//                }
//            }
//
//        }
//
//        @Override
//        protected void onProgressUpdate(String... recvMessage) {
//            Log.e("onProgressUpdate","true");
////            mConversationArrayAdapter.insert(mConnectedDeviceName + ": " + recvMessage[0], 0);
//        }
//
//        @Override
//        protected void onPostExecute(Boolean isSucess) {
//            super.onPostExecute(isSucess);
//            Log.e("onPostExecute","true");
//            if ( !isSucess ) {
//
//
//                closeSocket();
//                Log.d(TAG, "Device connection was lost");
//                isConnectionError = true;
//                showErrorDialog("Device connection was lost");
//            }
//        }
//
//        @Override
//        protected void onCancelled(Boolean aBoolean) {
//            super.onCancelled(aBoolean);
//            Log.e("onCancelled","true");
//            closeSocket();
//        }
//
//        void closeSocket(){
//            Log.e("closeSocket","true");
//            try {
//
//                mBluetoothSocket.close();
//                Log.d(TAG, "close socket()");
//
//            } catch (IOException e2) {
//
//                Log.e(TAG, "unable to close() " +
//                        " socket during connection failure", e2);
//            }
//        }
//
//        void write(String msg){
//            Log.e("write","true");
//            //msg += "\n";
//
//            try {
//                mOutputStream.write(msg.getBytes());
//                mOutputStream.flush();
//            } catch (IOException e) {
//                //블루투스 끊길떄 여기로 온다
//                Log.e(TAG, "Exception during send", e );
//                Log.e("unbind", "bluetooth");
////                if ( mConnectedTask == null ) {
////                    Log.e("if", "BCD" );
////                    Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
////                    final BluetoothDevice[] pairedDevices = devices.toArray(new BluetoothDevice[0]);
////                    Log.e("mConnectedTask != null","true");
////
////                    ConnectTask task = new ConnectTask(pairedDevices[0]);
////                    Log.e("List",pairedDevices[0].toString());
////                    Log.e("List","true");
////                    task.execute();
////                }
////                Log.e("notif", "BCD" );
////                Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
////                final BluetoothDevice[] pairedDevices = devices.toArray(new BluetoothDevice[0]);
//////                Log.e("mConnectedTask != null","true");
////
////                ConnectTask task = new ConnectTask(pairedDevices[0]);
////                Log.e("List",pairedDevices[0].toString());
////                Log.e("List","true");
////                task.execute();
//            }
//
////            mInputEditText.setText(" ");
//        }
//    }
//
//
//    public void showPairedDevicesListDialog()
//    {
//        //연결된 디바이스 목록 보여줌
//        Log.e("showPairedDevicesLis","true");
//        //paried된 디바이스 목록 보여줌
//        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
//        final BluetoothDevice[] pairedDevices = devices.toArray(new BluetoothDevice[0]);
//
//        if ( pairedDevices.length == 0 ){
//            showQuitDialog( "연결 가능한 디바이스가 없습니다.");
//            return;
//        }
//
//        String[] items;
//        items = new String[pairedDevices.length];
//        for (int i=0;i<pairedDevices.length;i++) {
//            items[i] = pairedDevices[i].getName();
//        }
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("블루투스 연결");
//        builder.setCancelable(false);
//        builder.setItems(items, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//
//                // Attempt to connect to the device
//                ConnectTask task = new ConnectTask(pairedDevices[which]);
//                task.execute();
//            }
//        });
//        builder.create().show();
//    }
//
//
//
//    public void showErrorDialog(String message)
//    {
//        Log.e("showErrorDialog","true");
////        Toast.makeText(getApplicationContext(),"블루투스 연결 실패",Toast.LENGTH_SHORT).show();
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Quit");
//        builder.setCancelable(false);
//        builder.setMessage(message);
//        builder.setPositiveButton("OK",  new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                dialog.dismiss();
//                if ( isConnectionError  ) {
//                    isConnectionError = false;
//                    Toast.makeText(getApplicationContext(),"블루투스 연결 실패",Toast.LENGTH_SHORT).show();
////                    finish();
//                }
//            }
//        });
//        builder.create().show();
//    }
//
//
//    public void showQuitDialog(String message)
//    {
//        //블루투스 연결가능한 매체 갯수가 0개이거나 블루투스 연결 거부 했을 때
//        Log.e("showQuitDialog","true");
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Quit");
//        builder.setCancelable(false);
//        builder.setMessage(message);
//        builder.setPositiveButton("OK",  new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                finish();
//            }
//        });
//        builder.create().show();
//    }
//
//    void sendMessage(String msg){
////메시지보내기
//        if ( mConnectedTask != null ) {
//            mConnectedTask.write(msg);
//            Log.d(TAG, "send message: " + msg);
//            Log.e("message",msg);
////            mConversationArrayAdapter.insert("Me:  " + msg, 0);
//            mConversationArrayAdapter.insert(msg, 0);
//        }
//    }
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        //블루투스 연결
//        if(requestCode == REQUEST_BLUETOOTH_ENABLE){
//            if (resultCode == RESULT_OK){
//                //허용이 안되어 있다가 허용으로 바꿨을 때
//                showPairedDevicesListDialog();
//            }
//            if(resultCode == RESULT_CANCELED){
//                showQuitDialog( "Handlear을 이용하시려면 \n[블루투스] 를 허용해 주세요");
//            }
//        }
//    }
//}
