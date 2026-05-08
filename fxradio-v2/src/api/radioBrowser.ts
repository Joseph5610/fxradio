import { Station, Country, Tag, Stats } from "../types/api";

const USER_AGENT = "FXRadio/2.0 (https://hudacek.online/fxradio)";

class RadioBrowserService {
  private baseUrl: string | null = null;

  private async getBaseUrl(): Promise<string> {
    if (this.baseUrl) return this.baseUrl;

    // In a real app, we might want to resolve this via DNS or a list of mirrors
    // For now, we use a reliable entry point
    this.baseUrl = "https://de1.api.radio-browser.info/json";
    return this.baseUrl;
  }

  private async request<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
    const base = await this.getBaseUrl();
    const response = await fetch(`${base}${endpoint}`, {
      ...options,
      headers: {
        "User-Agent": USER_AGENT,
        ...options.headers,
      },
    });

    if (!response.ok) {
      throw new Error(`Radio Browser API error: ${response.statusText}`);
    }

    return response.json();
  }

  async getTopStations(limit = 100): Promise<Station[]> {
    return this.request<Station[]>(`/stations/topclick/${limit}`);
  }

  async searchStations(query: string, limit = 100): Promise<Station[]> {
    return this.request<Station[]>(`/stations/byname/${encodeURIComponent(query)}?limit=${limit}`);
  }

  async getStationsByCountry(countryCode: string, limit = 100): Promise<Station[]> {
    return this.request<Station[]>(`/stations/bycountrycodeexact/${countryCode}?limit=${limit}`);
  }

  async getCountries(): Promise<Country[]> {
    return this.request<Country[]>("/countries");
  }

  async getTags(): Promise<Tag[]> {
    return this.request<Tag[]>("/tags");
  }

  async getStats(): Promise<Stats> {
    return this.request<Stats>("/stats");
  }

  async voteForStation(uuid: string): Promise<{ ok: boolean; message: string }> {
    return this.request<{ ok: boolean; message: string }>(`/vote/${uuid}`, { method: "POST" });
  }
}

export const radioBrowserService = new RadioBrowserService();
