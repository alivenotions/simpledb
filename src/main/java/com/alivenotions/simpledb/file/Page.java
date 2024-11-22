package com.alivenotions.simpledb.file;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Page {
  private final ByteBuffer buffer;
  public static final Charset CHARSET = StandardCharsets.UTF_8;

  private Page(byte[] bytes) {
    buffer = ByteBuffer.wrap(bytes);
  }

  private Page(int blocksize) {
    buffer = ByteBuffer.allocateDirect(blocksize);
  }

  public static Page newPage(byte[] bytes) {
    return new Page(bytes);
  }

  public static Page newPage(int blockSize) {
    return new Page(blockSize);
  }

  public int getInt(int offset) {
    return buffer.getInt(offset);
  }

  public void setInt(int offset, int value) {
    buffer.putInt(offset, value);
  }

  public byte[] getBytes(int offset) {
    buffer.position(offset);
    int length = buffer.getInt();
    byte[] b = new byte[length];
    buffer.get(b);
    return b;
  }

  public void setBytes(int offset, byte[] value) {
    buffer.position(offset);
    buffer.putInt(value.length);
    buffer.put(value);
  }

  public String getString(int offset) {
    byte[] b = getBytes(offset);
    return new String(b, CHARSET);
  }

  public void setString(int offset, String value) {
    byte[] b = value.getBytes(CHARSET);
    setBytes(offset, b);
  }

  public static int maxLength(int strlen) {
    float bytesPerChar = CHARSET.newEncoder().maxBytesPerChar();
    return Integer.BYTES + (strlen * (int) bytesPerChar);
  }

  ByteBuffer contents() {
    buffer.position(0);
    return buffer;
  }
}
