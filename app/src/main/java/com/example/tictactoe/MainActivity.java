package com.example.tictactoe;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;



public class MainActivity extends AppCompatActivity {

    private enum Player {X, O, NONE}
    private Player activePlayer = Player.X;
    private Player[] gameState = new Player[9];
    private final int[][] winPositions = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
            {0, 4, 8}, {2, 4, 6}
    };
    boolean gameActive = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Arrays.fill(gameState, Player.NONE);
    }

    public void playerTap(View view) {
        ImageView img = (ImageView) view;
        int tappedImage = Integer.parseInt(img.getTag().toString());

        if (!gameActive) {
            reset();
            return;
        }

        if (gameState[tappedImage] == Player.NONE) {
            gameState[tappedImage] = activePlayer;
            img.setTranslationY(-1000f);

            if (activePlayer == Player.X) {
                activePlayer = Player.O;
                img.setImageResource(R.drawable.x);
                updateStatus("Ai thinking!");

                int bestMove = findBestMove();
                if (bestMove != -1){
                    gameState[bestMove] = activePlayer;
                    ImageView bestMoveImg = findViewById(getResources().getIdentifier("c" + bestMove, "id", getPackageName()));
                    bestMoveImg.setImageResource(R.drawable.o);
                    bestMoveImg.setTranslationY(-1000f);
                    bestMoveImg.animate().translationYBy(1000f).setDuration(300);

                    activePlayer = Player.X;
                    updateStatus("Tap to play");
                    checkGameState();

                }


            }

            img.animate().translationYBy(1000f).setDuration(300);
            checkGameState();

        }

    }


    private void checkGameState() {

        for (int[] winPosition : winPositions) {
            if (gameState[winPosition[0]] != Player.NONE &&
                    gameState[winPosition[0]] == gameState[winPosition[1]] &&
                    gameState[winPosition[1]] == gameState[winPosition[2]]) {
                gameActive = false;
                updateStatus(gameState[winPosition[0]] == Player.X ? "You Won!" : "Ai Won!");
                return;

            }
        }

        if (isBoardFull()){
            gameActive = false;
            updateStatus("Game Draw!\n" +
                        "Tap to restart"
            );
        }

    }


    public void reset(){
        activePlayer = Player.X;
        Arrays.fill(gameState, Player.NONE);
        gameActive = true;

        for (int i = 0; i < gameState.length; i++) {
            String imageViewID = "c" + i;
            int resID = getResources().getIdentifier(imageViewID, "id", getPackageName());
            ((ImageView) findViewById(resID)).setImageResource(0);

        }

        updateStatus("Tap to play");

    }


    private boolean isBoardFull() {
        for (Player state : gameState) {
            if (state == Player.NONE) {
                return false;
            }
        }
        return true;
    }


    private void updateStatus(String message) {
        TextView status = findViewById(R.id.statusBar);
        status.setText(message);
    }


    // methods for Ai move using MinMax algorithm with alpha beta pruning

    private int evaluateBoard(){
        for(int [] winPosition : winPositions){
            if(gameState[winPosition[0]] != Player.NONE &&
                gameState[winPosition[0]] == gameState[winPosition[1]] &&
                gameState[winPosition[1]] == gameState[winPosition[2]]){
                if (gameState[winPosition[0]] == Player.X){
                    return +10;
                } else if (gameState[winPosition[0]] == Player.O){
                    return -10;
                }

            }

        }

        return 0;
    }

    private int minmax(Player[] board, boolean isMax, int depth, int alpha, int beta){
        int score = evaluateBoard();

        if (score == 10) return score - depth;

        if (score == -10) return score + depth;

        if (isBoardFull()) return 0;

        if (isMax){
            int best = Integer.MIN_VALUE;

            for (int i = 0; i < board.length; i++) {
                if (board[i] == Player.NONE){
                    board[i] = Player.X;
                    best = Math.max(best, minmax(board, false, depth + 1, alpha, beta));
                    board[i] = Player.NONE;
                    alpha = Math.max(alpha, best);

                    // Alpha-Beta Pruning
                    if (beta <= alpha) break;
                }
            }

            return best;

        } else {
            int best = Integer.MAX_VALUE;

            for (int i = 0; i < board.length; i++) {
                if (board[i] == Player.NONE){
                    board[i] = Player.O;
                    best = Math.min(best, minmax(board, true, depth + 1, alpha, beta));
                    board[i] = Player.NONE;
                    beta = Math.min(beta, best);

                    // Alpha-Beta Pruning
                    if (beta <= alpha) break;
                }
            }
            return best;
        }

    }


    private int findBestMove(){
        int bestVal = Integer.MIN_VALUE;
        int bestMove = -1;

        for (int i = 0; i < gameState.length; i++) {
            if(gameState[i] == Player.NONE){
                gameState[i] = Player.X;
                int moveVal = minmax(gameState, false, 0,Integer.MIN_VALUE, Integer.MAX_VALUE);
                gameState[i] = Player.NONE;

                if (moveVal > bestVal){
                    bestMove = i;
                    bestVal = moveVal;
                }

            }
        }
        return bestMove;
    }





}