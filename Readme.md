
# Welcome to a simple REST server

## Simple REST Server - Description

Simple REST Server is a trivial web application that can be used to upload
files containing predefined structure of records which later can be retrieved or deleted.
The uploaded records are being persisted to a simple storage during application shutdown
and retrieved (if available) during application startup.


## How to build

Simply run 'mvn package' and the jar that can be run will be inside target sub-directory.


## How to run

Run 'java -jar target/simple-rest-1.0-jar-with-dependencies.jar'

or

Run 'mvn exec:java'

## Supported options
-Dport=12345 - the port the server listens on, by default (if not set) it is 8080
-Dapp.storage.dir=/some/directory - the directory where the uploaded records are going to be stored
and retrieved from (default same as java.io.tmpdir)


## How to use the application

GET /echo - checks if the application is up and running
GET /record/{primary_key} - retrieves the record for given primary key
DELETE /record/{primary_key} - deletes the record for given primary key
POST /upload - uploads a given comma-separated, 4-field-wide file. For successful upload 
the encoding (Content-Type) must be set to 'multipart/form-data' and the file must be uploaded as the part of name 'file'
One can have a look at /upload.html for the sample upload page.

All response returns HTTP status code (200, 400 or 500) together with JSON response which contains 
'message' field with detailed information.

## Assumption made

1. Internalization - is omitted for brevity.
2. The application uses as much as possible JDK provided library, thus e.g. Java serialization 
instead of more sophisticated gRPS for record serialization.
3. Upload performs strict validation which includes the header, i.e., fields count and names, 
the records, i.e., number of fields, non-empty primary key, and finally the last line at the end of file 
(there must me a new line that just contains \r (or \n)). Only completely correct files are accepted. 
4. The timestamp (the fourth field in the uploaded file) is considered to be long number, thus conversion from string to long is 
made and only records that fulfil this requirement are accepted.
5. First three fields in the uploaded file are treated as StringS.
6. It is possible for a record to have just primary key without any other fields, but still four-field CSV is required, thus "key,,,"
is possible as the record with just primary key called 'key'. 
When uploading next, new files, the best effort is made, thus all non-conflicting (unique primary key) records are
accepted. All duplicated are ignored. Information if the upload was clean or with duplicates 
is returned after upload is finished. 
7. Empty line is allowed only at the end of file. Placing empty line anywhere else will cause the upload to fail.
8. Each uploaded file must follow the strict validation rules mentioned in point 3. 
9. A simple object-storage is used. The storage file is loaded from the app.storage.dir at the application
startup and persisted during shutdown.
10. Unrecognized HTTP verbs or URI result in an error with 'nothing here' message.