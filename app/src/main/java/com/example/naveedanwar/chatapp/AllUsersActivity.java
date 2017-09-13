package com.example.naveedanwar.chatapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersActivity extends AppCompatActivity {

    private Toolbar toolbar;
    public  RecyclerView mList;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_all_users);
        mList = (RecyclerView)findViewById(R.id.mList);
        mList.setHasFixedSize(true);
        mList.setLayoutManager(new LinearLayoutManager(this));
        toolbar = (Toolbar)findViewById(R.id.usertoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Users,UserViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UserViewHolder>(
                Users.class,
                R.layout.single_user_layout,
                UserViewHolder.class,
                databaseReference

        ) {
            @Override
            protected void populateViewHolder(UserViewHolder viewHolder, Users model, int position) {
                viewHolder.setName(model.getUsername());
                viewHolder.setStatus(model.getStatus());
                viewHolder.setImage(model.getImage());
            }
        };
        mList.setAdapter(firebaseRecyclerAdapter);
    }





    public static class UserViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public UserViewHolder(View itemView) {
            super(itemView);
            mView =  itemView;
        }

        public void setName(String names){

            TextView name = (TextView)mView.findViewById(R.id.urname);
            name.setText(names);
        }
        public void setStatus(String ustatus){
            TextView status = (TextView)mView.findViewById(R.id.urstatus);
            status.setText(ustatus);
        }

        public void setImage(String uimage){
            CircleImageView image = (CircleImageView)mView.findViewById(R.id.urimage);
          //  Picasso.with(this).load(uimage).into(image);


        }
    }







}
