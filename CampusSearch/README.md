# Campus Search (CampusFind prototype)

Android app for University of Lahore students to report and find lost items on campus.

- **Theme:** light green, yellow and white (Material 3)
- **Language / UI:** Java, XML layouts, Material Components
- **Database:** local SQLite (`DatabaseHelper`) - users (PBKDF2 hashed passwords) + items
- **Location:** Google Play Services Fused Location (`LocationHelper`)
- **Min SDK:** 26 (Android 8.0)

## How to run
1. Install Android Studio (Koala or newer), open this folder (`CampusSearch`).
2. Wait for Gradle sync (internet needed the first time).
3. Create/choose an emulator (with Google Play / Google APIs) or connect a phone.
4. Click Run. First launch seeds 6 starter items.
5. Emulator location: `... > Extended controls > Location` -> set a point (e.g. 31.38955, 74.24070) and Send.

## Features
F1 icon + name | F2 Login/Register | F3 bottom nav + Profile | F4 Home list (newest first) |
F5 details | F6 Report form + validation | F7 current location | F8 SQLite save |
F9 session + logout | F10 open in Maps | F11 Nearby (sorted by distance) | F12 Lost/Found filter + search + Mark as Returned

## Project structure
```
app/src/main/java/com/campussearch/app/
  data/  DatabaseHelper, SessionManager, PasswordUtil, Item, User
  util/  Validators, LocationHelper, DistanceUtil
  ui/    LoginActivity, RegisterActivity, MainActivity, ItemDetailsActivity,
         HomeFragment, NearbyFragment, ReportFragment, ProfileFragment, ItemAdapter
```

## Before submitting - edit these
- Amna,Rumaisa / Entry ID / 2 + 70149112,70149424 / GitHub link: `app/src/main/res/values/strings.xml` and this README

