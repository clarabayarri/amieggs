package com.amieggs.game;

import java.lang.Math;

import com.amieggs.datamanagers.SelectedEggsManager;
import com.amieggs.game.EggSet;

import android.graphics.Canvas;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

public class BoardView extends SurfaceView implements SurfaceHolder.Callback {

	private GameActivity mainActivity;
	private BoardThread boardThread;
	
    private int numColumns = 6;
    private int numRows = 7;
    private float gridWidth;
    private float gridHeight;
    private float columnWidth;
    private float rowHeight;
    
    private EggSet eggSet = null;
    private int[][] tmpMatrix = null;
    
    private Sprite touched = null;
    private Sprite exchange1 = null;
    private Sprite exchange2 = null;
    
    //the actual coordinates for all positions
    private float[] rows = new float[numRows];
    private float[] columns = new float[numColumns];
    
    private long waitingTime = 300;
    private TextView[] counters = new TextView[6];
    private Handler handler = new Handler();
    
    private Runnable updateCounters = new Runnable() {
    	public void run() {
    		for (int i = 0; i < 6; ++i){
    			counters[i].setText(String.valueOf(currentRemaining[i]));
    		}
    	}
    };
    private Runnable scanBoard = new Runnable() {
    	public void run() {
    		if(!eggSet.possibilitiesStillAvailable()){
    			//mainActivity.onNoMoreMoves();
    		}
    	}
    };
    
    private Runnable findGroups = new Runnable() {
        public void run() {
            // acciones que se ejecutan tras los milisegundos
            int[] deleted = eggSet.findGroupsAndErase();
        	for(int i = 0; i < deleted.length; ++i){
        		if(currentRemaining[i] > deleted[i]) currentRemaining[i] -= deleted[i];
        		else currentRemaining[i] = 0;
        	}
        	updateCounters();
        }
    };
    
    private int[] currentRemaining = new int[6];
    
    SelectedEggsManager sem;
    
	public BoardView(GameActivity context) {
		super(context);
		mainActivity = context;
		getHolder().addCallback(this);
        boardThread = new BoardThread(getHolder(), this);
        sem = new SelectedEggsManager(context);
        setFocusable(true);
	}
	
	public void setCountersAccess(TextView[] counters){
		this.counters = counters;
	}
	
	private void initBoardView() {
		gridWidth = getWidth();
		gridHeight = getHeight();
		columnWidth = gridWidth/numColumns;
		rowHeight = gridHeight/numRows;

		//calculate coordinates
    	for(int i = 0; i < numRows; ++i){
			rows[i] = rowHeight/2 + rowHeight*i;
		}
		for(int i = 0; i < numColumns; ++i){
			columns[i] = columnWidth/2 + columnWidth*i;
		}
		
		eggSet = new EggSet(mainActivity, gridHeight, gridWidth);
		if(tmpMatrix != null)eggSet.setMatrix(tmpMatrix);
	}
	
	public void setMatrix(int[][] matrix){
		if(eggSet != null) eggSet.setMatrix(matrix);
		else tmpMatrix = matrix;
	}
	
	public int[][] getMatrix() {
		return eggSet.getMatrix();
	}

	@Override
	protected void onDraw(Canvas canvas) {
        checkForChanges();
		if(eggSet.replaceShouldBeDone()){
        	int elements = eggSet.calculateReplacements();
        	mainActivity.updateScore(elements);
        	handler.removeCallbacks(scanBoard);
            handler.postDelayed(scanBoard, waitingTime);
            //esperem a que acabin d'entrar i mirem si s'han creat mŽs grups.
    		handler.removeCallbacks(findGroups);
    		handler.postDelayed(findGroups, waitingTime);
        }
		eggSet.updatePhysics();
		eggSet.onDraw(canvas);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
        initBoardView();
		boardThread.setRunning(true);
		boardThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
        boardThread.setRunning(false);
        while (retry) {
            try {
                boardThread.join();
                retry = false;
            } catch (InterruptedException e) {
                // we will try it again and again...
            }
        }
	}
	
