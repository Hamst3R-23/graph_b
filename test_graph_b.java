package vlad;

public class test_graph_b {
    public static void main(String[] args) {
        try {

            System.out.println("Tarjan's algorithm:");
            graph_b_2<String> graph1;
            graph1 = new graph_b_2<>("tests/test1.txt");
            System.out.println("Graph from test1.txt:");
            graph1.print();
            System.out.println("Tarjan's algorithm: ");
            System.out.println(graph1.Tarjan());
            System.out.println();
            System.out.println();

            System.out.println("Fleury's algorithm:");
            graph_b_2<String> graph2;
            graph2 = new graph_b_2<>("tests/test2.txt");
            System.out.println("Graph from test2.txt:");
            graph2.print();
            System.out.println("Fleury's algorithm: ");
            System.out.println(graph2.Fleury());
            System.out.println();
            System.out.println();


            System.out.println("Euler cycle search algorithm based on union of cycles:");
            graph_b_2<String> graph3;
            graph3 = new graph_b_2<>("tests/test3.txt");
            System.out.println("Graph from test3.txt:");
            graph3.print();
            System.out.println("Euler cycle search algorithm based on union of cycles:");
            System.out.println(graph3.get_euler_cycle());
            System.out.println();
            System.out.println();


            System.out.println("Kosaraju's algorithm:");
            graph_b_2<String> graph4;
            graph4 = new graph_b_2<>("tests/test4.txt");
            System.out.println("Graph from test4.txt:");
            graph4.print();
            System.out.println("Kosaraju's algorithm: ");
            System.out.println(graph4.Kosaraju());
            System.out.println();

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}