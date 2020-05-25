package com.markocki.web;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.markocki.model.Record;
import com.markocki.model.RecordCreateException;
import com.markocki.model.RecordFactory;
import com.markocki.storage.NoRecordFoundException;
import com.markocki.storage.RecordStoreException;
import com.markocki.storage.Storage;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.utils.StringUtils;

public class FrontControllerTest {
	Storage createTestStorage() throws RecordCreateException {
		Record recordToBeFound = RecordFactory.createRecord("key", "name", "description", System.currentTimeMillis());

		return new Storage() {

			@Override
			public Record findByPrimaryKey(String primaryKey) throws NoRecordFoundException {
				if (recordToBeFound.getPrimaryKey().equals(primaryKey)) {
					return recordToBeFound;
				} else {
					throw new NoRecordFoundException(primaryKey);
				}
			}

			@Override
			public Record delete(Record recordToDelete) throws NoRecordFoundException {
				if (recordToBeFound.getPrimaryKey().equals(recordToBeFound.getPrimaryKey())) {
					return recordToBeFound;
				} else {
					throw new NoRecordFoundException(recordToBeFound.getPrimaryKey());
				}
			}

			@Override
			public void save(Record recordToStore) throws RecordStoreException {
			}

		};
	}

	
	
	@Test
	public void testEcho() throws Exception {
		FrontController fc = new FrontController(createTestStorage(), "");
		Route echoRoute = fc.echoRoute();
		Request request = new Request() {
		    public String body() {
		        return "";
		    }
		};
		
		Response response = new Response() {
		    String body = null;
		    
			public void status(int statusCode) {
		    }
		    
		    public void body(String body) {
		        this.body = body;
		    }
		    
		    public String body() {
		        return this.body;
		    }		    
		};

		echoRoute.handle(request,response );
		
		assertEquals(true, response.body().contains("It works"));
	}
	
	@Test
	public void testSuccessfulDelete() throws Exception {
		FrontController fc = new FrontController(createTestStorage(), "");
		Route echoRoute = fc.delete();
		Request request = new Request() {
		    public String body() {
		        return "";
		    }
		    
		    public String params(String param) {
	            return "key";
		    }
		};
		
		Response response = new Response() {
		    String body = null;
		    int status;
		    
			public void status(int statusCode) {
				this.status = statusCode;
			}
		    public int status() {
		        return this.status;
		    }		    
		    public void body(String body) {
		        this.body = body;
		    }
		    
		    public String body() {
		        return this.body;
		    }		    
		};

		echoRoute.handle(request,response );
		
		assertEquals(true, response.body().contains("Record deleted for PRIMARY_KEY"));
		assertEquals(200, response.status());

	}	

	@Test
	public void testUnSuccessfulDelete() throws Exception {
		FrontController fc = new FrontController(createTestStorage(), "");
		Route echoRoute = fc.delete();
		Request request = new Request() {
		    public String body() {
		        return "";
		    }
		    
		    public String params(String param) {
	            return "keNotToBeFoundy";
		    }
		};
		
		Response response = new Response() {
		    String body = null;
		    int status;
		    
			public void status(int statusCode) {
				this.status = statusCode;
			}
		    public int status() {
		        return this.status;
		    }
		    public void body(String body) {
		        this.body = body;
		    }
		    
		    public String body() {
		        return this.body;
		    }		    
		};

		echoRoute.handle(request,response );
		
		assertEquals(true, response.body().contains("keNotToBeFoundy"));
		assertEquals(400, response.status());

	}	
	
	
	@Test
	public void testSuccessfulGet() throws Exception {
		FrontController fc = new FrontController(createTestStorage(), "");
		Route echoRoute = fc.get();
		Request request = new Request() {
		    public String body() {
		        return "";
		    }
		    
		    public String params(String param) {
	            return "key";
		    }
		};
		
		Response response = new Response() {
		    String body = null;
		    int status;
		    
			public void status(int statusCode) {
				this.status = statusCode;
			}
		    public int status() {
		        return this.status;
		    }
		    public void body(String body) {
		        this.body = body;
		    }
		    
		    public String body() {
		        return this.body;
		    }		    
		};

		echoRoute.handle(request,response );
		
		assertEquals(true, response.body().contains("key"));
		assertEquals(200, response.status());
	}		
	
	
	@Test
	public void testUnSuccessfulGet() throws Exception {
		FrontController fc = new FrontController(createTestStorage(), "");
		Route echoRoute = fc.get();
		Request request = new Request() {
		    public String body() {
		        return "";
		    }
		    
		    public String params(String param) {
	            return "keNotToBeFoundy";
		    }
		};
		
		Response response = new Response() {
		    String body = null;
		    int status;
		    
			public void status(int statusCode) {
				this.status = statusCode;
			}
		    public int status() {
		        return this.status;
		    }
		    public void body(String body) {
		        this.body = body;
		    }
		    
		    public String body() {
		        return this.body;
		    }		    
		};

		echoRoute.handle(request,response );
		
		assertEquals(true, response.body().contains("keNotToBeFoundy"));
		assertEquals(400, response.status());
	}		
	
	
}
