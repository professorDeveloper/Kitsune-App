/*
 *  Created by Azamov X ㋡ on 11/21/23, 2:02 AM
 *  Copyright (c) 2023 . All rights reserved.
 *  Last modified 11/21/23, 2:02 AM
 *
 *
 */

package com.azamovhudstc.graphqlanilist.utils

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Apollo

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class CustomDispatcher

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class RetrofitClient
