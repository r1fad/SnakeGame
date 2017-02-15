public class Game
{

// ----------------------------------------------------------------------
// Part a: the score message
  private String scoreMessage;

  public String getScoreMessage()
  {
    return this.scoreMessage;
  } // getScoreMessage


  public void setScoreMessage(String message)
  {
    this.scoreMessage = message;
  } // setScoreMessage


  public String getAuthor()
  {
    return "Rifad Lafir";
  } // getAuthor


// ----------------------------------------------------------------------
// Part b: constructor and grid accessors
  private final int gridSize;
  private final Cell[][] grid;

  public Game(int requiredGridSize)
  {
    this.gridSize = requiredGridSize;
    this.grid = new Cell[this.gridSize][this.gridSize];

    for(int row=0; row<this.gridSize; row++){
      for(int column=0; column<this.gridSize; column++){
        this.grid[row][column] = new Cell();
      }
    }
  } // Game


  public int getGridSize()
  {
    return this.gridSize;
  } // getGridSize


  public Cell getGridCell(int x, int y)
  {
    return grid[x][y];
  } // getGridCell


// ----------------------------------------------------------------------
// Part c: initial game state

// Part c-1: setInitialGameState method
  private int score=0;
  public void setInitialGameState(int requiredTailX, int requiredTailY,
                                  int requiredLength, int requiredDirection)
  {
    this.numberOfTress = 0;
    for(int row=0; row<this.gridSize; row++){
      for(int column=0; column<this.gridSize; column++){
        grid[row][column].setClear();
      }//for column
    }//for row
    if (treesEnabled)
      placeTree();
    placeSnake(requiredTailX,requiredTailY,requiredLength, requiredDirection);
    placeFood();
  } // setInitialGameState


// ----------------------------------------------------------------------
// Part c-2 place food
  public void placeFood(){
    Cell cellForFood;
    do{
      int x = (int) (Math.random() * this.gridSize);
      int y = (int) (Math.random() * this.gridSize);
      cellForFood = this.getGridCell(x,y);
    }while (cellForFood.getType() != 1);
    cellForFood.setFood();
    
  }//placeFood

// ----------------------------------------------------------------------
// Part c-3: place snake

  //defining instnace varialbes
  private int xOfTailOfSnake;
  private int yOfTailOfSnake;
  private int xOfHeadOfSnake;
  private int yOfHeadOfSnake;
  private int directionOfSnake;
  private int lengthOfSnake;

  public void placeSnake(int tailX, int tailY, int length, int direction){
    this.xOfTailOfSnake = tailX;
    this.yOfTailOfSnake = tailY;
    this.lengthOfSnake = length;
    this.directionOfSnake = direction;

    //setting the tail
    grid[xOfTailOfSnake][yOfTailOfSnake].setSnakeTail();
    grid[xOfTailOfSnake][yOfTailOfSnake].setSnakeOutDirection(directionOfSnake);
    int oppositeDirection = Direction.opposite(directionOfSnake);
    grid[xOfTailOfSnake][yOfTailOfSnake].setSnakeInDirection(oppositeDirection);

    //setting the body
    int xOfBodyOfSnake;
    int yOfBodyOfSnake;
    xOfBodyOfSnake = tailX+Direction.xDelta(direction);
    yOfBodyOfSnake = tailY+Direction.yDelta(direction);
    Cell nextCell = grid[xOfBodyOfSnake][yOfBodyOfSnake];
    for (int bodyIndex = 0; bodyIndex < lengthOfSnake-2; bodyIndex++){
      nextCell.setSnakeBody();
      nextCell.setSnakeOutDirection(direction);
      nextCell.setSnakeInDirection(oppositeDirection);
      xOfBodyOfSnake += Direction.xDelta(direction);
      yOfBodyOfSnake += Direction.yDelta(direction);
      nextCell = grid[xOfBodyOfSnake][yOfBodyOfSnake];
    }//for

    //setting the head
    xOfHeadOfSnake = xOfBodyOfSnake;
    yOfHeadOfSnake = yOfBodyOfSnake;
    grid[xOfHeadOfSnake][yOfHeadOfSnake].setSnakeHead();
    grid[xOfHeadOfSnake][yOfHeadOfSnake].setSnakeOutDirection(direction);
    grid[xOfHeadOfSnake][yOfHeadOfSnake].setSnakeInDirection(oppositeDirection);
  }//placeSnake


// ----------------------------------------------------------------------
// Part d: set snake direction


  public void setSnakeDirection(int requiredDirection)
  {
    this.directionOfSnake = requiredDirection;

    //store cell that contains snake head in a variable
    Cell snakeHead = grid[xOfHeadOfSnake][yOfHeadOfSnake];
    
    //check if snake trying to move into itself
    if(snakeHead.getSnakeInDirection() != requiredDirection)
      snakeHead.setSnakeOutDirection(requiredDirection);
    else
      setScoreMessage("You cannot move into your own body");
  } // setSnakeDirection


// ----------------------------------------------------------------------
// Part e: snake movement

// Part e-1: move method
  public void move(int moveValue)
  {
    //store cell that contains snake head in a variable
    Cell snakeHead = grid[xOfHeadOfSnake][yOfHeadOfSnake];

    if(!snakeHead.isSnakeBloody()){
      int newXHead = xOfHeadOfSnake+Direction.xDelta(directionOfSnake);
      int newYHead = yOfHeadOfSnake+Direction.yDelta(directionOfSnake);  

      boolean okayToMove = checkDealCrashes(newXHead, newYHead);
    
      if (okayToMove){
        int cellType = grid[newXHead][newYHead].getType();

        moveSnakeHead(newXHead,newYHead);

        //if cell contains food
        if (cellType == 5)
          eatFood(moveValue);
        else
          moveSnakeTail();

    }// if okay to move

   }// if snake not bloody
    
  } // move


// ----------------------------------------------------------------------
// Part e-2: move the snake head
  public void moveSnakeHead(int xHead, int yHead){

    int oldHeadOutDir = grid[xOfHeadOfSnake][yOfHeadOfSnake].getSnakeOutDirection();


    grid[xOfHeadOfSnake][yOfHeadOfSnake].setSnakeBody();

    grid[xHead][yHead].setSnakeHead(Direction.opposite(directionOfSnake)
                                    ,directionOfSnake);

    xOfHeadOfSnake = xHead;
    yOfHeadOfSnake = yHead;
  }//moveSnakeHead


// ----------------------------------------------------------------------
// Part e-3: move the snake tail
  public void moveSnakeTail(){
    grid[xOfTailOfSnake][yOfTailOfSnake].setClear();

    int snakeTailOutDir;
    snakeTailOutDir=grid[xOfTailOfSnake][yOfTailOfSnake].getSnakeOutDirection();


    xOfTailOfSnake+=Direction.xDelta(snakeTailOutDir);
    yOfTailOfSnake+=Direction.yDelta(snakeTailOutDir);

    grid[xOfTailOfSnake][yOfTailOfSnake].setSnakeTail();    
  }//moveSnakeTail



// ----------------------------------------------------------------------
// Part e-4: check for and deal with crashes
  public boolean checkDealCrashes(int xHead, int yHead){
    boolean triedToLeaveGrid;
    boolean cellOccupied;
    
    triedToLeaveGrid = (xHead<0 || yHead<0 || xHead>=gridSize || yHead>=gridSize);

    if (triedToLeaveGrid){
      boolean dead = countdown();
      if (dead)
        grid[xOfHeadOfSnake][yOfHeadOfSnake].setSnakeBloody(true);
      return false;
    }

    cellOccupied = (grid[xHead][yHead].getType()==3 || 
                    grid[xHead][yHead].getType()==4 ||
                    grid[xHead][yHead].getType()==6);

    if (cellOccupied){ 
      boolean dead = countdown();
      if (dead){
          grid[xOfHeadOfSnake][yOfHeadOfSnake].setSnakeBloody(true);
          grid[xHead][yHead].setSnakeBloody(true);
        }
        return false;  
    }
    else
      //resetCountdown();
      return true;    
  }//checkDealCrashes


// ----------------------------------------------------------------------
// Part e-5: eat the food
  public void eatFood(int moveVal){
    if(!treesEnabled){
      int addToScore = moveVal*((lengthOfSnake/(gridSize*gridSize/36+1))+1);
      this.score += addToScore;
      this.setScoreMessage(addToScore+" Points!!");
      this.lengthOfSnake+=1; 
      this.placeFood();
    }else{
      int addToScore = moveVal*((lengthOfSnake/(gridSize*gridSize/36+1))+1);
      addToScore *= this.numberOfTress;
      this.score += addToScore;
      this.setScoreMessage(addToScore+" Points!!");
      this.lengthOfSnake+=1; 
      this.placeFood();
      placeTree();
    }


  }
  public int getScore()
  {
    return score;
  } // getScore


// ----------------------------------------------------------------------
// Part f: cheat

  public void cheat()
  {
    this.score = (this.score==1)?0:this.score;
    int halfScore = this.score/2;

    this.score-=halfScore;

    this.setScoreMessage("You lost "+halfScore+" because you cheated");

    //set cells to not bloody
    for(int row=0;row<gridSize;row++)
      for(int column=0;column<gridSize;column++)
        grid[row][column].setSnakeBloody(false);
  } // cheat


// ----------------------------------------------------------------------
// Part g: trees
  private int numberOfTress;
  private boolean treesEnabled=false;

  public void toggleTrees()
  {
    if (treesEnabled){
      treesEnabled = false;
      for(int row=0;row<gridSize;row++)
        for(int column=0;column<gridSize;column++)
          if (grid[row][column].getType()==6)
            grid[row][column].setClear();
    }
    else if (!treesEnabled){
      treesEnabled = true;
      placeTree();
    }
  } // toggleTrees

  private void placeTree(){
    Cell cellForTree;
    do{
      int x = (int) (Math.random() * this.gridSize);
      int y = (int) (Math.random() * this.gridSize);
      cellForTree = this.getGridCell(x,y);
    }while (cellForTree.getType() != 1);
    cellForTree.setTree();
    this.numberOfTress+=1;
  }
// ----------------------------------------------------------------------
// Part h: crash countdown
  private final int numberOfMoves = 5;
  private int currentCountdown =5;

  private void resetCountdown(){
    if (currentCountdown<numberOfMoves)
      setScoreMessage("You escaped death by "+ currentCountdown +" moves");
    this.currentCountdown = numberOfMoves;
  }//resetCountdown

  private boolean countdown(){
    this.currentCountdown--;
    if (currentCountdown>0)
    { setScoreMessage("You have "+currentCountdown+" moves until you die");
      return false;
    }
    else {
      resetCountdown();
      setScoreMessage("You died! You cannot leave the area!");
      return true;
    }   
  }//countdown





// ----------------------------------------------------------------------
// Part i: optional extras


  public String optionalExtras()
  {
    return "  No optional extras defined\n";
  } // optionalExtras


  public void optionalExtraInterface(char c)
  {
    if (c > ' ' && c <= '~')
      setScoreMessage("Key " + new Character(c).toString()
                      + " is unrecognised (try h)");
  } // optionalExtraInterface

} // class Game
