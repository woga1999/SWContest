package com.example.woga1.navigation;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MenuActivity extends AppCompatActivity {
    //거의 Main화면이다. 맨처음 나오는 Activity
    String[] names = {"","","","","","","","","","","","","","",""} ;
    List<String> destinationLists;
    public Location nowPlace = null;
    static final int[] images={R.drawable.mapholder,R.drawable.mapholder,R.drawable.mapholder,R.drawable.mapholder,R.drawable.mapholder,
            R.drawable.mapholder,R.drawable.mapholder,R.drawable.mapholder,R.drawable.mapholder,R.drawable.mapholder,
            R.drawable.mapholder,R.drawable.mapholder,R.drawable.mapholder,R.drawable.mapholder,R.drawable.mapholder,};
    private GridView gv;
    private String longitude;
    private String latitude;

    private final int REQUEST_BLUETOOTH_ENABLE = 100;

    private TextView mConnectionStatus;
    private EditText mInputEditText;

    ConnectedTask mConnectedTask = null;
    static BluetoothAdapter mBluetoothAdapter;
    private String mConnectedDeviceName = null;
    private ArrayAdapter<String> mConversationArrayAdapter;
    static boolean isConnectionError = false;
    private static final String TAG = "BluetoothClient";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//        }
//        nowPlace =  myLocation();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);}
        EditText search = (EditText) findViewById(R.id.search);
//        imageGridView = (GridView) findViewById(R.id.gridView);

        Button button1 = (Button) findViewById(R.id.button1);
        Button button2 = (Button) findViewById(R.id.button2);
        Button button3 = (Button) findViewById(R.id.button3);
        Button button4 = (Button) findViewById(R.id.button4);
        Button button5 = (Button) findViewById(R.id.button5);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFFFFFFFF));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_bar);

        search.setMovementMethod(null);





//        ArrayList<Integer> imageList = new ArrayList<>();
//        imageList.add(R.drawable.mapholder);
//        imageList.add(R.drawable.mapholder);
//        imageList.add(R.drawable.mapholder);
//        imageList.add(R.drawable.mapholder);
//        imageList.add(R.drawable.mapholder);
//        imageList.add(R.drawable.mapholder);
//        imageList.add(R.drawable.mapholder);
//        imageList.add(R.drawable.mapholder);
//        imageList.add(R.drawable.mapholder);
//        imageList.add(R.drawable.mapholder);
//        imageList.add(R.drawable.mapholder);
//        imageList.add(R.drawable.mapholder);
//        imageList.add(R.drawable.mapholder);
//        imageList.add(R.drawable.mapholder);
//        imageList.add(R.drawable.mapholder);

//        ImageGridViewCustomAdapter customAdapter = new ImageGridViewCustomAdapter(this,imageList);
//        imageGridView.setAdapter(customAdapter);
//
//        imageGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                Toast.makeText(MenuActivity.this,""+id,Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(MenuActivity.this, ReadyActivity.class));
//            }
//        });

        search.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuActivity.this, SearchActivity.class));
            }

        });

        button1.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuActivity.this, FavoriteActivity.class));
            }

        });

        button2.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuActivity.this, NearActivity.class));
            }

        });

        button3.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, DestinationActivity.class);
                intent.putExtra("destination", names);
                startActivityForResult(intent, 1);
            }

        });

        button4.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, BluetoothActivity.class);
                startActivityForResult(intent, 1);
            }

        });

        button5.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, NoticeActivity.class);
                startActivityForResult(intent, 1);
            }

        });

//        SharedPreferences test = getSharedPreferences("pref", MODE_PRIVATE);
//        String firstData = test.getString("First", "a");
////        Toast.makeText(getApplicationContext(),firstData,Toast.LENGTH_SHORT).show();
//
//        SharedPreferences sharedPreferences = getSharedPreferences("pref", Activity.MODE_PRIVATE);
//        Gson gson = new Gson();
//        String listToString = sharedPreferences.getString("First", "a");
//        List<String> destinationLists;
//        destinationLists=gson.fromJson(listToString,List.class);
//        for(int i=0; i<destinationLists.size() ; i++)
//        {
//            Log.e("test",destinationLists.get(i));
//        }

        SharedPreferences sharedPreferences = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String listToString = sharedPreferences.getString("Destination", null);
        if(listToString != null) {
            Log.e("test","null이 아님");
            Gson gson = new Gson();
            List<String> destinationLists;
            destinationLists = gson.fromJson(listToString, List.class);

            for (int i = 0; i < destinationLists.size(); i++) {
                Log.e("destination", destinationLists.get(i));
                names[i] = destinationLists.get(i);
            }

        }
        gv = (GridView) findViewById(R.id.gridView);

        //Adapter
        ImageGridViewCustomAdapter adapter = new ImageGridViewCustomAdapter(this, getImageandText());
        gv.setAdapter(adapter);

