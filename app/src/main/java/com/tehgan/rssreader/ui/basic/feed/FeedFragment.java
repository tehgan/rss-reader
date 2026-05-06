package com.tehgan.rssreader.ui.basic.feed;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tehgan.rssreader.BuildConfig;
import com.tehgan.rssreader.R;
import com.tehgan.rssreader.interfaces.RssCallback;
import com.tehgan.rssreader.data.Rss;
import com.tehgan.rssreader.tasks.RssParser;
import com.tehgan.rssreader.interfaces.TitleCallback;
import com.tehgan.rssreader.databinding.FragmentFeedBinding;
import com.tehgan.rssreader.ui.basic.common.BasicAdapter;

import java.util.Collections;
import java.util.List;

public class FeedFragment extends Fragment implements RssCallback {
    private List<Rss> rssItems = Collections.emptyList();
    private FeedViewModel viewModel;

    private FragmentFeedBinding binding;
    private BasicAdapter adapter;

    @Override
    public void onProgressUpdate(int progress) {
        if (binding != null) {
            binding.progressBar.setProgress(progress);
        }
    }

    @Override
    public void onRssFetched(String channel, List<Rss> items) {
        if (adapter != null) {
            rssItems = items;
            adapter.updateList(rssItems);
            // Cache items in-memory
            viewModel.setItems(items);
            if (binding != null) {
                if (binding.refreshLayout.isRefreshing()) {
                    binding.refreshLayout.setRefreshing(false);
                }
                if (items.isEmpty()) {
                    setupEmptyFeedError();
                    setupLayout(true);
                } else {
                    setupLayout(false);
                }
            }
        }
        if (!channel.isBlank()) {
            // Activate MainActivity's callback
            TitleCallback tc = (TitleCallback) requireActivity();
            tc.onTitleUpdate(channel);
        }
    }

    @Override
    public void onError(String error) {
        String toastMsg = getResources().getString(R.string.error_toast_base, error);
        Toast.makeText(requireContext(), toastMsg, Toast.LENGTH_LONG).show();
        if (binding != null) {
            if (binding.refreshLayout.isRefreshing()) {
                binding.refreshLayout.setRefreshing(false);
            }

            binding.loadingOrException.setText(error);

            setupLayout(true);

            switch (error) {
                case "XmlPullParserException":
                    binding.errorMessage.setText(R.string.error_XmlPullParser);
                    break;
                case "UnknownHostException":
                    if (connectionFunctional()) {
                        binding.errorMessage.setText(R.string.error_UnknownHost);
                    } else {
                        binding.errorMessage.setText(R.string.error_UnknownHost_NoInternet);
                    }
                    break;
                case "MalformedURLException":
                    binding.errorMessage.setText(R.string.error_MalformedURL);
                    break;
                case "SSLHandshakeException":
                    binding.errorMessage.setText(R.string.error_SSLHandshake);
                    break;
                case "SocketTimeoutException":
                    binding.errorMessage.setText(R.string.error_SocketTimeout);
                    break;
                default:
                    // IOException or unknown error
                    binding.errorMessage.setText(R.string.error_general);
                    break;
            }
        }
    }

    /**
     * Check if the user has a functioning internet connection.
     * @return <code>true</code> if so, otherwise <code>false</code>.
     */
    private boolean connectionFunctional() {
        ConnectivityManager cm = requireActivity().getSystemService(ConnectivityManager.class);
        NetworkInfo connected = cm.getActiveNetworkInfo();
        return (connected != null);
    }

    private void setupEmptyFeedError() {
        binding.loadingOrException.setText(R.string.info_empty_feed);
        binding.errorMessage.setText(R.string.info_empty_feed_description);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (adapter == null) {
            adapter = new BasicAdapter(requireContext(), Collections.emptyList());
        }
        /* Scope FeedViewModel to MainActivity's lifetime, rather than FeedFragment.
         * (this prevents the ViewModel from getting destroyed/recreated
         *  whenever this Fragment's navigated away from) */
        viewModel = new ViewModelProvider(requireActivity()).get(FeedViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFeedBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.feedList.setAdapter(adapter);
        binding.refreshLayout.setOnRefreshListener(this::fetchRss);
        if (rssItems.isEmpty()) {
            List<Rss> vmItems = viewModel.getItems();
            if (vmItems == null || vmItems.isEmpty()) {
                fetchRss();
            } else {
                rssItems = vmItems;
                adapter.updateList(rssItems);
                setupLayout(false);
            }
        } else {
            setupLayout(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Executes <code>RssParser</code>,
     * either with the RSS URL stored in SharedPreferences (if there is one)
     * or the RSS_FALLBACK_URL declared in the module-level build file.<br>
     * The result ({@code <List>Rss}) is returned via a callback.
     */
    private void fetchRss() {
        SharedPreferences prefs = requireActivity().getPreferences(Context.MODE_PRIVATE);
        String url = prefs.getString("url", BuildConfig.RSS_FALLBACK_URL);
        new RssParser(url, this).execute("");
    }

    /**
     * Toggles visibility of certain View elements
     * @param errorOccurred Determines whether the error layout is displayed
     *                     ({@code true}) or the standard layout ({@code false})
     */
    private void setupLayout(boolean errorOccurred) {
        binding.progressBar.setVisibility(View.GONE);
        if (errorOccurred) {
            binding.feedList.setVisibility(View.GONE);
            binding.loadingOrException.setVisibility(View.VISIBLE);
            binding.errorMessage.setVisibility(View.VISIBLE);
        } else {
            binding.feedList.setVisibility(View.VISIBLE);
            binding.loadingOrException.setVisibility(View.GONE);
            binding.errorMessage.setVisibility(View.GONE);

            // Clear error text
            binding.loadingOrException.setText("");
            binding.errorMessage.setText("");
        }
    }
}