package com.example.chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MainActivity extends AppCompatActivity {
    public static String IP = "ip";
    public static String NICK = "nick";
    EditText ipEditText;
    EditText nickEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ipEditText = findViewById(R.id.IpET);
        nickEditText = findViewById(R.id.NET);


    }



    public void startButton(View view) {
        Intent intent = new Intent(getApplicationContext(), SimpleChatActivity.class);
        intent.putExtra(IP, ipEditText.getText().toString());
        intent.putExtra(NICK, nickEditText.getText().toString());
        startActivity(intent);
    }

    public void clearIpEditText(View view) {
        clearText(ipEditText);
    }

    public void clearNickEditText(View view) {
        clearText(nickEditText);
    }

    public void clearText(EditText t) {
        t.setText("");
    }


}
