package com.nhsd.a2si.endpoint.doswrapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.cxf.binding.soap.SoapFault;
import org.apache.cxf.binding.soap.interceptor.SoapPreProtocolOutInterceptor;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nhsd.a2si.client.dos.DosSoap12FaultPostUnmarshalInInterceptor;

/**
 * This class changes the Fault Code in a SOAP Fault.
 * DOS uses the fault code to hold an error code but SOAP does not allow this, only strings from
 * https://www.w3.org/2003/05/soap-envelope/ - faultcodeEnum should be allowed.
 * To avoid validation errors, an interceptor, DosSoap12FaultPostUnmarshalInInterceptor, for the DOS client (that calls the DOS System and gets responses from DOS)
 * changes the fault code to be a valid value, whilst using the original error code and prefixing it to the
 * free form message, using a "--" as a separator'.
 *
 * This interceptor takes the message and restores the original message text and adds the code to a sub code element
 */

public class DosSoap12FaultInInterceptor extends AbstractPhaseInterceptor<Message> {
    private static final Logger logger = LoggerFactory.getLogger(DosSoap12FaultInInterceptor.class);

    public DosSoap12FaultInInterceptor() {
        super(Phase.PRE_STREAM);
        addBefore(SoapPreProtocolOutInterceptor.class.getName());
    }

    @Override
    public void handleMessage(Message message) {

        boolean isOutbound = false;
        isOutbound = message == message.getExchange().getOutMessage()
               || message == message.getExchange().getOutFaultMessage();

        if (isOutbound) {
		    	OutputStream os = message.getContent(OutputStream.class);
		    	CachedOutputStream cs = new CachedOutputStream();
		    	message.setContent(OutputStream.class, cs);
		
		    	message.getInterceptorChain().doIntercept(message);
		
		    	try {
		    	    cs.flush();
		    	    CachedOutputStream csnew = (CachedOutputStream) message.getContent(OutputStream.class);
		
		    	    String soapMessage = IOUtils.toString(csnew.getInputStream());	    	    
		    	    //SoapFault soapFault = (SoapFault) message.getContent(Exception.class);
		    	    Exception soapFault = (Exception) message.getContent(Exception.class);
		    	    String dosMessage = "";
		    	    if (soapFault instanceof SoapFault) {
		    	    	dosMessage = ((SoapFault) soapFault).getMessage();
		    	    } else {
		    	    	dosMessage = "xxx--" + soapFault.getMessage();
		    	    }
		    	    if (soapFault != null) {
		    	    	// Add xml header if it's not there
		    	    	if (!soapMessage.startsWith("<?xml")) {
		    	    		soapMessage = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + soapMessage;
		    	    	}
		    	    	
		    	    	
		    	    	// Code and Message are in the fault separated by "--"
		    	    	// Split and alter the output xml
		    	    	//String dosMessage = soapFault.getMessage();
		    	        String[] codeAndMessage = dosMessage.split("--");		    	    	
		    	    	
		    	        if (codeAndMessage.length > 0) {
		    	    			soapMessage = soapMessage.replace("env:Receiver", codeAndMessage[0]);
		    	    			if (codeAndMessage.length > 1) {
			    	    			soapMessage = soapMessage.replace(dosMessage, codeAndMessage[1]);	
			    	    			soapMessage = soapMessage.replaceAll("<env:Text xml:lang=\"en-GB\">", "<env:Text>");
			    	    			soapMessage = soapMessage.replaceAll("<env:Text xml:lang=\"en-US\">", "<env:Text>");
			    	    			soapMessage = soapMessage.replaceAll("<env:Text xml:lang=\"en\">", "<env:Text>");
		    	    			}
		    	        }
		    	    }		    	    
		    	    
		    	    ByteArrayInputStream bain = new ByteArrayInputStream(soapMessage.getBytes("UTF-8"));
		    	    
	            CachedOutputStream.copyStream(bain, os, 1024);
		
	            cs.close();
	            os.flush();
	
	            message.setContent(OutputStream.class, os);
	        } catch (IOException ioe) {
	            ioe.printStackTrace();
	        }
        } else {
            try {
                InputStream is = message.getContent(InputStream.class);
                message.setContent(InputStream.class, is);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}
}
