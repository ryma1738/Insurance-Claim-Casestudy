import React, { useState, useEffect, } from 'react';
import { Col, Container, Row} from "react-bootstrap";
import { signup } from '../util/api';
import { createPhoneNumber } from '../util/helpers';


function Signup(props) {

    const [email, setEmail] = useState("");
    const [phoneNumber, setPhoneNumber] = useState("");
    const [dob, setDob] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [errorMessage, setErrorMessage] = useState("");

    async function attemptSignup(e) {
        setErrorMessage("");
        e.preventDefault();
        if (password !== confirmPassword) {
            return setErrorMessage("Passwords do not match");
        }
        const response = await signup(email, password, phoneNumber, new Date(dob));
        if (response.ok) {
            const data = await response.json();
            localStorage.setItem('jwtInsuranceCase', data.jwtToken);
            window.location = "/";
        } else if (response.status === 400) {
            const err = await response.json();
            setErrorMessage(err.message);
        } else if (response.status === 500) {
            alert("An Unknown Error has occurred, please try again.")
        }
    }

    useEffect(() => {
        if (props.loggedIn) {
            window.location = "/"
        }
    });
    return (
        <Container fluid="xxl">
            <Row className="d-flex justify-content-center align-items-center mt-5">
                <Col xl={4} md={6} sm={10} xs={11} className="loginDiv">
                    <h2 className="text-center mt-2 lobster fs-1">Sign Up</h2>
                    <form onSubmit={(e) => attemptSignup(e)}>
                        <div className="my-3">
                            <label htmlFor="phoneNumber" >Phone Number:</label>
                            <input type="tel" id="phoneNumber" className="text-center" value={phoneNumber} placeholder="888-888-8888"
                                minLength={12} maxLength={12} onChange={(e) => setPhoneNumber(createPhoneNumber(e.target.value, phoneNumber))}></input>
                        </div>
                        <div className="my-3">
                            <label htmlFor="email" >Email:</label>
                            <input type="email" id="email" className="text-center" minLength={10} maxLength={40}
                                required placeholder="yourEmail@email.com" onChange={(e) => setEmail(e.target.value)}></input>
                        </div>
                        <div className="my-3">
                            <label htmlFor="dateOfBirth" >Date of Birth:</label>
                            <input type="date" id="dateOfBirth" className="text-center" 
                                required onChange={(e) => setDob(e.target.value)}></input>
                        </div>
                        <div className="my-3">
                            <label htmlFor="password" >Password:</label>
                            <input type="password" id="password" className="text-center" minLength={6} maxLength={25}
                                required placeholder="Password" onChange={(e) => setPassword(e.target.value)}></input>
                        </div>
                        <div className="my-3">
                            <label htmlFor="passwordConfirm" >Confirm Password:</label>
                            <input type="password" id="passwordConfirm" className="text-center" minLength={6} maxLength={25}
                                required placeholder="Confirm Password" onChange={(e) => setConfirmPassword(e.target.value)}></input>
                        </div>
                        <p className="text-center text-danger">{errorMessage}</p>
                        <div className="d-flex justify-content-center align-items-center my-3">
                            <button type="submit" className="button">Sign Up</button>
                        </div>
                    </form>
                </Col>
            </Row>
            <Row className="mt-5 d-flex justify-content-center align-items-center" style={{ borderBottom: "5px solid var(--cyan)" }}>
                <p className="text-center">Have an account? <a href="/login" className="text-link">Login</a></p>
            </Row>
        </Container>
    );
}

export default Signup;