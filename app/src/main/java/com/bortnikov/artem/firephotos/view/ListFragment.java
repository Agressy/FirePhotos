package com.bortnikov.artem.firephotos.view;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.bortnikov.artem.firephotos.R;
import com.bortnikov.artem.firephotos.data.model.Upload;
import com.bortnikov.artem.firephotos.presenter.ListPresenter;
import com.bortnikov.artem.firephotos.presenter.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class ListFragment extends MvpAppCompatFragment implements ListView,
        SwipeRefreshLayout.OnRefreshListener {

    @InjectPresenter
    ListPresenter listPresenter;

    private static final int PICK_IMAGE_ID = 234;
    private static final int ALL_PERMISSIONS_RESULT = 107;
    private static final int GRID_SPAN_COUNT = 2;

    private ImageAdapter adapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView recyclerView;

    private TextView emptyTextView;

    private ArrayList<String> permissions = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater
                .inflate(R.layout.fragment_list, container, false);

        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent chooseImageIntent = ImagePicker.getPickImageIntent(getActivity());
            startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
        });

        recyclerView = root.findViewById(R.id.rv_list_tasks);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), GRID_SPAN_COUNT));
        adapter = new ImageAdapter(getActivity());
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout = root.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);

        emptyTextView = root.findViewById(R.id.empty_text_view);

        permissions.add(CAMERA);
        permissions.add(READ_EXTERNAL_STORAGE);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        ArrayList<String> permissionsToRequest = findUnAskedPermissions(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(new String[0]),
                        ALL_PERMISSIONS_RESULT);
            }
        }

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        listPresenter.getData();
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }
        return result;
    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return (Objects.requireNonNull(getActivity())
                    .checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
        }
        return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PICK_IMAGE_ID:
                Uri imageUri = ImagePicker.getImageFromResult(getActivity(), resultCode, data);
                listPresenter.uploadData(imageUri);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void startLoading() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideLoading() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showEmptyText() {
        emptyTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showError(Throwable ex) {
        Toast.makeText(getContext(), ex.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setItems(List<Upload> items) {
        adapter.setItems(items);
    }

    @Override
    public void updateList() {
        emptyTextView.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
        recyclerView.invalidate();
    }

    @Override
    public void onRefresh() {
        listPresenter.getData();
    }
}
