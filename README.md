# Gradle - GCSBuildCache 

This Gradle plugin provides a build cache implementation that uses Google Cloud Storage to store build artifacts.

## Options

The build cache takes the following options:

| Option             | Description                                                                                                      |
|--------------------|------------------------------------------------------------------------------------------------------------------|
| credentials        | JSON key file of the service account to use (optional) - leave blank to use Google Cloud SDK application default |
| bucket             | Name of the Google Cloud Storage bucket (required)                                                               |
| expireAfterSeconds | Amount of time to pass after which a cached artifact expires and is removed from the cache (optional)            |

## Usage

There are multiple ways to use the Google Cloud Storage based build cache inside your projects.

### As plugin in `settings.gradle.kts`

.settings.gradle.kts
```kotlin
plugins {
    id("com.ridedott.gradle-gcs-build-cache") version "1.0.1"
}

buildCache {
    remote<com.ridedott.gradle.buildcache.GCSBuildCache> {
        credentials = 'my-key.json' // optional - leave blank to use Google Cloud SDK default
        bucket = "my-bucket-name"
        expireAfterSeconds = 60 * 60 * 24 // optional
    }
}
```

## License

include::LICENSE[]
