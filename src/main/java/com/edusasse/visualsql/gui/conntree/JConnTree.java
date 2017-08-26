package com.edusasse.visualsql.gui.conntree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;


@SuppressWarnings("serial")
public class JConnTree extends DefaultTreeModel  {
    
	@SuppressWarnings("unused")
	private final DefaultTreeModel rootnode = new DefaultTreeModel(new DefaultMutableTreeNode("root"));

    public JConnTree(TreeNode tn){
        super(tn);
        
    }

}
