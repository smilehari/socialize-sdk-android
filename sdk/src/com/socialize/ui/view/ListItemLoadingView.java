package com.socialize.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.socialize.ui.util.Colors;
import com.socialize.util.DisplayUtils;
import com.socialize.view.BaseView;

/**
 * Renders a simple loading spinner.
 * @author jasonpolites
 *
 */
public class ListItemLoadingView extends BaseView {

	private DisplayUtils displayUtils;
	private Colors colors;
	
	public ListItemLoadingView(Context context) {
		super(context);
	}
	
	public void init() {
//		final int eight = displayUtils.getDIP(8);
		
//		ProgressBar progress = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleSmall);
//		TextView text = new TextView(getContext());
//		
//		progress.setPadding(eight, eight, 0, eight);
//		
//		text.setTextColor(colors.getColor(Colors.BODY));
//		text.setText("Loading...");
//		text.setPadding(eight, eight, eight, eight);
//		
//		ListView.LayoutParams layout = new ListView.LayoutParams(ListView.LayoutParams.FILL_PARENT, ListView.LayoutParams.WRAP_CONTENT);
//		
//		setBackgroundColor();
//		setOrientation(LinearLayout.HORIZONTAL);
//		setLayoutParams(layout);
//		setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
//		setPadding(eight,eight,eight,eight);
//		
//		addView(progress);
//		addView(text);
		
		int padding = displayUtils.getDIP(4);
		
		ListView.LayoutParams masterParams = new ListView.LayoutParams(ListView.LayoutParams.FILL_PARENT, displayUtils.getDIP(48));
		
		setBackgroundColor(colors.getColor(Colors.LOADING_ITEM_BG));
		
		setLayoutParams(masterParams);
		setOrientation(HORIZONTAL);
		
		ProgressBar progress = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleSmall);
		progress.setPadding(padding, padding, padding, padding);
		
		TextView text = new TextView(getContext());
		text.setTextColor(Color.GRAY);
		text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		text.setText("Loading...");
		text.setPadding(0, padding, padding, padding);
		
		LayoutParams progressLayoutParams = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		progressLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		progressLayoutParams.weight = 0.0f;
		
		LayoutParams textParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		textParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
		textParams.weight = 1.0f;
		textParams.setMargins(0, 0, 0, 0);
		
		setLayoutParams(masterParams);
		progress.setLayoutParams(progressLayoutParams);
		text.setLayoutParams(textParams);
		text.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
		
		addView(progress);
		addView(text);		
	}

	public DisplayUtils getDisplayUtils() {
		return displayUtils;
	}

	public void setDisplayUtils(DisplayUtils deviceUtils) {
		this.displayUtils = deviceUtils;
	}

	public Colors getColors() {
		return colors;
	}

	public void setColors(Colors colors) {
		this.colors = colors;
	}
}
