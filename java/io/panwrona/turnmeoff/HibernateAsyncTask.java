package io.panwrona.turnmeoff;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

/**
 * Created by Mariusz on 15.10.14.
 *
 * AsyncTask class which manages connection with server app and is sending hibernate command.
 *
 */
public class HibernateAsyncTask extends AsyncTask<String, String, TCPClient> {

    private              TCPClient  tcpClient                 ;
    private              Handler    mHandler                  ;
    private static final String     COMMAND = "shutdown -h"   ;
    private static final String     TAG = "HibernateAsyncTask";

    /**
     * HibernateAsyncTask constructor with handler passed as argument. The UI is updated via handler.
     * In doInBackground(...) method, the handler is passed to TCPClient object.
     * @param mHandler Handler object that is retrieved from MainActivity class and passed to TCPClient
     *                 class for sending messages and updating UI.
     */
    public HibernateAsyncTask(Handler mHandler){
        this.mHandler = mHandler;
    }

    /**
     * Overriden method from AsyncTask class. There the TCPClient object is created.
     * @param params From MainActivity class empty string is passed.
     * @return TCPClient object for closing it in onPostExecute method.
     */
    @Override
    protected TCPClient doInBackground(String... params) {
        Log.d(TAG, "In do in background");

        //Creating tcpClient object
        try{
            tcpClient = new TCPClient(mHandler,
                                      COMMAND,
                                      new IpGetter().getIp(),
                                      new TCPClient.MessageCallback() {
                @Override
                public void callbackMessageReceiver(String message) {
                    publishProgress(message);
                }
            });
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        //Running tcpClient
        tcpClient.run();
        return tcpClient;
    }

    /**
     * Overriden method from AsyncTask class. Here we're checking if server answered properly.
     * @param values If "restart" message came, the client is stopped and computer should be restarted.
     *               Otherwise "wrong" message is sent and 'Error' message is shown in UI.
     */
    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        Log.d(TAG, "In progress update, values: " + values[0]);

        //Checking
        if(values[0].equals("hibernate")){
            tcpClient.sendMessage(COMMAND);
            tcpClient.stopClient();
            mHandler.sendEmptyMessageDelayed(MainActivity.HIBERNATE, 2000);
        }else{
            tcpClient.sendMessage("wrong");
            mHandler.sendEmptyMessageDelayed(MainActivity.ERROR, 2000);
            tcpClient.stopClient();
        }
    }

    @Override
    protected void onPostExecute(TCPClient result){
        super.onPostExecute(result);
        Log.d(TAG, "In on post execute");
        if(result != null && result.isRunning()){
            result.stopClient();
        }
        mHandler.sendEmptyMessageDelayed(MainActivity.SENT, 4000);
    }
}

