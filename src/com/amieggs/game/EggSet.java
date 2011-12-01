package com.amieggs.game;

import java.util.Random;

import com.amieggs.datamanagers.SelectedEggsManager;
import com.amieggs.game.Sprite.Coordinates;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;

public class EggSet {

	private int numColumns = 6;
    private int numRows = 7;
    
	private Bitmap background;
    
    /** What to draw for the eggs in normal state */
    private Bitmap[] eggImages = new Bitmap[6];
    
    /** the actual eggs */
    private Sprite[][] boardImages = new Sprite[numRows][numColumns];
    private int[][] checkGroupsMatrix = new int[numRows][numColumns];
    private boolean thereAreImagesToReplace = false;
    
    //the actual coordinates for all positions
    private float[] rows = new float[numRows];
    private float[] columns = new float[numColumns];
    
    private float gridWidth;
    private float gridHeight;
    private float columnWidth;
    private float rowHeight;
    
    private int velocity = 15;
    
    private Random randomGenerator = new Random();
    
    public EggSet(Context context, float gridH, float gridW) {
    	gridHeight = gridH;
    	gridWidth = gridW;
    	
    	rowHeight = gridHeight/numRows;
    	columnWidth = gridWidth/numColumns;
    	
    	SelectedEggsManager sem = new SelectedEggsManager(context);
    	
    	background = BitmapFactory.decodeResource(context.getResources(), R.drawable.grid);
    	
    	// cache handles to our key sprites & other drawables
    	int[] selectedEggs = sem.getSelectedEggs();
    	for(int i = 0; i < selectedEggs.length; ++i){
    		eggImages[i] = BitmapFactory.decodeResource(context.getResources(), selectedEggs[i]);
    	}
        
      	//calculate coordinates
    	for(int i = 0; i < numRows; ++i){
			rows[i] = rowHeight/2 + rowHeight*i;
		}
		for(int i = 0; i < numColumns; ++i){
			columns[i] = columnWidth/2 + columnWidth*i;
		}
      	populateBoard();
    }
    
    public void populateBoard() {
		for(int row = 0; row < numRows; ++row){
			for(int column = 0; column < numColumns; ++column){
				addNewEggToPosition(row,column);
			}
		}
	}
    
    public void setMatrix(int[][] matrix) {
    	for(int row = 0; row < numRows; ++row){
    		for(int column = 0; column < numColumns; ++column){
    			boardImages[row][column].setEggIndex(matrix[row][column]);
    			boardImages[row][column].setImage(eggImages[matrix[row][column]]);
    			checkGroupsMatrix[row][column] = matrix[row][column];
    		}
    	}
    }
    
    public int[][] getMatrix() {
    	return checkGroupsMatrix;
    }
    
    public boolean replaceShouldBeDone(){
    	return thereAreImagesToReplace;
    }
    
    private void addNewEggToPosition(int row, int column){
    	int randomIndex = randomGenerator.nextInt(6);
		while(!correctInitialPlacement(row,column,randomIndex)){
			randomIndex = randomGenerator.nextInt(6);
		}
		if(rowHeight > 0 && (eggImages[randomIndex].getWidth() > columnWidth)){
			float smallestDimension = columnWidth-6;
			if(rowHeight < columnWidth) smallestDimension = rowHeight;
			eggImages[randomIndex] = Bitmap.createScaledBitmap(eggImages[randomIndex], (int)smallestDimension, (int)smallestDimension, true);
		}
		Sprite s = new Sprite(randomIndex, eggImages[randomIndex]);
		s.setRowAndColumn(row, column);
		//calcular coordenades correctes
		s.getCoordinates().setCenterY(rows[row]);
		s.getCoordinates().setCenterX(columns[column]);
		s.getTargetCoordinates().setCenterY(rows[row]);
		s.getTargetCoordinates().setCenterX(columns[column]);
		boardImages[row][column] = s;
		checkGroupsMatrix[row][column] = randomIndex;
    }

	private Boolean correctInitialPlacement(int row, int column, int egg){
		if (row > 1){
			if(egg==checkGroupsMatrix[row-1][column] && egg==checkGroupsMatrix[row-1][column]){
				return false;
			}
		}
		if (column > 1){
			if(egg==checkGroupsMatrix[row][column-1] && egg==checkGroupsMatrix[row][column-2]){
				return false;
			}
		}
		return true;
	}
	
	public boolean possibilitiesStillAvailable() {
		boolean found = false;
		for(int row = 1; row < numRows; ++row){
			for(int column = 1; column < numColumns; ++column){
				int egg = checkGroupsMatrix[row][column];
				if(groupComponentOnPositionWithIgnoring(row-1,column,egg,row,column) || groupComponentOnPositionWithIgnoring(row,column-1,egg,row,column)){
					found = true;
					//System.out.println("possibility at " + row + "/" + column);
					break;
				}
			}
		}
		if(!found){
			System.out.println();
		}
		return found;
	}
    
