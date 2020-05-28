package com.markocki.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.markocki.gson.SimpleMessage;
import com.markocki.model.Record;

import spark.Response;

public class ResponseBuilder {
	// as Gson is thread safe we can have one and reuse it for all Records.
	static Gson gson = (new GsonBuilder()).setPrettyPrinting().create();

	private static void createtResponse(Response response, int statusCode, String message) {
		response.status(statusCode);

		response.body(gson.toJson(new SimpleMessage(message)));
	}
	public static void createtResponseOK(Response response, Record record) {
		// correctly processed - status 200
		response.status(200);
		response.body(record.toString());
	}

	public static void createtResponseOK(Response response, String message) {
		// correctly processed - status 200
		createtResponse(response, 200, message);
	}
	public static void createtResponseError(Response response, String message) {
		// something went wrong - status 400
		createtResponse(response, 400, message);
	}
}
