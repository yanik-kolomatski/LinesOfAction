import java.time.Duration;
import java.time.Instant;
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

    private static Map<Piece[][], Integer> boardScores = new HashMap<>();

    private static  int countturns = 0;

    public ArrayList<Move> getNextMoveAB(Board board){
        int currentDepth = 0;

        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        ArrayList<Move> bestMoves = new ArrayList<>();
        List<Move> possibleMovesCPU = board.getPossibleMoves(board, this.piece);
        List<Map.Entry<Move, Integer>> moveScoreList = new ArrayList<>();

        int maximum = Integer.MIN_VALUE;
        Instant start = Instant.now();
        for (Move move: possibleMovesCPU) {

            /*if(Duration.between(start, Instant.now()).toMillis() > 4900){
                break;
            }*/

            Board newBoard = new Board(board);
            newBoard.play(move);

            int score = getMoveScoreAB(newBoard, false, alpha, beta, currentDepth + 1, 0, start);
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


            for(Move move: bestMoves){
                if (Math.abs(move.getEndRow()-centerRow) + Math.abs(move.getEndCol()-centerCol) < distance){
                    centerindex = i;
                    distance = Math.abs(move.getEndRow()-centerRow) + Math.abs(move.getEndCol()-centerCol);
                }
                i++;
            }
            Collections.swap(bestMoves, centerindex, 0);



        distance  = Integer.MIN_VALUE;

        if(countturns < 4){
            for(int j = 0; j < bestMoves.size()/2; j++){
                Move move = bestMoves.get(j);

                if( Math.abs(move.getEndRow()-move.getStartRow()) + Math.abs(move.getEndCol()-move.getStartRow()) > distance){
                    Collections.swap(bestMoves, j, 0);
                }
            }
        }


        countturns++;
        return bestMoves;
    }

    private int getMoveScoreAB(Board board, boolean isCpuTurn, int alpha, int beta, int currentDepth, int b, Instant start) {
        this.numExploredNodes++;
        /*Integer evaluateValue = boardScores.get(board.getBoard());

        if(evaluateValue == null){
            evaluateValue = board.evaluate(piece);
            boardScores.put(board.getBoard(), evaluateValue);
        }*/
        int evaluateValue = board.evaluate(piece);

        if (evaluateValue > 200 || evaluateValue < -200) {
            return evaluateValue;
        }


        if(b == 0){
            if(countturns <= 3){
                b += 3;
            } else if (board.getPiecesCount() > 10) {
                b += 5;
            }else{
                b += 6;
            }
        }

        /*if(Duration.between(start, Instant.now()).toMillis() > 4900){
            return evaluateValue;
        }*/


        if (currentDepth == b) {
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

                score = getMoveScoreAB(newBoard, !isCpuTurn, alpha, beta, currentDepth + 1, b, start);

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

                score = getMoveScoreAB(newBoard, !isCpuTurn, alpha, beta, currentDepth + 1, b, start);

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
