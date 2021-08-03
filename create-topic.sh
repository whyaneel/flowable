docker exec kafka kafka-topics --zookeeper zookeeper:2181 \
                --create --topic ONE_TASK_TOPIC_JSON --partitions 1 --replication-factor 1 --if-not-exists