package org.auto.test.step_defs;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.auto.core.api_specs.PostAPISpecs;
import org.auto.core.api_specs.UserAPISpecs;
import org.auto.core.helpers.ConfigReader;
import org.junit.Assert;

import java.util.*;

import static io.restassured.RestAssured.given;
public class StepDefUserPostActions{

    PostAPISpecs post_api_specs;
    UserAPISpecs user_api_specs;
    HashMap<String, String> create_post_data;
    HashMap<String, String> update_post_data;
    Response res;
    List<Response> multiple_posts_res_list;
    String user_id;
    String post_id;
    ConfigReader cr = new ConfigReader();


    @Given("^application is up and reachable for posts$")
    public void initialize_post_api_spec(){
        given()
                .get(cr.get_base_uri())
                .then()
                .assertThat().statusCode(200);
        post_api_specs  = new PostAPISpecs();
        user_api_specs = new UserAPISpecs();
    }

    @And("^create user for posting with following details \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\"$")
    public void create_user_as_prerequisite(String name, String email, String gender, String status){
        HashMap<String, String> test_data = new HashMap<>();
        test_data.put("name", name);
        test_data.put("email", email);
        test_data.put("gender", gender);
        test_data.put("status", status);
        Response res = user_api_specs.create_user(test_data);
        res.then().assertThat().statusCode(201);
    }

    @And("^setup create_post api$")
    public void initialize_create_post(){
        create_post_data = new HashMap<>();
    }

    @And("^setup update_post api$")
    public void initialize_update_post(){
        update_post_data = new HashMap<>();
    }

    @And("^setup post title as \"([^\"]*)\"$")
    public void set_post_title(String content){
        create_post_data.put("title", content);
    }

    @And("^setup post body as \"([^\"]*)\"$")
    public void set_post_body(String content){
        create_post_data.put("body", content);
    }

    @And("^post is created for the user$")
    public void post_is_created_for_the_user() {
       res = post_api_specs.create_post(create_post_data, user_id);
       post_id = res.then().extract().jsonPath().get("data['id']").toString();
    }

    @When("^get user_id of user with email \\\"([^\\\"]*)\\\"$")
    public void get_userid_of_user_with_email(String email_id) {
        user_id = user_api_specs.get_user_id_by_email(email_id);
    }

    @Then("^verify post api response code is (\\d+)$")
    public void verify_the_response_code(int response_code) {
        post_api_specs.verify_response_code(res, response_code);
    }

    @Then("^verify post api response time is within configured value$")
    public void verify_response_time(){
        post_api_specs.verify_response_time(res);
    }

    @Then("^verify details of the post \\\"([^\\\"]*)\\\"$")
    public void verify_post_details(String action_type){
        HashMap<String, String> test_data = null;
        if(action_type.equals("created")){
            test_data = create_post_data;
        }else if(action_type.equals("updated")){
            test_data = update_post_data;
        }else{
            Assert.fail("Unhandled action type passed");
        }
        Assert.assertEquals("Details of the post " + action_type + " didnt match with the test data data-key is user_id", res.then()
                .assertThat().extract().jsonPath().get("data['user_id']").toString(), user_id);
        Assert.assertEquals("Details of the post " + action_type + " didnt match with the test data data-key is title", res.then()
                .assertThat().extract().jsonPath().get("data['title']").toString(), test_data.get("title"));
        Assert.assertEquals("Details of the post " + action_type + " didnt match with the test data data-key is body", res.then()
                .assertThat().extract().jsonPath().get("data['body']").toString(), test_data.get("body"));
    }

    @Then("^get the post data and compare with values in response$")
    public void compare_res_data_with_get_data(){
        Response get_res = given(post_api_specs.req_spec_without_auth)
                .when()
                .get(post_api_specs.posts_resource_path+"/"+post_id)
                .then()
                .extract()
                .response();
        get_res.then().assertThat().statusCode(200);
        Assert.assertEquals("Post data retrieved doesnt match with data returned in response data-key is user_id", res.then()
                .assertThat().extract().jsonPath().get("data['user_id']").toString(), get_res.then().extract().jsonPath().get("data['user_id']").toString());
        Assert.assertEquals("Post data retrieved doesnt match with data returned in response data-key is title", res.then()
                .assertThat().extract().jsonPath().get("data['title']").toString(), get_res.then().extract().jsonPath().get("data['title']"));
        Assert.assertEquals("Post data retrieved doesnt match with data returned in response data-key is body", res.then()
                .assertThat().extract().jsonPath().get("data['body']").toString(), get_res.then().extract().jsonPath().get("data['body']"));
    }

