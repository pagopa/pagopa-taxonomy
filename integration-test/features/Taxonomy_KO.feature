Feature: Taxonomy

  Scenario: No CSV file in the storage when trying to generate taxonomy
    Given the infrastructure is up and running
    And There is no CSV file in the storage
    When someone requests the taxonomy to be generated
    Then check status code equals 404

  Scenario: No JSON file in the storage when requesting taxonomy
    Given There is no JSON file in the storage
    When a partner requests a taxonomy json as standard version
    Then check status code equals 404

  Scenario: Get taxonomy json invalid version
    Given a json file has already been created
    When a partner requests a taxonomy json as none version
    Then check status code equals 400

  Scenario: Get taxonomy invalid extension
    Given a json file has already been created
    When a partner requests a taxonomy zip as standard version
    Then check status code equals 400