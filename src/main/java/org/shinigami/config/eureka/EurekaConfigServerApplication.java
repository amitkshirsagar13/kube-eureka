package org.shinigami.config.eureka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * 
 * <p>
 * <b>Overview:</b>
 * <p>
 * 
 * 
 * <pre>
 * &#64;projectName config-eureka
 * Creation date: Sep 28, 2017
 * &#64;author Amit Kshirsagar
 * &#64;version 1.0
 * &#64;since
 * 
 * <p><b>Modification History:</b><p>
 * 
 * 
 * </pre>
 */
@SpringBootApplication
@EnableEurekaServer
@EnableConfigServer
public class EurekaConfigServerApplication {

	/**
	 * log4j object for debugging.
	 */
	private static Logger slf4jLogger = LoggerFactory.getLogger(EurekaConfigServerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(EurekaConfigServerApplication.class, args);
	}

}
