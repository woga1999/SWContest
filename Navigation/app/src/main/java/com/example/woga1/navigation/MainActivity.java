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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.woga1.navigation.SaveData.GridViewVO;
import com.example.woga1.navigation.SaveData.ImageGridViewCustomAdapter;
import com.example.woga1.navigation.Navigation.ReadyActivity;
import com.example.woga1.navigation.SaveData.DestinationActivity;
import com.example.woga1.navigation.SaveData.FavoriteActivity;
import com.example.woga1.navigation.SaveData.NoticeActivity;
import com.example.woga1.navigation.Search.POIActivity;
import com.example.woga1.navigation.Search.SearchActivity;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    //Main화면


    String[] names = {"","","","","","","","","","","","","","",""} ;
    List<String> destinationLists;
    public Location nowPlace = null;
    static final int[] images={R.drawable.mapholder,R.drawable.mapholder,R.drawable.mapholder,R.drawable.mapholder,R.drawable.mapholder,
            R.drawable.mapholder,R.drawable.mapholder,R.drawable.mapholder,R.drawable.mapholder
            ,R.drawable.mapholder,R.drawable.mapholder,R.drawable.mapholder,R.drawable.mapholder,R.drawable.mapholder,R.drawable.mapholder};

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

    public static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alertCheckGPS();
        mContext = this;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);}
        LinearLayout search = (LinearLayout) findViewById(R.id.search);
//        imageGridView = (GridView) findViewById(R.id.gridView);

        ImageButton favoriteButton = (ImageButton) findViewById(R.id.destination);
        ImageButton poiButton = (ImageButton) findViewById(R.id.poiButton);
        ImageButton destinationButton = (ImageButton) findViewById(R.id.destinationButton);
//        ImageButton button4 = (ImageButton) findViewById(R.id.button4);
        ImageButton notificationButton = (ImageButton) findViewById(R.id.notificationButton);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFFFFFFFF));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_bar);

//        search.setMovementMethod(null);

        //검색화면
        search.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }

        });

        //즐겨찾기 화면
        favoriteButton.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, FavoriteActivity.class));

            }

        });

        //주변검색 화면
        poiButton.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, POIActivity.class));
            }

        });

        //최근목적지 화면
        destinationButton.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DestinationActivity.class);
                intent.putExtra("destination", names);
                startActivityForResult(intent, 1);
            }

        });

        //블루투스 화면
