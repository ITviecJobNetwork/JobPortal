package com.fashion.service.notify;

import com.fashion.dto.notify.Notification;

public interface INotifyService<T extends Notification> {

    void sendNotify(T notification);
}
