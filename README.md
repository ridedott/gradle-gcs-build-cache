# Gradle - GCSBuildCache

This Gradle plugin provides a build cache implementation that uses Google Cloud Storage to store build artifacts.

## Options

The build cache takes the following options:

| Option                            | Description                                                                                           |
| --------------------------------- | ----------------------------------------------------------------------------------------------------- |
| bucket                            | Name of the Google Cloud Storage bucket (required)                                                    |
| serviceAccountCredentialsFilePath | The path to the JSON key file of the service account to use (optional)                                |
| serviceAccountCredentialsJSON     | The JSON key of the service account to use (optional)                                                 |
| expireAfterSeconds                | Amount of time to pass after which a cached artifact expires and is removed from the cache (optional) |

When both `serviceAccountCredentialsFilePath` and `serviceAccountCredentialsJSON` are not provided,
this plugin defaults to the Google Cloud SDK default credentials.

## Using Google Cloud SDK default credentials

To use the Google Cloud SDK default credentials, you need to authenticate with the Google Cloud SDK.

First, follow [the install guide](https://cloud.google.com/sdk/docs/install) to install the Google Cloud SDK.

Next, run the following command in a terminal:

```bash
gcloud init
```

When prompted, choose the project ID where you created your GCS bucket.

Finally, run the following command in a terminal:

```
gcloud auth application-default login
```

Now you're ready to use the Gradle remote build cache.

## Usage

Add this plugin to your `settings.gradle.kts` as follows:

```kotlin
plugins {
    id("com.ridedott.gradle-gcs-build-cache") version "1.0.1"
}

buildCache {
    remote<com.ridedott.gradle.buildcache.GCSBuildCache> {
        bucket = "my-bucket-name" // required
        serviceAccountCredentialsFilePath = 'my-key.json' // optional
        serviceAccountCredentialsJSON = "{ ... }" // optional
        expireAfterSeconds = 60 * 60 * 24 // optional
    }
}
```
