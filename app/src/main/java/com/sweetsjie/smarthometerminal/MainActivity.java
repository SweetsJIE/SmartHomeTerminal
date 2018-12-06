package com.sweetsjie.smarthometerminal;

import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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

    //控件
    private Button      connectBt;
    private EditText    ip;
    private EditText    inputRomotePort;
    private EditText    inputLocalPort;
    private EditText    input;
    private TextView    show;
    private ImageButton settingsBt;
    private ImageButton menuBt;
    private TextView    aboutTv;


    public String result = "";
    private PopupWindow popupWindow;
    private int from = 0;

    private Handler handler = null;

    private AlertDialog.Builder builder;
    private View settingView;
    private View aboutView;
    AlertDialog dialog;


    private int localPort = 9000;  //本地端口
    private int romotePort = 8000; //远程端口
    InetAddress address = InetAddress.getByName("192.168.2.194"); //远程IP

    DatagramSocket socket_UDP = null;  //UDP协议Socket
    DatagramPacket DP = null;  //UDP协议数据包
    public static String string = null;  //指令

    private AboutFragment aboutFragment;
    private FragmentManager fragmentManager;
    private WelcomeFragment welcomeFragment;
    private LedFragment ledFragment;

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


        //窗口控件初始化
        builder = new AlertDialog.Builder(this);
        settingView = View.inflate(this,R.layout.layout_settings,null);
        builder.setCancelable(false);
        dialog = builder.create();
        dialog.setView(settingView);
//        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        aboutView = getLayoutInflater().inflate(R.layout.left_menu, null);

        //fragment管理器初始化
        fragmentManager = getSupportFragmentManager();

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                //dialog.show();
//            }
//        });

        ip = settingView.findViewById(R.id.ip);
        inputRomotePort = settingView.findViewById(R.id.targetPort);
        inputLocalPort = settingView.findViewById(R.id.localPort);
        input = settingView.findViewById(R.id.input);
        show = settingView.findViewById(R.id.show);
        menuBt = findViewById(R.id.menuBt);
        settingsBt = findViewById(R.id.settingsBt);


        setTabSelection(Location.MAIN.ordinal());


//        aboutTv = aboutView.findViewById(R.id.aboutTv);
//        aboutTv.setText("about");
//        aboutTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(MainActivity.this,"ablou click",Toast.LENGTH_LONG).show();
//            }
//        });
//        aboutTv.setOnClickListener(this);



        menuBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                from = Location.LEFT.ordinal();
                initPopupWindow();
            }


        });
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

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

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
                break;

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



    protected void initPopupWindow(){
        View popupWindowView = getLayoutInflater().inflate(R.layout.left_menu, null);
        //内容，高度，宽度
//        if(Location.BOTTOM.ordinal() == from){
//            popupWindow = new PopupWindow(popupWindowView, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
//        }else{
            popupWindow = new PopupWindow(popupWindowView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.FILL_PARENT, true);
//        }
        //动画效果
        if(Location.LEFT.ordinal() == from) {
            popupWindow.setAnimationStyle(R.style.AnimationLeftFade);
        }
        //菜单背景色
        ColorDrawable dw = new ColorDrawable(0xffffffff);
        popupWindow.setBackgroundDrawable(dw);
        //宽度
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        //高度
        //popupWindow.setHeight(LayoutParams.FILL_PARENT);
        //显示位置
        if(Location.LEFT.ordinal() == from){
            popupWindow.showAtLocation(getLayoutInflater().inflate(R.layout.activity_main, null), Gravity.LEFT, 0, 2000);
        }

        //设置背景半透明
        backgroundAlpha(0.5f);
        //关闭事件
        popupWindow.setOnDismissListener(new popupDismissListener());

        popupWindowView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
				/*if( popupWindow!=null && popupWindow.isShowing()){
					popupWindow.dismiss();
					popupWindow=null;
				}*/
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
                return false;
            }
        });

        TextView about = popupWindowView.findViewById(R.id.aboutTv);
        TextView led = popupWindowView.findViewById(R.id.ledTv);

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTabSelection(Location.ABOUT.ordinal());
                popupWindow.dismiss();
            }
        });
        led.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTabSelection(Location.LED.ordinal());
                popupWindow.dismiss();
            }
        });

        //about.setText("about");
//        Button open = (Button)popupWindowView.findViewById(R.id.open);
//        Button save = (Button)popupWindowView.findViewById(R.id.save);
//        Button close = (Button)popupWindowView.findViewById(R.id.close);

//
//        open.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context, "Open", Toast.LENGTH_LONG).show();
//                popupWindow.dismiss();
//            }
//        });
//
//        save.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context, "Open", Toast.LENGTH_LONG).show();
//                popupWindow.dismiss();
//            }
//        });
//
//        close.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context, "Open", Toast.LENGTH_LONG).show();
//                popupWindow.dismiss();
//            }
//        });
    }

    /**
     * 根据传入的index参数来设置选中的tab页。
     *
     * @param index
     * 每个tab页对应的下标。0表示消息，1表示联系人，2表示动态，3表示设置。
     */
    private void setTabSelection(int index) {
        // 每次选中之前先清楚掉上次的选中状态
        //clearSelection();
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (index) {
            case 1:
                if (welcomeFragment == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    welcomeFragment = new WelcomeFragment();
                    transaction.add(R.id.fragmentLayout, welcomeFragment);
                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(welcomeFragment);
                }
                break;
            case 2:
                if (aboutFragment == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    aboutFragment = new AboutFragment();
                    transaction.add(R.id.fragmentLayout, aboutFragment);
                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(aboutFragment);
                }
                break;
            case 3:
                if (ledFragment == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    ledFragment = new LedFragment();
                    transaction.add(R.id.fragmentLayout, ledFragment);
                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(ledFragment);
                }
                break;
        }
        transaction.commit();
    }

    /**
     * 添加新笔记时弹出的popWin关闭的事件，主要是为了将背景透明度改回来
     *
     */
    class popupDismissListener implements PopupWindow.OnDismissListener{

        @Override
        public void onDismiss() {
            backgroundAlpha(1f);
            //setTabSelection(0);
        }

    }

    /**
     * 设置添加屏幕的背景透明度
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }
    /**
     * 菜单弹出方向
     *
     */
    public enum Location {
        LEFT,
        MAIN,
        ABOUT,
        LED,
        SOCKET;

    }

    /**
     * 将所有的Fragment都置为隐藏状态。
     *
     * @param transaction
     * 用于对Fragment执行操作的事务
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (aboutFragment != null) {
            transaction.hide(aboutFragment);
        }
        if (welcomeFragment != null) {
            transaction.hide(welcomeFragment);
        }
        if (ledFragment != null) {
            transaction.hide(ledFragment);
        }
//        if (settingFragment != null) {
//            transaction.hide(settingFragment);
//        }
    }

    /**
     * 清除掉所有的选中状态。
     */
    private void clearSelection() {
//        aboutFragment.setBackgroundColor(0xffffffff);
//        welcomeFragment.setBackgroundColor(0xffffffff);
//        ledFragment.setBackgroundColor(0xffffffff);
//        settingLayout.setBackgroundColor(0xffffffff);
    }





}
