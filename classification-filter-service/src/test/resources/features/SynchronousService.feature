Feature: Synchronous Sentence Service 

    Background: The system is ready for fresh requests
        Given a unique value is available as a placeholder named 'uniqueCorrelationId' 
        And the next multipart upload is started
        And the system property 'spring.profiles.active' is empty, it is defaulted to 'patent'
        And a HTTP server is started
        
    @ENTELLECT-603 
    Scenario: Getting sentences via synchronous call 
        Given the next multipart with name 'file' is a file from '/PreviousServiceTestResult.json' of type 'multipart/form-data' 
        When the multipart payload is submitted via 'POST' to 'C:/Program Files/Git/classificationFilter?correlationId=>{uniqueCorrelationId}' 
        Then the last response had a HTTP status code of '200' 
        And the response JSON contains the following properties: 
        """
        {
          "correlationId": ">{uniqueCorrelationId}",
          "response": {
            "text": "Subject:\r\n\r\ndrug-drug interactions with Saxenda® (liraglutide injection).  \r\n\r\n",
            "annotationSets": {
              "sentences": [
                {
                  "start": 0,
                  "end": 8
                },
                {
                  "start": 12,
                  "end": 73
                }
              ]
            }
          }
        }
        """
        
    @ENTELLECT-656
    Scenario: Responses are JSON-LD
        Given the next multipart with name 'file' is a file from '/PreviousServiceTestResult.json' of type 'multipart/form-data' 
        When the multipart payload is submitted via 'POST' to 'C:/Program Files/Git/classificationFilter' 
        Then the last response had a HTTP status code of '200' 
        And the response JSON contains the following properties: 
        """
        {
            "@context": {
                "@vocab": "http://www.elsevier.com/"
            },
            "@type": "ServicePayload",
            "response": {
                "@context": {
                    "@vocab": "http://schema.org/"
                },
                "@type": "MessageWithTextAndAnnotationSets",
                "annotationSets": {
                    "metadata": [{
                        "@type": "Annotation"
                    }],
                    "styles": [{
                        "@type": "Annotation"
                    }, {
                        "@type": "Annotation"
                    }, {
                        "@type": "Annotation"
                    }, {
                        "@type": "Annotation"
                    }],
                    "sentences": [{
                        "@type": "Annotation"
                    }, {
                        "@type": "Annotation"
                    }]
                }
            },
            "provenance": [{
                "@context": {
                    "prov": "http://www.w3.org/ns/prov#",
                    "externalResourceVersion": "prov:used",
                    "serviceVersion": "prov:wasAttributedTo"
                },
                "@type": "Provenance"
            }]
        }
        """
        
    @ENTELLECT-656
    Scenario: Responses have provenance
        Given the next multipart with name 'file' is a file from '/PreviousServiceTestResult.json' of type 'multipart/form-data' 
        When the multipart payload is submitted via 'POST' to 'C:/Program Files/Git/classificationFilter' 
        Then the last response had a HTTP status code of '200' 
        And the response JSON contains the following properties: 
        """
        {
            "provenance": [{
                "serviceVersion": "http://elsevier.com/sentence-service/<{ignoreShaOfServiceAsItWillChange}",
                "externalResourceVersion": ["http://opennlp.sourceforge.net/models-1.5/en-sent.bin"]
            }]
        }
        """
        
    @ENTELLECT-1287
    Scenario: Message correlation ID in payload is overriden with message correlation ID in URL
        Given the next multipart with name 'file' is a file from '/PreviousServiceTestResult.json' of type 'multipart/form-data' 
        When the multipart payload is submitted via 'POST' to 'C:/Program Files/Git/classificationFilter?correlationId=>{uniqueCorrelationId}' 
        Then the last response had a HTTP status code of '200' 
        And the response JSON contains the following properties: 
        """
        {
          "correlationId": ">{uniqueCorrelationId}"
        }
        """
        
    Scenario: Request data is returned in the response
        Given the next multipart with name 'file' is a file from '/PreviousServiceTestResult.json' of type 'multipart/form-data' 
        When the multipart payload is submitted via 'POST' to 'C:/Program Files/Git/classificationFilter' 
        Then the last response had a HTTP status code of '200' 
        And the response JSON contains the following properties: 
        """
        {
          "response": {
            "text": "Subject:\r\n\r\ndrug-drug interactions with Saxenda® (liraglutide injection).  \r\n\r\n",
            "annotationSets": {
              "metadata": [
                {
                  "start": 0,
                  "end": 79,
                  "attributes": {
                    "input": "s3://input/test.doc",
                    "created": "1970-01-01T00:00:00.000",
                    "author": "Novo Nordisk Medical Information",
                    "modified": "1970-01-01T00:00:00.000",
                    "modifiedBy": "Entellect Test",
                    "title": "Drug Interactions"
                  }
                }
              ],
              "styles": [
                {
                  "name": "span",
                  "start": 0,
                  "end": 8,
                  "attributes": {
                    "font-weight": "bold",
                    "font-size": "10.0pt"
                  }
                },
                {
                  "name": "span",
                  "start": 12,
                  "end": 47,
                  "attributes": {
                    "font-size": "10.0pt"
                  }
                },
                {
                  "name": "span",
                  "start": 47,
                  "end": 48,
                  "attributes": {
                    "vertical-align": "super",
                    "font-size": "10.0pt"
                  }
                },
                {
                  "name": "span",
                  "start": 48,
                  "end": 75,
                  "attributes": {
                    "font-size": "10.0pt"
                  }
                }
              ]
            }
          }
        }
        """
        
    Scenario:  No enrichment produces an empty annotation set
        Given the next multipart with name 'file' is a file from '/EmptyFixture.json' of type 'multipart/form-data'
        When the multipart payload is submitted via 'POST' to 'C:/Program Files/Git/classificationFilter' 
        Then the last response had a HTTP status code of '200' 
        And the response JSON contains the following properties: 
        """
        {
          "response": {
            "annotationSets": {
              "sentences": []
            }
          }
        }
        """
    
    @ENTELLECT-657
    Scenario: An error is generated for an invalid document
        Given the next multipart with name 'file' is a file from '/cucumber.xml' of type 'multipart/form-data'
        When the multipart payload is submitted via 'POST' to 'C:/Program Files/Git/classificationFilter' 
        # TODO Why is this error generating a 200?
        Then the last response had a HTTP status code of '200' 
        And the response JSON contains the following properties: 
        """
        {
            "@context": {
                "@vocab": "http://www.elsevier.com/"
            },
            "@type": "ServicePayload",
            "errors": [{
                "@type": "ServiceError",
                "errorMessage": "Invalid input.",
                "stackTrace": "<{ignoreTheStackTraceThatWillChange}",
                "httpStatusCode": 400
            }],
            "provenance": [{
                "@context": {
                    "prov": "http://www.w3.org/ns/prov#",
                    "externalResourceVersion": "prov:used",
                    "serviceVersion": "prov:wasAttributedTo"
                },
                "@type": "Provenance",
                "serviceVersion": "http://elsevier.com/sentence-service/<{ignoreShaOfServiceAsItWillChange}",
                "externalResourceVersion": ["http://opennlp.sourceforge.net/models-1.5/en-sent.bin"]
            }]
        }
        """