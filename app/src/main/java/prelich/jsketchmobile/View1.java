package prelich.jsketchmobile;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Handler;
import android.util.*;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;

public class View1 extends LinearLayout implements Observer {
	
	private Model model;

	RadioGroup tools_radio1;
	RadioGroup tools_radio2;
	RadioGroup colors_radio1;
	RadioGroup colors_radio2;
	private boolean isChecking = true;

	ListView thickness_listview;
	CustomAdapter thickness_adapter;

	CanvasView canvas;
	int startX, startY;

	// Custom View that renders the shapes in the model and handles input for drawing shapes
	public class CanvasView extends View {
		private Paint drawPaint;
		private ScaleGestureDetector scaleDetector;

		public CanvasView(Context context) {
			super(context);
			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
			setFocusable(true);
			setFocusableInTouchMode(true);
			setupPaint();

			scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
		}

		private void setupPaint() {
			drawPaint = new Paint();
			drawPaint.setAntiAlias(true);
			drawPaint.setStyle(Paint.Style.STROKE);
			drawPaint.setStrokeJoin(Paint.Join.ROUND);
			drawPaint.setStrokeCap(Paint.Cap.ROUND);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			// Draw all shapes in the shape list
			ArrayList<MyShape> shapeList = model.getShapeList();
			for (MyShape shape: shapeList) {

				int shapeThickness = (shape.thickness+1)*3;
				tool thisShapeType = shape.shapeType;

				if (shape.fillColor != 0) {
					drawPaint.setStyle(Paint.Style.FILL);
					drawPaint.setColor(shape.fillColor);
					switch (thisShapeType){
						case RECTANGLE:
							canvas.drawRect(shape.x1, shape.y1, shape.x2, shape.y2, drawPaint);
							break;
						case CIRCLE:
							canvas.drawOval(shape.x1, shape.y1, shape.x2, shape.y2, drawPaint);
							break;
					}
				}

				drawPaint.setStyle(Paint.Style.STROKE);
				drawPaint.setStrokeWidth(shapeThickness);

				if(shape == model.getSelectedShape()) {
					drawPaint.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
				}

				drawPaint.setColor(shape.strokeColor);
				switch (thisShapeType){
					case LINE:
						canvas.drawLine(shape.x1, shape.y1, shape.x2, shape.y2, drawPaint);
						break;
					case RECTANGLE:
						canvas.drawRect(shape.x1, shape.y1, shape.x2, shape.y2, drawPaint);
						break;
					case CIRCLE:
						canvas.drawOval(shape.x1, shape.y1, shape.x2, shape.y2, drawPaint);
						break;
				}
				drawPaint.setPathEffect(null);
			}

			// Also draw the shape currently being drawn
			if(model.getDrawingShape() != null) {
				drawPaint.setStrokeWidth((model.getCurrentThickness()+1)*3);
				drawPaint.setColor(model.getCurrentColor());

				MyShape currentShape = model.getDrawingShape();
				tool currentTool = model.getCurrentTool();
				switch (currentTool){
					case LINE:
						canvas.drawLine(currentShape.x1, currentShape.y1, currentShape.x2, currentShape.y2, drawPaint);
						break;
					case RECTANGLE:
						canvas.drawRect(currentShape.x1, currentShape.y1, currentShape.x2, currentShape.y2, drawPaint);
						break;
					case CIRCLE:
						canvas.drawOval(currentShape.x1, currentShape.y1, currentShape.x2, currentShape.y2, drawPaint);
						break;
				}
			}
		}

		private class ScaleListener	extends ScaleGestureDetector.SimpleOnScaleGestureListener {
			@Override
			public boolean onScale(ScaleGestureDetector detector) {

				model.scaleSelectedShape(detector.getScaleFactor());

				invalidate();
				return true;
			}
		}

		// This handler is used to detect tap and hold gestures for clearing the canvas
		// When the user presses down with the erase tool, if they move or release before 1 second,
		// the handler is removed as the tap and hold gesture was not intended
		final Handler handler = new Handler();
		Runnable mLongPressed = new Runnable() {
			public void run() {
				model.clearAllShapes();
			}
		};

