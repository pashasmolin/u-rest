package utest;

/**
 * Created by PaTa on 2/22/2016.
 */

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utestSource.Landlord;

import static com.jayway.restassured.RestAssured.*;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;

public class LandlordTest {

    @BeforeClass
    public static void init() {
        RestAssured.baseURI = "http://localhost:8080";
    }


    @Test
    public void getLandlords() {
        when()
                .get("/landlords")
                .then()
                .statusCode(200)
                .body("", is(notNullValue()));
    }

    @Test
    public void postLandlord01() {
        Landlord landlord = new Landlord("Tanushka4", "Smolin");

        String id = given()
                .contentType(ContentType.JSON)
                .body(landlord)
                .when()
                .post("/landlords")
                .then()
                .statusCode(201)
                .body("firstName", is(landlord.getFirstName()))
                .body("lastName", is(landlord.getLastName()))
                .body("trusted", is(false))
                .body("apartments", is(empty()))
                .extract()
                .path("id");


        given()
                .pathParam("id", id)
                .when()
                .get("/landlords/{id}")
                .then()
                .statusCode(200)
                .body("id", is(id))
                .body("firstName", is(landlord.getFirstName()))
                .body("lastName", is(landlord.getLastName()))
                .body("trusted", is(false))
                .body("apartments", is(empty()));

        String json = get("/landlords").asString();
        System.out.println(json);
    }


    @Test
    public void postLandlord02() {
        Landlord landlord = new Landlord("Tanushka5", "Smolin5", true);

        String id = given()
                .contentType(ContentType.JSON)
                .body(landlord)
                .when()
                .post("/landlords")
                .then()
                .statusCode(201)
                .body("firstName", is(landlord.getFirstName()))
                .body("lastName", is(landlord.getLastName()))
                .body("trusted", is(true))
                .body("apartments", is(empty()))
                .extract()
                .path("id");


        given()
                .pathParam("id", id)
                .when()
                .get("/landlords/{id}")
                .then()
                .statusCode(200)
                .body("id", is(id))
                .body("firstName", is(landlord.getFirstName()))
                .body("lastName", is(landlord.getLastName()))
                .body("trusted", is(true))
                .body("apartments", is(empty()));

        String json = get("/landlords").asString();
        System.out.println(json);
    }

    @Test
    public void postLandlordNegative01() {
        Landlord landlord = new Landlord("", "");

        given()
                .contentType(ContentType.JSON)
                .body(landlord)
                .when()
                .post("/landlords")
                .then()
                .statusCode(400)
                .body("message", is("Fields are with validation errors"))
                .body("fieldErrorDTOs[1].fieldName", is("firstName"))
                .body("fieldErrorDTOs[1].fieldError", is("First name can not be empty"))
                .body("fieldErrorDTOs[0].fieldName", is("lastName"))
                .body("fieldErrorDTOs[0].fieldError", is("Last name can not be empty"));

    }

    @Test
    public void putLandlord01() {

        //CREATE NEW LANDLORD
        Landlord landlord = new Landlord("Alex", "Fruz", true);
        String id = given()
                .contentType(ContentType.JSON)
                .body(landlord)
                .when()
                .post("/landlords")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        //UPDATE
        Landlord landLordForUpdate = new Landlord("Alexey", "Fruzik");
        given()
                .contentType(ContentType.JSON)
                .body(landLordForUpdate)
                .pathParam("id", id)
                .when()
                .put("/landlords/{id}")
                .then()
                .statusCode(200)
                .body("message", is("LandLord with id: " + id + " successfully updated"));

        //VERIFY UPDATE
        given()
                .pathParam("id", id)
                .when()
                .get("/landlords/{id}")
                .then()
                .statusCode(200)
                .body("firstName", is(landLordForUpdate.getFirstName()))
                .body("lastName", is(landLordForUpdate.getLastName()))
                .body("trusted", is(false));
    }

    @Test
    public void deleteLandlord01() {
        Landlord landlord = new Landlord("Test", "Testovi4");
        //create landlord
        String id = given()
                .contentType(ContentType.JSON)
                .body(landlord)
                .when()
                .post("/landlords")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        //delete him
        given()
                .pathParam("id", id)
                .when()
                .delete("/landlords/{id}")
                .then()
                .statusCode(200)
                .body("message", is("LandLord with id: " + id + " successfully deleted"));

        //check there is no landlord exists with such id anymore by checking 404 code
        given()
                .pathParam("id", id)
                .when()
                .get("/landlords/{id}")
                .then()
                .statusCode(404)
                .body("message", is("There is no LandLord with id: " + id));
    }


}