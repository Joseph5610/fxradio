import { useState, useMemo } from "react";
import { useQuery } from "@tanstack/react-query";
import { radioBrowserService } from "./api/radioBrowser";
import { usePlayerStore, useFavoritesStore, useRecentStore } from "./store/useStore";
import { useAudio } from "./hooks/useAudio";
import { Search, Heart, Globe, Tag as TagIcon, History, Play, Pause, SkipBack, SkipForward, Volume2, Music } from "lucide-react";
import { cn } from "./lib/utils";

type View = "browse" | "favorites" | "countries" | "tags" | "recent";

function App() {
  const { currentStation, isPlaying, volume, metadata, setCurrentStation, togglePlay, setVolume } = usePlayerStore();
  const { favorites, isFavorite, addFavorite, removeFavorite } = useFavoritesStore();
  const { recent, addRecent } = useRecentStore();
  const [view, setView] = useState<View>("browse");
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedItem, setSelectedItem] = useState<string | null>(null);

  // Initialize audio engine
  useAudio();

  const { data: stations, isLoading: isLoadingStations } = useQuery({
    queryKey: ["stations", view, searchQuery, selectedItem],
    queryFn: () => {
      if (searchQuery) {
        return radioBrowserService.searchStations(searchQuery, 50);
      }
      if (view === "browse") {
        return radioBrowserService.getTopStations(50);
      }
      if (view === "countries" && selectedItem) {
        return radioBrowserService.getStationsByCountry(selectedItem, 50);
      }
      return Promise.resolve([]);
    },
    enabled: view === "browse" || !!searchQuery || (view === "countries" && !!selectedItem),
  });

  const { data: countries, isLoading: isLoadingCountries } = useQuery({
    queryKey: ["countries"],
    queryFn: () => radioBrowserService.getCountries(),
    enabled: view === "countries",
  });

  const { data: tags, isLoading: isLoadingTags } = useQuery({
    queryKey: ["tags"],
    queryFn: () => radioBrowserService.getTags(),
    enabled: view === "tags",
  });

  const displayStations = useMemo(() => {
    if (searchQuery) return stations || [];
    if (view === "favorites") return favorites;
    if (view === "recent") return recent;
    if (view === "browse") return stations || [];
    if (view === "countries" && selectedItem) return stations || [];
    return [];
  }, [view, favorites, recent, stations, searchQuery, selectedItem]);

  const isLoading = isLoadingStations || (view === "countries" && !selectedItem && isLoadingCountries) || (view === "tags" && isLoadingTags);

  return (
    <div className="flex h-screen w-full bg-background text-foreground overflow-hidden font-sans select-none">
      {/* Sidebar - macOS inspired */}
      <aside className="w-64 bg-muted/40 border-r border-border backdrop-blur-xl flex flex-col">
        <div className="p-6 pb-2">
          <div className="flex items-center space-x-2">
            <img src="/logo.png" alt="FXRadio" className="w-8 h-8" />
            <h1 className="text-xl font-bold tracking-tight text-primary">FXRadio</h1>
          </div>
        </div>

        <nav className="flex-1 px-3 py-4 space-y-0.5">
          <div className="px-3 py-2 text-[11px] font-bold text-muted-foreground uppercase tracking-widest opacity-50">Library</div>
          <SidebarItem
            icon={<Heart className="w-4 h-4" />}
            label="Favorites"
            active={view === "favorites"}
            onClick={() => { setView("favorites"); setSearchQuery(""); }}
            count={favorites.length}
          />
          <SidebarItem
            icon={<History className="w-4 h-4" />}
            label="Recently Played"
            active={view === "recent"}
            onClick={() => { setView("recent"); setSearchQuery(""); }}
          />

          <div className="px-3 py-2 mt-4 text-[11px] font-bold text-muted-foreground uppercase tracking-widest opacity-50">Discover</div>
          <SidebarItem
            icon={<Search className="w-4 h-4" />}
            label="Browse"
            active={view === "browse" && !searchQuery}
            onClick={() => { setView("browse"); setSearchQuery(""); }}
          />
          <SidebarItem
            icon={<Globe className="w-4 h-4" />}
            label="Countries"
            active={view === "countries"}
            onClick={() => { setView("countries"); setSearchQuery(""); }}
          />
          <SidebarItem
            icon={<TagIcon className="w-4 h-4" />}
            label="Tags"
            active={view === "tags"}
            onClick={() => { setView("tags"); setSearchQuery(""); }}
          />
        </nav>

        <div className="p-4 border-t border-border/50">
           {currentStation && (
             <div className="flex items-center space-x-3 p-2 rounded-lg bg-primary/5 border border-primary/10">
                <div className="w-10 h-10 rounded bg-primary/10 flex items-center justify-center overflow-hidden">
                  {currentStation.favicon ? <img src={currentStation.favicon} alt="" className="w-full h-full object-cover" /> : <div className="text-[10px] text-primary/40 font-bold">FX</div>}
                </div>
                <div className="flex-1 min-w-0">
                  <div className="text-xs font-bold truncate">{currentStation.name}</div>
                  <div className="text-[10px] text-muted-foreground truncate">{currentStation.country}</div>
                </div>
             </div>
           )}
        </div>
      </aside>

      {/* Main Content */}
      <main className="flex-1 flex flex-col min-w-0 relative">
        <header className="h-12 border-b border-border/40 flex items-center px-6 justify-between bg-background/80 backdrop-blur-md sticky top-0 z-10">
          <div className="flex items-center space-x-2">
            {selectedItem && (
              <button
                onClick={() => setSelectedItem(null)}
                className="p-1 hover:bg-muted rounded text-muted-foreground"
              >
                <SkipBack className="w-4 h-4" />
              </button>
            )}
            <div className="text-[13px] font-semibold">
              {searchQuery ? `Search results for "${searchQuery}"` : selectedItem || view.charAt(0).toUpperCase() + view.slice(1)}
            </div>
          </div>
          <div className="relative group">
             <Search className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-muted-foreground" />
             <input
              type="search"
              placeholder="Search stations..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="h-7 w-48 rounded-md border border-border/50 bg-muted/30 pl-8 pr-3 py-1 text-[12px] transition-all focus:w-64 focus:bg-background focus:ring-1 focus:ring-primary/20"
             />
          </div>
        </header>

        <div className="flex-1 overflow-auto p-8 custom-scrollbar">
          {isLoading ? (
            <div className="flex flex-col items-center justify-center h-full text-muted-foreground space-y-4">
              <div className="w-8 h-8 border-2 border-primary border-t-transparent rounded-full animate-spin"></div>
              <p className="text-sm font-medium">Loading...</p>
            </div>
          ) : (view === "countries" && !selectedItem && !searchQuery) ? (
            <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-6 gap-4">
              {countries?.filter(c => c.stationcount > 10).map((country) => (
                <button
                  key={country.name}
                  onClick={() => setSelectedItem(country.iso_3166_1)}
                  className="p-4 rounded-xl bg-muted/30 hover:bg-muted/60 transition-colors text-center"
                >
                  <div className="text-2xl mb-1">{getFlagEmoji(country.iso_3166_1)}</div>
                  <div className="text-xs font-bold truncate">{country.name}</div>
                  <div className="text-[10px] text-muted-foreground">{country.stationcount} stations</div>
                </button>
              ))}
            </div>
          ) : (view === "tags" && !searchQuery) ? (
            <div className="flex flex-wrap gap-2">
               {tags?.filter(t => t.stationcount > 50).map((tag) => (
                 <button
                  key={tag.name}
                  onClick={() => {
                    setSearchQuery(tag.name);
                    setView("browse");
                  }}
                  className="px-3 py-1.5 rounded-full bg-muted/50 hover:bg-primary hover:text-primary-foreground transition-all text-xs font-medium"
                 >
                   #{tag.name} <span className="opacity-50 ml-1">{tag.stationcount}</span>
                 </button>
               ))}
            </div>
          ) : displayStations.length === 0 ? (
            <div className="flex flex-col items-center justify-center h-full text-muted-foreground space-y-2 opacity-50">
              <Music className="w-12 h-12 stroke-1" />
              <p className="text-sm font-medium">No stations found</p>
            </div>
          ) : (
            <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-6">
              {displayStations.map((station) => (
                <div
                  key={station.stationuuid}
                  className={cn(
                    "group relative flex flex-col items-center text-center space-y-3 p-4 rounded-xl transition-all duration-200 hover:bg-muted/50 cursor-default",
                    currentStation?.stationuuid === station.stationuuid && "bg-primary/5"
                  )}
                  onClick={() => {
                    setCurrentStation(station);
                    addRecent(station);
                  }}
                >
                  <div className="relative aspect-square w-full rounded-2xl bg-muted shadow-sm flex items-center justify-center overflow-hidden group-hover:shadow-md transition-shadow">
                    {station.favicon ? (
                      <img
                        src={station.favicon}
                        alt={station.name}
                        className="w-full h-full object-cover"
                        onError={(e) => {
                          (e.target as HTMLImageElement).src = 'https://www.google.com/s2/favicons?domain=' + new URL(station.url_resolved).hostname + '&sz=128';
                        }}
                      />
                    ) : (
                      <Globe className="w-1/3 h-1/3 text-muted-foreground/30" />
                    )}
                    <div className="absolute inset-0 bg-black/40 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center">
                       <button className="w-12 h-12 rounded-full bg-white text-black flex items-center justify-center transform scale-90 group-hover:scale-100 transition-transform shadow-xl">
                          <Play className="w-6 h-6 fill-current ml-1" />
                       </button>
                    </div>
                    <button
                      onClick={(e) => {
                        e.stopPropagation();
                        isFavorite(station.stationuuid) ? removeFavorite(station.stationuuid) : addFavorite(station);
                      }}
                      className={cn(
                        "absolute top-2 right-2 p-1.5 rounded-full backdrop-blur-md opacity-0 group-hover:opacity-100 transition-opacity z-10",
                        isFavorite(station.stationuuid) ? "bg-red-500 text-white opacity-100" : "bg-black/20 text-white hover:bg-black/40"
                      )}
                    >
                      <Heart className={cn("w-3 h-3", isFavorite(station.stationuuid) && "fill-current")} />
                    </button>
                  </div>
                  <div className="w-full px-1">
                    <h3 className="text-[13px] font-bold leading-tight truncate">{station.name}</h3>
                    <p className="text-[11px] text-muted-foreground mt-0.5 truncate">{station.tags.split(',')[0] || station.country}</p>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Player Bar - macOS Float style */}
        <footer className="h-20 border-t border-border/30 bg-background/90 backdrop-blur-xl flex items-center px-8 z-20">
          <div className="flex items-center space-x-4 w-[30%]">
             <div className="h-12 w-12 rounded-lg bg-muted flex-shrink-0 overflow-hidden shadow-sm border border-border/20">
                {currentStation?.favicon ? (
                  <img src={currentStation.favicon} alt="" className="w-full h-full object-cover" />
                ) : (
                  <div className="w-full h-full flex items-center justify-center text-muted-foreground/20">
                     <Globe className="w-6 h-6" />
                  </div>
                )}
             </div>
             <div className="min-w-0">
               <div className="text-[13px] font-bold truncate tracking-tight">{currentStation?.name || "Not Playing"}</div>
               <div className="text-[11px] text-muted-foreground truncate opacity-70">{currentStation ? currentStation.country : "Select a station to start listening"}</div>
             </div>
          </div>

          <div className="flex-1 flex flex-col items-center justify-center px-4">
            <div className="flex items-center space-x-6 mb-1">
              <button className="text-muted-foreground/40 cursor-default p-1">
                <SkipBack className="w-5 h-5 fill-current" />
              </button>
              <button
                onClick={togglePlay}
                disabled={!currentStation}
                className="h-10 w-10 bg-primary text-primary-foreground rounded-full flex items-center justify-center hover:scale-105 active:scale-95 transition-all shadow-md disabled:opacity-50 disabled:hover:scale-100"
              >
                {isPlaying ? <Pause className="w-5 h-5 fill-current" /> : <Play className="w-5 h-5 fill-current ml-0.5" />}
              </button>
              <button className="text-muted-foreground/40 cursor-default p-1">
                <SkipForward className="w-5 h-5 fill-current" />
              </button>
            </div>

            <div className="w-full max-w-md flex flex-col items-center">
               <div className="text-[12px] font-medium text-foreground/90 truncate w-full text-center h-5">
                 {isPlaying && metadata ? (
                    <div className="animate-in fade-in slide-in-from-bottom-1 duration-500">{metadata}</div>
                 ) : isPlaying ? (
                    <div className="text-muted-foreground/60 text-[10px] uppercase tracking-widest flex items-center justify-center space-x-2">
                       <span className="flex h-1.5 w-1.5 rounded-full bg-primary animate-pulse"></span>
                       <span>Live Stream</span>
                    </div>
                 ) : null}
               </div>
            </div>
          </div>

          <div className="w-[30%] flex justify-end items-center space-x-3">
            <Volume2 className="w-4 h-4 text-muted-foreground" />
            <input
              type="range"
              min="0"
              max="1"
              step="0.01"
              value={volume}
              onChange={(e) => setVolume(parseFloat(e.target.value))}
              className="w-24 accent-primary"
            />
            <div className="w-px h-4 bg-border/50 mx-2"></div>
            <button className="p-1.5 rounded hover:bg-muted transition-colors">
               <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="text-muted-foreground"><path d="M3 18v-6a9 9 0 0 1 18 0v6"/><path d="M21 19a2 2 0 0 1-2 2h-1a2 2 0 0 1-2-2v-3a2 2 0 0 1 2-2h3zM3 19a2 2 0 0 0 2 2h1a2 2 0 0 0 2-2v-3a2 2 0 0 0-2-2H3z"/></svg>
            </button>
          </div>
        </footer>
      </main>
    </div>
  );
}

function getFlagEmoji(countryCode: string) {
  if (!countryCode) return "🌐";
  const codePoints = countryCode
    .toUpperCase()
    .split("")
    .map((char) => 127397 + char.charCodeAt(0));
  return String.fromCodePoint(...codePoints);
}

function SidebarItem({ icon, label, active = false, count, onClick }: { icon: React.ReactNode; label: string; active?: boolean; count?: number; onClick?: () => void }) {
  return (
    <div
      onClick={onClick}
      className={cn(
      "flex items-center justify-between px-3 py-1.5 rounded-md text-[13px] font-medium transition-all cursor-default group",
      active ? "bg-primary text-primary-foreground shadow-sm" : "text-foreground/70 hover:bg-muted/60 hover:text-foreground"
    )}>
      <div className="flex items-center space-x-2.5">
        <span className={cn("transition-colors", active ? "text-primary-foreground" : "text-primary/60 group-hover:text-primary")}>{icon}</span>
        <span>{label}</span>
      </div>
      {count !== undefined && count > 0 && (
        <span className={cn("text-[11px] px-1.5 py-0.5 rounded-full font-bold", active ? "bg-white/20" : "bg-muted text-muted-foreground")}>{count}</span>
      )}
    </div>
  );
}

export default App;
