package com.example.googlemap

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class GoogleMapFragment : Fragment() {

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient;

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_google_map, container, false)

        var supportMapFragment =
            childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment;



        supportMapFragment.getMapAsync { map ->

            map.setOnMapClickListener {
                var latLng = LatLng(it.latitude, it.longitude)
                var marker = MarkerOptions();
                marker.position(latLng);
                marker.title("(${it.latitude} ${it.longitude})")
                map.addMarker(marker);
            }

            //this method consume more battery because map access all time location in background
            map.isMyLocationEnabled = true


            //this method is optimised and low battery consuming
            fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireContext().applicationContext);

            fusedLocationProviderClient.lastLocation.addOnCompleteListener {
                if (it.isSuccessful) {
                    var location = it.result

                    if (location != null) {
                        var latLng = LatLng(location.latitude, location.longitude)
                        var marker = MarkerOptions();
                        marker.position(latLng);
                        map.addMarker(marker);
                        var cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 25F);
                        map.animateCamera(cameraUpdate)
                    } else {
                        Toast.makeText(
                            requireContext().applicationContext,
                            "Please try again",
                            Toast.LENGTH_SHORT
                        ).show();
                        childFragmentManager.beginTransaction()
                            .replace(R.id.mapContainer, ErrorFragment()).commit();
                    }

                } else {
                    Toast.makeText(
                        requireContext().applicationContext,
                        "permission is not allowed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        return view;
    }

}