package com.thecodewarrior.guides.api;

import java.util.List;

import com.thecodewarrior.guides.guides.Guide;
import com.thecodewarrior.guides.guides.elements.GuideElement;

public class GuideNull extends Guide {

	public GuideNull() {
		super(null);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean parse() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public String viewName() {
		return "NULL";
	}

	@Override
	public List<GuideElement> getGuideElements(int width, int height) {
		// TODO Auto-generated method stub
		return null;
	}

}
