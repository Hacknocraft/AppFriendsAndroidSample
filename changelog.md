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
