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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.markocki.converter.RecordsUploader;
import com.markocki.converter.RecordsUploaderFileParseException;
import com.markocki.converter.RecordsUploaderInternalException;
import com.markocki.model.Record;
import com.markocki.storage.NoRecordFoundException;
import com.markocki.storage.RecordStoreException;
import com.markocki.storage.Storage;

import spark.Route;

public class FrontController {
	private static final Logger logger = LoggerFactory.getLogger(FrontController.class);
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
			logger.trace("Method called ...");
			ResponseBuilder.createtResponseOK(response, "It works " + request.body());
			return response.body();
		};
	}

	public Route delete() {
		return (request, response) -> {
			logger.trace("[DELETE] Method called ...");

			Record record;
			String key = request.params(PRIMARY_KEY_REQUEST_PARAMETER_NAME);

			logger.debug("[DELETE] Looking for record of primary key:" + key);

			try {
				record = getStorage().findByPrimaryKey(key);
			} catch (NoRecordFoundException exc) {
				logger.warn("[DELETE] Failure while looking for the record of primiary key="+key,exc);
				throw exc;
			}
			
			try {
				getStorage().delete(record);
			} catch (NoRecordFoundException exc) {
				logger.warn("[DELETE] Failure while removing the found record of primiary key="+key,exc);
				throw exc;
			}
			
			String responseStr = "Record deleted for PRIMARY_KEY=" + key;
			logger.debug("[DELETE] "+responseStr);
			ResponseBuilder.createtResponseOK(response, responseStr);

			return response.body();
		};
	}

	public Route get() {
		return (request, response) -> {
			logger.trace("[GET] Method called ...");

			Record record;
			
			String key = request.params(PRIMARY_KEY_REQUEST_PARAMETER_NAME);

			logger.debug("[GET] Looking for record of primary key:" + key);

			try {
				record = getStorage().findByPrimaryKey(key);
			} catch (NoRecordFoundException exc) {
				logger.warn("[GET] Failure while looking for the record of primiary key="+key,exc);
				throw exc;
			}

			logger.debug("[GET] Found record for primary key:"+key+". Record content: "+record);
			ResponseBuilder.createtResponseOK(response, record);

			return response.body();
		};
	}

	public Route upload() {
		return (request, response) -> {
			logger.trace("[UPLOAD] Method called ...");

			
			logger.trace("[UPLOAD] enable jetty multipart support");
			request.raw().setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, multipartConfigElement);

			// API contract - uploaded file will be delivered via POST under file multipart
			Part filePart = null;

			try {
				logger.trace("[UPLOAD] getting request part 'file'");
				filePart = request.raw().getPart(FILE_PART_NAME);

				if (filePart != null) {
					logger.trace("[UPLOAD] request part 'file' found");

					try (InputStream fileInputStream = filePart.getInputStream();
							BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));) {
						logger.trace("[UPLOAD] reading file content");

						List<Record> result = RecordsUploader.tryRetrieveRecords(reader);

						logger.info("[UPLOAD] Number of records read="+result.size());
						
						logger.trace("[UPLOAD] saving the records to the storage");
						boolean duplicatesFound = false;
						for (Iterator<Record> iterator = result.iterator(); iterator.hasNext();) {
							Record record = (Record) iterator.next();

							try {
								getStorage().save(record);
							} catch (RecordStoreException exc) {
								duplicatesFound = true;
							}
						}

						logger.trace("[UPLOAD] saving the records to the storage - completed");
						
						if (duplicatesFound) {
							logger.info("[UPLOAD] Uploaded, duplicates eliminated");
							ResponseBuilder.createtResponseError(response, "Uploaded, duplicates eliminated");
						} else {
							logger.info("[UPLOAD] Uploaded successfully and clearly");
							ResponseBuilder.createtResponseOK(response, "Uploaded successfully and clearly");
						}
					} catch (IOException exc) {
						logger.error("[UPLOAD] Internal error while uploading the file.",exc);
						throw new RecordsUploaderInternalException(
								"Internal error while uploading the file =" + exc.getMessage());
					}
				} else {
					logger.error("[UPLOAD] No file to upload found under 'file' part");
					throw new RecordsUploaderInternalException("No file to upload found under 'file' part");
				}

			} catch (IOException exc) {
				logger.error("[UPLOAD] There was no 'file' part inside the request",exc);
				throw new RecordsUploaderInternalException("There was no 'file' part inside the request");
			} catch (ServletException exc) {
				logger.error("[UPLOAD] Error while uploading a file",exc);
				throw new RecordsUploaderInternalException(exc.getMessage());
			}

			return response.body();
		};
	}

}
