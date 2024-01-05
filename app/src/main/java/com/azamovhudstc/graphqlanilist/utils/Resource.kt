/*
 *  Created by Azamov X ㋡ on 1/5/24, 12:14 AM
 *  Copyright (c) 2024 . All rights reserved.
 *  Last modified 1/5/24, 12:14 AM
 *
 *
 */

package com.azamovhudstc.graphqlanilist.utils

sealed class Resource<out T : Any> {
    object Loading : Resource<Nothing>()
    data class Success<out T : Any>(val data: T) : Resource<T>()
    data class Error(val throwable: Throwable) : Resource<Nothing>()
}