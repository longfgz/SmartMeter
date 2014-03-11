package com.smsc.smartmeter.draw;

import java.net.ContentHandler;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.content.Context;

public class LineChart {
	private GraphicalView chart; 
	private String titleString;
	private String xTitleString;
	private Context context;
	private String formatString;
	public LineChart(String title, String xTitle, Context context, String format) {
		this.titleString = title;
		this.xTitleString = xTitle;
	}
	
	private XYMultipleSeriesRenderer getDemoRenderer() {
		return null;
	}
	
	private XYMultipleSeriesDataset getDemoDataSet() {
		return null;
		
	}
}
