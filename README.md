# agility-image-example

Most important part of the JAVA client:

creating the client and preparing the json for the request, the media type is _APPLICATION_JSON_:

```java
Client client = ClientBuilder.newClient( new ClientConfig() );
WebTarget webTarget = client.target(line.getOptionValue(URL_ARG));

String jsonString = "{"
					+"\""+MRKTID+"\":\""+line.getOptionValue(MRKTID_ARG)
					+"\",\""+OFFRPERDID+"\":"+line.getOptionValue(OFFRPERDID_ARG)+","
					+"\""+SPREADNR+"\":"+line.getOptionValue(SPREADNR_ARG)+","
					+"\""+VEHID+"\":\""+line.getOptionValue(VEHID_ARG)+"\""
					+"}";

Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);
Response response = invocationBuilder.post(Entity.json(jsonString));
```

The response is a json with 3 things in it:

```java
String output = response.readEntity(String.class);

parts = output.split(",");


if (log) LOG.info("Status: "+response.getStatus());

for (int i=0; i<parts.length; i++) {
	LOG.debug("## IMAGE part"+i+": " + parts[i]);
}
```

we need the second part _parts[1]_

```java
...
byte[] decodedBytes = Base64.decodeBase64(parts[1].getBytes());

File outputImage = new File(line.getOptionValue(FILE_ARG));

FileUtils.writeByteArrayToFile(outputImage, decodedBytes);
...
```
