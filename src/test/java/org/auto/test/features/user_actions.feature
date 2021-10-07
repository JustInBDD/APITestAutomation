Feature: EndUser should be able to create, retrieve, update or delete application user.

  @verify_create_user @smoke_test
  Scenario Outline: Verify create user api functionality
    Given application is up and reachable
    And setup create_user api
    When set name as <name>
    And set gender as <gender>
    And set email as <email>
    And set status as <status>
    And creates user with the details provided
    Then verify the response code is <status_code>
    And verify the <schema_name> json schema
    And verify response time is within configured value
    And verify the user is created successfully
    And verify details of user created is persisted in the application
    And cleanup the user data created
    Examples:
      | name         | gender | email                            | status   | status_code | schema_name |
      | Tony Stark   | male   | ironman_test@fakemail.com        | active   | 201         | create_user |
      | Steve Rogers | male   | captainamerica_test@fakemail.com | active   | 201         | create_user |
      | Natasha      | female | spy_test@fakemail.com            | inactive | 201         | create_user |

  @verify_update_user @smoke_test
  Scenario Outline: Verify you are able to update user details
    Given application is up and reachable
    And update details of user with email <search_by_email>
    When update name of user to <name>
    And update gender of user to <gender>
    And update status of user to <status>
    And update email of user to <email>
    And testdata is prepared
    And updates user with details provided
    Then verify the response code is <status_code>
    And verify the <schema_name> json schema
    And verify response time is within configured value
    And verify the user details are updated successfully
    And verify details of user updated is persisted in the application
    And cleanup the user data created
    Examples:
      | search_by_email                  | name           | gender | email                                    | status   | status_code | schema_name |
      | ironman_test@fakemail.com        | Tony Stark     | male   | ironman_test_updated@fakemail.com        | inactive | 200         | update_user |
      | captainamerica_test@fakemail.com | Steve Rogers M | male   | captainamerica_test_updated@fakemail.com | active   | 200         | update_user |
      | spy_test@fakemail.com            | Natasha R      | female | spy_test@fakemail.com                    | active   | 200         | update_user |

  @verify_get_user @smoke_test
  Scenario Outline: Verify existing user data from get user api
    Given application is up and reachable
    When get user details with <email>
    And user is created in application with <name> <email> <gender> <status> details
    Then verify user details <name> <email> <gender> <status>
    And verify the response code is <status_code>
    And verify the <schema_name> json schema
    And verify response time is within configured value
    And cleanup the user data created
    Examples:
      | name           | gender | email                                    | status   | status_code | schema_name     |
      | Tony Stark     | male   | ironman_test_updated@fakemail.com        | inactive | 200         | get_single_user |
      | Steve Rogers M | male   | captainamerica_test_updated@fakemail.com | active   | 200         | get_single_user |
      | Natasha R      | female | spy_test@fakemail.com                    | active   | 200         | get_single_user |

  @verify_delete_user @smoke_test
  Scenario Outline: Verify deleting an user from the application
    Given application is up and reachable
    And user is created in application with <name> <email> <gender> <status> details
    And delete user with email <email>
    When delete user
    Then verify the response code is <status_code>
    And verify response time is within configured value
    And verify user with <email> is not present in the application
    Examples:
      | name           | gender | email                                    | status   | status_code |
      | Tony Stark     | male   | ironman_test_updated@fakemail.com        | inactive | 204         |
      | Steve Rogers M | male   | captainamerica_test_updated@fakemail.com | active   | 204         |
      | Natasha R      | female | spy_test@fakemail.com                    | active   | 204         |


  @verify_user_input_parameters @smoke_test
  Scenario Outline: Validate error message displayed when invalid input is given
    Given application is up and reachable
    And setup create_user api
    When set name as <name>
    And set gender as <gender>
    And set email as <email>
    And set status as <status>
    And creates user with the details provided
    Then verify the response code is <status_code>
    And verify response time is within configured value
    And verify the create_user_error_message json schema
    And verify error message <err_message> is displayed for field <field_name>
    Examples:
      | name       | gender | email                             | status     | status_code | err_message    | field_name |
      | Tony Stark | empty  | fironman_test@fakemail.com        | active     | 422         | can't be blank | gender     |
      | empty      | male   | fcaptainamerica_test@fakemail.com | active     | 422         | can't be blank | name       |
      | Natasha    | female | empty                             | inactive   | 422         | can't be blank | email      |
      | Steve      | male   | fcaptainamerica_test@fakemail.com | empty      | 422         | can't be blank | status     |
      | Steve      | male   | fcaptainamerica_testfakemailcom   | active     | 422         | is invalid     | email      |
      | Steve      | sdfwfe | fcaptainamerica_test@fakemail.com | active     | 422         | is invalid     | gender     |
      | Steve      | male   | fcaptainamerica_test@fakemail.com | sdffwefwef | 422         | is invalid     | status     |






