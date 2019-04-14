package martijn.quoridor.brains;

import martijn.quoridor.model.*;

import java.util.ArrayList;

public class Hero extends Brain {

    @Override
    public Move getMove(Board board) {
        return new Jump(board.getTurn().)
    }

//    private Move alpha_beta_search(Board board) {
//        int v = max_value(board, Integer.MIN_VALUE, Integer.MAX_VALUE);
//    }

    private int max_value(Board board, int alpha, int beta) {
        if (board.getTurn().isGoal(new Position(board.getWidth() - 1, board.getHeight() - 1))) {
            return utility(board);
        }
        int v = Integer.MIN_VALUE;
        for (Board a : successor(board)) {
            v = Integer.max(v, min_value(a, alpha, beta));
            if (v >= beta) return v;
            alpha = Integer.max(alpha, v);
        }
        return v;
    }

    private int min_value(Board board, int alpha, int beta) {
        if (board.getTurn().isGoal(new Position(board.getWidth() - 1, board.getHeight() - 1))) {
            return utility(board);
        }
        int v = Integer.MAX_VALUE;
        for (Board a : successor(board)) {
            v = Integer.max(v, max_value(a, alpha, beta));
            if (v <= alpha) return v;
            beta = Integer.min(beta, v);
        }
        return v;
    }

    private int utility(Board board) {
        return 0;
    }

    private ArrayList<Board> successor(Board board) {
        ArrayList newBorned = new ArrayList();
        Player me = board.getTurn();
        Position myPosition = me.getPosition();
        int myX = myPosition.getX();
        int myY = myPosition.getY();

        int x = myX + 1, y = myY + 1;
        if (x < board.getWidth() && y + 1 < board.getHeight()) {
            if (!board.containsWallPosition(new Position(x, y))) {
                bornChild(newBorned, x, y);
            }
        }
    }

    private void bornChild(ArrayList<Boolean> fringe, int x, int y) {

    }
}
