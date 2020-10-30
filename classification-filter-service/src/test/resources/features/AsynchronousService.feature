Feature: Asynchronous Sentence Service

    Background: The system is ready for fresh requests
        Given that a local S3 bucket named 'sendbucket' available whose URI is available in property 'sendBucketUri'
        And that a local S3 bucket named 'receivebucket' available whose URI is available in property 'receiveBucketUri'
        And there is a local SQS queue named 'sendQueue' whose URI is available in property 'sendQueueUri'
        And there is a local SQS queue named 'receiveQueue' whose URI is available in property 'receiveQueueUri'
        And the system property 'sqs.endpoint' is empty, it is defaulted to '>{sendQueueUri}'
        And the system property 'sqs.sentence.service.environment.aware.queue' is empty, it is defaulted to 'sendQueue'
        And a unique value is available as a placeholder named 'uniqueCorrelationId'
        And a HTTP server is started

    @ENTELLECT-4384
    Scenario: Getting sentences via asynchronous call with response address
        Given that S3 will respond to a request for '>{sendBucketUri}/SentenceInput.json' with the contents of file from '/PreviousServiceTestResult.json'
        When a message is sent to the SQS queue with URI '>{sendQueueUri}' with content:
        """
        {
            "documentToEnrichUri":">{sendBucketUri}/SentenceInput.json",
            "uploadEnrichedDocumentUri":">{receiveBucketUri}/SentenceOutput.json",
            "correlationId":">{uniqueCorrelationId}",
            "responseAddress":">{receiveQueueUri}"
        }
        """
        Then the next message received on queue with URI '>{receiveQueueUri}' will be JSON and have the properties:
        """
        {
            "documentToEnrichUri":">{sendBucketUri}/SentenceInput.json",
            "uploadEnrichedDocumentUri":">{receiveBucketUri}/SentenceOutput.json",
            "correlationId":">{uniqueCorrelationId}",
            "responseAddress":">{receiveQueueUri}"
        }
        """
        And there will be a JSON file with properties available at '>{receiveBucketUri}/SentenceOutput.json' containing:
        """
        {
          "correlationId": ">{uniqueCorrelationId}",
          "response": {
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
    
    @ENTELLECT-4384
    Scenario: Getting sentences via asynchronous call without response address
        Given that S3 will respond to a request for '>{sendBucketUri}/SentenceInput.json' with the contents of file from '/PreviousServiceTestResult.json'
        When a message is sent to the SQS queue with URI '>{sendQueueUri}' with content:
        """
        {
            "documentToEnrichUri":">{sendBucketUri}/SentenceInput.json",
            "uploadEnrichedDocumentUri":">{receiveBucketUri}/SentenceOutput.json"
        }
        """
        Then there will eventually be a JSON file with properties available at '>{receiveBucketUri}/SentenceOutput.json' containing:
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
              ],
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
        
    @ENTELLECT-4384
    Scenario: The request message is returned in response message
        Given that S3 will respond to a request for '>{sendBucketUri}/SentenceInput.json' with the contents of file from '/PreviousServiceTestResult.json'
        When a message is sent to the SQS queue with URI '>{sendQueueUri}' with content:
        """
        {
            "documentToEnrichUri":">{sendBucketUri}/SentenceInput.json",
            "uploadEnrichedDocumentUri":">{receiveBucketUri}/SentenceOutput.json",
            "correlationId":">{uniqueCorrelationId}",
            "responseAddress":">{receiveQueueUri}"
        }
        """
        Then the next message received on queue with URI '>{receiveQueueUri}' will be JSON and have the properties:
        """
        {
            "documentToEnrichUri":">{sendBucketUri}/SentenceInput.json",
            "uploadEnrichedDocumentUri":">{receiveBucketUri}/SentenceOutput.json",
            "correlationId":">{uniqueCorrelationId}",
            "responseAddress":">{receiveQueueUri}"
        }
        """
        And there will be a JSON file with properties available at '>{receiveBucketUri}/SentenceOutput.json' containing:
        """
        {
          "correlationId": ">{uniqueCorrelationId}"
        }
        """
        
    @ENTELLECT-656
    Scenario: Responses are JSON-LD
        Given that S3 will respond to a request for '>{sendBucketUri}/SentenceInput.json' with the contents of file from '/PreviousServiceTestResult.json'
        When a message is sent to the SQS queue with URI '>{sendQueueUri}' with content:
        """
        {
            "documentToEnrichUri":">{sendBucketUri}/SentenceInput.json",
            "uploadEnrichedDocumentUri":">{receiveBucketUri}/SentenceOutput.json",
            "responseAddress":">{receiveQueueUri}"
        }
        """
        Then the next message received on queue with URI '>{receiveQueueUri}' will be JSON and have the properties:
        """
        {
            "correlationId":"1234"
        }
        """
        And there will be a JSON file with properties available at '>{receiveBucketUri}/SentenceOutput.json' containing:
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
        Given that S3 will respond to a request for '>{sendBucketUri}/SentenceInput.json' with the contents of file from '/PreviousServiceTestResult.json'
        When a message is sent to the SQS queue with URI '>{sendQueueUri}' with content:
        """
        {
            "documentToEnrichUri":">{sendBucketUri}/SentenceInput.json",
            "uploadEnrichedDocumentUri":">{receiveBucketUri}/SentenceOutput.json",
            "responseAddress":">{receiveQueueUri}"
        }
        """
        Then the next message received on queue with URI '>{receiveQueueUri}' will be JSON and have the properties:
        """
        {
            "correlationId":"1234"
        }
        """
        And there will be a JSON file with properties available at '>{receiveBucketUri}/SentenceOutput.json' containing:
        """
        {
          "provenance": [{
            "serviceVersion": "http://elsevier.com/sentence-service/<{ignoreShaOfServiceAsItWillChange}",
            "externalResourceVersion": ["http://opennlp.sourceforge.net/models-1.5/en-sent.bin"]
          }]
        }
        """
        
    @ENTELLECT-4384
    Scenario: Correlation ID in message overrides the payload
        Given that S3 will respond to a request for '>{sendBucketUri}/SentenceInput.json' with the contents of file from '/PreviousServiceTestResult.json'
        When a message is sent to the SQS queue with URI '>{sendQueueUri}' with content:
        """
        {
            "documentToEnrichUri":">{sendBucketUri}/SentenceInput.json",
            "uploadEnrichedDocumentUri":">{receiveBucketUri}/SentenceOutput.json",
            "correlationId":">{uniqueCorrelationId}",
            "responseAddress":">{receiveQueueUri}"
        }
        """
        Then the next message received on queue with URI '>{receiveQueueUri}' will be JSON and have the properties:
        """
        {
            "documentToEnrichUri":">{sendBucketUri}/SentenceInput.json",
            "uploadEnrichedDocumentUri":">{receiveBucketUri}/SentenceOutput.json",
            "correlationId":">{uniqueCorrelationId}",
            "responseAddress":">{receiveQueueUri}"
        }
        """
        And there will be a JSON file with properties available at '>{receiveBucketUri}/SentenceOutput.json' containing:
        """
        {
          "correlationId": ">{uniqueCorrelationId}"
        }
        """
        
    @ENTELLECT-4384
    Scenario: The request payload is returned in the response payload
        Given that S3 will respond to a request for '>{sendBucketUri}/SentenceInput.json' with the contents of file from '/PreviousServiceTestResult.json'
        When a message is sent to the SQS queue with URI '>{sendQueueUri}' with content:
        """
        {
            "documentToEnrichUri":">{sendBucketUri}/SentenceInput.json",
            "uploadEnrichedDocumentUri":">{receiveBucketUri}/SentenceOutput.json",
            "responseAddress":">{receiveQueueUri}"
        }
        """
        Then the next message received on queue with URI '>{receiveQueueUri}' will be JSON and have the properties:
        """
        {
            "correlationId":"1234"
        }
        """
        And there will be a JSON file with properties available at '>{receiveBucketUri}/SentenceOutput.json' containing:
        """
        {
          "correlationId": "1234",
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
        
    Scenario: No enrichment produces an empty annotation set
        Given that S3 will respond to a request for '>{sendBucketUri}/SentenceInput.json' with the contents of file from '/EmptyFixture.json'
        When a message is sent to the SQS queue with URI '>{sendQueueUri}' with content:
        """
        {
            "documentToEnrichUri":">{sendBucketUri}/SentenceInput.json",
            "uploadEnrichedDocumentUri":">{receiveBucketUri}/SentenceOutput.json",
            "responseAddress":">{receiveQueueUri}"
        }
        """
        Then the next message received on queue with URI '>{receiveQueueUri}' will be JSON and have the properties:
        """
        {
            "correlationId":"5678"
        }
        """
        And there will be a JSON file with properties available at '>{receiveBucketUri}/SentenceOutput.json' containing:
        """
        {
          "response": {
            "annotationSets": {
              "sentences": []
            }
          }
        }
        """
        
    @ENTELLECT-4384
    Scenario: An error is generated when there is no document to enrich
        When a message is sent to the SQS queue with URI '>{sendQueueUri}' with content:
        """
        {
            "documentToEnrichUri":">{sendBucketUri}/SentenceInput.json",
            "uploadEnrichedDocumentUri":">{receiveBucketUri}/SentenceOutput.json",
            "responseAddress":">{receiveQueueUri}"
        }
        """
        Then the next message received on queue with URI '>{receiveQueueUri}' will be JSON and have the properties:
        """
        {
            "documentToEnrichUri":">{sendBucketUri}/SentenceInput.json",
            "uploadEnrichedDocumentUri":">{receiveBucketUri}/SentenceOutput.json",
            "responseAddress":">{receiveQueueUri}"
        }
        """
        And there will be a JSON file with properties available at '>{receiveBucketUri}/SentenceOutput.json' containing:
        # TODO Why do we have no provenance?
        """
        {
            "@context": {
                "@vocab": "http://www.elsevier.com/"
            },
            "@type": "ServicePayload",
            "errors": [{
                "@type": "ServiceError",
                "errorMessage": "The specified key does not exist (Service: Amazon S3; Status Code: 404; Error Code: NoSuchKey; Request ID: <{ignoreRestOfString}",
                "stackTrace": "<{ignoreTheStackTraceThatWillChange}",
                "httpStatusCode": 400
            }]
        }
        """
        
    @ENTELLECT-657
    Scenario: An error is generated for an invalid document
        Given that S3 will respond to a request for '>{sendBucketUri}/SentenceInput.json' with the contents of file from '/cucumber.xml'
        When a message is sent to the SQS queue with URI '>{sendQueueUri}' with content:
        """
        {
            "documentToEnrichUri":">{sendBucketUri}/SentenceInput.json",
            "uploadEnrichedDocumentUri":">{receiveBucketUri}/SentenceOutput.json",
            "responseAddress":">{receiveQueueUri}"
        }
        """
        Then the next message received on queue with URI '>{receiveQueueUri}' will be JSON and have the properties:
        """
        {
            "documentToEnrichUri":">{sendBucketUri}/SentenceInput.json",
            "uploadEnrichedDocumentUri":">{receiveBucketUri}/SentenceOutput.json",
            "responseAddress":">{receiveQueueUri}"
        }
        """
        And there will be a JSON file with properties available at '>{receiveBucketUri}/SentenceOutput.json' containing:
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
        
    @ENTELLECT-657
    Scenario: An error is generated with a correlation ID
        Given that S3 will respond to a request for '>{sendBucketUri}/SentenceInput.json' with the contents of file from '/cucumber.xml'
        When a message is sent to the SQS queue with URI '>{sendQueueUri}' with content:
        """
        {
            "documentToEnrichUri":">{sendBucketUri}/SentenceInput.json",
            "uploadEnrichedDocumentUri":">{receiveBucketUri}/SentenceOutput.json",
            "correlationId": ">{uniqueCorrelationId}",
            "responseAddress":">{receiveQueueUri}"
        }
        """
        Then the next message received on queue with URI '>{receiveQueueUri}' will be JSON and have the properties:
        """
        {
            "correlationId": ">{uniqueCorrelationId}"
        }
        """
        And there will be a JSON file with properties available at '>{receiveBucketUri}/SentenceOutput.json' containing:
        """
        {
            "correlationId": ">{uniqueCorrelationId}",
            "errors": [{
                "httpStatusCode": 400
            }]
        }
        """