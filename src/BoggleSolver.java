import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdOut;

public class BoggleSolver {
    private ModifiedTST tst;

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)

    public BoggleSolver(String[] dictionary) {
        tst = new ModifiedTST();
        int i = 0;
        for (String word : dictionary) {
            tst.put(word, i); // put each word into the TST with index i of the order they came in
            i++;
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        int rows = board.rows();
        int cols = board.cols();
        SET<String> allWords = new SET<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                String letters = "";
                // StringBuilder letters = new StringBuilder();
                boolean[][] visited = new boolean[rows][cols];
                dfs(board, i, j, visited, letters, allWords);
            }
        }
        return allWords;
    }

    private void dfs(BoggleBoard board, int i, int j, boolean[][] visited, String prefix, SET<String> realWord) {
        if (visited[i][j]) {
            return;
        }

        char currentChar = board.getLetter(i, j); // the current character that is being checked
        // int index = prefix.length();
        if (currentChar == 'Q') {
            prefix += "QU";
            // prefix.append("QU"); // add "QU" to the current word if "Q" is visited
        } else {
            prefix += currentChar;
            // prefix.append(currentChar); // add the current character to the existing word
        }

        // the problem is that the currentChar is appended to prefix regardless if it is a possible word
        // goal: go back one if
        // String stringWord = prefix.toString();

        //  if there are no possible words with this prefix, return
        if (!tst.hasPrefix(prefix)) {
            return;
        }

        if (tst.contains(prefix) && prefix.length() > 2) {
            realWord.add(prefix); // add the valid word to the SET
        }

        visited[i][j] = true;

        // start checking the adjacent tiles from top-left to bottom-right
        // eg. rowStart of -1 means checking all the tiles one row above
        // colEnd of 1 means the last column to be checked is the column to the right
        int rowStart = -1;
        int rowEnd = 1;
        int colStart = -1;
        int colEnd = 1;

        // if top row, don't attempt to check the above row
        if (i == 0) {
            rowStart = 0;
            // if bottom row, don't attempt to check the bottom row
        } else if (i == board.rows() - 1) {
            rowEnd = 0;
        }

        // if left-most column, don't attempt to check the column to left
        if (j == 0) {
            colStart = 0;
            // if right-most column, don't attempt to check the column to right
        } else if (j == board.cols() - 1) {
            colEnd = 0;
        }

        for (int row = rowStart; row <= rowEnd; row++) {
            for (int col = colStart; col <= colEnd; col++) {
                // skip visiting current tile
                if (row == 0 && col == 0) {
                    continue;
                }
                // check that next tile is in the board
                if (i + row >= 0 && i + row < board.rows() && j + col >= 0 && j + col < board.cols()) {
                    if (!visited[i + row][j + col]) {
                        dfs(board, i + row, j + col, visited, prefix, realWord); // visit the 8 neighboring tiles
                    }
                }
            }
        }
        visited[i][j] = false; // unmark the current tile so it can be revisited for later words
        // prefix.delete(index, prefix.length());
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (tst.contains(word)) {
            return getScore(word);
        }
        return 0;
    }

    private int getScore(String s) {
        int length = s.length();

        if (length <= 2) {
            return 0;
        } else if (length <= 4) {
            return 1;
        } else if (length == 5) {
            return 2;
        } else if (length == 6) {
            return 3;
        } else if (length == 7) {
            return 5;
        } else {
            return 11;
        }
    }


    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }

}

