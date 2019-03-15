package com.example.ipscan.lib.helpers;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class CIDR {

  private Host hostFrom;
  private Host hostTo;

  public CIDR(String cidr) throws UnknownHostException {
    if (cidr.contains("/")) {
      int index = cidr.indexOf("/");
      String addressPart = cidr.substring(0, index);
      String networkPart = cidr.substring(index + 1);

      InetAddress inetAddress = InetAddress.getByName(addressPart);
      int prefixLength = Integer.parseInt(networkPart);

      ByteBuffer maskBuffer = ByteBuffer.allocate(4).putInt(-1);
      int targetSize = 4;

      BigInteger mask = (new BigInteger(1, maskBuffer.array())).not().shiftRight(prefixLength);

      ByteBuffer buffer = ByteBuffer.wrap(inetAddress.getAddress());
      BigInteger ipVal = new BigInteger(1, buffer.array());

      BigInteger startIp = ipVal.and(mask);
      BigInteger endIp = startIp.add(mask.not());

      byte[] startIpArr = toBytes(startIp.toByteArray(), targetSize);
      byte[] endIpArr = toBytes(endIp.toByteArray(), targetSize);

      InetAddress startAddress = InetAddress.getByAddress(startIpArr);
      InetAddress endAddress = InetAddress.getByAddress(endIpArr);

      hostFrom = new Host(startAddress.getHostAddress());
      hostTo = new Host(endAddress.getHostAddress());

    } else {
      throw new IllegalArgumentException("not an valid CIDR format!");
    }
  }

  public Host getHostFrom() {
    return hostFrom;
  }

  public void setHostFrom(Host hostFrom) {
    this.hostFrom = hostFrom;
  }

  public Host getHostTo() {
    return hostTo;
  }

  public void setHostTo(Host hostTo) {
    this.hostTo = hostTo;
  }

  private byte[] toBytes(byte[] array, int targetSize) {
    int counter = 0;
    List<Byte> newArr = new ArrayList<Byte>();
    while (counter < targetSize && (array.length - 1 - counter >= 0)) {
      newArr.add(0, array[array.length - 1 - counter]);
      counter++;
    }

    int size = newArr.size();
    for (int i = 0; i < (targetSize - size); i++) {

      newArr.add(0, (byte) 0);
    }

    byte[] ret = new byte[newArr.size()];
    for (int i = 0; i < newArr.size(); i++) {
      ret[i] = newArr.get(i);
    }
    return ret;
  }


}
