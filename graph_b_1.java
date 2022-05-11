package vlad;

import java.util.ArrayList;

public interface graph_b_1<T> {
    //add vertex
    void add_vertex(T vertexName) throws Exception;

    //remove vertex
    void remove_vertex(T vertexName) throws Exception;

    //add oriented edge connecting "start" and "end"
    void add_edge(T src, T dst, int weight) throws Exception;

    //add non-oriented edge connecting "start" and "end"
    void add_nonoriented_edge(T src, T dst, int weight) throws Exception;

    //remove edge from "start" to "end"
    void remove_edge(T src, T dst) throws Exception;

    //removes non-oriented edge between "start" and "end"
    void remove_nonoriented_edge(T src, T dst) throws Exception;

    //Method that displays the graph on the screen
    void print();
}