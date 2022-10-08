# FXRadio

[![Build](https://github.com/Joseph5610/fxradio-main/actions/workflows/main.yml/badge.svg?branch=master)](https://github.com/Joseph5610/fxradio-main/actions/workflows/main.yml)

Internet radio directory desktop app written in [tornadofx](https://tornadofx.io) framework.

Play thousands of radio stations from around the world. 
Find out which stations are currently trending or find all radio stations broadcasting in your country.

The app is using VLCLib, so we recommend installing VLC player for the best experience. If the VLC Player is not present on your system, the app will use ffmpeg library to play the audio streams. However, ffmpeg implementation is not perfect at the moment, and some (many) of the streams might play broken or not play at all.

This app is using http://radio-browser.info public API

[Visit the website of the project to find more information](https://hudacek.online/fxradio)

# Download and run the app

Download the latest release from [Releases](https://github.com/Joseph5610/fxradio-main/releases) page in this repository.
Currently, we provide several release files:
- .dmg installer for macOS
- .msi installer for Windows

:warning: **Native format for Unix OS is currently not built** 
Those builds have not been tested, but you can still try to build it yourself.

### Windows

Download **fxradio_windows.msi**, run it and proceed with installation. 
Windows version is currently in beta, so you should expect some bugs.

In case your antivirus flags the installer as malicious, please add it to exceptions. App is currently not correctly signed so it might trigger some false positives.

### macOS
Download DMG archive **fxradio_macOS.dmg**. After downloading follow those steps:
1. Double click to open the downloaded file and move the FxRadio icon into "Applications" folder
2. Find the FXRadio application in Launchpad or Applications folder in Finder

:warning: Currently, macOS refuses to run the app entirely with the warning that the app is unsafe and should be moved to trash. This is because builds are not correctly signed. 
There is a workaround that requires some basic Terminal skills. After installation of the app, perform this command:

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

