/* 
 * <copyright> 
 *  Copyright 1997-2001 Clark Software Engineering (CSE)
 *  under sponsorship of the Defense Advanced Research Projects 
 *  Agency (DARPA). 
 *  
 *  This program is free software; you can redistribute it and/or modify 
 *  it under the terms of the Cougaar Open Source License as published by 
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).  
 *  
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS  
 *  PROVIDED "AS IS" WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR  
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF  
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT  
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT  
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL  
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,  
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR  
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.  
 *  
 * </copyright> 
 */

package org.cougaar.lib.uiframework.ui.components.mthumbslider;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;

public class MetalMThumbSliderUI extends MetalSliderUI
  implements MThumbSliderAdditional {

  MThumbSliderAdditionalUI additonalUi;
  MouseInputAdapter mThumbTrackListener;

  public static ComponentUI createUI(JComponent c)    {
    return new MetalMThumbSliderUI((JSlider)c);
  }

  public MetalMThumbSliderUI()   {}

  // Need to initialize parameters used for this L&F (Some are not defined by other L&Fs)
//  public MetalMThumbSliderUI(JSlider b)   {}
  public MetalMThumbSliderUI(JSlider b)
  {
    super();

// Slider Defaults from Java Metal Look and Feel
/*
"Slider.border", null,
"Slider.foreground", getPrimaryControlShadow(),
"Slider.background", getControl(),
"Slider.focus", getFocusColor(),
"Slider.focusInsets", sliderFocusInsets,
"Slider.trackWidth", new Integer( 7 ),
"Slider.majorTickLength", new Integer( 6 ),
"Slider.horizontalThumbIcon", MetalIconFactory.getHorizontalSliderThumbIcon(),
"Slider.verticalThumbIcon", MetalIconFactory.getVerticalSliderThumbIcon(),
*/

    // These in particular are not defined by the Basic L&F (what most L&Fs extend)
    UIManager.put("Slider.trackWidth", new Integer(7));
    UIManager.put("Slider.majorTickLength", new Integer(6));

    UIManager.put("Slider.horizontalThumbIcon", MetalIconFactory.getHorizontalSliderThumbIcon());
    UIManager.put("Slider.verticalThumbIcon", MetalIconFactory.getVerticalSliderThumbIcon());
  }


  public Rectangle getTrackBounds()
  {
    int trackLeft = 0;
    int trackTop = 0;
    int trackRight = 0;
    int trackBottom = 0;

    if (slider.getOrientation() == JSlider.HORIZONTAL)
    {
      trackBottom = trackRect.height - getThumbOverhang();
      trackTop = trackBottom - getTrackWidth();
      trackRight = trackRect.width;
    }
    else
    {
      trackLeft = trackRect.width - getThumbOverhang() - getTrackWidth();
      trackRight = trackRect.width - getThumbOverhang();
      trackBottom = trackRect.height;
    }

    return(new Rectangle(trackLeft + trackRect.x, trackTop + trackRect.y, (trackRight - trackLeft) - 1, (trackBottom - trackTop) - 1));
  }

  public void installUI(JComponent c)   {
    additonalUi = new MThumbSliderAdditionalUI(this);
    additonalUi.installUI(c);
    mThumbTrackListener = createMThumbTrackListener((JSlider) c);
    super.installUI(c);
  }

  public void uninstallUI(JComponent c) {
    super.uninstallUI(c);
    additonalUi.uninstallUI(c);
    additonalUi = null;
    mThumbTrackListener = null;
  }

  protected MouseInputAdapter createMThumbTrackListener( JSlider slider ) {
    return additonalUi.trackListener;
  }

  protected TrackListener createTrackListener( JSlider slider ) {
    return null;
  }

  protected ChangeListener createChangeListener( JSlider slider ) {
    return additonalUi.changeHandler;
  }

  protected void installListeners( JSlider slider ) {
    slider.addMouseListener(mThumbTrackListener);
    slider.addMouseMotionListener(mThumbTrackListener);
    slider.addFocusListener(focusListener);
    slider.addComponentListener(componentListener);
    slider.addPropertyChangeListener( propertyChangeListener );
    slider.getModel().addChangeListener(changeListener);
  }

  protected void uninstallListeners( JSlider slider ) {
    slider.removeMouseListener(mThumbTrackListener);
    slider.removeMouseMotionListener(mThumbTrackListener);
    slider.removeFocusListener(focusListener);
    slider.removeComponentListener(componentListener);
    slider.removePropertyChangeListener( propertyChangeListener );
    slider.getModel().removeChangeListener(changeListener);
  }

  protected void calculateGeometry() {
    super.calculateGeometry();
    additonalUi.calculateThumbsSize();
    additonalUi.calculateThumbsLocation();
  }

  public Rectangle[] getThumbRects() {
    return additonalUi.getThumbRects();
  }

  protected void calculateThumbLocation() {}

  Icon thumbRenderer;

  public void paint( Graphics g, JComponent c ) {
    Rectangle clip = g.getClipBounds();
    Rectangle[] thumbRects = additonalUi.getThumbRects();


    int thumbNum = additonalUi.getThumbNum();

    // Somewhere the additonalUi.calculateThumbsLocation() is not called when the total range is changed and the the
    // range of the thumb slider is set, so we recalculate thumbRects here every time we paint
    for (int i=0; i<thumbNum; i++) {
      if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
        int value = ((CMThumbSlider)slider).getValueAt(i);
        int valuePosition = xPositionForValue(value);
        thumbRects[i].x = valuePosition - (thumbRects[i].width / 2);
        thumbRects[i].y = trackRect.y;
      }
      else {
        int valuePosition = yPositionForValue(((CMThumbSlider)slider).getValueAt(i));
        thumbRects[i].x = trackRect.x;
        thumbRects[i].y = valuePosition - (thumbRects[i].height / 2);
      }
    }


    thumbRect = thumbRects[0];
//    int thumbNum = additonalUi.getThumbNum();

    if ( slider.getPaintTrack() && clip.intersects( trackRect ) ) {
      boolean filledSlider_tmp = filledSlider;
      filledSlider = false;
      paintTrack( g );
      filledSlider = filledSlider_tmp;

      if ( filledSlider ) {
        g.translate(  trackRect.x,  trackRect.y );

        Point t1 = new Point(0,0);
        Point t2 = new Point(0,0);
        Rectangle maxThumbRect = new Rectangle(thumbRect);
        thumbRect = maxThumbRect;

        if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
          t2.y = (trackRect.height - 1) - getThumbOverhang();
          t1.y = t2.y - (getTrackWidth() - 1);
          t2.x = trackRect.width - 1;
          int maxPosition = xPositionForValue(slider.getMaximum());
          thumbRect.x = maxPosition - (thumbRect.width / 2) -2;
          thumbRect.y = trackRect.y;
        }
        else {
          t1.x = (trackRect.width - getThumbOverhang()) - getTrackWidth();
          t2.x = (trackRect.width - getThumbOverhang()) - 1;
          t2.y = trackRect.height - 1;
          int maxPosition = yPositionForValue(slider.getMaximum());
          thumbRect.x = trackRect.x;
          thumbRect.y = maxPosition - (thumbRect.height / 2) -2;
        }

        Color fillColor = ((CMThumbSlider)slider).getTrackFillColor();
        if (fillColor == null) {
          fillColor = MetalLookAndFeel.getControlShadow();
        }
        fillTrack( g, t1, t2, fillColor);

        for (int i=thumbNum-1; 0<=i; i--) {
          thumbRect = thumbRects[i];
          fillColor = ((CMThumbSlider)slider).getFillColorAt(i);
          if (fillColor == null) {
            fillColor = MetalLookAndFeel.getControlShadow();
          }
          fillTrack( g, t1, t2, fillColor);
        }

        g.translate( -trackRect.x, -trackRect.y );
      }
    }
    if ( slider.getPaintTicks() && clip.intersects( tickRect ) ) {
      paintTicks( g );
    }
    if ( slider.getPaintLabels() && clip.intersects( labelRect ) ) {
      paintLabels( g );
    }

    for (int i=thumbNum-1; 0<=i; i--) {
      if ( clip.intersects( thumbRects[i] ) ) {
        thumbRect = thumbRects[i];
        thumbRenderer = ((CMThumbSlider)slider).getThumbRendererAt(i);
        if (thumbRenderer == null) {
          if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
            thumbRenderer = horizThumbIcon;
          } else {
            thumbRenderer = vertThumbIcon;
          }
        }
        paintThumb( g );
      }
    }
  }


  public void paintThumb(Graphics g) {
    thumbRenderer.paintIcon( slider, g, thumbRect.x,     thumbRect.y );
  }


  public void fillTrack(Graphics g, Point t1, Point t2, Color fillColor) {
    //                               t1-------------------
    //                               |                   |
    //                               --------------------t2
    int middleOfThumb = 0;

    if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
      middleOfThumb = thumbRect.x + (thumbRect.width / 2) - trackRect.x;
      if ( slider.isEnabled() ) {
        g.setColor(fillColor);
        g.fillRect( t1.x+2,
        t1.y+2,
              middleOfThumb - t1.x -1,
        t2.y - t1.y -3);
        g.setColor(fillColor.brighter());
        g.drawLine( t1.x+1, t1.y+1, middleOfThumb, t1.y+1 );
        g.drawLine( t1.x+1, t1.y+1, t1.x+1,        t2.y-2 );
      } else {
        g.setColor(fillColor);
        g.fillRect( t1.x,
        t1.y,
        middleOfThumb - t1.x +2,
        t2.y - t1.y );
      }
    }
    else {
      middleOfThumb = thumbRect.y + (thumbRect.height / 2) - trackRect.y;
      if ( slider.isEnabled() ) {
        g.setColor( slider.getBackground() );
        g.drawLine( t1.x+1, middleOfThumb, t2.x-2, middleOfThumb );
        g.drawLine( t1.x+1, middleOfThumb, t1.x+1, t2.y - 2 );
        g.setColor( fillColor );
        g.fillRect( t1.x + 2,
        middleOfThumb + 1,
        t2.x - t1.x -3,
        t2.y-2 -  middleOfThumb);
      } else {
        g.setColor( fillColor );
        g.fillRect( t1.x,
        middleOfThumb +2,
              t2.x-1 - t1.x,
        t2.y - t1.y );
      }
    }
  }

  public void scrollByBlock(int direction) {}
  public void scrollByUnit(int direction) {}

  //
  //  MThumbSliderAdditional
  //
  public Rectangle getTrackRect() {
    return trackRect;
  }
  public Dimension getThumbSize() {
    return super.getThumbSize();
  }
  public int xPositionForValue(int value) {
    return super.xPositionForValue( value);
  }
  public int yPositionForValue(int value) {
    return super.yPositionForValue( value);
  }
}

