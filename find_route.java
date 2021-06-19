import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/*This program implements Uniform Cost Search or A* based on the inputs and tries to find a optimal path
 * that exists between two nodes which are cities in this case.*/
public class find_route {
	public static List<InputRoutes> inputRouteList = new ArrayList<InputRoutes>();//create a list to store all cities or nodes as objects.
	public static List<String> closedSet = new ArrayList<String>(); //create  a list to store closed or visited nodes.
	public static HashMap<String, Float> heuristicMap = new HashMap<String, Float>();//Hash map that stores heuristic values in key value format

	/*A class that stores city,the cost between two cities and its parent */
	class Node{
		float stepCost;  //step cost or path between a 2 cities.
		float heuristicCost;  //heuristics (estimated cost) between 2 cities.
		String cityName;    //value of the node that is the city
		Node stateParent;    //parent of a current city.
		Node(String cityName, Node stateParent, float stepCost)//this constructor is used for uninformed search (UCS)
		{
			this.stepCost = stepCost;
			this.stateParent = stateParent;
			this.cityName = cityName;
		}
		Node(String cityName, Node stateParent, float stepCost, float heuristicCost)//this constructor is used for informed search (A*) 
		{
			this.stepCost = stepCost;
			this.stateParent = stateParent;
			this.cityName = cityName;
			this.heuristicCost = heuristicCost;
		}

	}
	/*Comparator class implemented by Priority queue to sort according to the g(n) cost*/
	class CompareStepCost implements Comparator<Node>{
		public int compare(Node one,Node two)
		{
			if (one.stepCost>two.stepCost) return 1;
			else if (one.stepCost<two.stepCost) return -1;
			else return 0;
		}
	}
	/*Comparator class implemented by Priority queue to sort according to the f(n) cost*/
	class CompareHeuristicCost implements Comparator<Node>{
		public int compare(Node one,Node two)
		{
			if (one.heuristicCost>two.heuristicCost) return 1;
			else if (one.heuristicCost<two.heuristicCost) return -1;
			else return 0;
		}
	}
	/*Main method to accept input from user invoke functionality*/
	public static void main(String[] args) 
	{

		if(args.length == 0)
		{
			System.out.println("ERROR: Please enter the file name as the first commandline argument!");
			System.exit(0);
		} 
		find_route test = new find_route();//Create object of the class find_Route;

		/*If command line arguments accept 4 arguments-a graph file with city 
		 * and costs, start city, destination city and a heuristic file; do a A* or Informed Search*/
		if(args.length==4) 
		{
			String inputFile = args[0];
			String source = args[1];
			String goal = args[2];
			String heuristicCostFile = args[3];
			try {
				test.informedSearch(inputFile,source,goal,heuristicCostFile);//method call to informed (A*) search
			} catch (IOException e) {
				e.printStackTrace();}	
		}

		/*If command line arguments accept 3 arguments-a graph file with city 
		 * and costs, start city & a destination city do a UCS or Uninformed Search*/
		else
		{
			String inputFile = args[0];
			String source = args[1];
			String goal = args[2];
			try {
				test.uninformedSearch(inputFile,source,goal);//method call to uninformed (UCS) search
			} catch (IOException e) {
				e.printStackTrace();}
		}
	}
	/*This method performs A* search or Informed Search*/
	public void informedSearch(String inputFile, String  source, String goal, String heuristicCostFile) throws IOException
	{
		readInputFile(inputFile);//method call to read and store graph file
		readHeuristicCostFile(heuristicCostFile);//method call to read and store heuristics cost file
		int node_Expanded =0, nodeGenerated=0;

		//Initialize priority queue which is a data structure that will sort and store Nodes
		PriorityQueue<Node> fringe = new PriorityQueue<Node>(5000,new CompareHeuristicCost());
		fringe.add(new Node(source,null,0,0));//Initialize and add first node.
		nodeGenerated++;

		List<String> optimalPathList = new ArrayList<String>(); //List of string to store optimal paths
		while(!fringe.isEmpty())//Loop keeps choosing a node till the queue is empty
		{
			Node currentState = fringe.poll();//Retrieves and removes the head of the queue.
			if(currentState.cityName.equalsIgnoreCase(goal))
			{
				System.out.println("nodes expanded "+node_Expanded);//print total nodes expanded
				System.out.println("nodes generated "+nodeGenerated);//print total nodes generated
				System.out.println("distance "+currentState.stepCost);//print total distance

				/*Loop will backtrack to the optimal path it tried to find and store it in a list.
				 *It will also check if the goal city has been reached and will stop the search.*/
				while(currentState.stateParent!=null)
				{
					for(InputRoutes route: inputRouteList)//iterate through route map for back tracing
					{
						if(currentState.cityName.equals(route.dest) && currentState.stateParent.cityName.equals(route.start))
						{
							//Store the Route in a String and add to Optimal Path List
							String finalRoute = currentState.stateParent.cityName +" to "+currentState.cityName+" , "+route.cost;
							optimalPathList.add(finalRoute);						
						}
					}
					//Assign parent to current node and repeat till parent is null or when you reach first node/city
					currentState=currentState.stateParent;
				}

				Collections.reverse(optimalPathList);//Reverse the optimal path list as the route was stored in list via back tracing.
				System.out.println(optimalPathList);//Print the final route.
				break;
			}
			/*This if condition will will add nodes to closed set if not already present 
			 * and will expand the nodes*/
			if(!closedSet.contains(currentState.cityName))//check if a node is not already present in closed set 
			{
				closedSet.add(currentState.cityName);
				node_Expanded++;
				//For loop to iterate over list of route map
				for(InputRoutes cityName:inputRouteList) {

					//Compare nodes and chooses the optimal value to proceed.
					if(cityName.start.equals(currentState.cityName))
					{
						float stepCostSuccessor = cityName.cost+currentState.stepCost;//Add the step cost to the chosen next node
						float heuristicCostSuccessor = stepCostSuccessor + heuristicMap.get(cityName.dest);//Add estimated heuristic h(n) to step costg(n)
						Node sucessorNode = new Node(cityName.dest,currentState,stepCostSuccessor,heuristicCostSuccessor);
						nodeGenerated++;
						fringe.add(sucessorNode);
					}
				}
			}
		}
		if(fringe.isEmpty())//If no path exists between 2 nodes or cities.
		{
			System.out.println("nodes expanded "+node_Expanded);//print total nodes expanded
			System.out.println("nodes generated "+nodeGenerated);//print total nodes generated
			System.out.println("infinity");//print total distance
		}
	}

