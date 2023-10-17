Feature: Taxonomy

  Scenario: Generate Taxonomy JSON
    Given the infrastructure is up and running
    When someone requests the taxonomy to be generated
    Then check status code equals 200

  Scenario: Get taxonomy json standard version
    Given a json file has already been created
    When a partner requests a taxonomy json as standard version
    Then check status code equals 200

  Scenario: Get taxonomy json topicflag version
    Given a json file has already been created
    When a partner requests a taxonomy json as topicflag version
    Then check status code equals 200

  Scenario: Get taxonomy csv
    Given a json file has already been created
    When a partner requests a taxonomy csv as standard version
    Then check status code equals 200