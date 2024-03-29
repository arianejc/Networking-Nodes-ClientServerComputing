// Ariane Correa
// ajcorrea

package com.example.project2task5;

import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

// Reference: EchoServerUDP.java from Coulouris text (https://github.com/CMU-Heinz-95702/Project-2-Client-Server)
// Reference: EchoServerTCP.java from Coulouris text (https://github.com/CMU-Heinz-95702/Project-2-Client-Server)

public class VerifyingServerTCP {
    // Map to store user IDs and their corresponding values
    static Map<String, Integer> UserID_Value_Map = new TreeMap<>();
    static Socket clientSocket = null; // Declare and Initialize Client Socket
    static BigInteger e; // e is the exponent of the public key
    static BigInteger n; // n is the modulus for both the private and public keys
    static String operation;
    static String value;
    static String userId;

    public static void main(String args[]) {

        System.out.println("The TCP server is running.");

        try {

            Scanner sc = new Scanner(System.in);
            System.out.println("Provide port number server should listen on");
            int serverPort = sc.nextInt();  // Read the port number from the user.
            ServerSocket listenSocket = new ServerSocket(serverPort); //Initialize Server Socket

            /*
             * Block waiting for a new connection request from a client.
             * When the request is received, "accept" it, and the rest
             * the tcp protocol handshake will then take place, making
             * the socket ready for reading and writing.
             */
            clientSocket = listenSocket.accept();
            // If we get here, then we are now connected to a client.

            // Set up "in" to read from the client socket
            Scanner in;
            in = new Scanner(clientSocket.getInputStream());

            // Set up "out" to write to the client socket
            PrintWriter out;
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));

            /*
             * Forever,
             *   read a line from the socket
             *   print it to the console
             *   echo it (i.e. write it) back to the client
             */

