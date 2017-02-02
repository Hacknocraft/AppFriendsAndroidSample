3.0.8 (2017-02-02)
------------------

- fixed the photos 90 degree rotated issues
- fixed the read receipts sending problem
- fixed the problem of opening chat directly from push notification

3.0.7 (2017-01-31)
------------------

- performance update with chat
- fixed issues with message receipts sending multiple times and other issues
- fixed a bug where read receipts are being sent for system messages
- added a way to programmatically pass app id and app secret to init function "public void init(final Context context, final String appId, final String appSecret) "
- updated animations for auto scrolling to new messages
- updated camera and gallery permission support of Android M - N
- to properly allow Android-N device to take pictures from camera, the app must expose file path for the camera app. Please add to the app's AndroidManifest.xml:
```
<provider
            android:name="android.support.v4.content.FileProvider"
            android:authorities="${applicationId}.provider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/provider_paths" />
</provider>
```
and put a `provider_paths.xml` file inside `res/xml/` with:
```
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <external-path name="external_files" path="."/>
</paths>
```

3.0.6 (2017-01-27)
------------------

- fixed bug with push registration return value
- added checks for client app backgrounding/foregrounding
- added sending read receipts
- added live dialog information update
- added permission checking for picking image and taking new photos
- handle dialog name change and adding new users from another device to group dialog in chat
- added interface to receive callback when send messages

3.0.5 (2017-01-22)
------------------

- Added interface for registering push token
- Added automatic push notification switch when has been backgrounded
- Adjusted last read message to include sender's user name
- Updated dialog badge to include unread count
- Improved syncing capabilities

Notes:
- Please add `maven { url 'https://jitpack.io' }` to project level gradle file for dependencies.


3.0.4 (2017-01-13)
------------------
- Dialog list sorting by update time
- Dialog badge dot for newly updated dialogs
- Updated error messages for services
- Private conversation dialog name update
- Stability improvements
- Bug fixes
