package martijn.quoridor.brains;

import martijn.quoridor.model.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Hero extends Brain {

    private int depth;

    public Hero(String name, int depth) {
        super(name);
        this.depth = depth;
    }

    @Override
    public Move getMove(Board board) {
        Node parent = createTree(board);
        setValues(parent);
        Node bestMoveNode = minmax(parent, depth, true, Integer.MIN_VALUE, Integer.MAX_VALUE);
        return bestMoveNode.getMove();
    }

    private Node createTree(Board board){
        int depth = getDepth();
        Node parent = new Node();
        parent.setMove(new Jump(board.getTurn().stepToGoal()));
        ArrayList<Node> childes = successor(parent, board);
        parent.setChildes(childes);
        depth--;
        ArrayList<Node> parents = new ArrayList<>();
        parents.add(parent);
        while (depth > 0) {
            ArrayList<Node> newParents = new ArrayList<>();
            for (Node p : parents) {
                for (Node c : p.getChildes()) {
                    if (c.getMove().isLegal(board)) {
                        continue;
                    }
                    board.move(c.getMove());
                    ArrayList<Node> childes1 = successor(c,board);
                    c.setChildes(childes1);
                    newParents.addAll(childes1);
                    board.undo();
                }
            }
            parents.clear();
            parents.addAll(newParents);
            newParents.clear();
            depth--;
            if (depth == 0) {
                for (Node node : newParents)
                    node.setValue(new Random().nextInt(10));
            }
        }
        return parent;
    }

    private void setValues(Node parent, Board board) {
        int depth = getDepth();
        for (Node node : parent.getChildes()) {
            board.move(node.getMove());
            int val = -getValue(board, depth - 1);
            node.setValue(val);
            board.undo();
        }
    }

    private int getValue(Board board, int depth){

    }

    private int getDepth(){ return this.depth; }

    private Node minmax(Node node, int depth, boolean isMax, int alpha, int beta) {
        if (node.getChildes().isEmpty())
            return node;

        if (isMax) {
            Node bestMove = new Node(Integer.MIN_VALUE);
            for (Node child : node.getChildes()) {
                Node move = minmax(child, depth + 1, false, alpha, beta);
                bestMove = (bestMove.getValue() > move.getValue()) ? bestMove : move;
                alpha = (alpha > bestMove.getValue()) ? alpha : bestMove.getValue();
                if (beta <= alpha) break;
            }
            return bestMove;
        } else {
            Node bestMove = new Node(Integer.MAX_VALUE);
            for (Node child : node.getChildes()) {
                Node move = minmax(child, depth + 1, true, alpha, beta);
                bestMove = (bestMove.getValue() < move.getValue()) ? bestMove : move;
                beta = (beta < bestMove.getValue()) ? beta : bestMove.getValue();
                if (beta <= alpha) break;
            }
            return bestMove;
        }
    }

//    private int max_value(Board board, int alpha, int beta) {
//        if (board.getTurn().isGoal(new Position(board.getWidth() - 1, board.getHeight() - 1))) {
//            return utility(board);
//        }
//        int v = Integer.MIN_VALUE;
//        for (Board a : successor(board)) {
//            v = Integer.max(v, min_value(a, alpha, beta));
//            if (v >= beta) return v;
//            alpha = Integer.max(alpha, v);
//        }
//        return v;
//    }
//
//    private int min_value(Board board, int alpha, int beta) {
//        if (board.getTurn().isGoal(new Position(board.getWidth() - 1, board.getHeight() - 1))) {
//            return utility(board);
//        }
//        int v = Integer.MAX_VALUE;
//        for (Board a : successor(board)) {
//            v = Integer.max(v, max_value(a, alpha, beta));
//            if (v <= alpha) return v;
//            beta = Integer.min(beta, v);
//        }
//        return v;
//    }
//
//    private int utility(Board board) {
//        return 0;
//    }

    private ArrayList<Node> successor(Node parent, Board board) {
        ArrayList<Node> childes = new ArrayList<>();
        Player p = board.getTurn();

        Node child = new Node();
        child.setMove(new Jump(p.stepToGoal()));
        childes.add(child);

        if (p.getWallCount() > 0) {
            PositionSet set = new PositionSet(board.getWidth() + 1, board
                    .getHeight() + 1);
            for (Player pl : board.getPlayers()) {
                markWallLocations(set, pl);
            }
            for (int x = 1; x < board.getWidth(); x++) {
                for (int y = 1; y < board.getHeight(); y++) {
                    Position pos = new Position(x, y);
                    if (set.contains(pos)) {
                        pos = pos.south().west();
                        add(childes, new PutWall(pos, Wall.HORIZONTAL), board);
                        add(childes, new PutWall(pos, Wall.VERTICAL), board);
                    }
                }
            }
        }

        for (Node child1 : childes)
            child1.setParent(parent);
        return childes;
    }

    private void markWallLocations(PositionSet set, Player p) {
        Orientation[] path = p.findGoal();
        Position pos = p.getPosition();
        select(set, pos);
        for (Orientation o : path) {
            pos = pos.move(o);
            select(set, pos);
        }
    }

    private void add(List<Node> childes, Move move, Board board) {
        if (move.isLegal(board)) {
            Node node = new Node();
            node.setMove(move);
            childes.add(node);
        }
    }

    private void select(PositionSet set, Position pos) {
        set.add(pos);
        set.add(pos.east());
        set.add(pos.north());
        set.add(pos.east().north());
    }

    private void bornChild(ArrayList<Board> fringe, int x, int y) {

    }

    static class Node implements Comparable {
        private Move move;
        private int value;
        private ArrayList<Node> childes = new ArrayList<>();
        private Node parent;

        public Node() {
        }

        public Node(int value) {
            this.value = value;
        }

        public Node(Move move, int value, ArrayList<Node> childes, Node parent) {
            this.move = move;
            this.value = value;
            this.childes = childes;
            this.parent = parent;
        }

        public Move getMove() {
            return move;
        }

        public void setMove(Move move) {
            this.move = move;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public ArrayList<Node> getChildes() {
            return childes;
        }

        public void setChildes(ArrayList<Node> childes) {
            this.childes = childes;
        }

        public Node getParent() {
            return parent;
        }

        public void setParent(Node parent) {
            this.parent = parent;
        }

        @Override
        public int compareTo(Object o) {
            Node node = (Node) o;
            return node.value - this.value;
        }
    }
}
