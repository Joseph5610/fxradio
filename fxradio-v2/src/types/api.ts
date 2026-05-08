export interface Station {
  stationuuid: string;
  name: string;
  url_resolved: string;
  homepage: string;
  favicon: string | null;
  tags: string;
  country: string;
  countrycode: string;
  state: string;
  language: string;
  codec: string;
  bitrate: number;
  votes: number;
  geo_lat: number | null;
  geo_long: number | null;
  clicktrend: number;
  clickcount: number;
  languagecodes: string;
  has_extended_info: boolean;
}

export interface Country {
  name: string;
  iso_3166_1: string;
  stationcount: number;
}

export interface Tag {
  name: string;
  stationcount: number;
}

export interface Stats {
  supported_version: number;
  software_version: string;
  status: string;
  stations: number;
  stations_broken: number;
  tags: number;
  countries: number;
  languages: number;
  users_online: number;
}
