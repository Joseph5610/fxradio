import { useEffect, useRef } from "react";
import { usePlayerStore } from "../store/useStore";
import { invoke } from "@tauri-apps/api/core";

export function useAudio() {
  const { currentStation, isPlaying, volume, setIsPlaying } = usePlayerStore();
  const audioRef = useRef<HTMLAudioElement | null>(null);

  useEffect(() => {
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
          try {
            const metadata = await invoke("get_icy_metadata", { url: currentStation.url_resolved });
            console.log("Metadata:", metadata);
          } catch (e) {
            console.error("Failed to fetch metadata:", e);
          }
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
    };
  }, [currentStation, isPlaying, setIsPlaying]);

  useEffect(() => {
    if (audioRef.current) {
      audioRef.current.volume = volume;
    }
  }, [volume]);

  return audioRef.current;
}
