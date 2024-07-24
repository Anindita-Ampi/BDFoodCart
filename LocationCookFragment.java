package com.example.lenovo.bdfoodcart;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class LocationCookFragment extends Fragment {

    TextView placeNameText;
    TextView placeAddressText;
    WebView attributionText;
    Button getPlaceButton;
    private final static int MY_PERMISSION_FINE_LOCATION=101;
    private final static int PLACE_PICKER_REQUEST=1;
    View myview;




    public LocationCookFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myview=inflater.inflate(R.layout.fragment_location_cook, container, false);
        requestPermission();

        placeNameText = (TextView) myview.findViewById(R.id.tvPlaceName);
        placeAddressText = (TextView) myview.findViewById(R.id.tvPlaceAddress);
        attributionText = (WebView) myview.findViewById(R.id.wvAttribution);
        getPlaceButton = (Button) myview.findViewById(R.id.btGetPlace);

        getPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {

                    Intent intent = builder.build(getActivity());
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                }catch (GooglePlayServicesRepairableException e){
                    e.printStackTrace();
                }catch (GooglePlayServicesNotAvailableException e){
                    e.printStackTrace();
                }

            }
        });

        return myview;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==PLACE_PICKER_REQUEST){
            if (resultCode==RESULT_OK){
                Place place=PlacePicker.getPlace(getContext(),data);
                placeNameText.setText(place.getName());
                placeAddressText.setText(place.getAddress());
                if (place.getAttributions()==null){
                    attributionText.loadData("no attribution","text/html; charset=utf-8","UTF-8");
                }
                else {
                    attributionText.loadData(place.getAttributions().toString(),"text/html;charset=utf-8","UTF-8");
                }
            }
        }
    }

    private void requestPermission() {

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION )!= PackageManager.PERMISSION_GRANTED){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSION_FINE_LOCATION);
            }}
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case MY_PERMISSION_FINE_LOCATION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED){

                    Toast.makeText(getContext(), "requires location permission to be granted", Toast.LENGTH_LONG).show();

                }
                break;
        }

    }
}
