package com.newsloop.newsloop_be.api.controller;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.xspec.S;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newsloop.newsloop_be.api.controller.model.AuthResult;
import com.newsloop.newsloop_be.api.controller.model.InterestsRequest;
import com.newsloop.newsloop_be.api.controller.model.User;
import com.newsloop.newsloop_be.service.UserService;

import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    private static final String DB_URL = "jdbc:mysql://newsloop.cn64cgageebh.us-west-1.rds.amazonaws.com:3306/newsloop";
    private static final String DB_USERNAME = "admin";
    private static final String DB_PASSWORD = "IyeFY1jdK.S1ks";


    private static final String BUCKET_NAME = "nipunakarunarathna";
    private static final String ACCESS_KEY_ID = "AKIAQ3EGVN6Q5NLYYBPX";
    private static final String SECRET_ACCESS_KEY = "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";
    private static final Region REGION = Region.US_WEST_1;


    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/authUser")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> authUser(@RequestParam String userName, @RequestParam String password) {
        AuthResult authResult = userService.authUser(userName, password);
    
        // Logging for debugging purposes
        System.out.println("rest of the data: " + authResult.getS3Url());
        System.out.println("data status: " + authResult.hasUserData());
        System.out.println("user name: " + authResult.getName());
    
        // Create a map to hold the response data
        Map<String, Object> response = new HashMap<>();
        response.put("s3Url", authResult.getS3Url());
        response.put("hasUserData", authResult.hasUserData());
        response.put("name", authResult.getName()); // Add name to the response
    
        // Extract folder name from the S3 URL
        String folderName = authResult.getS3Url().replace("https://" + BUCKET_NAME + ".s3.amazonaws.com/", "");
    
        // Create S3 client
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(ACCESS_KEY_ID, SECRET_ACCESS_KEY);
        S3Client s3 = S3Client.builder()
                .region(REGION)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    
        // List objects in the specified folder
        ListObjectsV2Request listObjectsReqManual = ListObjectsV2Request.builder()
                .bucket(BUCKET_NAME)
                .prefix(folderName)
                .build();
        ListObjectsV2Response listObjResponse = s3.listObjectsV2(listObjectsReqManual);
    
        // Store object URLs in a string array
        List<String> objectUrls = new ArrayList<>();
        for (S3Object s3Object : listObjResponse.contents()) {
            String objectUrl = "https://" + BUCKET_NAME + ".s3.amazonaws.com/" + s3Object.key();
            objectUrls.add(objectUrl);
        }
    
        // Add object URLs to the response map
        response.put("objectUrls", objectUrls.toArray(new String[0]));
    
        // Return the map as the API response
        return ResponseEntity.ok(response);
    }




    @GetMapping("/gauthUser")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> gauthUser(@RequestParam String userName) {
        AuthResult authResult = userService.gauthUser(userName);
    
        // Logging for debugging purposes
        System.out.println("rest of the data: " + authResult.getS3Url());
        System.out.println("data status: " + authResult.hasUserData());
        System.out.println("user name: " + authResult.getName());
    
        // Create a map to hold the response data
        Map<String, Object> response = new HashMap<>();
        response.put("s3Url", authResult.getS3Url());
        response.put("hasUserData", authResult.hasUserData());
        response.put("name", authResult.getName()); // Add name to the response
    
        // Extract folder name from the S3 URL
        String folderName = authResult.getS3Url().replace("https://" + BUCKET_NAME + ".s3.amazonaws.com/", "");
    
        // Create S3 client
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(ACCESS_KEY_ID, SECRET_ACCESS_KEY);
        S3Client s3 = S3Client.builder()
                .region(REGION)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    
        // List objects in the specified folder
        ListObjectsV2Request listObjectsReqManual = ListObjectsV2Request.builder()
                .bucket(BUCKET_NAME)
                .prefix(folderName)
                .build();
        ListObjectsV2Response listObjResponse = s3.listObjectsV2(listObjectsReqManual);
    
        // Store object URLs in a string array
        List<String> objectUrls = new ArrayList<>();
        for (S3Object s3Object : listObjResponse.contents()) {
            String objectUrl = "https://" + BUCKET_NAME + ".s3.amazonaws.com/" + s3Object.key();
            objectUrls.add(objectUrl);
        }
    
        // Add object URLs to the response map
        response.put("objectUrls", objectUrls.toArray(new String[0]));
    
        // Return the map as the API response
        return ResponseEntity.ok(response);
    }










    @PostMapping("/signUp")
    public User signupUser(@RequestParam String userName, @RequestParam String password, @RequestParam String name) {
        return userService.createUser(userName, password, name);
    }




    @PostMapping("/gsignUp")
    public User gsignupUser(@RequestParam String userName, @RequestParam String name) {
        return userService.gcreateUser(userName, name);
    }






    


    // defining below function here for ease of code 

    @PostMapping("/postInterests")
    public String postInterests(@RequestBody InterestsRequest interestsRequest) {
        String email = interestsRequest.getEmail();
        String name = interestsRequest.getName();
        Map<String, InterestsRequest.CategoryDetails> categories = interestsRequest.getCategories();
    
        System.out.println("Email: " + email);
        System.out.println("Name: " + name);
    
        Connection connection = null;
        PreparedStatement preparedStatement = null;
    
        try {
            // Establish connection
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    
            // Delete existing records with the same email
            String deleteSql = "DELETE FROM user_data WHERE email = ?";
            preparedStatement = connection.prepareStatement(deleteSql);
            preparedStatement.setString(1, email);
            preparedStatement.executeUpdate();
            preparedStatement.close();
    
            // Prepare SQL statement for insertion
            String insertSql = "INSERT INTO user_data (name, email, interests_1, interests_2, interests_3) VALUES (?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(insertSql);
    
            for (Map.Entry<String, InterestsRequest.CategoryDetails> entry : categories.entrySet()) {
                System.out.println("Category: " + entry.getKey());
                System.out.println("Country: " + entry.getValue().getCountry());
                System.out.println("Subcategories: ");
                for (String subcategory : entry.getValue().getSubcategories()) {
                    System.out.println(" - " + subcategory);
    
                    // Set parameters and execute SQL
                    preparedStatement.setString(1, name);
                    preparedStatement.setString(2, email);
                    preparedStatement.setString(3, entry.getKey()); // Category
                    preparedStatement.setString(4, subcategory);   // Subcategory
                    preparedStatement.setString(5, entry.getValue().getCountry()); // Country
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error processing request";
        } finally {
            // Close resources
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    
        return "Processed successfully";
    }
    

    @GetMapping("/getInterests")
    public ResponseEntity<?> getInterests(@RequestParam String email) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
    
        try {
            // Establish connection
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    
            // Prepare SQL statement
            String sql = "SELECT name, email, interests_1, interests_2, interests_3 FROM user_data WHERE email = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, email);
    
            // Execute query
            resultSet = preparedStatement.executeQuery();
    
            // Process result set
            Map<String, Object> responseMap = new HashMap<>();
            Map<String, Map<String, List<String>>> userMap = new HashMap<>();
            String name = "";
            String emailResult = "";
    
            while (resultSet.next()) {
                name = resultSet.getString("name");
                emailResult = resultSet.getString("email");
                String category = resultSet.getString("interests_1");
                String subcategory = resultSet.getString("interests_2");
                String country = resultSet.getString("interests_3");
    
                userMap.putIfAbsent(category, new HashMap<>());
                userMap.get(category).putIfAbsent("subcategories", new ArrayList<>());
                userMap.get(category).putIfAbsent("countries", new ArrayList<>());
    
                userMap.get(category).get("subcategories").add(subcategory);
                userMap.get(category).get("countries").add(country);
            }
    
            responseMap.put("name", name);
            responseMap.put("email", emailResult);
            responseMap.put("interests", userMap);
    
            // Convert responseMap to JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(responseMap);
    
            return ResponseEntity.ok(jsonResponse);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving data");
        } finally {
            // Close resources
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @GetMapping("/getcategoris")
    public static String[] getAllCategoryNames() {
        List<String> categories = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // Establish connection
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            // Create statement
            statement = connection.createStatement();
            // Execute query
            resultSet = statement.executeQuery("SELECT name FROM categories");

            // Process results
            while (resultSet.next()) {
                categories.add(resultSet.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Convert list to array
        return categories.toArray(new String[0]);
    }

    public static void main(String[] args) {
        String[] categoryNames = getAllCategoryNames();
        for (String name : categoryNames) {
            System.out.println(name);
        }
    }


    @GetMapping("/getsubcategories")
    public static String[] getSubcategories(@RequestParam String category) {
        List<String> subcategories = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // Establish connection
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            // Create statement
            statement = connection.createStatement();
            // Execute query
            String query = "SELECT subcategory_name FROM subcategories WHERE category_name = '" + category + "'";
            resultSet = statement.executeQuery(query);

            // Process results
            while (resultSet.next()) {
                subcategories.add(resultSet.getString("subcategory_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Convert list to array
        return subcategories.toArray(new String[0]);
    }


    @GetMapping("/countries")
    public List<String> getAllCountryNames() {
        List<String> countryNames = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // Establish connection
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            statement = connection.createStatement();

            // Execute query
            resultSet = statement.executeQuery("SELECT country_name FROM countries");

            // Process result set
            while (resultSet.next()) {
                countryNames.add(resultSet.getString("country_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return countryNames;
    }


    @PostMapping("/timeUp")
    public String setTime(@RequestParam String email, @RequestParam String time) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Establish the connection
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

            // Check if the current value for time exists for the given email
            String checkSQL = "SELECT time FROM user WHERE email = ?";
            preparedStatement = connection.prepareStatement(checkSQL);
            preparedStatement.setString(1, email);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String currentTime = resultSet.getString("time");
                if (currentTime != null && !currentTime.isEmpty()) {
                    // Delete the current value for time
                    String deleteSQL = "UPDATE user SET time = NULL WHERE email = ?";
                    preparedStatement = connection.prepareStatement(deleteSQL);
                    preparedStatement.setString(1, email);
                    preparedStatement.executeUpdate();
                }
            }

            // Create the SQL update statement
            String updateSQL = "UPDATE user SET time = ? WHERE email = ?";
            preparedStatement = connection.prepareStatement(updateSQL);
            preparedStatement.setString(1, time);
            preparedStatement.setString(2, email);

            // Execute the update
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                return "Time set successfully";
            } else {
                return "Failed to set time: user not found";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Failed to set time: " + e.getMessage();
        } finally {
            // Close the resources
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @PostMapping("/generatenow")
    public String generate_email(@RequestParam String email) {
         try {
            // AWS credentials
            BasicAWSCredentials awsCreds = new BasicAWSCredentials("AKIAQ3EGVN6Q5NLYYBPX", "L63qSMzz0TXpMmw5MtasJUVH6mpBmBYMyN1fG2rJ");

            // Create AWS Lambda client
            AWSLambda awsLambda = AWSLambdaClientBuilder.standard()
                                .withRegion(Regions.US_WEST_1)
                                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                                .build();

            // Create request payload
            ObjectMapper objectMapper = new ObjectMapper();
            String payload = objectMapper.writeValueAsString(new LambdaInput(email));

            // Create InvokeRequest
            InvokeRequest invokeRequest = new InvokeRequest()
                    .withFunctionName("generate_on_call")
                    .withPayload(payload);

            // Invoke Lambda function
            InvokeResult invokeResult = awsLambda.invoke(invokeRequest);

            // Get response
            String response = new String(invokeResult.getPayload().array(), StandardCharsets.UTF_8);
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return "Error invoking Lambda function";
        }
    }


    private static class LambdaInput {
        private String email;

        public LambdaInput(String email) {
            this.email = email;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}

