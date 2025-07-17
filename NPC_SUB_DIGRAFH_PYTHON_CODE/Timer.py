#timer.py

import time
class timer:
    def __init__(self):
        self._start_time = None         # atributo privado
        self.elapsed     = 0.0

    def start(self):
        if self._start_time is None:    # evita volver a arrancar sin parar
            self._start_time = time.perf_counter()


    def stop(self):
        if self._start_time is not None:
            self.elapsed = time.perf_counter() - self._start_time
            self._start_time = None
        return self.elapsed