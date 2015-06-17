package com.kodkast.tech.devstack.legacy;

import org.junit.BeforeClass;
import org.junit.Test;

public class TreeNodeTest {

    @BeforeClass
    public static void setUp() {

    }

    @Test
    public void testPreOrder() {
        TreeNode node = getExampleTree();
        TreeNode.preOrder(node);
    }

    @Test
    public void testPostOrder() {
        TreeNode node = getExampleTree();
        TreeNode.postOrder(node);
    }

    private TreeNode getExampleTree() {

        TreeNode root = new TreeNode("root");
        TreeNode node1 = new TreeNode("node1");
        TreeNode node2 = new TreeNode("node2");
        TreeNode node3 = new TreeNode("node3");
        TreeNode node4 = new TreeNode("node4");
        TreeNode node5 = new TreeNode("node5");
        TreeNode node6 = new TreeNode("node6");
        TreeNode node7 = new TreeNode("node7");

        root.addChild(node1);
        root.addChild(node2);
        node1.addChild(node3);
        node2.addChild(node4);
        node2.addChild(node5);
        node4.addChild(node6);
        node4.addChild(node7);

        /*
             root
            /    \
         node1  node2
          /     /   \
      node3  node4  node5
             /   \
         node6  node7

         */

        return root;
    }

}



