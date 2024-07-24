package com.example.lenovo.bdfoodcart;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.example.lenovo.bdfoodcart.R.attr.title;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileCookFragment extends Fragment {

    private View myview;
    private static final int GALLERY_REQUEST = 1;
    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;
    private CircleImageView mDisplayImage;
    private TextView textViewName,mNewImage,textViewNo;
    private EditText editTextName,editTextNo;
    public StorageReference mStorage;
    private ProgressDialog progressDialog;
    private Button buttonEdit, buttonDelete, buttonSave;


    public ProfileCookFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myview = inflater.inflate(R.layout.fragment_profile_cook, container, false);

        mDisplayImage = (CircleImageView) myview.findViewById(R.id.profile_image);
        mNewImage = (TextView)myview.findViewById(R.id.new_image);

        editTextName = (EditText) myview.findViewById(R.id.editTextName);
        textViewName = (TextView) myview.findViewById(R.id.textViewName);
        editTextNo = (EditText) myview.findViewById(R.id.editTextNo);
        textViewNo = (TextView) myview.findViewById(R.id.textViewNo);

        buttonEdit=(Button) myview.findViewById(R.id.buttonEdit);
        buttonDelete=(Button)myview. findViewById(R.id.buttonDelete);
        buttonSave=(Button)myview. findViewById(R.id.buttonSave);

        buttonSave.setVisibility(View.GONE);
        editTextName.setEnabled(false);
        editTextNo.setEnabled(false);

        mNewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent,"SELECT IMAGE"),GALLERY_REQUEST);

                /*

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
                */

            }
        });

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editTextName.setEnabled(true);
                editTextNo.setEnabled(true);
                buttonSave.setVisibility(View.VISIBLE);
                buttonEdit.setVisibility(View.GONE);

            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                mCurrentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getActivity(), "Profile Deleted!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), SignIn_Login.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);;
                            startActivity(intent);
                        }
                    }
                });


            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                buttonEdit.setVisibility(View.VISIBLE);
                buttonSave.setVisibility(View.GONE);

                updateProfile();

            }
        });

        mStorage = FirebaseStorage.getInstance().getReference();
        mCurrentUser=FirebaseAuth.getInstance().getCurrentUser();
        String uid = mCurrentUser.getUid();
        mUserDatabase= FirebaseDatabase.getInstance().getReference().child("cooker").child(uid);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name =dataSnapshot.child("userName").getValue().toString();
                String image =dataSnapshot.child("image").getValue().toString();
                String phn =dataSnapshot.child("userPhn").getValue().toString();

                editTextName.setText(name);
                editTextNo.setText(phn);
                Picasso.with(getActivity()).load(image).into(mDisplayImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return myview;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();
            CropImage.activity(imageUri).setAspectRatio(1,1).start(getContext(),this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                progressDialog=new ProgressDialog(getActivity());
                progressDialog.setTitle("Uploading Image...");
                progressDialog.setMessage("Please Wait...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                Uri resultUri = result.getUri();

                String uid = mCurrentUser.getUid();

                StorageReference filepath = mStorage.child("Profile Photos").child(uid +".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful()){

                            String downloadUrl = task.getResult().getDownloadUrl().toString();

                            mUserDatabase.child("image").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){

                                        progressDialog.dismiss();
                                        Toast.makeText(getActivity(), "Success Uploading", Toast.LENGTH_SHORT).show();

                                    }else{

                                        Toast.makeText(getActivity(), "Error in Uploading", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();

                                    }
                                }
                            });

                        }else{

                            Toast.makeText(getActivity(), "Error in Uploading", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                        }

                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }


    private void updateProfile() {

        String nm = editTextName.getText().toString().trim();
        String phn = editTextNo.getText().toString().trim();

        editTextName.setEnabled(false);
        editTextNo.setEnabled(false);

        mUserDatabase.child("userName").setValue(nm);
        mUserDatabase.child("userPhn").setValue(phn);

        Toast.makeText(getActivity(), "Profile Info Updated!", Toast.LENGTH_SHORT).show();
    }
}
