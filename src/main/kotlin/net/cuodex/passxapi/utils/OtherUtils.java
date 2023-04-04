package net.cuodex.passxapi.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import net.cuodex.passxapi.entity.LoginCredential;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class OtherUtils {

    public static String getTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(new Date());
    }

    public static boolean isEmailValid(String email) {
        return email.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
    }

    public static boolean isUsernameValid(String username) {
        return username.matches("^[a-zA-Z0-9_]*$") && username.length() >= 3 && username.length() <= 16;
    }

    public static List<String> readFirst(final Path path, final int numLines) throws IOException, IOException {
        try (final Stream<String> lines = Files.lines(path)) {
            return lines.limit(numLines).collect(toList());
        }
    }

    public static LoginCredential encryptCredential(LoginCredential decryptedCredential) {
        LoginCredential encryptedCredential = new LoginCredential();
        encryptedCredential.setId(decryptedCredential.getId());
        encryptedCredential.setUserAccount(decryptedCredential.getUserAccount());
        encryptedCredential.setTitle(EncryptionUtils.encrypt(decryptedCredential.getTitle()));
        encryptedCredential.setUsername(EncryptionUtils.encrypt(decryptedCredential.getUsername()));
        encryptedCredential.setEmail(EncryptionUtils.encrypt(decryptedCredential.getEmail()));
        encryptedCredential.setPassword(EncryptionUtils.encrypt(decryptedCredential.getPassword()));
        encryptedCredential.setUrl(EncryptionUtils.encrypt(decryptedCredential.getUrl()));
        encryptedCredential.setDescription(EncryptionUtils.encrypt(decryptedCredential.getDescription()));

        return encryptedCredential;
    }

    public static LoginCredential decryptCredential(LoginCredential encryptedCredential) {
        LoginCredential decryptedCredential = new LoginCredential();

        decryptedCredential.setId(encryptedCredential.getId());
        decryptedCredential.setUserAccount(encryptedCredential.getUserAccount());
        decryptedCredential.setTitle(EncryptionUtils.decrypt(encryptedCredential.getTitle()));
        decryptedCredential.setUsername(EncryptionUtils.decrypt(encryptedCredential.getUsername()));
        decryptedCredential.setEmail(EncryptionUtils.decrypt(encryptedCredential.getEmail()));
        decryptedCredential.setPassword(EncryptionUtils.decrypt(encryptedCredential.getPassword()));
        decryptedCredential.setUrl(EncryptionUtils.decrypt(encryptedCredential.getUrl()));
        decryptedCredential.setDescription(EncryptionUtils.decrypt(encryptedCredential.getDescription()));

        return decryptedCredential;
    }

    public static List<String> getSessionIdList(List<PassxUserSession> activeSessions) {
        return activeSessions.stream().map(PassxUserSession::getSessionId).toList();
    }

    public static byte[] getQRCodeImage(String text, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageConfig con = new MatrixToImageConfig( 0xFF000000 , 0xFFFFFFFF ) ;

        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream,con);
        return pngOutputStream.toByteArray();
    }

    public static String checkHutchaToken2(String hutchaToken, String ipAddress) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();

            HttpPost httpPost = new HttpPost(Variables.HUTCHA_API_HOST + "check-token");
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Accept", "application/json");

            JSONObject json = new JSONObject();
            json.put("token", hutchaToken);
            json.put("ipAddress", ipAddress);

            StringEntity entity = new StringEntity(json.toString());
            httpPost.setEntity(entity);

            CloseableHttpResponse response = httpClient.execute(httpPost);
            int responseCode = response.getStatusLine().getStatusCode();

            String responseString = new String(response.getEntity().getContent().readAllBytes());

            JSONObject jsonResponse = new JSONObject(responseString);
            String message = jsonResponse.getString("message");
            return responseCode == 200 ? "Correctly solved HUTCHA." : "[Error] " + message;

        } catch (IOException | JSONException | ParseException e) {
            e.printStackTrace();
            return "[Error] Unknown error while checking HUTCHA token. Please try again later.";
        }
    }


    public static String checkHutchaToken(String hutchaToken, String ipAddress) {
        try {
            // Create the JSON object to send in the body of the request
            JSONObject json = new JSONObject();
            json.put("token", hutchaToken);
            json.put("ipAddress", ipAddress);

            // Disable SSL validation
            HostnameVerifier ValidHost = new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(ValidHost);

            URL url = new URL(Variables.HUTCHA_API_HOST + "check-token");
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(7000);

            // Write the JSON body to the request
            OutputStream os = connection.getOutputStream();
            os.write(json.toString().getBytes());
            os.flush();


            // Get the response code
            int responseCode = connection.getResponseCode();


            // Close the connection

            // Read the response
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream() == null ? connection.getInputStream() : connection.getErrorStream()));
            String output;
            StringBuilder response = new StringBuilder();
            while ((output = br.readLine()) != null) {
                response.append(output);
            }

            br.close();
            os.close();
            connection.disconnect();

            // Parse json response
            JSONObject jsonResponse = new JSONObject(response.toString());
            String message = jsonResponse.getString("message");
            return responseCode == 200 ? "Correnctly solved HUTCHA." : "[Error] " + message;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return "[Error] Unknown error while checking HUTCHA token. Please try again later.";
        }
    }


    }
