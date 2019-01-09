package com.bortnikov.artem.firephotos.di;

import com.bortnikov.artem.firephotos.data.usecase.DataUseCase;

import dagger.Module;
import dagger.Provides;

@Module
class ListUseCaseModule {
    @Provides
    DataUseCase dataUseCase() {
        return new DataUseCase();
    }
}
