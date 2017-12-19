package com.google.firebase.codelab.friendlychat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final String TIME = "time";
    public static final String MSG = "msg";
    public static final String FROM = "from";

    private Button mSendButton;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;
    private EditText mMessageEditText;

    private FirestoreRecyclerAdapter mFirebaseAdapter;

    // Firebase instance variables

    public static final String COLLECTION_REF = "/chats/COL/cities/BOG/orders/CW00000000/messages/";

    private CollectionReference mColRef = FirebaseFirestore.getInstance()
            .collection(COLLECTION_REF);


    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        ImageView messageImageView;
        TextView messengerTextView;
        CircleImageView messengerImageView;

        public MessageViewHolder(View v) {
            super(v);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            messageImageView = (ImageView) itemView.findViewById(R.id.messageImageView);
            messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
            messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize ProgressBar and RecyclerView.
        mProgressBar = findViewById(R.id.progressBar);
        mMessageRecyclerView = findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        fetchMessages();

        mMessageEditText = findViewById(R.id.messageEditText);

        mSendButton = findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    public void fetchMessages() {

        Query query = FirebaseFirestore.getInstance()
                .collection(COLLECTION_REF)
                .orderBy("time"); //.limit(50);

        // Configure recycler adapter options:
        //  * query is the Query object defined above.
        //  * FriendlyMessage.class instructs the adapter to convert each DocumentSnapshot to a FriendlyMessage object

        FirestoreRecyclerOptions<FriendlyMessage> options =
                new FirestoreRecyclerOptions.Builder<FriendlyMessage>()
                .setQuery(query, FriendlyMessage.class)
                .build();

        mFirebaseAdapter = new FirestoreRecyclerAdapter<FriendlyMessage, MessageViewHolder>(options) {

            @Override
            public MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new MessageViewHolder(inflater.inflate(R.layout.item_message, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(final MessageViewHolder viewHolder,
                                            int position,
                                            FriendlyMessage friendlyMessage) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);

                if (friendlyMessage.getMsg() != null) {
                    viewHolder.messageTextView.setText(friendlyMessage.getMsg());
                    viewHolder.messageTextView.setVisibility(TextView.VISIBLE);
                }

                viewHolder.messengerTextView.setText(friendlyMessage.getFrom());
            }
        };

        mMessageRecyclerView.setAdapter(mFirebaseAdapter);

    }

    private void sendMessage() {
        FriendlyMessage friendlyMessage = new FriendlyMessage("shopper",
                mMessageEditText.getText().toString(),
                new Date());

        if(friendlyMessage.getMsg().isEmpty() || friendlyMessage.getFrom().isEmpty()) { return; }

        Map<String, Object> dataToSave = new HashMap<>();
        dataToSave.put(FROM, friendlyMessage.getFrom());
        dataToSave.put(MSG, friendlyMessage.getMsg());
        dataToSave.put(TIME, friendlyMessage.getTime());

        mColRef.add(dataToSave).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {

            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {

                if (task.isSuccessful()) {
                    Log.e(TAG, "Message was saved");
                } else {
                    Log.e(TAG, "ERROR", task.getException());
                }

            }
        });

        mMessageEditText.setText("");
    }

    @Override
    public void onStart() {
        super.onStart();
        mFirebaseAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFirebaseAdapter.stopListening();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
