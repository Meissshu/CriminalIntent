package com.meishu.android.criminalintent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Meishu on 15.07.2017.
 */

public class BigMiniatureFragment extends DialogFragment {

    public static final String ARG_FILE = "file.miniature";
    private ImageView imageView;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String path = (String) getArguments().getSerializable(ARG_FILE);
        Bitmap bitmap = PictureUtils.getScaledBitmap(path, getActivity());

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.big_miniature_fragment, null);
        imageView = (ImageView) v.findViewById(R.id.iv_big_miniature);
        imageView.setImageBitmap(bitmap);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = getDialog();
                if (dialog != null)
                    dialog.dismiss();
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialog != null)
                            dialog.dismiss();
                    }
                })
                .create();
    }

    public static BigMiniatureFragment getInstance(String path) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_FILE, path);
        BigMiniatureFragment fragment = new BigMiniatureFragment();
        fragment.setArguments(bundle);

        return fragment;

    }
}
