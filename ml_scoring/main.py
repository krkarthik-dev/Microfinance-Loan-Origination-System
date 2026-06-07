import asyncio
import json
import os
from fastapi import FastAPI
from kafka import KafkaConsumer, KafkaProducer

from ml_model import score_loan

app = FastAPI(title="ML Credit Scoring Engine")

KAFKA_BROKER = os.getenv("KAFKA_BROKER", "localhost:9092")

producer = KafkaProducer(bootstrap_servers=KAFKA_BROKER)

async def consume_loan_events():
    """
    Background task to consume loan_events, calculate credit scores,
    and publish them to score_results.
    """
    consumer = KafkaConsumer(
        'loan_events',
        bootstrap_servers=KAFKA_BROKER,
        group_id='ml_scoring_group',
        auto_offset_reset='earliest'
    )

    while True:
        # Polling kafka non-blockingly for the asyncio event loop
        records_dict = consumer.poll(timeout_ms=100)
        
        if not records_dict:
            await asyncio.sleep(0.1)
            continue
            
        for tp, messages in records_dict.items():
            for msg in messages:
                try:
                    if msg.value is None:
                        continue
                    event = json.loads(msg.value.decode('utf-8'))
                    
                    if event.get('type') == 'loan_created':
                        loan_data = event.get('data', {})
                        loan_id = loan_data.get('id')
                        
                        if loan_id is None:
                            continue
                        
                        # Perform feature engineering and ML scoring
                        scoring_result = score_loan(loan_data)
                        print(f"Processed loan {loan_id}, Result: {scoring_result}")
                        
                        # Publish the scoring result to 'score_results' topic
                        result_event = {
                            'loan_id': loan_id,
                            'score': scoring_result['score'],
                            'risk_tier': scoring_result['risk_tier'],
                            'pod': scoring_result['pod']
                        }
                        
                        producer.send(
                            'score_results',
                            key=str(loan_id).encode('utf-8'),
                            value=json.dumps(result_event).encode('utf-8')
                        )
                        producer.flush()
                        
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
