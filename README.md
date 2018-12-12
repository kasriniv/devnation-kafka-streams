# devnation-kafka-streams

word-rest-service -> RESTful service that writes to Kafka
demo-kafka-streams -> counts words

demo-word-count-rest -> aggregate the output of the Kafka Streams word count app upstream and exposes a REST service 
that shows the word counts

all 3 of them are just a mvn fabric8:deploy away
the POM has the environment variable with the Kafka service name