//        ImageGridViewCustomAdapter customAdapter = new ImageGridViewCustomAdapter(this,imageList);
//        imageGridView.setAdapter(customAdapter);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(names[position]=="")
                {
                    Log.e("position", "빈칸");
                }
                else {
                    Log.e("position", names[position]);
                    changeToLongitudeLatitude(names[position]);
//                changeToLongitudeLatitude("서울 영등포구 도림로53길 9");
                    Intent intent = new Intent(MenuActivity.this, ReadyActivity.class);
                    intent.putExtra("destination", names[position]);
                    intent.putExtra("longitude", longitude);
                    intent.putExtra("latitude", latitude);
                    startActivityForResult(intent, 1);
                }
            }
        });


//        Log.d( TAG, "Initalizing Bluetooth adapter...");
//        //1.블루투스 사용 가능한지 검사합니다.
//        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (mBluetoothAdapter == null) {
//            Log.e("aaa","1");
//            showErrorDialog("This device is not implement Bluetooth.");
//            return;
//        }
//
//        if (!mBluetoothAdapter.isEnabled()) {
//            //블루투스 허용되지 않았을 때
//            Log.e("aaa","2");
//            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(intent, REQUEST_BLUETOOTH_ENABLE);
//        }
//        else {
//            //블루투스 허용되어 있을 때
//            Log.e("aaa","3");
//            Log.d(TAG, "Initialisation successful.");
//
//            //2. 페어링 되어 있는 블루투스 장치들의 목록을 보여줍니다.
//            //3. 목록에서 블루투스 장치를 선택하면 선택한 디바이스를 인자로 하여
//            //   doConnect 함수가 호출됩니다.
//            showPairedDevicesListDialog();
//        }
//        chkGpsService();
        alertCheckGPS();

        if( !isNetworkConnected(this) ){
            Toast.makeText(getApplicationContext(),"YEs",Toast.LENGTH_LONG).show();
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("네트워크 연결 오류").setMessage("네트워크 연결 상태 확인 후 다시 시도해 주십시요.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick( DialogInterface dialog, int which )
                        {
                            finish();
                        }
                    }).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"네트워크 연결완료",Toast.LENGTH_LONG).show();
        }



    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("onStart","true");
//        alertCheckGPS();
//        if( !isNetworkConnected(this) ){
//            Toast.makeText(getApplicationContext(),"YEs",Toast.LENGTH_LONG).show();
//            new AlertDialog.Builder(this)
//                    .setIcon(android.R.drawable.ic_dialog_alert)
//                    .setTitle("네트워크 연결 오류").setMessage("네트워크 연결 상태 확인 후 다시 시도해 주십시요.")
//                    .setPositiveButton("확인", new DialogInterface.OnClickListener()
//                    {
//                        @Override
//                        public void onClick( DialogInterface dialog, int which )
//                        {
//                            finish();
//                        }
//                    }).show();
//        }
//        else{
//            Toast.makeText(getApplicationContext(),"NO",Toast.LENGTH_LONG).show();
//        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();  // Always call the superclass method first
        Log.e("onRestart","true");
        //alertCheckGPS();
        // Activity being restarted from stopped state
        if( !isNetworkConnected(this) ){
            Toast.makeText(getApplicationContext(),"YEs",Toast.LENGTH_LONG).show();
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("네트워크 연결 오류").setMessage("네트워크 연결 상태 확인 후 다시 시도해 주십시요.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick( DialogInterface dialog, int which )
                        {
                            finish();
                        }
                    }).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"네트워크 연결완료",Toast.LENGTH_LONG).show();
        }
    }


