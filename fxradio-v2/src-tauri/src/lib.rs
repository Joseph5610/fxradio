use serde::{Deserialize, Serialize};
use std::io::{Read};
use reqwest::header::{HeaderMap, HeaderValue};

#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct IcyMetadata {
    pub stream_title: Option<String>,
}

mod commands {
    use super::*;
    use std::io::Read;

    #[tauri::command]
    pub fn get_icy_metadata(url: String) -> Result<IcyMetadata, String> {
        let mut headers = HeaderMap::new();
        headers.insert("Icy-MetaData", HeaderValue::from_static("1"));
        headers.insert("User-Agent", HeaderValue::from_static("FXRadio/2.0"));

        let client = reqwest::blocking::Client::new();
        let mut response = client.get(&url)
            .headers(headers)
            .send()
            .map_err(|e| e.to_string())?;

        let metaint = response.headers()
            .get("icy-metaint")
            .and_then(|h| h.to_str().ok())
            .and_then(|s| s.parse::<usize>().ok());

        if let Some(interval) = metaint {
            let mut buffer = vec![0u8; interval];
            response.read_exact(&mut buffer).map_err(|e| e.to_string())?;

            let mut length_byte = [0u8; 1];
            response.read_exact(&mut length_byte).map_err(|e| e.to_string())?;

            let length = length_byte[0] as usize * 16;
            if length > 0 {
                let mut meta_buffer = vec![0u8; length];
                response.read_exact(&mut meta_buffer).map_err(|e| e.to_string())?;

                let meta_str = String::from_utf8_lossy(&meta_buffer);
                // Example: StreamTitle='Song Artist - Song Name';
                if let Some(start) = meta_str.find("StreamTitle='") {
                    let rest = &meta_str[start + 13..];
                    if let Some(end) = rest.find("';") {
                        return Ok(IcyMetadata {
                            stream_title: Some(rest[..end].to_string()),
                        });
                    }
                }
            }
        }

        Ok(IcyMetadata { stream_title: None })
    }
}

#[cfg_attr(mobile, tauri::mobile_entry_point)]
pub fn run() {
    tauri::Builder::default()
        .plugin(tauri_plugin_opener::init())
        .invoke_handler(tauri::generate_handler![commands::get_icy_metadata])
        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}