		@Override
		public boolean onTouchEvent(MotionEvent event) {

			scaleDetector.onTouchEvent(event);

			int x = (int) event.getX();
			int y = (int) event.getY();

			if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
				startX = x;
				startY = y;

				if(model.getCurrentTool() == tool.CIRCLE || model.getCurrentTool() == tool.RECTANGLE || model.getCurrentTool() == tool.LINE) {
					model.drawingShape(0,0,0,0);
				}
				else if (model.getCurrentTool() == tool.SELECT) {
					model.selectShape(x, y);
				}
				else if (model.getCurrentTool() == tool.ERASE) {
					handler.postDelayed(mLongPressed, 1000);
					model.eraseShape(x, y);
				}
				else if (model.getCurrentTool() == tool.FILL) {
					model.fillShape(x, y);
				}
			}
			else if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
				if(model.getCurrentTool() == tool.CIRCLE || model.getCurrentTool() == tool.RECTANGLE || model.getCurrentTool() == tool.LINE) {
					model.drawingShape(startX, startY, x, y);
				}
				else if (model.getCurrentTool() == tool.SELECT) {
					if (event.getPointerCount() == 1) {
						model.moveSelectedShape(x, y);
					}
				}
				else if (model.getCurrentTool() == tool.ERASE) {
					handler.removeCallbacks(mLongPressed);
				}
			}
			else if (event.getActionMasked() == MotionEvent.ACTION_UP) {
				if(model.getCurrentTool() == tool.CIRCLE || model.getCurrentTool() == tool.RECTANGLE || model.getCurrentTool() == tool.LINE) {
					model.addShape();
				}
				else if (model.getCurrentTool() == tool.ERASE) {
					handler.removeCallbacks(mLongPressed);
				}
			}
			postInvalidate();
			return true;
		}
	}


	public View1(Context context, Model m) {
		super(context);
	    Log.d("MVC", "View1 constructor");
		
		// get the xml description of the view and "inflate" it
		// into the display (kind of like rendering it)
		View.inflate(context, R.layout.view, this);

		model = m;
		model.addObserver(this);

		canvas = new CanvasView(context);
		ViewGroup v1 = (ViewGroup) findViewById(R.id.canvasHolder);
		v1.addView(canvas);

		tools_radio1 = (RadioGroup) findViewById(R.id.tools_radio1);
		tools_radio2 = (RadioGroup) findViewById(R.id.tools_radio2);

		// There are 2 rows/columns of tool and color radio buttons so we clear the selected button
		// from the other group whenever the user checks a new tool or color
		tools_radio1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId != -1 && isChecking) {
					isChecking = false;
					tools_radio2.clearCheck();

					switch (checkedId) {
						case R.id.selectRadioButton:
							model.setCurrentTool(tool.SELECT);
							model.clearSelectedShape();break;
						case R.id.eraseRadioButton:
							model.setCurrentTool(tool.ERASE);
							model.clearSelectedShape(); break;
						case R.id.fillRadioButton:
							model.setCurrentTool(tool.FILL);
							model.clearSelectedShape(); break;
					}
				}
				isChecking = true;
			}
		});
		tools_radio2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId != -1 && isChecking) {
					isChecking = false;
					tools_radio1.clearCheck();

					switch (checkedId) {
						case R.id.lineRadioButton:
							model.setCurrentTool(tool.LINE);
							model.clearSelectedShape();break;
						case R.id.rectangleRadioButton:
							model.setCurrentTool(tool.RECTANGLE);
							model.clearSelectedShape(); break;
						case R.id.circleRadioButton:
							model.setCurrentTool(tool.CIRCLE);
							model.clearSelectedShape(); break;
					}
				}
				isChecking = true;
			}
		});

		colors_radio1 = (RadioGroup) findViewById(R.id.colors_radio1);
		colors_radio2 = (RadioGroup) findViewById(R.id.colors_radio2);
		colors_radio1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId != -1 && isChecking) {
					isChecking = false;
					colors_radio2.clearCheck();

					switch (checkedId) {
						case R.id.blackRadioButton:
							model.setCurrentColor(Color.BLACK);
							if(model.getCurrentTool() == tool.SELECT) {
								model.setSelectedShapeColor(Color.BLACK);
							}
							break;
						case R.id.redRadioButton:
							model.setCurrentColor(Color.RED);
							if(model.getCurrentTool() == tool.SELECT) {
								model.setSelectedShapeColor(Color.RED);
							}
							break;
					}
				}
				isChecking = true;
			}
		});
		colors_radio2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId != -1 && isChecking) {
					isChecking = false;
					colors_radio1.clearCheck();

					switch (checkedId) {
						case R.id.yellowRadioButton:
							model.setCurrentColor(Color.YELLOW);
							if(model.getCurrentTool() == tool.SELECT) {
								model.setSelectedShapeColor(Color.YELLOW);
							}
							break;
						case R.id.blueRadioButton:
							model.setCurrentColor(Color.BLUE);
							if(model.getCurrentTool() == tool.SELECT) {
								model.setSelectedShapeColor(Color.BLUE);
							}
							break;
					}
				}
				isChecking = true;
			}
		});

		thickness_listview = (ListView)findViewById(R.id.listView);
		// Use a custom adapter to render the different line thicknesses in the list
		thickness_listview.setAdapter(new CustomAdapter(context, model));
		thickness_adapter = (CustomAdapter) thickness_listview.getAdapter();
	}

	// The model calls this to update the view
	public void update(Observable observable, Object data) {
		switch (model.getCurrentTool()) {
			case SELECT:
				tools_radio1.check(R.id.selectRadioButton); break;
			case ERASE:
				tools_radio1.check(R.id.eraseRadioButton); break;
			case FILL:
				tools_radio1.check(R.id.fillRadioButton); break;
			case LINE:
				tools_radio2.check(R.id.lineRadioButton); break;
			case RECTANGLE:
				tools_radio2.check(R.id.rectangleRadioButton); break;
			case CIRCLE:
				tools_radio2.check(R.id.circleRadioButton); break;
		}

		switch (model.getCurrentColor()) {
			case Color.BLACK:
				colors_radio1.check(R.id.blackRadioButton); break;
			case Color.RED:
				colors_radio1.check(R.id.redRadioButton); break;
			case Color.YELLOW:
				colors_radio2.check(R.id.yellowRadioButton); break;
			case Color.BLUE:
				colors_radio2.check(R.id.blueRadioButton); break;
		}

		thickness_adapter.setSelected(model.getCurrentThickness());

		canvas.postInvalidate();

	    Log.d("MVC", "update View1");
	}
}