    public void onDraw(Canvas canvas) {
    	//canvas.drawColor(Color.parseColor(getResources().getString(R.color.background)));
    	RectF size = new RectF(0,0,gridWidth,gridHeight);
    	canvas.drawBitmap(background, null, size, null);
    	Bitmap bitmap;
        Coordinates coords;
        Sprite touched = null;
        for(int i = 0; i < numRows; ++i) {
        	for (int j = 0; j < numColumns; ++j){
        		if(!boardImages[i][j].isMoving()){
	        		//pintem primer tots els que no s—n el seleccionat
	        		bitmap = boardImages[i][j].getImage();
		        	coords = boardImages[i][j].getCoordinates();
		        	canvas.drawBitmap(bitmap, coords.getX(), coords.getY(), null);
	        	}
        		else touched = boardImages[i][j];
        	}
        	
        }
        if(touched != null){
        	//pintem el seleccionat a sobre de tot
        	bitmap = touched.getImage();
	        coords = touched.getCoordinates();
	        canvas.drawBitmap(bitmap, coords.getX(), coords.getY(), null);
        }
    }
    
    public Sprite spriteOnCoordinates(float x, float y){
    	int column = (int)(x/columnWidth);
    	int row = (int)(y/rowHeight);
    	return spriteFromPosition(row, column);
    }
    
    public Sprite spriteFromPosition(int row, int column){
    	return boardImages[row][column];
    }
    
    public synchronized void exchangePositions(Sprite s1, Sprite s2){
		int tmpRow = s1.getRow();
		int tmpColumn = s1.getColumn();
		s1.setRowAndColumn(s2.getRow(), s2.getColumn());
		s2.setRowAndColumn(tmpRow, tmpColumn);
		boardImages[s1.getRow()][s1.getColumn()] = s1;
		checkGroupsMatrix[s1.getRow()][s1.getColumn()] = s1.getEggIndex();
		boardImages[s2.getRow()][s2.getColumn()] = s2;
		checkGroupsMatrix[s2.getRow()][s2.getColumn()] = s2.getEggIndex();
    }
    
    public int[] findGroupsAndErase() {
    	
    	int[] deleted = new int[6];
    	//esborrem totes les altres crides programades
    	
    	//System.out.println("New board:");
    	for (int row = 0; row < numRows; ++row){
    		for(int column = 0; column < numColumns; ++column){
    			if(groupComponentOnPosition(row,column)){
	    			boardImages[row][column].setReplace(true);
	    			deleted[boardImages[row][column].getEggIndex()]++;
	    			thereAreImagesToReplace = true;
	    		}
    		}
    	}
    	return deleted;
    }
    
    public boolean groupComponentOnPosition(int row, int column){
    	int eggIndex = checkGroupsMatrix[row][column];
    	if(row > 1 && eggIndex == checkGroupsMatrix[row-1][column] && eggIndex == checkGroupsMatrix[row-2][column]){
    		return true;
    	}
    	if(row > 0 && row < numRows-1 && eggIndex == checkGroupsMatrix[row-1][column] && eggIndex == checkGroupsMatrix[row+1][column]) {
    		return true;
    	}
    	if(row < numRows-2 && eggIndex == checkGroupsMatrix[row+1][column] && eggIndex == checkGroupsMatrix[row+2][column]){
    		return true;
    	}
    	if(column > 1 && eggIndex == checkGroupsMatrix[row][column-1] && eggIndex == checkGroupsMatrix[row][column-2]){
    		return true;
    	}
    	if(column > 0 && column < numColumns-1 && eggIndex == checkGroupsMatrix[row][column-1] && eggIndex == checkGroupsMatrix[row][column+1]) {
    		return true;
    	}
    	if(column < numColumns-2 && eggIndex == checkGroupsMatrix[row][column+1] && eggIndex == checkGroupsMatrix[row][column+2]){
    		return true;
    	}
    	return false;
    }
    
