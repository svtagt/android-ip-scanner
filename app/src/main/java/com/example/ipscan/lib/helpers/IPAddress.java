package com.example.ipscan.lib.helpers;

public class IPAddress {

  private final int value;

  public IPAddress(String stringValue) {
    String[] parts = stringValue.split("\\.");
    if (parts.length != 4) {
      throw new IllegalArgumentException();
    }
    value =
        (Integer.parseInt(parts[0], 10) << (8 * 3)) & 0xFF000000 |
        (Integer.parseInt(parts[1], 10) << (8 * 2)) & 0x00FF0000 |
        (Integer.parseInt(parts[2], 10) << (8 * 1)) & 0x0000FF00 |
        (Integer.parseInt(parts[3], 10) << (8 * 0)) & 0x000000FF;
  }

  public IPAddress(int value) {
    this.value = value;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (int i = 3; i >= 0; --i) {
      sb.append(getOctet(i));
      if (i != 0) sb.append(".");
    }
    return sb.toString();
  }

  public IPAddress next() {
    return new IPAddress(value + 1);
  }

  public IPAddress next(int count) {
    return new IPAddress(value + count);
  }

  public IPAddress prev() {
    return new IPAddress(value - 1);
  }

  public IPAddress prev(int count) {
    return new IPAddress(value - count);
  }

  public boolean gt(IPAddress ipAddress) {
    return this.value > ipAddress.getValue();
  }

  public boolean gte(IPAddress ipAddress) {
    return this.value >= ipAddress.getValue();
  }

  public boolean lt(IPAddress ipAddress) {
    return this.value < ipAddress.getValue();
  }

  public boolean lte(IPAddress ipAddress) {
    return this.value <= ipAddress.getValue();
  }


  public static int countBetween(IPAddress ipAddress1, IPAddress ipAddress2) {
    return Math.abs(ipAddress2.getValue() - ipAddress1.getValue());
  }

  public static int range(IPAddress ipAddress1, IPAddress ipAddress2) {
    return Math.abs(ipAddress2.getValue() - ipAddress1.getValue()) + 1;
  }

  public static IPAddress sub(IPAddress ipAddress1, IPAddress ipAddress2) {
    return new IPAddress(ipAddress2.getValue() - ipAddress1.getValue());
  }


  @Override
  public boolean equals(Object obj) {
    if (obj instanceof IPAddress) {
      return value == ((IPAddress) obj).value;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return value;
  }

  public int getValue() {
    return value;
  }

  private int getOctet(int i) {
    if (i < 0 || i >= 4) throw new IndexOutOfBoundsException();
    return (value >> (i * 8)) & 0x000000FF;
  }
}
