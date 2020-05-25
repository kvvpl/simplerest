package com.markocki.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.markocki.storage.Storage;
import com.markocki.storage.StorageFactory;

import spark.Route;
import spark.Spark;

public class HTTPServer {
	private static final Logger logger = LoggerFactory.getLogger(HTTPServer.class);
	private static final String tmpdir = System.getProperty("java.io.tmpdir");
	private static final String storagedir = System.getProperty("app.storage.dir", tmpdir);
	private static final int port = Integer.getInteger("port",8080);
	
	
	public static void main(String[] args) {
		Storage theStorage = StorageFactory.loadStorage(storagedir);
		FrontController fc = new FrontController(theStorage, tmpdir);
				
		HTTPServer server = new HTTPServer(port, fc);

		server.start();
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                server.stop();
                StorageFactory.closeStorage(theStorage, storagedir);
                logger.info("Server stopped");
            }
        });

        logger.info("Server up and running");
	}


	public HTTPServer(int port, FrontController fc) {
		Spark.port(port);
    	
        Spark.staticFiles.location("/");
        Spark.notFound("{message: nothing here}");
        Spark.internalServerError("{message: something seriously wrong happened}");
        
        createGetEndpoint("/echo", fc.echoRoute());
        createGetEndpoint("/record/"+FrontController.PRIMARY_KEY_REQUEST_PARAMETER_NAME, fc.get());
        createDeleteEndpoint("/record/"+FrontController.PRIMARY_KEY_REQUEST_PARAMETER_NAME, fc.delete());
        createPostEndpoint("/upload", fc.upload());
               
        logger.info("Server configured");
	}
	
	public void start() {
        logger.info("Server started");

	}
	public void stop() {
        logger.info("Server stopped");

	}
	
	
    private void createPostEndpoint(String path, Route requestHandler) {
        Spark.post(path,requestHandler);
    }

    private static void createGetEndpoint(String path, Route requestHandler) {
       Spark.get(path, requestHandler);
    }


    private static void createDeleteEndpoint(String path, Route requestHandler) {
        Spark.delete(path, requestHandler);
    }

}
