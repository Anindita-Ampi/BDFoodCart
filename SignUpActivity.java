package com.example.lenovo.bdfoodcart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.lenovo.bdfoodcart.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SignUpActivity extends AppCompatActivity {

    private ImageButton imageButton;
    private static final int GALLERY_REQUEST = 1;
    private Uri mImageUri = null;
    private EditText et1,et2,et3,et4;
    private RadioGroup rgroup;
    private RadioButton rbutton;
    private Button btn;
    private ProgressDialog progressDialog;
    public FirebaseAuth firebaseAuth;
    public DatabaseReference databaseBooker;
    public DatabaseReference databaseCooker;
    public StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        imageButton = (ImageButton) findViewById(R.id.imageButton);
        et1 = (EditText) findViewById(R.id.et1);
        et2 = (EditText) findViewById(R.id.et2);
        et3 = (EditText) findViewById(R.id.et3);
        et4 = (EditText) findViewById(R.id.et4);
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
                progressDialog.setMessage("Signing Up, Please Wait...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                registerUser();
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

        /*progressDialog.setMessage("Loading, Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();*/
        String uid =FirebaseAuth.getInstance().getCurrentUser().getUid();

       // StorageReference filepath = mStorage.child("User Photos").child(mImageUri.getLastPathSegment());
        StorageReference filepath =mStorage.child("Profile Photos").child(uid+".jpg");
        filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    addUser(downloadUrl);
                }
        });

    }

    private void addUser(Uri downloadUrl) {
        //Toast.makeText(this, cat, Toast.LENGTH_SHORT).show();

        String name = et1.getText().toString().trim();
        String phn = et2.getText().toString().trim();
        String image = downloadUrl.toString();
        String uid = firebaseAuth.getCurrentUser().getUid();

        if (rgroup.getCheckedRadioButtonId() == rbutton.getId()) {
            //String id = databaseCooker.push().getKey();
            User user = new User(name, phn, image, "supply");
            databaseCooker.child(uid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    et1.setText("");
                    et2.setText("");
                    et3.setText("");
                    et4.setText("");
                    Toast.makeText(getApplicationContext(), "Successfully User Added!", Toast.LENGTH_SHORT).show();

                    progressDialog.dismiss();

                    Intent in = new Intent(SignUpActivity.this, CookActivity.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(in);
                    finish();

                }
            });

        } else {
            //String id = databaseBooker.push().getKey();
            User user = new User(name, phn, image, "order");
            databaseBooker.child(uid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    et1.setText("");
                    et2.setText("");
                    et3.setText("");
                    et4.setText("");
                    Toast.makeText(SignUpActivity.this, "Successfully User Added!", Toast.LENGTH_SHORT).show();

                    progressDialog.dismiss();

                    Intent in = new Intent(SignUpActivity.this, CustActivity.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(in);
                    finish();

                }
            });

        }

    }


    private void registerUser() {


        String name = et1.getText().toString().trim();
        String phn = et2.getText().toString().trim();
        String email = et3.getText().toString().trim();
        String password = et4.getText().toString().trim();
        //validation
        if(TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please enter name!", Toast.LENGTH_SHORT).show();
            progressDialog.hide();
            return;
        }
        else if(TextUtils.isEmpty(phn)) {
            Toast.makeText(this, "Please enter mobile number!", Toast.LENGTH_SHORT).show();
            progressDialog.hide();
            return;
        }
        else if(mImageUri==null){
            Toast.makeText(this, "Please Select Your Photo!", Toast.LENGTH_SHORT).show();
            progressDialog.hide();
            return;
        }
        else if (TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter email!", Toast.LENGTH_SHORT).show();
            progressDialog.hide();
            return;
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter password!", Toast.LENGTH_SHORT).show();
            progressDialog.hide();
            return;
        }
        else if (password.length() <= 5){
            Toast.makeText(this, "Password must be more than 5 characters!", Toast.LENGTH_SHORT).show();
            progressDialog.hide();
            return;
        }



        //now we can create the user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            
                            Toast.makeText(SignUpActivity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                            startPosting();


                        } else {

                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                progressDialog.hide();
                                Toast.makeText(SignUpActivity.this, "Email already exists!", Toast.LENGTH_SHORT).show();
                            }else {
                                progressDialog.hide();
                                Toast.makeText(SignUpActivity.this, "Something went terrible wrong!", Toast.LENGTH_SHORT).show();

                            }
                            return;
                        }
                    }
                });
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
