package com.tehgan.rssreader.ui.detail;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tehgan.rssreader.R;
import com.tehgan.rssreader.data.Rss;
import com.tehgan.rssreader.interfaces.ThumbnailCallback;
import com.tehgan.rssreader.tasks.ThumbnailDownloader;
import com.tehgan.rssreader.data.repo.RssRepo;
import com.tehgan.rssreader.databinding.FragmentDetailBinding;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailFragment extends Fragment implements ThumbnailCallback {
    private FragmentDetailBinding binding;
    private String thumbnailRawUrl;

    private RssRepo rssRepo;

    @Override
    public void onThumbnailDownloaded(Bitmap bmp) {
        if (binding != null && bmp != null) {
            binding.thumbnail.setImageBitmap(bmp);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rssRepo = new RssRepo(this.requireActivity().getApplication());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // As it stands it's impossible for the args to be null.
        DetailFragmentArgs args = DetailFragmentArgs.fromBundle(getArguments());
        Rss rss = args.getRss();

        // Binding validity is checked in toggleFavouriteButton()
        /* In a 'real-world' program this should probably be optimized to account for large tables,
         * but wall-clock speed is instantaneous with small tables, so it's good enough academically. */
        rssRepo.getAll().observe(getViewLifecycleOwner(), rssEntities -> {
            boolean isFavourite = false;
            for (int i = 0; i < rssEntities.size(); i++) {
                if (rssEntities.get(i).link.equals(rss.getLink())) {
                    isFavourite = true;
                    break;
                }
            }
            toggleFavouriteButton(isFavourite, rss);
        });

        thumbnailRawUrl = rss.getThumbnailRawUrl();
        fetchThumbnail();

        binding.headline.setText(rss.getTitle());

        String date = getFormattedDate(rss.getPubDate());
        binding.date.setText(date);

        // Underlines detailLink's text, to style it more similarly to a hyperlink.
        // Adapted from https://stackoverflow.com/a/2394939
        String rawLink = rss.getLink();
        SpannableString link = new SpannableString(rawLink);
        link.setSpan(new UnderlineSpan(), 0, link.length(), 0);
        binding.link.setText(link);

        // When the link is clicked, open it in the user's preferred web browser
        binding.link.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(rawLink));
            startActivity(i);
        });

        // RSS descriptions may have HTML tags e.g. <b>, <i>, <p>, etc.
        // Filter out img tags (otherwise fromHtml fills them in with a placeholder drawable)
        String desc = rss.getDescription().replaceAll("<img.+/(img)*>", "");
        binding.description.setText(Html.fromHtml(
                desc,
                Html.FROM_HTML_MODE_LEGACY,
                null,
                null)
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void toggleFavouriteButton(Boolean favourite, Rss rss) {
        if (binding != null) {
            if (favourite) {
                binding.toggleFavourite.setText(R.string.unfavourite);
                binding.toggleFavourite.setOnClickListener(
                        v -> rssRepo.deleteViaLink(rss.getLink())
                );
            } else {
                binding.toggleFavourite.setText(R.string.favourite);
                binding.toggleFavourite.setOnClickListener(
                        v -> rssRepo.insert(rssRepo.dataToEntity(rss))
                );
            }
        }
    }

    private void fetchThumbnail() {
        Log.d("DetailFragment", "Fetching thumbnail...");
        new ThumbnailDownloader(this).execute(thumbnailRawUrl);
    }

    /**
     * Tries to format the raw date into a user-friendly String.
     * Follows RSS spec, but some feeds provide non-standardized dates;
     * if such a date is encountered, it's returned as-is.
     * @param rawDate The RSS item's pubDate
     */
    /* The RSS spec claims "that the year may be expressed with two characters or four characters"
     *  and gives an example of a date from 2002...
     * I don't imagine any active feeds would still be using a 2-digit year. */
    private String getFormattedDate(String rawDate) {
        DateFormat format = null;
        if (rawDate.startsWith("Thur,")) {
            /* SimpleDateFormat requires a 3-letter day yet certain feeds write Thursday as "Thur";
             *  replacing "Thur" with "Thu" works around that. */
            rawDate = rawDate.replace("Thur,", "Thu,");
        }

        if (rawDate.matches(".*\\s[A-Za-z]{3}$")) {
            // Ends with alphabetical time zone
            format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
        } else if (rawDate.matches(".*[+-]\\d{4}$")) {
            // Ends with offset e.g. "+0000"
            format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
        }

        Date d = null;
        if (format != null) {
            try {
                d = format.parse(rawDate);
            } catch (ParseException e) {
                new RuntimeException(e).printStackTrace();
                Log.w("getFormattedDate", "Parser exception encountered, returning unformatted date.");
            }
        }

        String formattedDate = null;
        if (d != null) {
            // E.g. "Mon. Nov. 24 2025, 4:11 p.m."
            formattedDate = new SimpleDateFormat("ccc MMM d yyyy, h:mm a", Locale.getDefault()).format(d);
        }

        return (formattedDate != null ? formattedDate : rawDate);
    }
}