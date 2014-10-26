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
