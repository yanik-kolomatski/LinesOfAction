import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        Socket MyClient;
        BufferedInputStream input;
        BufferedOutputStream output;
        Board board = new Board();
        Piece piece = null;
        String host;
        int port ;
        //Scanner scanner = new Scanner(System.in);
        //System.out.print("Please enter the host name: ");
        if(args.length > 0){
            host = args[0];
            if (args.length > 1) {
                port = Integer.parseInt(args[1]);
            }else{
                port = 8888;
            }
        }else{
            host = "localhost";
            port = 8888;
        }

        //System.out.print("Please enter the port number: ");

        Move previousMove = null;
        boolean cpu = true;

        try {
            MyClient = new Socket(host, port);
            input = new BufferedInputStream(MyClient.getInputStream());
            output = new BufferedOutputStream(MyClient.getOutputStream());
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

            while (1 == 1) {
                char cmd = 0;
                cmd = (char) input.read();
                // System.out.println(cmd);

                // Debut de la partie en joueur blanc
                if (cmd == '1') {
                    byte[] aBuffer = new byte[1024];

                    int size = input.available();
                    // System.out.println("size " + size);
                    input.read(aBuffer, 0, size);
                    String s = new String(aBuffer).trim();
                    board = readBoard(s);
                    System.out.println(board);
                    System.out.println();

                    System.out.println("Nouvelle partie! Vous jouer blanc, entrez votre premier coup : ");
                    piece = Piece.RED;
                    String move = null;
                    if (cpu) {
                        CPUPlayer cpuPlayer = new CPUPlayer(piece);
                        List<Move> moves = cpuPlayer.getNextMoveAB(board);
                        move = moves.get(0).toString();
                        Move moveServer = new Move(move);
                        board.play(moveServer);
                    } else {
                        move = null;
                        move = console.readLine().toUpperCase();

                        Move moveServer = new Move(move);
                        board.play(moveServer);
                        System.out.println();
                        System.out.println(board);
                        System.out.println();
                    }

                    output.write(move.getBytes(), 0, move.length());
                    output.flush();
                }

                // Debut de la partie en joueur Noir
                if (cmd == '2') {
                    System.out.println("Nouvelle partie! Vous jouer noir, attendez le coup des blancs");
                    piece = Piece.BLACK;
                    byte[] aBuffer = new byte[1024];

                    int size = input.available();
                    // System.out.println("size " + size);
                    input.read(aBuffer, 0, size);
                    String s = new String(aBuffer).trim();
                    board = readBoard(s);
                    System.out.println();
                    System.out.println(board.toString());
                    System.out.println();
                }

                // Le serveur demande le prochain coup
                // Le message contient aussi le dernier coup joué.
                if (cmd == '3') {
                    byte[] aBuffer = new byte[16];

                    int size = input.available();
                    input.read(aBuffer, 0, size);

                    String s = new String(aBuffer);

                    Move moveServer = new Move(s);
                    System.out.println("Dernier coup: " + moveServer);
                    board.play(moveServer);

                    String moveString = null;

                    if (cpu) {
                        CPUPlayer cpuPlayer = new CPUPlayer(piece);
                        List<Move> moves = cpuPlayer.getNextMoveAB(board);
                        moveString = moves.get(0).toString();
                        Move moveClient = new Move(moveString);
                        if (moves.size() > 1){
                            if(moveClient.equals(previousMove)){
                                moveClient = moves.get(1);
                            }
                        }
                        previousMove = moveClient;
                        board.play(moveClient);
                    } else {
                        // Debug
                        if (moveString.equals("1")) {
                            System.out.println("Enter \"-1\" to continue the game.");
                            System.out.println();
                            while (true) {
                                try {
                                    System.out.println("Enter position to show possible moves.");
                                    System.out.println("Enter column: ");
                                    String column = console.readLine();
                                    if (column.equals("-1")) {
                                        break;
                                    }
                                    int columnIndex = Move.columnLetterToIndex(column.toUpperCase());
                                    System.out.println("Enter row: ");
                                    String row = console.readLine();
                                    if (row.equals("-1")) {
                                        break;
                                    }
                                    int rowIndex = Integer.parseInt(row) - 1;
                                    System.out.println(board.toStringWithPossibleMovesForPiece(rowIndex, columnIndex));
                                    System.out.println();
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }
                            }
                            System.out.println("Entrez votre coup: ");
                            moveString = console.readLine().toUpperCase();
                        } else if (moveString.equals("2")) {
                            System.out.println("Enter \"-1\" to continue the game.");
                            System.out.println();
                            while (true) {
                                System.out.println("Enter piece colour to show possible moves: ");
                                String pieceColour = console.readLine().toUpperCase();
                                if (pieceColour.equals("-1")) {
                                    break;
                                }
                                if (pieceColour.matches("RED|BLACK") == false) {
                                    continue;
                                }
                                Piece pieceForPossibleMoves = null;
                                if (pieceColour.equals("RED")) {
                                    pieceForPossibleMoves = Piece.RED;
                                } else {
                                    pieceForPossibleMoves = Piece.BLACK;
                                }
                                List<Move> possibleMoves = board.getPossibleMoves(board, pieceForPossibleMoves);
                                for (Move move : possibleMoves) {
                                    System.out.println(move);
                                }
                            }
                            System.out.println("Entrez votre coup: ");
                            moveString = console.readLine().toUpperCase();
                        } else if (moveString.equals("3")) {
                            System.out.println("Enter \"-1\" to continue the game.");
                            System.out.println();
                            System.out.println("Enter start and end position to calculate distance");
                            System.out.println();
                            while (true) {
                                try {
                                    System.out.println("Position 1 column: ");
                                    String column1 = console.readLine();
                                    if (column1.equals("-1")) {
                                        break;
                                    }
                                    int column1Index = Move.columnLetterToIndex(column1.toUpperCase());
                                    System.out.println("Position 1 row: ");
                                    String row1 = console.readLine();
                                    if (row1.equals("-1")) {
                                        break;
                                    }
                                    int row1Index = Integer.parseInt(row1) - 1;
                                    System.out.println("Position 2 column: ");
                                    String column2 = console.readLine();
                                    if (column2.equals("-1")) {
                                        break;
                                    }
                                    int column2Index = Move.columnLetterToIndex(column2.toUpperCase());
                                    System.out.println("Position 2 row: ");
                                    String row2 = console.readLine();
                                    if (row2.equals("-1")) {
                                        break;
                                    }
                                    int row2Index = Integer.parseInt(row2) - 1;
                                    int distance = board.getDistanceBetweenPieces(row1Index, column1Index, row2Index,
                                            column2Index);
                                    System.out.println();
                                    System.out.println("Distance: " + distance);
                                    System.out.println();
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }

                            }
                        } else if (moveString.equals("4")) {
                            System.out.println("Enter \"-1\" to continue the game.");
                            System.out.println();
                            while (true) {
                                System.out.println("Call to evaluate()");
                                System.out.println("Enter piece colour : ");
                                String pieceColour = console.readLine().toUpperCase();
                                if (pieceColour.equals("-1")) {
                                    break;
                                }
                                if (pieceColour.matches("RED|BLACK") == false) {
                                    continue;
                                }
                                Piece pieceForEvaluate = null;
                                if (pieceColour.equals("RED")) {
                                    pieceForEvaluate = Piece.RED;
                                } else {
                                    pieceForEvaluate = Piece.BLACK;
                                }
                                int evaluateValue = board.evaluate(pieceForEvaluate);
                                System.out.println("evaluate() value: " + evaluateValue);
                            }
                            System.out.println("Entrez votre coup: ");
                            moveString = console.readLine().toUpperCase();
                        }

                        System.out.println();
                        System.out.println(board);
                        System.out.println();

                        System.out.println("Entrez votre coup: ");

                        moveString = console.readLine().toUpperCase();

                        Move moveClient = new Move(moveString);
                        board.play(moveClient);
                        System.out.println();
                        System.out.println(board);
                        System.out.println();

                    }

                    output.write(moveString.getBytes(), 0, moveString.length());
                    output.flush();
                }

                // Le dernier coup est invalide
                if (cmd == '4') {
                    System.out.println("Coup invalide, entrez un nouveau coup : ");
                    String moveString = null;
                    moveString = console.readLine().toUpperCase();
                    output.write(moveString.getBytes(), 0, moveString.length());

                    Move moveClient = new Move(moveString);
                    board.play(moveClient);
                    System.out.println();
                    System.out.println(board);
                    System.out.println();

                    output.flush();
                }

                // La partie est terminée
                if (cmd == '5') {
                    byte[] aBuffer = new byte[16];
                    int size = input.available();
                    input.read(aBuffer, 0, size);
                    String s = new String(aBuffer);
                    System.out.println("Partie Terminé. Le dernier coup joué est: " + s);
                    String move = null;
                    move = console.readLine();
                    output.write(move.getBytes(), 0, move.length());
                    output.flush();

                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private static Board readBoard(String s) {
        try {
            Board newBoard = new Board();
            String[] boardValues;
            boardValues = s.split(" ");
            int x = 0, y = 0;
            for (int i = 0; i < boardValues.length; i++) {
                Piece piece = Piece.createPiece(Integer.parseInt(boardValues[i]));
                newBoard.setPiece(x, y, piece);
                x++;
                if (x == 8) {
                    x = 0;
                    y++;
                }
            }
            return newBoard;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
