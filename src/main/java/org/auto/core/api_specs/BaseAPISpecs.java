package org.auto.core.api_specs;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.auto.core.helpers.ConfigReader;
import org.hamcrest.Matchers;
import java.util.HashMap;

/**
 * Base template representing the common api data elements
 */
public class BaseAPISpecs {


    ConfigReader cr = new ConfigReader();

    /**
     * Get req spec with auth request specification.
     *
     * @return the request specification
     */
    public RequestSpecification get_req_spec_with_auth() {
        HashMap<String, String> header = new HashMap<String, String>() {{
            put("Authorization", "Bearer " + cr.get_auth_token());
        }};
        RequestSpecification req_spec = new RequestSpecBuilder().setBaseUri(cr.get_base_uri())
                .addHeaders(header)
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON).build();
        return req_spec;
    }

    /**
     * Get req spec without auth request specification.
     *
     * @return the request specification
     */
    public RequestSpecification get_req_spec_without_auth() {
        RequestSpecification req_spec = new RequestSpecBuilder().setBaseUri(cr.get_base_uri())
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON).build();
        return req_spec;
    }

    /**
     * Verify response code.
     *
     * @param res           response object to be verified
     * @param response_code response status value to asserted for
     */
    public void verify_response_code(Response res, int response_code) {
        res.then().assertThat().statusCode(response_code);
    }

    /**
     * Verify response time.
     *
     * @param res Response object to verify the response code
     */
    public void verify_response_time(Response res) {
        res.then()
                .assertThat()
                .time(Matchers.lessThan(cr.get_max_response_time()));
    }


}
