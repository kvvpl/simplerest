package com.markocki.gson;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class ExceptionAdapter extends TypeAdapter<Exception>{
	@Override
	public void write(JsonWriter out, Exception value) throws IOException {
	      out.beginObject(); 
	      out.name("message"); 
	      out.value(value.getMessage()); 
	      out.endObject(); 	
	}

	@Override
	public Exception read(JsonReader in) throws IOException {
		throw new IOException("Not implemented");
	}
	
}
