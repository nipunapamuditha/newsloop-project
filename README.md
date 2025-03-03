# Newsloop - Tailored Audio News Briefings

![Newsloop](https://your-image-url.com)  

## Overview
Newsloop is a personalized news briefing application that delivers audio news updates tailored to users' interests. By leveraging multiple APIs, AI-driven narration, and cloud-based automation, Newsloop provides an effortless way to stay informed without spending hours consuming news content.

## Features
- **Personalized News Briefings**: Users select their preferred categories, subcategories, and countries.
- **Aggregated News Sources**: Fetches news from NewsAPI, Twitter API, and other sources.
- **AI-Powered Narration**: Uses OpenAI API to generate news summaries.
- **Realistic Text-to-Speech (TTS)**: Converts summaries into natural-sounding audio via ElevenLabs API.
- **Cloud-Based Scheduling & Instant Playback**: 
  - AWS Lambda (Python) for instant and scheduled news generation.
  - CloudWatch Events for automated scheduling.
  - AWS SDK for Java to trigger instant news generation.

## Tech Stack
- **Frontend**: React.js (Future Implementation)
- **Backend**: Python (FastAPI)
- **Cloud & Hosting**: AWS Lambda, CloudWatch
- **APIs & Services**: 
  - [NewsAPI](https://newsapi.org/) - Fetch news articles
  - [Twitter API](https://developer.twitter.com/) - Get real-time updates
  - [OpenAI API](https://openai.com/) - Summarization & text generation
  - [ElevenLabs API](https://elevenlabs.io/) - Text-to-Speech conversion

## Getting Started
### Prerequisites
- Python 3.x
- AWS CLI & SDK for Java (for triggering Lambda functions)
- API keys for NewsAPI, Twitter, OpenAI, and ElevenLabs

### Installation
1. Clone the repository:
   ```sh
   git clone https://github.com/yourusername/newsloop.git
   cd newsloop
   ```
2. Install dependencies:
   ```sh
   pip install -r requirements.txt
   ```
3. Set up environment variables:
   ```sh
   cp .env.example .env
   ```
   - Add your API keys and configuration in the `.env` file.

4. Run the API server:
   ```sh
   uvicorn main:app --reload
   ```

## Usage
- **User Setup**: Fill out the form with your news preferences.
- **News Generation**: 
  - Scheduled via AWS CloudWatch.
  - Instant generation via API call.
- **Audio Playback**: Generated audio files can be played through the web interface (WIP).

## Future Improvements
- Implement a React-based frontend for better UX.
- Expand to more news sources.
- Improve AI summarization for more natural storytelling.
- Optimize cloud cost by caching frequently requested summaries.

## Contributing
Feel free to submit issues, feature requests, or pull requests to help improve Newsloop.

## License
This project is licensed under the MIT License.

## Contact
**Nipuna Karunarathna**  
Operations Engineer @ Mad Mobile LK  
[LinkedIn](https://www.linkedin.com/in/nipuna-karunarathna) | [Website](https://yourwebsite.com)

---
_Project started: August 19, 2024_
