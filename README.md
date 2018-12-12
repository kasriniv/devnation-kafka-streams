# devnation-kafka-streams

1.word-rest-service -> RESTful service that writes to Kafka

2. demo-kafka-streams -> counts words

3. demo-word-count-rest -> aggregate the output of the Kafka Streams word count app upstream and exposes a REST service 
that shows the word counts

all 3 of them are just a mvn fabric8:deploy away
the POM has the environment variable with the Kafka service name


To POST message to topic :
curl -X POST http://<route>/camel/lines -d 'myword2'
  to GET from topic:
  curl http://demo-word-count-rest-<rest of route>/camel/updates
  curl http://demo-word-count-rest-<rest of route>/camel/word-count
