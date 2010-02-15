package com.biggo.AndroidGMEPlayer;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

/*The original implementation was taken from Google Groups 
Android Developers courtesy of a post by Marc Lester Tan
with some custom modifications.*/ 
public class SeekBarPreference extends DialogPreference
{

    private Context context;
    private SeekBar seekBar;
    private TextView text;
    private float minValue;
    private float maxValue;
    private float defaultValue;
    private int precision;
    private float increment;
    private String units;
    private float value;
    private String format;

    public SeekBarPreference(Context context, AttributeSet attrs) 
    {
        super(context, attrs);
        this.context = context;
        int count = attrs.getAttributeCount();
        if(count >= 9)
        {
        	defaultValue = attrs.getAttributeFloatValue(3, 1);
        	precision = attrs.getAttributeIntValue(4, 1);
        	minValue = attrs.getAttributeFloatValue(5, 0);
        	maxValue = attrs.getAttributeFloatValue(6, 10);
        	increment = attrs.getAttributeFloatValue(7, 1);
        	units = attrs.getAttributeValue(8);
        	format = "%1$." + precision + "f " + units; 
        }
    }

    protected void onPrepareDialogBuilder(Builder builder) 
    {

		LinearLayout layout = new LinearLayout(context);
		layout.setLayoutParams(new
		LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		layout.setMinimumWidth(400);
		layout.setPadding(20, 20, 20, 20);
		layout.setOrientation(LinearLayout.VERTICAL);
		
		text = new TextView(context);
    	value = getPersistedFloat(defaultValue);
		text.setText(String.format(format, value));
		
		seekBar = new SeekBar(context);
        seekBar.setMax(100);
        seekBar.setOnSeekBarChangeListener(seekBarListener);
		seekBar.setLayoutParams(new
		ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		double percent = (value - minValue) / (maxValue);
		int newProgress = (int)(percent * 100);
		seekBar.setProgress(newProgress); // You might want to fetch the value here from sharedpreferences*
		
		layout.addView(text);
		
		layout.addView(seekBar);
		
		builder.setView(layout);
		
		super.onPrepareDialogBuilder(builder);
    }

    protected void onDialogClosed(boolean positiveResult) {
        if(positiveResult){
        	persistFloat(value);
        }
        else {
        	value = getPersistedFloat(defaultValue);
        }
    }
    
    private OnSeekBarChangeListener seekBarListener = new OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar seek, int progress, boolean fromUser) {
			if(fromUser)
			{
				double percent = progress / 100.0;
				double inputValue = (float) ((maxValue - minValue) * percent);
				inputValue = (inputValue -(inputValue % increment)) + minValue;
				percent = inputValue / maxValue;
				value = (float) inputValue;
				text.setText(String.format(format , value));
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			
		}
    	
    };

} 