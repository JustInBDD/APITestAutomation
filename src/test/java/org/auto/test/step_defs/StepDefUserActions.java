package org.auto.test.step_defs;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.auto.core.api_specs.UserAPISpecs;
import org.auto.core.helpers.ConfigReader;
import org.junit.Assert;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class StepDefUserActions {

    UserAPISpecs user_api_specs;
    HashMap<String, String> create_user_data;
    HashMap<String, String> update_user_data;
    HashMap<String, String> delete_user_data;
    HashMap<String, String> get_user_data;
    HashMap<String, String> prepare_test_data;
    Response res;
    ConfigReader cr = new ConfigReader();

    @Given("^application is up and reachable$")
    public void application_is_up_and_reachable() {
        given()
                .get(cr.get_base_uri())
                .then()
                .assertThat().statusCode(200);
        user_api_specs = new UserAPISpecs();
    }

    @Given("^setup create_user api$")
    public void setup_create_user_api() {
        create_user_data = new HashMap<>();
        
    }

    @When("^set (.+) as (.+)$")
    public void set_test_data(String key_name, String content) {
        if(content.equals("empty")){
            create_user_data.put(key_name, "");
        }else{
        create_user_data.put(key_name, content);
        }
    }

    @When("^creates user with the details provided$")
    public void creates_user() {
        res = user_api_specs.create_user(create_user_data);
    }

    @Then("^verify the response code is (\\d+)$")
    public void verify_response_code(int response_code) {
user_api_specs.verify_response_code(res, response_code);
    }

    @Then("^verify the (.+) json schema$")
    public void verify_json_schema(String schema_file_name) {
        res.then()
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath(schema_file_name + "_schema.json"));
    }

    @Then("^verify response time is within configured value$")
    public void verify_response_time() {
        user_api_specs.verify_response_time(res);
    }

    @Then("^verify the user is created successfully$")
    public void verify_the_user_is_created_successfully() {
        Assert.assertEquals("Users' name doesnt match name returned in response", res.then().assertThat().extract().body().jsonPath().get("data['name']").toString(), create_user_data.get("name"));
        Assert.assertEquals("Users' email doesnt match email returned in response", res.then().assertThat().extract().body().jsonPath().get("data['email']").toString(), create_user_data.get("email"));
        Assert.assertEquals("Users' gender doesnt match gender returned in response", res.then().assertThat().extract().body().jsonPath().get("data['gender']").toString(), create_user_data.get("gender"));
        Assert.assertEquals("Users' status doesnt match status returned in response", res.then().assertThat().extract().body().jsonPath().get("data['status']").toString(), create_user_data.get("status"));
    }

    @Then("^verify details of user (.+) is persisted in the application$")
    public void verify_user_details_persisted_in_the_application(String action_type) {
        String user_id = res.body().jsonPath().get("data['id']").toString();
        HashMap<String, String> user_data = null;
        if(action_type.equals("created")){
            user_data = create_user_data;
        }else if(action_type.equals("updated")){
            user_data = update_user_data;
        }else{
            Assert.fail("Invalid action type " + action_type + " configured, allowed values are created/updated");
        }
        Response get_user_res = given(user_api_specs.req_spec_without_auth)
                .when()
                .get(user_api_specs.user_resource_path + "/" + user_id);
        get_user_res.then().assertThat().statusCode(200);
        Assert.assertEquals("Users name retrieved after creation/update doesnt match with test data value", get_user_res.then().assertThat().extract().body().jsonPath().get("data['name']").toString(), user_data.get("name"));
        Assert.assertEquals("Users email retrieved after creation/update doesnt match with test data value", get_user_res.then().assertThat().extract().body().jsonPath().get("data['email']").toString(), user_data.get("email"));
        Assert.assertEquals("Users gender retrieved after creation/update doesnt match with test data value", get_user_res.then().assertThat().extract().body().jsonPath().get("data['gender']").toString(), user_data.get("gender"));
        Assert.assertEquals("Users status retrieved after creation/update doesnt match with test data value", get_user_res.then().assertThat().extract().body().jsonPath().get("data['status']").toString(), user_data.get("status"));
    }

    @Given("^update details of user with email (.+)$")
    public void update_details_of_user_with_email(String search_by_email){
        
        update_user_data = new HashMap<>();
        update_user_data.put("email",search_by_email);
    }

    @When("^update (.+) of user to (.+)$")
    public void build_user_details_for_update(String key_name, String value){
        if(update_user_data.containsKey(key_name) && key_name.equals("email")){
            update_user_data.put("next_email", value);
        }else{
            update_user_data.put(key_name, value);
        }

    }

    @When("^updates user with details provided$")
    public void updates_user_with_the_details_provided() {
       String user_id = user_api_specs.get_user_id_by_email(update_user_data.get("email"));
       update_user_data.put("prev_email", update_user_data.get("email"));
       update_user_data.put("email", update_user_data.get("next_email"));
       update_user_data.remove("next_email");
       res = given(user_api_specs.req_spec_with_auth)
               .log()
               .all()
                .when()
               .log()
               .all()
                .body(update_user_data)
                .patch(user_api_specs.user_resource_path+ "/" + user_id)
                .then()
                .log()
                .all()
                .extract()
                .response();
    }

    @Then("^verify the user details are updated successfully$")
    public void verify_the_user_is_updated_successfully() {
        for(String key: update_user_data.keySet()){
            if(key.equals("user_id") || key.equals("prev_email")){
                continue;
            }
            Assert.assertEquals("Updated user data doesnt match with test data", res.then().assertThat().extract().body().jsonPath().get("data['" + key + "']").toString(), update_user_data.get(key));
        }
    }

    @Given("^delete user with email (.+)$")
    public void delete_user_with_email(String search_by_email) {
        delete_user_data = new HashMap<>();
        Response get_user_id = given(user_api_specs.req_spec_without_auth)
                .when()
                .queryParam("email", search_by_email)
                .get(user_api_specs.user_resource_path)
                .then()
                .extract()
                .response();
        Assert.assertEquals("Zero/duplicate result for searching the user using email id", "1", get_user_id.then().assertThat().extract().body().jsonPath().get("meta['pagination']['total']").toString());
        delete_user_data.put("user_id",get_user_id.then().extract().jsonPath().get("data[0]['id']").toString());
    }

    @When("^delete user$")
    public void delete_user_with_email() {
        res = user_api_specs.delete_user_by_id(delete_user_data.get("user_id"));
    }

    @Then("^verify user with (.+) is not present in the application$")
    public void verify_user_with_is_not_present_in_the_application(String search_by_email){
        Response get_user_id = given(user_api_specs.req_spec_without_auth)
                .when()
                .queryParam("email", search_by_email)
                .get(user_api_specs.user_resource_path)
                .then()
                .log()
                .all()
                .extract()
                .response();
        Assert.assertTrue("Deleted user search did not return non-zero result", get_user_id.then().assertThat().extract().body().jsonPath().get("meta['pagination']['total']").equals(0));
    }

    @When("^get user details with (.+)$")
    public void get_user_details_with(String search_by_email){
        get_user_data = new HashMap<>();
        get_user_data.put("current_email", search_by_email);
    }

    @Then("^verify user details (.+) (.+) (.+) (.+)$")
    public void verify_user_details(String name, String email, String gender, String status){
        String user_id = user_api_specs.get_user_id_by_email(get_user_data.get("current_email"));
        res = given(user_api_specs.req_spec_without_auth)
                .log()
                .all()
                .when()
                .log()
                .all()
                .get(user_api_specs.user_resource_path+ "/" + user_id)
                .then()
                .log()
                .all()
                .extract()
                .response();
        Assert.assertEquals("User data retrieved doesnt match with test data data-key is name", res.then().assertThat().extract().body().jsonPath().get("data['name']").toString(), name);
        Assert.assertEquals("User data retrieved doesnt match with test data data-key is email", res.then().assertThat().extract().body().jsonPath().get("data['email']").toString(), email);
        Assert.assertEquals("User data retrieved doesnt match with test data data-key is gender", res.then().assertThat().extract().body().jsonPath().get("data['gender']").toString(), gender);
        Assert.assertEquals("User data retrieved doesnt match with test data data-key is status", res.then().assertThat().extract().body().jsonPath().get("data['status']").toString(), status);
    }

    @And("cleanup the user data created")
    public void cleanup_user_data(){
        String user_id = res.body().jsonPath().get("data['id']").toString();
        Response delete_user_response = user_api_specs.delete_user_by_id(user_id);
        delete_user_response.then().assertThat().statusCode(204);
    }

    @And("testdata is prepared")
    public void prepare_test_data(){
        Response res = null;
        if(prepare_test_data != null){
            res = user_api_specs.create_user(prepare_test_data);
        }else if(update_user_data != null){
            res = user_api_specs.create_user(update_user_data);
        }else if(get_user_data != null){
            res = user_api_specs.create_user(get_user_data);
        }else if(delete_user_data != null){
            res = user_api_specs.create_user(delete_user_data);
        }else{
            Assert.fail("No test data to create users");
        }
        res.then().assertThat().statusCode(201);
    }

    @And("^user is created in application with (.+) (.+) (.+) (.+) details$")
    public void user_is_created_in_application_with_details(String name, String email, String gender, String status){
        HashMap<String, String> test_data = new HashMap<>();
        test_data.put("name", name);
        test_data.put("email", email);
        test_data.put("gender", gender);
        test_data.put("status", status);
        Response res = user_api_specs.create_user(test_data);
        res.then().assertThat().statusCode(201);
    }

    @And("^verify error message (.+) is displayed for field (.+)$")
    public void verify_error_message(String err_message, String field_name){
       Assert.assertEquals("Field-name in error response didnt match",res.then().extract().jsonPath().get("data[0]['field']").toString(),field_name);
       Assert.assertEquals("Error message value didnt match",res.then().extract().jsonPath().get("data[0]['message']").toString(),err_message);
    }



    }















