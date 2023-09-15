package org.example.marvel.characters.JMeter;

import org.example.marvel.characters.MarvelAsyncGrpcClient;
import io.grpc.StatusRuntimeException;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import javax.net.ssl.SSLException;


public class MarvelCharacterGrpcSampler extends AbstractJavaSamplerClient {

    MarvelAsyncGrpcClient client = null;
    String host = "";
    String port = "";
    String ssl = "";

    @Override
    public void setupTest(JavaSamplerContext context) {
        host = context.getParameter("host");
        port = context.getParameter("port");
        ssl = context.getParameter("ssl");

        try {
            this.client = new MarvelAsyncGrpcClient(host, Integer.parseInt(port), Integer.parseInt(ssl));
        }
        catch(SSLException e) {
            e.printStackTrace();
        }
        super.setupTest(context);
    }

    @Override
    public Arguments getDefaultParameters() {
        Arguments defaultParameters = new Arguments();
        defaultParameters.addArgument("host", "grpc.marvel");
        defaultParameters.addArgument("port", "11000");
        defaultParameters.addArgument("ssl", ssl);
        return defaultParameters;
    }

    public SampleResult runTest(JavaSamplerContext context) {
        ssl = context.getParameter("ssl");
        SampleResult result = new SampleResult();
        String response = "";
        result.sampleStart();

        try {
            if (!client.isActiveChannel()) {
                this.client = new MarvelAsyncGrpcClient(host, Integer.parseInt(port), Integer.parseInt(ssl));
            }
            // Assuming you have a method to retrieve Marvel character data by alias
            this.client.getCharacterInfoByAlias();
            result.sampleEnd();
            result.setSuccessful(true);
            result.setResponseData(response.getBytes());
            result.setResponseMessage("Successfully retrieved Marvel character information");
            result.setResponseCodeOK(); // 200 code

        } catch (SSLException | StatusRuntimeException e) {
            result.sampleEnd(); // stop stopwatch
            result.setSuccessful(false);
            result.setResponseMessage("Exception: " + e);
            result.setSuccessful(false);
            // get stack trace as a String to return as document data
            java.io.StringWriter stringWriter = new java.io.StringWriter();
            e.printStackTrace(new java.io.PrintWriter(stringWriter));
            result.setResponseData(stringWriter.toString().getBytes());
            result.setDataType(SampleResult.TEXT);
            result.setResponseCode("500");
        }
        return result;
    }

    @Override
    public void teardownTest(JavaSamplerContext context) {
        try {
            client.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.teardownTest(context);
    }
}
