import mysql.connector
import boto3
import os
from elevenlabs import save
from elevenlabs.client import ElevenLabs
from newsapi import NewsApiClient
import requests
from datetime import datetime, timedelta
import logging
# Set API keys and other sensitive information directly in the code
#set_api_key("bfb02dc484476a5721a19551f7c5f61e")

client = ElevenLabs(
  api_key="" # Defaults to ELEVEN_API_KEY
)


logger = logging.getLogger()
logger.setLevel(logging.INFO)  # You can set it to DEBUG, WARNING, etc.



bucket_name = ''
access_key = ''
secret_key = ''
news_api_key = ''
API_URL = "https://www.stack-inference.com/run_deployed_flow?flow_id=65b4b6cce1b68786a2c68699&org=8a8e6770-77a7-456d-a8d8-f2f2507b0a04"
headers = {
    'Authorization': 'Bearer oken',
    'Content-Type': 'application/json'
}
db_config = {
    'host': '',
    'user': 'admin',
    'password': '',
    'database': 'newsloop'
}

def speech_writer_function(payload_str):
    logger.info("insiwde speech writer.")
    payload = {"in-0": payload_str}
    response = requests.post(API_URL, headers=headers, json=payload)
    response_json = response.json()
    content = response_json["out-0"]
    return content

def get_top_headlines(country, category, api_key, Subcategory):
    url = f'https://newsapi.org/v2/top-headlines'
    params = {
        'country': country,
        'category': category,
        'apiKey': api_key,
        "q": Subcategory
    }
    response = requests.get(url, params=params)
    if response.status_code == 200:
        return response.json()
    else:
        print(f"Error: {response.status_code}")
        return None

def lambda_handler(event, context):
    conn = mysql.connector.connect(**db_config)
    cursor = conn.cursor()
    logger.info("handling started.")
    current_time = datetime.now()

    # Log the current time
    logger.info(f"Current time: {current_time}")

    try:
        current_time = datetime.now()
        next_hour = current_time.replace(minute=0, second=0, microsecond=0) + timedelta(hours=1)
        query = f"""
        SELECT email
        FROM newsloop.user
        WHERE TIME(time) BETWEEN '{current_time.strftime('%H:%M:%S')}' AND '{next_hour.strftime('%H:%M:%S')}';
        """
        cursor.execute(query)
        logger.info("if any email.")
        result = cursor.fetchall()
        if result:
            for row in result:
                logger.info("in email list.")
                user_email = row[0]
                print(user_email)
                query2 = """SELECT c.short_name AS country_code, ud.interests_1, ud.interests_2
FROM user_data ud
JOIN countries c ON ud.interests_3 = c.country_name
WHERE ud.email = %s;"""
                conn2 = mysql.connector.connect(**db_config)
                cursor2 = conn2.cursor()
                cursor2.execute(query2, (user_email,))
                result2 = cursor2.fetchall()
                cursor2.close()
                conn2.close()

                new_list_for_gpt = []
                for row2 in result2:
                    sub_cat_identifier = row2[0]
                    category_name = row2[1].lower()
                    subcategory_name = row2[2].lower()

                    if category_name in ["politics", "sports", "business", "technology", "science", "health", "entertainment", "fashion"]:
                        result = get_top_headlines(sub_cat_identifier, category_name, news_api_key, subcategory_name)
                        if result:
                            articles = result.get('articles', [])
                            if articles:
                                for article in articles:
                                    title = str(article.get('title', 'N/A'))
                                    description = str(article.get('description', 'N/A'))
                                    single_news_str = title + description
                                    new_list_for_gpt.append(single_news_str)

                new_list_for_gpt_str = str(new_list_for_gpt).replace('[', '').replace(']', '')
                anchor_speech = speech_writer_function(new_list_for_gpt_str)
                logger.info("speech writer called.")
                anchor_speech = anchor_speech[:2499]

                # Extract the third sentence
                sentences = anchor_speech.split('.')
                if len(sentences) > 3:
                    third_sentence = sentences[2].strip()
                else:
                    third_sentence = "default_filename"

                # Format the filename
                current_time = datetime.now()
                formatted_date = current_time.strftime("%Y-%m-%d")
                audiofile_name = f"{third_sentence}_{formatted_date}.mp3"
                logger.info(audiofile_name)
                audiofile_path = os.path.join('/tmp', audiofile_name)

                # Assuming client and save are defined elsewhere
                audio = client.generate(
                    text=anchor_speech,
                    voice="Daniel",
                    model="eleven_multilingual_v2"
                )
                save(audio, audiofile_path)

                s3 = boto3.client('s3', aws_access_key_id=access_key, aws_secret_access_key=secret_key)
                folder_name = user_email + '/'
                file_path = audiofile_path

                logger.info("generating file name")
                logger.info(file_path)
                logger.info("file name")
                logger.info(audiofile_name)
                logger.info("actual file name")
                logger.info(audiofile_name)

                s3.upload_file(file_path, bucket_name, folder_name + audiofile_name)
    finally:
        cursor.close()
        conn.close()

    return {
        'statusCode': 200,
        'body': 'Process completed successfully'
    }