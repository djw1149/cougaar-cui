package org.cougaar.lib.uiframework.ui.components.mthumbslider;

import java.awt.*;
import javax.swing.event.*;
import javax.swing.*;

public class CMThumbSlider extends JSlider /*implements ChangeListener*/ {
  protected int thumbNum;
  protected BoundedRangeModel[] sliderModels;
  protected Icon[] thumbRenderers;
  protected Color[] fillColors;
  protected Color trackFillColor;

  private static final String uiClassID = "MThumbSliderUI";

  public CMThumbSlider(int n) {
    createThumbs(n);
    updateUI();
  }

  public Rectangle getTrackRect() {
    MetalMThumbSliderUI ui = (MetalMThumbSliderUI)this.getUI();
    return ui.getTrackRect();
  }

  protected void createThumbs(int n) {
    thumbNum = n;
    sliderModels   = new BoundedRangeModel[n];
    thumbRenderers = new Icon[n];
    fillColors = new Color[n];
    for (int i=0;i<n;i++) {
      sliderModels[i] = new DefaultBoundedRangeModel(50, 0, 0, 100);
      //sliderModels[i].addChangeListener(this);
      thumbRenderers[i] = null;
      fillColors[i] = null;
    }
  }

  public void updateUI() {
    AssistantUIManager.setUIName(this);
    super.updateUI();
  }

  public String getUIClassID() {
    return uiClassID;
  }

  public int getThumbXLoc() {
    return getThumbXLoc(0);
  }

  public int getThumbXLoc(int index) {
    MetalMThumbSliderUI ui = (MetalMThumbSliderUI)getUI();
    Rectangle[] rects = ui.getThumbRects();
    Rectangle rect = rects[index];
    return rect.x + rect.width/2;
  }

  public int getThumbYLoc() {
    return getThumbYLoc(0);
  }

  public int getThumbYLoc(int index) {
    MetalMThumbSliderUI ui = (MetalMThumbSliderUI)getUI();
    Rectangle[] rects = ui.getThumbRects();
    Rectangle rect = rects[index];
    return rect.y + rect.height/2;
  }

  public int getThumbNum() {
   return thumbNum;
  }

  /*
  public void setValue(int val) {
    // CSB - why are these calls to setValueIsAdjusting necessary???
    setValueIsAdjusting(true);
    getModelAt(0).setValue(val);
    setValueIsAdjusting(false);
  }

  public int getValue() {
    return getModelAt(0).getValue();
  }
  */

  public int getValueAt(int index) {
    return getModelAt(index).getValue();
  }

  public void setValueAt(int n, int index) {
    /*
    if (thumbNum > 1) {
      if (index == 0 && getModelAt(1).getValue() < n)
        getModelAt(1).setValue(n);
      else if (index == 1 && getModelAt(0).getValue() > n)
        getModelAt(0).setValue(n);
      else
        getModelAt(index).setValue(n);
    }
    else
    */
      getModelAt(index).setValue(n);
      fireStateChanged(); // PHF
  }

  public void setMininum(int min) {
    getModelAt(0).setMinimum(min);
  }

  public void setMaximum(int max) {
    getModelAt(0).setMaximum(max);
  }

  public int getMinimum() {
    return getModelAt(0).getMinimum();
  }

  public int getMaximum() {
    return getModelAt(0).getMaximum();
  }

  public BoundedRangeModel getModelAt(int index) {
    return sliderModels[index];
  }

  public Icon getThumbRendererAt(int index) {
    return thumbRenderers[index];
  }

  public void setThumbRendererAt(Icon icon, int index) {
    thumbRenderers[index] = icon;
  }

  public Color getFillColorAt(int index) {
    return fillColors[index];
  }

  public void setFillColorAt(Color color, int index) {
    fillColors[index] = color;
  }

  public Color getTrackFillColor() {
    return trackFillColor;
  }

  public void setTrackFillColor(Color color) {
    trackFillColor = color;
  }

  /*
  public void stateChanged(ChangeEvent e) {
    BoundedRangeModel model = (BoundedRangeModel)e.getSource();
    if (model.getValueIsAdjusting())
      return;
    fireStateChanged();
  }
  */
}

