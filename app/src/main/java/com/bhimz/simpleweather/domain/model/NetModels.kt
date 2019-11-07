package com.bhimz.simpleweather.domain.model

import com.google.gson.*
import java.lang.reflect.Type

data class ForecastData(
    val list: List<WeatherData>
)

data class WeatherData(
    val dt: Long,
    val temperature: Double,
    val weatherCondition: String,
    val icon: String
)

class WeatherDataDeserializer : JsonDeserializer<WeatherData> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): WeatherData {
        var dt = 0L
        var temperature = 0.0
        var weatherCondition = ""
        var icon = ""
        json?.let {
            if (it.isJsonObject) {
                val obj = it.asJsonObject
                if (obj.has("dt")) { dt = obj["dt"].asLong }
                if (obj.has("main") &&
                    obj["main"].isJsonObject &&
                    obj["main"].asJsonObject.has("temp")) {
                    temperature = obj["main"].asJsonObject["temp"].asDouble
                }
                if (obj.has("weather") && obj["weather"].isJsonArray) {
                    val objList = obj["weather"].asJsonArray
                    if (objList.size() > 0 && objList[0].isJsonObject) {
                        val weather = objList[0].asJsonObject
                        if (weather.has("main")) {
                            weatherCondition = weather["main"].asString
                        }
                        if (weather.has("icon")) {
                            icon = weather["icon"].asString
                        }
                    }
                }
            }
        }
        return WeatherData(dt, temperature, weatherCondition, icon)
    }


}