package com.nhsd.a2si.client.dos;

import https.nww_pathwaysdos_nhs_uk.app.api.webservices.PathWayServiceSoap;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.xml.ws.soap.SOAPBinding;

/**
 * The real DOS Client, i.e. DosSoapClient, requires a class that is a SOAP Client, removing the need for the DOS
 * client to have to deal with SOAP marshalling and unmarshalling etc.
 * This class creates the necessary class that is injected into the DOS Soap client.
 */
@Profile({
        "doswrapper-local-dos-soap-local-wiremock-cpsc-stub-na",
        "doswrapper-local-dos-soap-local-wiremock-cpsc-rest-local",
        "doswrapper-local-dos-soap-uat-cpsc-stub-na",
        "doswrapper-local-dos-soap-uat-cpsc-rest-local",
        "doswrapper-local-dos-soap-prod-cpsc-stub-na",
        "doswrapper-local-dos-soap-prod-cpsc-rest-local",
        "doswrapper-aws-dos-soap-uat-cpsc-stub-na",
        "doswrapper-aws-dos-soap-uat-cpsc-rest-aws",
        "doswrapper-aws-dos-soap-aws-wiremock-cpsc-stub-na",
        "doswrapper-aws-dos-soap-aws-wiremock-cpsc-rest-aws",
        "doswrapper-aws-dos-soap-prod-cpsc-rest-aws"})
@Configuration
public class DosSoapClientConfiguration {

    // The Client will use this URL to point to the DOS System
    @Value("${dos.service.url}")
    private String dosServiceUrl;

    @Bean(name = "pathWayServiceSoap")
    public PathWayServiceSoap createPathwayServiceSoapBean() {

        JaxWsProxyFactoryBean jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
        // We need to specify the SOAP Binding as SOAP 1.2
        jaxWsProxyFactoryBean.setBindingId(SOAPBinding.SOAP12HTTP_BINDING);
        jaxWsProxyFactoryBean.setServiceClass(PathWayServiceSoap.class);
        jaxWsProxyFactoryBean.getInInterceptors().add(new LoggingInInterceptor());
        jaxWsProxyFactoryBean.getOutInterceptors().add(new LoggingOutInterceptor());

        jaxWsProxyFactoryBean.getInFaultInterceptors().add(new DosSoap12FaultPostUnmarshalInInterceptor());

        jaxWsProxyFactoryBean.setAddress(dosServiceUrl);

        return (PathWayServiceSoap) jaxWsProxyFactoryBean.create();
    }

    @Override
    public String toString() {
        return "DosClientConfig{" +
                "dosServiceUrl='" + dosServiceUrl + '\'' +
                '}';
    }
}