package stepDefinations.Java;

import io.restassured.response.Response;
import java.io.File;

import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class GetStudentTest {

private static Process serverProcess;
private static long serverPid;
public static boolean waitForServerToStart(String url, int maxRetries) {
    int attempts = 0;
    while (attempts < maxRetries) {
        try {
            Response response = given()
                    .auth().preemptive().basic("admin", "1234")
                    .when()
                    .get(url);

            int code = response.getStatusCode();
            System.out.println("Attempt " + (attempts + 1) + ": Status code = " + code);

            if (code == 200 || code == 404 || code == 401) {
                return true;
            }

        } catch (Exception e) {
            System.out.println("Attempt " + (attempts + 1) + " failed: " + e.getMessage());
        }

        try {
            Thread.sleep(1000); // wait 1 second
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }

        attempts++;
    }
    return false;
}



public static void startServer() throws IOException {
    ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "node server.js");
    builder.directory(new File("C:\\Users\\kksuc\\IdeaProjects\\Practice\\src\\main\\resources\\api-auth-server"));
    builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
    builder.redirectError(ProcessBuilder.Redirect.INHERIT);
    serverProcess = builder.start();

    // Capture the PID (Java 9+ only)
    serverPid = serverProcess.pid();
    System.out.println("✅ JSON Server started from Java with PID: " + serverPid);
}

// // Start the JSON Server with server.js
// public static void startServer() throws IOException {
//     new ProcessBuilder("cmd.exe", "/c", "node server.js");
//     builder.directory(new File("C:\\Users\\kksuc\\IdeaProjects\\Practice\\src\\main\\resources\\api-auth-server"));
//     builder.redirectErrorStream(true); // Combines stderr and stdout
//     serverProcess = builder.start();

//     // Optional: Read and print the server output
//     new Thread(() -> {
//         try (BufferedReader reader = new BufferedReader(new InputStreamReader(serverProcess.getInputStream()))) {
//             String line;
//             while ((line = reader.readLine()) != null) {
//                 System.out.println("[Server Log] " + line);
//                 if (line.contains("JSON Server running")) break; // Ready to make API calls
//             }
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }).start();

//     // Wait a bit to ensure server is up before making requests
//     try {
//         Thread.sleep(5000);
//     } catch (InterruptedException e) {
//         Thread.currentThread().interrupt();
//     }

//     System.out.println("✅ JSON Server started from Java.");
// }

// Stop the JSON Server
public static void stopServer() {
    try {
        if (serverProcess != null && serverProcess.isAlive()) {
            serverProcess.destroy();
            Thread.sleep(2000); // Give it a moment
        }

        // Force kill any process on port 3000 (used by JSON Server)
        System.out.println("🔍 Killing any process running on port 3000...");
        new ProcessBuilder("cmd.exe", "/c", "for /f \"tokens=5\" %a in ('netstat -aon ^| findstr :3000 ^| findstr LISTENING') do taskkill /F /PID %a")
                .inheritIO()
                .start()
                .waitFor();

        System.out.println("✅ Port 3000 process terminated.");

    } catch (Exception e) {
        System.err.println("❌ Failed to stop server: " + e.getMessage());
        e.printStackTrace();
    }
}




    public static void main(String[] args) {
    try {
        // Start the server
        startServer();

        if (!waitForServerToStart("http://localhost:3000/students", 15)) {
            System.err.println("Server failed to start after multiple attempts.");
            return;
        }

        baseURI = "http://localhost:3000";
        List<String> createdIds = new ArrayList<>();

        // ===== GET /students/1 =====
        Response getSingleStudent = given()
                .auth().preemptive().basic("admin", "1234")
                .when()
                .get("/students/1");

        System.out.println("GET /students/1 Response: ");
        getSingleStudent.prettyPrint();

        getSingleStudent.then().statusCode(200)
                .body("course", hasItem("java"));

        // ===== GET /students =====
        Response getAllStudents = given()
                .auth().preemptive().basic("admin", "1234")
                .when()
                .get("/students");

        System.out.println("GET /students Response: ");
        getAllStudents.prettyPrint();

        // ===== POST /students =====
        Response postResponse = given()
                .auth().preemptive().basic("admin", "1234")
                .header("Content-Type", "application/json")
                .body("{ \"name\": \"Anjali_Post\", \"course\": [\"java\"], \"location\": \"Gurgaon\", \"phone\": \"9410072070\" }")
                .when()
                .post("/students");

        String postId = postResponse.jsonPath().getString("id");
        createdIds.add(postId);

        System.out.println("POST /students Response: ");
        postResponse.prettyPrint();
        postResponse.then().statusCode(201)
                .body("name", equalTo("Anjali_Post"));

        // ===== PUT /students/{id} =====
        Response putResponse = given()
            .auth().preemptive().basic("admin", "1234")
            .header("Content-Type", "application/json")
            .body("{ \"name\": \"Anjali_PUT\", \"course\": [\"Python\"], \"location\": \"Delhi\", \"phone\": \"9410000000\" }")
            .when()
            .put("/students/" + postId);

        System.out.println("PUT /students/{id} Response: ");
        putResponse.prettyPrint();

        // ===== PATCH /students/{id} =====
        Response patchResponse =   given()
            .auth().preemptive().basic("admin", "1234")
            .header("Content-Type", "application/json")
            .body("{ \"location\": \"Noida\" }")
            .when()
            .patch("/students/" + postId);

        System.out.println("PATCH /students/{id} Response: ");
        patchResponse.prettyPrint();

        // ===== POST Another Student =====
        Response postResponse2 = given()
                .auth().preemptive().basic("admin", "1234")
                .header("Content-Type", "application/json")
                .body("{ \"name\": \"Patch_Student\", \"course\": [\"C++\"], \"location\": \"Mumbai\", \"phone\": \"9999999999\" }")
                .when()
                .post("/students");
        String postId2 = postResponse2.jsonPath().getString("id");

        
        createdIds.add(postId2);

        System.out.println("Second POST Response:");
        postResponse2.prettyPrint();

        // ===== PATCH Second Student =====

        Response patchResponse2 = given()
            .auth().preemptive().basic("admin", "1234")
            .header("Content-Type", "application/json")
            .body("{ \"phone\": \"8888888888\" }")
            .when()
                .patch("/students/" + postId2);

        System.out.println("PATCH Second Student Response:");
        patchResponse2.prettyPrint();

                // ===== GET /students =====
        Response getAllStudents1 = given()
                .auth().preemptive().basic("admin", "1234")
                .when()
                .get("/students");

        System.out.println("GET /students Response: ");
        getAllStudents1.prettyPrint();
        // ===== DELETE All Created Students =====
        for (String id : createdIds) {

            Response deleteResponse = given()
                    .auth().preemptive().basic("admin", "1234")
                    .when()
                    .delete("/students/" + id);

            System.out.println("Deleted Student ID: " + id);
            deleteResponse.then().statusCode(200);  // or 204 depending on API config
        }

        System.out.println("✅ All test actions completed successfully.");

    } catch (Exception e) {
        System.err.println("❌ Error during test execution: " + e.getMessage());
        e.printStackTrace();
    } finally {
        // Always stop server no matter what
        stopServer();
    }
}

    
}
