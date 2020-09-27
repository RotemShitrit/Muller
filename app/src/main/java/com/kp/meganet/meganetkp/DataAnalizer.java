package com.kp.meganet.meganetkp;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 * Created by alex on 11/22/2015.
 */
public class DataAnalizer {

    public enum eParamType
    {
        PT_STR,
        PT_DOUBLE,
        PT_BOOL,
        PT_CHR,
        PT_BIT,
        PT_HEX,
        PT_OCT,
        PT_DEC,
        PT_ADDRESS,
        PT_SYSTEM,
        PT_PROTOCOL
    }

    public class cUnit {
        public cUnit(String prompt_prm) {

            systemStartPos = 0;
            systemLenght = 0;
            systemValue = 0;
            addressStartPos = 0;
            addressLengt = 0;
            addressValue = 0;
            grpStartPos = 0;
            grpLenght = 0;
            grpValue = 0;
            periodicStartPos = 0;
            periodicLenght = 0;
            periodicValue = 0;
            inputStartPos = 0;
            inputLenght = 0;
            inputValue = 0;
        }

        public int systemStartPos, systemLenght, systemValue;
        public int addressStartPos, addressLengt, addressValue;
        public int grpStartPos, grpLenght, grpValue;
        public int periodicStartPos, periodicLenght, periodicValue;
        public int inputStartPos, inputLenght, inputValue;
    }


    private cUnit _unit;
    private Map<String, String> _unitData;
    private volatile byte[] _readDataArr;
    DevDB _devObject  = null;
    private int _promptNameLen;

    public DataAnalizer() {

        _unitData = new HashMap<String, String>();
        InitNDevices("");
    }

