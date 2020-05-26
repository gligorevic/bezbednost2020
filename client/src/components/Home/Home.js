import React from "react";
import MainNavbar from "../layouts/Navbar/MainNavbar";
import InfoDialog from "../Dialogs/InfoDialog";
import ficaImage from "../../images/fica.jpg";

const Home = ({}) => {
  return (
    <>
      <MainNavbar />
      <img
        src={ficaImage}
        style={{
          width: "100%",
          height: "100%",
          position: "absolute",
          top: 0,
          left: 0,
          zIndex: -1,
          opacity: 0.6,
        }}
      />
      <p>Stranica sa oglasima</p>
    </>
  );
};

export default Home;
