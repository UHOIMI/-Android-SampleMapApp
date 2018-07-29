package com.example.g015c1140.SampleMapApp

import java.io.Serializable
import java.util.*

data class SpotData(
        val name : String,
        val latitude : Double,
        val longitude : Double,
        val dateTime : Date
): Serializable