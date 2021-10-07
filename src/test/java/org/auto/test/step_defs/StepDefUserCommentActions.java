package org.auto.test.step_defs;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.auto.core.api_specs.CommentApiSpecs;
import org.auto.core.api_specs.PostAPISpecs;
import org.auto.core.api_specs.UserAPISpecs;
import org.auto.core.helpers.ConfigReader;
import org.junit.Assert;

import java.util.*;

import static io.restassured.RestAssured.given;

public class StepDefUserCommentActions {
    PostAPISpecs post_api_specs;
    UserAPISpecs user_api_specs;
    CommentApiSpecs comment_api_specs;
    HashMap<String, String> create_comment_data;
    HashMap<String, String> update_comment_data;
    Response res;
    List<Response> multiple_comments_res_list;
    String user_id;
    String post_id;
    String comment_id;
    ConfigReader cr = new ConfigReader();

    @Given("^application is up and reachable for comments$")
    public void initialize_comment_api_spec() {
        given()
                .get(cr.get_base_uri())
                .then()
                .assertThat().statusCode(200);
        comment_api_specs = new CommentApiSpecs();
        post_api_specs = new PostAPISpecs();
        user_api_specs = new UserAPISpecs();
    }

    @And("^create user for commenting with following details \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\"$")
    public void create_user(String name, String email, String gender, String status) {
        HashMap<String, String> test_data = new HashMap<>();
        test_data.put("name", name);
        test_data.put("email", email);
        test_data.put("gender", gender);
        test_data.put("status", status);
        Response res = user_api_specs.create_user(test_data);
        res.then().assertThat().statusCode(201);
        user_id = res.then().extract().jsonPath().get("data['id']").toString();
    }

    @And("set create_comment api for comment api test")
    public void setup_create_comment_data() {
        create_comment_data = new HashMap<>();
    }

    @And("^setup update_comment api$")
    public void initialize_update_comment() {
        update_comment_data = new HashMap<>();
    }

    @And("^create post with title as \"([^\"]*)\" and body as \"([^\"]*)\"$")
    public void create_post(String title, String body) {
        HashMap<String, String> test_data = new HashMap<>();
        test_data.put("title", title);
        test_data.put("body", body);
        Response res = post_api_specs.create_post(test_data, user_id);
        res.then().assertThat().statusCode(201);
        post_id = res.then().extract().jsonPath().get("data['id']").toString();
    }

    @And("^setup comment \\\"([^\\\"]*)\\\" as \\\"([^\\\"]*)\\\"$")
    public void setup_comment_test_data(String key_name, String content) {
        create_comment_data.put(key_name, content);
    }

    @And("^comment is created for the post$")
    public void create_comment() {
        res = given(comment_api_specs.req_spec_with_auth)
                .when()
                .body(create_comment_data)
                .post(post_api_specs.posts_resource_path + "/" + post_id + "/comments")
                .then()
                .extract()
                .response();
        comment_id = res.then().extract().response().jsonPath().get("data['id']").toString();
    }

    @Then("^verify comment api response code is (\\d+)$")
    public void verify_the_response_code(int response_code) {
        comment_api_specs.verify_response_code(res, response_code);
    }

