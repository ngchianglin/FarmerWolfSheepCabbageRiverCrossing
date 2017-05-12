/*
* MIT License
*
*Copyright (c) 2017 Ng Chiang Lin
*
*Permission is hereby granted, free of charge, to any person obtaining a copy
*of this software and associated documentation files (the "Software"), to deal
*in the Software without restriction, including without limitation the rights
*to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
*copies of the Software, and to permit persons to whom the Software is
*furnished to do so, subject to the following conditions:
*
*The above copyright notice and this permission notice shall be included in all
*copies or substantial portions of the Software.
*
*THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
*IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
*FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
*AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
*LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
*OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
*SOFTWARE.
*
*/

/**
 * 
 * Java Application to solve the farmer, wolf, cabage, sheep river crossing puzzle
 * Uses a breadth first search to explore all the state representations and find
 * the solutions. 
 * 
 * The farmer, wolf, cabbage , sheep river crossing puzzle
 * There is a wolf, sheep, cabbage and a farmer with a boat on one bank of a 
 * river. The farmer is only able to carry one occupant on his boat over to
 * the other side of the river. If the wolf and sheep are left alone without 
 * the farmer, the wolf will eat the sheep. If the sheep and cabbage are left
 * alone without the farmer, the sheep will eat the cabbage.
 * How can the farmer bring all of them across the river to the 
 * other bank. 
 * 
 * Possible moves from one bank to the other. 
 * F , FC , FW , FS
 * 
 * where
 * F represents farmer
 * C represents cabbage
 * W represents wolf
 * S represents sheep
 * 
 * Ng Chiang Lin
 * May 2017
 * 
 * 
 */

import java.util.ArrayList;
import java.util.Iterator;

public class FarmerWolfCabbageSheep
{

    // The possible moves that the farmer can make
    private String[] moves = { "F", "FW", "FS", "FC" };
    private ArrayList<Node> queue;
    private ArrayList<Node> solutions;
    private Node root;

    public FarmerWolfCabbageSheep()
    {
        queue = new ArrayList<Node>();
        solutions = new ArrayList<Node>();

    }

    /**
     * Private Inner State class to represent a state A state consists of the
     * occupants (farmer, wolf, sheep, cabbage) on the left and right bank of
     * the river One of the river bank (left or right) is where the farmer is
     * located and from here the farmer can cross the river to the opposite bank
     * optionally bringing along another occupant(wolf , sheep, cabbage)
     * 
     * It is assumed that the initial state is
     * 
     * left bank: farmer(F), wolf(W), cabbage(C), sheep(S) right bank:
     * 
     * All occupants including the farmer is on the left bank and the right bank
     * is empty. The farmer will attempt to move everyone to the right bank
     * through a sequence of crossings subjected to the constraints in the
     * puzzle. Farmer can at most move one more occupant besides
     * himself/herself. Wolf , sheep cannot be together without farmer. Cabbage,
     * sheep cannot be together without farmer. The solution state will be
     * 
     * left bank: right bank: farmer (F), wolf(W), cabbage(C), sheep(S)
     * 
     * The left bank is empty and all the occupants farmer, wolf, cabbage and
     * sheep are on the right bank.
     *
     */
    private class State
    {
        private String bank; // The active bank where the farmer is currently
                             // located
        private ArrayList<String> left, right; // left and right bank with its
                                               // occupants.

        public State(String bank, ArrayList<String> left, ArrayList<String> right)
        {
            this.bank = bank;
            this.left = left;
            this.right = right;
        }

        /**
         * Takes a ArrayList<String> that contains the occupants in a river bank
         * (left or right) and check whether the puzzle constraints Wolf , sheep
         * cannot be together without farmer Cabbage, sheep cannot be together
         * without farmer are met.
         * 
         * @param b An ArrayList<String> representing the river bank with its
         *            occupants
         * @return true if puzzle constraints are met, false otherwise.
         */
        private boolean checkAllowBank(ArrayList<String> b)
        {
            // Wolf and Sheep together without Farmer
            if (b.contains("W") && b.contains("S") && (b.contains("F") == false))
                return false;
            // Sheep and Cabbage together without Farmer
            if (b.contains("S") && b.contains("C") && (b.contains("F") == false))
                return false;

            return true;
        }

