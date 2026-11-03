"""
SQLite Database Layer for OfflineAI Studio.
Stores history of video generations, QR creations, slideshows, and Tamil audio records.
"""

import sqlite3
from pathlib import Path

DB_PATH = Path.home() / ".offline_ai_studio" / "studio.db"

def init_db():
    DB_PATH.parent.mkdir(parents=True, exist_ok=True)
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.execute("""
        CREATE TABLE IF NOT EXISTS history (
            id TEXT PRIMARY KEY,
            type TEXT,
            title TEXT,
            subtitle TEXT,
            timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
        )
    """)
    conn.commit()
    conn.close()

def insert_history(item_id: str, item_type: str, title: str, subtitle: str):
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.execute(
        "INSERT INTO history (id, type, title, subtitle) VALUES (?, ?, ?, ?)",
        (item_id, item_type, title, subtitle)
    )
    conn.commit()
    conn.close()

def get_history(filter_type: str = None) -> list[dict]:
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    if filter_type and filter_type != "ALL":
        cursor.execute("SELECT id, type, title, subtitle, timestamp FROM history WHERE type = ? ORDER BY timestamp DESC", (filter_type,))
    else:
        cursor.execute("SELECT id, type, title, subtitle, timestamp FROM history ORDER BY timestamp DESC")
    rows = cursor.fetchall()
    conn.close()
    return [{"id": r[0], "type": r[1], "title": r[2], "subtitle": r[3], "timestamp": r[4]} for r in rows]
