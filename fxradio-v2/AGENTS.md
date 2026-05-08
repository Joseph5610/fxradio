# FXRadio v2 - AI Agent Guidelines

Welcome to the `fxradio-v2` repository. This document provides critical context for any AI assistant working on this codebase.

## 1. Project Context & How It Works
This is a modernization of a legacy Kotlin/TornadoFX app into a modern web/desktop stack.

### 1.1 Tech Stack
- **Frontend**: React 19, TypeScript, Vite
- **Styling**: Tailwind CSS + shadcn/ui concepts (using `@base-ui/react` primitives)
- **Desktop Shell**: Tauri 2.0 (Rust)
- **State**: Zustand (global state) & TanStack Query (API state)
- **Audio**: HTML5 Audio API + Rust ICY Metadata extraction

### 1.2 Core Data Flow & Architecture
- **Data Fetching:** The app talks to the public **Radio Browser API**. All data fetching (searching stations, listing countries, getting top tags) is handled by `src/api/radioBrowser.ts` and managed in React components via **TanStack Query** (`useQuery`).
- **Global State (Zustand):** The current playing station, audio volume, and play/pause status are stored in `src/store/useStore.ts`. This allows any UI component (like the sidebar or the player bar) to control playback.
- **Audio Playback:** Actual playback is managed by a custom hook `useAudio.ts` which listens to the Zustand store. When `currentStation` changes, it updates an underlying `HTMLAudioElement` and plays the stream.
- **Metadata Extraction:** Because internet radio streams (ICY streams) send metadata (the current song name) within the audio stream itself (and often CORS prevents browsers from reading it), the app uses a **Rust Tauri Command** in the background to connect to the stream, extract the icy-metadata, and send it back to the frontend to display the playing song.

## 2. Core Directives for Agents

### 2.1 Architecture & State
- **Zustand**: Used for player state (current station, play/pause, volume) and UI state. See `src/store`.
- **TanStack Query**: Used for data fetching from the Radio Browser API. See `src/hooks` and `src/services/api`.
- **Tauri Integration**: Avoid using generic web APIs when a robust Tauri equivalent is needed for the desktop version (e.g., file system, global shortcuts). Use `@tauri-apps/api`.

### 2.2 Styling & Components
- **Tailwind CSS**: Strict adherence to Tailwind. Avoid custom CSS files unless absolutely necessary.
- **Base UI**: We use `@base-ui/react` for accessible primitive components. Do not implement complex interactive components from scratch if a Base UI primitive exists.
- **Path Aliases**: Always use `@/` for importing from the `src` directory (e.g., `import { Button } from "@/components/ui/button"`).

### 2.3 Migration Status
- We have successfully ported the UI shell, API client, and basic audio playback.
- **Pending Features**: Favorites (persistence), advanced search, custom UI refinements (macOS-like aesthetics).
- Reference `MIGRATION_PLAN.md` for historical context on what was ported.

### 2.4 Best Practices
- **Linting**: We enforce strict ESLint and Prettier rules. If your generated code fails to build, check `npm run lint`.
- **Types**: Maintain strict TypeScript adherence. Avoid `any` types.
- **Rust**: Keep Tauri Rust commands clean and return appropriate serialized results to the frontend.

## 3. Directory Structure
- `src/api`: API client and models.
- `src/components`: React components (UI components should go into `src/components/ui`).
- `src/hooks`: Custom React hooks, including TanStack Query hooks.
- `src/store`: Zustand stores.
- `src/styles`: Global CSS and Tailwind directives.
- `src/types`: TypeScript definitions.
- `src-tauri`: Rust code for the desktop application.
