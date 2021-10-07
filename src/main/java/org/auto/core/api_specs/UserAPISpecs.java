package org.auto.core.api_specs;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;

import java.util.HashMap;

import static io.restassured.RestAssured.given;

public class UserAPISpecs extends BaseAPISpecs {

    public  RequestSpecification req_spec_with_auth;
    public  RequestSpecification req_spec_without_auth;
    public  String user_resource_path = "/public/v1/users";

    public UserAPISpecs() {
        req_spec_with_auth = get_req_spec_with_auth();
        req_spec_without_auth = get_req_spec_without_auth();
    }

    public String get_user_id_by_email(String email){
        String user_id;
        Response get_user_id = given(req_spec_without_auth)
                .when()
                .queryParam("email", email)
                .get(user_resource_path)
                .then()
                .extract()
                .response();
        Assert.assertEquals("Zero/duplicate data found when user data is retrieved using email", "1", get_user_id.then().assertThat().extract().body().jsonPath().get("meta['pagination']['total']").toString());
        user_id = get_user_id.then().extract().jsonPath().get("data[0]['id']").toString();
        return user_id;
    }

    public Response delete_user_by_id(String user_id){
        Response res = given(req_spec_with_auth)
                .log()
                .all()
                .when()
                .log()
                .all()
                .delete(user_resource_path+ "/" + user_id)
                .then()
                .log()
                .all()
                .extract()
                .response();
        return res;
    }

    public Response create_user(HashMap<String, String> test_data){
        Response res = given(req_spec_with_auth)
                .when()
                .body(test_data)
                .post(user_resource_path)
                .then()
                .log()
                .all()
                .extract()
                .response();
        return res;
    }



}
