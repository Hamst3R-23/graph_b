package vlad;

import java.io.File;
import java.util.*;

public class graph_b_2<T> implements graph_b_1<T>{
    //using HashMap for graph
    private HashMap<T, ArrayList<adjacent_vertex<T>>> graph;

    //default constructor
    public graph_b_2() {
        graph = new HashMap<>();
    }

    //file constructor
    public graph_b_2(String fileName) throws Exception {
        Scanner scanner;
        File file = new File(fileName);

        //size - 3, because: vertex name + space + weight
        if(!file.exists())
            throw new Exception("file doesn't exist");

        if(file.length() < 3)
            throw new Exception("wrong format of file");

        scanner = new Scanner(file);

        //all the lines of the file
        List<String[]> new_file = new ArrayList<>();

        //weights of the edges
        List<ArrayList<Integer>> edges_weight = new ArrayList<>();

        //names
        List<T> names = new ArrayList<>();

        while(scanner.hasNextLine())
            new_file.add(scanner.nextLine().split("\\s"));
        scanner.close();

        for(int i = 0; i < new_file.size(); i++)
            names.add((T) new_file.get(i)[0]);

        for(int i = 0; i < new_file.size(); i++) {
            edges_weight.add(new ArrayList<>());
            for(int j = 1; j < new_file.get(i).length; j++) {
                int value = Integer.parseInt(new_file.get(i)[j]);
                edges_weight.get(i).add(value);
            }
        }

        for(ArrayList<Integer> list : edges_weight)
            if(list.size() != new_file.size())
                throw new Exception("matrix is not square!");

        //creating a graph
        graph = new HashMap<>();
        for(int i = 0; i < edges_weight.size(); i++) {
            graph.put(names.get(i), new ArrayList<>());
            for(int j = 0; j < edges_weight.get(i).size(); j++)
                if(edges_weight.get(i).get(j) != 0)
                    graph.get(names.get(i)).add(new adjacent_vertex<>(names.get(j), edges_weight.get(i).get(j)));
        }
    }

    public graph_b_2(graph_b_2<T> copy) {
        graph = new HashMap<>();
        for(T src : copy.graph.keySet()) {
            graph.put(src, new ArrayList<>());
            for (adjacent_vertex<T> dst : copy.graph.get(src))
                graph.get(src).add(new adjacent_vertex<>(dst.get_name(), dst.get_weight()));
        }
    }



    //add vertex(
    public void add_vertex(T vertex_name) throws Exception{
        if(graph.containsKey(vertex_name) || vertex_name == null)
            throw new Exception("already have this vertex or put nothing");
        graph.put(vertex_name, new ArrayList<>());
    }

    //add oriented edge with from "start" to "end"
    public void add_edge(T start, T end, int weight) throws Exception {
        if(start == null || end == null || weight == 0)
            throw new Exception("put nothing or weight 0");
        if(!graph.containsKey(start) && !graph.containsKey(end)) {
            graph.put(start, new ArrayList<>());
            graph.put(end, new ArrayList<>());
        }
        else if(graph.containsKey(start) && !graph.containsKey(end))
            graph.put(end, new ArrayList<>());
        else if(!graph.containsKey(start) && graph.containsKey(end))
            graph.put(start, new ArrayList<>());
        else {
            for(adjacent_vertex<T> vertex : graph.get(start)){
                if(vertex.get_name() == end) {
                    vertex.set_weight(weight);
                    return;
                }
            }
        }
        graph.get(start).add(new adjacent_vertex<>(end, weight));
    }

    //adds non-oriented edge from "start" to "end"
    public void add_nonoriented_edge(T start, T end, int weight) throws Exception {
        add_edge(start, end, weight);
        add_edge(end, start, weight);
    }

    //remove vertex
    public void remove_vertex(T vertex_name) throws Exception{
        if(graph.isEmpty())
            throw new Exception("graph is empty");
        if(!graph.containsKey(vertex_name))
            throw new Exception("vertex with this name doesn't exist");
        graph.remove(vertex_name);
        for(T key : graph.keySet()) {
            for(adjacent_vertex<T> vertex : graph.get(key)) {
                if(vertex_name == vertex.get_name()) {
                    graph.get(key).remove(vertex);
                    break;
                }
            }
        }
    }

