package com.bignerdranch.android.firebasedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class    MainActivity extends AppCompatActivity {

    private EditText mEditText;
    private Spinner mSpinner;
    private ListView listView;
    private ArrayList<Aritst> mAritsts;
    private ArrayAdapter<Aritst> mArtistArrayAdapter;
    private int id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEditText = findViewById(R.id.artist);
        mSpinner = findViewById(R.id.genre);
        listView = findViewById(R.id.artistListView);
        mAritsts = new ArrayList<>();
        mArtistArrayAdapter = new ArrayAdapter<Aritst>(
                this,
                android.R.layout.simple_list_item_1,
                mAritsts
        );
        listView.setAdapter(mArtistArrayAdapter);

    }

    public void addArtist(View view) {
        String artist_id = String.valueOf(id);
        String artist_name = mEditText.getText().toString().trim();
        String artist_genre = mSpinner.getSelectedItem().toString();

        if (!TextUtils.isEmpty(artist_name)) {
            Aritst aritst = new Aritst(artist_id, artist_name, artist_genre);

            mAritsts.add(aritst);
            mArtistArrayAdapter.notifyDataSetChanged();
            id++;
            mEditText.setText("");
        } else {
            Toast.makeText(this, "Please Enter the Artist name...", Toast.LENGTH_SHORT).show();
        }


    }
}
