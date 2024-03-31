# FXRadio

[![Build](https://github.com/Joseph5610/fxradio/actions/workflows/main.yml/badge.svg?branch=master)](https://github.com/Joseph5610/fxradio-main/actions/workflows/main.yml)

[Visit the website of the project](https://hudacek.online/fxradio)

Internet radio directory desktop app written in [tornadofx](https://tornadofx.io) framework.

Play thousands of radio stations from all around the world.

The app is using VLCLib, so we recommend installing VLC player for the best experience.
If the VLC Player is not installed, the app will use [humble-video](https://github.com/artclarke/humble-video) library
to play the audio.
However, some stations might sound broken or not play at all with this player.

The app is using http://radio-browser.info API

## Download and run the app

Download the latest release from [Releases](https://github.com/Joseph5610/fxradio/releases) page in this repository.

#### Windows

Download **fxradio_windows.msi**, run it and proceed with installation.
In case your antivirus flags the installer or application exe file as malicious, please add it to exceptions. App is
currently not correctly signed, so it might trigger some false positives.

#### macOS

Download DMG archive **fxradio_macOS.dmg**. After downloading, follow those steps:

1. Double click to open the downloaded file and move the FXRadio icon into "Applications" folder
2. Open the Applications folder in Finder and launch the app

:warning: macOS will refuse to run the app with the warning that the app is unsafe and should be moved to trash.
This is because builds are not correctly signed. If the alert is shown, go to

```
System Preferences -> Security & Privacy 
```

Allow changes by clicking on Lock icon and click on Open anyway button.

If the option to open the app is not present, try to run the following command in the Terminal:

```bash
sudo xattr -r -d com.apple.quarantine /Applications/FXRadio.app/
```

# Build

App requires JDK 21 and JavaFX 21 to build and run.

To build the app yourself:

- Clone the repository
- Run ```./gradlew jfxNative``` command from the root project directory

Build output is present in ```build/jfx/``` directory.

-----------------------------------------------------
macOS install disk background designed by [xb100 / Freepik](https://www.freepik.com/author/xb100)

[Voice chat icons created by Rizki Ahmad Fauzi - Flaticon](https://www.flaticon.com/free-icons/voice-chat)
