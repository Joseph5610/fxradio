# FXRadio

Internet radio directory written in [tornadofx](https://tornadofx.io) framework.

Play thousands of radio stations from around the world. Find out which stations are currently most rated by other listeners or select the country to list all the radio stations from the selected country. If you know the name of your favourite station, you can just search for it and it will appear instantly if we have it in our database. If we don't, you can add it yourself - you just need to provide a couple of details about the station.

The app is using VLCLib, so we recommend installing VLC player for the best experience. If the VLC Player is not present on your system, the app will use ffmpeg library to play the audio streams. However, ffmpeg implementation is not perfect at the moment, and some (many) of the streams might play broken or not play at all.

This app is using http://radio-browser.info public API

[Visit the website of the project to find more information](https://hudacek.online/fxradio)

# Download

Download the latest release from [Releases](https://github.com/Joseph5610/fxradio-main/releases) page in this repository.
Currently, we provide several release files:
- If you live on the edge, you can download plain JAR file, but beware, the JAR file can only be run using **Java 1.8**.
- Otherwise, we currently provide native .app file for macOS

:warning: **Native format for Windows/Unix OS is currently not built** 
Those builds have not been tested, but you can still try to build it yourself.

# How to run the app

Assuming you already downloaded the preferred distribution from [Releases](https://github.com/Joseph5610/fxradio-main/releases)
page, there is just a few more steps to run the app.

If you downloaded ZIP archive **release_osx.zip** that contains native app file for macOS:
1. Extract the ZIP file to some location on your filesystem
2. Locate FxRadio.app in the extracted folder and double click to run it.

If you are presented with the dialog that the app is unsafe to run, don't worry. The app is in early stages of development
and because of that the distribution is currently not signed. You just need to enable the app in your macOS System Preferences -> Security and Privacy tab

:warning: With some builds, macOS refuses to run the app entirely with the warning that the app is unsafe and should be moved to trash. This is because builds are not correctly signed. 
There is a workaround that requires some basic Terminal skills. After installation of the app, perform this command:

```bash
sudo xattr -r -d com.apple.quarantine /Applications/FXRadio.app/
```

If you are not on macOS and you have downloaded .jar files, beware, this distribution is not officially supported but allows you to run the app on any system that supports Java.
1. Ensure that you have Java8 installed, as this is only supported Java version
2. Locate the downloaded fxRadio.jar file on your file system and navigate to this directory using command line.
3. Check that java command points to JRE 1.8 installation - run the command  **java -version**
3. If java version command returns 1.8.0_xyz, you can run the command **java -jar fxRadio.jar** and app should now start. 

As we didn't test the jar files on every OS, we cannot guarantee that app will run smoothly. You should expect bugs.

# Build

> We recommend using IntelliJ IDEA as IDE for this project. 

To build the app yourself you only need to set up a few things:

- Clone the repository
- Open existing gradle project from the location where you downloaded the files 
- Run **jfxNative** gradle task

Build output is present in build/jfx/ directory. 

