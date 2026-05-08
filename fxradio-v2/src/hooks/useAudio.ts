import { useEffect, useRef } from "react";
import { usePlayerStore } from "../store/useStore";
import { invoke } from "@tauri-apps/api/core";

export function useAudio() {
  const { currentStation, isPlaying, volume, setIsPlaying, setMetadata } = usePlayerStore();
  const audioRef = useRef<HTMLAudioElement | null>(null);

  useEffect(() => {
    let metadataInterval: number | null = null;

    const fetchMetadata = async () => {
      if (currentStation && isPlaying) {
        try {
          const metadata = await invoke<{ stream_title: string | null }>("get_icy_metadata", { url: currentStation.url_resolved });
          if (metadata.stream_title) {
            setMetadata(metadata.stream_title);
          }
        } catch (e) {
          console.error("Failed to fetch metadata:", e);
        }
      }
    };

    if (!audioRef.current) {
      audioRef.current = new Audio();
    }

    const audio = audioRef.current;

    const playAudio = async () => {
      if (currentStation && isPlaying) {
        try {
          audio.src = currentStation.url_resolved;
          audio.volume = volume;
          await audio.play();

          // Try to fetch metadata if in Tauri
          fetchMetadata();
          metadataInterval = window.setInterval(fetchMetadata, 15000);
        } catch (error) {
          console.error("Playback failed:", error);
          setIsPlaying(false);
        }
      } else {
        audio.pause();
        audio.src = "";
      }
    };

    playAudio();

    return () => {
      audio.pause();
      audio.src = "";
      if (metadataInterval) clearInterval(metadataInterval);
    };
  }, [currentStation, isPlaying, setIsPlaying, setMetadata]);

  useEffect(() => {
    if (audioRef.current) {
      audioRef.current.volume = volume;
    }
  }, [volume]);

  return audioRef.current;
}
