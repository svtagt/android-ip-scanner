package com.example.ipscan.lib.helpers;

public class Host {

  private final int value;

  public Host(String stringValue) {
    String[] parts = stringValue.split("\\.");
    if (!isValid(stringValue)) {
      throw new IllegalArgumentException();
    }
    value =
      (Integer.parseInt(parts[0], 10) << (8 * 3)) & 0xFF000000 |
        (Integer.parseInt(parts[1], 10) << (8 * 2)) & 0x00FF0000 |
        (Integer.parseInt(parts[2], 10) << (8 * 1)) & 0x0000FF00 |
        (Integer.parseInt(parts[3], 10) << (8 * 0)) & 0x000000FF;
  }

  public Host(int value) {
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

  public Host next() {
    return new Host(value + 1);
  }

  public Host next(int count) {
    return new Host(value + count);
  }

  public Host prev() {
    return new Host(value - 1);
  }

  public Host prev(int count) {
    return new Host(value - count);
  }

  public boolean gt(Host host) {
    return this.value > host.getValue();
  }

  public boolean gte(Host host) {
    return this.value >= host.getValue();
  }

  public boolean lt(Host host) {
    return this.value < host.getValue();
  }

  public boolean lte(Host host) {
    return this.value <= host.getValue();
  }


  public static int countBetween(Host host1, Host host2) {
    return Math.abs(host2.getValue() - host1.getValue());
  }

  public static int range(Host host1, Host host2) {
    return Math.abs(host2.getValue() - host1.getValue()) + 1;
  }

  public static Host sub(Host host1, Host host2) {
    return new Host(host2.getValue() - host1.getValue());
  }


  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Host) {
      return value == ((Host) obj).value;
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

  public static boolean isValid(String host) {
    String[] parts = host.split("\\.");

    if (parts.length != 4) {
      return false;
    }

    for (int i = 0; i < parts.length; i++) {
      int val = Integer.parseInt(parts[i]);
      if (val < 0 || val > 255) {
        return false;
      }
    }

    return true;
  }

  public static boolean isRange(String hostRange) {
    String[] parts = hostRange.split("-");
    return hostRange.contains("-") && isValid(parts[0]) && isValid(parts[1]);
  }


} 
