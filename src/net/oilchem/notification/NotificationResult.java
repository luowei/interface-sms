package net.oilchem.notification;

import javapns.notification.PushedNotification;

import java.io.Serializable;
import java.util.List;

/**
 * Created by luowei on 2014/8/11.
 */
public class NotificationResult implements Serializable {

    List<PushedNotification> failedNotifications;
    List<PushedNotification> successfulNotifications;

    public List<PushedNotification> getFailedNotifications() {
        return failedNotifications;
    }

    public void setFailedNotifications(List<PushedNotification> failedNotifications) {
        this.failedNotifications = failedNotifications;
    }

    public List<PushedNotification> getSuccessfulNotifications() {
        return successfulNotifications;
    }

    public void setSuccessfulNotifications(List<PushedNotification> successfulNotifications) {
        this.successfulNotifications = successfulNotifications;
    }
}
