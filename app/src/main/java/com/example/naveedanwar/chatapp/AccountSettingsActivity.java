package com.example.naveedanwar.chatapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaDrm;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceDataStore;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.Gallery;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.yalantis.ucrop.UCrop;

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.R.attr.maxWidth;
import static android.R.attr.progress;

public class AccountSettingsActivity extends Activity {


    private static final int MAX_LENGTH =12 ;
    private TextView name;
    private TextView status;
    private Button change, update;
    private DatabaseReference databaseReference;
    private StorageReference mStorageRef;
    private FirebaseAuth firebaseAuth;
    private static final int galleryPic = 1;
    private CircleImageView imageView;
    private ProgressDialog pg;

    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        name =(TextView) findViewById(R.id.textView4);
        status =(TextView) findViewById(R.id.textView6);
        change = (Button)findViewById(R.id.change_picture);
        update = (Button)findViewById(R.id.update_status);
        imageView =(CircleImageView)findViewById(R.id.circleImageView);
        pg = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getCurrentUser().getUid();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String uname = dataSnapshot.child("username").getValue().toString();
                String ustatus = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String thumbnail_image = dataSnapshot.child("thumbnail_image").getValue().toString();
                name.setText(uname);
                status.setText(ustatus);
                Picasso.with(AccountSettingsActivity.this).load(image).into(imageView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
update.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        startActivity(new Intent(AccountSettingsActivity.this,StatusActivity.class));

    }
});

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i,"Images"),galleryPic);

            }



        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        Uri resultUri = data.getData();
        if(requestCode==galleryPic && resultCode==RESULT_OK){
            CropImage.activity(resultUri)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri image = result.getUri();
                String randomname =random()+".jpg";
                StorageReference dref = mStorageRef.child("profile_images").child(randomname);
                dref.putFile(image).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        pg.show();
                   if(task.isSuccessful()){
                      final String url = task.getResult().getDownloadUrl().toString();
                       databaseReference.child("image").setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {

                              pg.setTitle("Profile Picture");
                              pg.setMessage("Uploading please wait....");
                              pg.dismiss();
                              Picasso.with(AccountSettingsActivity.this).load(url).into(imageView);

                           }
                       });



                     //  Toast.makeText(AccountSettingsActivity.this,"Toast",Toast.LENGTH_LONG).show();
                   }
                    }
                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }
    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(MAX_LENGTH);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

}
