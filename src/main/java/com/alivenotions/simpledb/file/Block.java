package com.alivenotions.simpledb.file;

public record Block(String fileName, int number) {
  public static Block of(String fileName, int number) {
    return new Block(fileName, number);
  }
}
