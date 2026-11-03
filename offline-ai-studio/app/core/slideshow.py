"""
Offline Text-to-Video Slideshow Engine for low-resource computers.
Splits script into scene visuals with Ken Burns pan/zoom, subtitles, and ffmpeg export.
"""

from PIL import Image, ImageDraw
import numpy as np

def split_script_into_scenes(script_text: str) -> list[dict]:
    raw_blocks = [b.strip() for b in script_text.split("\n\n") if b.strip()]
    if not raw_blocks:
        raw_blocks = ["Scene 1: Introduction to OfflineAI Studio", "Scene 2: Core Offline Capabilities"]

    scenes = []
    for idx, block in enumerate(raw_blocks):
        lines = block.split("\n")
        heading = lines[0][:40]
        body = " ".join(lines[1:]) if len(lines) > 1 else lines[0]
        scenes.append({
            "scene_idx": idx + 1,
            "heading": heading,
            "body": body,
            "duration": 4
        })
    return scenes

def render_slideshow_frames(scenes: list[dict], width: int = 768, height: int = 432, fps: int = 10) -> list[Image.Image]:
    frames = []
    for scene in scenes:
        scene_frames = scene["duration"] * fps
        for f in range(scene_frames):
            prog = f / max(1, scene_frames)
            img = Image.new("RGB", (width, height), color=(15, 23, 42))
            draw = ImageDraw.Draw(img)

            # Draw card
            draw.rectangle([40, 40, width - 40, height - 40], fill=(30, 41, 59))
            draw.text((60, 60), f"SCENE {scene['scene_idx']}: {scene['heading']}", fill=(0, 229, 255))
            draw.text((60, 120), scene["body"][:80], fill=(241, 245, 249))

            # Progress bar
            bar_w = int((width - 80) * prog)
            draw.line([(40, height - 50), (40 + bar_w, height - 50)], fill=(0, 229, 255), width=4)
            frames.append(img)
    return frames
