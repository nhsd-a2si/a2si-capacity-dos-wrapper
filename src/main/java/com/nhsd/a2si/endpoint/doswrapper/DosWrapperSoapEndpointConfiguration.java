package com.nhsd.a2si.endpoint.doswrapper;

import org.apache.cxf.Bus;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.ws.Endpoint;

/**
 * DOS Wrapper SOAP Endpoint Configuration creates the SOAP endpoint, using Spring Autowiring on the
 * constructor method to inject CXF's (The SOAP framework used) main bus component. A CXF endpoint is created
 * and a URL suffix is assigned to it.
 * Logging is also enabled for the endpoint. This endpoint is what SOAP requests are actually sent to.
 */
@Configuration
public class DosWrapperSoapEndpointConfiguration {

    private Bus bus;

    private DosWrapperSoapEndpoint dosWrapperSoapEndpoint;

    @Autowired
    public DosWrapperSoapEndpointConfiguration(Bus bus, DosWrapperSoapEndpoint dosWrapperSoapEndpoint) {
        this.bus = bus;
        this.dosWrapperSoapEndpoint = dosWrapperSoapEndpoint;
    }

    @Bean
    public Endpoint endpoint() {

        // register the endpoint class - an endpoint is the service class, handling requests and returning responses.
        EndpointImpl endpoint = new EndpointImpl(bus, dosWrapperSoapEndpoint);

        //set the url suffix the endpoint will use, after the cxf.path value defined in application.properties
        // the actual full url for the service will therefore be something like:
        //     http://localhost:9090/nhsd/doswrapper/ws/pathwayService
        endpoint.publish("/webservices");

        endpoint.getOutFaultInterceptors().add(new DosSoap12FaultInInterceptor());

        endpoint.getOutInterceptors().add(new SoapPrefixResponseInterceptor());

        endpoint.getOutFaultInterceptors().add(new SoapPrefixResponseInterceptor());

        LoggingFeature logFeature = new LoggingFeature();
        logFeature.setPrettyLogging(true);
        logFeature.initialize(bus);
        endpoint.getFeatures().add(logFeature);

        return endpoint;
    }
}
