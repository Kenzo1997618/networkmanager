package STKSoftwareController;

import java.net.*;
import java.io.*;
import java.util.*;

//����STK��������StkCon
public class StkCon {
    //�������        
    private Socket         socket         = null;
    private String         host           = "0.0.0.0";
    private int            port           = 5001;
    private PrintWriter    toStk          = null;
    private BufferedReader fromStk        = null;
    private Hashtable      returnDataHash = null;
    private boolean        ackOn          = false;
    private StringBuffer   retStringBuffer;
    private boolean returnedAck;

    //���巽��

    //����Java���캯����ʼ������
    //����ʹ�õ���һ��˽�к���initVariables�����Ļ����˵��
    public StkCon(){
        host="0.0.0.0";
        port=5001;
        initVariables();
    }

    public StkCon(String h, int p) {
        host = h;
        port = p;
        initVariables();
    }

    public StkCon(String connectionInfo) {
        StringTokenizer st=new StringTokenizer(connectionInfo,":");
        try{
            host=st.nextToken();
            port=new Integer(st.nextToken()).intValue();
        }catch(java.util.NoSuchElementException e){
            System.err.println("You did something wrong with the formatting, I'm switching back to the defaults");
            host="0.0.0.0";
            port=5001;
        }catch(java.lang.NullPointerException e){
            System.err.println("You did something wrong with the formatting, I'm switching back to the defaults");
            host="0.0.0.0";
            port=5001;
        }
        initVariables();
    }    

    //initVariables������ʼ���������ݵĽṹ�ͻ����� 
    private void initVariables(){   
        retStringBuffer=new StringBuffer();
        returnDataHash = new Hashtable();
        returnDataHash.put("3DGETVIEWPOINT",     "0");
        returnDataHash.put("ACCESS",             "0");
        returnDataHash.put("AER",                "0");
        returnDataHash.put("ALLACCESS",          "1"); 
        returnDataHash.put("ALLINSTANCENAMES",   "0");
        returnDataHash.put("ANIMFRAMERATE",      "0");
        returnDataHash.put("CHAINALLACCESS",     "1"); 
        returnDataHash.put("CHAINGETACCESSES",   "1"); 
        returnDataHash.put("CHAINGETINTERVALS",  "1"); 
        returnDataHash.put("CHAINGETSTRANDS",    "1"); 
        returnDataHash.put("CHECKSCENARIO",      "0");
        returnDataHash.put("CLOSEAPPROACH",      "1"); 
        returnDataHash.put("CONVERTDATE",        "0");
        returnDataHash.put("DISQUERY",           "0");
        returnDataHash.put("DOESOBJEXIST",       "0");
        returnDataHash.put("GETACCESSES",        "1"); 
        returnDataHash.put("GETANIMTIME",        "0");
        returnDataHash.put("GETATTITUDETARG",    "0");
        returnDataHash.put("GETCONVERSION",      "0");
        returnDataHash.put("GETDB",              "0");
        returnDataHash.put("GETDEFAULTDIR",      "0");
        returnDataHash.put("GETDESCRIPTION",     "0");
        returnDataHash.put("GETDSPFLAG",         "0");
        returnDataHash.put("GETDSPINTERVALS",    "0");
        returnDataHash.put("GETEPOCH",           "0");
        returnDataHash.put("GETNUMNOTES",        "0");
        returnDataHash.put("GETREPORT",          "1"); 
        returnDataHash.put("GETRPTSUMMARY",      "1"); 
        returnDataHash.put("GETSCENPATH",        "0");
        returnDataHash.put("GETSTKHOMEDIR",      "0");
        returnDataHash.put("GETSTKVERSION",      "0");
        returnDataHash.put("GETTIMEPERIOD",      "0");
        returnDataHash.put("GRIDINSPECTOR",      "0");
        returnDataHash.put("LIFETIME",           "0");
        returnDataHash.put("LISTSUBOBJECTS",     "0");
        returnDataHash.put("POSITION",           "0");
        returnDataHash.put("SENSORQUERY",        "0");
        returnDataHash.put("SHOWUNITS",          "0");
        returnDataHash.put("STOPWATCHGET",       "0");
        returnedAck=false;
    }

