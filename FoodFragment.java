package com.example.lenovo.bdfoodcart;




import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.TextView;
import android.widget.Toast;


import com.example.lenovo.bdfoodcart.Interface.ItemClickListener;
import com.example.lenovo.bdfoodcart.Model.Food;

import com.example.lenovo.bdfoodcart.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class FoodFragment extends Fragment {


    FragmentActivity context;
    DatabaseReference food;
    private static final String TAG = "Food Fragment";

    RecyclerView allFood;
    RecyclerView.LayoutManager layoutManager;

    public FoodFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myview=inflater.inflate(R.layout.fragment_food, container, false);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        food=FirebaseDatabase.getInstance().getReference("Food").child(uid);
        Log.e(TAG, "foooRef" + food);

        context=getActivity();

        allFood=(RecyclerView)myview.findViewById(R.id.allFood);
        allFood.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(context);
        allFood.setLayoutManager(layoutManager);
        loadMenu();

        return  myview;


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu,menu);
        return;



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_idd){

            AddFoodFragment foodFragment=new AddFoodFragment();
            FragmentTransaction transaction=getFragmentManager().beginTransaction();
            transaction.replace(R.id.relative,foodFragment);
            transaction.addToBackStack(null);
            transaction.commit();

        }

        return super.onOptionsItemSelected(item);
    }



    private void loadMenu() {



        FirebaseRecyclerAdapter<Food,MenuViewHolder> adapter=new FirebaseRecyclerAdapter<Food,MenuViewHolder>(Food.class,R.layout.menu_item,MenuViewHolder.class,food){


            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, Food model, int position) {

                viewHolder.txtMenuName.setText(model.getFoodName());
                Picasso.with(getActivity()).load(model.getFoodImage()).into(viewHolder.imageView);

                final Food clickItem=model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(getActivity(),""+clickItem.getFoodName()
                                , Toast.LENGTH_SHORT).show();
                    }
                });

            }
        };

        allFood.setAdapter(adapter);
    }


}
