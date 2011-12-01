package com.amieggs.game;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class BoardThread extends Thread {
	private SurfaceHolder _surfaceHolder;
	static final long FPS = 10;
	private BoardView _view;
	private boolean _running = false;
    
    public BoardThread(SurfaceHolder surfaceHolder, BoardView view) {
          this._view = view;
          this._surfaceHolder = surfaceHolder;
    }

    public void setRunning(boolean run) {
          _running = run;
    }
    
    public SurfaceHolder getSurfaceHolder() {
        return _surfaceHolder;
    }

    @Override
    public void run() {
    	long ticksPS = 1000 / FPS;
        long startTime;
        long sleepTime;
        while (_running) {
               Canvas c = null;
               startTime = System.currentTimeMillis();
               try {
                      c = _surfaceHolder.lockCanvas();
                      synchronized (_surfaceHolder) {
                             _view.onDraw(c);
                      }
               } finally {
                      if (c != null) {
                             _surfaceHolder.unlockCanvasAndPost(c);
                      }
               }
               sleepTime = ticksPS-(System.currentTimeMillis() - startTime);
               try {
                      if (sleepTime > 0)
                             sleep(sleepTime);
                      else
                             sleep(10);
               } catch (Exception e) {}
        }
    }
}
