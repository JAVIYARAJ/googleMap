package com.example.googlemap

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private final var STATUS_CODE = 201;
    var manager = supportFragmentManager;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val KEY = "access_fine_permission";

        var sharedPreferences = getSharedPreferences("google_map_permission", MODE_PRIVATE);
        var editor = sharedPreferences.edit()

        //access fine location use to get precise location
        //access coarse location use to get approximate location

        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager;

        var isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        Toast.makeText(applicationContext, "${isGpsEnabled.toString()}", Toast.LENGTH_SHORT).show()
        var isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isGpsEnabled && isNetworkEnabled) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                //denied by user and not check don't ask again checkbox
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Toast.makeText(
                        applicationContext,
                        "call denied by user and not check don't ask again checkbox",
                        Toast.LENGTH_SHORT
                    ).show()
                    editor.putBoolean(KEY, true);
                    editor.commit();
                    requestPermissions(
                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), STATUS_CODE
                    )
                } else {
                    //first time permission pop up show
                    Toast.makeText(
                        applicationContext, "second use case call", Toast.LENGTH_SHORT
                    ).show()
                    if (sharedPreferences.getBoolean(KEY, true)) {
                        Toast.makeText(
                            applicationContext,
                            "first time permission pop up show",
                            Toast.LENGTH_SHORT
                        ).show()
                        editor.putBoolean(KEY, false).commit();
                        requestPermissions(
                            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), STATUS_CODE
                        )
                    } else {
                        Toast.makeText(
                            applicationContext, "setting call", Toast.LENGTH_SHORT
                        ).show()
                        var alert = AlertDialog.Builder(this);
                        alert.setMessage("Please allow location permission from setting to use this app");
                        alert.setTitle("Permission")
                        alert.setIcon(R.drawable.ic_lock_icon);
                        alert.setCancelable(false);
                        alert.setPositiveButton(
                            "Yes"
                        ) { _, _ ->
                            manager.beginTransaction().add(R.id.mapContainer, ErrorFragment())
                                .commit()
                        };
                        alert.setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                            finish()
                        }
                        alert.create().show();
                    }
                }
            } else {
                manager.beginTransaction().add(R.id.mapContainer, GoogleMapFragment()).commit()
            }
        } else {
            Toast.makeText(applicationContext, "Please turn on internet", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STATUS_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            manager.beginTransaction().add(R.id.mapContainer, GoogleMapFragment()).commit()

        } else {
            Log.d("Practice", "Permission denied");
        }
    }
}