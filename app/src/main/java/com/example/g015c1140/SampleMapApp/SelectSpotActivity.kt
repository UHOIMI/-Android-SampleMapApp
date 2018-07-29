package com.example.g015c1140.SampleMapApp

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import java.util.*
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.AdapterView.OnItemSelectedListener
import java.lang.Double.parseDouble
import java.text.SimpleDateFormat


//import sun.util.locale.provider.LocaleProviderAdapter.getAdapter

class SelectSpotActivity : AppCompatActivity() {

    var spotList = arrayListOf<SpotData>()
    var newSpotList = arrayListOf<SpotData>()
    val spotNameList = ArrayList<String>()
    val newSpotNameList = ArrayList<String>()
    lateinit var userSpotList : ListView
    lateinit var selectSpotList : ListView
    var nowSort = "昇順"
    val RESULT_SUBACTIVITY = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_spot)

        spotList = intent.getSerializableExtra("SPOTDATA") as ArrayList<SpotData>
        val spinner = findViewById(R.id.sort) as Spinner;

        val df = SimpleDateFormat("yyyy/MM/dd ")

        for(_spotList in spotList) {
            spotNameList.add(df.format(_spotList.dateTime) + "：" + _spotList.name)
        }

        val userSpotAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, spotNameList)
        userSpotList = findViewById(R.id.userSpotList) as ListView
        userSpotList.setAdapter(userSpotAdapter)

        val selectSpotAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, newSpotNameList)
        selectSpotList = findViewById(R.id.selectSpotList) as ListView
        selectSpotList.setAdapter(selectSpotAdapter)

        userSpotList.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val msg = (position + 1).toString() + "番目のアイテムが追加されました"
                Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()
                val list = parent as ListView
                val item = list.getItemAtPosition(position) as String
                val adapter = list.adapter as ArrayAdapter<String>
                selectSpotAdapter.add(userSpotAdapter.getItem(position))
                newSpotList.add(spotList.get(position))
                selectSpotList.setAdapter(selectSpotAdapter);
                adapter.remove(item);
                spotList.removeAt(position)
            }
        })

        // リスナーを登録
        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            //　アイテムが選択された時
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                val spinner = parent as Spinner
                val item = spinner.selectedItem as String
                if(!nowSort.equals(item)){
                    sortList()
                    nowSort = item
                }
            }

            //　アイテムが選択されなかった
            override fun onNothingSelected(parent: AdapterView<*>) {
                //
            }
        }

        val searchButton = findViewById<Button>(R.id.searchButton)
        searchButton.setOnClickListener {
            val intent = Intent(application, PlacePicker::class.java)
            startActivityForResult(intent, RESULT_SUBACTIVITY)
        }

        val mapButton = findViewById<Button>(R.id.mapButton)
        mapButton.setOnClickListener {
            val intent = Intent(application, PinMapActivity::class.java)
            intent.putExtra("SPOTDATA", newSpotList)
            startActivityForResult(intent, RESULT_SUBACTIVITY)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (resultCode == Activity.RESULT_OK && requestCode == RESULT_SUBACTIVITY && null != intent) {
            Log.d("エックス", intent.getStringExtra("LatLngX"))
            Log.d("ワイ", intent.getStringExtra("LatLngY"))
            Log.d("名前", intent.getStringExtra("NAME"))
            //val GETPOINT = LatLng(Double.parseDouble(intent.getStringExtra("LatLngX")), Double.parseDouble(intent.getStringExtra("LatLngY")))
            /*for(_newSpotList in newSpotList) {
                newSpotNameList.add(_newSpotList.name)
            }*/
            val selectSpotAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,newSpotNameList)
            selectSpotAdapter.add(intent.getStringExtra("NAME"))
            newSpotList.add(SpotData(intent.getStringExtra("NAME"),parseDouble(intent.getStringExtra("LatLngX")),parseDouble(intent.getStringExtra("LatLngY")),Date()))

            selectSpotList.setAdapter(selectSpotAdapter);
        }
    }

    fun sortList(){
        //val tempSpotList = spotList
        //val tempSpotNameList = spotNameList
        Collections.reverse(spotList)
        Collections.reverse(spotNameList)
        val userSpotAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, spotNameList)
        userSpotList.setAdapter(userSpotAdapter)
        /*spotList.clear()
        for(_tempSpot in tempSpotList) {
            spotList.add(_tempSpot)
        }*/
    }
}
