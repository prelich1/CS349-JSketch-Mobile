package prelich.jsketchmobile;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

// Most of the model code was taken from my Assignment 2.
// It was modified to work on Android.

enum tool {
	SELECT, ERASE, LINE, CIRCLE, RECTANGLE, FILL
}

// Class used to store shape data
// Implements Parcelable to be able to save MyShape states on screen rotation
class MyShape implements Parcelable {
	tool shapeType;
	int strokeColor = Color.BLACK;
	int fillColor = 0;
	int thickness = 1;
	float x1, y1, x2, y2;

	MyShape(tool newShapeType, int xStart, int yStart, int xStop, int yStop, int sColor, int newThickness) {
		shapeType = newShapeType;
		strokeColor = sColor;
		thickness = newThickness;
		x1 = xStart;
		y1 = yStart;
		x2 = xStop;
		y2 = yStop;
	};

	protected MyShape(Parcel in) {
		strokeColor = in.readInt();
		fillColor = in.readInt();
		thickness = in.readInt();
		x1 = in.readInt();
		y1 = in.readInt();
		x2 = in.readInt();
		y2 = in.readInt();
	}

	public static final Creator<MyShape> CREATOR = new Creator<MyShape>() {
		@Override
		public MyShape createFromParcel(Parcel in) {
			return new MyShape(in);
		}

		@Override
		public MyShape[] newArray(int size) {
			return new MyShape[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(strokeColor);
		dest.writeInt(fillColor);
		dest.writeInt(thickness);
		dest.writeFloat(x1);
		dest.writeFloat(y1);
		dest.writeFloat(x2);
		dest.writeFloat(y2);
	}
}


public class Model extends Observable {

	private tool currentTool = tool.SELECT;
	private int currentColor = Color.BLACK;
	private Integer currentThickness = 0;	// Based off index of list so start at 0, canvas' onDraw method will handle this correctly
	private MyShape drawingShape = null;
	private MyShape selectedShape = null;
	private ArrayList<MyShape> shapeList = new ArrayList<MyShape>();

	Model() {
	}

	public ArrayList<MyShape> getShapeList() {
		return shapeList;
	}
	public tool getCurrentTool() {
		return currentTool;
	}
	public int getCurrentColor() {
		return currentColor;
	}
	public Integer getCurrentThickness() {
		return currentThickness;
	}
	public MyShape getDrawingShape() {
		return drawingShape;
	}
	public MyShape getSelectedShape() {
		return selectedShape;
	}


	public void setCurrentTool(tool toolName) {
		currentTool = toolName;
		setChanged();
		notifyObservers();
	}

	public void setCurrentColor(int color) {
		currentColor = color;
		setChanged();
		notifyObservers();
	}

	public void setCurrentThickness(int thickness) {
		currentThickness = thickness;
		setChanged();
		notifyObservers();
	}

	public void setSelectedShapeColor(int color) {
		if(selectedShape != null) {
			selectedShape.strokeColor = color;
		}
		setChanged();
		notifyObservers();
	}

	public void setSelectedShapeThickness(int thickness) {
		if(selectedShape != null) {
			selectedShape.thickness = thickness;
		}
		setChanged();
		notifyObservers();
	}

	public void clearAllShapes() {
		shapeList = new ArrayList<MyShape>();
		setChanged();
		notifyObservers();
	}

	public void loadShapes(ArrayList<MyShape> loadedShapeList) {
		shapeList = loadedShapeList;
		setChanged();
		notifyObservers();
	}


	public void drawingShape(int x1, int y1, int x2, int y2) {
		int xStart = Math.min(x1, x2);
		int yStart = Math.min(y1, y2);
		int xStop = Math.max(x1, x2);
		int yStop = Math.max(y1, y2);

		int width = Math.abs(xStart - xStop);
		int height = Math.abs(yStart - yStop);

		if(currentTool == tool.RECTANGLE) {
			drawingShape = new MyShape(tool.RECTANGLE, xStart, yStart, xStop, yStop, currentColor, currentThickness);
		}
		else if(currentTool == tool.CIRCLE) {
			int size = Math.max(width, height);
			drawingShape = new MyShape(tool.CIRCLE, xStart, yStart, xStart+size, yStart+size, currentColor, currentThickness);
		}
		else if(currentTool == tool.LINE) {
			drawingShape = new MyShape(tool.LINE, x1, y1, x2, y2, currentColor, currentThickness);
		}

		setChanged();
		notifyObservers();
	}

	public void addShape() {
		shapeList.add(drawingShape);
		drawingShape = null;
		setChanged();
		notifyObservers();
	}

	// Used for translating shape coordinates
	int prevX;
	int prevY;

	public void clearSelectedShape() {
		selectedShape = null;
		setChanged();
		notifyObservers();
	}

	// Used for bounds checking for shape selection, erasing, and filling
	private boolean checkCollision(MyShape shape, int x, int y) {
		float xStart = Math.min(shape.x1, shape.x2);
		float yStart = Math.min(shape.y1, shape.y2);
		float xStop = Math.max(shape.x1, shape.x2);
		float yStop = Math.max(shape.y1, shape.y2);

		Rect bounds = new Rect((int) xStart, (int) yStart, (int) xStop, (int) yStop);

		return bounds.contains(x, y);
	}

	public void selectShape(int x, int y) {
		prevX = x;
		prevY = y;

		for(int i = shapeList.size()-1; i >= 0; i--) {
			if (checkCollision(shapeList.get(i), x, y)) {
				clearSelectedShape();
				selectedShape = shapeList.get(i);
				currentThickness = selectedShape.thickness;
				currentColor = selectedShape.strokeColor;
				break;
			}
		}
		setChanged();
		notifyObservers();
	}

	public void moveSelectedShape(int x, int y) {
		if(selectedShape != null) {
			int deltaX = x - prevX;
			int deltaY = y - prevY;

			selectedShape.x1 = selectedShape.x1 + deltaX;
			selectedShape.y1 = selectedShape.y1 + deltaY;
			selectedShape.x2 = selectedShape.x2 + deltaX;
			selectedShape.y2 = selectedShape.y2 + deltaY;

			prevX = x;
			prevY = y;
		}
		setChanged();
		notifyObservers();
	}

	public void scaleSelectedShape(float scaleFactor) {
		if(selectedShape != null) {
			float width = Math.abs(selectedShape.x2 - selectedShape.x1);
			float height = Math.abs(selectedShape.y2 - selectedShape.y1);

			// To stop shapes from shrinking into non existence
			if(scaleFactor < 1 && (width <= 2 || height <= 2)) {
				return;
			}

			float deltaWidth = (width * scaleFactor) - width;
			float deltaHeight = (height * scaleFactor) - height;

			if (selectedShape.x1 < selectedShape.x2) {
				selectedShape.x1 -= deltaWidth / 2;
				selectedShape.x2 += deltaWidth / 2;
			}
			else {
				selectedShape.x1 += deltaWidth / 2;
				selectedShape.x2 -= deltaWidth / 2;
			}

			if (selectedShape.y1 < selectedShape.y2) {
				selectedShape.y1 -= deltaHeight / 2;
				selectedShape.y2 += deltaHeight / 2;
			}
			else {
				selectedShape.y1 += deltaHeight / 2;
				selectedShape.y2 -= deltaHeight / 2;
			}
		}
		setChanged();
		notifyObservers();
	}

	public void eraseShape(int x, int y) {
		for(int i = shapeList.size()-1; i >= 0; i--) {
			if (checkCollision(shapeList.get(i), x, y)) {
				shapeList.remove(i);
				break;
			}
		}
		setChanged();
		notifyObservers();
	}

	public void fillShape(int x, int y) {
		for(int i = shapeList.size()-1; i >= 0; i--) {
			if (checkCollision(shapeList.get(i), x, y)) {
				shapeList.get(i).fillColor = currentColor;
				break;
			}
		}
		setChanged();
		notifyObservers();
	}


	// Observer methods
	@Override
	public void addObserver(Observer observer) {
		Log.d("MVC", "Model: Observer added");
		super.addObserver(observer);
	}

	@Override
	public synchronized void deleteObservers() {
		super.deleteObservers();
	}

	@Override
	public void notifyObservers() {
		Log.d("MVC", "Model: Observers notified");
		super.notifyObservers();
	}

	@Override
	protected void setChanged() {
		super.setChanged();
	}

	@Override
	protected void clearChanged() {
		super.clearChanged();
	}

}