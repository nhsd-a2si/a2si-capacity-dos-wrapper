package com.nhsd.a2si.endpoint.doswrapper;

import org.apache.cxf.binding.soap.SoapFault;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.phase.Phase;

import java.util.HashMap;
import java.util.Map;

public class SoapPrefixResponseInterceptor extends AbstractSoapInterceptor {

    public SoapPrefixResponseInterceptor() {
        super(Phase.PREPARE_SEND);
    }

    public void handleMessage(SoapMessage message) {
        Map<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("env", "http://www.w3.org/2003/05/soap-envelope");
        
        //SoapFault soapFault = (SoapFault) message.getContent(Exception.class);
        Exception soapFault = (Exception) message.getContent(Exception.class);
        if (soapFault ==  null) {
    			message.put("org.apache.cxf.stax.force-start-document", Boolean.TRUE);
    			stringStringHashMap.put("ns1", "https://nww.pathwaysdos.nhs.uk/app/api/webservices");
        }

        message.put("soap.env.ns.map", stringStringHashMap);

        message.put("disable.outputstream.optimization", true);
        
    }
}