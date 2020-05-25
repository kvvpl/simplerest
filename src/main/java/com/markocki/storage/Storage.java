package com.markocki.storage;

import com.markocki.model.Record;

public interface Storage {
	public Record findByPrimaryKey(String primaryKey) throws NoRecordFoundException;
	public Record delete(Record recordToDelete) throws NoRecordFoundException;
	public void save(Record recordToStore) throws RecordStoreException;
}

