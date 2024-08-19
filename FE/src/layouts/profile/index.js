import React, { useState, useEffect } from "react";
import { useLocation } from "react-router-dom";
import Grid from "@mui/material/Grid";
import Card from "@mui/material/Card";
import Autocomplete from "@mui/material/Autocomplete";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import IconButton from "@mui/material/IconButton";
import DeleteIcon from "@mui/icons-material/Delete";
import Box from "@mui/material/Box";
import { styled } from "@mui/material/styles";
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { TimePicker } from '@mui/x-date-pickers/TimePicker';
import dayjs from 'dayjs';
import SoftBox from "components/SoftBox";
import SoftTypography from "components/SoftTypography";
import DashboardLayout from "examples/LayoutContainers/DashboardLayout";
import Footer from "examples/Footer";
import ProfilesList from "examples/Lists/ProfilesList";
import Header from "layouts/profile/components/Header";
import PlatformSettings from "layouts/profile/components/PlatformSettings";
import profilesListData from "layouts/profile/data/profilesListData";

const StyledCard = styled(Card)(({ theme }) => ({
  borderRadius: theme.shape.borderRadius,
  boxShadow: theme.shadows[3],
  padding: theme.spacing(3),
}));

const StyledButton = styled(Button)(({ theme }) => ({
  marginTop: theme.spacing(2),
  borderRadius: theme.shape.borderRadius,
}));

const SelectionBox = styled(SoftBox)(({ theme }) => ({
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'space-between',
  padding: theme.spacing(1),
  border: `1px solid ${theme.palette.divider}`,
  borderRadius: theme.shape.borderRadius,
  marginBottom: theme.spacing(1),
  fontSize: '0.875rem', // Adjust font size here
}));

