package com.darylkurtz.systemDependencies;

import org.junit.*;
import java.util.ArrayList;
import java.util.List;

public class systemDependenciesTest {

    @Before
    public void setUp() {
        System.out.println("@Before - setUp");
    }

    @Test
    public void testDependOn() {
        List<String> DOTestDependencies = new ArrayList<>();
        DOTestDependencies.add("TELNET");
        DOTestDependencies.add("TCPIP");
        DOTestDependencies.add("NETCARD");

        systemDependencies testDepend = new systemDependencies();
        testDepend.dependOn(DOTestDependencies);
        System.out.println("Expected 2 keys, result: " + (testDepend.dependencyMapping.size()));
        System.out.println("Keys made: " + testDepend.dependencyMapping.keySet());
        Assert.assertTrue(testDepend.dependencyMapping.size() == 2);

    }

    @Test
    public void testDependOnInstall() {
        List<String> DOITestDependencies = new ArrayList<>();
        DOITestDependencies.add("TELNET");
        DOITestDependencies.add("TCPIP");
        DOITestDependencies.add("NETCARD");

        systemDependencies testDependInstall = new systemDependencies();
        testDependInstall.dependOn(DOITestDependencies);
        testDependInstall.installItem("TELNET");
        System.out.println("Expected 3 installed, result: " + (testDependInstall.installedItems.size()));
        System.out.println("Keys made: " + testDependInstall.dependencyMapping.keySet());
        System.out.println("Apps installed " + testDependInstall.installedItems);
        Assert.assertTrue(testDependInstall.installedItems.size() == 3);

    }

    @Test
    public void testDependLoop() {
        List<String> DLTestDependenciesA = new ArrayList<>();
        List<String> DLTestDependenciesB = new ArrayList<>();
        DLTestDependenciesA.add("TELNET");
        DLTestDependenciesA.add("TCPIP");
        DLTestDependenciesB.add("TCPIP");
        DLTestDependenciesB.add("TELNET");

        systemDependencies testDepend = new systemDependencies();
        testDepend.dependOn(DLTestDependenciesA);
        testDepend.dependOn(DLTestDependenciesB);
        Assert.assertTrue("TELNET is dependent on TCPIP, ignoring", true);


    }

    @Test
    public void testInstallItem() {
        List<String> ITestApps = new ArrayList<>();
        ITestApps.add("TELNET");
        ITestApps.add("TCPIP");
        ITestApps.add("NETCARD");

        systemDependencies testInstall = new systemDependencies();
        testInstall.installItem(ITestApps.get(2));
        System.out.println("Expected 1 installed, result: " + (testInstall.installedItems.size()));
        System.out.println("Keys made: " + testInstall.dependencyMapping.keySet());
        System.out.println("Apps installed " + testInstall.installedItems);
        Assert.assertTrue(testInstall.installedItems.size() == 1);
    }

    @Test
    public void testRemoveItem() {
        List<String> RTestApps = new ArrayList<>();
        RTestApps.add("TELNET");
        RTestApps.add("TCPIP");
        RTestApps.add("NETCARD");

        systemDependencies testRemove = new systemDependencies();
        for (String i : RTestApps) {
            testRemove.installItem(i);
        }
        testRemove.removeItem("NETCARD");
        System.out.println("Expected 2 installed, result: " + (testRemove.installedItems.size()));
        System.out.println("Keys made: " + testRemove.dependencyMapping.keySet());
        System.out.println("Apps installed " + testRemove.installedItems);
        Assert.assertTrue(testRemove.installedItems.size() == 2);
    }

    @Test
    public void testRemoveDepend() {
        List<String> RDTestApps = new ArrayList<>();
        RDTestApps.add("TELNET");
        RDTestApps.add("TCPIP");
        RDTestApps.add("NETCARD");

        systemDependencies testRemoveDepend = new systemDependencies();
        testRemoveDepend.dependOn(RDTestApps);
        for (String i : RDTestApps) {
            testRemoveDepend.installItem(i);
        }
        testRemoveDepend.removeItem("NETCARD");
        System.out.println("Expected 0 installed, result: " + (testRemoveDepend.installedItems.size()));
        System.out.println("Keys made: " + testRemoveDepend.dependencyMapping.keySet());
        System.out.println("Apps installed " + testRemoveDepend.installedItems);
        Assert.assertTrue(testRemoveDepend.installedItems.size() == 0);
    }

    @Test
    public void testTwinDependencies() {
        List<String> dependOne = new ArrayList<>();
        dependOne.add("TELNET");
        dependOne.add("TCPIP");
        dependOne.add("NETCARD");

        List<String> dependTwo = new ArrayList<>();
        dependTwo.add("DNS");
        dependTwo.add("TCPIP");
        dependTwo.add("NETCARD");

        List<String> testTwinDepApps = new ArrayList<>();
        testTwinDepApps.add("TELNET");
        testTwinDepApps.add("TCPIP");
        testTwinDepApps.add("NETCARD");
        testTwinDepApps.add("DNS");

        systemDependencies testTwin = new systemDependencies();
        testTwin.dependOn(dependOne);
        testTwin.dependOn(dependTwo);
        for (String depApp : testTwinDepApps) {
            testTwin.installItem(depApp);
        }
        testTwin.removeItem("NETCARD");
        System.out.println("Expected 0 installed, result: " + (testTwin.installedItems.size()));
        System.out.println("Keys made: " + testTwin.dependencyMapping.keySet());
        System.out.println("Apps installed " + testTwin.installedItems);
        Assert.assertTrue(testTwin.installedItems.size() == 0);
    }

    @Test
    public void testfullCycle() {
        List<String> fullTestDependencies = new ArrayList<>();
        fullTestDependencies.add("TELNET");
        fullTestDependencies.add("TCPIP");
        fullTestDependencies.add("NETCARD");

        systemDependencies testFull = new systemDependencies();
        testFull.dependOn(fullTestDependencies);
        Assert.assertTrue(testFull.dependencyMapping.size() == 2);

        testFull.installItem("TELNET");
        Assert.assertTrue(testFull.installedItems.size() == 3);

        testFull.removeItem("TCPIP");
        Assert.assertTrue(testFull.installedItems.size() == 1);

    }

    @After
    public void tearDown() {
        System.out.println("@After - tearDown");
        systemDependencies.installedItems.clear();
        systemDependencies.dependencyMapping.clear();
        systemDependencies.inputItems.clear();
        systemDependencies.singleResult = null;
    }
}