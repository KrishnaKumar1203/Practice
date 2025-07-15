package stepDefinations.Java;

import io.restassured.response.Response;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class GetStudentTest {

    public static void main(String[] args) {

        baseURI = "http://localhost:3000";

        // ===== GET /students/1 =====
        Response response = 
            given().
                auth().preemptive().basic("admin", "1234").
            when().
                get("/students/1");

        // ===== GET /students =====
        Response response1 = 
            given().
                auth().preemptive().basic("admin", "1234").
            when().
                get("/students");

        System.out.println("Response Body (students/1): " + response.getBody().asString());
        System.out.println("Response Body (students): " + response1.getBody().asString());
        System.out.println("Status Code (students/1): " + response.getStatusCode());

        // Validate that 'course' contains "java" in students/1 (assuming course is a list)
        response.
            then().
                statusCode(200).
                body("course", hasItem("Java")); // Ensure JSON actually has "Java" not "java"

        // ===== POST /students =====
        Response response2 = 
            given().
                auth().preemptive().basic("admin", "1234").
                header("Content-Type", "application/json").
                body("{ \"name\": \"Anjali2\", \"course\": [\"Java\"], \"location\": \"Gurgaon2\", \"phone\": \"9410072070\" }").
            when().
                post("/students");

        response2.
            then().
                statusCode(201).
                body("name", equalTo("Anjali2")).
                body("course[0]", equalTo("Java")).  // Since course is now an array
                body("location", equalTo("Gurgaon2")).
                body("phone", equalTo("9410072070"));

        System.out.println("Response Body (POST): " + response2.getBody().asString());
    }
}