//        button4.setOnClickListener(new EditText.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, BluetoothActivity.class);
//                startActivityForResult(intent, 1);
//            }
//
//        });

        //공지사항 화면
        notificationButton.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NoticeActivity.class);
                startActivityForResult(intent, 1);
            }

        });

        //그리드뷰에 들어가는 배열에 sharedPreference의 값 넣기
        SharedPreferences sharedPreferences = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String listToString = sharedPreferences.getString("Destination", null);
        if(listToString != null) {
            Log.e("test","null이 아님");
            Gson gson = new Gson();
            List<String> destinationLists;
            destinationLists = gson.fromJson(listToString, List.class);

            for (int i = 0; i < destinationLists.size(); i++) {
//            for (int i = 0; i < 15; i++) {
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

        //그리드뷰 클릭이벤트
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
                    Intent intent = new Intent(MainActivity.this, ReadyActivity.class);
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
//            showErrorDialog("This device is not implement Bluetooth.");
//            return;
//        }
//
//        if (!mBluetoothAdapter.isEnabled()) {
//            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(intent, REQUEST_BLUETOOTH_ENABLE);
//        }
//        else {
//            Log.d(TAG, "Initialisation successful.");
//
//            //2. 페어링 되어 있는 블루투스 장치들의 목록을 보여줍니다.
//            //3. 목록에서 블루투스 장치를 선택하면 선택한 디바이스를 인자로 하여
//            //   doConnect 함수가 호출됩니다.
//            showPairedDevicesListDialog();
//        }


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


    public void bluetoothCheck()
    {

        Log.d( TAG, "Initalizing Bluetooth adapter...");
        //1.블루투스 사용 가능한지 검사합니다.
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            showErrorDialog("This device is not implement Bluetooth.");
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_BLUETOOTH_ENABLE);
        }
        else {
            Log.d(TAG, "Initialisation successful.");

            //2. 페어링 되어 있는 블루투스 장치들의 목록을 보여줍니다.
            //3. 목록에서 블루투스 장치를 선택하면 선택한 디바이스를 인자로 하여
            //   doConnect 함수가 호출됩니다.
            showPairedDevicesListDialog();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("onStart","true");
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


    private ArrayList<GridViewVO> getImageandText()
    {

        ArrayList<GridViewVO> players = new ArrayList<GridViewVO>();
        for(int i=0; i<15;i++)
        {
            players.add(new GridViewVO(names[i],images[i]));
        }

        return players;
    }

    //목적지를 입력받아 위도, 경도로 바꾸기기
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
            }
        }
    }

    @Override
    protected void onDestroy() {
        Log.e("onDestroy","true");
        super.onDestroy();
    }

    //블루투스 연결중
    //runs while listening for incoming connections.
    private class ConnectTask extends AsyncTask<Void, Void, Boolean> {

        private BluetoothSocket mBluetoothSocket = null;
        private BluetoothDevice mBluetoothDevice = null;

        ConnectTask(BluetoothDevice bluetoothDevice) {
            mBluetoothDevice = bluetoothDevice;
            mConnectedDeviceName = bluetoothDevice.getName();

            //SPP
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

            try {
                mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(uuid);
                Log.d( TAG, "create socket for "+mConnectedDeviceName);

            } catch (IOException e) {
                Log.e( TAG, "socket create failed " + e.getMessage());
            }

        }


        @Override
        protected Boolean doInBackground(Void... params) {

            // Always cancel discovery because it will slow down a connection
            mBluetoothAdapter.cancelDiscovery();

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

            if ( isSucess ) {
                connected(mBluetoothSocket);
            }
            else{

                isConnectionError = true;
                Log.d( TAG,  "Unable to connect device");
                Toast.makeText(getApplicationContext(),"Unable to connect device",Toast.LENGTH_SHORT).show();
//                showErrorDialog("Unable to connect device");
            }
        }
    }

    public void connected( BluetoothSocket socket ) {
        mConnectedTask = new ConnectedTask(socket);
        mConnectedTask.execute();
    }


    //블루투스 연결완료
    private class ConnectedTask extends AsyncTask<Void, String, Boolean> {

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
        }



        @Override
        protected Boolean doInBackground(Void... params) {

            byte [] readBuffer = new byte[1024];
            int readBufferPosition = 0;

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

            mConversationArrayAdapter.insert(mConnectedDeviceName + ": " + recvMessage[0], 0);
        }

        @Override
        protected void onPostExecute(Boolean isSucess) {
            super.onPostExecute(isSucess);

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

            closeSocket();
        }

        void closeSocket(){

            try {

                mBluetoothSocket.close();
                Log.d(TAG, "close socket()");

            } catch (IOException e2) {

                Log.e(TAG, "unable to close() " +
                        " socket during connection failure", e2);
            }
        }

        void write(String msg){

            msg += "\n";

            try {
                mOutputStream.write(msg.getBytes());
                mOutputStream.flush();
            } catch (IOException e) {
                Log.e(TAG, "Exception during send", e );
            }

//            mInputEditText.setText(" ");
        }
    }


    //연결된 디바이스 목록 보여줌
    public void showPairedDevicesListDialog()
    {

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


    //블루투스 연결 실패
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

    //블루투스 연결가능한 매체 갯수가 0개이거나 블루투스 연결 거부 했을 때
    public void showQuitDialog(String message)
    {

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

    //메시지보내기
    public void sendMessage(String msg){
        if ( mConnectedTask != null ) {
            mConnectedTask.write(msg);
            Log.d(TAG, "send message: " + msg);
//            mConversationArrayAdapter.insert("Me:  " + "100 14.", 0);
        }
    }


    //블루투스 연결
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

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

    //네트워크 연결
    public boolean isNetworkConnected(Context context){
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




    //gps체크
    private void alertCheckGPS() { //gps 꺼져있으면 켤 껀지 체크
//
//        Intent intent = new Intent(NoticeActivity.this, gpsCheck.class);
//        startActivityForResult(intent, 1);
        LocationManager locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//        if(!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        if(!locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
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
