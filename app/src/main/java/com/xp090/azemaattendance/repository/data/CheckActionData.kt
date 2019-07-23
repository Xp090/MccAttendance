package com.xp090.azemaattendance.repository.data

import retrofit2.http.Query
import java.io.File

data class CheckActionData  (var checkType: String, var latitude: Double,
                             var longitude: Double)