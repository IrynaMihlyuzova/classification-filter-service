Feature: Entellect Sentence Service Healthcheck

	@healthcheck @async
	Scenario: Successful Entellect Sentence Async Service Healthcheck
		Given the system property 'environment' is empty, it is defaulted to 'dev'
		And the system property 'dnsZone' is empty, it is defaulted to 'cef-nonprod.elsevier.com'
		And all system properties will be available as placeholders
		When a submission is made via 'GET' to 'http://>{environment}-classification-filter-async.>{dnsZone}:80/health'
		Then the last response had a HTTP status code of '200'

	@healthcheck @sync
	Scenario: Successful Entellect Sentence Sync Service Healthcheck
		Given the system property 'environment' is empty, it is defaulted to 'dev'
		And the system property 'dnsZone' is empty, it is defaulted to 'cef-nonprod.elsevier.com'
		And all system properties will be available as placeholders
		When a submission is made via 'GET' to 'http://>{environment}-classification-filter-sync.>{dnsZone}:80/health'
		Then the last response had a HTTP status code of '200'
