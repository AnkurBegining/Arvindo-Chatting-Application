package com.example.android.arvindo;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;

    public static final int RC_SIGN_IN = 1;

    private static int RC_PHOTO_PICKER = 2;

    private ListView mMessageListView;
    private MessageAdapter mAdapter;
    private ProgressBar mProgressBar;
    private ImageButton mImageButton;
    private EditText mEditText;
    private Button mSendButton;

    private String mUserName;

    //All Firebase Declaration
    private FirebaseDatabase mFirebaseDatabase;
    //Use for database reference for message body
    private DatabaseReference mFirebaseDatabaseReference;
    //To read data on screen
    private ChildEventListener mChildEventListner;
    //Authenticate
    private FirebaseAuth mFireBaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private FirebaseStorage mFirebaseStorage;
    private StorageReference mFirebaseStorageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseDatabaseReference = mFirebaseDatabase.getReference().child("messages");

        mUserName = ANONYMOUS;

        mFireBaseAuth = FirebaseAuth.getInstance();
        mFirebaseStorage=FirebaseStorage.getInstance();
        mFirebaseStorageReference = mFirebaseStorage.getReference().child("photo_chat");

        //initialize Reference to view
        mMessageListView = (ListView) findViewById(R.id.list_item_view);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mImageButton = (ImageButton) findViewById(R.id.photo_picker);
        mEditText = (EditText) findViewById(R.id.message_edit_text);
        mSendButton = (Button) findViewById(R.id.send_button);

        //initialize Message ListView and its adapter
        List<MessageBody> message = new ArrayList<>();
        mAdapter = new MessageAdapter(this, R.layout.list_item, message);
        mMessageListView.setAdapter(mAdapter);

        //initialize ProgressBar
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        //ImagePicker button is used to upload Image
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);


            }
        });

        //Enable send button when there is text to send
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        //send Button send the messaage and clear the editText

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Send message on click

                MessageBody currentMessageBody = new MessageBody(mEditText.getText().toString(), mUserName, null);

                mFirebaseDatabaseReference.push().setValue(currentMessageBody);

                //clear the text
                mEditText.setText("");
                Log.v("MainActivity = ", "TEXT BOX CLEAR");
            }
        });


        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    //User is signed in
                    Toast.makeText(MainActivity.this, "User is signed in", Toast.LENGTH_LONG).show();
                    onSignedIntialized(user.getDisplayName());
                } else {
                    Toast.makeText(MainActivity.this, "User is logged out", Toast.LENGTH_LONG).show();
                    onSignedOutCleanUp();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(
                                            AuthUI.EMAIL_PROVIDER,
                                            AuthUI.GOOGLE_PROVIDER)
                                    .build(),
                            +RC_SIGN_IN);
                }


            }
        };

    }

    private void onSignedIntialized(String userName) {
        mUserName = userName;
        attachedDataBaseReadListner();

    }

    private void attachedDataBaseReadListner() {
        if (mChildEventListner == null) {
            mChildEventListner = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    MessageBody currentMessageBody = dataSnapshot.getValue(MessageBody.class);
                    mAdapter.add(currentMessageBody);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            //Reference to find what exactly i am listening to.
            mFirebaseDatabaseReference.addChildEventListener(mChildEventListner);
        }
    }

    private void onSignedOutCleanUp() {
        mUserName = ANONYMOUS;
        mAdapter.clear();
        detachDateBaseListner();

    }

    private void detachDateBaseListner() {
        if (mChildEventListner != null) {
            mFirebaseDatabaseReference.removeEventListener(mChildEventListner);
            mChildEventListner = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sign_out:
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(MainActivity.this, "User successfully logged in", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(MainActivity.this, "User could not signed properly", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        else if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK){
            Uri selectedImageUri = data.getData();
            mFirebaseStorageReference.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    MessageBody currentMessageBody = new MessageBody(null,mUserName,downloadUrl.toString());
                    mFirebaseDatabaseReference.push().setValue(currentMessageBody);

                }
            });

        }



       
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFireBaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFireBaseAuth.removeAuthStateListener(mAuthStateListener);
        detachDateBaseListner();
        mAdapter.clear();
    }
}
