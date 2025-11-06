import java.util.*;

public class CPUPlayer
{
    private int numExploredNodes;
    
    private Piece piece;

    public CPUPlayer(Piece cpu){
        this.piece = cpu;
    }

    public int getNumOfExploredNodes(){
        return numExploredNodes;
    }

    public ArrayList<Move> getNextMoveAB(Board board){
        int currentDepth = 0;

        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        ArrayList<Move> bestMoves = new ArrayList<>();
        List<Move> possibleMovesCPU = board.getPossibleMoves(board, this.piece);
        List<Map.Entry<Move, Integer>> moveScoreList = new ArrayList<>();

        int maximum = Integer.MIN_VALUE;

        for (Move move: possibleMovesCPU) {
            Board newBoard = new Board(board);
            newBoard.play(move);

            int score = getMoveScoreAB(newBoard, false, alpha, beta, currentDepth + 1);
            moveScoreList.add(new AbstractMap.SimpleEntry<>(move, score));

            if (score > maximum) {
                maximum = score;
            }
        }

        for (Map.Entry<Move, Integer> moveScore: moveScoreList) {
            if (moveScore.getValue() == maximum) {
                bestMoves.add(moveScore.getKey());
            }
        }

        int distance  = Integer.MAX_VALUE;
        int i = 0;
        double centerCol = 3.5;
        double centerRow = 3.5;
        for(Move move: bestMoves){
            if (Math.abs(move.getEndRow()-centerRow) + Math.abs(move.getEndCol()-centerCol) < distance){
                Collections.swap(bestMoves, i, 0);
            }
            i++;
        }

        return bestMoves;
    }

    private int getMoveScoreAB(Board board, boolean isCpuTurn, int alpha, int beta, int currentDepth) {
        this.numExploredNodes++;

        int evaluateValue = board.evaluate(piece);

        if (evaluateValue > 200 || evaluateValue < -200) {
            return evaluateValue;
        }

        if (currentDepth == 5) {
            return evaluateValue;
        }

        Piece otherPlayerPiece = this.piece == Piece.RED ? Piece.BLACK : Piece.RED;

        int score = 0;

        if (isCpuTurn) {
            List<Move> possibleMovesCPU = board.getPossibleMoves(board, this.piece);

            int maximum = Integer.MIN_VALUE;

            for (Move move: possibleMovesCPU) {
                Board newBoard = new Board(board);
                newBoard.play(move);

                score = getMoveScoreAB(newBoard, !isCpuTurn, alpha, beta, currentDepth + 1);

                if (score > maximum) {
                    maximum = score;
                    alpha = score;

                    if (alpha >= beta) {
                        break;
                    }
                }
            }

            return maximum;
        } else {
            List<Move> possibleMovesOtherPlayer = board.getPossibleMoves(board, otherPlayerPiece);

            int minimum = Integer.MAX_VALUE;

            for (Move move: possibleMovesOtherPlayer) {
                Board newBoard = new Board(board);
                newBoard.play(move);

                score = getMoveScoreAB(newBoard, !isCpuTurn, alpha, beta, currentDepth + 1);

                if (score < minimum) {
                    minimum = score;
                    beta = score;

                    if (beta <= alpha) {
                        break;
                    }
                }
            }

            return minimum;
        }
    }
}
