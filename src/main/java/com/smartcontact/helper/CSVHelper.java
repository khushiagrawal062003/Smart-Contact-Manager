package com.smartcontact.helper;

import com.smartcontact.entity.Contact;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CSVHelper {

    public static final String TYPE = "text/csv";

    // Convert list of contacts to CSV string
    public static String contactsToCSV(List<Contact> contacts) {
        StringBuilder sb = new StringBuilder();
        // CSV Header
        sb.append("Name,Nickname,Company,Email,Phone,Category,Favorite,Address,Notes\n");
        for (Contact contact : contacts) {
            sb.append(escapeCSVField(contact.getName())).append(",")
              .append(escapeCSVField(contact.getSecondName())).append(",")
              .append(escapeCSVField(contact.getWork())).append(",")
              .append(escapeCSVField(contact.getEmail())).append(",")
              .append(escapeCSVField(contact.getPhone())).append(",")
              .append(escapeCSVField(contact.getCategory())).append(",")
              .append(contact.isFavorite()).append(",")
              .append(escapeCSVField(contact.getAddress())).append(",")
              .append(escapeCSVField(contact.getDescription())).append("\n");
        }
        return sb.toString();
    }

    // Parse CSV input stream into a list of Contacts
    public static List<Contact> csvToContacts(InputStream is) {
        List<Contact> contacts = new ArrayList<>();
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line = fileReader.readLine(); // skip header line
            while ((line = fileReader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                List<String> values = parseCSVLine(line);
                if (values.isEmpty()) {
                    continue;
                }
                
                Contact contact = new Contact();
                contact.setName(values.get(0));
                contact.setSecondName(values.size() > 1 ? values.get(1) : "");
                contact.setWork(values.size() > 2 ? values.get(2) : "");
                contact.setEmail(values.size() > 3 ? values.get(3) : "");
                contact.setPhone(values.size() > 4 ? values.get(4) : "");
                
                // Fallback default for Category if missing
                String category = (values.size() > 5 && !values.get(5).isEmpty()) ? values.get(5) : "Personal";
                contact.setCategory(category);
                
                // Parse Favorite Boolean
                boolean favorite = false;
                if (values.size() > 6 && !values.get(6).isEmpty()) {
                    String favStr = values.get(6);
                    favorite = favStr.equalsIgnoreCase("true") || favStr.equals("1") || favStr.equalsIgnoreCase("yes");
                }
                contact.setFavorite(favorite);
                
                contact.setAddress(values.size() > 7 ? values.get(7) : "");
                contact.setDescription(values.size() > 8 ? values.get(8) : "");
                contact.setImage("contact_default.png"); // Set default image on import
                
                contacts.add(contact);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse CSV file: " + e.getMessage(), e);
        }
        return contacts;
    }

    private static String escapeCSVField(String data) {
        if (data == null) {
            return "";
        }
        String escapedData = data.replaceAll("\\R", " "); // replace line breaks with space
        if (escapedData.contains(",") || escapedData.contains("\"") || escapedData.contains("'")) {
            escapedData = escapedData.replace("\"", "\"\"");
            escapedData = "\"" + escapedData + "\"";
        }
        return escapedData;
    }

    private static List<String> parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder currentField = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(currentField.toString().trim());
                currentField.setLength(0);
            } else {
                currentField.append(c);
            }
        }
        result.add(currentField.toString().trim());
        return result;
    }
}
