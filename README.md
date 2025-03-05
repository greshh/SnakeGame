# SnakeGame
_**Start:** March 30<sup>th</sup> 2023, **Finish:** April 24<sup>th</sup> 2023_

For a second year course: Games Programming

## Scenario

Develop a Java version of the classic game Snake. The game will allow a user to control the movement of the snake using the keyboard's cursor controls. The player will initially control a three-circle player object (Snake with red head and green body), which will move one cell at a time on the X or Y axis, representing the game world. As the player consumes a green apple, the snake will in turn grow by 1.

If the snake collides with itself, then the game is over, and it may prompt the user to play again. If the user consumes an apple, the apple will disappear and the length of the snake will be incremented by one. Apples eaten by the snake will be replaced immediately and are to be placed at random, on another square not currently occupied by the player’s snake or another item or obstacle. A grid for the game world can be used.

**Snake Movement:**

The snake can move into the cells immediately adjacent to itself but it cannot move back on itself. The snake will be moving forward in its current direction at all times.

**Snake Implementation:**

Use an Array or an ArrayList to represent the body of the snake using a maximum of 20 circles (including it’s head). An extra variable should be used to keep track of the current size of the snake. When the snake moves to a new cell, all the previous snake cells will be shuffled down by one array element. The direction of the head of the snake should be tracked.

**Snake Collisions:**

The game will end if the snake collides with itself or with the borders of the game. 
