package com.nhsd.a2si.client.dos;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;

import javax.xml.soap.SOAPConstants;

/**
 * This class changes the Fault Code in a SOAP Fault.
 * DOS uses the fault code to hold an error code but SOAP does not allow this, only strings from
 * https://www.w3.org/2003/05/soap-envelope/ - faultcodeEnum should be allowed.
 * To avoid validation errors, this interceptor changes the fault code to be a valid value, whilst using the
 * original error code and prefixing it to the free form message, using a "--" as a separator'.
 */
public class DosSoap12FaultPostUnmarshalInInterceptor extends AbstractSoapInterceptor {

    public DosSoap12FaultPostUnmarshalInInterceptor() {
        super(Phase.POST_UNMARSHAL);

    }

    @Override
    public void handleMessage(SoapMessage soapMessage) throws Fault {

        Fault fault = (Fault) soapMessage.getContent(Exception.class);

        String dosErrorCode = fault.getFaultCode().getLocalPart();

        String dosMessage = fault.getMessage();

        fault.setFaultCode(SOAPConstants.SOAP_RECEIVER_FAULT);
    }

}