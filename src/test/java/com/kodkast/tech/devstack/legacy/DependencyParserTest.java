package com.kodkast.tech.devstack.legacy;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

public class DependencyParserTest {

    private static DependencyParser parser;

    @BeforeClass
    public static void setUp() {
        parser = new DependencyParser();
    }

    @Test
    public void testParseConfig() {

        parser.addDependency("A", "B");
        parser.addDependency("B", "C");
        parser.addDependency("B", "D");
        parser.addDependency("C", "E");

        List<TreeNode> nodes = parser.getAllDependencies("A");

        TreeNode.printNodesList(nodes);
    }

}



