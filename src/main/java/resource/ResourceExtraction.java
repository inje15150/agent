package resource;

import com.sun.management.OperatingSystemMXBean;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResourceExtraction {

    OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    String[] cmd = {"sh", "-c", "ps aux |awk '{print $11,$2,$4}'"};
    String[] osCmd = {"sh", "-c", "cat /etc/*-release |uniq |grep PRETTY_NAME"};
    String[] linuxInterfaceNameCmd = {"sh", "-c", "ip -br -4 a |grep UP |awk '{print $1}'"};
    String windowNetstatCmd = "netstat -rn";
    String[] linuxNetstatCmd = {"sh", "-c", "netstat -rn |grep UG |awk '{print $2}'"};

    /** OS info */
    public String osInfo() throws IOException {
        String osInfo = osBean.getName();

        if (osInfo.contains("Linux")) {
            Process process = Runtime.getRuntime().exec(osCmd);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String data = null;
            while ((data = reader.readLine()) != null) {
                String[] split = data.split("=");
                osInfo = split[1].replace("\"", "");
            }
        }
        return osInfo;
    }

    /** hostname */
    public String getHostName() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostName();
    }

    /** ip */
    public String getIp() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }

    /** cpu */
    public float getCpu() {
        double systemCpuLoad = osBean.getSystemCpuLoad();
        float floatSystemCpuLoad = (float) (systemCpuLoad * 100);

        return (float) Math.round(floatSystemCpuLoad * 100) / 100;
    }

    /** memory */
    public float getMem() {
        float totalPhysicalMemorySize = (float) osBean.getTotalPhysicalMemorySize();
        float freePhysicalMemorySize = (float) osBean.getFreePhysicalMemorySize();
        float usedMemSize = totalPhysicalMemorySize - freePhysicalMemorySize;

        float result = (usedMemSize / totalPhysicalMemorySize) * 100;

        return (float) Math.round(result * 100) / 100;
    }

    /** disk */
    public float getDisk() {
        File file = new File("/");
        float totalSpace = (float) file.getTotalSpace();
        float freeSpace = (float) file.getFreeSpace();
        float usedSpace = totalSpace - freeSpace;

        return (float) Math.round(((usedSpace / totalSpace) * 100) * 100) / 100;
    }

    /** process info */
    public List<ProcessInfo> getProcess() {

        List<ProcessInfo> processInfos = null;
        try {
            if (osInfo().contains("Windows")) {
                Process process = Runtime.getRuntime().exec("tasklist /fo csv");
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                processInfos = new ArrayList<>();

                String data;
                String replaceData;
                while ((data = reader.readLine()) != null) {
                    ProcessInfo processInfo = new ProcessInfo();

                    replaceData = data.replace("\",\"", ",");
                    replaceData = replaceData.replace("\"", "");

                    String[] split = replaceData.split(",");

                    processInfo.setName(split[0]);
                    processInfo.setPid(split[1]);
                    processInfo.setUsedMem(split[4] + "M");

                    processInfos.add(processInfo);
                }
                processInfos.remove(0);
                return processInfos;
            }
            if (osInfo().contains("Linux")) {
                Process process = Runtime.getRuntime().exec(cmd);
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                processInfos = new ArrayList<>();

                String data;
                String replaceData;
                while ((data = reader.readLine()) != null) {
                    ProcessInfo processInfo = new ProcessInfo();

                    replaceData = data.replace(" ", ",");

                    String[] split = replaceData.split(",");

                    processInfo.setName(split[0]);
                    processInfo.setPid(split[1]);
                    processInfo.setUsedMem(split[2]);

                    processInfos.add(processInfo);
                }
                processInfos.remove(0);
                return processInfos;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** 게이트웨이 주소 */
    public String getGateway() throws IOException {
        String gateWay = null;
        if (osInfo().contains("Windows")) {
            gateWay = getWindowGatewayCmd(Runtime.getRuntime().exec(windowNetstatCmd));
        } else if (osInfo().contains("Linux")) {
            gateWay = getLinuxGatewayCmd(Runtime.getRuntime().exec(linuxNetstatCmd));
        }
        return gateWay;
    }

    /** window 게이트웨이 주소 추출 */
    public String getWindowGatewayCmd(Process process) {
        String _255 = "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
        String exlP = "(?:" + _255 + "\\.){3}" + _255;

        Pattern pattern = Pattern.compile("^\\s*(?:0\\.0\\.0\\.0\\s*){1,2}(" + exlP + ").*"); // 정규식으로 패턴 생성
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    return matcher.group(1);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** 리눅스 gateway 주소 추출 */
    public String getLinuxGatewayCmd(Process process) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String result = "";
            String line;
            while ((line = reader.readLine()) != null) {
                result = line;
            }
            reader.close();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** 인터페이스 이름 */
    public String getInterface() throws IOException {
        String interfaceName = "";
        if (osInfo().contains("Windows")) {
            try {
                InetAddress ip = InetAddress.getLocalHost();
                NetworkInterface networkInterface = NetworkInterface.getByInetAddress(ip);
                interfaceName = networkInterface.getName();
                return interfaceName;

            } catch (UnknownHostException | SocketException e) {
                e.printStackTrace();
            }
        } else if (osInfo().contains("Linux")) {
            Process process = Runtime.getRuntime().exec(linuxInterfaceNameCmd);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            interfaceName = reader.readLine();
            reader.close();
            return interfaceName;
        }
        return null;
    }

    /**
     * ip 주소 추출
     */
    public String getIpConfig() throws IOException {
        if (osInfo().contains("Windows")) {
            return getIp();
        } else if (osInfo().contains("Linux")) {
            return linuxIfConfigCmd();
        }
        return null;
    }

    public String linuxIfConfigCmd() {
        String result = "";
        try {
            String getInterface = getInterface();
            String[] linuxIfConfig = {"sh", "-c", "ifconfig " + getInterface + " |grep 'inet ' |awk '{print $2}'"};
            Process process = Runtime.getRuntime().exec(linuxIfConfig);

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line = null;
            while ((line = reader.readLine()) != null) {
                result += line;
            }
            reader.close();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** mac 주소 추출 */
    public String getMacAddress() {
        String macAddress;
        try {
            if (osInfo().contains("Windows")) {
                macAddress = getWinMacAddress();
                return macAddress;
            } else if (osInfo().contains("Linux")) {
                macAddress = getLinuxMacAddress();
                return macAddress;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 윈도우 MAC 주소 얻어오기
    public String getWinMacAddress() {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(ip);

            if (ip != null) {
                byte[] mc = networkInterface.getHardwareAddress();

                String macAddress = "";
                for (byte b : mc) {
                    macAddress += (String.format("%02X", b) + ":"); // %02 : 두자리 헥사, X : 대문자
                }
                return (macAddress.substring(0, macAddress.length() - 1));
            }
        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 리눅스 MAC 주소 추출
    public String getLinuxMacAddress() {
        String result = "";

        try {
            String getInterface = getInterface();
            String[] linuxMacAddress = {"sh", "-c", "ifconfig " + getInterface + " |grep ether |awk '{print $2}'"};
            Process process = Runtime.getRuntime().exec(linuxMacAddress);

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line = null;
            while ((line = reader.readLine()) != null) {
                result += line;
            }
            reader.close();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /** event date */
    public String getEventDate() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }

    /** jvm total memory */
    public float jvmTotalMemory() {

        return (float) (Runtime.getRuntime().totalMemory() / 1024 / 1024);
    }

    /** jvm used memory */
    public float jvmUsedMemory() {
        float totalMemory = (float) Runtime.getRuntime().totalMemory() / 1024 / 1024;
        float freeMemory = (float) Runtime.getRuntime().freeMemory() / 1024 / 1024;
        return (totalMemory - freeMemory);
    }

    /**
     * jvm free memory
     */
    public float jvmFreeMemory() {
        return (float) (Runtime.getRuntime().freeMemory() / 1024 / 1024);
    }
}
