package com.example.woga1.navigation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class AddressSearchActivity extends AppCompatActivity implements PlaceSelectionListener{
    private static final String LOG_TAG = "PlaceSelectionListener";
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));
    private static final int REQUEST_SELECT_PLACE = 1000;
    private TextView locationTextView;
    private TextView attributionsTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_search);
//        Toolbar toolbar = new Toolbar(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        locationTextView = (TextView) findViewById(R.id.txt_location);
//        attributionsTextView = (TextView) findViewById(R.id.txt_attributions);
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder
                    (PlaceAutocomplete.MODE_FULLSCREEN)
                    .setBoundsBias(BOUNDS_MOUNTAIN_VIEW)
                    .build(AddressSearchActivity.this);
            startActivityForResult(intent, REQUEST_SELECT_PLACE);
        } catch (GooglePlayServicesRepairableException |
                GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Method #2
//                try {
//                    Intent intent = new PlaceAutocomplete.IntentBuilder
//                            (PlaceAutocomplete.MODE_FULLSCREEN)
//                            .setBoundsBias(BOUNDS_MOUNTAIN_VIEW)
//                            .build(AddressSearchActivity.this);
//                    startActivityForResult(intent, REQUEST_SELECT_PLACE);
//                } catch (GooglePlayServicesRepairableException |
//                        GooglePlayServicesNotAvailableException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }
    @Override
    public void onPlaceSelected(Place place) {
//        Log.i(LOG_TAG, "Place Selected: " + place.getName());
//        Toast.makeText(getApplicationContext(),place.getName(),Toast.LENGTH_SHORT).show();
//        Toast.makeText(getApplicationContext(),place.getAddress(),Toast.LENGTH_SHORT).show();
//        Toast.makeText(getApplicationContext(),place.getPhoneNumber(),Toast.LENGTH_SHORT).show();
//        Toast.makeText(getApplicationContext(),place.getPhoneNumber(),Toast.LENGTH_SHORT).show();
//        Toast.makeText(getApplicationContext(),place.getId(),Toast.LENGTH_SHORT).show();
//        locationTextView.setText(getString(R.string.formatted_place_data, place
//                .getName(), place.getAddress(), place.getPhoneNumber(), place
//                .getWebsiteUri(), place.getRating(), place.getId()));
        if (!TextUtils.isEmpty(place.getAttributions())){
            attributionsTextView.setText(Html.fromHtml(place.getAttributions().toString()));
        }
    }

    @Override
    public void onError(Status status) {
        Log.e(LOG_TAG, "onError: Status = " + status.toString());
        Toast.makeText(this, "Place selection failed: " + status.getStatusMessage(),
                Toast.LENGTH_SHORT).show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_PLACE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                this.onPlaceSelected(place);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                this.onError(status);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
