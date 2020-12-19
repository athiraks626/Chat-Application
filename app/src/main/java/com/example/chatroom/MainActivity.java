package com.example.chatroom;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class MainActivity extends AppCompatActivity {
    private WebSocket webSocket;
    private MessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView messageList =  findViewById(R.id.messageList);
        final EditText messageBox = findViewById(R.id.messageBox);
        TextView send = findViewById(R.id.send);

        instantiateWebSocket();

          adapter = new MessageAdapter();
         messageList.setAdapter(adapter);

         send.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 String  message =  messageBox.getText().toString().trim();
                 if(!message.isEmpty()){
                     webSocket.send(message);
                     JSONObject jsonObject = new JSONObject();

                     try{
                         jsonObject.put("message",message);
                         jsonObject.put("byServer",false);
                         adapter.addItem(jsonObject);
                     }catch (Exception e){

                     }
                 }

             }
         });
    }

    private void instantiateWebSocket() {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url("ws://192.168.1.47:8080").build();
        SocketListener socketListener = new SocketListener(this);
        webSocket = okHttpClient.newWebSocket(request,socketListener);
    }

    public class SocketListener extends WebSocketListener {

        public MainActivity activity;

        public SocketListener(MainActivity activity) {
            this.activity = activity;
        }

        @Override
        public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            super.onClosed(webSocket, code, reason);
        }

        @Override
        public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            super.onClosing(webSocket, code, reason);
        }

        @Override
        public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
            super.onFailure(webSocket, t, response);
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull final String text) {
            super.onMessage(webSocket, text);

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("message", text);
                        jsonObject.put("byServer", true);
                        adapter.addItem(jsonObject);
                    } catch (Exception e) {

                    }
                }
            });
        }



        @Override
        public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
            super.onOpen(webSocket, response);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity,"Connection Established ",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
        public class MessageAdapter  extends BaseAdapter{

          List<JSONObject> messageList = new ArrayList<>() ;

            @Override
            public int getCount() {
                return messageList.size();
            }

            @Override
            public Object getItem(int i) {
                return messageList.get(i);
            }

            @Override
            public long getItemId(int i ) {
                return i;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup ) {

                if(view == null )

               view = getLayoutInflater().inflate(R.layout.message_list_item,viewGroup ,false);
               TextView  sendMessage = view.findViewById(R.id.sentMessage );
               TextView receivedMessage = view.findViewById(R.id.receivedMessage);
                JSONObject item = messageList.get(i);
                try{
                    if(item.getBoolean("byServer")){
                        receivedMessage.setVisibility(View.VISIBLE);
                       receivedMessage.setText(item.getString("message"));
                       sendMessage.setVisibility(View.INVISIBLE);
                    }
                     else{
                        sendMessage.setVisibility(View.VISIBLE);
                        sendMessage.setText(item.getString("message" ));
                        receivedMessage.setVisibility( View.INVISIBLE);
                     }

                }catch (Exception e){
                    e.printStackTrace();
                }
                return view ;
            }
            void addItem(JSONObject item){
            messageList.add(item);
            notifyDataSetChanged();
            }
        }

}