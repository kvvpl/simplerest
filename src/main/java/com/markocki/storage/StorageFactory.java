package com.markocki.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.markocki.model.Record;

public class StorageFactory {
	private static final Logger logger = LoggerFactory.getLogger(StorageFactory.class);
	private static final String DB_FILENAME = "storage.db";

	public static Storage loadStorage(String storagedir) {
		Storage result = new SimpleStorage();

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(storagedir + File.separatorChar+DB_FILENAME));) {
			while (true) {
				result.save((Record) ois.readObject());
			}
		} catch (RecordStoreException exc) {
			// should not happen as we previously stored unique primary keys
			// if even though happens = ignore
		} catch (ClassNotFoundException exc) {
			logger.error("Error while loading the simple storage content", exc);
		} catch (IOException exc) {
			// end of file
			// intentionally caught and ignore
		}

		return result;
	}

	public static void closeStorage(Storage storage, String storagedir) {
		if (storage instanceof SimpleStorage) {
			Collection<Record> records = ((SimpleStorage) storage).getAllRecords();

			try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(storagedir + File.separatorChar+DB_FILENAME));) {
				for (Iterator<Record> iterator = records.iterator(); iterator.hasNext();) {
					Record record = iterator.next();
					oos.writeObject(record);
				}
			} catch (IOException exc) {
				logger.error("Error while saving storage to the file.", exc);
			}

		} else {
			logger.error("Error while closing the simple storage content. Should be of SimpleStorage type but was "
					+ storagedir.getClass().getName());
		}
	}

	static class SimpleStorage implements Storage {
		// let's have concurrency support, so both reads and writes (uploads) are concurrently possible
		// and reads return the current value of the record
		
		private Map<String, Record> theStorage = new ConcurrentHashMap<String, Record>();

		@Override
		public Record findByPrimaryKey(String primaryKey) throws NoRecordFoundException {
			Record result = theStorage.get(primaryKey);
			
			if ( result != null ) {
				return  result;
			} else {
				throw new NoRecordFoundException("No record for PRIMARY_KEY=" + primaryKey);
			}
		}

		@Override
		public Record delete(Record recordToDelete) throws NoRecordFoundException {
			String primaryKey = recordToDelete.getPrimaryKey();

			Record result = theStorage.remove(primaryKey);
			
			if ( result != null ) {
				return  result;
			} else {
				throw new NoRecordFoundException("No record for PRIMARY_KEY=" + primaryKey);
			}			
		}

		@Override
		public void save(Record recordToStore) throws RecordStoreException {
			String primaryKey = recordToStore.getPrimaryKey();

			// we do not accept duplicates, nor updates to the currently existing records
			if (!theStorage.containsKey(primaryKey)) {
				theStorage.put(primaryKey, recordToStore);
			} else {
				throw new RecordStoreException("Record for PRIMARY_KEY=" + primaryKey + " already exists");
			}

		}

		Collection<Record> getAllRecords() {
			return theStorage.values();
		}
	}

}

