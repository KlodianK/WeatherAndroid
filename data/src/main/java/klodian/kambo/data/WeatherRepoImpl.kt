package klodian.kambo.data

import arrow.core.Either
import klodian.kambo.domain.*
import kotlinx.coroutines.coroutineScope
import java.util.*
import javax.inject.Inject

class WeatherRepoImpl @Inject constructor(
    private val weatherApi: WeatherApi,
    private val getIconPath: GetIconPath
) : WeatherRepo {

    companion object{
        private const val TEMPERATURE_UNIT_IMPERIAL ="imperial"
        private const val TEMPERATURE_UNIT_METRIC ="metric"
    }
    override suspend fun getWeather(
        cityName: String,
        locale: Locale,
        measurementUnit: TemperatureMeasurementUnit
    ): Either<SafeRequestError, CompleteWeatherInfo> = coroutineScope {

        val unit = when (measurementUnit) {
            TemperatureMeasurementUnit.Fahrenheit -> TEMPERATURE_UNIT_IMPERIAL
            TemperatureMeasurementUnit.Celsius -> TEMPERATURE_UNIT_METRIC
        }

        performSafeRequest {
            val weatherResponse = weatherApi
                .getWeather(cityNamePattern = cityName, language = locale.language, units = unit)

            CompleteWeatherInfo(temperature = weatherResponse.main.toTemperature(measurementUnit),
                weather = weatherResponse.weather.map { it.toWeather() })
        }
    }

    private fun WeatherDto.toWeather(): Weather {
        return Weather(
            id = id,
            weather = main,
            description = description,
            iconName = getIconPath(icon)
        )
    }

    private fun TemperatureDto.toTemperature(measurementUnit: TemperatureMeasurementUnit): Temperature {
        return Temperature(temp, feelsLike, minTemp, maxTemp, pressure, humidity, measurementUnit)
    }
}