    public boolean groupComponentOnPositionWithIgnoring(int row, int column, int eggIndex, int ignoredRow, int ignoredColumn){
    	if(row > 1 && eggIndex == checkGroupsMatrix[row-1][column] && eggIndex == checkGroupsMatrix[row-2][column]){
    		if(row-1 != ignoredRow || column != ignoredColumn) return true;
    	}
    	if(row > 0 && row < numRows-1 && eggIndex == checkGroupsMatrix[row-1][column] && eggIndex == checkGroupsMatrix[row+1][column]) {
    		if((row-1 != ignoredRow || column != ignoredColumn)&&(row+1 != ignoredRow || column != ignoredColumn)) return true;
    	}
    	if(row < numRows-2 && eggIndex == checkGroupsMatrix[row+1][column] && eggIndex == checkGroupsMatrix[row+2][column]){
    		if(row+1 != ignoredRow || column != ignoredColumn) return true;
    	}
    	if(column > 1 && eggIndex == checkGroupsMatrix[row][column-1] && eggIndex == checkGroupsMatrix[row][column-2]){
    		if(row != ignoredRow || column-1 != ignoredColumn) return true;
    	}
    	if(column > 0 && column < numColumns-1 && eggIndex == checkGroupsMatrix[row][column-1] && eggIndex == checkGroupsMatrix[row][column+1]) {
    		if((row != ignoredRow || column-1 != ignoredColumn)&&(row != ignoredRow || column+1 != ignoredColumn)) return true;
    	}
    	if(column < numColumns-2 && eggIndex == checkGroupsMatrix[row][column+1] && eggIndex == checkGroupsMatrix[row][column+2]){
    		if(row != ignoredRow || column+1 != ignoredColumn) return true;
    	}
    	return false;
    }
    
    /**
     * Marca tots els ous que formen part de grups i haurien de desapareixer.
     * @return
     */
    public synchronized int calculateReplacements(){
    	int iterations = 1;
		int elements = 0;
		while(thereAreImagesToReplace){
			int notReplaced = 0;
    		for(int row = numRows-1; row > 0; --row){
    			for(int column = 0; column < numColumns; ++column){
    				if(boardImages[row][column].shouldBeReplaced() && !boardImages[row-1][column].shouldBeReplaced()){
    					Sprite replaced = boardImages[row][column];
    					boardImages[row][column] = boardImages[row-1][column];
    					checkGroupsMatrix[row][column] = boardImages[row][column].getEggIndex();
    					boardImages[row][column].setRowAndColumn(row, column);
    					boardImages[row][column].getTargetCoordinates().setCenterX(columns[column]);
    					boardImages[row][column].getTargetCoordinates().setCenterY(rows[row]);
    					boardImages[row-1][column] = replaced;
    				}
    				else if (boardImages[row][column].shouldBeReplaced()){
    					++notReplaced;
    				}
    			}
    		}
    		
    		//iterem sobre la primera fila
    		for(int column = 0; column < numColumns; ++column){
    			if(boardImages[0][column].shouldBeReplaced()){
    				int randomIndex = randomGenerator.nextInt(6);
    				Sprite s = new Sprite(randomIndex, eggImages[randomIndex]);
    				s.setRowAndColumn(0, column);
    				//calcular coordenades correctes
    				s.getCoordinates().setCenterY(rows[0]-iterations*rowHeight);
    				s.getCoordinates().setCenterX(columns[column]);
    				s.getTargetCoordinates().setCenterY(rows[0]);
    				s.getTargetCoordinates().setCenterX(columns[column]);
    				boardImages[0][column] = s;
    				checkGroupsMatrix[0][column] = randomIndex;
    				
    				++elements;
    			}
    		}
    		
    		if(notReplaced == 0) thereAreImagesToReplace = false;
    		++iterations;
    		
		}
        
        return elements;
    }
    
    public void updatePhysics() {
    	for (int row = 0; row < numRows; ++row){
    		for(int column = 0; column < numColumns; ++column){
    			Sprite s = boardImages[row][column];
	    		if(!s.isMoving()){
	    			Coordinates actual = s.getCoordinates();
		    		Coordinates target = s.getTargetCoordinates();
		    		if(actual.getX() != target.getX() || actual.getY() != target.getY()){
		    			if(target.getX() > actual.getX()){
			    			s.getCoordinates().setX(actual.getX() + velocity);
			    		} else if(target.getX() < actual.getX()){
			    			s.getCoordinates().setX(actual.getX() - velocity);
			    		}
			    		if (target.getY() > actual.getY()){
			    			s.getCoordinates().setY(actual.getY() + velocity);
			    		} else if (target.getY() < actual.getY()){
			    			s.getCoordinates().setY(actual.getY() - velocity);
			    		}
			    		
			    		if((target.getX()-actual.getX()) < velocity && (target.getX()-actual.getX()) > -velocity){
			    			actual.setX(target.getX());
			    		}
			    		if ((target.getY()-actual.getY()) < velocity && (target.getY()-actual.getY()) > -velocity) {
			    			actual.setY(target.getY());
			    		}
		    		}
	    		}
    		}
    		
    	}
    }
}
