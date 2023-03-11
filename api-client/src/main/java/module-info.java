open module fxradio.api {
    requires okhttp3;
    requires retrofit2;
    exports online.hudacek.fxradio.apiclient;
    exports online.hudacek.fxradio.apiclient.http;
    exports online.hudacek.fxradio.apiclient.http.interceptor;
    exports online.hudacek.fxradio.apiclient.http.provider;
    exports online.hudacek.fxradio.apiclient.musicbrainz;
    exports online.hudacek.fxradio.apiclient.musicbrainz.model;
    exports online.hudacek.fxradio.apiclient.radiobrowser;
    exports online.hudacek.fxradio.apiclient.radiobrowser.model;
}