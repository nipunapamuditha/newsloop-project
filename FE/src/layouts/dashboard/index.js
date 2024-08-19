import React, { useState, useRef, useEffect } from "react";
import { useLocation } from "react-router-dom";
import Grid from "@mui/material/Grid";
import { makeStyles } from "@mui/styles";
import SoftBox from "components/SoftBox";
import SoftTypography from "components/SoftTypography";
import DashboardLayout from "examples/LayoutContainers/DashboardLayout";
import DashboardNavbar from "examples/Navbars/DashboardNavbar";
import typography from "assets/theme/base/typography";
import AudioPlayer from 'react-h5-audio-player';
import 'react-h5-audio-player/lib/styles.css';

const useStyles = makeStyles((theme) => ({
  audioFile: {
    cursor: "pointer",
    backgroundColor: "#f0f0f0",
    borderRadius: "5px",
    boxShadow: "0 2px 4px rgba(0, 0, 0, 0.1)", // Added subtle box shadow
    "&:hover": {
      backgroundColor: "#e0e0e0",
      boxShadow: "0 4px 8px rgba(0, 0, 0, 0.2)", // Slightly stronger shadow on hover
    },
  },
  activeAudioFile: {
    backgroundColor: "#d0d0d0",
  },
  playingAudioFile: {
    backgroundColor: "#c0c0c0",
    boxShadow: "0 4px 8px rgba(0, 0, 0, 0.3)", // Stronger shadow for playing state
  },
  popOutBlock: {
    position: "fixed",
    bottom: "20px",
    right: "20px",
    width: "300px",
    backgroundColor: "white",
    borderRadius: "10px",
    boxShadow: "0 4px 8px rgba(0, 0, 0, 0.1)",
    zIndex: 1000,
    padding: "20px",
    textAlign: "center",
  },
  albumArt: {
    width: '100%',
    borderRadius: '5px',
    marginBottom: '20px',
  }
}));

const imageUrls = [
  "https://nipunakarunarathna.s3.us-west-1.amazonaws.com/newsart/Default_generate_art_which_is_suitable_to_audio_news_briefing_3.jpg",
  "https://nipunakarunarathna.s3.us-west-1.amazonaws.com/newsart/Default_generate_art_which_is_suitable_to_audio_news_briefing_2.jpg",
  "https://nipunakarunarathna.s3.us-west-1.amazonaws.com/newsart/Default_generate_art_which_is_suitable_to_audio_news_briefing_1.jpg",
  "https://nipunakarunarathna.s3.us-west-1.amazonaws.com/newsart/Default_generate_art_which_is_suitable_to_audio_news_briefing_0.jpg",
  "https://nipunakarunarathna.s3.us-west-1.amazonaws.com/newsart/Default_breaking_news_daily_briefing_3.jpg",
  "https://nipunakarunarathna.s3.us-west-1.amazonaws.com/newsart/Default_breaking_news_daily_briefing_0.jpg"
];

function getRandomImageUrl() {
  const randomIndex = Math.floor(Math.random() * imageUrls.length);
  return imageUrls[randomIndex];
}

function extractAudioDetails(url) {
  const regex = /([^/]+)_(\d{4}-\d{2}-\d{2})\.mp3$/;
  const match = url.match(regex);
  if (match) {
    return {
      title: match[1].replace(/-/g, ' '),
      createdDate: match[2],
    };
  }
  return {
    title: "Unknown",
    createdDate: "Unknown",
  };
}

function Dashboard() {
  const { size } = typography;
  const classes = useStyles();
  const location = useLocation();

  const [userData, setUserData] = useState(location.state?.userData || JSON.parse(localStorage.getItem("userData")) || {});
  const [objectUrls, setObjectUrls] = useState(location.state?.objectUrls || JSON.parse(localStorage.getItem("objectUrls")) || []);
  const [currentAudio, setCurrentAudio] = useState(null);
  const [activeIndex, setActiveIndex] = useState(null);
  const [userName, setUserName] = useState(location.state?.userName || sessionStorage.getItem("userName") || "Sign in");
  const [currentImageUrl, setCurrentImageUrl] = useState("");
  const audioRef = useRef(null);

  useEffect(() => {
    if (location.state?.userData) {
      localStorage.setItem("userData", JSON.stringify(location.state.userData));
    }
    if (location.state?.objectUrls) {
      localStorage.setItem("objectUrls", JSON.stringify(location.state.objectUrls));
    }
    if (location.state?.userName) {
      sessionStorage.setItem("userName", location.state.userName);
      setUserName(location.state.userName);
    }
    if (objectUrls.length > 0) {
      setCurrentAudio(objectUrls[0]);
      setActiveIndex(0);
      setCurrentImageUrl(getRandomImageUrl());
    }
  }, [location.state, objectUrls]);

  const audioFiles = objectUrls
    .filter(url => url.endsWith('.mp3'))
    .map((url, index) => {
      const { title, createdDate } = extractAudioDetails(url);
      return {
        name: title,
        url,
        createdDate,
      };
    });

  const handleAudioSelect = (audio, index) => {
    setCurrentAudio(audio.url);
    setActiveIndex(index);
    setCurrentImageUrl(getRandomImageUrl());
  };

  return (
    <DashboardLayout>
      <DashboardNavbar userName={userName} />
      <SoftBox mt={3} px={3}>
        <Grid container spacing={3} justifyContent="center">
          <Grid item xs={12}>
            <SoftBox p={3} borderRadius="lg" boxShadow="lg" bgcolor="white">
              <SoftTypography variant="h5" fontWeight="bold" gutterBottom>
                Audio Files
              </SoftTypography>
              {audioFiles.map((audio, index) => (
                <SoftBox
                  key={index}
                  onClick={() => handleAudioSelect(audio, index)}
                  p={2}
                  mt={2}
                  className={`${classes.audioFile} ${activeIndex === index ? classes.activeAudioFile : ""} ${currentAudio === audio.url ? classes.playingAudioFile : ""}`}
                >
                  <SoftTypography variant="body1">{audio.name}</SoftTypography>
                  <SoftTypography variant="body2">
                    Created: {audio.createdDate}
                  </SoftTypography>
                </SoftBox>
              ))}
            </SoftBox>
          </Grid>
        </Grid>
      </SoftBox>
      {currentAudio && (
        <div className={classes.popOutBlock}>
          <img src={currentImageUrl} alt="Album Art" className={classes.albumArt} />
          <AudioPlayer 
            autoPlay
            src={currentAudio}
            onPlay={e => console.log("onPlay")}
            ref={audioRef}
            style={{ width: '100%', height: '80px' }} // Adjusted height
          />
        </div>
      )}
    </DashboardLayout>
  );
}

export default Dashboard;