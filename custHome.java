package com.example.lenovo.bdfoodcart;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.lenovo.bdfoodcart.Model.User;
import com.example.lenovo.bdfoodcart.Model.UserListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class custHome extends Fragment {


    FragmentActivity fm;


    DatabaseReference databaseUsers;

    // a list to store all the artists from firebase database
    List<customer_list> userList;
    ListView usersListView;

    @Override
    public void onStart() {
        super.onStart();


        //attaching value event listener
        databaseUsers.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userList.clear(); //clearing the previous list


                //iterating through all the nodes
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    //getting artist
                    String nm=snapshot.child("userName").getValue().toString();
                    String ph=snapshot.child("userPhn").getValue().toString();

                    customer_list user=new customer_list(nm,ph);



                    //adding artist to the list
                    userList.add(user);

                }

                fm=getActivity();


                //creating adapter
                UserListAdapter userAdapter= new UserListAdapter(fm, userList);

                //set the adapter to the list view
                usersListView.setAdapter(userAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_cust_home, container, false);




        //Toast.makeText(getActivity(),"Cust Home.",Toast.LENGTH_SHORT).show();

        databaseUsers = FirebaseDatabase.getInstance().getReference("cooker");
        usersListView = (ListView)v.findViewById(R.id.listViewUsers);
        userList = new ArrayList<>();

        return  v;
    }
}