function Overview() {
  const location = useLocation();
  const email = localStorage.getItem("email");
  const userName = localStorage.getItem("userName");
  const [categories, setCategories] = useState([]);
  const [subcategories, setSubcategories] = useState([]);
  const [countries, setCountries] = useState([]); // New state for countries
  const [selectedOptions1, setSelectedOptions1] = useState(null);
  const [selectedOptions2, setSelectedOptions2] = useState([]);
  const [selectedOptions3, setSelectedOptions3] = useState(null); // Single selection for country
  const [addedSelections, setAddedSelections] = useState([]);
  const [submitButtonText, setSubmitButtonText] = useState("Submit All");
  const [submitButtonColor, setSubmitButtonColor] = useState("secondary");
  const [selectedTime, setSelectedTime] = useState(dayjs('2022-04-17T15:30')); // State for time picker

  useEffect(() => {
    fetch('/getcategoris')
      .then(response => response.json())
      .then(data => {
        const categoryOptions = data.map(category => ({ label: category }));
        setCategories(categoryOptions);
      })
      .catch(error => console.error("Error fetching categories:", error));
  }, []);

  useEffect(() => {
    if (email) {
      fetch(`/getInterests?email=${encodeURIComponent(email)}`)
        .then(response => response.json())
        .then(data => {
          const initialSelections = Object.keys(data.interests).map(category => ({
            options1: { label: category },
            options3: data.interests[category].countries.map(country => ({ label: country }))[0], // Single country
            options2: data.interests[category].subcategories.map(subcategory => ({ label: subcategory })),
          }));
          setAddedSelections(initialSelections);
        })
        .catch(error => console.error("Error fetching interests:", error));
    }
  }, [email]);

  useEffect(() => {
    if (selectedOptions1) {
      fetch(`/getsubcategories?category=${encodeURIComponent(selectedOptions1.label)}`)
        .then(response => response.json())
        .then(data => {
          const subcategoryOptions = data.map(subcategory => ({ label: subcategory }));
          setSubcategories(subcategoryOptions);
        })
        .catch(error => console.error("Error fetching subcategories:", error));
    } else {
      setSubcategories([]);
    }
  }, [selectedOptions1]);

  useEffect(() => {
    fetch('/countries')
      .then(response => response.json())
      .then(data => {
        const countryOptions = data.map(country => ({ label: country }));
        setCountries(countryOptions);
      })
      .catch(error => console.error("Error fetching countries:", error));
  }, []);

  const handleAddSelection = () => {
    const newSelection = {
      options1: selectedOptions1,
      options2: selectedOptions2,
      options3: selectedOptions3,
    };
    setAddedSelections([...addedSelections, newSelection]);
    setSelectedOptions1(null);
    setSelectedOptions2([]);
    setSelectedOptions3(null);
  };

  const handleRemoveSelection = (index) => {
    const newSelections = addedSelections.filter((_, i) => i !== index);
    setAddedSelections(newSelections);
  };

  const handleSubmitAll = () => {
    const payload = {
      email,
      name: userName,
      categories: addedSelections.reduce((acc, selection) => {
        const category = selection.options1.label;
        acc[category] = {
          country: selection.options3 ? selection.options3.label : '',
          subcategories: selection.options2.map(opt => opt.label),
        };
        return acc;
      }, {}),
    };
  
    fetch('/postInterests', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(payload),
    })
      .then(response => {
        if (response.status === 200) {
          setSubmitButtonText("Successfully Submitted!");
          setSubmitButtonColor("success");
  
          // Call the additional API after successful submission
          const signUpPayload = new URLSearchParams({
            email: email,
            time: selectedTime.format('HH:mm'), // Format the time as HH:mm
          });
  
          return fetch('/timeUp', {
            method: 'POST',
            headers: {
              'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: signUpPayload.toString(),
          });
        } else {
          throw new Error("Failed to submit.");
        }
      })
      .then(signUpResponse => {
        if (signUpResponse.status !== 200) {
          throw new Error("Failed to sign up.");
        }
      })
      .catch(error => console.error("Error:", error))
      .finally(() => {
        setTimeout(() => {
          setSubmitButtonText("Submit All");
          setSubmitButtonColor("secondary");
        }, 3000);
      });
  };

  return (
    <DashboardLayout>
      <Header />
      <SoftBox mt={5} mb={3}>
        <Grid container spacing={3}>
          <Grid item xs={12} md={6} xl={4}>
            <PlatformSettings />
          </Grid>
          <Grid item xs={12} md={6} xl={8}>
            <StyledCard>
              <SoftTypography variant="h6" gutterBottom>
                My Preferences
              </SoftTypography>
              <form>
                <Grid container spacing={2}>
                  <Grid item xs={12} md={6}>
                    <Autocomplete
                      options={categories}
                      getOptionLabel={(option) => option.label}
                      value={selectedOptions1}
                      onChange={(event, newValue) => setSelectedOptions1(newValue)}
                      renderInput={(params) => (
                        <TextField 
                          {...params} 
                          label="Category" 
                          fullWidth 
                          margin="normal" 
                          InputLabelProps={{ shrink: true, style: { marginTop: '-13px' } }} 
                        />
                      )}
                    />
                  </Grid>
                  <Grid item xs={12} md={6}>
                    <Autocomplete
                      options={countries} // Use fetched countries
                      getOptionLabel={(option) => option.label}
                      value={selectedOptions3}
                      onChange={(event, newValue) => setSelectedOptions3(newValue)}
                      renderInput={(params) => (
                        <TextField 
                          {...params} 
                          label="Country" 
                          fullWidth 
                          margin="normal" 
                          InputLabelProps={{ shrink: true, style: { marginTop: '-13px' } }} 
                        />
                      )}
                    />
                  </Grid>
                  <Grid item xs={12}>
                    <Autocomplete
                      multiple
                      options={subcategories}
                      getOptionLabel={(option) => option.label}
                      value={selectedOptions2}
                      onChange={(event, newValue) => setSelectedOptions2(newValue)}
                      renderInput={(params) => (
                        <TextField 
                          {...params} 
                          label="Subcategory" 
                          fullWidth 
                          margin="normal" 
                          InputLabelProps={{ shrink: true, style: { marginTop: '-13px' } }} 
                        />
                      )}
                    />
                  </Grid>
                  <Grid item xs={12}>
                    <StyledButton variant="contained" color="primary" fullWidth onClick={handleAddSelection}>
                      Add Selection
                    </StyledButton>
                  </Grid>
                  <Grid item xs={12}>
                    <SoftBox mt={2}>
                      {addedSelections.map((selection, index) => (
                        <SelectionBox key={index}>
                          <div>
                            <SoftTypography variant="body2">
                              Selection {index + 1}:
                            </SoftTypography>
                            <div>
                              Category: {selection.options1 ? selection.options1.label : "None"}
                            </div>
                            <div>
                              Subcategory: {selection.options2.length > 0 ? selection.options2.map(opt => opt.label).join(', ') : "None"}
                            </div>
                            <div>
                              Country: {selection.options3 ? selection.options3.label : "None"}
                            </div>
                          </div>
                          <IconButton onClick={() => handleRemoveSelection(index)}>
                            <DeleteIcon />
                          </IconButton>
                        </SelectionBox>
                      ))}
                    </SoftBox>
                  </Grid>
                  <Grid item xs={12}>
                    <Box mt={2} mb={2}>
                      <LocalizationProvider dateAdapter={AdapterDayjs}>
                        <Box mt={2} mb={2}> {/* Add margin here */}
                          <TimePicker
                            value={selectedTime}
                            onChange={(newValue) => setSelectedTime(newValue)}
                            disableOpenPicker // Disable the clock icon
                            renderInput={(params) => (
                              <TextField 
                                {...params} 
                                label="Time" 
                                fullWidth 
                                margin="normal" 
                                InputLabelProps={{ shrink: true, style: { marginTop: '-13px' } }} 
                                sx={{
                                  '& .MuiInputBase-root': {
                                    color: 'black', // Text color
                                    backgroundColor: 'lightgray', // Background color
                                  },
                                  '& .MuiOutlinedInput-notchedOutline': {
                                    borderColor: 'black', // Border color
                                  },
                                  '& .MuiInputLabel-root': {
                                    color: 'black', // Label color
                                  },
                                  '& .MuiInputLabel-root.Mui-focused': {
                                    color: 'black', // Label color when focused
                                  },
                                  '& .MuiOutlinedInput-root.Mui-focused .MuiOutlinedInput-notchedOutline': {
                                    borderColor: 'black', // Border color when focused
                                  },
                                  '& .MuiSvgIcon-root': {
                                    color: 'black', // Icon color
                                  },
                                  '& .MuiPaper-root': {
                                    backgroundColor: 'lightgray', // Dropdown background color
                                  },
                                }}
                              />
                            )}
                          />
                        </Box>
                      </LocalizationProvider>
                    </Box>
                  </Grid>
                  <Grid item xs={12}>
                    <StyledButton variant="contained" color={submitButtonColor} fullWidth onClick={handleSubmitAll}>
                      {submitButtonText}
                    </StyledButton>
                  </Grid>
                </Grid>
              </form>
            </StyledCard>
          </Grid>
        </Grid>
      </SoftBox>
      <Footer />
    </DashboardLayout>
  );
}

export default Overview;