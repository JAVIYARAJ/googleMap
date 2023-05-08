package com.example.googlemap

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline

import com.directions.route.Routing
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

@Suppress("DEPRECATION")
class RoutingGoogleApp : AppCompatActivity(), RoutingListener {

    private var isPermissionGranted: Boolean? = false;

    private var myLocation: Location? = null;

    private lateinit var googleMap: GoogleMap;

    private var startLatLng: LatLng? = null
    private var endLatLng: LatLng? = null

    private var polyLineList: ArrayList<Polyline>? = null;

    private var PERMISSION_STATUS_CODE = 101;
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_routing_google_app)

        var supportMapFragment =
            supportFragmentManager.findFragmentById(R.id.googleMapFragment) as SupportMapFragment;

        requestLocationPermission();

        supportMapFragment.getMapAsync {
            googleMap = it;
            if (isPermissionGranted == true) {
                getMyLocation();
            }
        }


    }

    private fun requestLocationPermission() {
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_STATUS_CODE
            );
        } else {
            isPermissionGranted = true;
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_STATUS_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            isPermissionGranted = true;
            getMyLocation();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @SuppressLint("MissingPermission")
    private fun getMyLocation() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        googleMap.isMyLocationEnabled = true;

        fusedLocationProviderClient.lastLocation.addOnCompleteListener {
            if (it.isSuccessful) {

                myLocation = it.result;

                var camera = CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        it.result.latitude, it.result.longitude
                    ), 15F
                );
                googleMap.animateCamera(camera);
            }
        }

        googleMap.setOnMapClickListener {


            //clear map because we want to draw many routes that's
            googleMap.clear();

            startLatLng = LatLng(myLocation!!.latitude, myLocation!!.longitude);

            endLatLng = it;

            //find routes
            findRoutes(startLatLng!!, endLatLng!!);
        }
    }

    private fun findRoutes(startLatLng: LatLng, endLatLng: LatLng) {
        if (startLatLng == null || endLatLng == null) {
            Toast.makeText(this, "Unable to get location", Toast.LENGTH_LONG).show();
        } else {
            var routing = Routing.Builder().alternativeRoutes(true)
                .travelMode(AbstractRouting.TravelMode.DRIVING).waypoints(startLatLng, endLatLng)
                .key("").withListener(this).build()
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    override fun onRoutingFailure(p0: RouteException?) {
        findRoutes(startLatLng!!, endLatLng!!)
    }

    override fun onRoutingStart() {
        Toast.makeText(this, "Finding Route...", Toast.LENGTH_LONG).show();
    }

    override fun onRoutingSuccess(routes: java.util.ArrayList<Route>?, shortestRouteIndex: Int) {

        var ployStartLatLng: LatLng? = null;
        var polyEndLatLng: LatLng? = null;

        //use for map to know about poly line points and its other info for draw poly line in map
        var polyOption = PolylineOptions();

        polyLineList = ArrayList<Polyline>();


        //clear previous path coordinates
        if (polyLineList != null) {
            polyLineList!!.clear()
        } else {

            for (i in 0 until polyLineList!!.size) {

                //specify poly line info like line width,color,points etc.

                polyOption.width(10F);
                polyOption.color(resources.getColor(R.color.purple_200));

                //get all points which is shortest path and add into poly option
                polyOption.addAll(routes!![shortestRouteIndex].points);

                //poly line is collections of points that are use for map to draw line (poly line is not closed loop but if you want, then specify start and end points same)

                var polyline: Polyline = googleMap.addPolyline(polyOption);

                //get start points of polyline
                ployStartLatLng = polyline.points[0];
                var size = polyline.points.size;


                //get end points of polyline
                polyEndLatLng = polyline.points[size - 1];


                polyLineList!!.add(polyline);
            }

            var startMarker = MarkerOptions();
            startMarker.position(ployStartLatLng!!);
            startMarker.title("My Location")
            googleMap.addMarker(startMarker);

            var endMarker = MarkerOptions();
            endMarker.position(polyEndLatLng!!);
            endMarker.title("Destination")
            googleMap.addMarker(endMarker);
        }
    }

    override fun onRoutingCancelled() {
        findRoutes(startLatLng!!, endLatLng!!)
    }
}