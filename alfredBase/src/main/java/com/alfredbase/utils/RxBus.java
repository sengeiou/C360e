package com.alfredbase.utils;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;


public class RxBus {

    public static final String RX_MSG_1 = "RX_MSG_1";
    private static RxBus instance;
    private ConcurrentHashMap<Object, List<Subject>> maps = new ConcurrentHashMap<Object, List<Subject>>();

    private RxBus() {

    }

    public static RxBus getInstance() {
        if (instance == null) {
            synchronized (RxBus.class) {
                if (instance == null) {
                    instance = new RxBus();
                }
            }
        }
        return instance;
    }

    /**
     * 注册信息
     * @param tag   事件标识
     * @param <T>
     * @return
     */
    public <T> Observable<T> register(@NonNull String tag) {
        List<Subject> subjects = maps.get(tag);
        if (subjects == null) {
            subjects = new ArrayList<Subject>();
            maps.put(tag, subjects);
        }
        Subject<T, T> subject = PublishSubject.<T>create();
        subjects.add(subject);
        return subject;
    }

    /**
     * 解除注册
     * @param tag
     * @param observable
     */
    public void unregister(@NonNull String tag, @NonNull Observable observable) {
        List<Subject> subjects = maps.get(tag);
        if (subjects != null) {
            subjects.remove((Subject) observable);
            if (subjects.isEmpty()) {
                maps.remove(tag);
            }
        }
    }

    /**
     * 发送事件
     *
     * @param o
     */
    public void post(@NonNull String tag, Object o) {
        List<Subject> subjects = maps.get(tag);
        if (subjects != null && !subjects.isEmpty()) {
            for (Subject s : subjects) {
                s.onNext(o);
            }
        }
    }

    private boolean isEmpty(Collection collection) {

        return null == collection || collection.isEmpty();

    }
}
