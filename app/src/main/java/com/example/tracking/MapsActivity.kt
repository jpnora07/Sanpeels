package com.example.tracking

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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
import java.io.IOException
import java.util.*

@Suppress("DEPRECATION")
class MapsActivity : AppCompatActivity(), com.google.android.gms.maps.OnMapReadyCallback {

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

        val userbtn = findViewById<ImageButton>(R.id.userloc)
        userbtn.setOnClickListener {
            mapFragment.getMapAsync {
                getCurrentLocation()
            }
        }

        setupLocClient()
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
        reference.orderByChild("id").equalTo("2")
            .addValueEventListener(object : ValueEventListener, OnMapReadyCallback,
                com.google.android.gms.maps.OnMapReadyCallback {
                @SuppressLint("SuspiciousIndentation")
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()) {
                        val driverbtn = findViewById<Button>(R.id.directions)
                        driverbtn.setOnClickListener {
                            mapFragment.getMapAsync {
                                for (addressSnapshot in snapshot.children) {
                                    val customer =
                                        addressSnapshot.child("userlocation")
                                            .getValue(LocationLog::class.java)
                                    val latitudeCust = customer?.latitude
                                    val longitudeCust = customer?.longitude
                                    val latLngCust = LatLng(latitudeCust!!, longitudeCust!!)

                                    locationArrayList!!.add(latLngCust)
                                    for (i in locationArrayList!!.indices) {

                                        mMap = it
                                        mMap.addMarker(
                                            MarkerOptions()
                                                .position(
                                                    locationArrayList!![i]
                                                )
                                                .title("Collector")
                                                .snippet(getAddress(latLngCust))
                                        )
                                            ?.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.truckicon))
                                        val update =
                                            CameraUpdateFactory.newLatLngZoom(latLngCust, 15f)
                                        mMap.animateCamera(update)
                                        mMap.uiSettings.isMapToolbarEnabled = true
                                        mMap.uiSettings.isZoomControlsEnabled = true
                                        mMap.moveCamera(
                                            CameraUpdateFactory.newLatLng(
                                                locationArrayList!![i]
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                private fun getAddress(lat: LatLng): String? {
                    val geocoder = Geocoder(this@MapsActivity)
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

            database = FirebaseDatabase.getInstance()
            reference = database.getReference("Profile")
            fusedLocClient.lastLocation.addOnCompleteListener {
                // lastLocation is a task running in the background
                val location = it.result //obtain location
                //reference to the database
                val user = FirebaseAuth.getInstance().currentUser
                val userid = user!!.uid
                val latLng = LatLng(location.latitude, location.longitude)
                reference.child(userid).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val fname = dataSnapshot.child("Firstname").value as String?
                        val lname = dataSnapshot.child("Lastname").value as String?
                        val fullname = fname + " " + lname
                        val words = fullname.split(" ").toMutableList()
                        var outputName = ""
                        for (word in words) {
                            outputName += word.capitalize() + " "
                        }
                        outputName = outputName.trim()

                        val userLocation = reference.child(userid).child("userlocation")
                        userLocation.setValue(latLng)
                        userLocation.child("nameInLoc").setValue(outputName)
                            .addOnSuccessListener {
                                val toast = Toast.makeText(
                                    applicationContext,
                                    " Locate your rider ",
                                    Toast.LENGTH_SHORT
                                )
                                toast.setGravity(Gravity.TOP, 0, 100)
                                toast.show()
                                mMap.addMarker(
                                    MarkerOptions().position(latLng).title("You are here")
                                )
                                    ?.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.user_icon))

                                val update = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
                                mMap.animateCamera(update)
                                mMap.uiSettings.isMapToolbarEnabled = true

                            }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
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
        private const val TAG = "MapsActivity"
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