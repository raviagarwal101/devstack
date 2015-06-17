package com.kodkast.tech.devstack.legacy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TreeNode {

    private String name;
    private Object data;
    private List<TreeNode> children;

    private static Logger logger = LoggerFactory.getLogger(TreeNode.class);

    public TreeNode(String name) {
        this.name = name;
        children = new ArrayList<TreeNode>();
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addChild(TreeNode node) {
        children.add(node);
    }

    public boolean hasChildren() {
        if(children.size() > 0) {
            return true;
        }
        else {
            return false;
        }
    }

    public static void preOrder(TreeNode node) {

        if(node != null) {
            logger.info("Node - " + node.getName());
            if(node.hasChildren()) {
                List<TreeNode> children = node.getChildren();
                for(TreeNode childNode : children) {
                    preOrder(childNode);
                }
            }
        }
    }

    public static void postOrder(TreeNode node) {

        if(node != null) {
            if(node.hasChildren()) {
                List<TreeNode> children = node.getChildren();
                for(TreeNode childNode : children) {
                    postOrder(childNode);
                }
            }
            logger.info("Node - " + node.getName());
        }
    }

    public static List<TreeNode> getPostOrderNodes(TreeNode node) {

        List<TreeNode> nodes = new ArrayList<TreeNode>();

        if(node != null) {
            if(node.hasChildren()) {
                List<TreeNode> children = node.getChildren();
                for(TreeNode childNode : children) {
                    nodes.addAll(getPostOrderNodes(childNode));
                }
            }
            nodes.add(node);
        }

        return nodes;
    }

    public static List<TreeNode> getChildrenByAge(TreeNode node) {

        List<TreeNode> nodes = getPostOrderNodes(node);

        if(nodes.size() > 0) {
            nodes.remove(nodes.size() - 1);
        }

        return nodes;
    }

    public static void printNodesList(Collection<TreeNode> nodes) {
        for(TreeNode node : nodes) {
            logger.info("Node - " + node.getName());
        }
    }

}
