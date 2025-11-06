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

    private static int countedge = 0;

    private static  int countcenter = 0;

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

        double distance  = Integer.MAX_VALUE;
        int i = 0;
        double centerCol = 3.5;
        double centerRow = 3.5;
        int centerindex = 0;
        if(countcenter < 10){

            for(Move move: bestMoves){
                if (Math.abs(move.getEndRow()-centerRow) + Math.abs(move.getEndCol()-centerCol) < distance){
                    centerindex = i;
                    distance = Math.abs(move.getEndRow()-centerRow) + Math.abs(move.getEndCol()-centerCol);
                }
                i++;
            }
            Collections.swap(bestMoves, centerindex, 0);
            countcenter++;
        }

        if(countedge < 4){

            int n = countedge % 2 == 0 ? 0 : 7;

            for(int j = 0; j < bestMoves.size()/2; j++){
                Move move = bestMoves.get(j);
                int cr = piece == Piece.RED ? move.getStartCol() : move.getStartRow();

                if(cr == n){
                    Collections.swap(bestMoves, j, 0);
                    break;
                }
            }
            countedge += 1;
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
