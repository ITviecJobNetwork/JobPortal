package com.fashion.service.notify;

import com.fashion.dto.notify.Notification;
import lombok.Setter;

import java.util.concurrent.Executor;

public abstract class AsyncableNotifyService<T extends Notification> implements INotifyService<T> {

    @Setter
    protected Executor executor;

    public void sendAsyncNotify(T t) {
        executor.execute(() -> this.sendNotify(t));
    }

}
