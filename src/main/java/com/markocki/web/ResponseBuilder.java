package com.markocki.web;

import java.io.IOException;

import com.markocki.model.Record;

import spark.Response;

public class ResponseBuilder {
	private static void createtResponse(Response response, int statusCode, String message) {
		response.status(statusCode);
		
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("{");
		strBuilder.append("message: ");
		strBuilder.append(message);
		strBuilder.append("}");
		
		response.body(strBuilder.toString());
	}
	
	public static void createtResponseOK(Response response, Record record) {
		//correctly processed - status 200
		response.status(200);
		response.body(record.toString());
	}

	public static void createtResponseOK(Response response, String message) {
		//correctly processed - status 200
		createtResponse(response,200,message);
	}

	public static void createtResponseError(Response response, Exception exc) {
		//something went wrong - status 400
		createtResponse(response,400,exc.getMessage());
	}

	public static void createtResponseInternalError(Response response, String message) {
		//something went really wrong (on the server side) - status 500
		createtResponse(response,500,message);
	}

	public static void createtResponseError(Response response, String message) {
		//something went wrong - status 400
		createtResponse(response,400,message);
	}

	public static void createtResponseInternalError(Response response, Exception exc) {
		//something went really wrong (on the server side) - status 500
		createtResponse(response,500,exc.getMessage());
	}	
}
