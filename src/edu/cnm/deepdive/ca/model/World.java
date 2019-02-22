package edu.cnm.deepdive.ca.model;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Random;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class World {

  public static final int DEFAULT_SIZE = 200;

  private Cell[][] terrain;
  private Cell[][] next;
  private long generation;
  private final Object lock = new Object();
  private long checksum;
  private int population;

  public World() {
    this(DEFAULT_SIZE);
  }

  public World(int size) {
    terrain = new Cell[size][size];
    next = new Cell[size][size];
    for (Cell[] cellRow : terrain) {
      Arrays.fill(cellRow, Cell.DEAD);
    }
  }

  public World(int size, double threshold, Random rng) {
    this(size);
    for (Cell[] cellRow : terrain) {
      for (int col = 0; col < cellRow.length; col++) {
        if (rng.nextDouble() < threshold) {
          cellRow[col] = Cell.ALIVE;
          population++;
        }
      }
    }
  }

  public void set(int row, int col, Cell cell) {
    Cell previous = terrain[row][col];
    if ((previous == null || previous == Cell.DEAD) && cell == Cell.ALIVE) {
      population++;
    } else if (previous == Cell.ALIVE && (cell == null || cell == Cell.DEAD)) {
      population--;
    }
    synchronized (lock) {
      terrain[row][col] = cell;
    }
  }

  public void tick() {
    Checksum checksum = new CRC32();
    BitSet bitSet = new BitSet(terrain[0].length);
    int population = 0;
    for (int row = 0; row < terrain.length; row++) {
      for (int col = 0; col < terrain[row].length; col++) {
        next[row][col] = terrain[row][col].next(terrain, row, col);
        bitSet.set(col, next[row][col] == Cell.ALIVE);
        if (next[row][col] == Cell.ALIVE) {
          population++;
        }
      }
      byte[] rowBytes = bitSet.toByteArray();
      checksum.update(rowBytes, 0, rowBytes.length);
    }
    this.checksum = checksum.getValue();
    synchronized (lock) {
      for (int row = 0; row < terrain.length; row++) {
        System.arraycopy(next[row], 0, terrain[row], 0, next[row].length);
      }
      this.population = population;
      generation++;
    }
  }

  public void copyTerrain(Cell[][] terrainCopy) {
    synchronized (lock) {
      for (int row = 0; row < terrain.length; row++) {
        System.arraycopy(terrain[row], 0, terrainCopy[row], 0, terrain[row].length);
      }
    }
  }

  public long getGeneration() {
    return generation;
  }

  public long getChecksum() {
    return checksum;
  }

  public int getPopulation() {
    return population;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (Cell[] cellRow : terrain) {
      for (Cell cellCol : cellRow) {
        if (cellCol == Cell.ALIVE) {
          builder.append('*');
        } else {
          builder.append(' ');
        }
      }
      builder.append('\n');
    }
    return builder.toString();
  }

}

