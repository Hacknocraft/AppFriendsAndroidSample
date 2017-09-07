package me.appfriends.androidsample.sampleapp.channels;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.appfriends.androidsample.R;
import me.appfriends.sdk.model.Channel;
import me.appfriends.ui.channel.ChannelActivity;
import me.appfriends.ui.channellist.ChannelListRecyclerView;
import me.appfriends.ui.channellist.OnChannelClickListener;

/**
 * Created by Mike Dai Wang on 2017-05-15.
 */

public class ChannelListFragment extends Fragment {

    public static ChannelListFragment createInstance() {
        return new ChannelListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_channel_list, container, false);

        final ChannelListRecyclerView recyclerView
                = (ChannelListRecyclerView) view.findViewById(R.id.channel_list_recycler_view);

        recyclerView.setOnChannelClickListener(new OnChannelClickListener<Channel>() {
            @Override
            public void onChannelClicked(Channel channel) {
                startActivity(ChannelActivity.actionView(getContext(), channel.id));
            }
        });

        return view;
    }
}
