# FXRadio v2 (Modern)

Modern Internet Radio Directory app built with Tauri 2.0, React, and Rust.

## 🚀 How to Run & Build in VS Code

### 1. Prerequisites
Ensure you have the following installed on your system:
- **Node.js** (v18+) & **npm**
- **Rust** (via [rustup](https://rustup.rs/))
- **System Dependencies** (Tauri requirements for your OS): [Prerequisites Guide](https://tauri.app/v2/guides/prerequisites/)

### 2. Recommended Extensions
When you open this folder in VS Code, you should be prompted to install recommended extensions. Make sure you have:
- **Tauri** (tauri-apps.tauri-vscode)
- **rust-analyzer** (rust-lang.rust-analyzer)
- **Tailwind CSS IntelliSense** (bradlc.vscode-tailwindcss)

### 3. Running the App
You can run the app directly from VS Code using **Tasks**:
1. Open the Command Palette (`Cmd+Shift+P` / `Ctrl+Shift+P`).
2. Type `Tasks: Run Task`.
3. Select:
   - **`Tauri Dev`**: Launches the native desktop app with hot-reload.
   - **`Web Dev`**: Launches the web version in your browser (`localhost:1420`).

### 4. Building the App
To create a production build:
1. Open the Command Palette.
2. Type `Tasks: Run Task`.
3. Select:
   - **`Tauri Build`**: Generates a native installer (`.dmg` on macOS, `.msi` on Windows).
   - **`Web Build`**: Generates a static site in the `dist/` folder (ready for Cloudflare Pages).

## 📁 Project Structure
- `src/`: React frontend (UI, logic, hooks).
- `src-tauri/`: Rust backend (native features, metadata extraction).
- `MIGRATION_PLAN.md`: Detailed strategy for the rewrite.

## 🛠 Tech Stack
- **Frontend**: React, TypeScript, Tailwind CSS, Base UI.
- **State**: Zustand, TanStack Query.
- **Desktop**: Tauri 2.0 (Rust).
