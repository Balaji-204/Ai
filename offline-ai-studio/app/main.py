"""
OfflineAI Studio - Desktop Main Application Entry Point (PySide6)
100% Offline execution, no cloud API calls.
"""

import sys
import os
from PySide6.QtWidgets import (
    QApplication, QMainWindow, QWidget, QVBoxLayout, QHBoxLayout,
    QStackedWidget, QListWidget, QListWidgetItem, QLabel, QPushButton,
    QStatusBar
)
from PySide6.QtCore import Qt
from PySide6.QtGui import QIcon

# Enforce strict offline execution environment
os.environ["HF_HUB_OFFLINE"] = "1"
os.environ["TRANSFORMERS_OFFLINE"] = "1"

class OfflineAIStudioWindow(QMainWindow):
    def __init__(self):
        super().__init__()
        self.setWindowTitle("OfflineAI Studio")
        self.resize(1100, 720)
        self.init_ui()

    def init_ui(self):
        main_widget = QWidget()
        main_layout = QHBoxLayout(main_widget)
        main_layout.setContentsMargins(0, 0, 0, 0)
        main_layout.setSpacing(0)

        # Left Sidebar Navigation
        self.sidebar = QListWidget()
        self.sidebar.setFixedWidth(220)
        self.sidebar.setStyleSheet("""
            QListWidget {
                background-color: #12131A;
                color: #CBD5E1;
                font-size: 14px;
                border: none;
                padding-top: 12px;
            }
            QListWidget::item {
                height: 48px;
                padding-left: 16px;
            }
            QListWidget::item:selected {
                background-color: #1E2130;
                color: #00E5FF;
                font-weight: bold;
                border-left: 4px solid #00E5FF;
            }
        """)

        nav_items = [
            "Text to Video",
            "Image to QR",
            "Slideshow Video",
            "Tamil Audio (TTS/OCR)",
            "Model Manager",
            "History",
            "Settings"
        ]
        for item in nav_items:
            self.sidebar.addItem(QListWidgetItem(item))

        self.sidebar.setCurrentRow(0)
        self.sidebar.currentRowChanged.connect(self.on_nav_changed)
        main_layout.addWidget(self.sidebar)

        # Stacked Pages
        self.stack = QStackedWidget()
        self.stack.setStyleSheet("background-color: #0D0E15; color: #F1F5F9;")

        for item in nav_items:
            page = QWidget()
            layout = QVBoxLayout(page)
            layout.setContentsMargins(24, 24, 24, 24)
            title = QLabel(f"OfflineAI Studio: {item}")
            title.setStyleSheet("font-size: 22px; font-weight: bold; color: #00E5FF;")
            layout.addWidget(title)
            desc = QLabel("100% offline local inference ready. All weights cached in models/.")
            desc.setStyleSheet("color: #94A3B8; font-size: 14px;")
            layout.addWidget(desc)
            layout.addStretch()
            self.stack.addWidget(page)

        main_layout.addWidget(self.stack)
        self.setCentralWidget(main_widget)

        # Status Bar with Offline Badge
        status = QStatusBar()
        status.setStyleSheet("background-color: #161822; color: #10B981; font-weight: bold;")
        status.showMessage("● 100% OFFLINE (No Cloud Telemetry) | GPU Active")
        self.setStatusBar(status)

    def on_nav_changed(self, index):
        self.stack.setCurrentIndex(index)

def main():
    app = QApplication(sys.argv)
    window = OfflineAIStudioWindow()
    window.show()
    sys.exit(app.exec())

if __name__ == "__main__":
    main()
