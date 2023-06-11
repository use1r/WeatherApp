package com.example.weatherapp.features.recommendation.data.mapper

import com.example.weatherapp.constants.DATE_24_PATTERN
import com.example.weatherapp.features.recommendation.data.remote.dto.HourlyAirQuality
import com.example.weatherapp.features.recommendation.domain.model.AqiRecommendation
import com.example.weatherapp.features.recommendation.domain.model.Recommendations
import com.example.weatherapp.features.recommendation.domain.model.UvRecommendation
import com.example.weatherapp.features.recommendation.domain.model.toAqiLevel
import com.example.weatherapp.features.recommendation.domain.model.toUvIndexLevel
import com.example.weatherapp.util.filterPastHours
import com.example.weatherapp.util.toDate

fun HourlyAirQuality.toRecommendations(timeZone: String): Recommendations {
    // Filter time period already passed today
    val newTimeList = time.filterPastHours()
    // Create new lists filter values belong to passed time period
    val newUvIndexList = uvIndexList.takeLast(newTimeList.size).filterNotNull()
    val newAqiList = europeanAqiList.takeLast(newTimeList.size).filterNotNull()

    var recommendations = Recommendations()
    val lastIndex = newUvIndexList.size - 2
    val firstTimeLine = newTimeList[0].toDate(timeZone, DATE_24_PATTERN)

    for (i in 0..lastIndex) {
        val currentUvLevel = newUvIndexList[i].toUvIndexLevel()
        val nextUvLevel = newUvIndexList[i + 1].toUvIndexLevel()

        if (currentUvLevel != nextUvLevel || i == lastIndex) {
            recommendations = recommendations.copy(
                uvRecommendation = UvRecommendation(
                    firstTimePeriod = firstTimeLine,
                    secondTimePeriod = newTimeList[i].toDate(timeZone, DATE_24_PATTERN),
                    infoRes = currentUvLevel.infoRes,
                    recommendationRes = currentUvLevel.recommendationRes,
                ),
            )
            break
        }
    }

    for (i in 0..lastIndex) {
        val currentAqiLevel = newAqiList[i].toAqiLevel()
        val nextAqiLevel = newAqiList[i + 1].toAqiLevel()

        if (currentAqiLevel != nextAqiLevel || i == lastIndex) {
            recommendations = recommendations.copy(
                aqiRecommendation = AqiRecommendation(
                    firstTimePeriod = firstTimeLine,
                    secondTimePeriod = newTimeList[i].toDate(timeZone, DATE_24_PATTERN),
                    infoRes = currentAqiLevel.infoRes,
                    generalPopulationRecommendationRes = currentAqiLevel.generalPopulationRecommendationRes,
                    sensitivePopulationRecommendationRes = currentAqiLevel.sensitivePopulationRecommendationRes,
                ),
            )
            break
        }
    }

    return recommendations
}
