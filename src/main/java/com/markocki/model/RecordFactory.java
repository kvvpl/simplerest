package com.markocki.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;

public class RecordFactory {
	public static Record createRecord(String primaryKey, String name, String description, long updatedTimestamp)
			throws RecordCreateException {
		if (Objects.isNull(primaryKey) || "".equals(primaryKey.trim())) {
			throw new RecordCreateException("Primary key cannot be empty or null");
		} else {
			return new RecordImpl(primaryKey, name, description, updatedTimestamp);
		}
	}

	static class RecordImpl implements Record, Serializable {
		private String primaryKey;
		private String name;
		private String description;
		private long updatedTimestamp;
		private static final long serialVersionUID = 1L;

		@Override
		public String toString() {
			return "{primaryKey:" + primaryKey + ", name:" + name + ", description:" + description
					+ ", updatedTimestamp:" + updatedTimestamp + "}";
		}

		public RecordImpl(String primaryKey, String name, String description, long updatedTimestamp) {
			this.primaryKey = primaryKey;
			this.name = name;
			this.description = description;
			this.updatedTimestamp = updatedTimestamp;
		}

		public String getPrimaryKey() {
			return primaryKey;
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}

		public long getUpdatedTimestamp() {
			return updatedTimestamp;
		}

		private void writeObject(ObjectOutputStream out) throws IOException {
			out.writeUTF(primaryKey);
			out.writeUTF(name);
			out.writeUTF(description);
			out.writeLong(updatedTimestamp);
		}

		private void readObject(ObjectInputStream ois) throws IOException {
			this.primaryKey = ois.readUTF();
			this.name = ois.readUTF();
			this.description = ois.readUTF();
			this.updatedTimestamp = ois.readLong();
		}
	}
}