//    public Location nowLocation() {
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//        }
//    }

    private ArrayList<GridViewVO> getImageandText()
    {

        ArrayList<GridViewVO> players = new ArrayList<GridViewVO>();
        for(int i=0; i<15;i++)
        {
            players.add(new GridViewVO(names[i],images[i]));
        }

        return players;
    }

    private void changeToLongitudeLatitude(String destinations)
    {
        final Geocoder geocoder = new Geocoder(this);
        List<Address> list = null;
        List<Address> list1 = null;
        String start = "세종대학교";
        String destination = destinations;
        try {
            list = geocoder.getFromLocationName(
                    start, // 지역 이름
                    10); // 읽을 개수
            list1 = geocoder.getFromLocationName(destination, 10);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("test","입출력 오류 - 서버에서 주소변환시 에러발생");
        }

        if (list != null || list1 !=null) {
            if (list.size() == 0) {
                Toast.makeText(getApplicationContext(),"해당되는 주소 정보는 없습니다", Toast.LENGTH_LONG).show();
                //tv.setText("해당되는 주소 정보는 없습니다");
            }
            else if(list1.size() ==0) {
                Toast.makeText(getApplicationContext(),"해당되는 주소 정보는 없습니다", Toast.LENGTH_LONG).show();
            }
            else
            {
                Address addr = list.get(0);
                double startLat = addr.getLatitude();
                double startLon = addr.getLongitude();
                Address addr1 = list1.get(0);
                double endLat = addr1.getLatitude();
                double endLon = addr1.getLongitude();


                latitude= String.valueOf(endLat);
                longitude= String.valueOf(endLon);
//                Toast.makeText(getApplicationContext(),"start- 위도: "+String.valueOf(startLat)+" 경도: "+String.valueOf(startLon)+"  end- 위도:"+String.valueOf(endLat)+" 경도: "+String.valueOf(endLon), Toast.LENGTH_LONG).show();
                //tv.setText(list.get(0).toString());
                //          list.get(0).getCountryName();  // 국가명
                //          list.get(0).getLatitude();        // 위도
                //          list.get(0).getLongitude();    // 경도
            }
        }
    }

    @Override
    protected void onDestroy() {
        Log.e("onDestroy","true");
        super.onDestroy();
    }

    //runs while listening for incoming connections.
    private class ConnectTask extends AsyncTask<Void, Void, Boolean> {

        //블루투스 연결중
        private BluetoothSocket mBluetoothSocket = null;
        private BluetoothDevice mBluetoothDevice = null;

        ConnectTask(BluetoothDevice bluetoothDevice) {
            mBluetoothDevice = bluetoothDevice;
            mConnectedDeviceName = bluetoothDevice.getName();

            //SPP
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(uuid);
                Log.d( TAG, "create socket for "+mConnectedDeviceName);

            } catch (IOException e) {
                Log.e( TAG, "socket create failed " + e.getMessage());
            }
            Log.e("ConnectTask","true");
//            mConnectionStatus.setText("connecting...");
            Toast.makeText(getApplicationContext(),"connected to "+mConnectedDeviceName,Toast.LENGTH_SHORT).show();
        }


        @Override
        protected Boolean doInBackground(Void... params) {

            // Always cancel discovery because it will slow down a connection
            mBluetoothAdapter.cancelDiscovery();
            Log.e("doInBackground","true");
            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mBluetoothSocket.connect();
            } catch (IOException e) {
                // Close the socket
                try {
                    mBluetoothSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() " +
                            " socket during connection failure", e2);
                }

                return false;
            }

            return true;
        }


        @Override
        protected void onPostExecute(Boolean isSucess) {
            Log.e("onPostExecute","true");
            if ( isSucess ) {
                connected(mBluetoothSocket);
            }
            else{

                isConnectionError = true;
                Log.d( TAG,  "Unable to connect device");
                showErrorDialog("Unable to connect device");
            }
        }
    }


    public void connected( BluetoothSocket socket ) {
        mConnectedTask = new ConnectedTask(socket);
        mConnectedTask.execute();
    }


    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedTask extends AsyncTask<Void, String, Boolean> {
        //블루투스 연결완료
        private InputStream mInputStream = null;
        private OutputStream mOutputStream = null;
        private BluetoothSocket mBluetoothSocket = null;

        ConnectedTask(BluetoothSocket socket){

            mBluetoothSocket = socket;
            try {
                mInputStream = mBluetoothSocket.getInputStream();
                mOutputStream = mBluetoothSocket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "socket not created", e );
            }

            Log.d( TAG, "connected to "+mConnectedDeviceName);
//            mConnectionStatus.setText( "connected to "+mConnectedDeviceName);
            Toast.makeText(getApplicationContext(),"connected to "+mConnectedDeviceName,Toast.LENGTH_SHORT).show();
            Log.e("ConnectedTask","true");
        }


        @Override
        protected Boolean doInBackground(Void... params) {

            byte [] readBuffer = new byte[1024];
            int readBufferPosition = 0;
            Log.e("doInBackground1024","true");
            // Keep listening to the InputStream while connected
            while (true) {

                if ( isCancelled() ) return false;

                try {

                    int bytesAvailable = mInputStream.available();

                    if(bytesAvailable > 0) {

                        byte[] packetBytes = new byte[bytesAvailable];
                        // Read from the InputStream
                        mInputStream.read(packetBytes);

                        for(int i=0;i<bytesAvailable;i++) {

                            byte b = packetBytes[i];
                            if(b == '\n')
                            {
                                byte[] encodedBytes = new byte[readBufferPosition];
                                System.arraycopy(readBuffer, 0, encodedBytes, 0,
                                        encodedBytes.length);
                                String recvMessage = new String(encodedBytes, "UTF-8");

                                readBufferPosition = 0;

                                Log.d(TAG, "recv message: " + recvMessage);
                                publishProgress(recvMessage);
                            }
                            else
                            {
                                readBuffer[readBufferPosition++] = b;
                            }
                        }
                    }
                } catch (IOException e) {

                    Log.e(TAG, "disconnected", e);
                    return false;
                }
            }

        }

        @Override
        protected void onProgressUpdate(String... recvMessage) {
            Log.e("onProgressUpdate","true");
            mConversationArrayAdapter.insert(mConnectedDeviceName + ": " + recvMessage[0], 0);
        }

        @Override
        protected void onPostExecute(Boolean isSucess) {
            super.onPostExecute(isSucess);
            Log.e("onPostExecute","true");
            if ( !isSucess ) {


                closeSocket();
                Log.d(TAG, "Device connection was lost");
                isConnectionError = true;
                showErrorDialog("Device connection was lost");
            }
        }

        @Override
        protected void onCancelled(Boolean aBoolean) {
            super.onCancelled(aBoolean);
            Log.e("onCancelled","true");
            closeSocket();
        }

        void closeSocket(){
            Log.e("closeSocket","true");
            try {

                mBluetoothSocket.close();
                Log.d(TAG, "close socket()");

            } catch (IOException e2) {

                Log.e(TAG, "unable to close() " +
                        " socket during connection failure", e2);
            }
        }

        void write(String msg){
            Log.e("write","true");
            //msg += "\n";

            try {
                mOutputStream.write(msg.getBytes());
                mOutputStream.flush();
            } catch (IOException e) {
                Log.e(TAG, "Exception during send", e );
            }

            mInputEditText.setText(" ");
        }
    }


    public void showPairedDevicesListDialog()
    {
        //연결된 디바이스 목록 보여줌
        Log.e("showPairedDevicesLis","true");
        //paried된 디바이스 목록 보여줌
        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
        final BluetoothDevice[] pairedDevices = devices.toArray(new BluetoothDevice[0]);

        if ( pairedDevices.length == 0 ){
            showQuitDialog( "연결 가능한 디바이스가 없습니다.");
            return;
        }

        String[] items;
        items = new String[pairedDevices.length];
        for (int i=0;i<pairedDevices.length;i++) {
            items[i] = pairedDevices[i].getName();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("블루투스 연결");
        builder.setCancelable(false);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                // Attempt to connect to the device
                ConnectTask task = new ConnectTask(pairedDevices[which]);
                task.execute();
            }
        });
        builder.create().show();
    }



    public void showErrorDialog(String message)
    {
        Log.e("showErrorDialog","true");
//        Toast.makeText(getApplicationContext(),"블루투스 연결 실패",Toast.LENGTH_SHORT).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quit");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("OK",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                if ( isConnectionError  ) {
                    isConnectionError = false;
                    Toast.makeText(getApplicationContext(),"블루투스 연결 실패",Toast.LENGTH_SHORT).show();
//                    finish();
                }
            }
        });
        builder.create().show();
    }


    public void showQuitDialog(String message)
    {
        //블루투스 연결가능한 매체 갯수가 0개이거나 블루투스 연결 거부 했을 때
        Log.e("showQuitDialog","true");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quit");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("OK",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.create().show();
    }

