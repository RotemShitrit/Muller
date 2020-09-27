package com.kp.meganet.meganetkp;

import org.simpleframework.xml.Attribute;

/**
 * Created by alex on 11/22/2015.
 */
public class QryParams {
    public QryParams() {
        FirstOfDeviceName = "";
        ParameterID = 0;
        NDevice = 0;
        ParameterName = "";
        StringPosition = 0;
        StringLength = 0;
        StringType = "";
        ParameterType = "";
        CommandID = 0;
        CommandName = "";
        CommandString = "";
        ProgramCommand = "";
        TabID = 0;
        TabName = "";
        Control = "";
        AndrOption = "";
        Freq2 = "";
        StepOpt = "";
        ReadOnly = "";
    }

    public QryParams(
            String FirstOfDeviceName_prm,
            int ParameterID_prm,
            int NDevice_prm,
            String ParameterName_prm,
            int StringPosition_prm,
            int StringLength_prm,
            String StringType_prm,
            String ParameterType_prm,
            int CommandID_prm,
            String CommandName_prm,
            String CommandString_prm,
            String ProgramCommand_prm,
            int TabID_prm,
            String TabName_prm,
            String Control_prm,
            String AndrOption_prm,
            String Freq2_prm,
            String StepOpt_prm,
            String ReadOnly_prm
    )
    {
        FirstOfDeviceName = FirstOfDeviceName_prm;
        ParameterID = ParameterID_prm;
        NDevice = NDevice_prm;
        ParameterName = ParameterName_prm;
        StringPosition = StringPosition_prm;
        StringLength = StringLength_prm;
        StringType = StringType_prm;
        ParameterType = ParameterType_prm;
        CommandID = CommandID_prm;
        CommandName = CommandName_prm;
        CommandString = CommandString_prm;
        ProgramCommand = ProgramCommand_prm;
        TabID = TabID_prm;
        TabName = TabName_prm;
        Control = Control_prm;
        AndrOption = AndrOption_prm;
        Freq2 = Freq2_prm;
        StepOpt = StepOpt_prm;
        ReadOnly = ReadOnly_prm;
    }

    @Attribute
    public String FirstOfDeviceName = "";
    @Attribute
    public int ParameterID = 0;
    @Attribute
    public int NDevice = 0;
    @Attribute
    public String ParameterName = "";
    @Attribute
    public int StringPosition = 0;
    @Attribute
    public int StringLength = 0;
    @Attribute
    public String StringType = "";
    @Attribute
    public String ParameterType = "";
    @Attribute
    public int CommandID = 0;
    @Attribute
    public String CommandName = "";
    @Attribute
    public String CommandString = "";
    @Attribute
    public String ProgramCommand = "";
    @Attribute
    public int TabID = 0;
    @Attribute
    public String TabName = "";
    @Attribute
    public double MinValue = 0.0;
    @Attribute
    public double MaxValue = 0.0;
    @Attribute
    public double DefaultValue = 0.0;
    @Attribute
    public String Control = "";
    @Attribute
    public String AndrOption = "";
    @Attribute
    public String Freq2 = "";
    @Attribute
    public String StepOpt = "";
    @Attribute
    public String ReadOnly = "";

}

