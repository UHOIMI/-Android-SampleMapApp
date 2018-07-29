package com.example.g015c1140.SampleMapApp

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast

import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException


class PlacePicker : AppCompatActivity() {

    private val PLACE_PICKER_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_select_spot)

        val builder = com.google.android.gms.location.places.ui.PlacePicker.IntentBuilder()

        val context = applicationContext
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST)
        } catch (e: GooglePlayServicesRepairableException) {
            Log.d("PickerTest", e.toString())
        } catch (e: GooglePlayServicesNotAvailableException) {
            Log.d("PickerTest", e.toString())
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                val place = com.google.android.gms.location.places.ui.PlacePicker.getPlace(data, this)
                /*String toastMsg = String.format( "Place: %s/%s", place.getName(), place.getAddress() );
                Toast.makeText( this, toastMsg, Toast.LENGTH_LONG ).show();*/
                val intent = Intent(application, SelectSpotActivity::class.java)
                var LatLng = place.latLng.toString().substring(9)
                LatLng = LatLng.replace("[()]".toRegex(), "")
                val LatLngArray = LatLng.split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                intent.putExtra("LatLngX", LatLngArray[0])
                intent.putExtra("LatLngY", LatLngArray[1])
                intent.putExtra("NAME", place.name)
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else {
                Toast.makeText(this, "失敗:$requestCode", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onBackPressed() {
        // 何もしない
    }
}