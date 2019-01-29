package com.darylkurtz.systemDependencies;

import java.io.IOException;
import java.util.*;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class systemDependencies {
    // singleResult initialized for install/remove functions
    static String singleResult = "";

    // list initialized for tracking dependencies
    static Multimap<String,String> dependencyMapping = ArrayListMultimap.create();

    // hashset initialized for tracking installed components
    static Set<String> installedItems = new HashSet<>();

    static List<String> inputItems = new ArrayList<String>();

    // adds dependencies to list
    public static void dependOn(List<String> inputResult) {
        // check if dependencies already exist
        if (dependencyMapping.containsKey(inputResult.get(0))) {
            System.out.println("Dependency already exists");
        } else {
            // maps the dependencies
            for (int i=0; i < inputResult.size()-1; i++) {
                // Checking to see if there is already a dependency if key and value are swapped
                Collection<String> checkKey = dependencyMapping.get(inputResult.get(i+1));
                String checkVal = inputResult.get(i);
                Boolean equal = checkKey.contains(checkVal);

                // If equal returns true b is already dependent on a and the command should be ignored, otherwise create dependency
                if (equal) {
                    System.out.println(inputResult.get(i+1) + " is dependent on " + inputResult.get(i) +", ignoring");
                } else {
                    String key = inputResult.get(i);
                    String value = inputResult.get(i + 1);
                    dependencyMapping.put(key, value);
                }
            }
        }
    }

    // Check if an item is installed and if not install it
    // Will also check dependencies and install them if not already present.
    public static void installItem(String singleResult) {
        if (installedItems.contains(singleResult)) {
            System.out.println(singleResult + " is already installed");
        } else {
            // If there are no dependencies created just install straight away
            if (dependencyMapping.isEmpty()) {
                installedItems.add(singleResult);
                System.out.println("Installing " + singleResult);
            } else {
                // Check dependencies and build dependency stack
                Stack<String> dependStack = new Stack<>();
                dependStack.push(singleResult);
                while (!singleResult.isEmpty()) {
                    List <String> keyVals = (List<String>) dependencyMapping.get(dependStack.peek());
                    for (String s : keyVals) {
                        dependStack.push(s);
                    }
                    if (dependencyMapping.get(dependStack.peek()).isEmpty()) {
                        break;
                    }
                }

                // check if dependencies are already installed, install items not yet installed.
                while (!dependStack.empty()) {
                    String dependent = dependStack.peek();
                    if (installedItems.contains(dependent)) {
                        dependStack.pop();
                    } else {
                        installedItems.add(dependent);
                        System.out.println("Installing " + dependent);
                        dependStack.pop();
                    }
                }
            }
        }
    }

    // Checks if an item is installed and removes it, otherwise declare it is still needed
    public static void removeItem(String singleResult) {
        Set<String> removeList = new HashSet<>();
        List<String> dependChain = new ArrayList<>();
        List<String> dependTree = new ArrayList<>();
        removeList.add(singleResult);
        for (String item : installedItems) {
            // If there are no dependencies for the item removed the recursion should be escaped
            if (dependencyMapping.get(item).isEmpty()){
                break;
            }
            // otherwise we need to feel out every installed item dependency path to the item being removed
            removeList.add(item);
            dependTree.clear();
            String dependKey = String.valueOf(dependencyMapping.get(item));
            String trimKey = dependKey.substring(1, dependKey.length() - 1);
            String lastValue = null;
            if (!trimKey.isEmpty()) {
                dependChain.add(dependKey);
            }
            for (String depend : dependChain) {
                dependTree.add(depend.substring(1, depend.length() - 1));
                lastValue = depend;
            }
            if (lastValue == null) {
                // catch if lastValue is null
            } else if ((lastValue.substring(1, lastValue.length() - 1)) == singleResult) {
                for (String val : dependTree) {
                    removeList.add(val);
                }
            }

        }
        // remove all items with dependencies on singleResult
        for (String removeMe : removeList) {
            if (!installedItems.contains(removeMe)) {
                System.out.println(removeMe + " is still needed.");
            } else {
                installedItems.remove(removeMe);
                System.out.println("Removing " + removeMe);
            }
        }
    }

    // lists installed components.
    public static void showList() {
        for (String s : installedItems) {
            System.out.println(s);
        }
    }

    public static void main(String[] args) throws IOException {

        inputItems.add("DEPEND TELNET TCPIP NETCARD");
        inputItems.add("DEPEND TCPIP NETCARD");
        inputItems.add("DEPEND NETCARD TCPIP");
        inputItems.add("DEPEND DNS TCPIP NETCARD");
        inputItems.add("DEPEND BROWSER TCPIP HTML");
        inputItems.add("INSTALL NETCARD");
        inputItems.add("INSTALL TELNET");
        inputItems.add("INSTALL foo");
        inputItems.add("REMOVE NETCARD");
        inputItems.add("INSTALL BROWSER");
        inputItems.add("INSTALL DNS");
        inputItems.add("LIST");
        inputItems.add("REMOVE TELNET");
        inputItems.add("REMOVE NETCARD");
        inputItems.add("REMOVE DNS");
        inputItems.add("REMOVE NETCARD");
        inputItems.add("INSTALL NETCARD");
        inputItems.add("REMOVE TCPIP");
        inputItems.add("REMOVE BROWSER");
        inputItems.add("REMOVE TCPIP");
        inputItems.add("LIST");
        inputItems.add("END");

        // Start a loop to request input from stream until

        for (int i = 0; i < inputItems.size(); i++) {
            System.out.println(inputItems.get(i));
            String text = inputItems.get(i);

            // list initialized to store multiple items from input results
            List<String> inputResult = new ArrayList<>();

            // DEPEND log to check how many items are specified, text(n) is specified as dependencies for text(0)
            if (text.startsWith("DEPEND")) {

                String[] result = text.split("\\s");
                for (int x=0; x < result.length; x++) {
                    if (x>0) {
                        inputResult.add(result[x]);
                    }
                }
                dependOn(inputResult);
            }

            // INSTALL only accepts one input item, stored as singleResult and passed to install function.
            if (text.startsWith("INSTALL")) {
                String[] result = text.split("\\s");
                for (int x=0; x<result.length; x++) {
                    if (x==1) {
                        singleResult = result[x];
                    }
                }
                installItem(singleResult);
            }

            // REMOVE only accepts one input item, stored as singleResult and passed to install function.
            if (text.startsWith("REMOVE")) {

                String[] result = text.split("\\s");
                for (int x=0; x<result.length; x++) {
                    if (x==1) {
                        singleResult = result[x];
                    }
                }
                removeItem(singleResult);
            }

            if (text.equals("LIST")) {
                showList();
            }

            if (text.equals("END")) {
                break;
            }

        }
    }
}





