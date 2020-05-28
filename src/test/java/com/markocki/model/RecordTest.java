package com.markocki.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;

class RecordTest {

	@Test
	void getFactory() {
		assertNotNull(RecordFactory.class, "There is no RecordFactory class found");
	}

	@Test
	void createEmptyRecord() {
		try {
			Record record = RecordFactory.createRecord("", "", "", 0);
			fail("Record with empty primary key created");
		} catch (RecordCreateException exc) {
			assertEquals("Primary key cannot be empty or null", exc.getMessage());
		}
	}

	@Test
	void createMinimalRecord() {
		try {
			String primaryKey = "a sample primary key";
			Record record = RecordFactory.createRecord(primaryKey, "", "", 0);
			assertEquals(primaryKey,record.getPrimaryKey());
		} catch (RecordCreateException exc) {
			fail(exc.getMessage());
		}
	}

	@Test
	void createRecord() {
		try {
			String primaryKey = "a sample primary key";
			String name = "this is a name";
			String description  = "this is a description";
			long timestamp = System.currentTimeMillis();
			
			Record record = RecordFactory.createRecord(primaryKey, name, description, timestamp);
			assertEquals(primaryKey,record.getPrimaryKey());
			assertEquals(name,record.getName());
			assertEquals(description,record.getDescription());
			assertEquals(timestamp,record.getUpdatedTimestamp());
		} catch (RecordCreateException exc) {
			fail(exc.getMessage());
		}
	}

	@Test
	void storeRecord() {
		String tmpdir = System.getProperty("java.io.tmpdir");
		String dbName = "storage.db";
		
		String primaryKey = "a sample primary key";
		String name = "this is a name";
		String description  = "this is a description";
		long timestamp = System.currentTimeMillis();
		
	
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(tmpdir + File.separatorChar + dbName));) {
			Record record = RecordFactory.createRecord(primaryKey, name, description, timestamp);
			oos.writeObject(record);
		} catch (IOException|RecordCreateException exc) {
			fail(exc.getMessage());
		}
	}
	
	
	@Test
	void storeAndRestorRecord() throws IOException {
		String tmpdir = System.getProperty("java.io.tmpdir");
		String dbName = "storage.db";
		
		String primaryKey = "a sample primary key";
		String name = "this is a name";
		String description  = "this is a description";
		long timestamp = System.currentTimeMillis();
		
		boolean dbSucessfullyCreated = false;
		
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(tmpdir + File.separatorChar + dbName));) {
			Record record = RecordFactory.createRecord(primaryKey, name, description, timestamp);
			oos.writeObject(record);
			dbSucessfullyCreated = true;
		} catch (IOException|RecordCreateException exc) {
			fail(exc.getMessage());
		}
		
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(tmpdir + File.separatorChar + dbName));) {
			Object obj= ois.readObject();
			assertEquals(true, obj instanceof Record);
			Record record = (Record)obj;
			assertEquals(primaryKey,record.getPrimaryKey());
			assertEquals(name,record.getName());
			assertEquals(description,record.getDescription());
			assertEquals(timestamp,record.getUpdatedTimestamp());
		} catch (IOException|ClassNotFoundException exc) {
			fail(exc.getMessage());
		}
		
		if( dbSucessfullyCreated ) {
			Files.delete(new File(tmpdir + File.separatorChar + dbName).toPath());
		}
	}	
	
}
