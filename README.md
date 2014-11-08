Download below!
===============

(Download Server App)[https://github.com/panwrona/TurnMeOffMobile/blob/master/TurnMeOff.jar?raw=true]
Download Android Widget soon!

TurnMeOffMobile
===============

Android Widget TCP Client developed for connecting with server app and sending commands to:
* Restart the computer
* Turn Off the computer
* Hibernate the computer

If You see any issues, please inform! Every bit of help is very welcome.

How does it work?
-----------------

+ In MainActivity we have two overriden methods: onUpdate and onReceive.
    * In onUpdate we manage the views and intents. We are setting the pending intents to buttons and
    update via AppWidgetManager. Look at this code:
    ```java
    @Override
     public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                          int[] appWidgetIds) {
         super.onUpdate(context, appWidgetManager, appWidgetIds);

         for(int i=0; i<appWidgetIds.length; i++){

             int currentId = appWidgetIds[i];
             views         = new RemoteViews(context.getPackageName(), R.layout.activity_main);

             //Creating intents and pending intents for onReceive method
             Intent shutdownIntent          = new Intent(context, MainActivity.class)                   ;
                                              shutdownIntent.setAction(ACTION_SHUTDOWN)                 ;
             PendingIntent pendingShutdown  = PendingIntent.getBroadcast(context, 0, shutdownIntent, 0) ;

             Intent restartIntent           = new Intent(context, MainActivity.class)                   ;
                                              restartIntent.setAction(ACTION_RESTART)                   ;
             PendingIntent pendingRestart   = PendingIntent.getBroadcast(context, 1 , restartIntent, 0) ;

             Intent hibernateIntent         = new Intent(context, MainActivity.class)                   ;
                                              hibernateIntent.setAction(ACTION_HIBERNATE)               ;
             PendingIntent pendingHibernate = PendingIntent.getBroadcast(context, 2, hibernateIntent, 0);

             //setting pending intents to specific buttons
             views.setOnClickPendingIntent(R.id.shutdownButton , pendingShutdown) ;
             views.setOnClickPendingIntent(R.id.restartButton  , pendingRestart)  ;
             views.setOnClickPendingIntent(R.id.hibernateButton, pendingHibernate);

             appWidgetManager.updateAppWidget(currentId, views);

         }
     }
     ```
     The ```ACTION_SHUTDOWN```,```ACTION_RESTART``` and ```ACTION_HIBERNATE``` are static Strings with names
     of the actions from AndroidManifest:
     ```java
      <receiver android:name="io.panwrona.turnmeoff.MainActivity" >
                 <intent-filter>
                     <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
                     <action android:name="io.panwrona.turnmeoff.ACTION_SHUTDOWN" />
                     <action android:name="io.panwrona.turnmeoff.ACTION_RESTART"/>
                     <action android:name="io.panwrona.turnmeoff.ACTION_HIBERNATE"/>
                 </intent-filter>
                 <meta-data android:name="android.appwidget.provider"

                     android:resource="@xml/app_widget" />
             </receiver>
      ```

      *In onReceive method we are managing button clicking, which creates AsyncTask object.
      ```java
      @Override
          public void onReceive(Context context, Intent intent){
              super.onReceive(context,intent);

              views = new RemoteViews(context.getPackageName(), R.layout.activity_main);
              widget = new ComponentName(context, MainActivity.class);
              awManager = AppWidgetManager.getInstance(context);

              /**
               * Checking and comparing actions delivered from button clicks and managing ones.
               * If button is clicked and everything is going well, specific AsyncTask is being created,
               * and rest of procedure is being done.
               */
               if(intent.getAction().equals(ACTION_SHUTDOWN)){
                  new ShutdownAsyncTask(getmHandler(context)).execute("");
              }else if(intent.getAction().equals(ACTION_RESTART)){
                 new RestartAsyncTask(getmHandler(context)).execute("");
              }else if(intent.getAction().equals(ACTION_HIBERNATE)){
                  new HibernateAsyncTask(getmHandler(context)).execute("");
              }
          }
      ```

+ Let's say You clicked 'Shutdown PC' button. This is what happens:
    * The TCPClient object is created in ShutdownAsyncTask class:
    ```java
        /**
         * Overriden method from AsyncTask class. There the TCPClient object is created.
         * @param params From MainActivity class empty string is passed.
         * @return TCPClient object for closing it in onPostExecute method.
         */
    @Override
        protected TCPClient doInBackground(String... params) {
            Log.d(TAG, "In do in background");

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
                Log.d(TAG, "Caught null pointer exception");
                e.printStackTrace();
            }
            tcpClient.run();
            return null;
        }
    ```
    The IpGetter class is dynamically searching for the open server socket on your local network,
    on the 'hardcoded' port. Take a look on that class, there is everything explained in comments.

    * After creating the object and getting the proper ip number, the TCPClient object is trying to
    create socket, PrintWriter object and BufferReader for input/output.
    ```java
     public void run() {

            mRun = true;

            try {
                // Creating InetAddress object from ipNumber passed via constructor from IpGetter class.
                InetAddress serverAddress = InetAddress.getByName(ipNumber);

                Log.d(TAG, "Connecting...");

                /**
                 * Sending empty message with static int value from MainActivity
                 * to update UI ( 'Connecting...' ).
                 *
                 * @see com.example.turnmeoff.MainActivity.CONNECTING
                 */
                mHandler.sendEmptyMessageDelayed(MainActivity.CONNECTING,1000);

                /**
                 * Here the socket is created with hardcoded port.
                 * Also the port is given in IpGetter class.
                 *
                 * @see com.example.turnmeoff.IpGetter
                 */
                Socket socket = new Socket(serverAddress, 4444);


                try {

                    // Create PrintWriter object for sending messages to server.
                    out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                    //Create BufferedReader object for receiving messages from server.
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    Log.d(TAG, "In/Out created");
                    ```


        After that, the infinite loop is started and is working as far as the response come:

        ```java
        //Sending message with command specified by AsyncTask
                            this.sendMessage(command);

                            //
                            mHandler.sendEmptyMessageDelayed(MainActivity.SENDING,2000);

                            //Listen for the incoming messages while mRun = true
                            while (mRun) {
                                incomingMessage = in.readLine();
                                if (incomingMessage != null && listener != null) {

                                    /**
                                     * Incoming message is passed to MessageCallback object.
                                     * Next it is retrieved by AsyncTask and passed to onPublishProgress method.
                                     *
                                     */
                                    listener.callbackMessageReceiver(incomingMessage);

                                }
                                incomingMessage = null;

                            }

                            Log.d(TAG, "Received Message: " +incomingMessage);

                        } catch (Exception e) {

                            Log.d(TAG, "Error", e);
                            mHandler.sendEmptyMessageDelayed(MainActivity.ERROR, 2000);

                        } finally {

                            out.flush();
                            out.close();
                            in.close();
                            socket.close();
                            mHandler.sendEmptyMessageDelayed(MainActivity.SENT, 3000);
                            Log.d(TAG, "Socket Closed");
                        }

                    } catch (Exception e) {

                        Log.d(TAG, "Error", e);
                        mHandler.sendEmptyMessageDelayed(MainActivity.ERROR, 2000);

                    }

                }
     ```
     After getting message, the callback is getting involved and is pushing the message to 'onProgressUpdate(..)'
     method. In this method we are stopping the client and getting to the end:

     ```java
     @Override
         protected void onProgressUpdate(String... values) {
             super.onProgressUpdate(values);
             Log.d(TAG, "In progress update, values: " + values.toString());
             if(values[0].equals("shutdown")){
                 tcpClient.sendMessage(COMMAND);
                 tcpClient.stopClient();
                 mHandler.sendEmptyMessageDelayed(MainActivity.SHUTDOWN, 2000);

             }else{
                 tcpClient.sendMessage("wrong");
                 mHandler.sendEmptyMessageDelayed(MainActivity.ERROR, 2000);
                 tcpClient.stopClient();
             }
         }
     ```

     When client is stopped, we are going to 'onPostExecute(...)' method to check if client is stopped:

     ```java
     @Override
         protected void onPostExecute(TCPClient result){
             super.onPostExecute(result);
             Log.d(TAG, "In on post execute");
             if(result != null && result.isRunning()){
                 result.stopClient();
             }
             mHandler.sendEmptyMessageDelayed(MainActivity.SENT, 4000);

         }
     ```
+ Between all of these steps you can see the Handler object. It is used for updating the UI, just look
at the end off the MainActivity class.
