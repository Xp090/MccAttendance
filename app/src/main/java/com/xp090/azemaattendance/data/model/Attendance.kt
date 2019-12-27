package com.xp090.azemaattendance.data.model
import com.squareup.moshi.JsonClass

import com.squareup.moshi.Json
import java.util.*


@JsonClass(generateAdapter = true)
data class UserDailyAttendanceData(
    @Json(name = "attendance")
    val attendance: Attendance?,
    @Json(name = "user")
    val user: User
)

@JsonClass(generateAdapter = true)
data class User(
    @Json(name = "username")
    val username: String,
    @Json(name = "id")
    val id: String,
    @Json(name = "fullName")
    val fullName: String?,
    @Json(name = "projectId")
    val projectId: String?,
    @Json(name = "project")
    val project: Project?,
    @Json(name = "image")
    val image:String?
)

@JsonClass(generateAdapter = true)
data class Project(
    @Json(name = "id")
    val id: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "branches")
    val branches: List<Branch>?
)

@JsonClass(generateAdapter = true)
data class Branch(
    @Json(name = "areaSize")
    val areaSize: Int,
    @Json(name = "id")
    val id: String,
    @Json(name = "location")
    val location: GeoPoint,
    @Json(name = "name")
    val name: String
)

@JsonClass(generateAdapter = true)
data class GeoPoint(
    @Json(name = "_latitude")
    val latitude: Double,
    @Json(name = "_longitude")
    val longitude: Double
)

@JsonClass(generateAdapter = true)
data class Attendance(
    @Json(name = "checkIn")
    val checkIn: Map<String,CheckData>?,
    @Json(name = "checkOut")
    val checkOut: Map<String,CheckData>?,
    @Json(name = "dailyReportAnswers")
    val dailyReportAnswers: List<String>?,
    @Json(name = "date")
    val date: Date,
    @Json(name = "user")
    val user: User
)


@JsonClass(generateAdapter = true)
data class CheckData(
    @Json(name = "image")
    val image: String,
    @Json(name = "location")
    val location: GeoPoint,
    @Json(name = "time")
    val time: Date
)


