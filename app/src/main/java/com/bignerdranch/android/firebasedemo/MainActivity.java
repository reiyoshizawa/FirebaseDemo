package com.derrick.park.fbdemo;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText mEditText;
    private Spinner mSpinner;
    private ListView mListView;
    private ArrayList<Artist> mArtists;
    private ArrayAdapter<Artist> mArtistArrayAdapter;

    private DatabaseReference ref_artists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEditText = findViewById(R.id.artistET);
        mSpinner = findViewById(R.id.genres);
        mListView = findViewById(R.id.artistListView);
        mArtists = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref_artists = database.getReference("artists");

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // 1. which artist is clicked?
                Artist artist = mArtists.get(position);

                // 2. show the dialog.
                showUpdateDialog(artist.getId(), artist.getName(), artist.getGenre());
                return false;
            }
        });

    }

    private int getIndexForGenre(String genre) {

        switch (genre) {
            case "Pop":
                return 0;
            case "Rock":
                return 1;
            case "Hip-Hop":
                return 2;
            case "Classics":
                return 3;
            case "Samba":
                return 4;
            case "Reggae":
                return 5;
            case "K-Pop":
                return 6;
            case "EDM":
                return 7;
            default:
                return 0;
        }

    }

    private void showUpdateDialog(final String id, String name, String genre) {
        // 1. build the dialog with the custom layout
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.edit_artist_dialog, null);
        builder.setView(dialogView);

        final EditText editText = dialogView.findViewById(R.id.dialog_et);
        editText.setText(name);
        final Spinner spinner = dialogView.findViewById(R.id.dialog_spinner);
        spinner.setSelection(getIndexForGenre(genre));

        builder.setTitle("Update " + name);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        // 2. set click listener for update and delete buttons
        Button update_btn = dialogView.findViewById(R.id.dialog_update_btn);
        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Artist editted_artist = new Artist(
                        id,
                        editText.getText().toString().trim(),
                        spinner.getSelectedItem().toString());
                if (TextUtils.isEmpty(editText.getText().toString())) {
                    return;
                }

                ref_artists.child(id).setValue(editted_artist, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        Toast.makeText(MainActivity.this,
                                "Successfully updated the Artist",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialog.dismiss();
            }
        });

        Button delete_btn = dialogView.findViewById(R.id.dialog_delete_btn);
        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref_artists.child(id).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            Toast.makeText(MainActivity.this,
                                    "Successfully removed the Artist",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                alertDialog.dismiss();
            }
            // TODO: Remove tracks that belong to the artist
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        ref_artists.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // when database changed.
                mArtists.clear();
                for(DataSnapshot artistSnapshot: dataSnapshot.getChildren()){
                    Artist artist = artistSnapshot.getValue(Artist.class); // { id: .., name: ..., genre: ...}
                    mArtists.add(artist);
                }

                mArtistArrayAdapter = new ArrayAdapter<Artist>(
                        MainActivity.this,
                        android.R.layout.simple_list_item_1,
                        mArtists
                );
                mListView.setAdapter(mArtistArrayAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // when there's some error
                if (databaseError != null) {
                    Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public void addArtist(View view) {

        String artist_name = mEditText.getText().toString().trim();
        String artist_genre = mSpinner.getSelectedItem().toString();
        if (!TextUtils.isEmpty(artist_name)) {

            // 1. generate an unique id.
            String id = ref_artists.push().getKey();

            // 2. create an Artist Object using the id.
            Artist artist = new Artist(id, artist_name, artist_genre);

            // 3. add the artist as a child of "artists" ref_artists
            ref_artists.child(id).setValue(artist);

            mArtists.add(artist);
            mArtistArrayAdapter.notifyDataSetChanged();
            mEditText.setText("");
        } else {
            Toast.makeText(this, "Please Enter the Artist name...", Toast.LENGTH_SHORT).show();
        }

    }
}
