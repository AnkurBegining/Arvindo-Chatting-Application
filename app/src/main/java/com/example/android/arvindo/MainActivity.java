package com.example.android.arvindo;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;

    private static int RESULT_LOAD_IMAGE = 1;

    private ListView mMessageListView;
    private MessageAdapter mAdapter;
    private ProgressBar mProgressBar;
    private ImageButton mImageButton;
    private EditText mEditText;
    private Button mSendButton;

    private String mUserName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUserName = ANONYMOUS;

        //Intilize Reference to view
        mMessageListView = (ListView) findViewById(R.id.list_item_view);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mImageButton = (ImageButton) findViewById(R.id.photo_picker);
        mEditText = (EditText) findViewById(R.id.message_edit_text);
        mSendButton = (Button) findViewById(R.id.send_button);

        //Intilize Message ListView and its adapter
        List<MessageBody> message = new ArrayList<>();

        mAdapter = new MessageAdapter(this, R.layout.list_item, message);
        mMessageListView.setAdapter(mAdapter);

        //Intilize ProgressBar
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        //ImagePicker button is used to upload Image
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo: We need to Intent here.


            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
