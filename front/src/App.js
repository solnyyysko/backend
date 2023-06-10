import './App.css'
import React from "react"
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom"
import {connect} from "react-redux"
import Utils from "./utils/Utils"
import SideBar from "./components/SideBar";
import {useState} from "react";

import NavigationBar from "./components/NavigationBar"
import Home from "./components/Home"
import Login from "./components/Login"
import CountryListComponent from "./components/CountryListComponent";
import ArtistListComponent from "./components/ArtistListComponent";
import CountryComponent from "./components/CountryComponent";
import ArtistComponent from "./components/ArtistComponent";
import MyAccountComponent from "./components/MyAccountComponent";

const ProtectedRoute = ({children}) => {
    let user = Utils.getUser();
    return user ? children : <Navigate to={'/login'} />
};

const App = props =>  {
    const [exp,setExpanded] = useState(true);
    return (
        <div className="App">
            <BrowserRouter>
                <NavigationBar toggleSideBar={() =>
                    setExpanded(!exp)}/>
                <div className="wrapper">
                    <SideBar expanded={exp} />
                    <div className="container-fluid">
                        {props.error_message &&
                            <div className="alert alert-danger m-1">{props.error_message}</div>}
                        <Routes>
                            <Route path="login" element={<Login />}/>
                            <Route path="home" element={<ProtectedRoute><Home/></ProtectedRoute>}/>
                            <Route path="countries" element={<ProtectedRoute><CountryListComponent/></ProtectedRoute>}/>
                            <Route path="countries/:id" element={<ProtectedRoute><CountryComponent /></ProtectedRoute>}/>
                            <Route path="artists" element={<ProtectedRoute><ArtistListComponent/></ProtectedRoute>}/>
                            <Route path="artists/:id" element={<ProtectedRoute><ArtistComponent /></ProtectedRoute>}/>
                            <Route path="my" element={<ProtectedRoute><MyAccountComponent/></ProtectedRoute>}/>
                        </Routes>
                    </div>
                </div>
            </BrowserRouter>
        </div>
    );
}

function mapStateToProps(state) {
    const { msg } = state.alert;
    return { error_message: msg };
}

export default connect(mapStateToProps)(App);