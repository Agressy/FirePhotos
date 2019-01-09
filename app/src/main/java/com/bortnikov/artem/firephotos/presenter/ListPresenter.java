package com.bortnikov.artem.firephotos.presenter;

import android.annotation.SuppressLint;
import android.net.Uri;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.bortnikov.artem.firephotos.MainApp;
import com.bortnikov.artem.firephotos.data.model.Upload;
import com.bortnikov.artem.firephotos.data.usecase.DataUseCase;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import javax.inject.Inject;

import java.util.List;

import io.reactivex.observers.DisposableCompletableObserver;

@InjectViewState
public class ListPresenter extends MvpPresenter<ListView>
        implements Subscriber<List<Upload>> {

    @Inject
    DataUseCase usecase;

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        MainApp.getComponent().injectsToListPresenter(this);
    }

    public void getData() {
        getViewState().startLoading();
        usecase.getData().subscribe(this);
    }

    @SuppressLint("CheckResult")
    public void uploadData(Uri imageUri) {
        usecase.uploadData(imageUri).subscribeWith(new DisposableCompletableObserver() {
            @Override
            public void onComplete() {
                getData();
            }

            @Override
            public void onError(Throwable e) {
                onErrorMain(e);
            }
        });
    }

    private void onErrorMain(Throwable e) {
        getViewState().showError(e);
        getViewState().hideLoading();
        getViewState().showEmptyText();
    }

    @Override
    public void onSubscribe(Subscription s) {
        s.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(List<Upload> data) {
        getViewState().setItems(data);
    }

    @Override
    public void onError(Throwable e) {
        onErrorMain(e);
    }

    @Override
    public void onComplete() {
        getViewState().updateList();
        getViewState().hideLoading();
    }
}
