import { create } from "zustand";
import { persist } from "zustand/middleware";
import { Station } from "../types/api";

interface PlayerState {
  currentStation: Station | null;
  isPlaying: boolean;
  volume: number;
  metadata: string | null;
  setCurrentStation: (station: Station | null) => void;
  setIsPlaying: (isPlaying: boolean) => void;
  setVolume: (volume: number) => void;
  setMetadata: (metadata: string | null) => void;
  togglePlay: () => void;
}

export const usePlayerStore = create<PlayerState>()(
  persist(
    (set) => ({
      currentStation: null,
      isPlaying: false,
      volume: 0.7,
      metadata: null,
      setCurrentStation: (station) => set({ currentStation: station, isPlaying: !!station, metadata: null }),
      setIsPlaying: (isPlaying) => set({ isPlaying }),
      setVolume: (volume) => set({ volume }),
      setMetadata: (metadata) => set({ metadata }),
      togglePlay: () => set((state) => ({ isPlaying: !state.isPlaying })),
    }),
    {
      name: "fxradio-player-storage",
      partialize: (state) => ({ volume: state.volume }),
    }
  )
);

interface FavoritesState {
  favorites: Station[];
  addFavorite: (station: Station) => void;
  removeFavorite: (stationUuid: string) => void;
  isFavorite: (stationUuid: string) => boolean;
}

export const useFavoritesStore = create<FavoritesState>()(
  persist(
    (set, get) => ({
      favorites: [],
      addFavorite: (station) => {
        if (!get().isFavorite(station.stationuuid)) {
          set((state) => ({ favorites: [...state.favorites, station] }));
        }
      },
      removeFavorite: (uuid) =>
        set((state) => ({
          favorites: state.favorites.filter((s) => s.stationuuid !== uuid),
        })),
      isFavorite: (uuid) => get().favorites.some((s) => s.stationuuid === uuid),
    }),
    {
      name: "fxradio-favorites-storage",
    }
  )
);
