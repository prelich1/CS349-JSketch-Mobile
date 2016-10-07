package prelich.jsketchmobile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import java.util.ArrayList;

// Code implements MVC architecture. I used the provided MVC example from the slides as my
// base code and modified it.

public class MainActivity extends AppCompatActivity {
    Model model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity);

        // Setup model
        model = new Model();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        View1 view1 = new View1(this, model);
        ViewGroup v1 = (ViewGroup) findViewById(R.id.mainactivity_1);
        v1.addView(view1);

        model.setChanged();
        model.notifyObservers();
    }

    // Save and restore state (need to do this to support orientation change)

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d("MVC", "save state");

        // Save the canvas
        outState.putParcelableArrayList("ShapeList", model.getShapeList());

        // Save the selected tool, color, and thickness for the toolbar
        switch (model.getCurrentTool()) {
            case SELECT:
                outState.putInt("Tool", 0); break;
            case ERASE:
                outState.putInt("Tool", 1); break;
            case FILL:
                outState.putInt("Tool", 2); break;
            case LINE:
                outState.putInt("Tool", 3); break;
            case RECTANGLE:
                outState.putInt("Tool", 4); break;
            case CIRCLE:
                outState.putInt("Tool", 5); break;
        }
        outState.putInt("Color", model.getCurrentColor());
        outState.putInt("Thickness", model.getCurrentThickness());

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d("MVC", "restore state");
        super.onRestoreInstanceState(savedInstanceState);

        // Reload the canvas
        ArrayList<MyShape> list = savedInstanceState.getParcelableArrayList("ShapeList");
        model.loadShapes(list);

        // Reload the selected tool, color, and thickness for the toolbar
        switch (savedInstanceState.getInt("Tool")) {
            case 0:
                model.setCurrentTool(tool.SELECT); break;
            case 1:
                model.setCurrentTool(tool.ERASE); break;
            case 2:
                model.setCurrentTool(tool.FILL); break;
            case 3:
                model.setCurrentTool(tool.LINE); break;
            case 4:
                model.setCurrentTool(tool.RECTANGLE); break;
            case 5:
                model.setCurrentTool(tool.CIRCLE); break;
        }
        model.setCurrentColor(savedInstanceState.getInt("Color"));
        model.setCurrentThickness(savedInstanceState.getInt("Thickness"));
    }
}
