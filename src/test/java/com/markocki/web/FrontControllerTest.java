package com.markocki.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.logging.Level;
import java.util.logging.LogManager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.markocki.model.Record;
import com.markocki.storage.NoRecordFoundException;
import com.markocki.storage.Storage;

import spark.Request;
import spark.Response;

public class FrontControllerTest {
	@Test
	public void testEcho() throws Exception {
		final String MESSAGE = "It works";
		
		Request request = Mockito.mock(Request.class);
		Mockito.when(request.body()).thenReturn("");

		Response response = Mockito.mock(Response.class);
		Mockito.when(response.body()).thenReturn(MESSAGE);

		FrontController fc = Mockito.mock(FrontController.class);
		Mockito.when(fc.echoRoute()).thenCallRealMethod();

		fc.echoRoute().handle(request, response);

		Mockito.verify(request, Mockito.times(1)).body();
		Mockito.verifyNoMoreInteractions(request);

		Mockito.verify(response, Mockito.times(1)).body();
		Mockito.verify(response, Mockito.times(1)).body(Mockito.contains(MESSAGE));
		Mockito.verify(response, Mockito.times(1)).status(Mockito.eq(200));
		Mockito.verifyNoMoreInteractions(response);

		assertEquals(true, response.body().contains(MESSAGE));
	}

	@Test
	public void testSuccessfulDelete() throws Exception {
		final String PRIMARY_KEY = "key";

		Request request = Mockito.mock(Request.class);
		Mockito.when(request.params(Mockito.anyString())).thenReturn(PRIMARY_KEY);

		Response response = Mockito.mock(Response.class);

		Record record = Mockito.mock(Record.class);

		Storage storage = Mockito.mock(Storage.class);
		Mockito.when(storage.findByPrimaryKey(Mockito.contains(PRIMARY_KEY))).thenReturn(record);
		Mockito.when(storage.delete(record)).thenReturn(record);

		FrontController fc = Mockito.mock(FrontController.class);
		Mockito.when(fc.getStorage()).thenReturn(storage);

		Mockito.when(fc.delete()).thenCallRealMethod();

		fc.delete().handle(request, response);

		Mockito.verify(request, Mockito.times(1)).params(Mockito.anyString());
		Mockito.verifyNoMoreInteractions(request);

		Mockito.verify(response, Mockito.times(1)).status(200);
		Mockito.verify(response, Mockito.times(1)).body(Mockito.contains(PRIMARY_KEY));
		Mockito.verifyNoMoreInteractions(request);

		Mockito.verify(fc, Mockito.times(2)).getStorage();
		Mockito.verify(fc, Mockito.times(1)).delete();

		Mockito.verifyNoMoreInteractions(fc);

		Mockito.verify(storage, Mockito.times(1)).findByPrimaryKey(Mockito.contains(PRIMARY_KEY));
		Mockito.verify(storage, Mockito.times(1)).delete(record);
		Mockito.verifyNoMoreInteractions(storage);
	}

	@Test
	public void testUnSuccessfulDelete() throws Exception {
		final String PRIMARY_KEY = "keNotToBeFoundy";

		Request request = Mockito.mock(Request.class);
		Mockito.when(request.params(Mockito.anyString())).thenReturn(PRIMARY_KEY);

		Response response = Mockito.mock(Response.class);

		Storage storage = Mockito.mock(Storage.class);
		Mockito.when(storage.findByPrimaryKey(Mockito.contains(PRIMARY_KEY)))
				.thenThrow(new NoRecordFoundException(PRIMARY_KEY));

		FrontController fc = Mockito.mock(FrontController.class);
		Mockito.when(fc.getStorage()).thenReturn(storage);

		Mockito.when(fc.delete()).thenCallRealMethod();

		try {
			fc.delete().handle(request, response);
			fail("Should fail as the record should NOT be found");
		} catch (NoRecordFoundException exc) {
			assertTrue(exc.getMessage().contains(PRIMARY_KEY));
		}

		Mockito.verify(request, Mockito.times(1)).params(Mockito.anyString());
		Mockito.verifyNoMoreInteractions(request);

		Mockito.verify(response, Mockito.times(0)).status(Mockito.anyInt());
		Mockito.verify(response, Mockito.times(0)).body(Mockito.anyString());
		Mockito.verifyNoMoreInteractions(request);

		Mockito.verify(fc, Mockito.times(1)).getStorage();
		Mockito.verify(fc, Mockito.times(1)).delete();

		Mockito.verifyNoMoreInteractions(fc);

		Mockito.verify(storage, Mockito.times(1)).findByPrimaryKey(Mockito.contains(PRIMARY_KEY));
		Mockito.verify(storage, Mockito.times(0)).delete(Mockito.any());

		Mockito.verifyNoMoreInteractions(storage);
	}

