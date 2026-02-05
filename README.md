# ft_hangouts

Contact management Android application for 42 school project.

## Features

- **Contact Management**: Create, edit, delete contacts (5 fields: name, surname, phone, email, address)
- **SMS Messaging**: Send and receive text messages with conversation history
- **Bilingual**: English and Russian (follows system language)
- **Customizable Header**: Change app header color via menu
- **Background Time**: Shows toast with timestamp when returning from background
- **42 Logo**: Custom app icon
- **Orientation Support**: Works in portrait and landscape modes

## Requirements

- Android SDK 34
- Min SDK 24 (Android 7.0)
- Java 8+

## Build at 42 School (Mac)

### 1. Clone repository
```bash
git clone https://github.com/privet100/ft_hangouts.git
cd ft_hangouts
```

### 2. Install Android SDK in goinfre
```bash
./setup_android_42.sh
```
This installs Android SDK (~2GB) in `~/goinfre/android-sdk`

### 3. Set environment variables
```bash
export ANDROID_HOME=~/goinfre/android-sdk
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools
```

Add these lines to `~/.zshrc` to make permanent.

### 4. Build APK
```bash
./gradlew assembleDebug
```

### 5. Find APK
```bash
ls app/build/outputs/apk/debug/app-debug.apk
```

## Run the App

### Option 1: Physical Android Device (Recommended)
1. Enable "Developer Options" on your Android phone
2. Enable "USB Debugging"
3. Connect phone via USB
4. Copy APK: `adb install app/build/outputs/apk/debug/app-debug.apk`

Or simply transfer the APK file to your phone and install manually.

### Option 2: Android Emulator
```bash
# Install emulator (additional ~1GB)
sdkmanager "emulator" "system-images;android-34;google_apis;x86_64"

# Create AVD
avdmanager create avd -n test -k "system-images;android-34;google_apis;x86_64"

# Run emulator
emulator -avd test

# Install APK (in another terminal)
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Project Structure

```
app/src/main/
├── java/com/music42/ft_hangouts/
│   ├── MainActivity.kt              # Contact list
│   ├── ContactDetailActivity.kt     # View contact
│   ├── AddEditContactActivity.kt    # Create/edit contact
│   ├── ConversationActivity.kt      # SMS chat
│   ├── App.kt                       # Application class
│   ├── db/
│   │   ├── ContactDbHelper.kt       # SQLite database
│   │   ├── Contact.kt               # Contact model
│   │   └── Message.kt               # Message model
│   ├── adapter/
│   │   ├── ContactAdapter.kt        # Contacts RecyclerView
│   │   └── MessageAdapter.kt        # Messages RecyclerView
│   └── receiver/
│       └── SmsReceiver.kt           # Incoming SMS handler
└── res/
    ├── layout/                      # XML layouts
    ├── values/strings.xml           # English strings
    ├── values-ru/strings.xml        # Russian strings
    ├── menu/main_menu.xml           # Header color menu
    └── drawable/                    # Icons and backgrounds
```

## Permissions

The app requires these permissions (requested at runtime):
- `SEND_SMS` - Send text messages
- `RECEIVE_SMS` - Receive text messages
- `READ_SMS` - Read SMS for conversation history

## Testing Checklist

- [ ] Create a new contact with all 5 fields
- [ ] Edit an existing contact
- [ ] Delete a contact
- [ ] Send SMS to a contact
- [ ] Receive SMS from a contact (if testing with real device)
- [ ] Check conversation history shows sender/receiver correctly
- [ ] Change header color via menu
- [ ] Put app in background, return, verify toast shows time
- [ ] Change system language, verify app language changes
- [ ] Rotate device, verify layout works in both orientations
- [ ] Verify app icon is 42 logo

## Troubleshooting

### "SDK location not found"
```bash
export ANDROID_HOME=~/goinfre/android-sdk
```

### "Java not found"
Check Java installation:
```bash
java -version
/usr/libexec/java_home
```

### Build fails with memory error
```bash
export GRADLE_OPTS="-Xmx2048m"
./gradlew assembleDebug
```

### goinfre gets wiped
Re-run `./setup_android_42.sh` - it will reinstall SDK.

## License

42 School Project
