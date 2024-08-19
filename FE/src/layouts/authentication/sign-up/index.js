import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { Card, Checkbox } from "@mui/material";
import SoftBox from "components/SoftBox";
import SoftTypography from "components/SoftTypography";
import SoftInput from "components/SoftInput";
import SoftButton from "components/SoftButton";
import BasicLayout from "layouts/authentication/components/BasicLayout";
import Separator from "layouts/authentication/components/Separator";
import curved6 from "assets/images/curved-images/curved14.jpg";
import { GoogleOAuthProvider, GoogleLogin } from "@react-oauth/google";
import { jwtDecode } from "jwt-decode"; // Corrected import

const clientId = "801463696456-8i9f83b3ja6ic2n20gkd70ofoejckmvq.apps.googleusercontent.com";

function SignUp() {
  const [agreement, setAgreement] = useState(true);
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [errorMessage, setErrorMessage] = useState(""); // State for error message
  const navigate = useNavigate();

  const handleSetAgreement = () => setAgreement(!agreement);

  const handleSignUp = async () => {
    try {
      const response = await fetch('/signUp', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: new URLSearchParams({
          userName: email,
          password: password,
          name: name,
        }),
      });

      if (response.status === 200) {
        const text = await response.text();
        const data = text ? JSON.parse(text) : {};
        localStorage.setItem("email", data.email);
        localStorage.setItem("userName", data.name);
        navigate("/profile");
      } else {
        console.error("Sign-up failed");
      }
    } catch (error) {
      console.error("Error during sign-up:", error);
    }
  };

  const handleGSignUp = async (response) => {
    const userObject = jwtDecode(response.credential); // Corrected usage
    try {
      const apiResponse = await fetch('/gsignUp', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: new URLSearchParams({
          userName: userObject.email,
          name: userObject.name,
        }),
      });

      if (apiResponse.status === 200) {
        const text = await apiResponse.text();
        const data = text ? JSON.parse(text) : null;
        if (data) {
          localStorage.setItem("email", data.email);
          localStorage.setItem("userName", data.name);
          navigate("/profile");
        } else {
          setErrorMessage("User already exists. Please sign in.");
        }
      } else {
        console.error("Google Sign-up failed");
      }
    } catch (error) {
      console.error("Error during Google sign-up:", error);
    }
  };

  return (
    <BasicLayout
      title="Welcome!"
      description="newsloop.ca"
      image={"https://nipunakarunarathna.s3.us-west-1.amazonaws.com/xxyz.jpg"}
    >
      <Card>
        <SoftBox p={3} mb={1} textAlign="center">
          <SoftTypography variant="h5" fontWeight="medium">
            Register with
          </SoftTypography>
        </SoftBox>
        <SoftBox mb={2} display="flex" justifyContent="center">
          <GoogleOAuthProvider clientId={clientId}>
            <GoogleLogin
              onSuccess={handleGSignUp}
              onError={() => {
                console.error("Google Sign-In failed");
              }}
            />
          </GoogleOAuthProvider>
        </SoftBox>
        <Separator />
        <SoftBox pt={2} pb={3} px={3}>
          <SoftBox component="form" role="form">
            <SoftBox mb={2}>
              <SoftInput placeholder="Name" value={name} onChange={(e) => setName(e.target.value)} />
            </SoftBox>
            <SoftBox mb={2}>
              <SoftInput type="email" placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} />
            </SoftBox>
            <SoftBox mb={2}>
              <SoftInput type="password" placeholder="Password" value={password} onChange={(e) => setPassword(e.target.value)} />
            </SoftBox>
            <SoftBox display="flex" alignItems="center">
              <Checkbox checked={agreement} onChange={handleSetAgreement} />
              <SoftTypography
                variant="button"
                fontWeight="regular"
                onClick={handleSetAgreement}
                sx={{ cursor: "pointer", userSelect: "none" }}
              >
                &nbsp;&nbsp;I agree the&nbsp;
              </SoftTypography>
              <SoftTypography
                component="a"
                href="#"
                variant="button"
                fontWeight="bold"
                textGradient
              >
                Terms and Conditions
              </SoftTypography>
            </SoftBox>
            {errorMessage && (
              <SoftBox mt={2} mb={1}>
                <SoftTypography variant="body2" color="error">
                  {errorMessage}
                </SoftTypography>
              </SoftBox>
            )}
            <SoftBox mt={4} mb={1}>
              <SoftButton variant="gradient" color="dark" fullWidth onClick={handleSignUp}>
                sign up
              </SoftButton>
            </SoftBox>
            <SoftBox mt={3} textAlign="center">
              <SoftTypography variant="button" color="text" fontWeight="regular">
                Already have an account?&nbsp;
                <SoftTypography
                  component={Link}
                  to="/"
                  variant="button"
                  color="dark"
                  fontWeight="bold"
                  textGradient
                >
                  Sign in
                </SoftTypography>
              </SoftTypography>
            </SoftBox>
          </SoftBox>
        </SoftBox>
      </Card>
    </BasicLayout>
  );
}

export default SignUp;