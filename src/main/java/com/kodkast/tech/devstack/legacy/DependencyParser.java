package com.kodkast.tech.devstack.legacy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DependencyParser {

    private Map<String, TreeNode> componentMap;

    private static Logger logger = LoggerFactory.getLogger(DependencyParser.class);

    public DependencyParser() {
        componentMap = new HashMap<String, TreeNode>();
    }

    public void addDependency(String component, String dependency) {
        TreeNode componentNode = getComponentNode(component);
        TreeNode dependencyNode = getComponentNode(dependency);
        componentNode.addChild(dependencyNode);
    }

    public List<TreeNode> getAllDependencies(String component) {
        TreeNode node = getComponentNode(component);
        return TreeNode.getChildrenByAge(node);
    }

    public void showDependencies(String component) {
        TreeNode node = getComponentNode(component);
        List<TreeNode> nodes = TreeNode.getChildrenByAge(node);
        for(TreeNode dependentNode : nodes) {
            logger.info(dependentNode.getName());
        }
    }

    private TreeNode getComponentNode(String component) {

        if(!componentMap.containsKey(component)) {
            TreeNode node = new TreeNode(component);
            componentMap.put(component, node);
        }

        return componentMap.get(component);
    }
}
