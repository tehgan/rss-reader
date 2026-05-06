package com.tehgan.rssreader.ui.basic.favourites;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tehgan.rssreader.databinding.FragmentFavouritesBinding;
import com.tehgan.rssreader.ui.basic.common.BasicAdapter;

import java.util.Collections;

public class FavouritesFragment extends Fragment {
    private FragmentFavouritesBinding binding;
    private BasicAdapter adapter;
    private FavouritesViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (adapter == null) {
            adapter = new BasicAdapter(requireContext(), Collections.emptyList());
        }
        viewModel = new ViewModelProvider(this).get(FavouritesViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFavouritesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        super.onViewCreated(v, b);
        binding.favouritesList.setAdapter(adapter);
        // Automatically updates the adapter's list when favourites are added or removed
        viewModel.getAll().observe(getViewLifecycleOwner(), re -> adapter.updateList(re));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}