# Search-Agorithms
Code Structure:

/*This program implements Uniform Cost Search or A* based on the inputs and tries to find a optimal path
 * that exists between two nodes which are cities in this case.*/

Class find_route contains -
	class Node  /*A class that stores city,the cost between two cities and its parent */
	class CompareStepCost   /*Comparator class implemented by Priority queue to sort according to the g(n) cost*/
	class CompareHeuristicCost 	/*Comparator class implemented by Priority queue to sort according to the f(n) cost*/
	class InputRoutes /*Class that stores Route from the input graph file */
	main method  /*Main method to accept input from user invoke functionality*/
	readInputFile method /*Method that splits Route or graph file, Route file consists of all the routes between cities*/
	readHeuristicCostFile method  /*A method to reach heuristic cost file,This file contains estimated h(n) values for all the nodes or cities used in Route file*/
	uninformedSearch  /*This function performs a Uniform Cost Search or Uninformed search*/
	informedSearch  /*This function performs a A* Search or Informed search*/
	
	Attributes:
	inputRouteList //create a list to store all cities or nodes as objects.
	closedSet  //create  a list to store closed or visited nodes.
	heuristicMap  //Hash map that stores heuristic values in key value format

Class Node
	Attributes:    stepCost;  //step cost or path between 2 cities.
		       heuristicCost;  //heuristics cost(estimated cost) between 2 cities.
		       cityName;    //value of the node that is the city
		       stateParent;    //parent of a current city.



/*********************************************************************/

How to run the code
Pre-requisites- Basic Java
Not checked fro Omega compatibilty

1.Unzip file assignment1_code_1001751112.zip.

2.Open cmd from the location where the above files are stored.

3.Compile the java file by using - javac find_route.java

4.Run the java file for Uninformed search using - java find_route input1.txt Bremen Kassel.

5.Run the java file for Informed search using - java find_route input1.txt Bremen Kassel h_kassel.txt
