package io.jenkins.plugins.aws.lambda.cloud;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;

import com.cloudbees.jenkins.plugins.awscredentials.AWSCredentialsHelper;
import com.cloudbees.jenkins.plugins.awscredentials.AmazonWebServicesCredentials;

import hudson.ProxyConfiguration;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

import jenkins.model.Jenkins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to create AWS Lambda Client
 *
 * @author jlamande
 */
public class LambdaClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(LambdaClient.class);

    @CheckForNull
    private static AmazonWebServicesCredentials getCredentials(@Nullable String credentialsId) {
        return AWSCredentialsHelper.getCredentials(credentialsId, Jenkins.getActiveInstance());
    }

    public static AWSLambda buildClient(String credentialsId, String region) {
        try {
            ProxyConfiguration proxy = Jenkins.getActiveInstance().proxy;
            ClientConfiguration clientConfiguration = new ClientConfiguration();

            if (proxy != null) {
                clientConfiguration.setProxyHost(proxy.name);
                clientConfiguration.setProxyPort(proxy.port);
                clientConfiguration.setProxyUsername(proxy.getUserName());
                clientConfiguration.setProxyPassword(proxy.getPassword());
            }

            AWSLambdaClientBuilder builder = AWSLambdaClientBuilder.standard()
                .withClientConfiguration(clientConfiguration).withRegion(region);

            AmazonWebServicesCredentials credentials = getCredentials(credentialsId);
            if (credentials != null) {
                LOGGER.debug("[AWS Lambda Cloud]: Using credentials: {}", credentialsId);
                builder.withCredentials(credentials);
            }
            LOGGER.debug("[AWS Lambda Cloud]: Selected Region: {}", region);
            return builder.build();
        } catch(IllegalStateException e) {
            LOGGER.warn("Illegal state : {}", e.getMessage());
            return null;
        }
    }
}