    @And("^verify comment api \"([^\"]*)\" json schema$")
    public void verify_comment_api_json_schema(String schema_file_name) {
        res.then()
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath(schema_file_name + "_schema.json"));
    }

    @And("^verify comment api response time is within configured value$")
    public void verify_comment_api_response_time() {
        comment_api_specs.verify_response_time(res);
    }


    @And("^verify details of the comment \"([^\"]*)\"$")
    public void verify_details_of_the_comment(String action_type) {
        HashMap<String, String> test_data = null;
        if (action_type.equals("created")) {
            test_data = create_comment_data;
        } else if (action_type.equals("updated")) {
            test_data = update_comment_data;
        } else {
            Assert.fail("Unhandled action type passed");
        }
        Assert.assertEquals("Comment " + action_type + " response data didnt match with test data data-key is post_id", res.then()
                .assertThat().extract().jsonPath().get("data['post_id']").toString(), post_id);
        Assert.assertEquals("Comment " + action_type + " response data didnt match with test data data-key is name", res.then()
                .assertThat().extract().jsonPath().get("data['name']").toString(), test_data.get("name"));
        Assert.assertEquals("Comment " + action_type + " response data didnt match with test data data-key is email", res.then()
                .assertThat().extract().jsonPath().get("data['email']").toString(), test_data.get("email"));
        Assert.assertEquals("Comment " + action_type + " response data didnt match with test data data-key is body", res.then()
                .assertThat().extract().jsonPath().get("data['body']").toString(), test_data.get("body"));
    }

    @And("^get the comment data and compare with values in response$")
    public void get_the_comment_data_and_compare_with_values_in_response() {
        Response get_comment_res = given(comment_api_specs.req_spec_without_auth)
                .when()
                .get(comment_api_specs.comments_resource_path + "/" + comment_id)
                .then()
                .extract()
                .response();
        get_comment_res.then().assertThat().statusCode(200);
        Assert.assertEquals("Comment data retrieved doesnt match with data returned in response data-key is post_id", res.then()
                .assertThat().extract().jsonPath().get("data['post_id']").toString(), get_comment_res.then().extract().jsonPath().get("data['post_id']").toString());
        Assert.assertEquals("Comment data retrieved doesnt match with data returned in response data-key is name", res.then()
                .assertThat().extract().jsonPath().get("data['name']").toString(), get_comment_res.then().extract().jsonPath().get("data['name']"));
        Assert.assertEquals("Comment data retrieved doesnt match with data returned in response data-key is email", res.then()
                .assertThat().extract().jsonPath().get("data['email']").toString(), get_comment_res.then().extract().jsonPath().get("data['email']"));
        Assert.assertEquals("Comment data retrieved doesnt match with data returned in response data-key is body", res.then()
                .assertThat().extract().jsonPath().get("data['body']").toString(), get_comment_res.then().extract().jsonPath().get("data['body']"));
    }

    @And("^cleanup the user data created for testing comment api$")
    public void cleanup_the_user_data() {
        Response delete_user_response = user_api_specs.delete_user_by_id(user_id);
        delete_user_response.then().assertThat().statusCode(204);
    }

    @And("^update comment \\\"([^\\\"]*)\\\" to \\\"([^\\\"]*)\\\"$")
    public void update_post_title(String key_name, String content) {
        update_comment_data.put(key_name, content);
    }

    @And("^comment is updated for the post$")
    public void update_comment() {
        res = given(comment_api_specs.req_spec_with_auth)
                .when()
                .body(update_comment_data)
                .patch(comment_api_specs.comments_resource_path + "/" + comment_id)
                .then()
                .extract()
                .response();
    }

    @And("^delete the comment$")
    public void delete_the_post() {
        res = given(comment_api_specs.req_spec_with_auth)
                .when()
                .delete(comment_api_specs.comments_resource_path + "/" + comment_id)
                .then()
                .extract()
                .response();
    }

    @And("^verify getting the deleted comment returns \"([^\"]*)\"$")
    public void verify_getting_the_deleted_comment(String error_message) {
        Response get_res = given(comment_api_specs.req_spec_without_auth)
                .when()
                .get(comment_api_specs.comments_resource_path + "/" + comment_id)
                .then()
                .extract()
                .response();
        get_res.then().assertThat().statusCode(404);
        Assert.assertTrue("Error message didnt match message is " + error_message, get_res.then().assertThat().extract().jsonPath().get("data['message']").toString().equalsIgnoreCase(error_message));

    }

    @And("^retrieve the comment")
    public void retrieve_post() {
        res = given(comment_api_specs.req_spec_without_auth)
                .when()
                .get(comment_api_specs.comments_resource_path + "/" + comment_id)
                .then()
                .extract()
                .response();
    }

    @And("^create multiple comments with following details$")
    public void create_multiple_comments_with_following_details(List<Map<String, String>> test_data) {
        multiple_comments_res_list = new ArrayList<>();
        for (Map<String, String> each_data : test_data) {
            multiple_comments_res_list.add(comment_api_specs.create_comment(each_data, post_id));
        }
    }

    @And("^verify error messages in response when invalid values are passed to comment api$")
    public void validate_invalid_input_err_msg(List<Map<String, String>> test_data) {
        try {
            for (Map<String, String> each_data : test_data) {
                create_comment_data.put("name", each_data.get("name").equalsIgnoreCase("empty") ? "" : each_data.get("name"));
                create_comment_data.put("email", each_data.get("email").equalsIgnoreCase("empty") ? "" : each_data.get("email"));
                create_comment_data.put("body", each_data.get("body").equalsIgnoreCase("empty") ? "" : each_data.get("body"));
                res = comment_api_specs.create_comment(create_comment_data, post_id);
                res.then().assertThat().statusCode(Integer.parseInt(each_data.get("status_code")));
                Assert.assertTrue("Field name in the error response didnt match", res.then().assertThat().extract().jsonPath().get("data[0]['field']").toString().equalsIgnoreCase(each_data.get("field_name").toString()));
                Assert.assertTrue("Error message in the error response didnt match", res.then().assertThat().extract().jsonPath().get("data[0]['message']").toString().equalsIgnoreCase(each_data.get("error_message").toString()));
            }
        } catch (AssertionError err) {
            cleanup_the_user_data();
            Assert.fail(err.getMessage() + "\n" + err.getCause());
        }
        cleanup_the_user_data();
    }


    @And("^verify response_code of each comment api response is (.+)$")
    public void verify_res_code_res_list(String response_code) {
        if (multiple_comments_res_list == null || multiple_comments_res_list.size() == 0) {
            Assert.fail("List of responses is empty, hence marking the test case as fail");
        }
        for (Response each_res : multiple_comments_res_list) {
            comment_api_specs.verify_response_code(each_res, Integer.parseInt(response_code));
        }
    }

    @And("^verify response time of every comment of the user$")
    public void verify_response_time() {
        if (multiple_comments_res_list == null || multiple_comments_res_list.size() == 0) {
            Assert.fail("List of responses is empty, hence marking the test case as fail");
        }
        for (Response each_res : multiple_comments_res_list) {
            comment_api_specs.verify_response_time(each_res);
        }
    }

    @And("^verify comment data by retrieving all the comments under a post$")
    public void verify_nested_comment_data() {
        res = given(comment_api_specs.req_spec_without_auth)
                .when()
                .get(post_api_specs.posts_resource_path + "/" + post_id + "/comments")
                .then()
                .extract()
                .response();
        List<Map<String, String>> post_list = res.then().extract().jsonPath().getList("data");
        Map<String, Map<String, String>> searchable_map = new HashMap<>();
        for (Map<String, String> each_comment : post_list) {
            searchable_map.put(String.valueOf(each_comment.get("id")), each_comment);
        }
        for (Response each_res : multiple_comments_res_list) {
            String comment_id = each_res.then().extract().jsonPath().get("data['id']").toString();
            Assert.assertEquals("Posts data retrieved doesnt match with data shown in response body data-key is id", each_res.then().assertThat().extract().jsonPath().get("data['id']").toString(), String.valueOf(searchable_map.get(comment_id).get("id")));
            Assert.assertEquals("Posts data retrieved doesnt match with data shown in response body data-key is post_id", each_res.then().assertThat().extract().jsonPath().get("data['post_id']").toString(), String.valueOf(searchable_map.get(comment_id).get("post_id")));
            Assert.assertEquals("Posts data retrieved doesnt match with data shown in response body data-key is name", each_res.then().assertThat().extract().jsonPath().get("data['name']").toString(), searchable_map.get(comment_id).get("name"));
            Assert.assertEquals("Posts data retrieved doesnt match with data shown in response body data-key is email", each_res.then().assertThat().extract().jsonPath().get("data['email']").toString(), searchable_map.get(comment_id).get("email"));
            Assert.assertEquals("Posts data retrieved doesnt match with data shown in response body data-key is body", each_res.then().assertThat().extract().jsonPath().get("data['body']").toString(), searchable_map.get(comment_id).get("body"));

        }
        Assert.assertEquals("Number of test data doesnt match with number of posts for the user", searchable_map.size(), multiple_comments_res_list.size());
    }
}
