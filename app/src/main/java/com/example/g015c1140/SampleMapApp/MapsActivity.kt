package com.example.g015c1140.SampleMapApp

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONArray
import org.json.JSONException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener{

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location

    //data class spotData(val name : String, val latitude : Double, val longitude : Double)
    //val spotList: MutableList<SpotData> = mutableListOf()
    val spotList = arrayListOf<SpotData>()
    var spotName : EditText? = null

    val RESULT_SUBACTIVITY = 1000

    var JsonArray = JSONArray()

    //private lateinit var mLatTextView: TextView
    //private lateinit var mLongTextView: TextView

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("test","onCreate")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        spotName = findViewById(R.id.nameText) as EditText

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //mLatTextView = findViewById(R.id.latitudeTextView) as TextView
        //mLongTextView = findViewById(R.id.longitudeTextView) as TextView

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val sendButton = findViewById<Button>(R.id.SelectButton)
        sendButton.setOnClickListener {
            val intent = Intent(application, SelectSpotActivity::class.java)
            intent.putExtra("SPOTDATA", spotList)
            val requestCode = 1000
            startActivityForResult(intent, requestCode)
        }

    }

    // マーカーをタップすると呼び出される
    override fun onMarkerClick(p0: Marker?) = false

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    fun PinButtonTapped(view: View){
        var lm = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if(!gpsEnabled){
            AlertDialog.Builder(this).apply {
                setTitle("位置情報が有効になっていません")
                setMessage("このままアプリを続行したい場合は、有効化してください")
                setPositiveButton("設定", { _, _ ->
                    // OKをタップしたときの処理
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                })
                setNegativeButton("戻る", null)
                show()
            }
            return
        }
        setUpMap()
    }


    override fun onMapReady(googleMap: GoogleMap) {
        //　位置情報権限確認
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //　権限がない場合
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            mMap = googleMap
            return
        }
        mMap = googleMap

        // ピンのクリックリスナー
        mMap.setOnMarkerClickListener(this)
        //　現在位置マーカーと現在位置ボタン有効化
        mMap.isMyLocationEnabled = true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        // 自分のコード以外がrequestPermissionsしているかもしれないので、requestCodeをチェックします。
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }
        onMapReady(mMap)
    }


    private fun setUpMap() {
        Log.d("test", "setUpMap")

        //　位置情報権限確認
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //　権限がない場合
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            //最後に既知の場所を取得します。 まれな状況では、これはヌルになる可能性があります。
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                /****/
                //mLatTextView.text = locationlatitude.toString()
                //mLongTextView.text = location.longitude.toString()
                /****/

                spotList.add(SpotData(spotName!!.getText().toString(),location.latitude,location.longitude, Date()))

                Log.d("テスト", spotList.get(0).name)

                placeMarkerOnMap(currentLatLng)

                var cameraPosition: CameraPosition = CameraPosition.Builder()
                .target(currentLatLng)
                        .zoom(15f)
                        .build()
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

            }else{
                Log.d("ヌルチェッカー","null")
            }
        }
    }

    //　現在位置にマーカー設置
    private fun placeMarkerOnMap(location: LatLng) {
        Log.d("test", "place marker")
        mMap.clear()
        //　マーカー作成
        val markerOptions = MarkerOptions().position(location)

        //　文字列設定
        val titleStr = getAddress(location)
        Log.d("test", "アドレス")
        Log.d("test",titleStr)

        markerOptions.title(titleStr)

        //　マーカー設置
        mMap.addMarker(markerOptions)
    }

    //　住所表示
    private fun getAddress(latLng: LatLng): String {

        Log.d("test", "getAddress")

        val geocoder = Geocoder(this)
        val addresses: List<Address>?
        val address: Address?
        var addressText = ""

        try {
            //　アドレス取得
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (null != addresses && !addresses.isEmpty()) {
                address = addresses[0]
                for (i in 0 .. address.maxAddressLineIndex) {
                    addressText += if (i == 0) address.getAddressLine(i).toString() else "\n" + address.getAddressLine(i).toString()
                }
            }
        } catch (e: IOException) {
            Log.d("test","アドレスエラー")
            Log.e("MapsActivity", e.localizedMessage)
        }

        return addressText
    }

    inner class GetAllMemoTask: AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String?): String? {
            var connection: HttpURLConnection? = null
            var reader: BufferedReader? = null

            try {
                val url = URL("http://172.16.89.158:3000/api/v1/spot/find?user_id=1")
                connection = url.openConnection() as HttpURLConnection
                connection.connect()

                val br = BufferedReader(InputStreamReader(connection.inputStream))
                val sb = StringBuilder()
                for (line in br.readLines()) {
                    line.run { sb.append(line) }
                }

                val titleArrayList = ArrayList<String>()

                try {
                    JsonArray = JSONArray(sb.toString())
                    for (i in 0 until JsonArray.length()) {
                        println("array.getJSONObject(i):${JsonArray.getJSONObject(i)}")
                        titleArrayList.add(JsonArray.getJSONObject(i).getString("title"))
                    }
                } catch (e: JSONException) {
                    println("error")
                }
                br.close()
                return titleArrayList.toString()

            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            finally {
                connection?.disconnect()
                try {
                    reader?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return null
        }

        /*返ってきたデータをビューに反映させる処理はonPostExecuteに書きます。これはメインスレッドです。
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result == null) return

            val titleList = JSONArray(result)

            println("titleList${titleList}")
            arrayAdapter.clear()
            for (i in 0 until titleList.length()) {
                arrayAdapter.insert(titleList[i] as String?, i)
            }
            arrayAdapter.notifyDataSetChanged()
        }*/
    }
}
