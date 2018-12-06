package com.sweetsjie.smarthometerminal;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button connectBt;
    private EditText ip;
    private EditText inputRomotePort;
    private EditText inputLocalPort;
    private EditText input;
    private TextView show;
    private ImageButton settingsBt;
    private ImageButton menuBt;


    private String result = "";

    private Handler handler = null;

    private AlertDialog.Builder builder;
    private View settingView;
    AlertDialog dialog;


    private int localPort = 9000;  //本地端口
    private int romotePort = 8000; //远程端口
    InetAddress address = InetAddress.getByName("192.168.2.194"); //远程IP

    DatagramSocket socket_UDP = null;  //UDP协议Socket
    DatagramPacket DP = null;  //UDP协议数据包
    public static String string = null;  //指令

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    public MainActivity() throws UnknownHostException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);


        builder = new AlertDialog.Builder(this);
        settingView = View.inflate(this,R.layout.layout_settings,null);
        builder.setCancelable(false);
        dialog = builder.create();
        dialog.setView(settingView);
//        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //dialog.show();
            }
        });

        ip = settingView.findViewById(R.id.ip);
        inputRomotePort = settingView.findViewById(R.id.targetPort);
        inputLocalPort = settingView.findViewById(R.id.localPort);
        input = settingView.findViewById(R.id.input);
        show = settingView.findViewById(R.id.show);
        menuBt = findViewById(R.id.menuBt);
        settingsBt = findViewById(R.id.settingsBt);

        settingsBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        ip.setText("192.168.2.194");
        inputRomotePort.setText("8000");
        inputLocalPort.setText("10000");

        handler = new Handler();

        connectBt = settingView.findViewById(R.id.connect);
        connectBt.setOnClickListener(this);

        Thread receiveThread = new UdpReceiveThread();
        receiveThread.start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.connect:
                sendCmd("isReady?");

//                while (true){
//                    if (result.equals("ready")) {
//                        Toast.makeText(MainActivity.this,"连接控制终端成功",Toast.LENGTH_SHORT).show();
//                        break;
//                    }
//                    try {
//                        sleep(500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
                dialog.dismiss();
        }
    }

    //接收线程
    public class UdpReceiveThread extends Thread {
        private final String TAG = "UdpReceiveThread";

        @Override
        public void run() {
            while (isAlive()) { //循环接收，isAlive() 判断防止无法预知的错误
                try {
                    sleep(1000); //让它好好休息一会儿
                    Log.d(TAG, "start receive");
                    localPort = Integer.parseInt(inputLocalPort.getText().toString());
                    if (socket_UDP == null)
                        socket_UDP = new DatagramSocket(localPort); //新建UDP类型Socket绑定本地端口
                    byte data[] = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(data, data.length);
                    socket_UDP.receive(packet); //阻塞式，接收发送方的 packet
                    result = new String(packet.getData(), packet.getOffset(), packet.getLength()); //packet 转换
                    //show.setText(result);
                    handler.post(runnableUi);
                    Log.d(TAG, "UDP result: " + result);
                    socket_UDP.close(); //必须及时关闭 socket，否则会出现 error
                    socket_UDP = null;

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "receive error");
                    break; //当 catch 到错误时，跳出循环
                }
            }
        }
    }

    //UDP数据发送
    public void sendCmd(String str) {
        string = str;
        Thread t = new UDPSendThread();
        t.start();
    }

    //发送数据线程
    public class UDPSendThread extends Thread {
        @Override
        public void run() {
            sendData_UDP(string);
        }
    }

    //UDP发送数据基层代码
    public void sendData_UDP(String str) {
        try {
            localPort = Integer.parseInt(inputLocalPort.getText().toString());
            romotePort = Integer.parseInt(inputRomotePort.getText().toString());
            address = InetAddress.getByName(ip.getText().toString());

            Log.d("send", "info" + address + localPort + romotePort);
            if (socket_UDP == null)
                socket_UDP = new DatagramSocket(localPort); //新建UDP类型Socket绑定本地端口
            byte[] byteDate = str.getBytes();
            DP = new DatagramPacket(byteDate, byteDate.length, address, romotePort); //将数据装包发送给远程IP与端口
            socket_UDP.send(DP); //发送数据包
//                socket_UDP.close();
//                socket_UDP = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 构建Runnable对象，在runnable中更新界面
    Runnable runnableUi = new Runnable() {
        @Override
        public void run() {
            //更新界面
            show.setText(result);
        }
    };



}