	@Test
	public void testSuccessfulGet() throws Exception {
		final String PRIMARY_KEY = "key";

		Request request = Mockito.mock(Request.class);
		Mockito.when(request.params(Mockito.anyString())).thenReturn(PRIMARY_KEY);

		Response response = Mockito.mock(Response.class);

		Record record = Mockito.mock(Record.class);
		Mockito.when(record.toString()).thenReturn(PRIMARY_KEY);

		Storage storage = Mockito.mock(Storage.class);
		Mockito.when(storage.findByPrimaryKey(Mockito.contains(PRIMARY_KEY))).thenReturn(record);

		FrontController fc = Mockito.mock(FrontController.class);
		Mockito.when(fc.getStorage()).thenReturn(storage);

		Mockito.when(fc.get()).thenCallRealMethod();

		fc.get().handle(request, response);

		Mockito.verify(request, Mockito.times(1)).params(Mockito.anyString());
		Mockito.verifyNoMoreInteractions(request);

		Mockito.verify(response, Mockito.times(1)).status(200);
		Mockito.verify(response, Mockito.times(1)).body(Mockito.contains(PRIMARY_KEY));
		Mockito.verifyNoMoreInteractions(request);

		Mockito.verify(fc, Mockito.times(1)).getStorage();
		Mockito.verify(fc, Mockito.times(1)).get();

		Mockito.verifyNoMoreInteractions(fc);

		Mockito.verify(storage, Mockito.times(1)).findByPrimaryKey(Mockito.contains(PRIMARY_KEY));
		Mockito.verifyNoMoreInteractions(storage);
	}

	@Test
	public void testUnSuccessfulGet() throws Exception {
		final String PRIMARY_KEY = "keNotToBeFoundy";

		Request request = Mockito.mock(Request.class);
		Mockito.when(request.params(Mockito.anyString())).thenReturn(PRIMARY_KEY);

		Response response = Mockito.mock(Response.class);

		Storage storage = Mockito.mock(Storage.class);
		Mockito.when(storage.findByPrimaryKey(Mockito.contains(PRIMARY_KEY)))
				.thenThrow(new NoRecordFoundException(PRIMARY_KEY));

		FrontController fc = Mockito.mock(FrontController.class);
		Mockito.when(fc.getStorage()).thenReturn(storage);

		Mockito.when(fc.get()).thenCallRealMethod();

		try {
			fc.get().handle(request, response);
			fail("Should fail as the record should NOT be found");
		} catch (NoRecordFoundException exc) {
			assertTrue(exc.getMessage().contains(PRIMARY_KEY));
		}

		Mockito.verify(request, Mockito.times(1)).params(Mockito.anyString());
		Mockito.verifyNoMoreInteractions(request);

		Mockito.verify(response, Mockito.times(0)).status(Mockito.anyInt());
		Mockito.verify(response, Mockito.times(0)).body(Mockito.anyString());
		Mockito.verifyNoMoreInteractions(request);

		Mockito.verify(fc, Mockito.times(1)).getStorage();
		Mockito.verify(fc, Mockito.times(1)).get();

		Mockito.verifyNoMoreInteractions(fc);

		Mockito.verify(storage, Mockito.times(1)).findByPrimaryKey(Mockito.contains(PRIMARY_KEY));
		Mockito.verifyNoMoreInteractions(storage);
	}

	@BeforeAll
	public static void setUp() {
		// as there is no way to change log level in runtime for sl4j
		Logger logger = LoggerFactory.getLogger(FrontController.class);
		// let's use this way
		LogManager.getLogManager().getLogger(FrontController.class.getName()).setLevel(Level.OFF);
	}
}
