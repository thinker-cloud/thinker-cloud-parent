package com.thinker.cloud.core.thread;

import com.alibaba.ttl.TtlCallable;
import com.alibaba.ttl.TtlRunnable;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;

import java.io.Serial;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * 这是{@link ThreadPoolTaskExecutor}的一个简单替换，可搭配TransmittableThreadLocal实现父子线程之间的数据传递
 *
 * @author admin
 */
public class ThinkerThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {

    @Serial
    private static final long serialVersionUID = -445614176488260776L;

    @Override
    public void execute(@NonNull Runnable task) {
        Runnable ttlRunnable = TtlRunnable.get(task);
        super.execute(ttlRunnable);
    }

    @NonNull
    @Override
    public Future<?> submit(@NonNull Runnable task) {
        Runnable ttlRunnable = TtlRunnable.get(task);
        return super.submit(ttlRunnable);
    }

    @NonNull
    @Override
    public <T> Future<T> submit(@NonNull Callable<T> task) {
        Callable<T> ttlCallable = TtlCallable.get(task);
        return super.submit(ttlCallable);
    }

    @Deprecated
    @NonNull
    @Override
    public ListenableFuture<?> submitListenable(@NonNull Runnable task) {
        Runnable ttlRunnable = TtlRunnable.get(task);
        return super.submitListenable(ttlRunnable);
    }

    @Deprecated
    @NonNull
    @Override
    public <T> ListenableFuture<T> submitListenable(@NonNull Callable<T> task) {
        Callable<T> ttlCallable = TtlCallable.get(task);
        return super.submitListenable(ttlCallable);
    }

    @NonNull
    @Override
    public CompletableFuture<Void> submitCompletable(@NonNull Runnable task) {
        Runnable ttlRunnable = TtlRunnable.get(task);
        return super.submitCompletable(ttlRunnable);
    }

    @NonNull
    @Override
    public <T> CompletableFuture<T> submitCompletable(@NonNull Callable<T> task) {
        Callable<T> ttlCallable = TtlCallable.get(task);
        return super.submitCompletable(ttlCallable);
    }
}
