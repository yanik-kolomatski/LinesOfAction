import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board{
    private Piece[][] board;

    private int redPiecesCount = 0;
    private int blackPiecesCount = 0;

    private Position[] redPiecesPositions = new Position[12];
    private Position[] blackPiecesPositions = new Position[12];

    public Board() {
        this.board = new Piece[8][8];
        for (int i = 0; i < board.length; i++) {
            Arrays.fill(board[i], Piece.EMPTY);
        }
    }

    public int getBlackPiecesCount() {
        return blackPiecesCount;
    }
    public int getRedPiecesCount() {
        return redPiecesCount;
    }

    public Position[] getBlackPiecesPositions() {
        return blackPiecesPositions;
    }

    public Position[] getRedPiecesPositions() {
        return redPiecesPositions;
    }

    public Board(Board board, int n) {
        this.board = new Piece[8][8];
        this.board = board.getBoard();
        this.redPiecesCount = board.getRedPiecesCount();
        this.blackPiecesCount = board.getBlackPiecesCount();
        this.redPiecesPositions = board.getRedPiecesPositions();
        this.blackPiecesPositions = board.getBlackPiecesPositions();
    }

    public Board(Board otherBoard) {
        this.board = new Piece[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = otherBoard.getPiece(i, j);
                board[i][j] = piece;
                Position position = new Position(i, j);
                if (piece == Piece.RED) {
                    redPiecesCount++;
                    redPiecesPositions[redPiecesCount - 1] = position;
                } else if (piece == Piece.BLACK) {
                    blackPiecesCount++;
                    blackPiecesPositions[blackPiecesCount - 1] = position;
                }
            }
        }
    }

    public int getPiecesCount(Piece piece) {
        return piece == Piece.RED ? redPiecesCount : blackPiecesCount;
    }

    public int getPiecesCount() {
        return redPiecesCount + blackPiecesCount;
    }

    public Piece[][] getBoard() {
        return board;
    }

    public void play(Move move) {
        Position startPosition = new Position(move.getStartRow(), move.getStartCol());
        Position endPosition = new Position(move.getEndRow(), move.getEndCol());

        Piece pieceAtStartPosition = board[move.getStartRow()][move.getStartCol()];
        Piece pieceAtEndPosition = board[move.getEndRow()][move.getEndCol()];

        board[move.getStartRow()][move.getStartCol()] = Piece.EMPTY;
        board[move.getEndRow()][move.getEndCol()] = pieceAtStartPosition;

        if (pieceAtEndPosition == Piece.RED) {
            int nullPosition = -1;
            for (int i = 0; i < redPiecesCount; i++) {
                if (redPiecesPositions[i].equals(endPosition)) {
                    redPiecesPositions[i] = null;
                    nullPosition = i;
                    redPiecesCount--;
                    break;
                }
            }
            for (int i = nullPosition; i < redPiecesCount; i++) {
                redPiecesPositions[i] = redPiecesPositions[i + 1];
                redPiecesPositions[i + 1] = null;
            }
        }

        if (pieceAtEndPosition == Piece.BLACK) {
            int nullPosition = -1;
            for (int i = 0; i < blackPiecesCount; i++) {
                if (blackPiecesPositions[i].equals(endPosition)) {
                    blackPiecesPositions[i] = null;
                    nullPosition = i;
                    blackPiecesCount--;
                    break;
                }
            }
            for (int i = nullPosition; i < blackPiecesCount; i++) {
                blackPiecesPositions[i] = blackPiecesPositions[i + 1];
                blackPiecesPositions[i + 1] = null;
            }
        }

        if (pieceAtStartPosition == Piece.RED) {
            for (int i = 0; i < redPiecesCount; i++) {
                if (redPiecesPositions[i].equals(startPosition)) {
                    redPiecesPositions[i] = endPosition;
                    break;
                }
            }
        } else if (pieceAtStartPosition == Piece.BLACK) {
            for (int i = 0; i < blackPiecesCount; i++) {
                if (blackPiecesPositions[i].equals(startPosition)) {
                    blackPiecesPositions[i] = endPosition;
                    break;
                }
            }
        }
    }

    public List<Move> getPossibleMoves(Board board, Piece piece) {
        List<Move> possibleMoves = new ArrayList<Move>();

        Position piecePosition = null;
        int row = -1;
        int col = -1;

        if (piece == Piece.RED) {
            for (int i = 0; i < redPiecesCount; i++) {
                piecePosition = redPiecesPositions[i];
                row = piecePosition.getRow();
                col = piecePosition.getCol();
                possibleMoves.addAll(getPossibleMovesForPiece(row, col));
            }
        } else if (piece == Piece.BLACK) {
            for (int i = 0; i < blackPiecesCount; i++) {
                piecePosition = blackPiecesPositions[i];
                row = piecePosition.getRow();
                col = piecePosition.getCol();
                possibleMoves.addAll(getPossibleMovesForPiece(row, col));
            }
        }

        return possibleMoves;
    }

    public List<Move> getPossibleMovesForPiece(int row, int col) {
        if (board[row][col] == Piece.EMPTY) {
            return new ArrayList<Move>();
        }

        Piece thisPiece = board[row][col];
        Piece oppositePiece = thisPiece == Piece.RED ? Piece.BLACK : Piece.RED;

        List<Move> possibleMoves = new ArrayList<>();

        int piecesOnRow = countPiecesOnRow(row);
        int piecesOnColumn = countPiecesOnColumn(col);
        int piecesOnFirstDiagonal = countPiecesOnFirstDiagonal(row, col);
        int piecesOnSecondDiagonal = countPiecesOnSecondDiagonal(row, col);

        int rowOfPosition1OnSameColumn = row - piecesOnColumn;//vertical movement down
        int rowOfPosition2OnSameColumn = row + piecesOnColumn;//vertical movement up

        int columnOfPosition1OnSameRow = col - piecesOnRow;//horizontal movement left
        int columnOfPosition2OnSameRow = col + piecesOnRow;//horizontal movement right

        int rowOfPosition1OnFirstDiagonal = row - piecesOnFirstDiagonal;
        int columnOfPosition1OnFirstDiagonal = col - piecesOnFirstDiagonal;//increasing diagonal up
        int rowOfPosition2OnFirstDiagonal = row + piecesOnFirstDiagonal;
        int columnPosition2OnFirstDiagonal = col + piecesOnFirstDiagonal;//inreasing diagonal down

        int rowOfPosition1OnSecondDiagonal = row + piecesOnSecondDiagonal;//decreasing diagonal up
        int columnOfPosition1OnSecondDiagonal = col - piecesOnSecondDiagonal;
        int rowOfPosition2OnSecondDiagonal = row - piecesOnSecondDiagonal;//decreasing diagonal down
        int columnOfPosition2OnSecondDiagonal = col + piecesOnSecondDiagonal;

        outerIf: if (isPositionInBoardBounds(rowOfPosition1OnSameColumn, col)
                && thisPiece != board[rowOfPosition1OnSameColumn][col]) {
            for (int i = row - 1; i > rowOfPosition1OnSameColumn; i--) {
                if (board[i][col] == oppositePiece) {
                    break outerIf;
                }
            }
            possibleMoves.add(new Move(row, col, rowOfPosition1OnSameColumn, col));
        }

        outerIf: if (isPositionInBoardBounds(rowOfPosition2OnSameColumn, col)
                && thisPiece != board[rowOfPosition2OnSameColumn][col]) {
            for (int i = row + 1; i < rowOfPosition2OnSameColumn; i++) {
                if (board[i][col] == oppositePiece) {
                    break outerIf;
                }
            }
            possibleMoves.add(new Move(row, col, rowOfPosition2OnSameColumn, col));
        }

        outerIf: if (isPositionInBoardBounds(row, columnOfPosition1OnSameRow)
                && thisPiece != board[row][columnOfPosition1OnSameRow]) {
            for (int j = col - 1; j > columnOfPosition1OnSameRow; j--) {
                if (board[row][j] == oppositePiece) {
                    break outerIf;
                }
            }
            possibleMoves.add(new Move(row, col, row, columnOfPosition1OnSameRow));
        }

        outerIf: if (isPositionInBoardBounds(row, columnOfPosition2OnSameRow)
                && thisPiece != board[row][columnOfPosition2OnSameRow]) {
            for (int j = col + 1; j < columnOfPosition2OnSameRow; j++) {
                if (board[row][j] == oppositePiece) {
                    break outerIf;
                }
            }
            possibleMoves.add(new Move(row, col, row, columnOfPosition2OnSameRow));
        }

        outerIf: if (isPositionInBoardBounds(rowOfPosition1OnFirstDiagonal, columnOfPosition1OnFirstDiagonal)
                && thisPiece != board[rowOfPosition1OnFirstDiagonal][columnOfPosition1OnFirstDiagonal]) {
            for (int i = row - 1, j = col - 1; i > rowOfPosition1OnFirstDiagonal
                    && j > columnOfPosition1OnFirstDiagonal; i--, j--) {
                if (board[i][j] == oppositePiece) {
                    break outerIf;
                }
            }
            possibleMoves.add(new Move(row, col, rowOfPosition1OnFirstDiagonal, columnOfPosition1OnFirstDiagonal));
        }

        outerIf: if (isPositionInBoardBounds(rowOfPosition2OnFirstDiagonal, columnPosition2OnFirstDiagonal)
                && thisPiece != board[rowOfPosition2OnFirstDiagonal][columnPosition2OnFirstDiagonal]) {
            for (int i = row + 1, j = col + 1; i < rowOfPosition2OnFirstDiagonal
                    && j < columnPosition2OnFirstDiagonal; i++, j++) {
                if (board[i][j] == oppositePiece) {
                    break outerIf;
                }
            }
            possibleMoves.add(new Move(row, col, rowOfPosition2OnFirstDiagonal, columnPosition2OnFirstDiagonal));
        }

        outerIf: if (isPositionInBoardBounds(rowOfPosition1OnSecondDiagonal, columnOfPosition1OnSecondDiagonal)
                && thisPiece != board[rowOfPosition1OnSecondDiagonal][columnOfPosition1OnSecondDiagonal]) {
            for (int i = row + 1, j = col - 1; i < rowOfPosition1OnSecondDiagonal
                    && j > columnOfPosition1OnSecondDiagonal; i++, j--) {
                if (board[i][j] == oppositePiece) {
                    break outerIf;
                }
            }
            possibleMoves.add(new Move(row, col, rowOfPosition1OnSecondDiagonal, columnOfPosition1OnSecondDiagonal));
        }

        outerIf: if (isPositionInBoardBounds(rowOfPosition2OnSecondDiagonal, columnOfPosition2OnSecondDiagonal)
                && thisPiece != board[rowOfPosition2OnSecondDiagonal][columnOfPosition2OnSecondDiagonal]) {
            for (int i = row - 1, j = col + 1; i > rowOfPosition2OnSecondDiagonal
                    && j < columnOfPosition2OnSecondDiagonal; i--, j++) {
                if (board[i][j] == oppositePiece) {
                    break outerIf;
                }
            }
            possibleMoves.add(new Move(row, col, rowOfPosition2OnSecondDiagonal, columnOfPosition2OnSecondDiagonal));
        }

        return possibleMoves;
    }

    private int countPiecesOnRow(int row) {
        int numberOfPieces = 0;
        for (int j = 0; j < board[row].length; j++) {
            if (board[row][j] != Piece.EMPTY) {
                numberOfPieces++;
            }
        }
        return numberOfPieces;
    }

    private int countPiecesOnColumn(int col) {
        int numberOfPieces = 0;
        for (int i = 0; i < board.length; i++) {
            if (board[i][col] != Piece.EMPTY) {
                numberOfPieces++;
            }
        }
        return numberOfPieces;
    }

    private int countPiecesOnFirstDiagonal(int row, int col) {
        int numberOfPieces = 0;
        int i = row;
        int j = col;
        while (i >= 0 && j >= 0) {
            if (board[i][j] != Piece.EMPTY) {
                numberOfPieces++;
            }
            i--;
            j--;
        }
        i = row + 1;
        j = col + 1;
        while (i < 8 && j < 8) {
            if (board[i][j] != Piece.EMPTY) {
                numberOfPieces++;
            }
            i++;
            j++;
        }
        return numberOfPieces;
    }

    private int countPiecesOnSecondDiagonal(int row, int col) {
        int numberOfPieces = 0;
        int i = row;
        int j = col;
        while (i < 8 && j >= 0) {
            if (board[i][j] != Piece.EMPTY) {
                numberOfPieces++;
            }
            i++;
            j--;
        }
        i = row - 1;
        j = col + 1;
        while (i >= 0 && j < 8) {
            if (board[i][j] != Piece.EMPTY) {
                numberOfPieces++;
            }
            i--;
            j++;
        }
        return numberOfPieces;
    }

    private boolean isPositionInBoardBounds(int row, int col) {
        if (row >= 0 && row < 8 && col >= 0 && col < 8) {
            return true;
        }
        return false;
    }

    public int getDistanceBetweenPieces(int startRow, int startCol, int endRow, int endCol) {
        if (startRow == endRow) {
            return absoluteValue(endCol - startCol) - 1;
        } else if (startCol == endCol) {
            return absoluteValue(endRow - startRow) - 1;
        } else if ((startRow - startCol) == (endRow - endCol)) {
            return absoluteValue(endRow - startRow) - 1;
        } else if ((0 - startRow) - startCol == ((0 - endRow) - endCol)) {
            return absoluteValue(endRow - startRow) - 1;
        } else if ((startRow < endRow && startCol < endCol) || (startRow > endRow && startCol > endCol)) {
            int startRow1 = startRow;
            int startCol1 = startCol;
            int endRow1 = endRow;
            int endCol1 = endCol;

            if (startRow > endRow) {
                startRow1 = endRow;
                startCol1 = endCol;
                endRow1 = startRow;
                endCol1 = startCol;
            }

            int difference = -1;
            int rowDifference = endRow1 - startRow1;
            int colDifference = endCol1 - startCol1;
            if (rowDifference < colDifference) {
                difference = rowDifference;
                difference += endCol1 - (startCol1 + rowDifference);
                --difference;
            }
            if (colDifference < rowDifference) {
                difference = colDifference;
                difference += endRow1 - (startRow1 + colDifference);
                --difference;
            }
            return difference;
        } else if ((startRow > endRow && startCol < endCol) || (startRow < endRow && startCol > endCol)) {
            int startRow1 = startRow;
            int startCol1 = startCol;
            int endRow1 = endRow;
            int endCol1 = endCol;

            if (startRow < endRow) {
                startRow1 = endRow;
                startCol1 = endCol;
                endRow1 = startRow;
                endCol1 = startCol;
            }

            int difference = -1;
            int rowDifference = startRow1 - endRow1;
            int colDifference = endCol1 - startCol1;
            if (rowDifference < colDifference) {
                difference = rowDifference;
                difference += endCol1 - (startCol1 + rowDifference);
                --difference;
            }
            if (colDifference < rowDifference) {
                difference = colDifference;
                difference += (startRow1 - colDifference) - endRow1;
                --difference;
            }
            return difference;
        }
        return -1;
    }

    public boolean isConnected(int startRow, int startCol, int endRow, int endCol) {
        int diffrow = absoluteValue(startRow - endRow);
        int diffcol = absoluteValue(startCol - endCol);
        return diffcol <= 1 &&  diffrow <= 1 && diffrow + diffcol > 0;
    }

    private int absoluteValue(int num) {
        if (num < 0) {
            return (0 - num);
        }
        return num;
    }

    // A call to evaluate sets the fields :
    // isGameEnd, redWins, blackWins
    // TODO
    public int evaluate(Piece piece) {
        Position[] redPiecesPositions2 = Arrays.copyOfRange(redPiecesPositions, 0, redPiecesCount);
        Position[] blackPiecesPositions2 = Arrays.copyOfRange(blackPiecesPositions, 0, blackPiecesCount);

        int redPiecesLongestChainSize = findLongestChainSize(redPiecesPositions2);
        int blackPiecesLongestChainSize = findLongestChainSize(blackPiecesPositions2);

        int redPiecesPercentage = (int) (((double) redPiecesLongestChainSize / redPiecesCount) * 100);
        int blackPiecesPercentage = (int) (((double) blackPiecesLongestChainSize / blackPiecesCount) * 100);

        int percentageDifference = redPiecesPercentage - blackPiecesPercentage;

        if (redPiecesPercentage == 100) {
            percentageDifference += 300;
        }

        if (blackPiecesPercentage == 100) {
            percentageDifference -= 300;
        }


        Position[] pieces;
        int pCol;
        int pRow;
        int centerValue = 0;
        if (piece == Piece.BLACK) {
            pieces = blackPiecesPositions2;
        } else {
            pieces = redPiecesPositions2;
        }

        if (CPUPlayer.countturns <= 20){
            for (Position position : pieces) {
                pCol = position.getCol();
                pRow = position.getRow();
                centerValue += (int) (Math.abs(pCol - 3.5) + Math.abs(pRow - 3.5));
                if (pCol == 0 || pCol == 7) {
                    percentageDifference -= piece == Piece.BLACK ? -10 : 10;
                }
                if (pRow == 0 || pRow == 7) {
                    percentageDifference -= piece == Piece.BLACK ? -10 : 10;
                }
            }
        }

        if(piece == Piece.BLACK){
            percentageDifference += centerValue;
        }else{
            percentageDifference -= centerValue;
        }

        if (piece == Piece.BLACK) {
            percentageDifference = 0 - percentageDifference;
        }

        return percentageDifference;
    }

    public int findLongestChainSize(Position[] piecesPositions) {
        if (piecesPositions.length == 0) {
            return 0;
        }

        List<Position> piecesPositionsList = new ArrayList<>(Arrays.asList(piecesPositions));
        List<Position> piecesChain = new ArrayList<>();

        int longestChainSize = 1;

        int i = 0;
        while (piecesPositionsList.isEmpty() == false) {
            if (i == 0) {
                piecesChain.add(piecesPositionsList.get(0));
                piecesPositionsList.remove(0);
            }

            for (int j = 0; j < piecesPositionsList.size(); j++) {
                Position position1 = piecesChain.get(i);
                Position position2 = piecesPositionsList.get(j);
                int row1 = position1.getRow();
                int col1 = position1.getCol();
                int row2 = position2.getRow();
                int col2 = position2.getCol();
                boolean distance = isConnected(row1, col1, row2, col2);

                if (distance) {
                    piecesChain.add(piecesPositionsList.get(j));
                    piecesPositionsList.remove(j);
                    j--;
                }
            }

            i++;

            int chainSize = piecesChain.size();
            if (i == chainSize || piecesPositionsList.isEmpty()) {
                if (chainSize > longestChainSize) {
                    longestChainSize = chainSize;
                }
                i = 0;
                piecesChain.clear();
            }
        }

        return longestChainSize;
    }

    public boolean isGameEnd() {
        return false;
    }

    public Piece getPiece(int row, int col) {
        return board[row][col];
    }

    public void setPiece(int row, int col, Piece piece) {
        board[row][col] = piece;
        Position position = new Position(row, col);
        if (piece == Piece.RED) {
            redPiecesCount++;
            redPiecesPositions[redPiecesCount - 1] = position;
        } else if (piece == Piece.BLACK) {
            blackPiecesCount++;
            blackPiecesPositions[blackPiecesCount - 1] = position;
        }
    }

    public int getTotalPieceCount() {
        return redPiecesCount + blackPiecesCount;
    }

    public String toString() {
        StringBuilder boardString = new StringBuilder();
        for (int i = board.length - 1; i >= 0; i--) {
            boardString.append(i + 1);
            boardString.append("    ");
            for (int j = 0; j < board[i].length; j++) {
                boardString.append(board[i][j]);
                boardString.append(" ");
            }
            boardString.append("\n");
        }
        boardString.append("\n");
        boardString.append("     ");
        boardString.append("  A   ");
        boardString.append("  B   ");
        boardString.append("  C   ");
        boardString.append("  D   ");
        boardString.append("  E   ");
        boardString.append("  F   ");
        boardString.append("  G   ");
        boardString.append("  H   ");
        return boardString.toString();
    }

    public String toStringWithPossibleMovesForPiece(int row, int col) {
        List<Move> possibleMoves = getPossibleMovesForPiece(row, col);
        StringBuilder boardString = new StringBuilder();
        for (int i = board.length - 1; i >= 0; i--) {
            boardString.append(i + 1);
            boardString.append("    ");
            for (int j = 0; j < board[i].length; j++) {
                boolean possibleMove = false;
                for (Move move : possibleMoves) {
                    if (i == move.getEndRow() && j == move.getEndCol()) {
                        possibleMove = true;
                    }
                }
                if (possibleMove) {
                    boardString.append("XXXXX");
                } else {
                    boardString.append(board[i][j]);
                }
                boardString.append(" ");
            }
            boardString.append("\n");
        }
        boardString.append("\n");
        boardString.append("     ");
        boardString.append("  A   ");
        boardString.append("  B   ");
        boardString.append("  C   ");
        boardString.append("  D   ");
        boardString.append("  E   ");
        boardString.append("  F   ");
        boardString.append("  G   ");
        boardString.append("  H   ");
        return boardString.toString();
    }

}
