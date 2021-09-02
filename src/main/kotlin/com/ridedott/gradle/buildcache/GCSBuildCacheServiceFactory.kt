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

import org.gradle.api.GradleException
import org.gradle.caching.BuildCacheService
import org.gradle.caching.BuildCacheServiceFactory

/**
 * ServiceFactory that takes the given configuration to create a GCS based build-cache.
 *
 * @author Thorsten Ehlers (thorsten.ehlers@googlemail.com) (initial creation)
 */
class GCSBuildCacheServiceFactory : BuildCacheServiceFactory<GCSBuildCache> {

    override fun createBuildCacheService(
        configuration: GCSBuildCache,
        describer: BuildCacheServiceFactory.Describer,
    ): BuildCacheService {
        val bucket = configuration.bucket.takeIf { it.isNotEmpty() }
            ?: throw GradleException("Bucket name was not defined. Check GitHub for instructions.")

        val credentialsDescription = if (configuration.credentials.isEmpty()) {
            "application default"
        } else {
            "from file: ${configuration.credentials}"
        }

        val expireAfterSeconds = configuration.expireAfterSeconds

        describer
            .type("Google Cloud Storage")
            .config("credentials", credentialsDescription)
            .config("bucket", bucket)
            .config("expireAfterSeconds", expireAfterSeconds.toString())

        return GCSBuildCacheService(configuration.credentials, bucket, expireAfterSeconds)
    }
}