        /**
         * Public method to check if a State meets the puzzle constraints.
         * Disallow states are those that doesn't meet the puzzle constraints.
         * 
         * @return true if a State is allowed, false otherwise.
         */
        public boolean isAllow()
        {
            if (checkAllowBank(left) && checkAllowBank(right))
                return true;
            else
                return false;
        }

        /**
         * Check for the solution state where the puzzle is solved.
         * 
         * @return true if it is solution state, false otherwise.
         */
        public boolean isSolution()
        {
            if (left.isEmpty() && right.contains("W") && right.contains("S") && right.contains("C")
                    && right.contains("F"))
                return true;
            else
                return false;
        }

        /**
         * Transit to a new child State based on the move.
         * 
         * @param move ,Parameter containing the moves (F, FW, FS, FC) to
         *            transit to a new child State.
         * @return State , a new child State based on the transition or null if
         *         the move is not allowed.
         */

        public State transits(String move)
        {
            String nbank;
            ArrayList<String> nleft = new ArrayList<String>();
            ArrayList<String> nright = new ArrayList<String>();

            if (bank.equalsIgnoreCase("left"))
                nbank = "right";
            else
                nbank = "left";

            copylist(right, nright);
            copylist(left, nleft);

            for (int i = 0; i < move.length(); i++)
            {
                String item = move.substring(i, i + 1);
                if (bank.equalsIgnoreCase("left"))
                {
                    if (nleft.remove(item))
                        nright.add(item);
                    else
                        return null; // return null if the move contains
                                     // occupants that are not present.
                }
                else
                {
                    if (nright.remove(item))
                        nleft.add(item);
                    else
                        return null; // return null if the move contains
                                     // occupants that are not present.
                }
            }

            return new State(nbank, nleft, nright);

        }

        /**
         * Method to duplicate/copy a representation of the river bank and its
         * occupants from source to destination.
         */
        private void copylist(ArrayList<String> src, ArrayList<String> dst)
        {
            for (String e : src)
                dst.add(e);
        }

        /**
         * Compares current state with a specific state
         * 
         * @param s The State s to compare with
         * @return true if the current and specified state are the same, false
         *         otherwise
         */
        public boolean compare(State s)
        {
            ArrayList<String> tmp;

            if (!s.getBank().equalsIgnoreCase(bank))
                return false;

            tmp = s.getLeft();
            for (String e : left)
            {
                if (!tmp.contains(e))
                    return false;
            }

            tmp = s.getRight();
            for (String e : right)
            {
                if (!tmp.contains(e))
                    return false;
            }

            return true;
        }

        public String getBank()
        {
            return bank;
        }

        public ArrayList<String> getLeft()
        {
            return left;
        }

        public ArrayList<String> getRight()
        {
            return right;
        }

        @Override
        public String toString()
        {
            StringBuffer ret = new StringBuffer();
            ret.append("{L:");

            for (String e : left)
                ret.append(e);

            ret.append(" ");
            ret.append("R:");

            for (String e : right)
                ret.append(e);

            ret.append("}");
            return ret.toString();
        }

    }

    /**
     * Private Inner class Node for constructing the State graph
     */
    private class Node
    {
        public Node parent; // Parent of the node
        public State data; // State of the node
        public ArrayList<Node> adjlist; // Children of the node
        public int level; // Depth of the node
        public String move; // The move (transition) that creates the current
                            // node state.

        public Node(State data)
        {
            parent = null;
            this.data = data;
            adjlist = new ArrayList<Node>();
            level = 0;
            move = "";
        }

