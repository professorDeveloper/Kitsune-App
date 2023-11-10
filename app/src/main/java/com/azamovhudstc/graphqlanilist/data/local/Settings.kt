package com.azamovhudstc.graphqlanilist.data.local

import android.content.Context
import android.content.SharedPreferences
import com.azamovhudstc.graphqlanilist.R
import com.azamovhudstc.graphqlanilist.type.AnimeTypes
import com.azamovhudstc.graphqlanilist.utils.booleanPreference
import com.azamovhudstc.graphqlanilist.utils.enumPreference
import com.azamovhudstc.graphqlanilist.utils.getPreferenceKey
import com.azamovhudstc.graphqlanilist.utils.longPreference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Settings @Inject constructor(
    appContext: Context,
    override val preferences: SharedPreferences
) : PreferencesHolder {

    var skipIntroDelay by longPreference(
        appContext.getPreferenceKey(R.string.skip_delay),
        default = 85_000L
    )

    var seekForwardTime by longPreference(
        appContext.getPreferenceKey(R.string.forward_seek),
        default = 10_000L
    )

    var seekBackwardTime by longPreference(
        appContext.getPreferenceKey(R.string.backward_seek),
        default = 10_000L
    )

    var pipEnabled by booleanPreference(
        appContext.getPreferenceKey(R.string.pip),
        default = false
    )

    var selectedProvider by enumPreference(
        appContext.getPreferenceKey(R.string.anime_provider),
        AnimeTypes.GOGO_ANIME
    )


}
