package com.example.lenovo.bdfoodcart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lenovo.bdfoodcart.Model.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn_Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,View.OnClickListener{

    private Button btnLogin;
    private EditText editTextEmail,editTextPassword;
    private TextView t4,textForgot;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private static final String TAG = "SignIn_Login";
    private static final int RC_SIGN_IN = 1;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private GoogleApiClient mGoogleApiClient;
    private SignInButton mGooglebtn;
    private DatabaseReference databaseBooker,databaseCooker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in__login);

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        btnLogin = (Button) findViewById(R.id.btn);
        btnLogin.setOnClickListener(this);
        t4 = (TextView) findViewById(R.id.t4);
        t4.setOnClickListener(this);
        textForgot = (TextView) findViewById(R.id.t2);
        textForgot.setOnClickListener(this);
        mGooglebtn= (SignInButton) findViewById(R.id.sign_in_button);
        mGooglebtn.setOnClickListener(this);
        progressDialog=new ProgressDialog(this);


        databaseBooker=FirebaseDatabase.getInstance().getReference("booker");
        databaseBooker.keepSynced(true);
        databaseCooker=FirebaseDatabase.getInstance().getReference("cooker");
        databaseCooker.keepSynced(true);

        mAuth = FirebaseAuth.getInstance();
        /*mAuthStateListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null) {
                   // startActivity(new Intent(SignIn_Login.this,Profile_Info.class));
                }
            }
        }; */

        //-------Google Sign-In-----

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())

                .enableAutoManage(this /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(SignIn_Login.this, "You Got an Error", Toast.LENGTH_SHORT).show();
                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        //-------email password-----
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() !=null) {
            // User is signed in
            // cook or cust ?


            checkUserExist();
            finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() !=null) {
            // User is signed in
            // cook or cust ?
            progressDialog.setMessage("Signing in, Please Wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            checkUserExist();
            finish();
        }
    }

    @Override
    public void onClick(View view) {

        if(view == btnLogin) {
            userLogin();
        }
        if (view == t4) {

            startActivity(new Intent(this,SignUpActivity.class));
            //will open Sign-up activity here
        }
        if(view==mGooglebtn){
            signIn();
        }

        if(view==textForgot){

            resetPassword();
        }

    }

    private void resetPassword() {

        String emailAddress = editTextEmail.getText().toString().trim();
        if (TextUtils.isEmpty(emailAddress)){
            Toast.makeText(this, "Please enter email!", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Email sent.");
                }
            }
        });
    }

    //email password
    private void userLogin() {

        //getting all the inputs from the user
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        //validation
        if (TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter email!", Toast.LENGTH_SHORT).show();
            return;
        }

        else if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        else if (password.length() <= 5){
            Toast.makeText(this, "Password must be more than 5 characters!", Toast.LENGTH_SHORT).show();
            return;
        }

        //progressDialog.setMessage("Checking Login, Please Wait...");
        //progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            finish();
                            progressDialog.hide();
                            //cook or cust ?
                            checkUserExist();
                            finish();

                        } else {

                            Toast.makeText(SignIn_Login.this, "Invalid Credentials!", Toast.LENGTH_SHORT).show();
                            editTextEmail.setText("");
                            editTextPassword.setText("");

                            progressDialog.hide();

                        }
                    }
                });

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            progressDialog.setMessage("Signing in, Please Wait...");
            progressDialog.show();

            if (result.isSuccess()) {
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = result.getSignInAccount();
            firebaseAuthWithGoogle(account);
            } else {

                // Google Sign In failed, update UI appropriately
                // ...
                progressDialog.dismiss();
                Toast.makeText(SignIn_Login.this, "You Got an Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());


        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            Log.d(TAG, "signInWithCredential:success");
                            checkUserExist();
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignIn_Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                        // ...
                        progressDialog.dismiss();


                    }
                });
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    private void checkUserExist() {

        if (mAuth.getCurrentUser() != null) {

            final String user_id = mAuth.getCurrentUser().getUid();

            databaseBooker.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild(user_id)){

                        Intent cust=new Intent(SignIn_Login.this,CustActivity.class);
                        cust.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(cust);

                    } else{

                        databaseCooker.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if(dataSnapshot.hasChild(user_id)){
                                    Intent cook=new Intent(SignIn_Login.this,CookActivity.class);
                                    cook.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(cook);

                                }else {
                                    Intent pro=new Intent(SignIn_Login.this,Profile_Info.class);
                                    Toast.makeText(SignIn_Login.this, "You need to setup your account", Toast.LENGTH_SHORT).show();
                                    pro.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(pro);
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }


}