	/*This function performs a Uniform Cost Search or Uninformed search*/
	public void uninformedSearch(String inputFile, String  source, String goal) throws IOException
	{
		readInputFile(inputFile);//method call to read and store graph file
		int node_Expanded =0, nodeGenerated=0;
		//Initialize priority queue which is a data structure that will sort and store Nodes
		PriorityQueue<Node> fringe = new PriorityQueue<Node>(5000,new CompareStepCost());
		fringe.add(new Node(source,null,0));//Initialize and add first node.
		nodeGenerated++;
		List<String> optimalPathList = new ArrayList<String>(); //List of string to store optimal paths
		while(!fringe.isEmpty())//Loop keeps choosing a node till the queue is empty
		{

			Node currentState = fringe.poll();

			//System.out.println(currentState.cityName+"\n"+currentState.stepCost+"\n"+currentState.stateParent);
			if(currentState.cityName.equalsIgnoreCase(goal))
			{
				System.out.println("nodes expanded "+node_Expanded);//print total nodes expanded
				System.out.println("nodes generated "+nodeGenerated);//print total nodes generated
				System.out.println("distance "+currentState.stepCost);//print total distance

				/*Loop will backtrack to the optimal path it tried to find and store it in a list.
				 *It will also check if the goal city has been reached and will stop the search.*/
				while(currentState.stateParent!=null)
				{
					for(InputRoutes route: inputRouteList)//iterate through route map for back tracing
					{
						if(currentState.cityName.equals(route.dest) && currentState.stateParent.cityName.equals(route.start))
						{
							//Store the Route in a String and add to Optimal Path List
							String finalRoute = currentState.stateParent.cityName +" to "+currentState.cityName+" , "+route.cost;
							optimalPathList.add(finalRoute);
						}
					}
					//Assign parent to current node and repeat till parent is null or when you reach first node/city
					currentState=currentState.stateParent;
				}
				Collections.reverse(optimalPathList);//Reverse the optimal path list as the route was stored in list via back tracing.
				System.out.println(optimalPathList);//Print the final route.
				break;
			}

			/*This if condition will add nodes to closed set if not already present 
			 * and will expand the nodes*/
			if(!closedSet.contains(currentState.cityName))
			{
				closedSet.add(currentState.cityName);
				node_Expanded++;

				for(InputRoutes cityName:inputRouteList) 
				{
					//Compare nodes and choose the optimal value to proceed.
					if(cityName.start.equals(currentState.cityName))
					{
						float stepCostSuccessor = cityName.cost+currentState.stepCost;//Add the step cost to the chosen next node
						Node sucessorNode = new Node(cityName.dest,currentState,stepCostSuccessor);
						nodeGenerated++;
						fringe.add(sucessorNode);
					}

				}
			}
		}
		if(fringe.isEmpty())//If no path exists between 2 nodes or cities.
		{
			System.out.println("nodes expanded "+node_Expanded);//print total nodes expanded
			System.out.println("nodes generated "+nodeGenerated);//print total nodes generated
			System.out.println("infinity");
		}
	}
	/*Class that stores Route from the input graph file */
	public static class InputRoutes
	{
		String start,dest;//start city or start node and destination city or successor city.
		float cost;//step cost to reach next city 
	}

