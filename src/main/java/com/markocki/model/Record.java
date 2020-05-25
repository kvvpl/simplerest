package com.markocki.model;

public interface Record {
	public String getPrimaryKey(); 
	public String getName() ;
	public String getDescription() ;
	public long getUpdatedTimestamp() ;
}
