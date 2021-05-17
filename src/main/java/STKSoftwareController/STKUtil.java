package STKSoftwareController;

import org.junit.Test;

/**
 * STK工具类
 *
 * @author cinhori
 * @version 1.0
 * @since 2017-10-04 10:23
 */
public class STKUtil {
    private static StkCon myConnection;

    static {
        int status;
        if(myConnection == null){
            myConnection = new StkCon("192.168.1.15", 5001);
        }
        status = myConnection.connect();
        if (status < 0){
            System.out.println("Error connecting.");
        }
    }

    /**
     * 开始播放至结束
     */
    public static void start(){
        myConnection.sendConCommand("Animate * Start End");
    }

    /**
     * 暂停
     */
    public static void pause(){
        myConnection.sendConCommand("Animate * Pause");
    }

    /**
     * 重置
     */
    public static void reset(){
        myConnection.sendConCommand("Animate * Reset");
    }

    /**
     * 加速
     */
    public static void speedUp(){
        myConnection.sendConCommand("Animate * Faster");
    }

    /**
     * 减速
     */
    public static void speedDown(){
        myConnection.sendConCommand("Animate * Slower");
    }

    /**
     * 添加链路
     * @param fromObj 链路起始节点，如Geo1
     * @param toObj 链路结束节点，如Leo2
     * @param lineColor 链路颜色
     * @param lineWidth 链路线条宽
     */
    public static void addLine(String fromObj, String toObj, String lineColor, int lineWidth){
        String fromObjType = getType(fromObj);
        String toObjType = getType(toObj);
        String addLineCommand = "VO * ObjectLine Add FromObj " + fromObjType + "/" + fromObj
                + " ToObj " + toObjType + "/" + toObj
                + " AddIntervals 1 \"1 Jul 1970 12:35:00.00\" \"1 Jul 2099 14:55:00.00\" Color " + lineColor
                + " LineWidth "
                + lineWidth;
        System.out.println(addLineCommand);
        myConnection.sendConCommand(addLineCommand);
        takeEffect(fromObj, toObj);
    }

    public static void addLine(String fromObj, String toObj){
        addLine(fromObj, toObj, "red", 3);
    }

    /**
     * 删除链路
     * @param fromObj
     * @param toObj
     */
    public static void deleteLine(String fromObj, String toObj){
        String fromObjType = getType(fromObj);
        String toObjType = getType(toObj);
        String deleteLineCommand = "VO * ObjectLine Delete FromObj " + fromObjType + "/"
                + fromObj +  " ToObj " + toObjType + "/" + toObj;
        myConnection.sendConCommand(deleteLineCommand);
    }

    /**
     * 使链路生效
     * @param fromObj
     * @param toObj
     */
    private static void takeEffect(String fromObj, String toObj){
        String fromObjType = getType(fromObj);
        String toObjType = getType(toObj);
        String command = "VO * ObjectLine Modify FromObj " + fromObjType + "/"
                + fromObj + " ToObj " + toObjType + "/"
                + toObj + " IntervalType UseIntervals";
        myConnection.sendConCommand(command);
    }

    private static String getType(String fromObj){
        String type = null;
        if(fromObj.startsWith("GEO") || fromObj.startsWith("LEO")) type = "Satellite";
        else if(fromObj.startsWith("Facility")) type = "Facility";
        else if(fromObj.startsWith("Ground")) type = "GroundVehicle";
        return type;
    }

    public static void deleteLineAll(){
        String command = "VO * ObjectLine DeleteAll";
        myConnection.sendConCommand(command);
    }
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("开始播放");
        STKUtil.reset();
        STKUtil.start();
        Thread.sleep(3000);
        System.out.println("添加链路");
        STKUtil.addLine("Geo2", "Leo1", "red", 3);
        Thread.sleep(3000);
        System.out.println("删除链路");
        STKUtil.deleteLine("Geo2", "Leo1");
        Thread.sleep(3000);
        System.out.println("暂停");
        STKUtil.pause();
        Thread.sleep(2000);
        System.out.println("开始播放");
        STKUtil.start();
        Thread.sleep(2000);

        System.out.println("添加链路");
        STKUtil.addLine("Geo2", "Facility1", "red", 3);
        Thread.sleep(2000);
        STKUtil.deleteLine("Geo2", "Facility1");

        //STKUtil.addLine("Facility1", "GroundVehicle1", "red", 3);
        //Thread.sleep(2000);
        //STKUtil.deleteLine("Geo2", "GroundVehicle1");


        //STKUtil.reset();
    }
    
    @Test
    public void dem02(){
//    	STKUtil.deleteLine("GEO11", "GEO12");
//    	STKUtil.deleteLine("GEO11", "GEO13");
//    	STKUtil.deleteLine("GEO12", "GEO13");
//    	STKUtil.deleteLine("GEO11", "Facility1");
//    	STKUtil.deleteLine("GEO13", "Facility2");
//    	STKUtil.addLine("GEO11","GEO13");
    	deleteLineAll();
////    	start();
//    	reset();
    }
}
