


package video.search.query.reformulation.pagerank;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.jgraph.graph.DefaultEdge;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

public class WordNetworkMaker {
	
	ArrayList<String> sentences;
	public SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> wgraph;
	public DirectedGraph<String, DefaultEdge> graph;
	HashMap<String, Double> tokendb;
	final int WINDOW_SIZE = 2;
	final int TOPK_SIZE = 5;
	HashMap<String, Integer> coocCountMap;

	public WordNetworkMaker(ArrayList<String> sentences) {
		// initializing both graphs
		this.sentences = sentences;
		this.wgraph = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		this.graph = new DefaultDirectedGraph<>(DefaultEdge.class);
		this.tokendb = new HashMap<>();
		this.coocCountMap = new HashMap<>();
	}

	public DirectedGraph<String, DefaultEdge> createWordNetwork() {
		// developing the word network
		for (String sentence : this.sentences) {
			String[] tokens = sentence.split("\\s+");
			for (int index = 0; index < tokens.length; index++) {
				String previousToken = new String();
				String nextToken = new String();
				String currentToken = tokens[index];
				if (index > 0)
					previousToken = tokens[index - 1];

				if (index < tokens.length - 1)
					nextToken = tokens[index + 1];

				// now add the graph nodes
				if (!graph.containsVertex(currentToken)) {
					graph.addVertex(currentToken);
				}
				if (!graph.containsVertex(previousToken) && !previousToken.isEmpty()) {
					graph.addVertex(previousToken);
				}
				if (!graph.containsVertex(nextToken) && !nextToken.isEmpty()) {
					graph.addVertex(nextToken);
				}

				// adding edges to the graph
				if (!previousToken.isEmpty())
					if (!graph.containsEdge(currentToken, previousToken)) {
						graph.addEdge(currentToken, previousToken);
					}

				if (!nextToken.isEmpty())
					if (!graph.containsEdge(currentToken, nextToken)) {
						graph.addEdge(currentToken, nextToken);
					}
			}
		}
		// returning the created graph
		return graph;
	}

	protected void setEdgeWeight() {
		Set<DefaultWeightedEdge> edges = this.wgraph.edgeSet();
		for (DefaultWeightedEdge edge : edges) {
			String source = wgraph.getEdgeSource(edge);
			String dest = wgraph.getEdgeTarget(edge);
			String keypair = source + "-" + dest;
			if (coocCountMap.containsKey(keypair)) {
				this.wgraph.setEdgeWeight(edge, (double) coocCountMap.get(keypair));
			}
		}
	}

	protected void updateCooccCount(String source, String dest) {
		// updating the co-occurrence count
		String keypair = source + "-" + dest;
		if (this.coocCountMap.containsKey(keypair)) {
			int updated = coocCountMap.get(keypair) + 1;
			this.coocCountMap.put(keypair, updated);
		} else {
			this.coocCountMap.put(keypair, 1);
		}
	}

	public SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> createWeightedWordNetwork() {
		// developing the word network
		for (String sentence : this.sentences) {
			String[] tokens = sentence.split("\\s+");
			for (int index = 0; index < tokens.length; index++) {
				String previousToken = new String();
				String nextToken = new String();
				String currentToken = tokens[index];
				if (index > 0)
					previousToken = tokens[index - 1];

				if (index < tokens.length - 1)
					nextToken = tokens[index + 1];

				// now add the graph nodes
				if (!wgraph.containsVertex(currentToken)) {
					wgraph.addVertex(currentToken);
				}
				if (!wgraph.containsVertex(previousToken) && !previousToken.isEmpty()) {
					wgraph.addVertex(previousToken);
				}
				if (!wgraph.containsVertex(nextToken) && !nextToken.isEmpty()) {
					wgraph.addVertex(nextToken);
				}

				// adding edges to the graph
				if (!previousToken.isEmpty() && !currentToken.equals(previousToken)) {
					if (!wgraph.containsEdge(currentToken, previousToken)) {
						DefaultWeightedEdge e = wgraph.addEdge(currentToken, previousToken);
					}
					updateCooccCount(currentToken, previousToken);
				}
				if (!nextToken.isEmpty() && !currentToken.equals(nextToken)) {
					if (!wgraph.containsEdge(currentToken, nextToken)) {
						DefaultWeightedEdge e = wgraph.addEdge(currentToken, nextToken);
					}
					updateCooccCount(currentToken, nextToken);
				}
			}
		}

		// setting edge weight
		this.setEdgeWeight();

		// returning the created graph
		return wgraph;
	}

	public HashMap<String, Double> getTokenDictionary(boolean weighted) {
		// populating token dictionary
		HashSet<String> nodes = new HashSet<>();
		if (weighted)
			nodes.addAll(wgraph.vertexSet());
		else
			nodes.addAll(graph.vertexSet());
		for (String vertex : nodes) {
			this.tokendb.put(vertex, 0.0);
		}
		return this.tokendb;
	}

	public void showEdges(HashMap<String, Double> tokendb) {
		// showing the network edges
		if (graph != null) {
			Set<DefaultEdge> edges = graph.edgeSet();
			ArrayList<DefaultEdge> edgeList = new ArrayList<>(edges);
			for (DefaultEdge edge : edgeList) {
				System.out.println(graph.getEdgeSource(edge) + "---" + graph.getEdgeTarget(edge));
			}
		}
	}

	public static void main(String[] args) {
		// main method
		ArrayList<String> sentences = new ArrayList<String>();
		sentences.add("This a video segment extraction project!");
		sentences.add("It has ups and down!");
		sentences.add("But it is going to succeed hopefully at the end!");
		sentences.add("Now we are testing with Page Rank based query expansion method!");

		WordNetworkMaker wnMaker = new WordNetworkMaker(sentences);
		wnMaker.createWordNetwork();
		HashMap<String, Double> tokendb = wnMaker.getTokenDictionary(false);
		wnMaker.showEdges(tokendb);
	}
}