//    void sendMessage(String msg){
////메시지보내기
//        if ( mConnectedTask != null ) {
//            mConnectedTask.write(msg);
//            Log.d(TAG, "send message: " + msg);
//            mConversationArrayAdapter.insert("Me:  " + msg, 0);
//        }
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //블루투스 연결
        if(requestCode == REQUEST_BLUETOOTH_ENABLE){
            if (resultCode == RESULT_OK){
                //허용이 안되어 있다가 허용으로 바꿨을 때
                showPairedDevicesListDialog();
            }
            if(resultCode == RESULT_CANCELED){
                showQuitDialog( "Handlear을 이용하시려면 \n[블루투스] 를 허용해 주세요");
            }
        }
    }


    public boolean isNetworkConnected(Context context){
        //네트워크 연결
        boolean isConnected = false;

        ConnectivityManager manager =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mobile.isConnected() || wifi.isConnected()){
            isConnected = true;
        }else{
            isConnected = false;
        }
        return isConnected;
    }





    private void alertCheckGPS() { //gps 꺼져있으면 켤 껀지 체크
//
//        Intent intent = new Intent(NoticeActivity.this, gpsCheck.class);
//        startActivityForResult(intent, 1);
        LocationManager locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Handlear을 이용하시려면 \n[위치] 권한을 허용해 주세요")
                    .setCancelable(false)
                    .setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    moveConfigGPS();
                                }
                            });
//                    .setNegativeButton("취소",
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    dialog.cancel();
//                                }
//                            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"GPS 연결완료",Toast.LENGTH_LONG).show();
        }
    }

    // GPS 설정화면으로 이동
    private void moveConfigGPS() {
        Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(gpsOptionsIntent);
    }

}
