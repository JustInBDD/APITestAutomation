Feature: EndUser should be able to create, retrieve, update or delete user comments.

  @verify_create_comment @smoke_test
  Scenario: To test creating a comment for a post
    Given application is up and reachable for comments
    And create user for commenting with following details "Tony" "tony_test@fakemail.com" "male" "active"
    And set create_comment api for comment api test
    And create post with title as "Jarvis" and body as "Hello ! What can i do for you ?"
    And setup comment "name" as "Myself Tony"
    And setup comment "email" as "tony_test@fakemail.com"
    And setup comment "body" as "Hello Jarvis ! You rock"
    And comment is created for the post
    Then verify comment api response code is 201
    And verify comment api "create_comment" json schema
    And verify comment api response time is within configured value
    And verify details of the comment "created"
    And get the comment data and compare with values in response
    And cleanup the user data created for testing comment api

  @verify_update_comment @smoke_test
  Scenario: To test updating a comment of an user
    Given application is up and reachable for comments
    And create user for commenting with following details "Tony" "tony_test@fakemail.com" "male" "active"
    And set create_comment api for comment api test
    And create post with title as "Jarvis" and body as "Hello ! What can i do for you ?"
    And setup update_comment api
    And setup comment "name" as "Myself Tony"
    And setup comment "email" as "tony_test@fakemail.com"
    And setup comment "body" as "Hello Jarvis ! You rock"
    And comment is created for the post
    Then verify comment api response code is 201
    And update comment "name" to "Myself Tony Stark"
    And update comment "email" to "tony_test@fakemail.com"
    And update comment "body" to "Hello Jarvis! I Rock"
    And comment is updated for the post
    Then verify comment api response code is 200
    And verify comment api "update_comment" json schema
    And verify comment api response time is within configured value
    And verify details of the comment "updated"
    And get the comment data and compare with values in response
    And cleanup the user data created for testing comment api

  @verify_delete_comment @smoke_test
  Scenario: To test deleting a comment of a post
    Given application is up and reachable for comments
    And create user for commenting with following details "Tony" "tony_test@fakemail.com" "male" "active"
    And set create_comment api for comment api test
    And create post with title as "Jarvis" and body as "Hello ! What can i do for you ?"
    And setup comment "name" as "Myself Tony"
    And setup comment "email" as "tony_test@fakemail.com"
    And setup comment "body" as "Hello Jarvis ! You rock"
    And comment is created for the post
    And verify comment api response code is 201
    And delete the comment
    Then verify comment api response code is 204
    And verify comment api response time is within configured value
    And verify getting the deleted comment returns "Resource Not Found"
    And cleanup the user data created for testing comment api

  @verify_retrieve_comment @smoke_test
  Scenario: To test getting comment data of a post
    Given application is up and reachable for comments
    And create user for commenting with following details "Tony" "tony_test@fakemail.com" "male" "active"
    And set create_comment api for comment api test
    And create post with title as "Jarvis" and body as "Hello ! What can i do for you ?"
    And setup comment "name" as "Myself Tony"
    And setup comment "email" as "tony_test@fakemail.com"
    And setup comment "body" as "Hello Jarvis ! You rock"
    And comment is created for the post
    And verify comment api response code is 201
    And retrieve the comment
    And verify comment api response time is within configured value
    And verify comment api "get_comment" json schema
    And get the comment data and compare with values in response
    And cleanup the user data created for testing comment api

  @verify_create_nested_comment @smoke_test
  Scenario: To test creating multiple comments under same post for a user
    Given application is up and reachable for comments
    And create user for commenting with following details "Tony" "tony_test@fakemail.com" "male" "active"
    And set create_comment api for comment api test
    And create post with title as "Jarvis" and body as "Hello ! What can i do for you ?"
    And create multiple comments with following details
      | name   | email               | body    |
      | Jarvis | jarvis@fakemail.com | Phase 1 |
      | Jarvis | jarvis@fakemail.com | Phase 1 |
      | Jarvis | jarvis@fakemail.com | Phase 1 |
    Then verify response_code of each comment api response is 201
    And verify response time of every comment of the user
    And verify comment data by retrieving all the comments under a post
    And verify comment api "single_post_multiple_comment" json schema
    And cleanup the user data created for testing comment api

  @verify_invalid_input_err_msg @smoke_test
  Scenario: To test error message displayed for invalid inputs in comments api
    Given application is up and reachable for comments
    And create user for commenting with following details "Tony" "tony_test@fakemail.com" "male" "active"
    And set create_comment api for comment api test
    And create post with title as "Jarvis" and body as "Hello ! What can i do for you ?"
    Then verify error messages in response when invalid values are passed to comment api
      | name   | email               | body    | status_code | error_message  | field_name |
      | empty  | jarvis@fakemail.com | Phase 1 | 422         | can't be blank | name       |
      | Jarvis | empty               | Phase 1 | 422         | can't be blank | email      |
      | Jarvis | jarvis@fakemail.com | empty   | 422         | can't be blank | body       |
      | Jarvis | jarvisfakemailcom   | empty   | 422         | is invalid     | email      |