    //remove edge with start vertex "start" and end vertex "end"
    public void remove_edge(T start, T end) throws Exception{
        if(graph.isEmpty())
            throw new Exception("graph is empty");
        if(start == null || end == null)
            throw new Exception("put nothing");
        if(!graph.containsKey(start) || !graph.containsKey(end))
            throw new Exception("error in vertices of graph!");
        for(adjacent_vertex<T> vertex : graph.get(start)) {
            if(vertex.get_name() == end) {
                graph.get(start).remove(vertex);
                return;
            }
        }
        throw new Exception("no this edge in graph");
    }

    //remove non-oriented edge with start vertex "start" and end vertex "end"
    public void remove_nonoriented_edge(T start, T end) throws Exception{
        remove_edge(start, end);
        remove_edge(end, start);
    }

    //Tarjan's algorithm
    public Stack<T> Tarjan() throws Exception {
        if(graph.isEmpty())
            throw new Exception("graph is empty");

        HashMap<T, Boolean> visit = new HashMap<>();
        Stack<T> stack = new Stack<>();
        for(T key : graph.keySet())
            visit.put(key, false);

        for(T key : graph.keySet())
            if(!visit.get(key))
                DFS_1(key, visit, stack);

        Collections.reverse(stack);
        return stack;
    }

    //Checking for Eulerian cycle (incoming edges == outgoing edges)
    private boolean Eulerian_cycle() {
        for(T vertex_name : graph.keySet()) {
            //number of incoming edges
            int tmp = 0; //number of incoming edges
            for(T src : graph.keySet())
                if(src != vertex_name)
                    for(adjacent_vertex<T> dst : graph.get(src))
                        if (vertex_name == dst.get_name()) {
                            tmp++;
                            break;
                        }
            if(tmp != graph.get(vertex_name).size())
                return false;
        }
        return true;
    }

    //Fleury's algorithm
    public ArrayList<T> Fleury() throws Exception {
        if(graph.isEmpty())
            throw new Exception("graph is empty");

        if(!Eulerian_cycle())
            return null;

        ArrayList<T> result = new ArrayList<>();
        graph_b_2<T> current_graph = new graph_b_2<>(this);
        T current_vertex = get_start_vertex();

        result.add(current_vertex);
        Fleury(current_graph, current_vertex, result);

        if(result.size() != (getNumOfEdges() + 1))
            result = null;//if all the edges have not been bypassed -> no Euler cycle

        return result;
    }

    private void Fleury(graph_b_2<T> currentGraph, T currentVertex, ArrayList<T> result) throws Exception {
        graph_b_2<T> graphCopy = new graph_b_2<>(currentGraph);
        if(graphCopy.graph.get(currentVertex).size() == 0)
            return;
        for(adjacent_vertex<T> dst : graphCopy.graph.get(currentVertex)) {
            if(currentGraph.graph.get(currentVertex).size() == 1 || !bridge(new Edge<>(currentVertex, dst.get_name(), dst.get_weight()), currentGraph)) {
                result.add(dst.get_name());
                currentGraph.remove_edge(currentVertex, dst.get_name());
                currentVertex = dst.get_name();
                Fleury(currentGraph, currentVertex, result);
                return;
            }
        }
    }

    //returns starting vertex for searching Eulerian cycle
    private T get_start_vertex() {
        T vertex_name = null;
        for(T key : graph.keySet()) {
            vertex_name = key;
            break;
        }
        return vertex_name;
    }

    //Method that returns the number of edges of a graph
    private int getNumOfEdges() {
        int tmp = 0;//number of edges
        for(T start : graph.keySet())
            for(adjacent_vertex<T> end : graph.get(start))
                tmp++;
        return tmp;
    }

    //cheking for bridge
    private boolean bridge(Edge<T> edge, graph_b_2<T> current_graph) throws Exception {
        int first_length = current_graph.DFS(edge.get_start()).size();
        current_graph.remove_edge(edge.get_start(), edge.get_end());

        int second_length = current_graph.DFS(edge.get_end()).size();
        current_graph.add_edge(edge.get_start(), edge.get_end(), edge.getWeight());

        return first_length != second_length;//comparing number of connectivity components
    }

    //Euler cycle search algorithm based on union of cycles
    public ArrayList<T> get_euler_cycle() throws Exception {
        if(graph.isEmpty())
            throw new Exception("graph is empty");

        if(!Eulerian_cycle())
            return null;

        ArrayList<T> result = new ArrayList<>();
        T currentVertex = get_start_vertex();
        graph_b_2<T> currentGraph = new graph_b_2<>(this);

        get_euler_cycle(currentVertex, result, currentGraph);
        Collections.reverse(result);

        if(result.size() != (getNumOfEdges() + 1))
            result = null;//if all the edges have not been bypassed -> no Euler cycle

        return result;
    }

