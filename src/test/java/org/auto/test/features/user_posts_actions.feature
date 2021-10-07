Feature: EndUser should be able to create, retrieve, update or delete user posts.

  @verify_create_post @smoke_test
  Scenario: To test creating a post for an user
    Given application is up and reachable for posts
    And create user for posting with following details "Tony" "tony_test@fakemail.com" "male" "active"
    And setup create_post api
    When get user_id of user with email "tony_test@fakemail.com"
    And setup post title as "Jarvis Rocks"
    And setup post body as "Make me a donut"
    And post is created for the user
    Then verify post api response code is 201
    And verify post api "create_post" json schema
    And verify post api response time is within configured value
    And verify details of the post "created"
    And get the post data and compare with values in response
    And cleanup the user data created for testing post api

  @verify_update_post @smoke_test
  Scenario: To test updating a post of an user
    Given application is up and reachable for posts
    And create user for posting with following details "Tony" "tony_test@fakemail.com" "male" "active"
    And setup create_post api
    And setup update_post api
    When get user_id of user with email "tony_test@fakemail.com"
    And setup post title as "Jarvis Rocks"
    And setup post body as "Make me a donut"
    And post is created for the user
    And verify post api response code is 201
    And update post "title" to "Jarvis Rocks Double"
    And update post "body" to "Double the donut"
    And post is updated for the user
    Then verify post api response code is 200
    And verify post api "update_post" json schema
    And verify post api response time is within configured value
    And verify details of the post "updated"
    And get the post data and compare with values in response
    And cleanup the user data created for testing post api

  @verify_delete_post @smoke_test
  Scenario: To test deleting a post of an user
    Given application is up and reachable for posts
    And create user for posting with following details "Tony" "tony_test@fakemail.com" "male" "active"
    And setup create_post api
    When get user_id of user with email "tony_test@fakemail.com"
    And setup post title as "Jarvis Rocks"
    And setup post body as "Make me a donut"
    And post is created for the user
    And verify post api response code is 201
    And delete the post
    Then verify post api response code is 204
    And verify post api response time is within configured value
    And verify getting the deleted post returns "Resource Not Found"
    And cleanup the user data created for testing post api

  @verify_retrieve_post @smoke_test
  Scenario: To test getting post data of a user
    Given application is up and reachable for posts
    And create user for posting with following details "Tony" "tony_test@fakemail.com" "male" "active"
    And setup create_post api
    And setup update_post api
    When get user_id of user with email "tony_test@fakemail.com"
    And setup post title as "Jarvis Rocks"
    And setup post body as "Make me a donut"
    And post is created for the user
    Then verify post api response code is 201
    And retrieve the post
    And verify post api response time is within configured value
    And verify post api "get_post" json schema
    And get the post data and compare with values in response
    And cleanup the user data created for testing post api

  @verify_create_nested_post @smoke_test
  Scenario: To test creating multiple posts for same user
    Given application is up and reachable for posts
    And create user for posting with following details "Tony" "tony_test@fakemail.com" "male" "active"
    And setup create_post api
    When get user_id of user with email "tony_test@fakemail.com"
    And create multiple posts with following details
      | title       | body       |
      | post1_title | post1_body |
      | post2_title | post2body  |
      | post3_title | post3_body |
    Then verify response_code of each post api response is 201
    And verify response time of every post of the user
    And verify post data by retrieving all the posts of the user
    And verify post api "single_user_multiple_post" json schema
    And cleanup the user data created for testing post api

  @verify_invalid_input_err_msg @smoke_test
  Scenario: To test error message displayed for invalid inputs in posts api
    Given application is up and reachable for posts
    And create user for posting with following details "Tony" "tony_test@fakemail.com" "male" "active"
    And setup create_post api
    When get user_id of user with email "tony_test@fakemail.com"
    Then verify error messages in response when invalid values are passed to posts api
      | title  | body    | status_code | error_message  | field_name |
      | empty  | Phase 1 | 422         | can't be blank | title      |
      | Jarvis | empty   | 422         | can't be blank | body       |
