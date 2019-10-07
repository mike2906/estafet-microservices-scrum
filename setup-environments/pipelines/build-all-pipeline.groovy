@NonCPS
def getMicroServices(json) {
	def items = new groovy.json.JsonSlurper().parseText(json).items
	def microservices = []
	for (int i = 0; i < items.size(); i++) {
		microservices << items[i]['metadata']['name']
	}
	return microservices
}

node {
	stage ('build all microservices') {
		sh "oc get bc -n cicd --selector app=pipeline --selector type=build -o json > images.output"
		def images = readFile('images.output')
		def microservices = getMicroServices(images)
		microservices.each { microservice ->
			openshiftBuild namespace: "cicd", buildConfig: "build-${microservice}", waitTime: "300000"
			openshiftVerifyBuild namespace: "cicd", buildConfig: "build-${microservice}", waitTime: "300000" 
	  }	
	}
}
