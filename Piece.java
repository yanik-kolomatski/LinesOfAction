public enum Piece {
    RED(2, " RED "),
    BLACK(4, "BLACK"),
    EMPTY(0, "_____");

    // The number value for the piece is the number that is used to represent
    // the piece in the server responses
    private int number;

    // The string field is the string that will be returned when the toString()
    // method is called
    private String string;

    private Piece(int number, String string) {
        this.number = number;
        this.string = string;
    }

    public int getNumber() {
        return this.number;
    }

    public static Piece createPiece(int number) throws Exception {
        switch (number) {
            case 2:
                return Piece.RED;
            case 4:
                return Piece.BLACK;
            case 0:
                return Piece.EMPTY;
            default:
                throw new Exception("Invalid number value for piece: " + number);
        }
    }

    public String toString() {
        return string;
    }
}
