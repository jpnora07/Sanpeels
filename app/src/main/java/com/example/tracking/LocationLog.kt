package com.example.tracking

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class LocationLog(
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var nameInLoc: String?=null
)