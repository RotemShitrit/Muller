package com.kp.meganet.meganetkp;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * Created by alex on 11/22/2015.
 */
public class DevDB {
    public DevDB(){parameters = new ArrayList<QryParams>();}

    public DevDB(List<QryParams> parameters_prm)
    {
        parameters = parameters_prm;
    }
    @ElementList
    public List<QryParams> parameters;
}
