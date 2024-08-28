package jsonPackage;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;


import java.io.File;
import java.io.IOException;
import java.security.SecureRandom; 

import java.util.Base64;

public class App 
{
	
	public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: JsonProcessor <prnNumber> <jsonPath>");
            return;
        }

        String prnNumber = args[0].toLowerCase();
        String jsonPath = args[1];

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readValue(new File(jsonPath), JsonNode.class);

            String destinationValue = findDestinationValue(rootNode);
            if (destinationValue == null) {
                System.err.println("Destination key not found in JSON.");
                return;
            }

            String randomSalt = generateRandomSalt();
            String concatenatedString = prnNumber + destinationValue + randomSalt;
            String md5Hash = DigestUtils.md5Hex(concatenatedString);

            System.out.println(md5Hash + ";" + randomSalt);
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String findDestinationValue(JsonNode node) {
        if (node.has("destination")) {
            return node.get("destination").textValue();
        }

        for (JsonNode child : node) {
            String result = findDestinationValue(child);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    private static String generateRandomSalt() {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[8];
        random.nextBytes(saltBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(saltBytes);
    }

}
