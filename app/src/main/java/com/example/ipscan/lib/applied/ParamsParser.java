package com.example.ipscan.lib.applied;

import com.example.ipscan.lib.helpers.CIDR;
import com.example.ipscan.lib.helpers.Host;
import com.example.ipscan.lib.helpers.PortRange;

import java.net.UnknownHostException;
import java.util.ArrayList;

public class ParamsParser {
  private static final String PORTS_ARG = "-p";
  private static final String HOSTS_ARG = "-h";
  private static final String SEPARATOR = ",";

  public static final byte TYPE_PORTS = 1;
  public static final byte TYPE_HOSTS = 2;

  public static String getArgVal(String paramsStr, int type) {

    String targetArg = PORTS_ARG;
    String oppositeArg = HOSTS_ARG;

    if (type == TYPE_HOSTS) {
      targetArg = HOSTS_ARG;
      oppositeArg = PORTS_ARG;
    }

    int pIndex = paramsStr.indexOf(targetArg);
    if (pIndex == -1) {
      return "";
    }
    int portsSubsBeginIndex = pIndex + targetArg.length();

    String portsStr = paramsStr.substring(portsSubsBeginIndex).trim();
    if (portsStr.contains(oppositeArg)) {
      int hIndex = portsStr.indexOf(oppositeArg);
      portsStr = portsStr.substring(0, hIndex).trim();
    }

    return portsStr;
  }

  public static boolean[] extractPorts(String paramsStr) {
    boolean[] resultBoolArr = new boolean[65536];
    String portsStr = getArgVal(paramsStr, TYPE_PORTS);
    String[] portsArray = portsStr.split(SEPARATOR);

    for (int i = 0; i < portsArray.length; i++) {
      if (!portsArray[i].contains("-")) {
        //this it is single port
        resultBoolArr[Integer.valueOf(portsArray[i])] = true;
      } else {
        //if it is range
        String[] rangePortsArr = portsArray[i].split("-");
        int rangePortFrom = Integer.valueOf(rangePortsArr[0]);
        int rangePortTo = Integer.valueOf(rangePortsArr[1]);

        for (int j = rangePortFrom; j <= rangePortTo; j++) {
          resultBoolArr[j] = true;
        }
      }
    }
    return resultBoolArr;
  }

  public static String extractHosts(String paramsStr) {
    return getArgVal(paramsStr, TYPE_HOSTS);
  }

  public static ArrayList<PortRange> makePortRangesList(boolean[] portsArr) {
    ArrayList<PortRange> resultList = new ArrayList<>();

    int portFrom = -1;
    boolean rangeBeginFound = false;
    for (int i = 1; i < portsArr.length; i++) {
      if (!rangeBeginFound && portsArr[i]) {
        portFrom = i;
        rangeBeginFound = true;
      }

      if (rangeBeginFound && !portsArr[i]) {
        resultList.add(new PortRange(portFrom, i - 1));
        rangeBeginFound = false;
      }
    }

    return resultList;
  }

  public static ArrayList<Host> makeHostsList(String hostsStr) {
    ArrayList<Host> resultList = new ArrayList<>();
    String[] hostsArray = hostsStr.split(SEPARATOR);

    for (int i = 0; i < hostsArray.length; i++) {
      if (Host.isRange(hostsArray[i])) {
        //it is range
        String[] rangeHostsArr = hostsArray[i].split("-");
        Host rangeHostFrom = new Host(rangeHostsArr[0]);
        Host rangeHostTo = new Host(rangeHostsArr[1]);
        for (Host host = rangeHostFrom; host.lte(rangeHostTo); host = host.next()) {
          resultList.add(host);
        }
        continue;
      }

      if (hostsArray[i].contains("/")) {
        //it is CIDR format
        try {
          CIDR cidr = new CIDR(hostsArray[i]);
          Host cidrHostFrom = cidr.getHostFrom();
          Host cidrHostTo = cidr.getHostTo();

//          Log.e(Const.LOG_TAG,
//            "CIDR: " + hostsArray[i] +
//              " cidrHostFrom: " + cidrHostFrom.toString() +
//              " cidrHostTo: " + cidrHostTo.toString() +
//              " count: " + Host.countBetween(cidrHostFrom, cidrHostTo));

          for (Host host = cidrHostFrom; host.lte(cidrHostTo); host = host.next()) {
            resultList.add(host);
          }
        } catch (UnknownHostException e) {
          e.printStackTrace();
        }
        continue;
      }


      if (Host.isValid(hostsArray[i])) {
        resultList.add(new Host(hostsArray[i]));
        continue;
      }
    }
    //TODO
    return resultList;
  }
}