            while (true) {
                if (in.hasNextLine()) {
                    String requestString = in.nextLine();

                    if (verify(requestString)) {

                        System.out.println("Signature is verified");

                        System.out.println("User " + userId + " requested " + operation + " operation.");

                        // Initialize integer variable that will store the updated value
                        // after the operation is performed on value
                        int n = 0;

                        // If the operation selected is Addition
                        if (operation.equalsIgnoreCase("add")) {
                            n = add(userId, Integer.parseInt(value));
                        }
                        // If the operation selected is Subtraction
                        else if (operation.equalsIgnoreCase("subtract")) {
                            n = subtract(userId, Integer.parseInt(value));
                        }
                        // If the operation selected is Get
                        else if (operation.equalsIgnoreCase("get")) {
                            n = get(userId);
                        }

                        // Print to Console
                        System.out.println("Returning " + n + " to client");
                        out.println(n);
                        out.flush();
                    } else {
                        out.println("Error in request");
                        out.flush();
                    }
                }
            }
        }
        catch (SocketException e) {
            // Handle Socket Exception by displaying an error message
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            // Handle IO Exception by displaying an error message
            System.out.println("IO Exception: " + e.getMessage());
        } catch (NoSuchAlgorithmException ex) {
            // Handle Runtime Exception by displaying an error message
            throw new RuntimeException(ex);
        } finally {
            try {
                // Ensure that the clientSocket is closed, if it's not null
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                // Handle any potential IOException that may occur when closing the socket
                // This is typically ignored, as the main goal is to ensure the socket is closed
            }
        }
    }

    // Adds value received to the specific user
    public static int add(String userId, int n) {
        int result;
        if (UserID_Value_Map.containsKey(userId)) {
            result = UserID_Value_Map.get(userId) + n;
            UserID_Value_Map.put(userId, result);
        }
        else {
            result = n;
            UserID_Value_Map.put(userId, n);
        }
        return result;
    }

    // Subtracts value received from the specific user
    public static int subtract(String userId, int n) {
        int result;
        if (UserID_Value_Map.containsKey(userId)) {
            result = UserID_Value_Map.get(userId) - n;
            UserID_Value_Map.put(userId, result);
        }
        else {
            result = n;
            UserID_Value_Map.put(userId, n);
        }
        return result;
    }

    //  Gets value received from the specific user
    public static int get(String userId) {
        int result;
        if (UserID_Value_Map.containsKey(userId)) {
            result = UserID_Value_Map.get(userId);
        }
        else {
            result = 0;
            UserID_Value_Map.put(userId, 0);
        }
        return result;
    }

    // Method to verify if encryption was done correctly @throws NoSuchAlgorithmException
    // @throws UnsupportedEncodingException
    public static boolean verify(String message) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        boolean verify = true;

        // Fetch different values from the string
        String id = message.split(",")[0];
        String publicKey = message.split(",")[1];
        operation = message.split(",")[2];
        value = message.split(",")[3];
        String sign = message.split(",")[4];


        // Fetch the public and modulus , public key is first 6 characters
        e = new BigInteger(publicKey.substring(0, 5));
        n = new BigInteger(publicKey.substring(5));

        System.out.println("public key recieved from client " + e+n);

        // Validate id
        String validId = validateId(publicKey);

        if (!validId.equals(id))
            verify = false;

        // Recreate message
        String messageToCheck = id + "," + String.valueOf(e) + String.valueOf(n) + "," + operation + "," + value;

        // Verify if signature is valid
        if (!sign(messageToCheck, sign))
            verify = false;

        if (verify)
            userId = validId;

        return verify;
    }

    // Method to convert bytes to hex
    // Reference Code: https://mkyong.com/java/java-how-to-convert-bytes-to-hex/
    public static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();

        // Step 1: Iterate through each byte in the 'bytes' array.
        for (byte aByte : bytes) {

            // Step 2: Convert the byte to an integer and apply a bitwise AND operation with 0xff.
            int decimal = (int) aByte & 0xff; // This ensures that the integer is non-negative.

            // Step 3: Convert the decimal value to a hexadecimal string.
            String hex = Integer.toHexString(decimal);

            // Step 4: Check if the hex string has an odd length, and if so, pad it with a leading '0'.
            if (hex.length() % 2 == 1) {
                hex = "0" + hex;
            }

            // Step 5: Append the hexadecimal value to the result.
            result.append(hex);
        }

        // Step 6: Return the concatenated hexadecimal string representation of the bytes.
        return result.toString();
    }

    // Method to validate the user id
    public static String validateId(String key) throws NoSuchAlgorithmException {
        // Step 1: Create a MessageDigest instance using SHA-256 hashing algorithm.
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        // Step 2: Generate a hash of the input 'key'.
        byte[] hashBytes = digest.digest(key.getBytes());

        // Step 3: Create a new byte array to store the final 20 bytes of the hash.
        byte[] finalBytes = new byte[20];

        // Step 4: Copy the least significant 20 bytes of the hash into the final array.
        // This is done to ensure that the output is a 20-byte (160-bit) hash.
        System.arraycopy(hashBytes, hashBytes.length - 20, finalBytes, 0, 20);

        // Step 5: Convert the byte array to a hexadecimal string representation.
        return bytesToHex(finalBytes);
    }

    // Method to check whether signed message is valid
    // Reference Code : ShortMessageVerify.java (https://github.com/CMU-Heinz-95702/Project-2-Client-Server)
    public static boolean sign(String messageToCheck, String encryptedHashStr) throws
            UnsupportedEncodingException, NoSuchAlgorithmException {

        // Take the encrypted string and make it a big integer
        BigInteger encryptedHash = new BigInteger(encryptedHashStr);

        // Decrypt the encryptedHash to compute a decryptedHash
        BigInteger decryptedHash = encryptedHash.modPow(e, n);

        // Get the bytes from messageToCheck
        byte[] bytesOfMessageToCheck = messageToCheck.getBytes("UTF-8");

        // Hash the messageToCheck using SHA-256 (be sure to handle the extra byte as described in the signing method.)
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] messageToCheckDigest = md.digest(bytesOfMessageToCheck);
        messageToCheckDigest[0] = 0;

        // Make it a big int
        BigInteger bigIntegerToCheck = new BigInteger(messageToCheckDigest);

        // If this new hash is equal to the decryptedHash, return true else false.
        if (bigIntegerToCheck.compareTo(decryptedHash) == 0) {
            return true;
        }
        else {
            return false;
        }
    }
}
