package org.cougaar.lib.uiframework.ui.components;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import org.cougaar.lib.uiframework.ui.util.Selector;

/**
 * A UI control bean that is a single select JTree in a JPanel.  Control
 * implements Selector interface
 *
 * This bean has bounded property:  "selectedItem"
 */
public class CNodeSelector extends JPanel implements Selector
{
    private DefaultMutableTreeNode selectedNode;
    private CNodeSelectionControl nsc;

    /**
     * Default constructor.  Create a new tree with a simple default
     * tree.
     */
    public CNodeSelector()
    {
        super(new BorderLayout());

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("parent");
        root.add(new DefaultMutableTreeNode("child1"));
        root.add(new DefaultMutableTreeNode("child2"));
        selectedNode = root;

        init(root);
    }

    /**
     * Create a new node selector.
     *
     * @param root root node of tree that models tree from which the user will
     *             select nodes
     */
    public CNodeSelector(DefaultMutableTreeNode root)
    {
        super(new BorderLayout());
        this.selectedNode = root;

        init(root);
    }

    /**
     * Initialize control
     *
     * @param root root node of tree that models tree from which the user will
     *             select nodes
     */
    private void init(DefaultMutableTreeNode root)
    {
        nsc = new CNodeSelectionControl(root);
        JScrollPane scrolledNSC = new JScrollPane(nsc);
        add(scrolledNSC, BorderLayout.CENTER);
        setPreferredSize(new Dimension(220, 200));

        nsc.addTreeSelectionListener(new TreeSelectionListener() {
                public void valueChanged(TreeSelectionEvent e)
                {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                        nsc.getLastSelectedPathComponent();
                    if (node == null) return;

                    DefaultMutableTreeNode oldSelectedNode = selectedNode;
                    selectedNode = node;
                    String selection =(String)node.getUserObject();
                    firePropertyChange("selectedItem", oldSelectedNode,
                                       selectedNode);
                }
            });
    }

    /**
     * Not needed when compiling/running under jdk1.3
     */
    protected void firePropertyChange(String propertyName, Object oldValue,
                                      Object newValue)
    {
        super.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Sets if root node should be visible and selectable in tree selection
     * control.
     *
     * @param visible true if root node should be both visible and selectable
     */
    public void setRootVisible(boolean visible)
    {
        nsc.setRootVisible(visible);
    }

    /**
     * Expand the first level of the tree.
     */
    public void expandFirstLevel()
    {
        TreeNode root = (TreeNode)nsc.getModel().getRoot();
        for (int i = 0; i < root.getChildCount(); i++)
        {
            Object[] tp = {root, root.getChildAt(i)};
            nsc.expandPath(new TreePath(tp));
        }
    }

    /**
     * Get the currently selected node.
     *
     * @return the currently selected node
     *         (can cast to type DefaultMutableTreeNode)
     */
    public Object getSelectedItem()
    {
        return selectedNode;
    }

    /**
     * Set the selected node.
     *
     * @param selectedItem the new node
     *                     (can be of type String or DefaultMutableTreeNode)
     */
    public void setSelectedItem(Object selectedItem)
    {
        if (selectedItem instanceof DefaultMutableTreeNode)
        {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)selectedItem;
            nsc.setSelectionPath(new TreePath(node.getPath()));
        }
        else if (selectedItem instanceof String)
        {
            DefaultMutableTreeNode node =
                findNode((DefaultMutableTreeNode)nsc.getModel().getRoot(),
                         selectedItem.toString());
            nsc.setSelectionPath(new TreePath(node.getPath()));
        }
    }

    private DefaultMutableTreeNode
        findNode(DefaultMutableTreeNode currentNode, String nodeString)
    {
        if (currentNode.getUserObject().toString().equals(nodeString))
        {
            return currentNode;
        }
        if (!currentNode.isLeaf())
        {
            for (int i = 0; i < currentNode.getChildCount(); i++)
            {
                DefaultMutableTreeNode foundNode =
                    findNode((DefaultMutableTreeNode)
                             currentNode.getChildAt(i), nodeString);
                if (foundNode != null) return foundNode;
            }
        }

        return null;
    }
}