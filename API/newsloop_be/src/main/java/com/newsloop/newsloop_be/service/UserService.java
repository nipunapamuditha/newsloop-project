package com.newsloop.newsloop_be.service;

import org.springframework.stereotype.Service;
//import org.springframework.web.bind.annotation.RequestBody;

import com.newsloop.newsloop_be.api.controller.model.AuthResult;
import com.newsloop.newsloop_be.api.controller.model.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import software.amazon.awssdk.regions.Region;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import software.amazon.awssdk.core.sync.RequestBody;







@Service
public class UserService {

    private static final String DB_URL = "jdbc:mysql://newsloop.cn64cgageebh.us-west-1.rds.amazonaws.com:3306/newsloop";
    private static final String DB_USERNAME = "admin";
    private static final String DB_PASSWORD = "IyeFY1jdK.S1ks";



    private static final String BUCKET_NAME = "nipunakarunarathna";
    private static final String ACCESS_KEY_ID = "AKIAQ3EGVN6Q5NLYYBPX";
    private static final String SECRET_ACCESS_KEY = "L63qSMzz0TXpMmw5MtasJUVH6mpBmBYMyN1fG2rJ";
    private static final Region REGION = Region.US_WEST_1; // Change to your bucket's region
   

    public AuthResult authUser(String userName, String password) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
    
        System.out.println("Authenticating user: " + userName);
    
        try {
            // Establish connection to the database
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    
            // Prepare SQL query to verify user and fetch s3_url
            String verifySql = "SELECT email, s3_url, name FROM user WHERE email = ? AND password = ?";
            preparedStatement = connection.prepareStatement(verifySql);
            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, password);
    
            // Execute query
            resultSet = preparedStatement.executeQuery();
            String vname = null;
    
