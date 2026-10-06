package org.example;

import java.util.*;

public class ChoreGraph {
    private final Map<String, List<String>> adjacencyList;

    public ChoreGraph() { this.adjacencyList = new HashMap<>(); }

    // Add a chore vertex to the graph
    public void addChore(String chore) { adjacencyList.putIfAbsent(chore, new ArrayList<>()); }

    // Prerequisite must be completed before dependent
    public void addDependency(String prerequisite, String dependent) {
        addChore(prerequisite);
        addChore(dependent);
        adjacencyList.get(prerequisite).add(dependent);
    }

    // Cycle Detection using DFS: Returns true if a circular dependency cycle exists (e.g., A - B - C - A)
    public boolean hasCycle() {
        Set<String> visited = new HashSet<>();
        Set<String> recStack = new HashSet<>(); // Tracks nodes in the current DFS path

        for (String node : adjacencyList.keySet()) {
            if (dfsCycleCheck(node, visited, recStack)) {
                return true;
            }
        }
        return false;
    }

    private boolean dfsCycleCheck(String current, Set<String> visited, Set<String> recStack) {
        if (recStack.contains(current)) {
            return true; // Reached a node currently in the recursion stack
        }
        if (visited.contains(current)) {
            return false; // Already checked this branch safely
        }

        visited.add(current);
        recStack.add(current);

        for (String neighbor : adjacencyList.get(current)) {
            if (dfsCycleCheck(neighbor, visited, recStack)) {
                return true;
            }
        }

        recStack.remove(current); // Backtrack
        return false;
    }

    // Topological Sort using BFS: Computes a valid linear execution order of chores
    public List<String> getTopologicalOrder() {
        if (hasCycle()) {
            System.out.println("Error: Cannot determine valid task order because of a dependency cycle");
            return new ArrayList<>();
        }

        // Calculate in-degree (number of incoming edges / prerequisites) for each node
        Map<String, Integer> inDegree = new HashMap<>();
        for (String node : adjacencyList.keySet()) {
            inDegree.put(node, 0);
        }
        for (String node : adjacencyList.keySet()) {
            for (String neighbor : adjacencyList.get(node)) {
                inDegree.put(neighbor, inDegree.get(neighbor) + 1);
            }
        }

        // Queue holds all chores that have no prerequisites (in-degree == 0)
        Queue<String> queue = new LinkedList<>();
        for (String node : inDegree.keySet()) {
            if (inDegree.get(node) == 0) {
                queue.add(node);
            }
        }

        List<String> validOrder = new ArrayList<>();

        while (!queue.isEmpty()) {
            String current = queue.poll();
            validOrder.add(current);

            // Reduce in-degree for dependent neighbors
            for (String neighbor : adjacencyList.get(current)) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                // If all prerequisites for neighbor are completed, add to queue
                if (inDegree.get(neighbor) == 0) {
                    queue.add(neighbor);
                }
            }
        }

        return validOrder;
    }

    // Helper to display current dependencies
    public void printDependencies() {
        System.out.println("Chore Dependencies");
        for (String node : adjacencyList.keySet()) {
            System.out.println(node + " -> " + adjacencyList.get(node));
        }
    }
}