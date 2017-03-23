package de.lennartmeinhardt.android.moiree;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import de.lennartmeinhardt.android.moiree.menu.MenuFragment;

public class HelpMenu extends MenuFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Button watchVideoButton = (Button) view.findViewById(R.id.watch_video);
        watchVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openYoutubeVideo();
            }
        });
    }

    private void openYoutubeVideo() {
        String url = getString(R.string.youtube_link_freaky_dot_patterns);
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }
}
