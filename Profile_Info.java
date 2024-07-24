package com.example.lenovo.bdfoodcart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.lenovo.bdfoodcart.Model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Profile_Info extends AppCompatActivity{


    private ImageButton imageButton;
    private static final int GALLERY_REQUEST = 1;
    private Uri mImageUri = null;
    private EditText et1,et2,et3;
    private RadioGroup rgroup;
    private RadioButton rbutton;
    private Button btn;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    public DatabaseReference databaseCooker;
    public DatabaseReference databaseBooker;
    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile__info);

        imageButton = (ImageButton) findViewById(R.id.imageButton);
        et1 = (EditText) findViewById(R.id.et1);
        et2 = (EditText) findViewById(R.id.et2);
        et3 = (EditText) findViewById(R.id.et3);

        rgroup = (RadioGroup) findViewById(R.id.rcategory);
        rbutton = (RadioButton) findViewById(R.id.rcook);
        btn = (Button) findViewById(R.id.btn);
        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        databaseCooker = FirebaseDatabase.getInstance().getReference("cooker");
        databaseBooker=FirebaseDatabase.getInstance().getReference("booker");

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPosting();
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_REQUEST);
            }
        });

    }

    private void startPosting() {

        String name = et1.getText().toString().trim();
        String phn = et2.getText().toString().trim();
        String occ = et3.getText().toString().trim();


        if(TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please enter name!", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phn)) {
            Toast.makeText(this, "Please enter mobile number!", Toast.LENGTH_SHORT).show();
        }
        else if(mImageUri==null){
            Toast.makeText(this, "Please Select Your Photo!", Toast.LENGTH_SHORT).show();
        }

        else if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(phn) && mImageUri!=null ) {

            progressDialog.setMessage("Loading, Please Wait...");
            progressDialog.show();
            String uid =FirebaseAuth.getInstance().getCurrentUser().getUid();

            //StorageReference filepath = mStorage.child("User Photos").child(mImageUri.getLastPathSegment());
            StorageReference filepath =mStorage.child("Profile Photos").child(uid+".jpg");
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    addUser(downloadUrl);
                }
            });
        }
    }


    private void addUser(Uri downloadUrl) {
        //Toast.makeText(this, cat, Toast.LENGTH_SHORT).show();

        String name = et1.getText().toString().trim();
        String phn = et2.getText().toString().trim();
        String occ = et3.getText().toString().trim();
        String image = downloadUrl.toString();
        String uid = firebaseAuth.getCurrentUser().getUid();

        if (rgroup.getCheckedRadioButtonId() == rbutton.getId()) {
            //String id = databaseCooker.push().getKey();
            User user = new User(name, phn, image, "supply");
            databaseCooker.child(uid).setValue(user);

            et1.setText("");
            et2.setText("");
            et3.setText("");
            Toast.makeText(this, "Successfully User Added!", Toast.LENGTH_SHORT).show();

            progressDialog.dismiss();

            Intent in = new Intent(Profile_Info.this, CookActivity.class);
            in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(in);

        } else {
            //String id = databaseBooker.push().getKey();
            User user = new User(name, phn, image, "order");
            databaseBooker.child(uid).setValue(user);

            et1.setText("");
            et2.setText("");
            et3.setText("");
            Toast.makeText(this, "Successfully User Added!", Toast.LENGTH_SHORT).show();

            progressDialog.dismiss();

            Intent in = new Intent(Profile_Info.this, CustActivity.class);
            in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(in);
            finish();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            mImageUri = data.getData();
            imageButton.setImageURI(mImageUri);
        }
    }
}
