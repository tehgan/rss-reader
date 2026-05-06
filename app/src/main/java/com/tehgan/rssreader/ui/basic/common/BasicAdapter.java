package com.tehgan.rssreader.ui.basic.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.tehgan.rssreader.NavGraphDirections;
import com.tehgan.rssreader.data.Rss;
import com.tehgan.rssreader.databinding.ItemBasicBinding;

import java.util.List;

public class BasicAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private List<Rss> items;

    /**
     * Inflaters aren't accessible from outside an Activity,
     *  yet (from what I've seen) nested classes are typically discouraged in modern app development,
     *  so this adapter requires a Context and only uses it to store a LayoutInflater for getView.
     */
    public BasicAdapter(Context c, List<Rss> rssData) {
        inflater = LayoutInflater.from(c.getApplicationContext());
        items = rssData;
    }

    public void updateList(List<Rss> rssData) {
        items = rssData;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemBasicBinding binding;
        if (convertView == null) {
            binding = ItemBasicBinding.inflate(inflater, parent, false);
            convertView = binding.getRoot();
            // Setting and getting a tag allows ListView to work efficiently with ViewBinding
            convertView.setTag(binding);
        } else {
            binding = (ItemBasicBinding) convertView.getTag();
        }

        // Get associated RSS data (getItem needs to be cast as it returns an ambiguous Object)
        Rss rd = (Rss) getItem(position);

        binding.basicHeadline.setText(rd.getTitle());

        // Navigate to the DetailFragment, and provide said fragment with the associated RSS data
        binding.getRoot().setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(NavGraphDirections.actionBasicFragmentToDetailFragment(rd));
        });

        return binding.getRoot();
    }
}
