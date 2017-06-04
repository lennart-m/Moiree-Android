package de.lennartmeinhardt.android.moiree.menu;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import de.lennartmeinhardt.android.moiree.R;

public class AboutMenu extends MenuFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.menu_about, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Button sendEmailButton = (Button) view.findViewById(R.id.send_email);
        sendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });

        Button viewSourceCodeButton = (Button) view.findViewById(R.id.view_source_code);
        viewSourceCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewSourceCode();
            }
        });

        Button viewDesktopVersionButton = (Button) view.findViewById(R.id.view_desktop_version);
        viewDesktopVersionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewDesktopVersion();
            }
        });

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    }

    private void viewSourceCode() {
        String url = getString(R.string.source_code_address);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    private void viewDesktopVersion() {
        String url = getString(R.string.desktop_version_address);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    private void sendEmail() {
        ShareCompat.IntentBuilder.from(getActivity())
                .setType("message/rfc822")
                .addEmailTo(getString(R.string.email_address))
                .setSubject(getString(R.string.email_subject))
                .startChooser();
    }
}
