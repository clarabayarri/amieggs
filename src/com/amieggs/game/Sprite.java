package com.amieggs.game;

import android.graphics.Bitmap;

public class Sprite{
    private int eggIndex;
    private Bitmap bmp;
    
    private int row;
    private int column;
    private Coordinates coordinates;
    private Coordinates targetCoordinates;
    
    boolean isBeingMoved;
    boolean shouldBeReplaced;
   
    public Sprite (int index, Bitmap bitmap){
    	this.eggIndex = index;
    	this.bmp = bitmap;
    	this.coordinates = new Coordinates();
    	this.targetCoordinates = new Coordinates();
    	this.isBeingMoved = false;
    	this.shouldBeReplaced = false;
    }
    
    public void setEggIndex(int index){
    	this.eggIndex = index;
    }
    
    public void setImage(Bitmap bmp){
    	this.bmp = bmp;
    }
    
    public void setMoving(boolean move){
    	this.isBeingMoved = move;
    }
    
    public void setReplace(boolean replace){
    	this.shouldBeReplaced = replace;
    }
    
    public boolean isMoving() {
    	return this.isBeingMoved;
    }
    
    public boolean shouldBeReplaced() {
    	return this.shouldBeReplaced;
    }
    
    public Bitmap getImage() {
    	return this.bmp;
    }
    
    public Coordinates getCoordinates() {
        return coordinates;
    }
    
    public void setRowAndColumn(int row, int column){
    	this.row = row;
    	this.column = column;
    }
    
    public int getRow() {
    	return this.row;
    }
    
    public int getColumn() {
    	return this.column;
    }
    
    public int getEggIndex() {
    	return this.eggIndex;
    }
    
    public void setTargetCoordinates(float x, float y){
    	this.targetCoordinates.setX(x);
    	this.targetCoordinates.setY(y);
    }
    
    public Coordinates getTargetCoordinates() {
    	return this.targetCoordinates;
    }
    
    public class Coordinates {
    	float _x = 0;
    	float _y = 0;
    	
    	public Coordinates(){
    		_x = 0;
    		_y = 0;
    	}
    	
    	public float getX() {
            return _x;
        }

        public void setX(float value) {
            _x = value;
        }
        
        public void setCenterX(float value){
        	_x = value - bmp.getWidth()/2;
        }

        public float getY() {
            return _y;
        }
        
        public float getCenterX() {
        	return _x + bmp.getWidth()/2;
        }
        
        public float getCenterY() {
        	return _y + bmp.getHeight()/2;
        }

        public void setY(float value) {
            _y = value;
        }
        
        public void setCenterY(float value){
        	_y = value - bmp.getHeight() / 2;
        }

        public String toString() {
            return "Coordinates: (" + _x + "/" + _y + ")";
        }
    }
}
