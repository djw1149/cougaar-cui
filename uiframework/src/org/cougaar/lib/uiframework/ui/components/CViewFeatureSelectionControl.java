/*
 * <copyright>
 * Copyright 1997-2001 Defense Advanced Research Projects
 * Agency (DARPA) and ALPINE (a BBN Technologies (BBN) and
 * Raytheon Systems Company (RSC) Consortium).
 * This software to be used only in accordance with the
 * COUGAAR licence agreement.
 * </copyright>
 */
package org.cougaar.lib.uiframework.ui.components;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * Control used to select view related features of a stoplight chart.
 * Used to select whether chart should use color, value, or both when
 * displaying a data point in a cell.
 */
public class CViewFeatureSelectionControl extends JPanel
{
    /** color selection string */
    public static String COLOR = "Color";

    /** value selection string */
    public static String VALUE = "Value";

    /** both selection string */
    public static String BOTH = "Both";

    private CRadioButtonSelectionControl modeControl = null;
    private static String[] selections = {COLOR, VALUE, BOTH};
    private JCheckBox fitHorizontallyControl = null;
    private JCheckBox fitVerticallyControl = null;

    /**
     * Default constructor.  Create new view feature selection control with
     * horizontal orientation.
     */
    public CViewFeatureSelectionControl()
    {
        super(new GridLayout(2, 1));

        modeControl =
            new CRadioButtonSelectionControl(selections, BoxLayout.X_AXIS);
        init();
    }

    /**
     * Create new view feature selection control.
     *
     * @param orientation BoxLayout.X_AXIS or BoxLayout.Y_AXIS
     */
    public CViewFeatureSelectionControl(int orientation)
    {
        super(new GridLayout(1, 2));

        modeControl =
            new CRadioButtonSelectionControl(selections, orientation);
        init();
    }

    /**
     * Not needed when compiling/running under jdk1.3
     */
    protected void firePropertyChange(String propertyName, Object oldValue,
                                      Object newValue)
    {
        super.firePropertyChange(propertyName, oldValue, newValue);
    }

    private void init()
    {
        modeControl.setSelectedItem(BOTH);
        add(modeControl);
        modeControl.addPropertyChangeListener("selectedItem",
            new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent e)
                {
                    CViewFeatureSelectionControl.this.firePropertyChange(
                        "mode", e.getOldValue(), e.getNewValue());
                    boolean justColorSelected = e.getNewValue().
                        equals(CViewFeatureSelectionControl.COLOR);
                    fitHorizontallyControl.setEnabled(justColorSelected);
                    fitHorizontallyControl.setSelected(justColorSelected);
                    fitVerticallyControl.setEnabled(justColorSelected);
                    if (!justColorSelected)
                        fitVerticallyControl.setSelected(false);
                }
            });
        Box fitControls = new Box(BoxLayout.Y_AXIS);
        fitControls.add(Box.createVerticalStrut(8));
        fitHorizontallyControl = new JCheckBox("Fit Horizontally");
        fitControls.add(fitHorizontallyControl);
        fitVerticallyControl = new JCheckBox("Fit Vertically");
        fitControls.add(fitVerticallyControl);
        add(fitControls);
        fitHorizontallyControl.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e)
                {
                    boolean newState = fitHorizontallyControl.isSelected();
                    CViewFeatureSelectionControl.this.firePropertyChange(
                        "fitHorizontally", !newState, newState);
                }
            });
        fitVerticallyControl.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e)
                {
                    boolean newState = fitVerticallyControl.isSelected();
                    CViewFeatureSelectionControl.this.firePropertyChange(
                        "fitVertically", !newState, newState);
                }
            });
    }

    /**
     * Used to select whether chart should use color, value, or both when
     * displaying a data point in a cell.
     *
     * @param newMode CViewFeatureControl.COLOR, VALUE, or BOTH
     */
    public void setMode(String newMode)
    {
        modeControl.setSelectedItem(newMode);
    }

    /**
     * Used to get whether chart is using color, value, or both when
     * displaying a data point in a cell.
     *
     * @return CViewFeatureControl.COLOR, VALUE, or BOTH
     */
    public String getMode()
    {
        return (String)modeControl.getSelectedItem();
    }

    /**
     * If set to true the data cells will compress in width to fit all cells
     * in the viewport.  Otherwise, a horizontal scrollbar is used.
     *
     * @param newValue new state for checkbox
     */
    public void setFitHorizontally(boolean newValue)
    {
        fitHorizontallyControl.setSelected(newValue);
    }

    /**
     * If set to true the data cells will compress in width to fit all cells
     * in the viewport.  Otherwise, a horizontal scrollbar is used.
     *
     * @return current selected state of checkbox
     */
    public boolean getFitHorizontally()
    {
        return fitHorizontallyControl.isSelected();
    }

    /**
     * If set to true the data cells will compress in height to fit all cells
     * in the viewport.  Otherwise, a Vertical scrollbar is used.
     *
     * @param newValue new state for checkbox
     */
    public void setFitVertically(boolean newValue)
    {
        fitVerticallyControl.setSelected(newValue);
    }

    /**
     * If set to true the data cells will compress in height to fit all cells
     * in the viewport.  Otherwise, a Vertical scrollbar is used.
     *
     * @preturn current selected state of checkbox
     */
    public boolean getFitVertically()
    {
        return fitVerticallyControl.isSelected();
    }
}