    public boolean InitNDevices(String xmlLocation_prm)
    {
        /*
        DevDB dev = new DevDB(null);
        DbDevice[] deviceArr = new DbDevice[3];
        DbCommands[] commandArr = new DbCommands[4];
        DbParameters[] parameterArr = new DbParameters[5];


        for(int deviceIdx = 0; deviceIdx < deviceArr.length; deviceIdx++)
        {
            deviceArr[deviceIdx] = new DbDevice();
            deviceArr[deviceIdx].NDevice = deviceIdx;
            for(int commandIdx = 0; commandIdx < commandArr.length; commandIdx++)
            {
                commandArr[commandIdx] = new DbCommands();
                commandArr[commandIdx].CommandID = commandIdx;
                commandArr[commandIdx].NDevice = deviceIdx;
                for(int parameterIdx = 0; parameterIdx < parameterArr.length; parameterIdx++)
                {
                    parameterArr[parameterIdx] = new DbParameters();
                    parameterArr[parameterIdx].ParameterID = parameterIdx;
                    parameterArr[parameterIdx].CommandID = commandIdx;
                }
                commandArr[commandIdx].parameters = Arrays.asList(parameterArr);
            }
            deviceArr[deviceIdx].commands = Arrays.asList(commandArr);
        }
        dev.devices = Arrays.asList(deviceArr);

        try {
            xmlFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Serialize the Person

        try
        {
            Serializer serializer = new Persister();
            serializer.write(dev, xmlFile);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }*/


        // Create a second person object

       // ChangeBit((byte)12, 1, true);
        File xmlFile = new File(Environment.getExternalStorageDirectory().toString()+"/dev.xml");
        // Deserialize the Person
        if (xmlFile.exists())
        {
            try
            {
                Serializer serializer = new Persister();
                _devObject = serializer.read(DevDB.class, xmlFile);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        if(_devObject == null)
            return false;


        return true;
    }

    public DevDB GetDeviceData(int ndevice_prm, String version_prm)
    {
        DevDB device = new DevDB();

        return device;
    }

    public void SetPrompt(String promtp_prm) {
        _unit = new cUnit(promtp_prm);
        _unitData.clear();
        _unitData.put("Prompt", promtp_prm);
    }

    public Map<String, String> GetUnitData() {
        return _unitData;
    }


    public byte ChangeBit(byte value_prm, int position_prm, boolean bitValue_prm)
    {

        BigInteger b = new BigInteger(String.valueOf(value_prm));

        if(bitValue_prm)
            b = b.setBit(position_prm);
        else
            b = b.clearBit(position_prm);
        byte value = Byte.valueOf(b.toString());
        System.out.println(b.toString(2) + " " + value);

        return value;
    }

    public byte[] ApplyData(byte[] userDataParamArr, Map<String, QryParams> dataParams_prm, int promptNameLen_prm, boolean isPair_prm) {
        byte[] rawDataArr = userDataParamArr;

        int dataShift;
        if(isPair_prm)
            dataShift = 1 + promptNameLen_prm + 4;
        else
            dataShift = 1 + promptNameLen_prm;

        for(Map.Entry<String, QryParams> param : dataParams_prm.entrySet())
        {

            if(param.getValue().ParameterType.equals("CHR"))
            {

            }
            else if(param.getValue().ParameterType.equals("UNICODE"))
            {

            }
            else if(param.getValue().ParameterType.equals("MINUTES"))
            {

            }
            else if(param.getValue().ParameterType.equals("TIME"))
            {

            }
            else if(param.getValue().ParameterType.equals("DATE"))
            {

            }
            else if(param.getValue().ParameterType.equals("PASSWORD"))
            {

            }
            else if (param.getValue().ParameterType.contains("FREQUENCY"))
            {
                Integer v1, v2, v3;

                String[] separated = param.getValue().TabName.split(",");
                if(separated.length == 3)
                {
                    Double stepVal;


                    v1 = Integer.valueOf(separated[0]);
                    v2 = Integer.valueOf(separated[1]);
                    v3 = Integer.valueOf(separated[2]);

                    rawDataArr[dataShift + param.getValue().StringPosition + 0] = v1.byteValue();
                    rawDataArr[dataShift + param.getValue().StringPosition + 1] = v2.byteValue();
                    rawDataArr[dataShift + param.getValue().StringPosition + 2] = v3.byteValue();
                }
            }
            else if (param.getValue().ParameterType.contains("BIT"))
            {

                int openBrck, closeBrck;
                String brckStr, allBitsStr;
                int bitPos;

                String bitArrValue;

                openBrck = param.getValue().ParameterType.indexOf("(");
                closeBrck = param.getValue().ParameterType.indexOf(")");
                if((openBrck > 0 && closeBrck > 0) && (openBrck < closeBrck))
                {
                    brckStr = param.getValue().ParameterType.substring(openBrck+1, closeBrck);
                    allBitsStr = "";
                    /*
                    for(int i = 0; i < param.getValue().StringLength; i++)
                    {
                        allBitsStr += String.format("%8s", Integer.toBinaryString(data_prm[i] & 0xFF)).replace(' ', '0');
                    }
                    */

                    if(brckStr.length() > 2 && brckStr.contains(","))
                    {
                        /*
                        String[] separated = brckStr.split(",");
                        bitArrValue = "";
                        for(int i = 0; i < separated.length; i++)
                        {
                            bitArrValue += allBitsStr.substring(Integer.parseInt(separated[i])-1,Integer.parseInt(separated[i]));
                        }

                        //BitSet bits1 = fromString("1000001");
                        BitSet bits1 = fromString(bitArrValue);
                        byte[] b1 = bits1.toByteArray();
                        if(b1.length == 0)
                            param.getValue().TabName = "0";
                        for(int i = 0; i < b1.length; i++)
                        {
                            param.getValue().TabName += String.format("%02d", b1[i]);
                        }
                        */
                    }
                    else if(brckStr.length() > 0)
                    {
                        try
                        {
                            bitPos = Integer.parseInt(brckStr);
                            boolean bitOperation;
                            if(param.getValue().TabName.equals("1"))
                                bitOperation = true;
                            else
                                bitOperation = false;

                            rawDataArr[dataShift + param.getValue().StringPosition] = ChangeBit(rawDataArr[dataShift + param.getValue().StringPosition], bitPos-1, bitOperation);

                        }
                        catch(NumberFormatException nfe)
                        {
                            param.getValue().TabName = ""; // error type
                        }
                        // get bit in position
                    }
                    else
                    {
                        param.getValue().TabName = ""; // error type
                    }
                }

            }
            else
            {
                if(param.getValue().StringType.equals("Oct"))
                {

                }
                else if(param.getValue().StringType.equals("Hex"))
                {

                }
                else
                {
                /*
                Данные пользователя сначала конвертируются в hex , далее дополнить до нужного размера. т.е. если разме 3, а цифр 2
                то дополняются нулями. И каждую папу цифр (байт) присвоить элементу массива.

                1. param.TabName в hex.
                2. дополнение нулюми. Число должно быть размре * 2.
                3. Разбитие на пары (байты)
                4. Присвоение байтов элементам массива.
                 */

                    if(!Utilities.isNumeric(param.getValue().TabName))
                        param.getValue().TabName = "0";

                    // Getting hex value of parameter
                    String hexStr = Utilities.StringCompleter(Integer.toHexString(Integer.valueOf(param.getValue().TabName)), Integer.valueOf(param.getValue().StringLength) * 2, "0", true);

                    // Apply hex bytes to array elements

                    // 1. Взять массив с позиции
                    for(int i = 0; i < param.getValue().StringLength; i++)
                    {
                        if (param.getValue().ProgramCommand.equals("\\007"))
                            rawDataArr[dataShift + param.getValue().StringPosition + i] = (byte)Integer.parseInt(hexStr.substring(i*2, (i*2)+2), 16);
                    }
                }
            }
        }

        return rawDataArr;
    }

    public boolean isAck(byte[] data_prm) {
        return true;
    }

    private static BitSet fromString(final String s) {
        return BitSet.valueOf(new long[]{Long.parseLong(s, 2)});
    }

    private static String toString(BitSet bs) {
        return Long.toString(bs.toLongArray()[0], 2);
    }

    private QryParams DecodeDataArr(byte[] data_prm, QryParams param)// , int lenght_prm, String paramName_prm, String paramType_prm, String stringType_prm)
    {
        //String strRet = "";
        // param.StringLength, param.ParameterName, param.ParameterType, param.StringType

        if(param.ParameterType.equals("CHR"))
        {

        }
        else if(param.ParameterType.equals("UNICODE"))
        {

        }
        else if(param.ParameterType.equals("MINUTES"))
        {

        }
        else if(param.ParameterType.equals("TIME"))
        {

        }
        else if(param.ParameterType.equals("DATE"))
        {

        }
        else if(param.ParameterType.equals("PASSWORD"))
        {

        }
        else if (param.ParameterType.contains("FREQUENCY"))
        {
            Double stepValue, freqVal;
            boolean stepFlg;
            if(param.ParameterType.contains("6.25"))
            {
                stepValue = 6.25;
            }
            else
            {
                stepValue = 5.0;
            }

            Integer v1, v2, v3, vSum;

            v1 = ((int) data_prm[0] & 0xff);
            v2 = ((int) data_prm[1] & 0xff);
            v3 = ((int) data_prm[2] & 0xff);
            vSum = (((v2 * 256) + v3) * 64) + v1;
            freqVal = vSum.doubleValue() / (1000.0 / stepValue);
            param.TabName = v1.toString() + "," + v2.toString() + "," + v3.toString();

        }
        else if (param.ParameterType.contains("BIT"))
        {
            int openBrck, closeBrck;
            String brckStr, allBitsStr;
            int bitPos;

            String bitArrValue;

            openBrck = param.ParameterType.indexOf("(");
            closeBrck = param.ParameterType.indexOf(")");
            if((openBrck > 0 && closeBrck > 0) && (openBrck < closeBrck))
            {
                brckStr = param.ParameterType.substring(openBrck+1, closeBrck);
                allBitsStr = "";
                for(int i = 0; i < param.StringLength; i++)
                {
                    allBitsStr += String.format("%8s", Integer.toBinaryString(data_prm[i] & 0xFF)).replace(' ', '0');
                }

                if(brckStr.length() > 2 && brckStr.contains(","))
                {
                    String[] separated = brckStr.split(",");
                    bitArrValue = "";
                    for(int i = 0; i < separated.length; i++)
                    {
                        bitArrValue += allBitsStr.substring(Integer.parseInt(separated[i])-1,Integer.parseInt(separated[i]));
                    }

                    //BitSet bits1 = fromString("1000001");
                    BitSet bits1 = fromString(bitArrValue);
                    byte[] b1 = bits1.toByteArray();
                    if(b1.length == 0)
                        param.TabName = "0";
                    for(int i = 0; i < b1.length; i++)
                    {
                        param.TabName += String.format("%02d", b1[i]);
                    }
                }
                else if(brckStr.length() > 0)
                {
                    try
                    {
                        bitPos = Integer.parseInt(brckStr);

                        param.TabName = allBitsStr.substring(allBitsStr.length() - bitPos, allBitsStr.length() - bitPos +1 );
                    }
                    catch(NumberFormatException nfe)
                    {
                        param.TabName = ""; // error type
                    }
                    // get bit in position
                }
                else
                {
                    param.TabName = ""; // error type
                }
            }
        }
        else
        {
            if(param.StringType.equals("Oct"))
            {

            }
            else if(param.StringType.equals("Hex"))
            {

            }
            else
            {
                if(param.StringType.length() == 0)
                {
                    String hexValue = "";
                    int lVal;
                    for(int i = 0; i < param.StringLength; i++)
                    {
                        if(Integer.toHexString((int) data_prm[i] & 0xff).length() == 1)
                        {
                            hexValue += "0" + Integer.toHexString((int) data_prm[i] & 0xff);
                        }
                        else
                        {
                            hexValue += Integer.toHexString((int) data_prm[i] & 0xff);
                        }

                    }
                    lVal = Integer.parseInt(hexValue, 16);
                    param.TabName = Long.toString(lVal);
                }
            }
        }

        return param;
    }

    public Map<String, QryParams> AnalizeData(byte[] data_prm, String ndevice_prm, int promptNameLen_prm, boolean isPair_prm) {
        // type 0 - LARS, 1 = LARS1, 4 = LARS2

        Map<String, QryParams> paramsMap = new HashMap<String, QryParams>();
        QryParams tmpParam;
        int dataShift;
        if(isPair_prm)
            dataShift = 1 + promptNameLen_prm + 4;
        else
            dataShift = 1 + promptNameLen_prm;

        _promptNameLen = promptNameLen_prm;
        List<QryParams> parametersArr = new ArrayList<QryParams>();
        int ndeviceTmp = Integer.decode("0x" + ndevice_prm)-1;
        boolean simpleFlg = false;
        for(QryParams param : _devObject.parameters)
        {
            if(param.NDevice == ndeviceTmp)// && param.ParameterName.equals("Parameters"))
            {
                param.TabName = "";
                parametersArr.add(param);
            }
        }
        // Parameter array
        byte[] paramArr = new byte[256];
        String strTmp;
        // Обработка данных
        for(QryParams param : parametersArr)
        {
            // 1. Взять массив с позиции
            for(int i = 0; i < param.StringLength; i++)
            {
                paramArr[i] = data_prm[dataShift + param.StringPosition + i];
            }
            tmpParam = DecodeDataArr(paramArr, param);
            if(tmpParam != null)
                paramsMap.put(tmpParam.ParameterName, tmpParam);

        }
        for(Map.Entry<String, QryParams> itemFreq : paramsMap.entrySet())
        {
            if(itemFreq.getValue().ParameterType.contains("FREQUENCY"))
            {
                for(Map.Entry<String, QryParams> itemStep : paramsMap.entrySet())
                {
                    if(itemStep.getValue().ParameterName.contains("Step"))
                    {
                        if(itemStep.getValue().TabName.equals("0"))
                        {
                            itemFreq.getValue().StepOpt = "0";
                        }
                        else
                        {
                            itemFreq.getValue().StepOpt = "1";
                        }
                        //return paramsMap;
                    }
                }
            }
        }

        return paramsMap;

    }


    public static PairHeader DecodePairHeader(byte[] dataArr_prm)
    {
        // Check if is valid header

        return null;
    }

}
