package org.fit.uvs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.fit.layout.api.ParametrizedOperation.ValueType;
import org.fit.layout.impl.BaseOperator;
import org.fit.layout.impl.GenericTreeNode;
import org.fit.layout.model.Area;
import org.fit.layout.model.AreaTree;
import org.fit.layout.model.Box;
import org.fit.layout.model.Box.Type;
import org.fit.layout.model.Rectangular;
import org.fit.segm.grouping.AreaImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VisualSemanticsOperator extends BaseOperator {

        //private static Logger log = LoggerFactory.getLogger(VisualSemantics.class);
        
    protected List<AreaImpl> boxesSet;
    protected float alfa;
    protected float beta;
        
    protected final String[] paramNames = {"alfa", "beta"};
    protected final ValueType[] paramTypes = {ValueType.FLOAT, ValueType.FLOAT};
    
    protected int ClientAreaHalf = 0;
    
    public VisualSemanticsOperator() {
    	boxesSet = new ArrayList<>();
    	alfa = 0.9f;
    	beta = 0.8f;
    }
    
    public VisualSemanticsOperator(float alfa, float beta) {
    	boxesSet = new ArrayList<>();
    	this.alfa = alfa;
    	this.beta = beta;
    }
    
    @Override
    public String getId() {
    	return "FitLayout.Segm.VisualSemantics";
    }

    @Override
    public String getName() {
    	return "UVS";
    }

    @Override
    public String getDescription() {
        return "..."; //TODO
    }

    @Override
    public String[] getParamNames() {
    	return paramNames;
    }

    @Override
    public ValueType[] getParamTypes() {
    	return paramTypes;
    }
        
    public float getAlfa() {
    	return alfa;
    }
    
    public void setAlfa(float alffa) {
    	if (alffa > 0.9 || alffa < 0.4)
    		this.alfa = 0.9f;
    	else
    		this.alfa = alffa;
    }
    
    public float getBeta() {
    	return beta;
    }
    
    public void setBeta(float betta) {
    	if (betta > 0.8 || betta < 0.4)
    		this.beta = 0.8f;
    	else
    		this.beta = betta;
    }
    
    //==============================================================================
        
    @Override
    public void apply(AreaTree atree) {
    	apply(atree, atree.getRoot());
    }

    @Override
    public void apply(AreaTree atree, Area root) {
    	
    	ClientAreaHalf = (root.getWidth() * root.getHeight()) / 2;
    	
        makeSegmentation((AreaImpl) root);
    }

    //==============================================================================

    /**
     * Main function of given algorithm
     * @param root or start Block
     */
    public void makeSegmentation(AreaImpl root) {
        
    	runAlgorithm(root);
		AreaImpl newNode = new AreaImpl(0, 0, 0, 0);
		
		root.removeAllChildren();
    	
    	for (int i = 0; i < boxesSet.size(); i++) {
    		//boxesSet.get(i).removeAllChildren();
    		newNode.appendChild(boxesSet.get(i));
    	}
    	boxesSet.clear();
    	root.appendChild(newNode);
     }
    
       
    /**
     * Function for giving of all blocks
     * @param root start node of DOM Tree
     */
    public void runAlgorithm(AreaImpl root) {
    	
        if (!isDivisible(root)) {
        	boxesSet.add(root);
        }
        else {
        	for (int i = 0; i < root.getChildCount(); i++) {

        		runAlgorithm((AreaImpl) root.getChildArea(i));
        	}
        }
    }
    
    /**
     * Function for testing of it is block divisible
     * @param B given block
     * @return true or false if its block divisible
     */
    public boolean isDivisible(AreaImpl B) {

        //step one
        if (B.getChildCount() == 0) {
            return false;
        }
          
        //krok 2 3 budou přidány později

        
        //step four
        if (B.getChildCount() == 1) {
        	return true;
        }

        //step five
        if (avgSeamDegree(B) < alfa) {
            return true;
        }
        
        //step six
        if (AvgContentSimilarity(B) < beta) {
            return false;
        }
                
        //step seven
        int CurrentBlockArea = B.getWidth() * B.getHeight();
        
        if (CurrentBlockArea > ClientAreaHalf) {
        	return true;
        }
        	
        //step eight
        return false;
    }
        
    /**
     * Function for calculating of Seam Degree with Width of two Blocks
     * @param B1 first block
     * @param B2 second block
     * @return value of Seam Degree between two added blocks
     */
    public double seamDegreeW(AreaImpl B1, AreaImpl B2) {
                
        double SD = 0;
        double SeamLength = 0;
        
        if (B1.getX2() > B2.getX2() && B1.getX1() < B2.getX1()) {
        	SeamLength = B2.getWidth();
        }
        else if (B1.getX2() < B2.getX2() && B1.getX1() > B2.getX1()) {
        	SeamLength = B1.getWidth();
        }
        else if (B1.getX2() < B2.getX2()) {
        	SeamLength = Math.abs(B1.getX2() - B2.getX1());      
        }
        else {
        	SeamLength = Math.abs(B2.getX2() - B1.getX1()); 
        }
        
        double SeamMul = B1.getWidth() * B2.getWidth(); 
                
        SD = (SeamLength * SeamLength) / (SeamMul);
               
        return SD;
    }
        
        
    /**
     * Function for calculating of Seam Degree with Height of two Blocks
     * @param B1 first block
     * @param B2 second block
     * @return value of Seam Degree between two added blocks
     */
    public double seamDegreeH(AreaImpl B1, AreaImpl B2) {
                 
        double SD = 0;
        double SeamLength = 0;  
        
        if (B1.getY2() > B2.getY2() && B1.getY1() < B2.getY1()) {
        	SeamLength = B2.getHeight();
        }
        else if (B1.getY2() < B2.getY2() && B1.getY1() > B2.getY1()) {
        	SeamLength = B1.getHeight();
        }
        else if (B1.getY2() < B2.getY2()) {
        	SeamLength = Math.abs(B1.getY2() - B2.getY1());      
        }
        else {
        	SeamLength = Math.abs(B2.getY2() - B1.getY1()); 
        }     
        
        double SeamMul = B1.getHeight() * B2.getHeight(); 
                
        SD = Math.pow(SeamLength, 2.0) / (SeamMul);
               
        return SD;
    }
    
    /**
     * Function for test of width adjacent of two given blocks	
     * @param B1 first block
     * @param B2 second block
     * @return return true if is it adjacent false otherwise
     */
    public boolean widthAdj(AreaImpl B1, AreaImpl B2) {
    	
    	int x1, x2, y1, y2;
    	
    	if (B1.getX2() > B2.getX1() && 
    	    B1.getX1() < B2.getX2()) {
    		
    		if (B1.getX1() < B2.getX1()) 
    			x1 = B1.getX1();
    		else 
    			x1 = B2.getX1();
    		
    		if (B1.getX2() > B2.getX2()) 
    			x2 = B1.getX2();
    		else 
    			x2 = B2.getX2();
    		
    		if (B1.getY1() < B2.getY1()) {
    			y1 = B1.getY2();
    			y2 = B2.getY1();
    		}
    		else  {
    			y1 = B2.getY2();
    			y2 = B1.getY1();
    		}
    		
    		Rectangular rec = new Rectangular(x1,y1,x2,y2);
    		
    		AreaImpl parrentNode = (AreaImpl) B1.getParentArea();
    		
    		for (int i = 0; i < parrentNode.getChildCount(); i++) {
    			if (B1 != parrentNode.getChildArea(i) && B2 != parrentNode.getChildArea(i)) {
    				if (rec.intersects(parrentNode.getChildArea(i).getBounds())) {
    					return false;
    				}
    			}
    		}
    		
    		return true;
    	}
    	
    	return false;
    }
    
    /**
     * Function for test of height adjacent of two given blocks	
     * @param B1 first block
     * @param B2 second block
     * @return return true if is it adjacent false otherwise
     */
    public boolean heightAdj(AreaImpl B1, AreaImpl B2) {
    	
    	int x1, x2, y1, y2;
    	
    	if (B1.getY2() > B2.getY1() && 
    	    B1.getY1() < B2.getY2()) {
    		
    		if (B1.getY1() < B2.getY1()) 
    			y1 = B1.getY1();
    		else 
    			y1 = B2.getY1();
    		
    		if (B1.getY2() > B2.getY2()) 
    			y2 = B1.getY2();
    		else 
    			y2 = B2.getY2();
    		
    		if (B1.getX1() < B2.getX1()) {
    			x1 = B1.getX2();
    			x2 = B2.getX1();
    		}
    		else  {
    			x1 = B2.getX2();
    			x2 = B1.getX1();
    		}
    		
    		Rectangular rec = new Rectangular(x1,y1,x2,y2);
    		
    		AreaImpl parrentNode = (AreaImpl) B1.getParentArea();
    		
    		for (int i = 0; i < parrentNode.getChildCount(); i++) {
    			if (B1 != parrentNode.getChildArea(i) && B2 != parrentNode.getChildArea(i)) {
    				if (rec.intersects(parrentNode.getChildArea(i).getBounds())) {
    					return false;
    				}
    			}
    		}
    		
    		return true;
    	}
    	
    	return false;
    }
        
    /**
     * Function for calculating average seam degree of given block
     * @param B given block
     * @return average seam degree
     */
    public double avgSeamDegree(AreaImpl B) {
        int ChildCount = B.getChildCount();
        int M = 0;
        double pom = 0.0;
        double AvgSD = 0.0;
                
        for (int i = 0; i < ChildCount; i++) {
            for (int j = 0; j < ChildCount; j++) {
                if (i != j && i < j) {
                    if (widthAdj((AreaImpl) B.getChildArea(i),(AreaImpl) B.getChildArea(j))) {	
                        pom = seamDegreeW((AreaImpl) B.getChildArea(i),(AreaImpl) B.getChildArea(j));
                        AvgSD += pom;
                        M++;
                    }
                    else if (heightAdj((AreaImpl) B.getChildArea(i),(AreaImpl) B.getChildArea(j))) {
                        pom = seamDegreeH((AreaImpl) B.getChildArea(i),(AreaImpl) B.getChildArea(j));
                        AvgSD += pom;
                        M++;
                    }                                       
                }
            }
        }
                
        if (M != 0) {
            AvgSD = AvgSD / M;
        }
        
        return AvgSD;
    }
        
    /**
     * Function for calculating of Area content of given block
     * @param B given block
     * @return calculated Area
     */
    public double Area(AreaImpl B) {
             
        double AreaC = 0.0;
        
        for (int i = 0; i < B.getChildCount(); i++) {
        	if (B.getChildArea(i).getBoxes().get(0).getType() == Type.TEXT_CONTENT) {
        		String pom = B.getChildArea(i).getBoxes().get(0).getText();
        		if (pom != null) {
        			AreaC += B.getChildArea(i).getBoxes().get(0).getFontSize() * B.getChildArea(i).getBoxes().get(0).getFontSize() * pom.length();
        		}
        	}
        	else if (B.getChildArea(i).getBoxes().get(0).getType() == Type.REPLACED_CONTENT) {
        		Rectangular rec = B.getChildArea(i).getBoxes().get(0).getVisualBounds();
        		AreaC += rec.getWidth() * rec.getHeight();
        	}
        	else {
        		Rectangular rec = B.getChildArea(i).getBoxes().get(0).getVisualBounds();
        		AreaC += rec.getWidth() * rec.getHeight();
        	}
        }

        return AreaC;
    }
        
    /**
     * Weight function
     * @param vec1 content vector of first block
     * @param vec2 content vector of second block
     * @param B1 first block
     * @param B2 second block
     * @return value of Weight function
     */
    public double Weight(List<Double> vec1, List<Double> vec2, AreaImpl B1, AreaImpl B2) {
    	
    	double W = 0.0;
    	
    	double AreaB1 = Area(B1);
    	double AreaB2 = Area(B2);
    	
    	double Ui = 0;
    	double Vi = 0;
    	
    	for (int i = 0; i < vec1.size(); i++) {
    		Ui += vec1.get(i);
    	}

    	for (int j = 0; j < vec2.size(); j++) {
    		Vi += vec2.get(j);
    	}
    	
    	W = (Ui + Vi) / (AreaB1 + AreaB2);

    	return W;
    }
    
    /**
     * Function which calculate Cosine Similarity of two given vectors
     * @param B1 first vector
     * @param B2 second vector
     * @return calculated Cosine Similarity
     */
    public double CosineSimilarity(List<Double> vec1, List<Double> vec2) {

    	double CS = 0.0;
    	double top = 0;
    	double botUi = 0;
    	double botVi = 0;
    	

    	if (vec1.size() > vec2.size()) {
    		int pom = vec1.size() - vec2.size();
    		for (int i = 0; i < pom; i++) {
    			vec2.add(0.0);
    		}
    	}
    	else if (vec1.size() < vec2.size()){
    		int pom = vec2.size() - vec1.size();
    		for (int i = 0; i < pom; i++) {
    			vec1.add(0.0);
    		}
    	}

    	int n = vec1.size();
    	
    	for (int j = 0; j < n; j++) {
    		top += vec1.get(j) * vec2.get(j);
    		botUi += vec1.get(j) * vec1.get(j);
    		botVi += vec2.get(j) * vec2.get(j);
    	}

    	botUi = Math.sqrt(botUi);
    	botVi = Math.sqrt(botVi);
    	
    	if (botUi == 0 || botVi == 0) 
    		CS = 0;
    	else
    		CS = top / (botUi * botVi);
    	
		return CS;
    }
    
    /**
     * Function for return text contents of given block	
     * @param B given block
     * @return vector of text contents B
     */
    public List<Double> getTextVector(AreaImpl B) {
    	
    	List<Double> vec = new ArrayList<Double>();
    	
        for (int i = 0; i < B.getChildCount(); i++) {
        	if (B.getChildArea(i).getBoxes().get(0).getType() == Type.TEXT_CONTENT) {
        		String pom = B.getChildArea(i).getBoxes().get(0).getText();
        		vec.add((double)(B.getChildArea(i).getBoxes().get(0).getFontSize() * B.getChildArea(i).getBoxes().get(0).getFontSize() * pom.length()));
        	}
        }
        
    	Collections.sort(vec);
    	Collections.reverse(vec);
    	
    	return vec;
    }
  
    /**
     * Function for return image contents of given block	
     * @param B given block
     * @return vector of image contents B
     */
    public List<Double> getImgVector(AreaImpl B) {
    	
    	List<Double> vec = new ArrayList<Double>();
    	
        for (int i = 0; i < B.getChildCount(); i++) {
        	if (B.getChildArea(i).getBoxes().get(0).getType() == Type.REPLACED_CONTENT) {
        		Rectangular rec = B.getChildArea(i).getBoxes().get(0).getVisualBounds();
        		vec.add((double)(rec.getWidth() * rec.getHeight()));
        	}
        }
        
    	Collections.sort(vec);
    	Collections.reverse(vec);
    	
    	return vec;
    }
    
    /**
     * Function for return input contents of given block	
     * @param B given block
     * @return vector of input contents B
     */
    public List<Double> getContentVector(AreaImpl B) {
    	
    	List<Double> vec = new ArrayList<Double>();
    	
        for (int i = 0; i < B.getChildCount(); i++) {
        	if (B.getChildArea(i).getBoxes().get(0).getType() == Type.ELEMENT) {
        		Rectangular rec = B.getChildArea(i).getBoxes().get(0).getVisualBounds();
        		vec.add((double)(rec.getWidth() * rec.getHeight()));
        	}
        }
        
    	Collections.sort(vec);
    	Collections.reverse(vec);
    	
    	return vec;
    }
    
    /**
     * Function for calculating content similarity of two given blocks
     * @param B1 first block
     * @param B2 second block
     * @return calculated content similarity
     */
    public double contentSimilarity(AreaImpl B1, AreaImpl B2) {
    	
    	
    	double CS = 0.0;
    	List<Double> VtB1 = new ArrayList<Double>();
    	List<Double> VtB2 = new ArrayList<Double>();
    	List<Double> VimgB1 = new ArrayList<Double>();
    	List<Double> VimgB2 = new ArrayList<Double>();
    	List<Double> VcB1 = new ArrayList<Double>();
    	List<Double> VcB2 = new ArrayList<Double>();
    	
    	VtB1 = getTextVector(B1);
    	VtB2 = getTextVector(B2);
    	VimgB1 = getImgVector(B1);
    	VimgB2 = getImgVector(B2);
    	VcB1 = getContentVector(B1);
    	VcB2 = getContentVector(B2);
    	
    	CS += Weight(VtB1, VtB2, B1, B2) * CosineSimilarity(VtB1, VtB2);
    	CS += Weight(VimgB1, VimgB2, B1, B2) * CosineSimilarity(VimgB1, VimgB2);
    	CS += Weight(VcB1, VcB2, B1, B2) * CosineSimilarity(VcB1, VcB2);  	

    	return CS;
    }
    
    /**
     * Function for calculating average seam degree of given block
     * @param B given Box
     * @return average seam degree
     */
    public double AvgContentSimilarity(AreaImpl B) {
        int childCount = B.getChildCount();
        int m = 0;
        double pom = 0.0;
        double AvgCS = 0.0;
                
        for (int i = 0; i < childCount; i++) {
            for (int j = 0; j < childCount; j++) {
                if (i != j && i < j) {
                    if (   widthAdj((AreaImpl) B.getChildArea(i),(AreaImpl) B.getChildArea(j))
                    	|| heightAdj((AreaImpl) B.getChildArea(i),(AreaImpl) B.getChildArea(j))) {	
                        pom = contentSimilarity((AreaImpl) B.getChildArea(i),(AreaImpl) B.getChildArea(j));
                        AvgCS += pom;
                        m++;
                    }                                 
                }
            }
        }
                
        if (m != 0) {
        	AvgCS = AvgCS / m;
        }
        
        return AvgCS;
    }
}