            // Check if user exists
            if (resultSet.next()) {
                String email = resultSet.getString("email");
                String s3_url = resultSet.getString("s3_url");
                vname = resultSet.getString("name");
                System.out.println("User authenticated: " + email);
    
                // Prepare SQL query to fetch user data
                String dataSql = "SELECT id, name, email, interests_1, interests_2, interests_3 FROM user_data WHERE email = ?";
                preparedStatement = connection.prepareStatement(dataSql);
                preparedStatement.setString(1, email);
    
                // Execute query
                resultSet = preparedStatement.executeQuery();
    
                // Check if user data exists
                String mydata = "nothing"; // Initialize mydata with an empty string
                String name = null; // Initialize name
                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    name = vname;
                    ArrayList<String> interests_1 = new ArrayList<>();
                    interests_1.add(resultSet.getString("interests_1"));
                    ArrayList<String> interests_2 = new ArrayList<>();
                    interests_2.add(resultSet.getString("interests_2"));
                    ArrayList<String> interests_3 = new ArrayList<>();
                    interests_3.add(resultSet.getString("interests_3"));
                    mydata = "something";
    
                    // Print the details of the User object
                    System.out.println("User authenticated: " + email);
                    System.out.println("ID: " + id);
                    System.out.println("Name: " + name);
                    System.out.println("Email: " + email);
                    System.out.println("Interests 1: " + interests_1.toString());
                    System.out.println("Interests 2: " + interests_2.toString());
                    System.out.println("Interests 3: " + interests_3.toString());
                    System.out.println("S3 URL: " + s3_url);
                }
                System.out.println("User data status: " + mydata);
                return new AuthResult(s3_url, mydata, name); // Return name
            } else {
                System.out.println("Authentication failed: User not found");
                return new AuthResult(null, "false", null); // Authentication failed
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Authentication failed: SQL Exception");
            return new AuthResult(null, "false", null); // Authentication failed due to exception
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


    public AuthResult gauthUser(String userName) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
    
        System.out.println("Authenticating user: " + userName);

        String vname = null;
    
        try {
            // Establish connection to the database
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    
            // Prepare SQL query to verify user and fetch s3_url
            String verifySql = "SELECT email, s3_url, name FROM user WHERE email = ?";
            preparedStatement = connection.prepareStatement(verifySql);
            preparedStatement.setString(1, userName);
           // preparedStatement.setString(3, vname);
            
    
            // Execute query
            resultSet = preparedStatement.executeQuery();
    
            // Check if user exists
            if (resultSet.next()) {
                String email = resultSet.getString("email");
                String s3_url = resultSet.getString("s3_url");
                vname = resultSet.getString("name");
                System.out.println("User authenticated: " + email);
    
                // Prepare SQL query to fetch user data
                String dataSql = "SELECT id, name, email, interests_1, interests_2, interests_3 FROM user_data WHERE email = ?";
                preparedStatement = connection.prepareStatement(dataSql);
                preparedStatement.setString(1, email);
    
                // Execute query
                resultSet = preparedStatement.executeQuery();
    
                // Check if user data exists
                String mydata = "nothing"; // Initialize mydata with an empty string
                String name = null; // Initialize name
                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    name = vname;
                    ArrayList<String> interests_1 = new ArrayList<>();
                    interests_1.add(resultSet.getString("interests_1"));
                    ArrayList<String> interests_2 = new ArrayList<>();
                    interests_2.add(resultSet.getString("interests_2"));
                    ArrayList<String> interests_3 = new ArrayList<>();
                    interests_3.add(resultSet.getString("interests_3"));
                    mydata = "something";
    
                    // Print the details of the User object
                    System.out.println("User authenticated: " + email);
                    System.out.println("ID: " + id);
                    System.out.println("Name: " + name);
                    System.out.println("Email: " + email);
                    System.out.println("Interests 1: " + interests_1.toString());
                    System.out.println("Interests 2: " + interests_2.toString());
                    System.out.println("Interests 3: " + interests_3.toString());
                    System.out.println("S3 URL: " + s3_url);
                }
                System.out.println("User data status: " + mydata);
                return new AuthResult(s3_url, mydata, name); // Return name
            } else {
                System.out.println("Authentication failed: User not found");
                return new AuthResult(null, "false", null); // Authentication failed
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Authentication failed: SQL Exception");
            return new AuthResult(null, "false", null); // Authentication failed due to exception
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




    public User createUser(String userName, String password, String name) {

        System.out.println("Creating user: " + userName);
        System.out.println("Creating user: " + password);
        System.out.println("Creating user: " + name);
    
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
    
        try {
            // Establish connection to the database
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    
            // Check if user already exists
            String checkUserSql = "SELECT id FROM user WHERE email = ?";
            preparedStatement = connection.prepareStatement(checkUserSql);
            preparedStatement.setString(1, userName);
            resultSet = preparedStatement.executeQuery();
    
            if (resultSet.next()) {
                // User already exists
                return null;
            }
    
            // Initialize S3 client
            S3Client s3 = S3Client.builder()
                    .region(REGION)
                    .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(ACCESS_KEY_ID, SECRET_ACCESS_KEY)))
                    .build();
    
            // Create folder in S3 bucket
            String s3_url = "https://" + BUCKET_NAME + ".s3.amazonaws.com/" + userName + "/";
            try {
                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket(BUCKET_NAME)
                        .key(userName + "/") // S3 folder creation
                        .build();
                s3.putObject(putObjectRequest, RequestBody.empty());
            } catch (S3Exception e) {
                e.printStackTrace();
                return null; // Folder creation failed
            }
    
            // Prepare SQL query to insert new user
            String sql = "INSERT INTO user (email, password, name, s3_url) VALUES (?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, name);
            preparedStatement.setString(4, s3_url);
    
            // Execute update 
            int affectedRows = preparedStatement.executeUpdate();
    
            if (affectedRows > 0) {
                // Get the generated user ID
                resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    int id = resultSet.getInt(1);
    
                    // Create and return User object
                    return new User(id, name, userName, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), s3_url);
                }
            }
            return null; // User creation failed
        } catch (SQLException e) {
            e.printStackTrace();
            return null; // User creation failed due to exception
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



    public User gcreateUser(String userName, String name) {

        System.out.println("Creating user: " + userName);
        
        System.out.println("Creating user: " + name);
    
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
    
        try {
            // Establish connection to the database
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    
            // Check if user already exists
            String checkUserSql = "SELECT id FROM user WHERE email = ?";
            preparedStatement = connection.prepareStatement(checkUserSql);
            preparedStatement.setString(1, userName);
            resultSet = preparedStatement.executeQuery();
    
            if (resultSet.next()) {
                // User already exists
                return null;
            }
    
            // Initialize S3 client
            S3Client s3 = S3Client.builder()
                    .region(REGION)
                    .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(ACCESS_KEY_ID, SECRET_ACCESS_KEY)))
                    .build();
    
            // Create folder in S3 bucket
            String s3_url = "https://" + BUCKET_NAME + ".s3.amazonaws.com/" + userName + "/";
            try {
                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket(BUCKET_NAME)
                        .key(userName + "/") // S3 folder creation
                        .build();
                s3.putObject(putObjectRequest, RequestBody.empty());
            } catch (S3Exception e) {
                e.printStackTrace();
                return null; // Folder creation failed
            }
    
            // Prepare SQL query to insert new user
            String sql = "INSERT INTO user (email, name, s3_url) VALUES (?, ?, ?)";
            preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, userName);
            
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, s3_url);
    
            // Execute update 
            int affectedRows = preparedStatement.executeUpdate();
    
            if (affectedRows > 0) {
                // Get the generated user ID
                resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    int id = resultSet.getInt(1);
    
                    // Create and return User object
                    return new User(id, name, userName, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), s3_url);
                }
            }
            return null; // User creation failed
        } catch (SQLException e) {
            e.printStackTrace();
            return null; // User creation failed due to exception
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

}