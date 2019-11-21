import java.util.*;
import java.io.*;
import java.util.regex.*;
//contains the Node class for creating the node object
class Nodes
{
        
     int degree = 0;
     Nodes left, right, child, parent;
     boolean mark = false;
     private String keyWord;
     int key;

     Nodes(String keyWord,int key)
     {
        this.left = this;
        this.right = this;
        this.degree = 0;
        this.keyWord = keyWord;
        this.parent = null;
        this.key = key;

     }

     public  String  getKeyWord(){
         return this.keyWord;
     }

 }
class MaxFibonacciHeap
{
	
    private int numOfNodes;
    private Nodes maximumNode;
    

    //Inserting a new node into the max fibonacci heap
    public void nodeInsert(Nodes node)
    {


        //check if max node is not null
        if (maximumNode != null) {

            //add to the right of max node
            node.left = maximumNode;
            node.right = maximumNode.right;
            maximumNode.right = node;

            //check if node right is not null
            if ( node.right!=null) {                               
                node.right.left = node;
            }
            if ( node.right==null)
            {
                node.right = maximumNode;
                maximumNode.left = node;
            }
            if (node.key > maximumNode.key) {
                maximumNode = node;
            }
        } else {
            maximumNode = node;

        }

        numOfNodes++;
    }
    
    //Increase the value of key for the given node in max fibonacci heap
    public void increasingKey(Nodes a, int k)
    {
        if (k < a.key) {
        }

        a.key = k;

        Nodes b = a.parent;

        if ((b != null) && (a.key > b.key)) {
            cut(a, b);
            cascadingCut(b);
        }

        if (a.key > maximumNode.key) {
            maximumNode = a;
        }
    }
    
    //Removes the maximum from the max fibonacci heap
    public Nodes removeMax()
    {
        Nodes c = maximumNode;
        if (c != null) {
            int numOfChildren = c.degree;
            Nodes a = c.child;
            Nodes tempRight;

            //while there are children of max
            while (numOfChildren > 0) {
                tempRight = a.right;

                // remove a from child list
                a.left.right = a.right;
                a.right.left = a.left;

                // add a to root list of heap
                a.left = maximumNode;
                a.right = maximumNode.right;
                maximumNode.right = a;
                a.right.left = a;

                // set parent to null
                a.parent = null;
                a = tempRight;
                //decrease number of children of max
                numOfChildren--;

            }


            // remove c from root list of max fibonacci heap
            c.left.right = c.right;
            c.right.left = c.left;

            if (c == c.right) {
                maximumNode = null;

            } else {
               maximumNode = c.right;
               degreeWiseMerging();
           }
           numOfNodes--;
           return c;
       }
        return null;
    }

    //performs cut operation. Cuts a from b
    public void cut(Nodes a, Nodes b)
    {
        // removes a from child of b and decreases the degree of b
        a.left.right = a.right;
        a.right.left = a.left;
        b.degree--;

        // reset b.child if necessary
        if (b.child == a) {
            b.child = a.right;
        }

        if (b.degree == 0) {
            b.child = null;
        }

        // add a to root list of max fibonacci heap
        a.left = maximumNode;
        a.right = maximumNode.right;
        maximumNode.right = a;
        a.right.left = a;

        // set parent of a to nil
        a.parent = null;

        // set mark to false
        a.mark = false;
    }

    //Performs cascading cut on the given node as given in Cormen 
    public void cascadingCut(Nodes b)
    {
        Nodes a = b.parent;

        //if there is a parent
        if (a != null) {
            // if b is unmarked, set it marked
            if (!b.mark) {
                b.mark = true;
            } else {
                // it's marked, cut it from parent
                cut(b, a);

                // cut its parent as well
                cascadingCut(a);
            }
        }
    }

    //if two degrees are same, then it merges it
    public void degreeWiseMerging()
    {
        //read on internet that 45 is most optimised, else can be calculated using the formulae given in cormen
        int sizeofDegreeTable =45;


        List<Nodes> degreeTable =
        new ArrayList<Nodes>(sizeofDegreeTable);

        // Initializing degree table
        int i = 0;
        while(i < sizeofDegreeTable) {
            degreeTable.add(null);
            i++;
        }
                       


        // Finding the number of root nodes.
        int numberOfRoots = 0;
        Nodes a = maximumNode;


        if (a != null) {
            numberOfRoots++;
            a = a.right;                     

            while (a != maximumNode) {
                numberOfRoots++;
                a = a.right;
            }
        }

        // For each node in root list 
        while (numberOfRoots > 0) {

            int d = a.degree;
            Nodes next = a.right;

            // check if the degree is there in degree table, if yes then combine and merge else add
            for (;;) {
                Nodes b = degreeTable.get(d);
                if (b == null) {
                    break;
                }

                //Check whose key value is greater
                if (a.key < b.key) {
                    Nodes temp = b;
                    b = a;
                    a = temp;
                }

                //making 'b' the child of 'a' as 'a' key value is greater
                makeChild(b, a);

                //set the degree to null as a and b are combined now
                degreeTable.set(d, null);
                d++;
            }

            //store the new a(a+b) in the respective degree table position
            degreeTable.set(d, a);

            // iterating through list.
            a = next;
            numberOfRoots--;
        }



        
        //Deleting the maximum node
        maximumNode = null;

        // combine entries of the degree table
        //int i = 0;
        for(i=0; i < sizeofDegreeTable; i++) {
            Nodes b = degreeTable.get(i);
            if (b == null) {
                continue;
            }

            //till max node is not null
            if (maximumNode != null) {

                // First remove node from root list.
                b.left.right = b.right;
                b.right.left = b.left;

                // Now addding to root list again.
                b.left = maximumNode;
                b.right = maximumNode.right;
                maximumNode.right = b;
                b.right.left = b;

                // Checking if this is the new maximum
                if (b.key > maximumNode.key) {
                    maximumNode = b;
                }
            } else {
                maximumNode = b;
            }
        }
    }

