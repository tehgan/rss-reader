package com.tehgan.rssreader.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.tehgan.rssreader.BuildConfig;
import com.tehgan.rssreader.R;
import com.tehgan.rssreader.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;

    private boolean urlSet = false;
    private String oldUrl = "";
    private String userUrl = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.urlEdittext.setHint(BuildConfig.RSS_FALLBACK_URL);

        TextWatcher watcher = getWatcher();
        binding.urlEdittext.addTextChangedListener(watcher);

        SharedPreferences prefs = requireActivity().getPreferences(Context.MODE_PRIVATE);
        String url = prefs.getString("url", "");
        if (!url.isEmpty()) {
            oldUrl = url;
            binding.urlEdittext.setText(url);
        }

        binding.btnSet.setOnClickListener(v -> {
            urlSet = true;
            String input = binding.urlEdittext.getText().toString();
            if (!userUrl.isBlank()) {
                oldUrl = userUrl;
            }
            userUrl = input;

            Snackbar sb = Snackbar.make(binding.btnSet, R.string.set_snackbar, Snackbar.LENGTH_LONG);
            sb.setAction(R.string.undo, clickListener -> {
                userUrl = oldUrl;
                binding.urlEdittext.setText(userUrl);
            });
            sb.show();
        });

        binding.btnClear.setOnClickListener(v -> binding.urlEdittext.setText(""));
    }

    @Override
    public void onPause() {
        if (urlSet) {
            // Save the user's preferred RSS URL to SharedPreferences
            SharedPreferences prefs = requireActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            if (!userUrl.isEmpty()) {
                editor.putString("url", userUrl);
            } else {
                editor.clear();
            }
            editor.apply();
        }
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private TextWatcher getWatcher() {
        return new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /* This method's called in onViewCreated so the binding already exists,
                 *  no need for null checks. */
                binding.btnClear.setEnabled(!s.toString().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        };
    }
}