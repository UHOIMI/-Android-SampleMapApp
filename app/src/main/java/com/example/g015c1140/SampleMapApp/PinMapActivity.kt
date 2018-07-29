package com.example.g015c1140.SampleMapApp

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Double
import java.util.ArrayList

class PinMapActivity : AppCompatActivity(), GoogleMap.OnMarkerClickListener, OnMapReadyCallback {

    private var mBrisbane: Marker? = null
    private var mMap: GoogleMap? = null

    var GETPOINT = LatLng(0.0,0.0)

    var newSpotList = arrayListOf<SpotData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_map)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        newSpotList = intent.getSerializableExtra("SPOTDATA") as ArrayList<SpotData>


        /*if (mMap == null){
            Log.d("mMapチェッカー","ぬる")
        }

        for (_spot in newSpotList){
            Log.d("latitudeチェッカー", _spot.latitude.toString())
            GETPOINT = LatLng(Double.parseDouble(_spot.latitude.toString()), Double.parseDouble(_spot.longitude.toString()))
            if (GETPOINT == null) {
                Log.d("GETPOINTチェッカー", "ぬる")
            }
            mMap!!.addMarker(MarkerOptions()
                    .position(GETPOINT)
                    .title(_spot.name))
            mBrisbane!!.tag = 0
        }

        cm = CameraPosition.Builder()
                .target(GETPOINT)      // Sets the center of the map to Mountain View
                .zoom(17f)                   // Sets the zoom
                .bearing(0f)                // Sets the orientation of the camera to east
                .tilt(0f)                   // Sets the tilt of the camera to 30 degrees
                .build()

        if (GETPOINT == null){
            Log.d("GETPOINTチェッカー","ぬる")
        }
        if (cm == null){
            Log.d("cmチェッカー","ぬる")
        }
        mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cm))*/
    }

    /** Called when the map is ready.  */
    override fun onMapReady(map: GoogleMap) {
        mMap = map

        mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cm))

        // Add some markers to the map, and add a data object to each marker.

        /*mBrisbane = mMap!!.addMarker(MarkerOptions()
                .position(SHCOOL)
                .title("Shcool"))
        mBrisbane!!.tag = 0*/

        for (_spot in newSpotList){
            Log.d("latitudeチェッカー", _spot.latitude.toString())
            GETPOINT = LatLng(Double.parseDouble(_spot.latitude.toString()), Double.parseDouble(_spot.longitude.toString()))
            if (GETPOINT == null) {
                Log.d("GETPOINTチェッカー", "ぬる")
            }
            mBrisbane = mMap!!.addMarker(MarkerOptions()
                    .position(GETPOINT)
                    .title(_spot.name))
            mBrisbane!!.tag = 0
        }

        cm = CameraPosition.Builder()
                .target(GETPOINT)      // Sets the center of the map to Mountain View
                .zoom(17f)                   // Sets the zoom
                .bearing(0f)                // Sets the orientation of the camera to east
                .tilt(0f)                   // Sets the tilt of the camera to 30 degrees
                .build()

        mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cm))

        // Set a listener for marker click.
        mMap!!.setOnMarkerClickListener(this)
    }

    /** Called when the user clicks a marker.  */
    override fun onMarkerClick(marker: Marker): Boolean {

        // Retrieve the data from the marker.
        var clickCount = marker.tag as Int?

        // Check if a click count was set, then display the click count.
        if (clickCount != null) {
            clickCount = clickCount + 1
            marker.tag = clickCount
            Toast.makeText(this,
                    marker.title +
                            " has been clicked " + clickCount + " times.",
                    Toast.LENGTH_SHORT).show()
        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (resultCode == Activity.RESULT_OK && requestCode == RESULT_SUBACTIVITY && null != intent) {
            Log.d("エックス", intent.getStringExtra("LatLngX"))
            Log.d("ワイ", intent.getStringExtra("LatLngY"))
            Log.d("名前", intent.getStringExtra("NAME"))
            val GETPOINT = LatLng(Double.parseDouble(intent.getStringExtra("LatLngX")), Double.parseDouble(intent.getStringExtra("LatLngY")))
            mMap!!.addMarker(MarkerOptions()
                    .position(GETPOINT)
                    .title(intent.getStringExtra("NAME")))
            mBrisbane!!.tag = 0

            cm = CameraPosition.Builder()
                    .target(GETPOINT)      // Sets the center of the map to Mountain View
                    .zoom(15f)                   // Sets the zoom
                    .bearing(0f)                // Sets the orientation of the camera to east
                    .tilt(0f)                   // Sets the tilt of the camera to 30 degrees
                    .build()
            mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cm))
        }
    }

    companion object {

        private val SHCOOL = LatLng(35.625157, 139.342121)
        private var cm = CameraPosition.Builder()
                .target(SHCOOL)      // Sets the center of the map to Mountain View
                .zoom(17f)                   // Sets the zoom
                .bearing(0f)                // Sets the orientation of the camera to east
                .tilt(0f)                   // Sets the tilt of the camera to 30 degrees
                .build()

        internal val RESULT_SUBACTIVITY = 1000
    }
}