	public void startLevel(int newLevel) {
		int counter = newLevel+2;
		for (int i = 0; i < 6; ++i) currentRemaining[i] = counter;
		updateCounters();
		changeBoard();
	}
	
	private void updateCounters() {
		handler.removeCallbacks(updateCounters);
		handler.post(updateCounters);
		boolean finished = true;
		for(int i = 0; i < 6; ++i){
			if(currentRemaining[i] > 0){
				finished = false;
			}
		}
		if(finished){
			mainActivity.onLevelUp();
		}
	}
	
	public void changeBoard() {
		handler.removeCallbacks(scanBoard);
		eggSet = new EggSet(mainActivity, gridHeight, gridWidth);
	}
	
	@Override
    public boolean onTouchEvent(MotionEvent event) {
    	synchronized (boardThread.getSurfaceHolder()) {
    		if (event.getAction() == MotionEvent.ACTION_DOWN) {
    			//agafar les coordenades per averiguar a qui has tocat i posarlo a touched.
    			Sprite s = eggSet.spriteOnCoordinates(event.getX(), event.getY());
				touched = s;
				s.setMoving(true);
    		}
    		else if (event.getAction() == MotionEvent.ACTION_MOVE) {
    			if(Math.abs(event.getX()-columns[touched.getColumn()]) > Math.abs(event.getY()-rows[touched.getRow()])){
    				if(event.getX() >= columnWidth/2 && event.getX() < gridWidth-(columnWidth/2) && Math.abs(event.getX()-columns[touched.getColumn()]) < columnWidth){
        				touched.getCoordinates().setCenterX(event.getX());
        			}
    				touched.getCoordinates().setCenterY(rows[touched.getRow()]);
    			}
    			
    			else {
    				if(event.getY() >= rowHeight/2 && event.getY() < gridHeight-(rowHeight/2) && Math.abs(event.getY()-rows[touched.getRow()]) < rowHeight){
        				touched.getCoordinates().setCenterY(event.getY());
        			}
    				touched.getCoordinates().setCenterX(columns[touched.getColumn()]);
    			}
    			
    		}
    		else if (event.getAction() == MotionEvent.ACTION_UP) {
    			//mirar si s'ha de fer intercanvi
    			exchange1 = touched;
    			exchange2 = eggSet.spriteOnCoordinates(touched.getCoordinates().getCenterX(), touched.getCoordinates().getCenterY());
    			exchange1.setMoving(false);
    			touched = null;
    		}
            return true;
        }
    }
	
	public void checkForChanges() {
		if(exchange1 != null && exchange2 != null){
			if (exchange1.getEggIndex() != exchange2.getEggIndex()) calculateExchange(exchange1,exchange2);
			exchange1 = null;
			exchange2 = null;
		}
	}
	
	public void calculateExchange(Sprite s1, Sprite s2){
		if(eggSet.groupComponentOnPositionWithIgnoring(s1.getRow(), s1.getColumn(), s2.getEggIndex(), s2.getRow(), s2.getColumn()) || eggSet.groupComponentOnPositionWithIgnoring(s2.getRow(), s2.getColumn(), s1.getEggIndex(), s1.getRow(), s1.getColumn())){
			eggSet.exchangePositions(s1,s2);
			s1.getTargetCoordinates().setCenterX(columns[s1.getColumn()]);
			s1.getTargetCoordinates().setCenterY(rows[s1.getRow()]);
			s2.getTargetCoordinates().setCenterX(columns[s2.getColumn()]);
			s2.getTargetCoordinates().setCenterY(rows[s2.getRow()]);
			
			//esperem a que acabi l'intercanvi per eliminar grups
	        handler.postDelayed(findGroups, waitingTime);
			
		}
	}
}
