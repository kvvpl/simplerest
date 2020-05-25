package com.markocki.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;

import com.markocki.model.Record;
import com.markocki.model.RecordCreateException;
import com.markocki.model.RecordFactory;

public class StorageTest {
	Record createRecord(String key, String name, String description, long timestamp) throws RecordCreateException {
		return RecordFactory.createRecord(key, name, description, timestamp);
	}

	File createNewStorageDictionary(String directory) throws IOException {
		String newDirectory = directory + System.currentTimeMillis();
		File file = new File(newDirectory);

		if (file.mkdir()) {
			return file;
		} else {
			throw new IOException("Cannot create directory: " + newDirectory);
		}
	}

	void removeStorageDictionary(File directory) throws IOException {
		File[] files = directory.listFiles();
		for (int i = 0; i < files.length; i++) {
			files[i].delete();
		}
		Files.delete(directory.toPath());
	}

	@Test
	void getFactory() {
		assertNotNull(StorageFactory.class, "There is no StorageFactory class found");
	}

	@Test
	void testOpenEmptyStorage() throws IOException, RecordCreateException {
		String tmpdir = System.getProperty("java.io.tmpdir");

		File dir = createNewStorageDictionary(tmpdir);

		Storage storage = StorageFactory.loadStorage(dir.getPath());

		assertNotNull(storage, "Storage is null");

		removeStorageDictionary(dir);
	}

	@Test
	void testSaveAndRetrieve() throws IOException, RecordCreateException {
		String tmpdir = System.getProperty("java.io.tmpdir");

		File dir = createNewStorageDictionary(tmpdir);

		Storage storage = StorageFactory.loadStorage(dir.getPath());

		Record record = createRecord("key", "name", "description", System.currentTimeMillis());

		try {
			storage.save(record);

			Record retrievedRecord = storage.findByPrimaryKey(record.getPrimaryKey());

			assertEquals(record.getName(), retrievedRecord.getName(), "Name of record is different");
			assertEquals(record.getDescription(), retrievedRecord.getDescription(),
					"Description of record is different");
			assertEquals(record.getUpdatedTimestamp(), retrievedRecord.getUpdatedTimestamp(),
					"Timestamp of record is different");

		} catch (RecordStoreException | NoRecordFoundException exc) {
			fail(exc);
		}

		removeStorageDictionary(dir);
	}

	@Test
	void testCloseAndReopenStorage() throws IOException, RecordCreateException {
		String tmpdir = System.getProperty("java.io.tmpdir");

		File dir = createNewStorageDictionary(tmpdir);

		Storage storage = StorageFactory.loadStorage(dir.getPath());

		try {
		for (int counter = 1; counter <= 5; counter++) {
			Record record = createRecord("key" + counter, "name" + counter, "description", System.currentTimeMillis());
			storage.save(record);
		}

		StorageFactory.closeStorage(storage, dir.getPath());
		
		Storage storageReopened = StorageFactory.loadStorage(dir.getPath());
		
		for (int counter = 1; counter <= 5; counter++) {
			storageReopened.findByPrimaryKey("key"+counter);
		}
		}
		catch(RecordStoreException exc) {
			fail("Exception occured while storing record",exc);
		}catch(NoRecordFoundException exc) {
			fail("Exception occured while retrieving record",exc);
		}
		removeStorageDictionary(dir);
	}
}
