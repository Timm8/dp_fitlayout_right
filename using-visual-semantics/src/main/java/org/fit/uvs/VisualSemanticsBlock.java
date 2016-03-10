package org.fit.uvs;

import org.fit.layout.model.Box;
import org.fit.segm.grouping.AreaImpl;

public class VisualSemanticsBlock {
	
	private Box block;
	private AreaImpl area;
	
	public void setBlock(Box visualBlock) {
		this.block = visualBlock;
	}
	
	public Box getBlock() {
		return block;
	}
	
	public void setArea(AreaImpl visualBlockArea) {
		this.area = visualBlockArea;
	}
	
	public AreaImpl getArea() {
		return area;
	}
}
