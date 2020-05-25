package com.markocki.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.markocki.model.Record;

public class ConverterTest {

	@Test
	void testCorrectFile() {

		try (InputStream fileInputStream = ConverterTest.class.getResourceAsStream("correct.txt");
				BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));) {

			List<Record> result;
			result = RecordsUploader.tryRetrieveRecords(reader);
			assertEquals(2, result.size());

			String prefix = "[Record first] ";
			Record record = result.get(0);
			assertEquals("ala", record.getPrimaryKey(), prefix + "Primary key is different");
			assertEquals("ma", record.getName(), prefix + "Name is different");
			assertEquals("kota", record.getDescription(), prefix + "Description is different");
			assertEquals(1, record.getUpdatedTimestamp(), prefix + "Timestamp is different");

			prefix = "[Record second] ";
			record = result.get(1);
			assertEquals("ola", record.getPrimaryKey(), prefix + "Primary key is different");
			assertEquals("a", record.getName(), prefix + "Name is different");
			assertEquals("d", record.getDescription(), prefix + "Description is different");
			assertEquals(2, record.getUpdatedTimestamp(), prefix + "Timestamp is different");

		} catch (IOException | RecordsUploaderFileParseException exc) {
			fail(exc);
		}
	}

	@Test
	void testTooShortHeader() {
		try (InputStream fileInputStream = ConverterTest.class.getResourceAsStream("incorrectHeaderLength.txt");
				BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));) {

			RecordsUploader.tryRetrieveRecords(reader);
			fail("Should throw exception as header does not have the description field");

		} catch (RecordsUploaderFileParseException exc) {
			assertEquals("Header does not contains enough fields. Expected 4, received 3", exc.getMessage());
		} catch(IOException exc) {
			fail(exc);
		}
	}

	@Test
	void testIncorrectHeaderField() {
		try (InputStream fileInputStream = ConverterTest.class.getResourceAsStream("incorrectHeaderFieldName.txt");
				BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));) {

			RecordsUploader.tryRetrieveRecords(reader);
			fail("Should throw exception as header's description field is incorrectly named.");

		} catch (RecordsUploaderFileParseException exc) {
			assertEquals("Header's field at index 2 should be DESCRIPTION but was DES", exc.getMessage());
		} catch(IOException exc) {
			fail(exc);
		}
	}
	
	@Test
	void testNoHeader() {
		try (InputStream fileInputStream = ConverterTest.class.getResourceAsStream("noHeader.txt");
				BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));) {

			RecordsUploader.tryRetrieveRecords(reader);
			fail("Should throw exception as there is no header");

		} catch (RecordsUploaderFileParseException exc) {
			assertEquals("Header's line cannot be empty", exc.getMessage());
		} catch(IOException exc) {
			fail(exc);
		}
	}
	
	@Test
	void testPrimaryKeyEmpty() {
		try (InputStream fileInputStream = ConverterTest.class.getResourceAsStream("primaryKeyEmpty.txt");
				BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));) {

			RecordsUploader.tryRetrieveRecords(reader);
			fail("Should throw exception as first record's primary key is empty");

		} catch (RecordsUploaderFileParseException exc) {
			assertEquals("Line number 2 PRIMARY_KEY is empty but cannot.", exc.getMessage());
		} catch(IOException exc) {
			fail(exc);
		}
	}	
	
	@Test
	void testNoEmptyLineAtTheEndofFile() {
		try (InputStream fileInputStream = ConverterTest.class.getResourceAsStream("noEmptyLine.txt");
				BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));) {

			RecordsUploader.tryRetrieveRecords(reader);
			fail("Should throw exception as first record's primary key is empty");

		} catch (RecordsUploaderFileParseException exc) {
			assertEquals("Last line in the file must be empty but was not.", exc.getMessage());
		} catch(IOException exc) {
			fail(exc);
		}
	}	
	
}
