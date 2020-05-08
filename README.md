# FXRadio

Internet radio directory written in [tornadofx](https://tornadofx.io) framework.

Play thousands of radio stations from around the world. Find out which stations are currently most rated by other listeners or select the country to list all the radio stations from the selected country. If you know the name of your favourite station, you can just search for it and it will appear instantly if we have it in our database. If we don't, you can add it yourself - you just need to provide a couple of details about the station.

The app is using VLCLib so we recommend to install VLC player for the best experience. If the VLC Player is not present on your system, the app will use ffmpeg library to play the audio streams. However ffmpeg implementation is not perfect at the moment and some (many) of the streams might play broken or not play at all.

This app is using http://radio-browser.info public API

# Download

Download the latest release from [Releases](https://github.com/Joseph5610/broadcastsFX/releases) page in this repository.
Currently we provide several release files:
- If you live on the edge, you can download plain JAR file, but beware, the JAR file can only be run using **Java 1.8**.
- Otherwise, we currently provide native .app file for macOS

:warning: **Native format for Windows/Unix OS is currently not built** 
Those builds have not been tested, but you can still try and build it yourself.


# Build

> We recommend using IntelliJ IDEA as IDE for this project. 

To build the app yourself you only need to setup a few things:

- Clone the repository
- Open existing gradle project from the location where you downloaded the files 
- Run **jfxNative** gradle task

Build output is present in build/jfx/ directory. 
