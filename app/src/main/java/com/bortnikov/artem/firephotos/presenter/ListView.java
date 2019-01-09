package com.bortnikov.artem.firephotos.presenter;

import com.arellomobile.mvp.MvpView;
import com.bortnikov.artem.firephotos.data.model.Upload;

import java.util.List;

public interface ListView extends MvpView {
    void startLoading();

    void hideLoading();

    void showEmptyText();

    void showError(Throwable ex);

    void setItems(List<Upload> items);

    void updateList();
}
