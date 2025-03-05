import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class SnakeGame extends GameEngine 
{
    public static void main(String args[]) 
    {
        createGame(new SnakeGame(), 10);
    }

    /*-----------------------------------------*/
    // CLASSES
    /*-----------------------------------------*/

    class Body
    {
        // body position.
        double bodyPositionX;
        double bodyPositionY;

        // body velocity.
        double bodyVelocityX;
        double bodyVelocityY;

        // body angle.
        double bodyAngle;

        // body active.
        boolean bodyActive;
    }

    class Player {
        // snake position.
        int snakePositionX, snakePositionY;

        // snake velocity.
        double snakeVelocityX, snakeVelocityY;

        // snake angle.
        double snakeAngle;

        // snake active.
        boolean snakeActive = true;

        // snake body.
        ArrayList<Body> body = new ArrayList<>();
        int bodyCount;
    }

    class Apple 
    {
        // apple position.
        int applePositionX;
        int applePositionY;

        // apple active.
        boolean appleActive;
    }

    // enables multiplayer.
    Player[] player = { new Player(), new Player() };
    Apple[] apple = { new Apple(), new Apple(), new Apple() };

    /*-----------------------------------------*/
    // MAIN GAME
    /*-----------------------------------------*/

    boolean up, down, left, right, no1, no2, W, A, S, D, esc; // booleans for key-pressing.

    boolean gameOver, gameCompleted, winnerPlayerOne, winnerPlayerTwo, helpMenu;

    boolean isMultiPlayer = false;

    int highScore = 0;

    public void init() 
    {
        // initalise game over.
        gameOver = false;

        // initialise game completed.
        gameCompleted = false;

        // initialise winner.
        winnerPlayerOne = false;
        winnerPlayerTwo = false;

        // initalise help menu.
        helpMenu = false;

        // initialise key booleans.
        up = false;
        down = false;
        left = false;
        right = false;
        W = false;
        S = false;
        A = false;
        D = false;
        no1 = false;
        no2 = false;

        player[0].snakeActive = true;

        if (isMultiPlayer == false) {
            player[1].snakeActive = false;
        } else {
            player[1].snakeActive = true;
        }

        // creating body objects and initialising bodyActive.
        for (int p = 0; p < 2; p++) 
        {
            player[p].body = new ArrayList<Body>();

            if (player[p].body.size() < 20) 
            {
                for (int i = 0; i < 20; i++) {
                    player[p].body.add(new Body());
                }
            }

            for (int i = 0; i < 2; i++) {
                player[p].body.get(i).bodyActive = true;
            }

            for (int i = 2; i < 20; i++) {
                player[p].body.get(i).bodyActive = false;
            }

            player[p].bodyCount = 2;
        }

        initSnake();

        // initialise apple.
        initApple();
        if (isMultiPlayer) {
            for (int a = 0; a < 3; a++) {
                randomApple(width(), height(), a);
                apple[a].appleActive = true;
            }
        } else if (!isMultiPlayer) {
            randomApple(width(), height(), 0);
            apple[0].appleActive = true;
            apple[1].appleActive = false;
            apple[2].appleActive = false;
        }
    }

    public void update(double dt) 
    {
        if (gameOver == true) {
            // updates high score for single-player.
            if ((player[0].bodyCount > highScore) && !isMultiPlayer) { 
                highScore = player[0].bodyCount; 
                }

            if (no1) {
                gameOver = false;
                isMultiPlayer = false;
                init();
            } 
            if (no2) {
                gameOver = false;
                isMultiPlayer = true;
                init();
            }
            return;
        }

        if (gameCompleted == true) {
            // updates high score for single-player.
            if ((player[0].bodyCount > highScore) && !isMultiPlayer) {
                highScore = player[0].bodyCount;
            }

            if (no1) {
                gameCompleted = false;
                isMultiPlayer = false;
                init();
            } 
            if (no2) {
                gameCompleted = false;
                isMultiPlayer = true;
                init();
            }
            return;
        }

        if (esc) {
            if (!helpMenu) {
                player[0].snakeActive = false;
                player[1].snakeActive = false;
                apple[0].appleActive = false;
                apple[1].appleActive = false;
                apple[2].appleActive = false;
                helpMenu = true;
            } else if (helpMenu) {
                player[0].snakeActive = true;
                apple[0].appleActive = true;
                if (isMultiPlayer) { 
                    player[1].snakeActive = true; 
                    apple[1].appleActive = true;
                    apple[2].appleActive = true;
                }
                helpMenu = false;
            }
            return;
        }

        // updates both snakes.
        updateSnake(dt);

        for (int p = 0; p < 2; p++)
        {
            if (player[p].snakeActive == true) 
            {
                // checks for snake-body collision.
                for (int k = 1; k < 20; k++) {
                    if ((distance(player[p].snakePositionX, player[p].snakePositionY,
                        player[p].body.get(k).bodyPositionX,
                        player[p].body.get(k).bodyPositionY) < 5)
                            && (player[p].body.get(k).bodyActive == true)) {
                        if (isMultiPlayer) {
                            if (p == 0) { // if player 1 hits own body, player 2 wins.
                                winnerPlayerTwo = true;
                                winnerPlayerOne = false;
                            }
                            if (p == 1) { // if player 2 hits own body, player 1 wins. winnerPlayerOne = true;
                                winnerPlayerTwo = false;
                            }
                        }
                        gameOver = true;
                    }
                }

                // checks for snake-apple collision.
                for (int a = 0; a < 3; a++)
                {
                    if (apple[a].appleActive)
                    {
                        if ((distance(player[p].snakePositionX, player[p].snakePositionY, apple[a].applePositionX,
                                apple[a].applePositionY) <= 12)) {
                            randomApple(width(), height(), a);
                            if (player[p].bodyCount < 20) {
                                player[p].body.get(player[p].bodyCount).bodyActive = true;
                                player[p].body.get(player[p].bodyCount).bodyAngle = player[p].snakeAngle;
                                if (player[p].bodyCount == 0) {
                                    if (player[p].snakeAngle == 0) {
                                        player[p].body.get(player[p].bodyCount).bodyPositionX = player[p].snakePositionX;
                                        player[p].body.get(player[p].bodyCount).bodyPositionY = player[p].snakePositionY + 10;
                                    } else if (player[p].snakeAngle == 90) {
                                        player[p].body.get(player[p].bodyCount).bodyPositionX = player[p].snakePositionX + 10;
                                        player[p].body.get(player[p].bodyCount).bodyPositionY = player[p].snakePositionY;
                                    } else if (player[p].snakeAngle == 180) {
                                        player[p].body.get(player[p].bodyCount).bodyPositionX = player[p].snakePositionX;
                                        player[p].body.get(player[p].bodyCount).bodyPositionY = player[p].snakePositionY - 10;
                                    } else if (player[p].snakeAngle == 270) {
                                        player[p].body.get(player[p].bodyCount).bodyPositionX = player[p].snakePositionX - 10;
                                        player[p].body.get(player[p].bodyCount).bodyPositionY = player[p].snakePositionY;
                                    }
                                } else {
                                    if (player[p].body.get(player[p].bodyCount - 1).bodyAngle == 0) {
                                        player[p].body.get(player[p].bodyCount).bodyPositionX = player[p].body.get(player[p].bodyCount - 1).bodyPositionX;
                                        player[p].body.get(player[p].bodyCount).bodyPositionY = player[p].body.get(player[p].bodyCount - 1).bodyPositionY + 10;
                                    } else if (player[p].snakeAngle == 90) {
                                        player[p].body.get(player[p].bodyCount).bodyPositionX = player[p].body.get(player[p].bodyCount - 1).bodyPositionX + 10;
                                        player[p].body.get(player[p].bodyCount).bodyPositionY = player[p].body.get(player[p].bodyCount - 1).bodyPositionY;
                                    } else if (player[p].snakeAngle == 180) {
                                        player[p].body.get(player[p].bodyCount).bodyPositionX = player[p].body.get(player[p].bodyCount - 1).bodyPositionX;
                                        player[p].body.get(player[p].bodyCount).bodyPositionY = player[p].body.get(player[p].bodyCount - 1).bodyPositionY - 10;
                                    } else if (player[p].snakeAngle == 270) {
                                        player[p].body.get(player[p].bodyCount).bodyPositionX = player[p].body.get(player[p].bodyCount - 1).bodyPositionX - 10;
                                        player[p].body.get(player[p].bodyCount).bodyPositionY = player[p].body.get(player[p].bodyCount - 1).bodyPositionY;
                                    }
                                }
                            }
                            player[p].bodyCount++;
                        }
                    }
                }
            }
        }

        // checks for snake-snake collisions.
        if (isMultiPlayer) 
        {
            // if player 1 head collides with player 2's body, player 2 wins.
            for (int b = 0; b < 20; b++) {
                if (player[1].body.get(b).bodyActive) {
                    if (distance(player[0].snakePositionX, player[0].snakePositionY, 
                        player[1].body.get(b).bodyPositionX, player[1].body.get(b).bodyPositionY) < 5) {
                        winnerPlayerTwo = true;
                        winnerPlayerOne = false;
                        gameOver = true;
                    }
                }
            }

            // if player 2 head collides with player 1's body, player 1 wins.
            for (int b = 0; b < 20; b++) {
                if (player[0].body.get(b).bodyActive) {
                    if (distance(player[1].snakePositionX, player[1].snakePositionY,
                        player[0].body.get(b).bodyPositionX, player[0].body.get(b).bodyPositionY) < 5) {
                        winnerPlayerOne = true;
                        winnerPlayerTwo = false;
                        gameOver = true;
                    }
                }
            }
        }

        // checks if game is completed.
        if (player[0].bodyCount == 20) {
            winnerPlayerOne = true;
            winnerPlayerTwo = false;
            gameCompleted = true;
        }

        if (player[1].bodyCount == 20) {
            winnerPlayerTwo = true;
            winnerPlayerOne = false;
            gameCompleted = true;
        }
    }

    public void paintComponent() 
    {
        changeBackgroundColor(black);
        clearBackground(width(), height());
        
        if ((gameOver == false) && (gameCompleted == false)) {
            drawSnake();
            drawApple();
            
            // displays player 1's score.
            if (player[0].snakeActive)
            {
                changeColor(red);
                drawText(10, 30, "PLAYER 1", "Pixeloid Mono", 25);
                changeColor(green);
                drawText(10, 60, Integer.toString(player[0].bodyCount), "Pixeloid Mono", 25);
            }

            // displays player 1's score.
            if (player[1].snakeActive) 
            {
                changeColor(blue);
                drawText(360, 30, "PLAYER 2", "Pixeloid Mono", 25);
                changeColor(yellow);
                drawText(440, 60, Integer.toString(player[1].bodyCount), "Pixeloid Mono", 25);
            }

        } else if (gameOver && !isMultiPlayer) {
            // if the game is over, display "game over" text.
            changeColor(white);
            drawBoldText(85, 250, "GAME OVER!", "Pixeloid Mono", 50);
            drawText(35, 290, "(press '1' for single-player or '2' for multi-player)", "Pixeloid Mono", 12);
            drawBoldText(150, 340, "Your high score is: " + highScore, "Pixeloid Mono", 14);
        } else if (gameCompleted && !isMultiPlayer) {
            // if the game is over, display "game over" text.
            changeColor(white);
            drawBoldText(25, 250, "GAME COMPLETED!", "Pixeloid Mono", 45);
            drawText(35, 290, "(press '1' for single-player or '2' for multi-player)", "Pixeloid Mono", 12);
            drawBoldText(150, 340, "Your high score is: " + highScore, "Pixeloid Mono", 14);
        } else if ((gameCompleted || gameOver) && isMultiPlayer) {
            if (winnerPlayerOne) {
                changeColor(white);
                drawBoldText(35, 260, "PLAYER ONE WINS!", "Pixeloid Mono", 40);
                drawText(35, 290, "(press '1' for single-player or '2' for multi-player)", "Pixeloid Mono", 12);
            } else if (winnerPlayerTwo) {
                changeColor(white);
                drawBoldText(35, 260, "PLAYER TWO WINS!", "Pixeloid Mono", 40);
                drawText(35, 290, "(press '1' for single-player or '2' for multi-player)", "Pixeloid Mono", 12);
            }
        }

        if (!helpMenu && !gameOver && !gameCompleted) {
            changeColor(white);
            // draw title.
            drawBoldText(170, 50, "SNAKE", "Pixeloid Mono", 50);
        }

        if (helpMenu) {
            // draws helpMenu box.
            changeColor(32, 32, 32);
            drawSolidRectangle(50, 50, 400, 400);
            changeColor(white);

            // shows heading.
            drawText(65, 100, "--- PAUSED ---", "Pixeloid Mono", 40);

            // shows player one controls.
            drawBoldText(60, 200, "Player One:", "Pixeloid Mono", 20);
            changeColor(64, 64, 64);
            drawSolidRectangle(75, 255, 30, 30);
            drawSolidRectangle(115, 255, 30, 30);
            drawSolidRectangle(115, 215, 30, 30);
            drawSolidRectangle(155, 255, 30, 30);
            changeColor(white);
            drawBoldText(77, 273, "left", "Pixeloid Mono", 10);
            drawBoldText(117, 273, "down", "Pixeloid Mono", 10);
            drawBoldText(155, 273, "right", "Pixeloid Mono", 10);
            drawBoldText(124, 233, "up", "Pixeloid Mono", 10);

            // shows player two controls.
            drawBoldText(290, 200, "Player Two:", "Pixeloid Mono", 20);
            changeColor(64, 64, 64);
            drawSolidRectangle(305, 255, 30, 30);
            drawSolidRectangle(345, 255, 30, 30);
            drawSolidRectangle(345, 215, 30, 30);
            drawSolidRectangle(385, 255, 30, 30);
            changeColor(white);
            drawBoldText(316, 273, "A", "Pixeloid Mono", 10);
            drawBoldText(356, 273, "S", "Pixeloid Mono", 10);
            drawBoldText(397, 273, "D", "Pixeloid Mono", 10);
            drawBoldText(356, 233, "W", "Pixeloid Mono", 10);

            drawText(105, 430, "press 'esc' to resume.", "Pixeloid Mono", 20);
        }
    }

    public void keyPressed(KeyEvent e) 
    {
        /* PLAYER 1 CONTROLS */

        // pressed left arrow to go left.
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            left = true;
        }
        // pressed right arrow to go right.
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            right = true;
        }

        // pressed up arrow to go up.
        if (e.getKeyCode() == KeyEvent.VK_UP) { 
            up = true;
        }

        // pressed down arrow to go down.
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            down = true;
        }

        /* PLAYER 2 CONTROLS */

        // pressed 'A' key to go left.
        if (e.getKeyCode() == KeyEvent.VK_A) {
            A = true;
        }
        // pressed 'D' key to go right.
        if (e.getKeyCode() == KeyEvent.VK_D) {
            D = true;
        }

        // pressed 'W' key to go up.
        if (e.getKeyCode() == KeyEvent.VK_W) {
            W = true;
        }

        // pressed 'S' key to go down.
        if (e.getKeyCode() == KeyEvent.VK_S) {
            S = true;
        }

        /* OTHER */

        // pressed '1' key.
        if (e.getKeyChar() == KeyEvent.VK_1) {
            no1 = true;
        }

        // pressed '2' key.
        if (e.getKeyChar() == KeyEvent.VK_2) {
            no2 = true;
        }

        // pressed "esc" key for help menu.
        if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
            esc = true;
        }
    }

    public void keyReleased(KeyEvent e) 
    {
        /* PLAYER 1 CONTROLS */

        // pressed left arrow to go left.
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            left = false;
        }
        // pressed right arrow to go right.
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            right = false;
        }

        // pressed up arrow to go up.
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            up = false;
        }

        // pressed down arrow to go down.
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            down = false;
        }

        /* PLAYER 2 CONTROLS */

        // pressed 'A' key to go left.
        if (e.getKeyCode() == KeyEvent.VK_A) {
            A = false;
        }
        // pressed 'D' key to go right.
        if (e.getKeyCode() == KeyEvent.VK_D) {
            D = false;
        }

        // pressed 'W' key to go up.
        if (e.getKeyCode() == KeyEvent.VK_W) {
            W = false;
        }

        // pressed 'S' key to go down.
        if (e.getKeyCode() == KeyEvent.VK_S) {
            S = false;
        }

        /* OTHER */

        // pressed '1' key.
        if (e.getKeyChar() == KeyEvent.VK_1) {
            no1 = false;
        }

        // pressed '2' key.
        if (e.getKeyChar() == KeyEvent.VK_2) {
            no2 = false;
        }

        // pressed "esc" key for help menu.
        if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
            esc = false;
        }
    }

    /*-----------------------------------------*/
    // SNAKE
    /*-----------------------------------------*/

    public void initSnake() 
    {
        for (int p = 0; p < 2; p++) 
        {
            if (player[p].snakeActive == true) 
            {
                // initalise snake variables.

                // snake initialises at random place in the screen. similar to randomApple().
                player[p].snakePositionX = rand(width()-60)+30;
                player[p].snakePositionY = rand(height()-110)+80; 

                player[p].snakeVelocityX = 0;
                player[p].snakeVelocityY = 0;
                
                if ((player[p].snakePositionY >= 0) && (player[p].snakePositionY <= 250)) {
                    player[p].snakeAngle = 180;
                } else if ((player[p].snakePositionY > 250) && (player[p].snakePositionY <= 500)) {
                    player[p].snakeAngle = 0;
                }

                if (player[p].snakeAngle == 0) {
                    player[p].body.get(0).bodyPositionX = player[p].snakePositionX;
                    player[p].body.get(0).bodyPositionY = player[p].snakePositionY + 10;
                    player[p].body.get(1).bodyPositionX = player[p].body.get(0).bodyPositionX;
                    player[p].body.get(1).bodyPositionY = player[p].body.get(0).bodyPositionY + 10;
                }

                if (player[p].snakeAngle == 180) {
                    player[p].body.get(0).bodyPositionX = player[p].snakePositionX;
                    player[p].body.get(0).bodyPositionY = player[p].snakePositionY - 10;
                    player[p].body.get(1).bodyPositionX = player[p].body.get(0).bodyPositionX;
                    player[p].body.get(1).bodyPositionY = player[p].body.get(0).bodyPositionY - 10;
                }
            }
        }
    }

    public void drawSnake() 
    {
        if (player[0].snakeActive == true) 
        {
            changeColor(red);
            saveCurrentTransform();
            drawSolidCircle(player[0].snakePositionX, player[0].snakePositionY, 5);

            for (Body b:player[0].body) {
                if (b.bodyActive) {
                    changeColor(green);
                    saveCurrentTransform();
                    drawSolidCircle(b.bodyPositionX, b.bodyPositionY, 5);
                }
            }
        }

        if (player[1].snakeActive == true) 
        {
            changeColor(blue);
            saveCurrentTransform();
            drawSolidCircle(player[1].snakePositionX, player[1].snakePositionY, 5);

            for (Body b : player[1].body) {
                if (b.bodyActive) {
                    changeColor(yellow);
                    saveCurrentTransform();
                    drawSolidCircle(b.bodyPositionX, b.bodyPositionY, 5);
                }
            }
        }
    }

    public void updateSnake(double dt)
    {
        for (int p = 0; p < 2; p++) 
        {
            if (player[p].snakeActive == true) 
            {
                for (int j = 19; j > 0; j--) {
                    if (player[p].body.get(j).bodyActive) {
                        player[p].body.get(j).bodyPositionX = player[p].body.get(j-1).bodyPositionX;
                        player[p].body.get(j).bodyPositionY = player[p].body.get(j-1).bodyPositionY;
                        player[p].body.get(j).bodyAngle = player[p].body.get(j-1).bodyAngle;
                    }
                }
                player[p].body.get(0).bodyPositionX = player[p].snakePositionX;
                player[p].body.get(0).bodyPositionY = player[p].snakePositionY;
                player[p].body.get(0).bodyAngle = player[p].snakeAngle;

                player[p].snakeVelocityX = sin(player[p].snakeAngle) * 700 * dt;
                player[p].snakeVelocityY = -cos(player[p].snakeAngle) * 700 * dt;

                player[p].snakePositionX += player[p].snakeVelocityX * dt;
                player[p].snakePositionY += player[p].snakeVelocityY * dt;
            }
        }

        // checks for snake-wall collision.

        if (player[0].snakeActive)
        {
            if ((player[0].snakePositionX < 5) || (player[0].snakePositionY < 5) || (player[0].snakePositionY > 495)
                    || (player[0].snakePositionX > 495)) {
                gameOver = true;
                winnerPlayerTwo = true;
                winnerPlayerOne = false;
                return;
            }
        }

        if (player[1].snakeActive) 
        {
            if ((player[1].snakePositionX < 5) || (player[1].snakePositionY < 5) || (player[1].snakePositionY > 495)
                    || (player[1].snakePositionX > 495)) {
                gameOver = true;
                winnerPlayerOne = true;
                winnerPlayerTwo = false;
                return;
            }
        }

        // controls player 1.

        if (up && player[0].snakeAngle != 180) {
            player[0].snakeAngle = 0;
        }

        if (down && player[0].snakeAngle != 0) {
            player[0].snakeAngle = 180;
        }

        if (left && player[0].snakeAngle != 90) {
            player[0].snakeAngle = 270;
        }

        if (right && player[0].snakeAngle != 270) {
            player[0].snakeAngle = 90;
        }

        // controls player 2.

        if (W && player[1].snakeAngle != 180) {
            player[1].snakeAngle = 0;
        }

        if (S && player[1].snakeAngle != 0) {
            player[1].snakeAngle = 180;
        }

        if (A && player[1].snakeAngle != 90) {
            player[1].snakeAngle = 270;
        }

        if (D && player[1].snakeAngle != 270) {
            player[1].snakeAngle = 90;
        }
    }

    /*-----------------------------------------*/
    // APPLE
    /*-----------------------------------------*/

    Image appleImage;

    public void initApple() 
    {
        appleImage = loadImage("apple.png");
    }

    public void randomApple(int width, int height, int a) 
    {
        // giving a 10 pixel allowance at the edges of the screen.
        apple[a].applePositionX = rand(width-20)+10;
        apple[a].applePositionY = rand(height-70)+60;
    }

    public void drawApple() {
        for (Apple i:apple)
        {
            if (i.appleActive)
            {
                saveCurrentTransform();
                drawImage(appleImage, i.applePositionX, i.applePositionY, 10, 10);
                restoreLastTransform();
            }
        }
    }

}
