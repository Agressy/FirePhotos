package com.bortnikov.artem.firephotos.data.usecase;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.bortnikov.artem.firephotos.data.model.Upload;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.FlowableOnSubscribe;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DataUseCase {

    private StorageReference myStorageRef = FirebaseStorage
            .getInstance().getReference("uploads");
    private DatabaseReference mDatabaseRef = FirebaseDatabase
            .getInstance().getReference("uploads");

    private ArrayList<Upload> result = new ArrayList<>();

    public Flowable<ArrayList<Upload>> getData() {

        return Flowable.create((FlowableOnSubscribe<ArrayList<Upload>>) e ->
                mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                result.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    assert upload != null;
                    upload.setKey(postSnapshot.getKey());
                    result.add(upload);
                }
                e.onNext(result);
                e.onComplete();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                e.onError(databaseError.toException());
            }
        }), BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    public Completable uploadData(Uri imageUri) {
        return Completable.create(e -> {

            if (imageUri != null) {
                StorageReference fileReference = myStorageRef
                        .child(System.currentTimeMillis() + ".jpg");

                fileReference.putFile(imageUri).continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return fileReference.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();

                        assert downloadUri != null;
                        Upload upload = new Upload(downloadUri.toString());
                        mDatabaseRef.push().setValue(upload);
                        e.onComplete();

                    } else {
                        e.onError(Objects.requireNonNull(task.getException()));
                    }
                });
            }
        });
    }



}
