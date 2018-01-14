package com.example.chat;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.lang.ref.WeakReference;
import java.text.BreakIterator;
import java.util.ArrayList;

public class SimpleChatActivity extends AppCompatActivity {
    public static String IP = "ip";
    public static String NICK = "nick";
    TextView nickText;
    TextView idText;
    TextView nickTextView;
    TextView buttonSend;
    EditText MSG;
    ListView simple_list_item;
    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    MqttClient sampleClient = null;

    String nick;
    String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_chat);

        nickText = (TextView) findViewById(R.id.nickText);
        idText = (TextView) findViewById(R.id.idText);
        MSG = (EditText) findViewById(R.id.MSG);


        nick = getIntent().getStringExtra(MainActivity.NICK);
        ip = getIntent().getStringExtra(MainActivity.IP);
//        nickTextView.setText(getIntent().getStringExtra(MainActivity.NICK));

        simple_list_item = (ListView) findViewById(R.id.simple_list_itemdd);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        simple_list_item.setAdapter(adapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                startMQTT();
            }
        }).start();
    }

    private static class MyHandler extends Handler {
        private final WeakReference<SimpleChatActivity> sActivity;

        MyHandler(SimpleChatActivity activity) {
            sActivity = new WeakReference<SimpleChatActivity>(activity);
        }

        public void handleMessage(Message msg) {
            SimpleChatActivity activity = sActivity.get();
            activity.listItems.add("[" + msg.getData().getString("NICK") + "] " +
                    msg.getData().getString("MSG"));
            activity.adapter.notifyDataSetChanged();
            activity.simple_list_item.setSelection(activity.listItems.size() - 1);
        }
    }

    Handler myHandler = new MyHandler(this);


    public void buttonSend(View view) {
       /* Message msg = myHandler.obtainMessage();
        Bundle b = new Bundle();
        b.putString("NICK", NICK);
        b.putString("MSG", MSG.getText().toString());
        msg.setData(b);
        myHandler.sendMessage(msg);

        MSG.setText("");*/


        MqttMessage message = new MqttMessage(MSG.getText().toString().getBytes());
        message.setQos(0);
        try {
            sampleClient.publish(nick, message);
        } catch (MqttException e) {
        }
    }

    public void messageArrived(MqttMessage mqttMessage) throws Exception {
        Message msg = myHandler.obtainMessage();
        Bundle b = new Bundle();
        b.putString("NICK", NICK);
        b.putString("MSG", mqttMessage.toString());
        msg.setData(b);
        myHandler.sendMessage(msg);
    }

    protected void onDestroy() {
        super.onDestroy();
        if (sampleClient != null) {
            try {
                sampleClient.disconnect();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    private void startMQTT() {
        String clientId;

        MemoryPersistence persistence = new MemoryPersistence();
        try {

            String broker = "tcp://" + ip + ":1883";
            clientId = nick;

            sampleClient = new MqttClient(broker, clientId, persistence);
            sampleClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {

                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    Message msg = myHandler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("NICK", s);
                    b.putString("MSG", mqttMessage.toString());
                    msg.setData(b);
                    myHandler.sendMessage(msg);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
                //TODO
            });
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: " + broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");
            sampleClient.subscribe("#");
        } catch (MqttException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}