    private void get_euler_cycle(T current_vertex, ArrayList<T> result, graph_b_2<T> current_graph) throws Exception {
        Stack<T> euler_cycle = new Stack<>();
        euler_cycle.push(current_vertex);
        while(!euler_cycle.isEmpty()) {
            current_vertex = euler_cycle.peek();
            if(current_graph.graph.get(current_vertex).size() == 0) {
                result.add(current_vertex);
                euler_cycle.pop();
            }
            else {
                T new_Vertex = current_graph.graph.get(current_vertex).get(0).get_name();
                current_graph.remove_edge(current_vertex, new_Vertex);
                euler_cycle.push(new_Vertex);
            }
        }
    }

    //return graph with inverted edges
    private graph_b_2<T> Inverted_graph() throws Exception {
        graph_b_2<T> invertedGraph = new graph_b_2<>();
        for(T src : graph.keySet())
            for(adjacent_vertex<T> dst : graph.get(src))
                invertedGraph.add_edge(dst.get_name(), src, dst.get_weight());
        return invertedGraph;
    }

    //Method that performs the Kosaraju's algorithm
    public ArrayList<ArrayList<T>> Kosaraju() throws Exception {
        if(graph.isEmpty())
            throw new Exception("graph is empty");

        graph_b_2<T> inverted_graph = Inverted_graph();
        Stack<T> stack = new Stack<>();

        HashMap<T, Boolean> visit = new HashMap<>();
        for(T key : graph.keySet())
            visit.put(key, false);

        for(T key : inverted_graph.graph.keySet())
            if(!visit.get(key))
                DFS_1(key, visit, stack);
        Collections.reverse(stack);

        ArrayList<ArrayList<T>> result = new ArrayList<>();

        for(T key : graph.keySet())
            visit.put(key, false);

        while(!stack.isEmpty()) {
            T vertex_name = stack.pop();
            if(!visit.get(vertex_name))
                result.add(DFS(vertex_name, visit, new ArrayList<>()));
        }
        return result;
    }

    //DFS algorithm
    private ArrayList<T> DFS(T vertex_name) {
        ArrayList<T> visit = new ArrayList<>();
        DFS(vertex_name, visit);
        return visit;
    }

    private void DFS(T vertex_name, ArrayList<T> visit) {
        visit.add(vertex_name);
        for(adjacent_vertex<T> vertex : graph.get(vertex_name)) {
            if(!visit.contains(vertex.get_name()))
                DFS(vertex.get_name(), visit);
        }
    }

    private ArrayList<T> DFS(T vertex_name, HashMap<T, Boolean> visit, ArrayList<T> result) {
        visit.replace(vertex_name, true);
        result.add(vertex_name);
        for(adjacent_vertex<T> vertex : graph.get(vertex_name))
            if(!visit.get(vertex.get_name()))
                DFS(vertex.get_name(), visit, result);
        return result;
    }

    //DFS algorithm with time fixing
    private void DFS_1(T vertex_name, HashMap<T, Boolean> visit, Stack<T> result) {
        visit.replace(vertex_name, true);
        for(adjacent_vertex<T> vertex : graph.get(vertex_name)) {
            if(!visit.get(vertex.get_name()))
                DFS_1(vertex.get_name(), visit, result);
        }
        result.push(vertex_name);
    }

    //print graph
    public void print() {
        for(T key : graph.keySet()) {
            System.out.print(key + ": ");
            for(adjacent_vertex<T> vertex : graph.get(key))
                System.out.print(key + " -> " + vertex.get_name() + "(" + vertex.get_weight() + "); ");
            System.out.println();
        }
        System.out.println();
    }
}

//class of edge
class Edge<T> {
    private T start;
    private T end;
    private int weight;

    public Edge(T start, T end, int weight) {
        this.start = start;
        this.end = end;
        this.weight = weight;
    }

    public T get_start() {
        return start;
    }

    public T get_end() {
        return end;
    }

    public int getWeight() {
        return weight;
    }
}

//Class of adjacent vertex
class adjacent_vertex<T> {
    private T name;

    private int weight;

    public adjacent_vertex(T name, int weight) {
        this.name = name;
        this.weight = weight;
    }

    public T get_name() {
        return name;
    }

    public int get_weight() {
        return weight;
    }

    public void set_weight(int weight) {
        this.weight =  weight;
    }
}