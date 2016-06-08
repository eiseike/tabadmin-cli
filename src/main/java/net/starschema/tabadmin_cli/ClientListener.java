package net.starschema.tabadmin_cli;

import javax.management.Notification;
import javax.management.NotificationListener;

/**
 * Created by balazsa on 2016.06.07..
 */

public class ClientListener implements NotificationListener {
    public void handleNotification(Notification notification, Object handback)
    {
        System.out.println("\nReceived notification: " + notification);
    }
}
