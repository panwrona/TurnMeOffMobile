package io.panwrona.turnmeoff;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Main Widget Class.
 *
 *@author Mariusz
 */

public class MainActivity extends AppWidgetProvider{

    //static final integers for handler's msg.what
    public static final int SHUTDOWN   = 1;
    public static final int RESTART    = 2;
    public static final int HIBERNATE  = 3;
    public static final int ERROR      = 4;
    public static final int SENDING    = 5;
    public static final int CONNECTING = 6;
    public static final int SENT       = 7;

    //actions that are managed in onReceive method
    private static final String ACTION_SHUTDOWN  = "io.panwrona.turnmeoff.ACTION_SHUTDOWN" ;
    private static final String ACTION_RESTART   = "io.panwrona.turnmeoff.ACTION_RESTART"  ;
    private static final String ACTION_HIBERNATE = "io.panwrona.turnmeoff.ACTION_HIBERNATE";


    //Other objects used by MainActivity class.
    RemoteViews      views    ;
    ComponentName    widget   ;
    AppWidgetManager awManager;
    Handler          mHandler ;


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

    //Method for passing Handler object in AsyncTask's constructor with final Context from onReceive
    private Handler getmHandler(final Context context){
       final String mTag = "Handler";
        mHandler = new Handler(){
            public void handleMessage(Message msg) {
                switch(msg.what){
                    case SHUTDOWN:
                        Log.d(mTag, "In Handler's shutdown");

                         views     = new RemoteViews(context.getPackageName(), R.layout.activity_main);
                         widget    = new ComponentName(context, MainActivity.class);
                         awManager = AppWidgetManager.getInstance(context);
                                     views.setTextViewText(R.id.state, "Shutting PC...");
                                     awManager.updateAppWidget(widget,views);
                        break;
                    case RESTART:
                        Log.d(mTag, "In Handler's restart");

                        views     = new RemoteViews(context.getPackageName(), R.layout.activity_main);
                        widget    = new ComponentName(context, MainActivity.class);
                        awManager = AppWidgetManager.getInstance(context);
                                    views.setTextViewText(R.id.state, "Restarting PC...");
                                    awManager.updateAppWidget(widget,views);
                        break;
                    case HIBERNATE:
                        Log.d(mTag, "In Handler's hibernate");

                        views     = new RemoteViews(context.getPackageName(), R.layout.activity_main);
                        widget    = new ComponentName(context, MainActivity.class);
                        awManager = AppWidgetManager.getInstance(context);
                                    views.setTextViewText(R.id.state, "Hibernating PC...");
                                    awManager.updateAppWidget(widget,views);
                        break;
                    case ERROR:
                        Log.d(mTag, "In Handler's error");

                        views     = new RemoteViews(context.getPackageName(), R.layout.activity_main);
                        widget    = new ComponentName(context, MainActivity.class);
                        awManager = AppWidgetManager.getInstance(context);
                                    views.setTextViewText(R.id.state, "Something went wrong...");
                                    awManager.updateAppWidget(widget,views);
                        break;
                    case SENDING:
                        Log.d(mTag, "In Handler's sending");

                        views     = new RemoteViews(context.getPackageName(), R.layout.activity_main);
                        widget    = new ComponentName(context, MainActivity.class);
                        awManager = AppWidgetManager.getInstance(context);
                                    views.setTextViewText(R.id.state, "Sending message...");
                                    awManager.updateAppWidget(widget,views);
                        break;
                    case CONNECTING:
                        Log.d(mTag, "In Handler's connecting");

                        views     = new RemoteViews(context.getPackageName(), R.layout.activity_main);
                        widget    = new ComponentName(context, MainActivity.class);
                        awManager = AppWidgetManager.getInstance(context);
                                    views.setTextViewText(R.id.state, "Connecting...");
                                    awManager.updateAppWidget(widget,views);
                        break;
                    case SENT:
                        Log.d(mTag, "In Handler's sent");

                        views     = new RemoteViews(context.getPackageName(), R.layout.activity_main);
                        widget    = new ComponentName(context, MainActivity.class);
                        awManager = AppWidgetManager.getInstance(context);
                                    views.setTextViewText(R.id.state, "Waiting for command...");
                                    awManager.updateAppWidget(widget,views);
                        break;
                }
            }

        };
        return mHandler;
    }

}