        /**
         * Checks if a Node that has the same State is an ancestor of the
         * current Node.
         * 
         * @return true if a an ancestor node has the same state, false
         *         otherwise
         */
        public boolean isAncestor()
        {
            Node n = parent;
            boolean ret = false;
            while (n != null)
            {
                if (data.compare(n.data))
                {
                    ret = true;
                    break;
                }

                n = n.parent;
            }

            return ret;
        }

    }

    /**
     * Method to start the creation of the state graph using breadth first
     * search, transiting to allowable states
     */
    public void startGraphSearch()
    {
        ArrayList<String> left = new ArrayList<String>();
        left.add("W");
        left.add("S");
        left.add("C");
        left.add("F");

        State inits = new State("left", left, new ArrayList<String>());
        root = new Node(inits);
        root.level = 0;
        queue.add(root);

        while (!queue.isEmpty())
        {
            Node n = queue.remove(0);
            System.out.println("Processing Level " + n.level + " " + n.data);
            for (String m : moves)
            {

                State s = n.data.transits(m);

                if (s != null && s.isAllow()) // Check if it is allowable state
                {

                    Node child = new Node(s);
                    child.parent = n;
                    child.level = n.level + 1;
                    child.move = m + " moves " + child.data.getBank();

                    // Check that a node doesn't occur already as ancestor to
                    // prevent cycle in the graph
                    if (!child.isAncestor())
                    {
                        n.adjlist.add(child);

                        if (child.data.isSolution() == false)
                        {
                            queue.add(child);
                            System.out.println("Adding state " + child.data);
                        }
                        else
                        {
                            solutions.add(child);
                            System.out.println("Found solution " + child.data);

                        }
                    }

                }

            }

        }
    }

    /**
     * Prints out the entire state graph using breadth first search
     */
    public void printGraph()
    {
        ArrayList<Node> queue = new ArrayList<Node>();

        queue.add(root);

        while (!queue.isEmpty())
        {
            Node n = queue.remove(0);
            System.out.println("Level " + n.level + " " + n.data);

            ArrayList<Node> adjlist = n.adjlist;
            for (Node e : adjlist)
            {
                queue.add(e);
            }

        }

    }

    /**
     * Prints out the solutions, the states and the moves leading to the
     * solutions.
     */
    public void printSolution()
    {
        System.out.println("No. of solutions:  " + solutions.size());
        ArrayList<Node> stack;

        Iterator<Node> iter = solutions.iterator();
        int i = 1;
        while (iter.hasNext())
        {
            stack = new ArrayList<Node>();
            Node n = iter.next();
            stack.add(n);

            n = n.parent;
            while (n != null)
            {
                stack.add(n);
                n = n.parent;
            }
            System.out.println("Solution " + i);
            printSequence(stack);
            i++;
        }

    }

    private void printSequence(ArrayList<Node> stack)
    {
        StringBuffer buf = new StringBuffer();
        buf.append("No. of moves: ");
        buf.append(stack.size() - 1);
        buf.append("\n");
        for (int i = stack.size() - 1; i >= 0; i--)
        {
            Node n = stack.get(i);
            buf.append(n.data.toString());
            if (i != 0)
            {
                buf.append("--");
                buf.append(stack.get(i - 1).move);
                buf.append("->>");

            }
        }

        System.out.println(buf.toString());

    }

    public static void main(String[] args)
    {
        System.out.println("Solving Wolf, Sheep, Cabbage, Farmer, River Crossing Puzzle\n");
        FarmerWolfCabbageSheep obj = new FarmerWolfCabbageSheep();

        System.out.println("Creating State Graph using Breadth First Search");
        obj.startGraphSearch();

        System.out.println("\n\nState Graph in Breadth first order");
        obj.printGraph();
        System.out.println("\n\n");

        System.out.println("Solutions to the River Crossing Puzzle");
        obj.printSolution();

    }

}

