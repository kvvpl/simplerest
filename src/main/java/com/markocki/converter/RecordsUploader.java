package com.markocki.converter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.markocki.model.Record;
import com.markocki.model.RecordCreateException;
import com.markocki.model.RecordFactory;

public class RecordsUploader {
	public static List<Record> tryRetrieveRecords(BufferedReader reader)
			throws IOException, RecordsUploaderFileParseException {
		// Per specification point 1a first line is a header of given structure
		String line = reader.readLine();
		validateHeaderLine(line);

		List<Record> result = new ArrayList<Record>();
		boolean emptyLineRead = false;

		long lineNumber = 2; // header is the first line, so we read the next one
		while ((line = reader.readLine()) != null) {
			if ("".equals(line.trim())) {
				emptyLineRead = true;
			} else {
				if (!emptyLineRead) { // empty line expected only at the end of file
					result.add(readInNextRecord(line, lineNumber));
					emptyLineRead = false;
				} else {
					// only last line can be empty but got empty line followed by not empty one
					throw new RecordsUploaderFileParseException("Only last line in the file can be empty however line "
							+ (lineNumber - 1) + "  was also empty, i.e. is following by not empty one.");
				}
			}
			lineNumber++;
		}

		// Per specification point 1b last (read) line must be empty, so checking that
		if (emptyLineRead) { //
			return result;
		} else {
			throw new RecordsUploaderFileParseException("Last line in the file must be empty but was not.");

		}
	}
	
	private static Record readInNextRecord(String line, long lineNumber) throws RecordsUploaderFileParseException {
		// Per specification point 1 line must contains comma-separated values
		
		// a trick to make sure empty non required columns will be accepted
		String[] fields = line.replace(",",", ").split(",");

		// Per specification point 1c line must contains four values
		if (fields.length != 4) {
			throw new RecordsUploaderFileParseException("Line number " + lineNumber
					+ " does not contains enough fields. Expected 4, received " + fields.length);
		}

		// Per specification point 1d primary key must be non-blank
		if (fields[0] == null || "".equals(fields[0].trim())) {
			throw new RecordsUploaderFileParseException("Line number " + lineNumber + " PRIMARY_KEY is empty but cannot.");
		}

		String key = fields[0].trim();
		String name = fields[1].trim();
		String desc = fields[2].trim();
		String timestampStr = fields[3].trim();
		
		try {
			// Assumption made as specification does not make it clear!
			// Timestamp is of long type (so convert received string to long
			// as only PRIMARY_KEY must be non empty, timestamp can be
			// in that case set it to empty long, i.e. 0
			long timestamp = 0;
			if ( ! "".equals(timestampStr) ) {
				timestamp = Long.parseLong(timestampStr);
			}
			return RecordFactory.createRecord(key, name, desc, timestamp);
		} catch (NumberFormatException exc) {
			throw new RecordsUploaderFileParseException("Timestamp at line number " + lineNumber + " is of incorrect format.",
					exc);

		} catch(RecordCreateException exc) {
			throw new RecordsUploaderFileParseException("Record at line " + lineNumber + " cannot be parsed correctly.",
					exc);
			
		}
	}

	private static void validateHeaderLine(String line) throws RecordsUploaderFileParseException {
		if (line != null && !"".equals(line.trim())) {

			// Per specification point 1a we expect comma-separated data
			String[] headerFields = line.split(",");

			// check if header is of a given format, i.e.,
			// PRIMARY_KEY,NAME,DESCRIPTION,UPDATED_TIMESTAMP
			// Per specification point 1a
			if (headerFields.length != 4) {
				throw new RecordsUploaderFileParseException(
						"Header does not contains enough fields. Expected 4, received " + headerFields.length);
			}

			checkHeaderField("PRIMARY_KEY", headerFields[0], 0);
			checkHeaderField("NAME", headerFields[1], 1);
			checkHeaderField("DESCRIPTION", headerFields[2], 2);
			checkHeaderField("UPDATED_TIMESTAMP", headerFields[3], 3);
		} else {
			throw new RecordsUploaderFileParseException("Header's line cannot be empty");
		}
	}

	private static void checkHeaderField(String name, String value, int index) throws RecordsUploaderFileParseException {
		if (!name.equals(value)) {
			throw new RecordsUploaderFileParseException(
					"Header's field at index " + index + " should be " + name + " but was " + value);
		}
	}

}
