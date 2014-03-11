package com.smsc.smartmeter.draw;

import java.net.ContentHandler;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.content.Context;

public class LineChart {
	private GraphicalView chart; 
	private Context context;
	XYMultipleSeriesRenderer renderer;
	XYMultipleSeriesDataset dataset;
	public LineChart(Context context) {

		this.context = context;
	}
	
	private void setDemoRenderer(String title, 
			String xTitle, String format) {
		
	}
	
	private void setDemoDataSet() {
		
	}
}
