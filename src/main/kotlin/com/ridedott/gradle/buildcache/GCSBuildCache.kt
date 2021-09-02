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

import org.gradle.caching.configuration.AbstractBuildCache

/**
 * Configuration of the GCS based build cache.
 *
 * @author Thorsten Ehlers (thorsten.ehlers@googlemail.com) (initial creation)
 * @author Reinier Goltstein (reinier@ridedott.com) (modifications)
 */
abstract class GCSBuildCache(
    var bucket: String = "",
    var credentials: String = "",
    var expireAfterSeconds: Long = 0L
) : AbstractBuildCache()
