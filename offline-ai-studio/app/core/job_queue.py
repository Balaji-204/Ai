"""
Thread-safe background worker queue for non-blocking UI task execution.
"""

from PySide6.QtCore import QThread, Signal

class WorkerThread(QThread):
    progress = Signal(int, int, str, int)
    finished = Signal(object)
    failed = Signal(str)

    def __init__(self, task_fn, *args, **kwargs):
        super().__init__()
        self.task_fn = task_fn
        self.args = args
        self.kwargs = kwargs

    def run(self):
        try:
            res = self.task_fn(*self.args, **self.kwargs)
            self.finished.emit(res)
        except Exception as e:
            self.failed.emit(str(e))
