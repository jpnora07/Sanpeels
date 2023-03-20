package com.example.tracking

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.tracking.Model.DeliveryDetails
import com.example.tracking.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.maps.OnMapReadyCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*


class MapActivity_Rider : AppCompatActivity(), com.google.android.gms.maps.OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    lateinit var reference: DatabaseReference
    lateinit var database: FirebaseDatabase
    lateinit var geocoder: Geocoder
    lateinit var mapFragment: SupportMapFragment
    private var locationArrayList: ArrayList<LatLng>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        geocoder = Geocoder(this, Locale.getDefault())

        setupLocClient()

        val riderbtn = findViewById<ImageButton>(R.id.userloc)
        riderbtn.setOnClickListener {
            mapFragment.getMapAsync {
                getCurrentLocation()
            }
        }
    }


    private lateinit var fusedLocClient: FusedLocationProviderClient

    private fun setupLocClient() {
        fusedLocClient =
            LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        getCurrentLocation()
        getLocationRider()

    }

    private fun getLocationRider() {
        locationArrayList = ArrayList()
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("Profile")
        reference.orderByChild("id").equalTo("1")
            .addValueEventListener(object : ValueEventListener, OnMapReadyCallback,
                com.google.android.gms.maps.OnMapReadyCallback {
                @SuppressLint("SuspiciousIndentation")
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()) {
                        val rider = FirebaseAuth.getInstance().currentUser
                        val riderid = rider!!.uid
                        val customerbtn = findViewById<Button>(R.id.directions)
                        val key = reference.push().key
                        val riderRef =
                            reference.child(riderid).child("Arrival_of_Collector").child("Distance")
                                .child(key!!)
                        //need selected customer location

                        customerbtn.setOnClickListener {
                            mapFragment.getMapAsync { map ->
                                for (addressSnapshot in snapshot.children) {
                                    val customer = addressSnapshot.child("userlocation").getValue(LocationLog::class.java)
                                    if (customer != null) {
                                        val latitudeCust = customer.latitude
                                        val longitudeCust = customer.longitude
                                        val latLngCust = LatLng(latitudeCust!!, longitudeCust!!)

                                        locationArrayList!!.add(latLngCust)
                                        for (i in locationArrayList!!.indices) {
                                            mMap = map
                                            mMap.addMarker(
                                                MarkerOptions()
                                                    .position(locationArrayList!![i])
                                                    .title(customer.nameInLoc)
                                                    .snippet(getAddress(latLngCust))
                                            )
                                                ?.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.user_icon))
                                            val update = CameraUpdateFactory.newLatLngZoom(latLngCust, 20.0f)
                                            mMap.animateCamera(update)
                                            mMap.uiSettings.isMapToolbarEnabled = true
                                            mMap.uiSettings.isZoomControlsEnabled = true
                                            mMap.moveCamera(CameraUpdateFactory.newLatLng(locationArrayList!![i]))
                                            riderRef.child("timestamp").setValue(ServerValue.TIMESTAMP)
                                        }
                                    } else {
                                        // Handle the case where the location is null
                                        Log.w("MapActivity", "Skipping null location")
                                    }
                                }
                            }
                        }

                    }
                }

                private fun getAddress(lat: LatLng): String? {
                    val geocoder = Geocoder(this@MapActivity_Rider)
                    val list = geocoder.getFromLocation(lat.latitude, lat.longitude, 1)
                    return list?.get(0)?.getAddressLine(0)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        baseContext,
                        "Something wrong happened in your database!",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }

                override fun onMapReady(p0: GoogleMap) {
                    getCurrentLocation()
                }

                override fun onMapReady(p0: com.google.android.libraries.maps.GoogleMap?) {
                    getCurrentLocation()
                }
            })
    }

    private fun getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // call requestLocPermissions() if permission isn't granted
            requestLocPermissions()
        } else {

            //database = FirebaseDatabase.getInstance()
            //reference = database.getReference("Profile")
            fusedLocClient.lastLocation.addOnCompleteListener {
                // lastLocation is a task running in the background
                val location = it.result //obtain location
                //reference to the database
                val rider = FirebaseAuth.getInstance().currentUser
                val riderid = rider!!.uid
                val latLng = LatLng(location.latitude, location.longitude)
                reference.child(riderid).child("userlocation").setValue(latLng)
                    .addOnSuccessListener {
                        val toast = Toast.makeText(
                            applicationContext,
                            " You are here! ",
                            Toast.LENGTH_SHORT
                        )
                        toast.setGravity(Gravity.TOP, 0, 100)
                        toast.show()
                        mMap.addMarker(MarkerOptions().position(latLng).title("You are here"))
                            ?.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.truckicon))

                        val update = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
                        mMap.animateCamera(update)
                        mMap.uiSettings.isMapToolbarEnabled = false

                    }
            }
        }
    }

    private fun requestLocPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(ACCESS_FINE_LOCATION), //permission in the manifest
            REQUEST_LOCATION
        )
    }

    companion object {
        private const val REQUEST_LOCATION =
            1 //request code to identify specific permission request
        private const val TAG = "MapActivity_Rider"
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //check if the request code matches the REQUEST_LOCATION
        if (requestCode == REQUEST_LOCATION) {
            //check if grantResults contains PERMISSION_GRANTED.If it does, call getCurrentLocation()
            if (grantResults.size == 1 && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED
            ) {
                getCurrentLocation()
            } else {
                //if it doesn`t log an error message
                Log.e(TAG, "Location permission has been denied")
            }
        }
    }

}