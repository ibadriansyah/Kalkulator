#!/usr/bin/env python3
"""Pure-Python PNG generator (minimal) for Kalkulator launcher icons."""

import struct
import zlib
import os

def make_png(width, height, rgba):
    raw = b""
    for y in range(height):
        raw += b"\x00"
        for x in range(width):
            r, g, b, a = rgba[y][x]
            raw += struct.pack("BBBB", r, g, b, a)

    def chunk(tag, data):
        return struct.pack(">I", len(data)) + tag + data + struct.pack(">I", zlib.crc32(tag + data) & 0xFFFFFFFF)

    sig = b"\x89PNG\r\n\x1a\n"
    ihdr = struct.pack(">IIBBBBB", width, height, 8, 6, 0, 0, 0)
    return sig + chunk(b"IHDR", ihdr) + chunk(b"IDAT", zlib.compress(raw, 9)) + chunk(b"IEND", b"")

def rounded_rect(size, x1, y1, x2, y2, fill, border=None, border_w=1):
    """Generate RGBA pixels for a rounded rectangle."""
    rx, ry = x2 - x1, y2 - y1
    rw, rh = rx / size, ry / size
    px = [[fill for _ in range(size)] for _ in range(size)]

    for y in range(size):
        for x in range(size):
            if x1 <= x <= x2 and y1 <= y <= y2:
                px_ = x - x1
                py = y - y1
                # Skip if outside rounded corner
                if (px < 0 or px >= rx or py < 0 or py >= ry):
                    px[y][x] = (0, 0, 0, 0)
                    continue
                # Corner check
                corner = min(px, py)
                if corner < rx * 0.25:
                    a = int(255 * (corner / (rx * 0.25)))
                    px[y][x] = (int(fill[0]*a/255), int(fill[1]*a/255), int(fill[2]*a/255), a if border is None else 0)
    return px

def generate_icons():
    output_dir = "/data/data/com.termux/files/home/kalkulator/app/src/main/res"
    for density, size in [("mdpi", 48), ("hdpi", 72), ("xhdpi", 96), ("xxhdpi", 144), ("xxxhdpi", 192)]:
        # ic_launcher: main adaptive icon
        for name in ["ic_launcher", "ic_launcher_round"]:
            # Main: purple rounded rect with white display
            pixels = [[(0, 0, 0, 0) for _ in range(size)] for _ in range(size)]

            # Border rounded rect (deep purple)
            for y in range(size):
                for x in range(size):
                    # Main body: use simple filled approach
                    pass

            # Simple purple background rounded rect
            bx1, by1 = int(size * 0.12), int(size * 0.12)
            bx2, by2 = int(size * 0.88), int(size * 0.88)
            for y in range(size):
                for x in range(size):
                    if bx1 <= x <= bx2 and by1 <= y <= by2:
                        # Rounded corner check
                        px, py = x - bx1, y - by1
                        corner = min(px, py, bx2 - x, by2 - y)
                        if corner < (bx2 - bx1) * 0.2:
                            a = int(255 * (corner / ((bx2 - bx1) * 0.2)))
                            pixels[y][x] = (124 * a // 255, 58 * a // 255, 237 * a // 255, a)
                        else:
                            pixels[y][x] = (124, 58, 237, 255)

            # Display screen (white)
            sx1, sy1 = int(size * 0.35), int(size * 0.25)
            sx2, sy2 = int(size * 0.65), int(size * 0.55)
            for y in range(size):
                for x in range(size):
                    if sx1 <= x <= sx2 and sy1 <= y <= sy2:
                        pixels[y][x] = (248, 247, 255, 255)

            # Display border
            for y in range(size):
                for x in range(size):
                    if (sx1 - 1 <= x <= sx1 or sx2 <= x <= sx2 + 1 or
                        sy1 - 1 <= y <= sy1 or sy2 <= y <= sy2 + 1):
                        pixels[y][x] = (124, 58, 237, 255)

            # Glossy highlight (top-left of body)
            for y in range(size):
                for x in range(size):
                    if bx1 <= x <= bx2 and by1 <= y <= by2:
                        px, py = x - bx1, y - by1
                        if px < (bx2 - bx1) * 0.3 and py < (by2 - by1) * 0.3:
                            h = 1.0 - (px / ((bx2 - bx1) * 0.3))
                            pixels[y][x] = (
                                int(124 * 0.6 + 255 * h * 0.4),
                                int(58 * 0.6 + 138 * h * 0.4),
                                int(237 * 0.6 + 58 * h * 0.4),
                                255
                            )

            path = os.path.join(output_dir, "mipmap-" + density, name + ".png")
            os.makedirs(os.path.dirname(path), exist_ok=True)
            with open(path, "wb") as f:
                f.write(make_png(size, size, pixels))
            print(f"Created {path}")

if __name__ == "__main__":
    generate_icons()
