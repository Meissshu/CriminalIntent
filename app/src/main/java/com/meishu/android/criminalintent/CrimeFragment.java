package com.meishu.android.criminalintent;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Meishu on 16.03.2017.
 */

public class CrimeFragment extends Fragment {

    private Crime crime;
    private EditText titleField;
    private Button dateButton;
    private CheckBox solvedCheckBox;
    private Button reportButton;
    private Button suspectButton;
    private ImageButton callSuspect;
    private ImageButton photoButton;
    private ImageView photoView;
    private File photoFile;


    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    public static final String DIALOG_MINIATURE = "DialogMiniature";
    private static final int REQUEST_DATE = 0;
    public static final int REQUEST_SUSPECT = 1;
    public static final int REQUEST_TAKE_A_PIC = 2;

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        crime = CrimeLab.get(getActivity()).getCrime(crimeId);
        photoFile = CrimeLab.get(getActivity()).getPhotoFile(crime);
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(crime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        titleField = (EditText) v.findViewById(R.id.crime_title);
        titleField.setText(crime.getTitle());
        titleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                crime.setTitle(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {
                //
            }
        });

        dateButton = (Button) v.findViewById(R.id.crime_date);
        dateButton.setText(new SimpleDateFormat("EEEE, MMM dd, yyyy").format(crime.getDate()));
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.getInstance(crime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        solvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        solvedCheckBox.setChecked(crime.isSolved());
        solvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                crime.setSolved(isChecked);
            }
        });

        reportButton = (Button) v.findViewById(R.id.btn_crime_report);
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mime = "text/plain";
                Intent intent = ShareCompat.IntentBuilder.from(getActivity())
                        .setType(mime)
                        .setText(getCrimeReport())
                        .setSubject(getString(R.string.crime_report_suspect))
                        .setChooserTitle(getString(R.string.send_report))
                        .createChooserIntent();

                startActivity(intent);
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        suspectButton = (Button) v.findViewById(R.id.btn_crime_suspect);
        suspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_SUSPECT);
            }
        });
        if (crime.getSuspected() != null) {
            suspectButton.setText(crime.getSuspected());
        }

        //final Intent getSuspectNumber = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        callSuspect = (ImageButton) v.findViewById(R.id.ib_call_suspect);
        callSuspect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivityForResult(pickContact, REQUEST_SUSPECT_NUMBER);
                Intent intent = getDialIntent();
                if (intent == null) {
                    showNullSuspectAlertDialog();
                } else {
                    startActivity(intent);
                }
            }
        });

        final Intent takeAPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoButton = (ImageButton) v.findViewById(R.id.ib_crime_camera);

        boolean isPhotoAvailable = (photoFile != null) && (takeAPicture.resolveActivity(getActivity().getPackageManager()) != null);
        photoButton.setEnabled(isPhotoAvailable);

        if (isPhotoAvailable) {
            Uri uri = Uri.fromFile(photoFile);
            takeAPicture.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(takeAPicture, REQUEST_TAKE_A_PIC);
            }
        });

        photoView = (ImageView) v.findViewById(R.id.iv_crime_photo);
        ViewTreeObserver viewTreeObserver = photoView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                updateImageView();
            }
        });
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photoFile == null || !photoFile.exists())
                    return;
                FragmentManager manager = getFragmentManager();
                BigMiniatureFragment dialog = BigMiniatureFragment.getInstance(photoFile.getPath());
                dialog.show(manager, DIALOG_MINIATURE);
            }
        });


        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            suspectButton.setEnabled(false);
        }
        return v;
    }

    private void updateImageView() {
        if (photoFile == null || !photoFile.exists()) {
            photoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(photoFile.getPath(), photoView.getWidth(), photoView.getHeight());
            photoView.setImageBitmap(bitmap);
        }
    }

    private void showNullSuspectAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.call_null_suspect))
                .setTitle(R.string.call_null_suspect_title)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialog != null)
                            dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;
        switch (requestCode) {
            case REQUEST_DATE:
                Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                crime.setDate(date);
                dateButton.setText(crime.getDate().toString());
                break;
            case REQUEST_SUSPECT:
                if (data != null) {
                    Uri uri = data.getData();
                    // Определения полей, значения которых должны быть возвращены
                    String[] queryFields = {ContactsContract.Contacts.DISPLAY_NAME};

                    // выполнение запроса
                    Cursor cursor = getActivity().getContentResolver().query(uri, queryFields, null, null, null);

                    try {
                        if (cursor.moveToFirst()) {
                            String suspect = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)); // because we have only one column
                            crime.setSuspected(suspect);
                            suspectButton.setText(suspect);
                        }
                    } finally {
                        cursor.close();
                    }
                }
                break;
            case REQUEST_TAKE_A_PIC:
                updateImageView();
                break;
            default:
                break;
        }

    }

    private String getCrimeReport() {
        String solvedString;
        if (crime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, crime.getDate()).toString();

        String suspect = crime.getSuspected();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        return getString(R.string.crime_report, crime.getTitle(), dateString, solvedString, suspect);
    }

    private Intent getDialIntent() {
        // TODO: check for name exists in contacts
        String suspectedName = crime.getSuspected();
        if (suspectedName == null)
            return null;

        String[] queryFields = {ContactsContract.Contacts._ID};
        String selection = ContactsContract.Contacts.DISPLAY_NAME + " = ?";
        String[] selectionArgs = {suspectedName};

        Cursor cursor = getActivity().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, queryFields, selection, selectionArgs, null);

        String contactId = null;
        try {
            if (cursor.moveToFirst()) {
                contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            }
        } finally {
            cursor.close();
        }

        Cursor cursorPhone = getActivity().getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID +
                        " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
                new String[]{contactId},
                null);

        String contactNumber = null;
        try {
            if (cursorPhone.moveToFirst()) {
                contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
        } finally {
            cursorPhone.close();
        }

        Uri uriNumber = Uri.parse("tel:" + contactNumber);

        return new Intent(Intent.ACTION_DIAL, uriNumber);
    }
}
