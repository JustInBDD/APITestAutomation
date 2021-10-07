package org.auto.core.api_specs;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * Common Comments API data elements and functions
 */
public class CommentApiSpecs extends BaseAPISpecs {

    /**
     * The Req spec with auth.
     */
    public  RequestSpecification req_spec_with_auth;
    /**
     * The Req spec without auth.
     */
    public  RequestSpecification req_spec_without_auth;
    /**
     * The Comments resource path.
     */
    public  String comments_resource_path = "/public/v1/comments";
    /**
     * The Posts resource path.
     */
    public String posts_resource_path = "/public/v1/posts";

    /**
     * Instantiates a new Comment api specs.
     */
    public CommentApiSpecs() {
        req_spec_with_auth = get_req_spec_with_auth();
        req_spec_without_auth = get_req_spec_without_auth();
    }

    /**
     * Create comment
     *
     * @param test_data key,value pairs to create comment
     * @param post_id   id of the post on which the comment needs to created
     * @return the response
     */
    public Response create_comment(Map<String, String> test_data, String post_id){
        Response res = given(req_spec_with_auth)
                .when()
                .body(test_data)
                .post(posts_resource_path+"/"+post_id+"/comments")
                .then()
                .extract()
                .response();
        return res;
    }



}
