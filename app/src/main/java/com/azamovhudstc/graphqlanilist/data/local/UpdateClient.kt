package com.azamovhudstc.graphqlanilist.data.local

import android.icu.util.VersionInfo
import javax.inject.Inject

class UpdateClient @Inject constructor(
    private val updateService: UpdateService
) {
    suspend fun getUpdateInfo(): com.azamovhudstc.graphqlanilist.data.local.VersionInfo = updateService.getUpdateInfo()
}