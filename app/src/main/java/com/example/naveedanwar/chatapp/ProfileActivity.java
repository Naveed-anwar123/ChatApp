package com.example.naveedanwar.chatapp;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {



    private TextView name , status, count;
    private ImageView imageView;
    private Button sendFriendRequest;
    String uid;
    private DatabaseReference databaseReference , requestReference;
    private ProgressDialog pg;
    private Button sendbtn , declinebtn;
    private String current_status;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
         uid = getIntent().getStringExtra("user_id");
        name = (TextView)findViewById(R.id.dpname);
        status = (TextView)findViewById(R.id.dpstatus);
        count = (TextView)findViewById(R.id.dpcount);
        imageView = (ImageView)findViewById(R.id.dp);
        Typeface face= Typeface.createFromAsset(getAssets(), "fonts/Pacifico-Regular.ttf");
        name.setTypeface(face);
        sendbtn = (Button)findViewById(R.id.sendbtn);
        declinebtn = (Button)findViewById(R.id.declinebtn);
        current_status = "not_friends";
        pg = new ProgressDialog(this);
        pg.setTitle("Loading");
        pg.setMessage("Pleas wait...");
        pg.show();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        requestReference = FirebaseDatabase.getInstance().getReference().child("FriendRequests");
        mAuth = FirebaseAuth.getInstance();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name.setText(dataSnapshot.child("username").getValue().toString());
                status.setText(dataSnapshot.child("status").getValue().toString());
                String uri = dataSnapshot.child("image").getValue().toString();
                Picasso.with(ProfileActivity.this).load(uri).placeholder(R.drawable.crib).into(imageView);
                pg.dismiss();
                requestReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                       Toast.makeText(ProfileActivity.this,"Toast",Toast.LENGTH_LONG).show();
                        if(dataSnapshot.hasChild(uid)){
                            String stat = dataSnapshot.child(uid).child("request_type").getValue().toString();
                            if(stat.equals("received")){
                                current_status = "received";
                                sendbtn.setText("Accept Friend Request");
                            }
                            else if (stat.equals("sent")){
                                current_status = "sent";
                                sendbtn.setText("Cancel Friend Request");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(current_status.equals("not_friends")){
                    requestReference.child(mAuth.getCurrentUser().getUid()).child(uid).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                requestReference.child(uid).child(mAuth.getCurrentUser().getUid()).child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        sendbtn.setEnabled(true);
                                        sendbtn.setText("Cancel Friend Request");

                                        current_status = "friends";
                                    }
                                });

                            }
                        }
                    });

                }
                else if(current_status.equals("friends")){
                    requestReference.child(mAuth.getCurrentUser().getUid()).child(uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            requestReference.child(uid).child(mAuth.getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                current_status = "not_friends";
                                    sendbtn.setText("Send Friend Request");
                                }
                            });
                        }
                    });
                }

            }
        });




    }





    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(ProfileActivity.this,uid,Toast.LENGTH_LONG).show();

    }
}
