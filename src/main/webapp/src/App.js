import Box from '@mui/material/Box';
import CssBaseline from '@mui/material/CssBaseline';
import React from 'react';
import { BrowserRouter, Routes, Route } from "react-router-dom";
import './App.css';
import FooterMenu from './components/FooterMenu';
import Home from './components/Home';
import WishList from './components/WishList';
import Reserve from "./components/Reserve";
import Search from "./components/Search";
import Login_main from './components/login/Login_main';
import Login_demo from './components/login/Login_demo';
import GoogleLogin from './components/login/GoogleLogin';

function App() {
  return (
    <React.Fragment>
      <BrowserRouter>
        <CssBaseline />
        <Box
          sx={{
            display: "flex",
            flexDirection: "column",
            height: "100vh",
          }}
        >
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/wishList" element={<WishList />} />
            <Route path="/login"  >
              <Route index element={<Login_demo />}/>
              <Route path="/login/Google" element={<GoogleLogin/>}/>
            </Route>

            <Route path="/reserve" element={<Reserve />} />
            <Route path="/search" element={<Search />} />

          </Routes>
          <Box sx={{ display: { xs: "flex", md: "none" }, marginTop: "auto" }}>
            <FooterMenu />
          </Box>
          {/* <Box sx={displayOnDesktop}>
            <Footer />
          </Box> */}
        </Box>
      </BrowserRouter>
    </React.Fragment>
  );
}

export default App;