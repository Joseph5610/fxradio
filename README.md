# FXRadio

[![Build](https://github.com/Joseph5610/fxradio-main/actions/workflows/main.yml/badge.svg?branch=master)](https://github.com/Joseph5610/fxradio-main/actions/workflows/main.yml)

Internet radio directory desktop app written in [tornadofx](https://tornadofx.io) framework.

Play thousands of radio stations from all around the world. 

The app is using VLCLib, so we recommend installing VLC player for the best experience. 
If the VLC Player is not present on your system, the app will use ffmpeg library to play the audio. 
However, ffmpeg implementation is not perfect at the moment, and some stations might sound broken or not play at all.

The app is using http://radio-browser.info public API

[Visit the website of the project](https://hudacek.online/fxradio)

## Download and run the app

Download the latest release from [Releases](https://github.com/Joseph5610/fxradio-main/releases) page in this repository.
Currently, we provide following installers:
- .dmg installer for macOS
- .msi installer for Windows

### Windows

Download **fxradio_windows.msi**, run it and proceed with installation.
In case your antivirus flags the installer or application exe file as malicious, please add it to exceptions. App is currently not correctly signed so it might trigger some false positives.

### macOS
Download DMG archive **fxradio_macOS.dmg**. After downloading follow those steps:
1. Double click to open the downloaded file and move the FXRadio icon into "Applications" folder
2. Open the Applications folder in Finder and launch the app

:warning: Currently, macOS refuses to run the app entirely with the warning that the app is unsafe and should be moved to trash. 
This is because builds are not correctly signed. If the alert is shown, go to
```
System Preferences -> Security & Privacy 
```
Allow changes by clicking on Lock icon and click Open button on the screen. 

If the option to open the app is not present, try to run the following command in the Terminal app:

```bash
sudo xattr -r -d com.apple.quarantine /Applications/FXRadio.app/
```
# Build

> We recommend using IntelliJ IDEA as IDE for this project. 

To build the app yourself you only need to set up a few things:

- Clone the repository
- Open existing gradle project from the location where you downloaded the files 
- Run **jfxNative** gradle task

Build output is present in build/jfx/ directory. 

