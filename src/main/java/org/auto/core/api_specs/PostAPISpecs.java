package org.auto.core.api_specs;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class PostAPISpecs extends BaseAPISpecs{

    public RequestSpecification req_spec_with_auth;
    public RequestSpecification req_spec_without_auth;
    public String posts_resource_path = "/public/v1/posts";
    public String user_resource_path = "/public/v1/users";

    public PostAPISpecs() {
        req_spec_with_auth = get_req_spec_with_auth();
        req_spec_without_auth = get_req_spec_without_auth();
    }

    public Response create_post(Map<String, String> test_data, String user_id){
        Response res = given(req_spec_with_auth)
                .when()
                .body(test_data)
                .post(user_resource_path+"/"+user_id+"/posts")
                .then()
                .log()
                .all()
                .extract()
                .response();
        return res;
    }


}
