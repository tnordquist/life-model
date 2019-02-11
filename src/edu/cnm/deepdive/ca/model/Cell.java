package edu.cnm.deepdive.ca.model;

public enum Cell {
  DEAD {
    @Override
    public Cell next(Cell[][] terrain, int row, int column) {
      return (livingNeighbors(terrain, row, column) == 3) ? ALIVE : DEAD;
    }
  },
  ALIVE {
    @Override
    public Cell next(Cell[][] terrain, int row, int column) {
      int count = livingNeighbors(terrain, row, column);
      return (count == 2 || count == 3) ? ALIVE : DEAD;
    }

    @Override
    protected int livingNeighbors(Cell[][] terrain, int row, int column) {
      return super.livingNeighbors(terrain, row, column) - 1;
    }
  };

  public abstract Cell next(Cell[][] terrain, int row, int column);

  protected int livingNeighbors(Cell[][] terrain, int row, int column) {
    int count = 0;
    for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
      int wrapRow = Math.floorMod(row + rowOffset, terrain.length);
      for (int colOffset = -1; colOffset <= 1; colOffset++) {
        int wrapCol = Math.floorMod(column + colOffset, terrain[wrapRow].length);
        if (terrain[wrapRow][wrapCol] == ALIVE) {
          count++;
        }
      }
    }
    return count;
  }

}