	/*Method that splits Route or graph file
	 * Route file consists of all the routes between cities*/
	private static void readInputFile(String inputFile) throws IOException
	{
		File readInputFile = new File(inputFile);    //read the file
		BufferedReader bufferReader = new BufferedReader(new FileReader(inputFile));
		String line;

		while (!(line=bufferReader.readLine()).equals("END OF INPUT")) //read till it reaches END OF INPUT
		{
			//Create object of Route class to store route from A-B and B-A
			InputRoutes inputRouteOneWay = new InputRoutes();
			InputRoutes inputRouteTwoWay = new InputRoutes();
			//System.out.println(line.split(" ")[0]);
			inputRouteOneWay.start = line.split(" ")[0];
			inputRouteOneWay.dest = line.split(" ")[1];
			inputRouteOneWay.cost = Float.parseFloat(line.split(" ")[2]);
			inputRouteList.add(inputRouteOneWay);

			inputRouteTwoWay.start = line.split(" ")[1];
			inputRouteTwoWay.dest = line.split(" ")[0];
			inputRouteTwoWay.cost = Float.parseFloat(line.split(" ")[2]);
			inputRouteList.add(inputRouteTwoWay);

		}

	}
	/*A method to reach heuristic cost file
	 * This file contains estimated h(n) values for all the nodes or cities used in Route file*/
	private static void readHeuristicCostFile(String inputFile) throws IOException
	{
		File readInputFile = new File(inputFile);    //reading the file
		BufferedReader bufferReader = new BufferedReader(new FileReader(inputFile));
		String line;
		while (!(line=bufferReader.readLine()).equals("END OF INPUT")) //reading till END OF INPUT is encountered
		{	
			//store these vales in a hash map; As all the nodes(key) are unique with heuristic values(value)
			heuristicMap.put(line.split(" ")[0], Float.parseFloat(line.split(" ")[1]));

		}
	}

}