    //���Ӻ���
    //���ݳ�ʼ�������Ӳ���������Java��STK֮������ӹ�ϵ    
    public int connect() {
        if (socket == null)
        {
            try
            {
                socket = new Socket(host, port);
                toStk = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                fromStk = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            }
            catch (Exception e)
            {
                socket = null;
                toStk = null;
                fromStk = null;
                System.out.println("Error connecting to " + host + ":" + port + " -> "  + e);
                return -1;
            }
            
            sendConCommand("Concontrol", "/", "AckOn AsyncOff ErrorOn VerboseOff");
            return 0;
        }
        else
        {
            System.out.println("Already connected to " + host + ":" + port);
            return 0;
        }
    }
    
    //�Ͽ����Ӻ���
    public void disconnect() {
        if (socket != null)
        {
            toStk.println("Concontrol / disconnect");
            toStk.flush();
            try
            {
                toStk.close();
                fromStk.close();
                socket.close();
            }
            catch (IOException ioe) {}
            socket = null;
            toStk = null;
            fromStk = null;
        }
    }
    
    //�����������
    //Jave���ݲ�������STK���Ϳ�������
    public String sendConCommand(String cmd, String objectPath, String cmdData)
    {
        boolean foundAck;
        String buffer;
        StringTokenizer ackTestst;
        retStringBuffer.setLength(0);
        if ((socket != null) && (toStk != null) && (fromStk != null))
        {
            String command = cmd.toUpperCase();
            if(cmd.equalsIgnoreCase("CONCONTROL")){
                foundAck=false;
                ackTestst=new StringTokenizer(cmdData);
                while(!foundAck && ackTestst.hasMoreTokens()){
                    buffer=ackTestst.nextToken();
                    if(buffer!=null){
                        if(buffer.equalsIgnoreCase("ACKON")){
                            ackOn=true;
                            foundAck=true;
                        }
                        else if(buffer.equalsIgnoreCase("ACKOFF")){
                            ackOn=false;
                            foundAck=true;
                        }
                    }
                }
            }
            toStk.println(cmd + " " + objectPath + " " + cmdData);
            toStk.flush();
            
            if (ackOn){
                buffer=readAck();
                if(buffer!=null){
                    if(buffer.equalsIgnoreCase("ACK  ")){
                        returnedAck=true;
                    }
                    else{
                        returnedAck=false;
                    }
                }
                else{
                    returnedAck=false;
                }
            }
            else{
                returnedAck=false;
            }
            if ((returnDataHash.containsKey(command)&&ackOn&&returnedAck) || (returnDataHash.containsKey(command)&&!ackOn)){
                int multi = 0;
                try 
                {
                    multi = Integer.parseInt((String)returnDataHash.get(command));
                }
                catch (NumberFormatException nfe) {}
                int hdVal = read40ByteHeader();
                if (multi == 0)
                {
                   retStringBuffer.append(readNBytes(hdVal));
                }
                else 
                {
                    String multiHeader = readNBytes(hdVal);
                    multiHeader = multiHeader.replace('\n', ' ');
                    StringTokenizer multiTok = new StringTokenizer(multiHeader, " ");
                    int  numRecs = 0;
                    try
                    {
                        numRecs = Integer.parseInt(multiTok.nextToken());
                    }
                    catch (NumberFormatException nfe)
                    {
                        System.out.println("Error reading header info: " + nfe);
                    }
                    int numBytes = 0;
                    for (int i=0; i<numRecs; i++)
                    {
                       numBytes = read40ByteHeader();
                       retStringBuffer.append(readNBytes(numBytes));
                       retStringBuffer.append('\n');
                    }
                }
            }
            return retStringBuffer.toString();
        }
        else
        {
            return null;
        }
    }

