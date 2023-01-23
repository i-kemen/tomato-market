import * as React from "react";
import { useState, useEffect } from "react";
import { Link as RouterLink, useNavigate } from "react-router-dom";
import Button from "@mui/material/Button";
import CssBaseline from "@mui/material/CssBaseline";
import TextField from "@mui/material/TextField";
import Grid from "@mui/material/Grid";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import Container from "@mui/material/Container";
import { createTheme, ThemeProvider } from "@mui/material/styles";

import { api } from "../utils/apis";

const theme = createTheme();

const AUTH = {
  CUSTOMER: 0,
  SELLER: 1,
  ADMIN: 2,
};

export default function Profile() {
  const navigate = useNavigate();
  const [auth, setAuth] = useState(AUTH.CUSTOMER);
  const [isRenderProfile, setIsRenderProfile] = useState(false);
  const [userId, setUserId] = useState(0);
  const [username, setUsername] = useState("");
  const [nickname, setNickname] = useState("");
  const [introduce, setIntroduce] = useState("");

  useEffect(() => {
    getJWTToken();
    getUserProfile();
  }, []);

  const getJWTToken = () => {
    const jwtToken = localStorage.getItem("access_token");

    if (!jwtToken) {
      navigate("/login");
      return;
    }
    api.default.setHeadersAuthorization(jwtToken);
  };

  const getUserProfile = async () => {
    const { data } = await api.getProfile();
    setUserId(data.id);
    setUsername(data.username);
    setNickname(data.nickname);
    setAuth(AUTH[data.role]);

    if (AUTH[data.role] === AUTH.SELLER) {
      getSellerProfile(data.id);
    }

    setIsRenderProfile(true);
  };

  const getSellerProfile = async (userId) => {
    try {
      const { data } = await api.getMySellerProfile(userId);
      setIntroduce(data.introduce);
    } catch (e) {
      throw new Error(e);
    }
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    const data = new FormData(event.currentTarget);
    submitUserProfile(data.get("nickname"));

    if (auth === AUTH.SELLER) {
      submitSellerProfile(data.get("introduce"));
    }

    alert("프로필을 업데이트 했습니다.");
  };

  const submitUserProfile = async (nickname) => {
    const payload = {
      nickname: nickname,
    };

    try {
      await api.patchProfile(userId, payload);
      await getUserProfile();
    } catch (e) {
      throw new Error(e);
    }
  };

  const submitSellerProfile = async (introduce) => {
    const payload = {
      introduce: introduce,
    };

    try {
      await api.patchSellerProfile(userId, payload);
      await getSellerProfile(userId);
    } catch (e) {
      throw new Error(e);
    }
  };

  return (
    <ThemeProvider theme={theme}>
      {isRenderProfile ? (
        <Container component="main" maxWidth="xs">
          <CssBaseline />
          <Box
            sx={{
              marginTop: 8,
              display: "flex",
              flexDirection: "column",
              alignItems: "center",
            }}
          >
            <Typography component="h1" variant="h5">
              [
              {auth === AUTH.CUSTOMER
                ? "고객"
                : auth === AUTH.SELLER
                ? "판매자"
                : "관리자"}
              ]{nickname}님의 Profile
            </Typography>
            <Box
              component="form"
              onSubmit={handleSubmit}
              noValidate
              sx={{ mt: 1 }}
            >
              <TextField
                disabled
                margin="normal"
                required
                fullWidth
                id="username"
                label="아이디"
                name="username"
                InputProps={{
                  readOnly: true,
                }}
                defaultValue={username}
                autoFocus
              />
              <TextField
                margin="normal"
                required
                fullWidth
                name="nickname"
                label="닉네임"
                id="nickname"
                defaultValue={nickname}
              />
              {auth === AUTH.SELLER ? (
                <TextField
                  margin="normal"
                  required
                  fullWidth
                  name="introduce"
                  label="자기소개"
                  id="introduce"
                  value={introduce}
                  onChange={(e) => {
                    setIntroduce(e.target.value);
                  }}
                />
              ) : (
                <></>
              )}
              <Button
                type="submit"
                fullWidth
                color="success"
                variant="contained"
                sx={{ mt: 2, mb: 2 }}
              >
                저장
              </Button>
            </Box>
            <Grid container>
              <Grid item>
                <RouterLink
                  to={"/"}
                  style={{
                    fontSize: "0.875rem",
                    textDecoration: "underline",
                    color: "#1976d2",
                  }}
                >
                  {"홈으로 이동"}
                </RouterLink>
              </Grid>
            </Grid>
          </Box>
        </Container>
      ) : (
        <></>
      )}
    </ThemeProvider>
  );
}
