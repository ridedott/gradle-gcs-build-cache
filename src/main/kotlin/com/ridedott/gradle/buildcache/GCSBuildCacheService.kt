/**
 * Copyright 2019 Thorsten Ehlers
 * Copyright 2021 Dott B.V., Netherlands
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ridedott.gradle.buildcache

import com.google.auth.Credentials
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.storage.Bucket
import com.google.cloud.storage.StorageException
import com.google.cloud.storage.StorageOptions
import org.gradle.caching.BuildCacheEntryReader
import org.gradle.caching.BuildCacheEntryWriter
import org.gradle.caching.BuildCacheException
import org.gradle.caching.BuildCacheKey
import org.gradle.caching.BuildCacheService
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.IOException
import java.nio.channels.Channels
import java.time.Instant

/**
 * BuildCacheService that stores the build artifacts in Google Cloud Storage.
 * The creation time will be reset in a configurable interval to make sure
 * that artifacts still in use are not deleted.
 *
 * @author Thorsten Ehlers (thorsten.ehlers@googlemail.com) (initial creation)
 */
class GCSBuildCacheService(
    private val bucketName: String,
    private val expireAfterSeconds: Long,
    serviceAccountCredentialsFilePath: String,
    serviceAccountCredentialsJSON: String,
) : BuildCacheService {

    private val bucket: Bucket

    init {
        try {
            val storage = StorageOptions.newBuilder()
                .setCredentials(
                    getCredentials(serviceAccountCredentialsFilePath, serviceAccountCredentialsJSON)
                )
                .build()
                .service

            bucket = storage.get(bucketName)
                ?: throw BuildCacheException("$bucketName is unavailable")
        } catch (e: IOException) {
            throw BuildCacheException("Unable to access Google Cloud Storage bucket '$bucketName'.", e)
        } catch (e: StorageException) {
            throw BuildCacheException("Unable to access Google Cloud Storage bucket '$bucketName'.", e)
        }
    }

    private fun getCredentials(serviceAccountCredentialsFilePath: String, serviceAccountCredentialsJSON: String): Credentials {
        return when {
            serviceAccountCredentialsFilePath.isNotEmpty() -> {
                ServiceAccountCredentials.fromStream(FileInputStream(serviceAccountCredentialsFilePath))
            }
            serviceAccountCredentialsJSON.isNotEmpty() -> {
                ServiceAccountCredentials.fromStream(serviceAccountCredentialsJSON.byteInputStream())
            }
            else -> ServiceAccountCredentials.getApplicationDefault()
        }
    }

    override fun store(key: BuildCacheKey, writer: BuildCacheEntryWriter) {
        val value = ByteArrayOutputStream()
        writer.writeTo(value)

        try {
            bucket.create(key.hashCode, value.toByteArray())
        } catch (e: StorageException) {
            throw BuildCacheException(
                "Unable to store '${key.hashCode}' in Google Cloud Storage bucket '$bucketName'.",
                e
            )
        }
    }

    override fun load(key: BuildCacheKey, reader: BuildCacheEntryReader): Boolean {
        try {
            val blob = bucket.get(key.hashCode)

            if (blob != null) {
                reader.readFrom(Channels.newInputStream(blob.reader()))

                if (expireAfterSeconds > 0) {
                    // Update creation time so that artifacts that are still used won't be deleted.
                    val createTime = Instant.ofEpochMilli(blob.createTime)
                    if (createTime.plusSeconds(expireAfterSeconds).isBefore(Instant.now())) {
                        bucket.create(key.hashCode, blob.getContent())
                    }
                }

                return true
            }
        } catch (e: StorageException) {
            // https://github.com/googleapis/google-cloud-java/issues/3402
            if (e.message?.contains("404") == true) {
                return false
            }

            throw BuildCacheException(
                "Unable to load '${key.hashCode}' from Google Cloud Storage bucket '$bucketName'.",
                e
            )
        }

        return false
    }

    override fun close() {
        // nothing to do
    }
}
