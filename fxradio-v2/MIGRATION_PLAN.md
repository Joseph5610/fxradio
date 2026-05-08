# FXRadio v2 Migration Plan

This document outlines the strategy for migrating the FXRadio application from its legacy Kotlin/TornadoFX implementation to a modern, cross-platform stack using Tauri 2.0, React, and Rust.

## 1. Technology Stack

*   **Frontend:** React (Vite) + TypeScript
*   **Styling:** Tailwind CSS + shadcn/ui (using **Base UI** primitives)
*   **Desktop Shell:** Tauri 2.0 (Rust)
*   **State Management:** Zustand (Global State) & TanStack Query (Server State/Caching)
*   **Audio Engine:** HTML5 Audio API (Browser/Tauri) + Rust-based ICY Metadata extraction (Tauri)
*   **Persistence:** LocalStorage/IndexedDB (Web) & Tauri Plugin SQL (Desktop)
*   **Deployment:** Cloudflare Pages (Web/PWA), Native installers (macOS, Windows, Linux)

## 2. Architectural Mapping

| Feature | Legacy (Kotlin/TornadoFX) | Modern (React/Tauri/Rust) |
| :--- | :--- | :--- |
| **UI Framework** | TornadoFX / JavaFX | React + shadcn/ui (Base UI) |
| **API Client** | `api-client` (OkHttp/Retrofit) | `src/services/api` (Fetch + TanStack Query) |
| **Logic/Business** | `usecase` package | `src/hooks` and `src/services` |
| **State** | `viewmodel` (TornadoFX ViewModel) | `src/store` (Zustand) |
| **Audio** | VLCJ / Humble Video | HTML5 Audio + Rust ICY Metadata |
| **Styling** | Type-safe CSS (TornadoFX Styles) | Tailwind CSS (macOS-inspired theme) |
| **Database** | SQLite (JDBC) | Tauri SQL Plugin (Desktop) / LocalStorage (Web) |
| **Events** | TornadoFX EventBus | Custom Hooks / Zustand / Tauri Events |

## 3. Migration Strategy (Phased Approach)

### Phase 1: Foundation (Complete)
*   [x] Initialize `fxradio-v2` directory.
*   [x] Bootstrap Tauri 2.0 + React + TS.
*   [x] Set up Tailwind CSS and basic macOS shell.
*   [x] Implement basic macOS-style layout shell (Sidebar + Main View + Player Bar).

### Phase 2: Core Data & API (Complete)
*   [x] Port `api-client` models to TypeScript interfaces.
*   [x] Implement Radio Browser API integration.
*   [x] Implement basic global state with Zustand.

### Phase 3: Audio & Metadata (In Progress)
*   [x] Build the React Audio Engine (`useAudio`).
*   [x] Implement Rust command in Tauri to fetch ICY metadata.
*   [ ] Enhance Rust metadata service to be streaming/event-based.

### Phase 4: Persistence & Favorites
*   [ ] Implement "Favorites" logic (Zustand + Persistence).
*   [ ] Add "Pinned Countries" feature.

### Phase 5: UI/UX Refinement (macOS Look)
*   [ ] Implement translucent sidebar (Vibrancy on macOS).
*   [ ] Implement shadcn-like components using Base UI primitives.

### Phase 6: PWA & Deployment
*   [x] Configure PWA manifest and service workers.
*   [ ] Set up Cloudflare Pages CI/CD.

## 4. Key Improvements
*   **Weight:** Significantly smaller binary size than Java + VLC.
*   **Performance:** Native WebKit rendering on macOS.
*   **Portability:** True Web version via PWA.
*   **Modern DX:** Hot Module Replacement, Type safety, and a vast ecosystem of React components.
