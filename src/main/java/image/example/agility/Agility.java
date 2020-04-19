package image.example.agility;

import org.apache.commons.cli.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.glassfish.jersey.client.ClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;

public class Agility {

	private static final String MRKTID = "mrktId";
	private static final String OFFRPERDID = "offrPerdId";
	private static final String SPREADNR = "spreadNr";
	private static final String VEHID = "vehId";

	private static final String URL_ARG = "url";
	private static final String FILE_ARG = "file";
	private static final String DEBUG_ARG = "debug";
	private static final String HELP_ARG = "help";
	private static final String MRKTID_ARG = "market";
	private static final String OFFRPERDID_ARG = "campaign";
	private static final String SPREADNR_ARG = "spread";
	private static final String VEHID_ARG = "vehicle";

	private static final String URL_ARG_DESC = "url of the image web service";
	private static final String HELP_ARG_DESC = "print this message";
	private static final String MRKTID_ARG_DESC = "market id";
	private static final String OFFRPERDID_ARG_DESC = "campaign id";
	private static final String SPREADNR_ARG_DESC = "spread number";
	private static final String VEHID_ARG_DESC = "vehicle id";
	private static final String FILE_ARG_DESC = "output image file (jpeg)";
	private static final String DEBUG_ARG_DESC = "display debug messages and saves the json for the request and response";

	private static final String URL_ARG_S = "u";
	private static final String HELP_ARG_S = "h";
	private static final String MRKTID_ARG_S = "m";
	private static final String OFFRPERDID_ARG_S = "c";
	private static final String SPREADNR_ARG_S = "s";
	private static final String VEHID_ARG_S = "v";
	private static final String FILE_ARG_S = "f";
	private static final String DEBUG_ARG_S = "d";

	private static final String APP_NAME = "Agility";

	private static final Logger LOG = LoggerFactory.getLogger(Agility.class);


	public static void main(String[] args) {

		boolean log = false;


		CommandLineParser parser = new DefaultParser();


		Options options = new Options();

		options.addRequiredOption(URL_ARG_S,URL_ARG,true,URL_ARG_DESC);
		options.addRequiredOption(MRKTID_ARG_S,MRKTID_ARG,true,MRKTID_ARG_DESC);
		options.addRequiredOption(VEHID_ARG_S,VEHID_ARG,true,VEHID_ARG_DESC);
		options.addRequiredOption(SPREADNR_ARG_S,SPREADNR_ARG,true,SPREADNR_ARG_DESC);
		options.addRequiredOption(OFFRPERDID_ARG_S,OFFRPERDID_ARG,true,OFFRPERDID_ARG_DESC);
		options.addOption(HELP_ARG_S,HELP_ARG,false,HELP_ARG_DESC);
		options.addOption(FILE_ARG_S,FILE_ARG,true,FILE_ARG_DESC);
		options.addOption(DEBUG_ARG_S,DEBUG_ARG,false,DEBUG_ARG_DESC);

		String[] parts = null;

		try {
			CommandLine line = parser.parse( options, args );
			log = line.hasOption(DEBUG_ARG);

			if (line.hasOption(HELP_ARG)) {

				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp( APP_NAME, options );

				return;

			}

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

			String output = response.readEntity(String.class);

			parts = output.split(",");


			if (log) LOG.info("Status: "+response.getStatus());

			for (int i=0; i<parts.length; i++) {
				LOG.debug("## IMAGE part"+i+": " + parts[i]);
			}

			if (line.hasOption(FILE_ARG)) {

				try {

					byte[] decodedBytes = Base64.decodeBase64(parts[1].getBytes());

					File outputImage = new File(line.getOptionValue(FILE_ARG));

					FileUtils.writeByteArrayToFile(outputImage, decodedBytes);

					if (log) {
						FileUtils.writeStringToFile(new File(outputImage.getParent() + File.separator + "request.json"), jsonString, "utf-8");
						FileUtils.writeStringToFile(new File(outputImage.getParent() + File.separator + "response.json"), output, "utf-8");

						for (int i=0; i<parts.length; i++) {
							FileUtils.writeStringToFile(new File(outputImage.getParent() + File.separator + "parts"+i+".part"), parts[i], "utf-8");

						}

					}

				} catch (Exception e) {
					if (log) {
						LOG.error("ERROR", e);
					}
				}
			}

		} catch( ParseException exp ) {
			if (log) {
				LOG.error("Unexpected exception:" + exp.getMessage());
			}

			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "Agility", options );
		}


	}

}
