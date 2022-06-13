# Overview

This module contains example of implementation [Keyri](https://keyri.com) with Supabase.

## Contents

* [Requirements](#Requirements)
* [Permissions](#Permissions)
* [Keyri Integration](#Keyri-Integration)
* [Supabase Integration](#Supabase-Integration)
* [Authentication](#Authentication)

## Requirements

* Android API level 23 or higher
* AndroidX compatibility
* Kotlin coroutines compatibility

Note: Your app does not have to be written in kotlin to integrate this SDK, but must be able to
depend on kotlin functionality.

## Permissions

Open your app's `AndroidManifest.xml` file and add the following permission:

```xml

<uses-permission android:name="android.permission.INTERNET" />
```

## Keyri Integration

* Add the JitPack repository to your root build.gradle file:

```groovy
allprojects {
    repositories {
        // ...
        maven { url "https://jitpack.io" }
    }
}
```

* Add SDK dependency to your build.gradle file and sync project:

```kotlin
dependencies {
    // ...
    implementation("com.github.Keyri-Co:keyri-android-whitelabel-sdk:$latestKeyriVersion")
}
```

## Supabase Integration

Create Supabase project and enable Email Auth. Navigate to Project Settings -> API and copy your
project ApiKey. Use this key as the header for your API requests.

Use following endpoint for signup:
`https://pidfgjqywchqcqdjhmsj.supabase.co/auth/v1/signup`

Use next endpoint for login:
`https://pidfgjqywchqcqdjhmsj.supabase.co/auth/v1/token?grant_type=password`

Both requests must be sent with an `email` and `password`:

```json
{
  "email": "example@mail.com",
  "password": "SecretPassword"
}
```

This will return authorization response (applicable for v1 API version, subject to change for other
versions):

```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJhdXRoZW50aWNhdGVkIiwiZXhwIjoxNjU0ODczMDUzLCJzdWIiOiJiYjU3MmU4Yy1jZGFmLTQxZWQtOWM0YS03ZDg2ZDU3M2I4NGYiLCJlbWFpbCI6ImEua3VsaWFoaW5AY3NuLmtoYWkuZWR1IiwicGhvbmUiOiIiLCJhcHBfbWV0YWRhdGEiOnsicHJvdmlkZXIiOiJlbWFpbCIsInByb3ZpZGVycyI6WyJlbWFpbCJdfSwidXNlcl9tZXRhZGF0YSI6e30sInJvbGUiOiJhdXRoZW50aWNhdGVkIn0.X5XJFjrvW9IT8mPnNWNkcvWxQvHGDwC3lry5SD90Vkc",
  "token_type": "bearer",
  "expires_in": 3600,
  "refresh_token": "f4uxQYOW2-Q6IFLj38lVJw",
  "user": {
    "id": "bb572e8c-cdaf-41ed-9c4a-7d86d573b84f",
    "aud": "authenticated",
    "role": "authenticated",
    "email": "example@mail.com",
    "email_confirmed_at": "2022-06-10T10:45:05.510379Z",
    "phone": "",
    "confirmed_at": "2022-06-10T10:45:05.510379Z",
    "last_sign_in_at": "2022-06-10T13:57:33.232604594Z",
    "app_metadata": {
      "provider": "email",
      "providers": [
        "email"
      ]
    },
    "user_metadata": {},
    "identities": [
      {
        "id": "bb572e8c-cdaf-41ed-9c4a-7d86d573b84f",
        "user_id": "bb572e8c-cdaf-41ed-9c4a-7d86d573b84f",
        "identity_data": {
          "sub": "bb572e8c-cdaf-41ed-9c4a-7d86d573b84f"
        },
        "provider": "email",
        "last_sign_in_at": "2022-06-10T10:45:05.508407Z",
        "created_at": "2022-06-10T10:45:05.508451Z",
        "updated_at": "2022-06-10T10:45:05.508455Z"
      }
    ],
    "created_at": "2022-06-10T10:45:05.499328Z",
    "updated_at": "2022-06-10T13:57:33.233698Z"
  }
}
```

## Authentication

Use `access_token` and `email` fields to create payload and user signature:

```kotlin
val email = authResponse.user.email
val keyri = Keyri()

val payload = JSONObject().apply {
    put("token", authResponse.accessToken)
    put("provider", "supabase:email_password") // Optional
    put("timestamp", System.currentTimeMillis()) // Optional
    put("associationKey", keyri.getAssociationKey(email)) // Optional
    put("userSignature", keyri.getUserSignature(email, email)) // Optional
}.toString()

keyriAuth(email, payload)
```

Authenticate with Keyri. In the next showing `AuthWithScannerActivity` with providing
`publicUserId` and `payload`.

```kotlin
private val easyKeyriAuthLauncher =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        // Process authentication result
    }

private fun keyriAuth(publicUserId: String?, payload: String) {
    val intent = Intent(this, AuthWithScannerActivity::class.java).apply {
        putExtra(AuthWithScannerActivity.APP_KEY, BuildConfig.APP_KEY)
        putExtra(AuthWithScannerActivity.PUBLIC_USER_ID, publicUserId)
        putExtra(AuthWithScannerActivity.PAYLOAD, payload)
    }

    easyKeyriAuthLauncher.launch(intent)
}
```
