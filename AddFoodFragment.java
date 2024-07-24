package com.example.lenovo.bdfoodcart;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.lenovo.bdfoodcart.Model.Food;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddFoodFragment extends Fragment {

    private ImageButton imageButton;
    private static final int GALLERY_REQUEST = 1;
    private Uri mImageUri = null;
    private EditText et1,et2,et3;
    private Button btn;
    private ProgressDialog progressDialog;
    public FirebaseAuth firebaseAuth;
    public DatabaseReference databaseFood;
    public StorageReference mStorage;
    View myview;
    FragmentActivity context;


    public AddFoodFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myview=inflater.inflate(R.layout.fragment_add_food, container, false);



        context=getActivity();

        imageButton = (ImageButton) myview.findViewById(R.id.imageButton);
        et1 = (EditText) myview.findViewById(R.id.et1);
        et2 = (EditText) myview.findViewById(R.id.et2);
        et3 = (EditText) myview.findViewById(R.id.et3);
        btn = (Button) myview.findViewById(R.id.btn);
        progressDialog = new ProgressDialog(context);

        firebaseAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        databaseFood = FirebaseDatabase.getInstance().getReference("Food");

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
        return myview;
    }


    private void startPosting() {

        String name = et1.getText().toString().trim();
        String price = et2.getText().toString().trim();

        if(TextUtils.isEmpty(name)) {
            Toast.makeText(context, "Please enter Food name!", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(TextUtils.isEmpty(price)) {
            Toast.makeText(context, "Please enter Food Price!", Toast.LENGTH_SHORT).show();
            return;
        }

        else if(mImageUri==null){
            Toast.makeText(context, "Please Add Food Photo!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Loading, Please Wait...");
        progressDialog.show();

        StorageReference filepath = mStorage.child("User Foods").child(mImageUri.getLastPathSegment());
        filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                AddFood(downloadUrl);
            }
        });
    }


    private void AddFood(Uri downloadUrl) {

        String name = et1.getText().toString().trim();
        String price = et2.getText().toString().trim();
        String desc = et3.getText().toString().trim();
        String image = downloadUrl.toString();
        String uid = firebaseAuth.getCurrentUser().getUid();
        String foodid = databaseFood.push().getKey();

        Food food= new Food(name,image,price,desc,foodid);
        databaseFood.child(uid).child(foodid).setValue(food);
        et1.setText("");
        et2.setText("");
        et3.setText("");

        Toast.makeText(context, "Successfully Food Added!", Toast.LENGTH_SHORT).show();

        progressDialog.dismiss();

        Intent in = new Intent(context, CookActivity.class);
        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(in);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERY_REQUEST && resultCode==RESULT_OK){

            mImageUri = data.getData();
            imageButton.setImageURI(mImageUri);
        }
    }
}
