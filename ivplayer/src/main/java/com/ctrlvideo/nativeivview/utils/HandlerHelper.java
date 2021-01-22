package com.ctrlvideo.nativeivview.utils;


import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HandlerHelper {

    public void removeCallbacks(Runnable mTicker) {
        if (handler != null) {
            handler.removeCallbacks(mTicker);
        }
    }

    public void post(Runnable mTicker) {
        if (handler != null) {

            handler.post(mTicker);
        }
    }

    public void postAtTime(Runnable mTicker, long next) {
        if (handler != null) {
            handler.postAtTime(mTicker, next);
        }
    }

    public class MsgInfo {
        long sendTime;
        long delayTime;
        Object object;
    }


    @SuppressLint("UseSparseArrays")
    private Map<Integer, MsgInfo> msgList = new HashMap<>();

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            removeMessages(msg.what);
            msgList.remove(msg.what);
            HandlerHelper.this.handleMessage(msg);
        }
    };

    public void handleMessage(Message msg) {

        Log.d("HandlerHelper", new Gson().toJson(msgList));
    }


    public void sendEmptyMessage(int what) {
        if (handler != null) {
            handler.removeMessages(what);
            handler.sendEmptyMessage(what);
            Log.d("HandlerHelper", new Gson().toJson(msgList));
        }
    }

    public void sendMessage(Message message) {
        if (handler != null) {
            handler.removeMessages(message.what);
            handler.sendMessage(message);
            Log.d("HandlerHelper", new Gson().toJson(msgList));
        }
    }


    public void sendEmptyMessageDelayed(int what, long time) {

//        Message message = new Message();
//        message.what = what;
//        sendMessageDelayed(message, time);


        if (handler != null) {
            MsgInfo msgInfo = new MsgInfo();
            msgInfo.sendTime = System.currentTimeMillis();
            msgInfo.delayTime = time;
            msgList.put(what, msgInfo);

            handler.removeMessages(what);
            handler.sendEmptyMessageDelayed(what, time);
            Log.d("HandlerHelper", new Gson().toJson(msgList));
        }
    }


    public void sendMessageDelayed(Message message, long time) {
        if (handler != null) {
            MsgInfo msgInfo = new MsgInfo();
            msgInfo.sendTime = System.currentTimeMillis();
            msgInfo.delayTime = time;
            msgInfo.object = message.obj;
            msgList.put(message.what, msgInfo);

            handler.removeMessages(message.what);
            handler.sendMessageDelayed(message, time);
            Log.d("HandlerHelper", new Gson().toJson(msgList));
        }
    }


    public void removeMessages(int what) {
        if (handler != null) {
            msgList.remove(what);
            handler.removeMessages(what);

            Log.d("HandlerHelper", new Gson().toJson(msgList));
        }
    }

    public void removeAllMessage() {

        if (handler != null) {
            Set<Integer> set = msgList.keySet();
            for (Integer what : set) {

                handler.removeMessages(what);

            }
            msgList.clear();

            Log.d("HandlerHelper", new Gson().toJson(msgList));
        }


    }

    public void resume() {
        if (handler != null) {
            Set<Integer> set = msgList.keySet();
            for (Integer what : set) {
                MsgInfo msgInfo = msgList.get(what);
                if (msgInfo != null) {

                    msgInfo.sendTime = System.currentTimeMillis();


                    Message message=new Message();
                    message.what=what;
                    message.obj=msgInfo.object;

                    handler.sendMessageDelayed(message, msgInfo.delayTime);
                }

            }
            Log.d("HandlerHelper", new Gson().toJson(msgList));
        }
    }

    public void pause() {
        if (handler != null) {
            Set<Integer> set = msgList.keySet();
            for (Integer what : set) {
                MsgInfo msgInfo = msgList.get(what);

                if (msgInfo != null) {
                    long sendtime = msgInfo.sendTime;
                    long currentTime = System.currentTimeMillis();
                    long delayTime = msgInfo.delayTime;

                    msgInfo.sendTime = currentTime;
                    msgInfo.delayTime = delayTime - (currentTime - sendtime);
                }

                handler.removeMessages(what);
            }
            Log.d("HandlerHelper", new Gson().toJson(msgList));
        }

    }

    public void release() {
        removeAllMessage();
        if (handler != null) {
            handler = null;
        }
        if (msgList != null) {
            msgList = null;
        }
    }
}
