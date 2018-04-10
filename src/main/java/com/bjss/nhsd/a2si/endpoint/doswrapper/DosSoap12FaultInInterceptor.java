package com.bjss.nhsd.a2si.endpoint.doswrapper;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.cxf.binding.soap.SoapFault;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;

import javax.xml.namespace.QName;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * This class changes the Fault Code in a SOAP Fault.
 * DOS uses the fault code to hold an error code but SOAP does not allow this, only strings from
 * https://www.w3.org/2003/05/soap-envelope/ - faultcodeEnum should be allowed.
 * To avoid validation errors, an interceptor for the DOS client (that calls the DOS System and gets responses from DOS)
 * changes the fault code to be a valid value, whilst using the original error code and prefixing it to the
 * free form message, using a "--" as a separator'.
 *
 * This interceptor takes the message and restores the original message text and adds the code to a sub code element
 */
public class DosSoap12FaultInInterceptor extends AbstractSoapInterceptor {

    public DosSoap12FaultInInterceptor() {

        super(Phase.PREPARE_SEND);

    }

    @Override
    public void handleMessage(SoapMessage soapMessage) throws Fault {

        SoapFault soapFault = (SoapFault) soapMessage.getContent(Exception.class);

        String dosMessage = soapFault.getMessage();

        String[] codeAndMessage = dosMessage.split("--");

        QName code = soapFault.getFaultCode();

        Field[] fields = FieldUtils.getAllFields(soapFault.getClass());

        String fieldName = "code";

        Field field = null;

        //You can also use ReflectionUtils to get this very easily
        try {
            field = FieldUtils.getField(soapFault.getClass(), fieldName, true);
            code = (QName) field.get(soapFault);
        } catch (Exception e) {
            System.err.print(ExceptionUtils.getStackTrace(e));
        }

        field.setAccessible(true);

        //QName newQName = new QName(code.getNamespaceURI(), codeAndMessage[0], code.getPrefix());
        QName newQName = new QName(code.getNamespaceURI(), codeAndMessage[0]);

        try {
            //FieldUtils.writeField(field, soapFault, newQName, true);

            MethodUtils.invokeMethod(soapFault, true, "setFaultCode", newQName);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }

        soapFault.setMessage(codeAndMessage[1]);


    }


}