package prelich.jsketchmobile;

import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.*;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
/*
public class View2 extends LinearLayout implements Observer {
	
	private Model model;

	RadioGroup tools_radio1;
	RadioGroup tools_radio2;
	RadioGroup colors_radio1;
	RadioGroup colors_radio2;
	private boolean isChecking = true;

	ListView thickness_listview;
	CustomAdapter thickness_adapter;

	public View2(Context context, Model m) {
		super(context);
	    Log.d("MVC", "View2 constructor");

		// get the xml description of the view and "inflate" it
		// into the display (kind of like rendering it)
		View.inflate(context, R.layout.view2, this);

		// save the model reference
		model = m;
		// add this view to model's list of observers
		model.addObserver(this);

		tools_radio1 = (RadioGroup) findViewById(R.id.tools_radio1);
		tools_radio2 = (RadioGroup) findViewById(R.id.tools_radio2);
		tools_radio1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId != -1 && isChecking) {
					isChecking = false;
					tools_radio2.clearCheck();

					switch (checkedId) {
						case R.id.selectRadioButton:
							model.setCurrentTool(tool.SELECT); break;
						case R.id.eraseRadioButton:
							model.setCurrentTool(tool.ERASE); break;
						case R.id.fillRadioButton:
							model.setCurrentTool(tool.FILL); break;
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
							model.setCurrentTool(tool.LINE); break;
						case R.id.rectangleRadioButton:
							model.setCurrentTool(tool.RECTANGLE); break;
						case R.id.circleRadioButton:
							model.setCurrentTool(tool.CIRCLE); break;
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
							model.setCurrentColor(Color.BLACK); break;
						case R.id.redRadioButton:
							model.setCurrentColor(Color.RED); break;
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
							model.setCurrentColor(Color.YELLOW); break;
						case R.id.blueRadioButton:
							model.setCurrentColor(Color.BLUE); break;
					}
				}
				isChecking = true;
			}
		});

		thickness_listview = (ListView)findViewById(R.id.listView);
		thickness_listview.setAdapter(new CustomAdapter(context, model));
		thickness_adapter = (CustomAdapter) thickness_listview.getAdapter();

	}

	// the model call this to update the view
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

	    Log.d("MVC", "update View2");
	}
}
*/