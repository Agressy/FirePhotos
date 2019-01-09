package com.bortnikov.artem.firephotos.di;

import com.bortnikov.artem.firephotos.presenter.ListPresenter;

import dagger.Component;

@Component(modules = {ListUseCaseModule.class})
public interface AppComponent {

    void injectsToListPresenter(ListPresenter listPresenter);
}