    //Makes b the child of node a
    public void makeChild(Nodes b, Nodes a)
    {
        // removing b from root list of max fibonacci heap
        b.left.right = b.right;
        b.right.left = b.left;

        // making 'b' the child of 'a'
        b.parent = a;

        if (a.child == null) {
            a.child = b;
            b.right = b;
            b.left = b;
        } else {
            b.left = a.child;
            b.right = a.child.right;
            a.child.right = b;
            b.right.left = b;
        }

        // increasing degree of 'a' by 1
        a.degree++;

        // marking 'b' as false
        b.mark = false;
    }

}
public class keywordcounter{


    public static void main(String[] args){

        //Timer for calculating duration
      long start = System.currentTimeMillis();

      //Hash Map for Storing the keyWord and the node
      HashMap<String,Nodes> hm = new HashMap();

        //Creating an object of the Max Fibonacci Heap
      MaxFibonacciHeap fh = new MaxFibonacciHeap();

        //Reading file as input
      String  path = args[0];

        //Output File & writer pointer for writing to the output file
      File file = new File("output_file.txt");
      BufferedWriter writer=null;

        // try to check IOException and other unchecked exception(s)
      try {

        BufferedReader br = new BufferedReader(new FileReader(path));
        String str = br.readLine();

        Pattern pa1 = Pattern.compile("([$])([a-z_]+)(\\s)(\\d+)");
        Pattern pa2 = Pattern.compile("(\\d+)");
        writer = new BufferedWriter( new FileWriter(file));

        while (str != null) {

        	if(str.equalsIgnoreCase("stop"))
        	{
        		break;
        	}

            Matcher ma1 = pa1.matcher(str);
            Matcher ma2 = pa2.matcher(str);


            if (ma1.find()) {

                String keyWord1 = ma1.group(2);
                int key = Integer.parseInt(ma1.group(4));

                    //Check if it contains the key
                if ( !hm.containsKey(keyWord1))
                {
                    //Create new node and nodeInsert in Max Fibonacci Heap and Hash Map
                    Nodes node = new Nodes(keyWord1,key);
                    fh.nodeInsert(node);
                    hm.put(keyWord1,node);


                }
                else
                {
                   //if already in hashmap then call increase key present in max fibonacci heap
                    int increasingKey = hm.get(keyWord1).key + key;
                    fh.increasingKey(hm.get(keyWord1),increasingKey);
                }
            } else if (ma2.find()) {

                    //Count of number of Nodes to be removed
                int removeNum = Integer.parseInt(ma2.group(1));

                    //Removed Nodes
                ArrayList<Nodes> remNodes = new ArrayList<Nodes>(removeNum);

                int i = 0;
                while(i<removeNum)
                {

                        //Removed Nodes
                    Nodes node = fh.removeMax();

                        //remove from hashmap
                    hm.remove(node.getKeyWord());

                        //Create new node for insertion
                    Nodes newNode= new Nodes(node.getKeyWord(),node.key);

                        //add the new node for insertion into removed nodes list
                    remNodes.add(newNode);

                        //Add the , until the last hashTag
                    if ( i <removeNum-1) {
                        writer.write(node.getKeyWord() + ",");

                    }

                    else {

                        writer.write(node.getKeyWord());


                    }
                  i++;
                }

                    //insertion step
                for ( Nodes iter : remNodes)

                {

                    fh.nodeInsert(iter);
                    hm.put(iter.getKeyWord(),iter);


                }

                    //go to new line in writer pointer
                writer.newLine();
            }

                //Go to Next Line
            str = br.readLine();
        }
    }

    catch(Exception e){
        System.out.println(e);
    }
        //Close the writer
    finally {
        if ( writer != null ) {
            try {
                writer.close();
            } catch (IOException ioe2) {

            }
        }
    }

        //Print the time/duration required
    long end   = System.currentTimeMillis();
    long totalTime = end - start;
    System.out.println(" Total time in Milli Seconds: "+ totalTime);


    }


}
