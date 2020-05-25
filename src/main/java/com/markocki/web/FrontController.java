package com.markocki.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.Part;

import org.eclipse.jetty.server.Request;

import com.markocki.converter.RecordsUploader;
import com.markocki.converter.RecordsUploaderFileParseException;
import com.markocki.model.Record;
import com.markocki.storage.NoRecordFoundException;
import com.markocki.storage.RecordStoreException;
import com.markocki.storage.Storage;

import spark.Route;

public class FrontController {
	public final static String PRIMARY_KEY_REQUEST_PARAMETER_NAME = ":key";
	public final static String FILE_PART_NAME = "file";

	MultipartConfigElement multipartConfigElement;
	Storage theStorage;

	public FrontController(Storage storage, String tmpDir) {
		this.theStorage = storage;
		this.multipartConfigElement = new MultipartConfigElement(tmpDir);
	}

	Storage getStorage() {
		return theStorage;
	}

	public Route echoRoute() {
		return (request, response) -> {
			ResponseBuilder.createtResponseOK(response, "It works " + request.body());
			return response.body();
		};
	}

	public Route delete() {
		return (request, response) -> {
			String key = request.params(PRIMARY_KEY_REQUEST_PARAMETER_NAME);

			try {
				Record record = getStorage().findByPrimaryKey(key);
				getStorage().delete(record);
				ResponseBuilder.createtResponseOK(response, "Record deleted for PRIMARY_KEY=" + key);
			} catch (NoRecordFoundException exc) {
				ResponseBuilder.createtResponseError(response, exc);
			}

			return response.body();
		};
	}

	public Route get() {
		return (request, response) -> {
			String key = request.params(PRIMARY_KEY_REQUEST_PARAMETER_NAME);

			try {
				Record record = getStorage().findByPrimaryKey(key);
				ResponseBuilder.createtResponseOK(response, record);
			} catch (NoRecordFoundException exc) {
				ResponseBuilder.createtResponseError(response, exc);
			}

			return response.body();
		};
	}

	public Route upload() {
		return (request, response) -> {
			// enable jetty multipart support
			request.raw().setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, multipartConfigElement);

			// API contract - uploaded file will be delivered via POST under file multipart
			Part filePart = null;

			try {
				filePart = request.raw().getPart(FILE_PART_NAME);

				if (filePart != null) {
					try (InputStream fileInputStream = filePart.getInputStream();
							BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));) {
						List<Record> result = RecordsUploader.tryRetrieveRecords(reader);

						boolean duplicatesFound = false;
						for (Iterator<Record> iterator = result.iterator(); iterator.hasNext();) {
							Record record = (Record) iterator.next();

							try {
								getStorage().save(record);
							} catch (RecordStoreException exc) {
								duplicatesFound = true;
							}
						}

						if (duplicatesFound) {
							ResponseBuilder.createtResponseError(response, "Uploaded, duplicates eliminated");
						} else {
							ResponseBuilder.createtResponseOK(response, "Uploaded successfully and clearly");
						}

					} catch (RecordsUploaderFileParseException exc) {
						ResponseBuilder.createtResponseError(response, exc);
					} catch (IOException exc) {
						ResponseBuilder.createtResponseInternalError(response, exc);
					}
				} else {
					ResponseBuilder.createtResponseInternalError(response, "No file to upload found under 'file' part");
				}

			} catch (IOException exc) {
				ResponseBuilder.createtResponseError(response, "There was no 'file' part inside the request");
			} catch(ServletException exc) {
				ResponseBuilder.createtResponseError(response, exc.getMessage());
			}

			return response.body();
		};
	}

}
