public class Move {
    private int startRow;
    private int startCol;
    private int endRow;
    private int endCol;

    public Move(String moveString) throws Exception {
        String move = moveString
            .replaceAll(" ", "")
            .replaceAll("-", "")
            .toUpperCase();
        
        String startColString = move.substring(0, 1);
        String startRowString = move.substring(1, 2);
        String endColString = move.substring(2, 3);
        String endRowString = move.substring(3, 4);

        this.startCol = columnLetterToIndex(startColString);
        this.startRow = Integer.parseInt(startRowString) - 1;
        this.endCol = columnLetterToIndex(endColString);
        this.endRow = Integer.parseInt(endRowString) - 1;
    }

    public Move(int startRow, int startCol, int endRow, int endCol) {
        this.startRow = startRow;
        this.startCol = startCol;
        this.endRow = endRow;
        this.endCol = endCol;
    }

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public int getStartCol() {
        return startCol;
    }

    public void setStartCol(int startCol) {
        this.startCol = startCol;
    }

    public int getEndRow() {
        return endRow;
    }

    public void setEndRow(int endRow) {
        this.endRow = endRow;
    }

    public int getEndCol() {
        return endCol;
    }

    public void setEndCol(int endCol) {
        this.endCol = endCol;
    }

    public static int columnLetterToIndex(String column) throws Exception {
        switch (column) {
            case "A":
                return 0;
            case "B":
                return 1;
            case "C":
                return 2;
            case "D":
                return 3;
            case "E":
                return 4;
            case "F":
                return 5;
            case "G":
                return 6;
            case "H":
                return 7;
            default:
                throw new Exception("Invalid column: " + column);
        }
    }

    public static String columnIndexToLetter(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "A";
            case 1:
                return "B";
            case 2:
                return "C";
            case 3:
                return "D";
            case 4:
                return "E";
            case 5:
                return "F";
            case 6:
                return "G";
            case 7:
                return "H";
            default:
                throw new RuntimeException("Invalid column index: " + columnIndex);
        }
    }

    public String toString() {
        String startColString = columnIndexToLetter(startCol);
        String startRowString = (startRow + 1) + "";
        String endColString = columnIndexToLetter(endCol);
        String endRowString = (endRow + 1) + "";

        return String.format("%s%s-%s%s", startColString, startRowString, endColString, endRowString);
    }
}
