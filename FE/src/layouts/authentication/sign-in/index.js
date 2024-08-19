import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Link } from "react-router-dom";
import Switch from "@mui/material/Switch";
import SoftBox from "components/SoftBox";
import SoftTypography from "components/SoftTypography";
import { jwtDecode } from 'jwt-decode';
import SoftInput from "components/SoftInput";
import SoftButton from "components/SoftButton";
import CoverLayout from "layouts/authentication/components/CoverLayout";
import { GoogleOAuthProvider, GoogleLogin } from '@react-oauth/google';
import curved9 from "assets/images/curved-images/curved-6.jpg";

const logoUrl = "https://nipunakarunarathna.s3.us-west-1.amazonaws.com/Default_Create_a_sign_in_page_side_background_for_web_applicat_3.jpg";
const clientId = "801463696456-8i9f83b3ja6ic2n20gkd70ofoejckmvq.apps.googleusercontent.com";

function SignIn() {
  const [rememberMe, setRememberMe] = useState(true);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const navigate = useNavigate();

  const handleSetRememberMe = () => setRememberMe(!rememberMe);

  const handleSignIn = async (event) => {
    event.preventDefault();
    setErrorMessage(""); // Clear any previous error message
    try {
      const response = await fetch(`/authUser?userName=${email}&password=${password}`, {
        method: 'GET',
      });
  
      if (response.status === 500) {
        setErrorMessage("Incorrect password");
        return;
      }
  
      const contentType = response.headers.get("content-type");
      if (contentType && contentType.indexOf("application/json") !== -1) {
        const data = await response.json();
        if (data.hasUserData === "something") {
          localStorage.setItem("userName", data.name);
          localStorage.setItem("email", email);
          navigate("/dashboard", { state: { userData: data, userName: data.name, objectUrls: data.objectUrls, email: email } });
        } else if (data.hasUserData === "nothing") {
          localStorage.setItem("userName", data.name);
          localStorage.setItem("email", email);
          navigate("/profile", { state: { email: email, userName: data.name } });
        } else {
          alert("Authentication failed");
        }
      } else {
        const text = await response.text();
        console.error("Unexpected response format:", text);
        alert("Unexpected response format");
      }
    } catch (error) {
      console.error("Error during authentication", error);
    }
  };

  const handleGoogleSuccess = async (response) => {
    console.log("Google Login Response:", response);
  
    // Decode the JWT token to extract the email
    const decodedToken = jwtDecode(response.credential);
    const email = decodedToken.email;
  
    if (!email) {
      console.error("Email not found in Google response");
      setErrorMessage("Google Sign-In failed");
      return;
    }
  
    try {
      const apiResponse = await fetch(`/gauthUser?userName=${email}`, {
        method: 'GET',
      });
  
      const contentType = apiResponse.headers.get("content-type");
      if (contentType && contentType.indexOf("application/json") !== -1) {
        const data = await apiResponse.json();
        if (data.hasUserData === "something") {
          localStorage.setItem("userName", data.name);
          localStorage.setItem("email", email);
          navigate("/dashboard", { state: { userData: data, userName: data.name, objectUrls: data.objectUrls, email: email } });
        } else if (data.hasUserData === "nothing") {
          localStorage.setItem("userName", data.name);
          localStorage.setItem("email", email);
          navigate("/profile", { state: { email: email, userName: data.name } });
        } else {
          alert("Authentication failed");
        }
      } else {
        const text = await apiResponse.text();
        console.error("Unexpected response format:", text);
        alert("Unexpected response format");
      }
    } catch (error) {
      console.error("Error during Google authentication", error);
      setErrorMessage("Google Sign-In failed");
    }
  };

  const handleGoogleFailure = (error) => {
    console.error("Google Sign-In error", error);
    setErrorMessage("Google Sign-In failed");
  };

  return (
    <GoogleOAuthProvider clientId={clientId}>
      <CoverLayout
        title="Welcome back"
        description="Enter your email and password to sign in"
        image={logoUrl}
      >
        <SoftBox component="form" role="form" onSubmit={handleSignIn}>
          <SoftBox mb={2}>
            <SoftBox mb={1} ml={0.5}>
              <SoftTypography component="label" variant="caption" fontWeight="bold">
                Email
              </SoftTypography>
            </SoftBox>
            <SoftInput type="email" placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} />
          </SoftBox>
          <SoftBox mb={2}>
            <SoftBox mb={1} ml={0.5}>
              <SoftTypography component="label" variant="caption" fontWeight="bold">
                Password
              </SoftTypography>
            </SoftBox>
            <SoftInput type="password" placeholder="Password" value={password} onChange={(e) => setPassword(e.target.value)} />
          </SoftBox>
          {errorMessage && (
            <SoftBox mb={2}>
              <SoftTypography variant="caption" color="error">
                {errorMessage}
              </SoftTypography>
            </SoftBox>
          )}
          <SoftBox display="flex" alignItems="center">
            <Switch checked={rememberMe} onChange={handleSetRememberMe} />
            <SoftTypography
              variant="button"
              fontWeight="regular"
              onClick={handleSetRememberMe}
              sx={{ cursor: "pointer", userSelect: "none" }}
            >
              &nbsp;&nbsp;Remember me
            </SoftTypography>
          </SoftBox>
          <SoftBox mt={4} mb={1}>
            <SoftButton variant="gradient" color="info" fullWidth type="submit">
              sign in
            </SoftButton>
          </SoftBox>

          <SoftBox mt={3} textAlign="center">
            <SoftBox width="100%">
              <GoogleLogin
                onSuccess={handleGoogleSuccess}
                onFailure={handleGoogleFailure}
                cookiePolicy={'single_host_origin'}
                style={{ width: '100%' }}
              />
            </SoftBox>
          </SoftBox>
          <SoftBox mt={3} textAlign="center">
            <SoftTypography variant="button" color="text" fontWeight="regular">
              Don&apos;t have an account?{" "}
              <SoftTypography
                component={Link}
                to="/authentication/sign-up"
                variant="button"
                color="info"
                fontWeight="medium"
                textGradient
              >
                Sign up
              </SoftTypography>
            </SoftTypography>
          </SoftBox>
        
        </SoftBox>
      </CoverLayout>
    </GoogleOAuthProvider>
  );
}

export default SignIn;