    @And("^verify post api \\\"([^\\\"]*)\\\" json schema$")
    public void verify_post_api_createpost_json_schema(String schema_file_name){
        res.then()
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath(schema_file_name + "_schema.json"));
    }

    @And("cleanup the user data created for testing post api")
    public void cleanup_user_data(){
        Response delete_user_response = user_api_specs.delete_user_by_id(user_id);
        delete_user_response.then().assertThat().statusCode(204);
    }

    @And("^update post \\\"([^\\\"]*)\\\" to \\\"([^\\\"]*)\\\"$")
    public void update_post_title(String key_name, String content){
        update_post_data.put(key_name, content);
    }

    @And("^post is updated for the user$")
    public void post_is_updated_for_the_user(){
        res = given(post_api_specs.req_spec_with_auth)
                .when()
                .body(update_post_data)
                .patch(post_api_specs.posts_resource_path+"/"+post_id)
                .then()
                .extract()
                .response();
    }

    @And("^delete the post$")
    public void delete_the_post(){
        res = given(post_api_specs.req_spec_with_auth)
                .when()
                .delete(post_api_specs.posts_resource_path+"/"+post_id)
                .then()
                .extract()
                .response();
    }


    @And("^verify getting the deleted post returns \"([^\"]*)\"$")
    public void verify_getting_the_deleted_post(String error_message){
        Response get_res = given(post_api_specs.req_spec_without_auth)
                .when()
                .get(post_api_specs.posts_resource_path+"/"+post_id)
                .then()
                .extract()
                .response();
        get_res.then().assertThat().statusCode(404);
        Assert.assertTrue("Error message doesnt match", get_res.then().assertThat().extract().jsonPath().get("data['message']").toString().equalsIgnoreCase(error_message));
    }

    @And("^retrieve the post$")
    public void retrieve_post(){
        res =  given(post_api_specs.req_spec_without_auth)
                .when()
                .get(post_api_specs.posts_resource_path+"/"+post_id)
                .then()
                .extract()
                .response();
    }

    @And("^create multiple posts with following details$")
    public void create_multiple_posts_with_following_details(List<Map<String, String>> test_data){
        multiple_posts_res_list = new ArrayList<>();
        for(Map<String, String> each_data : test_data){
            multiple_posts_res_list.add(post_api_specs.create_post(each_data, user_id));
        }
    }

    @And("^verify response_code of each post api response is (.+)$")
    public void verify_res_code_res_list(String response_code){
        if(multiple_posts_res_list == null || multiple_posts_res_list.size()==0){
            Assert.fail("List of responses is empty, hence marking the test case as fail");
        }
        for(Response each_res: multiple_posts_res_list){
            post_api_specs.verify_response_code(each_res, Integer.parseInt(response_code));
        }
    }

    @And("^verify response time of every post of the user$")
    public void verify_response_time_list_res(){
        if(multiple_posts_res_list == null || multiple_posts_res_list.size()==0){
            Assert.fail("List of responses is empty, hence marking the test case as fail");
        }
        for(Response each_res: multiple_posts_res_list){
            post_api_specs.verify_response_time(each_res);
        }
    }

    @And("^verify post data by retrieving all the posts of the user$")
    public void verify_post_data_by_retrieving_all_the_posts_of_the_user(){
        res = given(post_api_specs.req_spec_without_auth)
                .when()
                .get(post_api_specs.user_resource_path+"/"+user_id+"/posts")
                .then()
                .extract()
                .response();
        List<Map<String, String>> post_list = res.then().extract().jsonPath().getList("data");
        Map<String, Map<String,String>> searchable_map = new HashMap<>();
        for(Map<String,String> each_post: post_list){
            searchable_map.put(String.valueOf(each_post.get("id")),each_post);
        }
        for(Response each_res: multiple_posts_res_list){
            String post_id = each_res.then().extract().jsonPath().get("data['id']").toString();
            Assert.assertEquals("Posts data retrieved doesnt match with data shown in response body data-key is id",each_res.then().assertThat().extract().jsonPath().get("data['id']").toString(),String.valueOf(searchable_map.get(post_id).get("id")));
            Assert.assertEquals("Posts data retrieved doesnt match with data shown in response body data-key is user_id",each_res.then().assertThat().extract().jsonPath().get("data['user_id']").toString(),String.valueOf(searchable_map.get(post_id).get("user_id")));
            Assert.assertEquals("Posts data retrieved doesnt match with data shown in response body data-key is title",each_res.then().assertThat().extract().jsonPath().get("data['title']").toString(),searchable_map.get(post_id).get("title"));
            Assert.assertEquals("Posts data retrieved doesnt match with data shown in response body data-key is body",each_res.then().assertThat().extract().jsonPath().get("data['body']").toString(),searchable_map.get(post_id).get("body"));
        }
        Assert.assertEquals("Number of test data doesnt match with number of posts for the user", searchable_map.size(), multiple_posts_res_list.size());
    }


    @And("^verify error messages in response when invalid values are passed to posts api$")
    public void validate_invalid_input_err_msg(List<Map<String, String>> test_data) {
        try {
            for (Map<String, String> each_data : test_data) {
                create_post_data.put("title", each_data.get("title").equalsIgnoreCase("empty") ? "" : each_data.get("title"));
                create_post_data.put("body", each_data.get("body").equalsIgnoreCase("empty") ? "" : each_data.get("body"));
                res = post_api_specs.create_post(create_post_data, user_id);
                res.then().assertThat().statusCode(Integer.parseInt(each_data.get("status_code")));
                Assert.assertTrue("Field name in the error response didnt match", res.then().assertThat().extract().jsonPath().get("data[0]['field']").toString().equalsIgnoreCase(each_data.get("field_name").toString()));
                Assert.assertTrue("Error message in the error response didnt match", res.then().assertThat().extract().jsonPath().get("data[0]['message']").toString().equalsIgnoreCase(each_data.get("error_message").toString()));
            }
        } catch (AssertionError err) {
            cleanup_user_data();
            Assert.fail(err.getMessage() + "\n" + err.getCause());
        }
        cleanup_user_data();
    }







}
