import asyncio
import json
import os
from fastapi import FastAPI
from confluent_kafka import Consumer, Producer

from ml_model import score_loan

app = FastAPI(title="ML Credit Scoring Engine")

KAFKA_BROKER = os.getenv("KAFKA_BROKER", "localhost:9092")

consumer_conf = {
    'bootstrap.servers': KAFKA_BROKER,
    'group.id': 'ml_scoring_group',
    'auto.offset.reset': 'earliest'
}

producer_conf = {
    'bootstrap.servers': KAFKA_BROKER
}

producer = Producer(producer_conf)

async def consume_loan_events():
    """
    Background task to consume loan_events, calculate credit scores,
    and publish them to score_results.
    """
    consumer = Consumer(consumer_conf)
    consumer.subscribe(['loan_events'])

    while True:
        # Polling kafka non-blockingly for the asyncio event loop
        msg = consumer.poll(0.1)
        
        if msg is None:
            await asyncio.sleep(0.1)
            continue
            
        if msg.error():
            print(f"Kafka Consumer Error: {msg.error()}")
            continue
            
        try:
            event = json.loads(msg.value().decode('utf-8'))
            
            if event.get('type') == 'loan_created':
                loan_data = event.get('data', {})
                loan_id = loan_data.get('id')
                
                if loan_id is None:
                    continue
                
                # Perform feature engineering and ML scoring
                score = score_loan(loan_data)
                print(f"Processed loan {loan_id}, Score: {score}")
                
                # Publish the scoring result to 'score_results' topic
                result_event = {
                    'loan_id': loan_id,
                    'score': score
                }
                
                producer.produce(
                    'score_results',
                    key=str(loan_id),
                    value=json.dumps(result_event)
                )
                producer.poll(0)
                
        except json.JSONDecodeError:
            print("Received malformed JSON message.")
        except Exception as e:
            print(f"Error processing message: {e}")

@app.on_event("startup")
async def startup_event():
    """
    Start the Kafka consumer in the background.
    """
    asyncio.create_task(consume_loan_events())

@app.get("/health")
def health_check():
    return {"status": "healthy"}