    public String sendConCommand(String inputCommand)
    {
        StringTokenizer st;
        boolean blankLine;
        String cmd,cmdData,buffer,command;
        boolean foundAck;
        retStringBuffer.setLength(0);
        if ((socket != null) && (toStk != null) && (fromStk != null)){
            st=new StringTokenizer(inputCommand);
            if(st.hasMoreTokens()){
                cmd=st.nextToken();
                blankLine=false;
            }
            else{
                cmd=" ";
                blankLine=true;
            }
            cmdData="";
            if(cmd.equalsIgnoreCase("CONCONTROL")){
                foundAck=false;
                while(st.hasMoreTokens()){
                    buffer=st.nextToken();
                    if(buffer!=null){
                        cmdData+=buffer+" ";
                        if(!foundAck){
                            if(buffer.equalsIgnoreCase("ACKON")){
                                ackOn=true;
                                foundAck=true;
                            }
                            else if(buffer.equalsIgnoreCase("ACKOFF")){
                                ackOn=false;
                                foundAck=true;
                            }
                        }
                    }
                }
            }
            else{
                while(st.hasMoreTokens()){
                    cmdData+=st.nextToken()+" ";
                }
            }
            if(!blankLine){
                command = cmd+" "+cmdData;
                toStk.println(command);
                toStk.flush();
            }
            if (ackOn&&!blankLine){
                buffer=readAck();
                if(buffer!=null){
                    if(buffer.equalsIgnoreCase("ACK  ")){
                        returnedAck=true;
                    }
                    else{
                        returnedAck=false;
                    }
                }
                else{
                    returnedAck=false;
                }
            }
            else{
                returnedAck=false;
            }
            if ((returnDataHash.containsKey(cmd.toUpperCase())&&ackOn&&returnedAck&&!blankLine) || (returnDataHash.containsKey(cmd.toUpperCase())&&!ackOn&&!blankLine)){
                int multi = 0;
                try 
                {
                    multi = Integer.parseInt((String)returnDataHash.get(cmd.toUpperCase()));
                }
                catch (NumberFormatException nfe) {}
                int hdVal = read40ByteHeader();
                if (multi == 0)
                {
                   retStringBuffer.append(readNBytes(hdVal));
                }
                else 
                {
                    String multiHeader = readNBytes(hdVal);
                    multiHeader = multiHeader.replace('\n', ' ');
                    StringTokenizer multiTok = new StringTokenizer(multiHeader, " ");
                    int  numRecs = 0;
                    try
                    {
                        numRecs = Integer.parseInt(multiTok.nextToken());
                    }
                    catch (NumberFormatException nfe)
                    {
                        System.out.println("Error reading header info: " + nfe);
                    }
                    int numBytes = 0;
                    for (int i=0; i<numRecs; i++)
                    {
                       numBytes = read40ByteHeader();
                       retStringBuffer.append(readNBytes(numBytes));
                       retStringBuffer.append('\n');
                    }
                }
            }
            return retStringBuffer.toString();
        }
        else
        {
            return null;
        }
    }
    
    //��ȡSTK�ķ�����Ϣ
    private String readAck() {
        char[] buffer = new char[4];
        String retString;
        int numRead = 0;
        try 
        {
            numRead = fromStk.read(buffer, 0, 3);
        } 
        catch (IOException ioe)
        {
            System.out.println("Error reading from Stk socket:  " + ioe);
        }
        if (numRead < 0)
        {
            System.out.println("Error reading Stk output.");
        }
        if (buffer[0] == 'A')
        {
            retString = new String("ACK  ");
        }
        else
        {
            try
            {
                fromStk.read(buffer, 0, 1);
            } 
            catch (IOException ioe)
            {
                System.out.println("Error reading from Stk socket:  " + ioe);
            }
            retString = new String("NACK ");
        }
        return retString;
    }

    //��ȡ�������ݵ��ļ�ͷ
    private int read40ByteHeader() {
        char[] header = new char[40];
        try
        {
            fromStk.read(header, 0, 40);
        } 
        catch (IOException ioe)
        {
            System.out.println("Error reading from Stk socket:  " + ioe);
        }
        String headerStr = (new String(header)).replace('\n', ' ');
        StringTokenizer stok = new StringTokenizer(headerStr, " ");
        stok.nextToken();
        int headerVal = 0;
        try
        {
            headerVal = Integer.parseInt(stok.nextToken());
        }
        catch (NumberFormatException nfe)
        {
            System.out.println("Error reading header info: " + nfe);
        }
        return headerVal;
    }

    //��ȡ��������
    private String readNBytes(int numBytes) {
        char[] buffer = new char[numBytes];
        try
        {
            fromStk.read(buffer, 0, numBytes);
        } 
        catch (IOException ioe)
        {
            System.out.println("Error reading from Stk socket:  " + ioe);
        }

        return (new String(buffer));
    }
    
    //��ȡSTK����״̬
    public boolean getAckStatus(){
        return(returnedAck);
    }
}
